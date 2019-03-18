/*
 * Copyright (C) 2019 Jorge Matricali <jorgematricali@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ar.net.argentum.servidor.protocolo;

import ar.net.argentum.servidor.Baldosa;
import ar.net.argentum.servidor.InventarioSlot;
import ar.net.argentum.servidor.Mapa;
import ar.net.argentum.servidor.ObjetoMetadata;
import ar.net.argentum.servidor.Servidor;
import ar.net.argentum.servidor.Usuario;
import ar.net.argentum.servidor.mundo.Personaje;
import ar.net.argentum.servidor.mundo.PersonajeImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ConexionConCliente extends Thread {

    private static final byte PQT_DESCONECTAR = 0x1;
    private static final byte PQT_INICIAR_SESION = 0x2;
    private static final byte PQT_CHAT = 0x3;
    private static final byte PQT_ACTUALIZAR_INVENTARIO = 0x4;
    private static final byte PQT_CAMBIA_MUNDO = 0x5;
    private static final byte PQT_USUARIO_NOMBRE = 0x6;
    private static final byte PQT_USUARIO_POSICION = 0x7;
    private static final byte PQT_USUARIO_STATS = 0x8;
    private static final byte PQT_MUNDO_REPRODUCIR_ANIMACION = 0x9;

    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Socket socket;
    private boolean conectado = false;
    private final byte versionProtocolo = 0x1;
    private String username = "";
    private final ArrayList<ConexionConCliente> conexiones;
    protected Usuario usuario;

    // Constructor 
    public ConexionConCliente(Socket s, ArrayList<ConexionConCliente> conexiones) throws IOException {
        this.socket = s;
        this.conexiones = conexiones;

        // Obtener Streams de entrada y salida
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {
        System.out.println("Nuevo thread iniciado.");

        this.conectado = true;

        while (conectado) {
            try {
                // Obtenemos el tipo de paquete recibido
                byte tipoPaquete;

                try {
                    tipoPaquete = dis.readByte();
                } catch (EOFException ex) {
                    Servidor.getLogger().log(Level.SEVERE, null, ex);
                    this.conectado = false;
                    break;
                }

                // Manejamos el paquete recibido
                switch (tipoPaquete) {

                    case PQT_DESCONECTAR:
                        System.out.println("Recibimos un cierre de conexion.");
                        // Terminamos
                        this.conectado = false;
                        break;

                    case PQT_INICIAR_SESION:
                        manejarInicioDeSesion();
                        break;

                    case PQT_CHAT:
                        String mensaje = dis.readUTF();

                        if (mensaje.startsWith("/")) {
                            // El usuario ha ingresado un comando
                            // Le quitamos la "/" inicial
                            manejarComando(mensaje.substring(1));
                            break;
                        }

                        Servidor.getServidor().enviarMensajeDeDifusion("\u00a79" + username + "\u00a77: " + mensaje);
                        break;

                    default:
                        System.out.println("Recibimos un paquete que no supimos manejar!");
                        break;
                }
            } catch (IOException e) {
                Servidor.getLogger().log(Level.SEVERE, null, e);
            }
        }

        try {
            // Cerramos el socket
            socket.close();

            // Cerramos los recursos abiertos
            dis.close();
            dos.close();

            if (usuario != null) {
                Servidor.getServidor().enviarMensajeDeDifusion("\u00a78{0} se ha desconectado del juego.", usuario.getNombre());
                usuario.setConectado(false);
                usuario.guardar();

                Mapa mapa = Servidor.getServidor().getMapa(usuario.getCoordenada().getMapa());

                // Eliminamos el personaje del mundo
                mapa.getBaldosa(usuario.getCoordenada().getPosicion()).setCharindex(0);
            }

            // Eliminamos la conexion de nuestra lista
            conexiones.remove(this);

        } catch (IOException e) {
            Servidor.getLogger().log(Level.SEVERE, null, e);
        }
    }

    public void desconectar() throws IOException {
        desconectar("");
    }

    public void desconectar(String message) {
        try {
            dos.writeByte(PQT_DESCONECTAR);
            dos.writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.conectado = false;
    }

    public void enviarMensaje(String mensaje) {
        try {
            dos.writeByte(PQT_CHAT);
            dos.writeUTF(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarMensaje(String mensaje, Object... args) {
        enviarMensaje(MessageFormat.format(mensaje, args));
    }

    public boolean manejarComando(String mensaje) {
        String[] args = mensaje.split("\\s+");

        switch (args[0].toUpperCase()) {
            case "SALIR":
                desconectar("Gracias por jugar Argentum Online!");
                return true;

            case "ONLINE":
                enviarMensaje("Hay {0} jugadores conectados.", conexiones.size());
                return true;
        }

        enviarMensaje("Comando invalido!");
        return false;
    }

    public boolean manejarInicioDeSesion() {
        try {
            System.out.println("Recibimos un inicio de sesion.");
            // Inicio de sesion
            Byte version = dis.readByte();

            if (versionProtocolo != version) {
                // Version de protocolo incompatible
                desconectar("Protocolo incompatible.");
                return false;
            }

            // Leemos el nombre de usuario
            String nombre = dis.readUTF();

            // Leemos la contraseña del usuario
            String password = dis.readUTF();

            if (nombre.length() > 16) {
                desconectar("El nombre de usuario puede tener como máximo 16 caracteres.");
                return false;
            }

            if (!Usuario.nombreValido(nombre)) {
                desconectar("El nombre de usuario tiene caracteres inválidos.\nSolo se permiten letras y espacios.");
                return false;
            }

            if (!Usuario.existePersonaje(nombre)) {
                desconectar("El personaje no existe.");
                return false;
            }

            try {
                this.usuario = Usuario.cargar(nombre);

                if (!usuario.getPassword().equals(password)) {
                    desconectar("Contraseña incorrecta.");
                    return false;
                }

                if (usuario.isConectado()) {
                    desconectar("El usuario ya se encuentra conectado.");
                    return false;
                }

                if (usuario.getCoordenada().getMapa() == 0) {
                    usuario.getCoordenada().setMapa(1);
                    usuario.getCoordenada().getPosicion().setX(50);
                    usuario.getCoordenada().getPosicion().setY(0);
                }

                Mapa mapa = Servidor.getServidor().getMapa(usuario.getCoordenada().getMapa());
                Baldosa baldosa = mapa.getBaldosa(usuario.getCoordenada().getPosicion());

                if (baldosa.getCharindex() != 0) {
                    // Ya hay alguien parado en esa posicion
                    desconectar("Hay alguien parado en tu posicion, intenta luego.");
                }

                // Creamos el personaje en la posicion del mundo
                Personaje pers = new PersonajeImpl();
                pers.setNombre(usuario.getNombre());
                pers.setAnimacionArma(1);
                pers.setAnimacionCuerpo(usuario.getCuerpo());
                pers.setAnimacionCabeza(usuario.getCabeza());
                pers.setPosicion(usuario.getCoordenada().getPosicion());
                mapa.getPersonajes().add(pers);
                baldosa.setCharindex(1);

                usuario.setConectado(true);
                usuario.guardar();

                enviarUsuarioNombre();
                enviarUsuarioCambiaMapa();
                enviarUsuarioPosicion();
                enviarUsuarioStats();
                usuarioInventarioActualizar();

            } catch (Exception ex) {
                desconectar("Ocurrio un error al cargar el personaje.");
                Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            Servidor.getServidor().enviarMensajeDeDifusion("\u00a78{0} ha ingresado al juego.", nombre);

            // @TODO: Implementar inicio de sesion
            this.username = nombre;
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void usuarioInventarioActualizar() {
        for (int i = 0; i < 20; ++i) {
            usuarioInventarioActualizarSlot(i);
        }
    }

    public void usuarioInventarioActualizarSlot(int slot) {
        InventarioSlot is = usuario.getInventarioSlot(slot);
        if (is == null) {
            return;
        }
        ObjetoMetadata om = is.getObjeto();
        if (om == null) {
            return;
        }
        try {
            dos.writeByte(PQT_ACTUALIZAR_INVENTARIO);
            dos.writeInt(slot);
            dos.writeInt(om.getId());
            dos.writeInt(om.getTipo().valor());
            dos.writeInt(om.getGrhIndex());
            dos.writeUTF(om.getNombre());
            dos.writeInt(is.getCantidad());
            dos.writeBoolean(is.isEquipado());
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarUsuarioCambiaMapa() {
        try {
            dos.writeByte(PQT_CAMBIA_MUNDO);
            dos.writeInt(usuario.getCoordenada().getMapa());
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarUsuarioNombre() {
        try {
            dos.writeByte(PQT_USUARIO_NOMBRE);
            dos.writeUTF(usuario.getNombre());
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarUsuarioPosicion() {
        try {
            dos.writeByte(PQT_USUARIO_POSICION);
            dos.writeInt(usuario.getCoordenada().getMapa());
            dos.writeInt(usuario.getCoordenada().getPosicion().getX());
            dos.writeInt(usuario.getCoordenada().getPosicion().getY());
            dos.writeInt(usuario.getOrientacion().valor());
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarUsuarioStats() {
        try {
            dos.writeByte(PQT_USUARIO_STATS);
            // Salud
            dos.writeInt(usuario.getVida().getMin());
            dos.writeInt(usuario.getVida().getMax());
            // Mana
            dos.writeInt(usuario.getMana().getMin());
            dos.writeInt(usuario.getMana().getMax());
            // Stamina
            dos.writeInt(usuario.getStamina().getMin());
            dos.writeInt(usuario.getStamina().getMax());
            // Hambre
            dos.writeInt(usuario.getHambre().getMin());
            dos.writeInt(usuario.getHambre().getMax());
            // Sed
            dos.writeInt(usuario.getSed().getMin());
            dos.writeInt(usuario.getSed().getMax());

        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarMundoReproducirAnimacion(int x, int y, int animacion) {
        try {
            dos.writeByte(PQT_MUNDO_REPRODUCIR_ANIMACION);
            dos.writeInt(animacion);
            dos.writeInt(x);
            dos.writeInt(y);

        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
