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

import ar.net.argentum.servidor.Servidor;
import ar.net.argentum.servidor.Usuario;
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

    private static final byte DESCONECTAR = 0x1;
    private static final byte INICIAR_SESION = 0x2;
    private static final byte CHAT = 0x3;

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

                    case DESCONECTAR:
                        System.out.println("Recibimos un cierre de conexion.");
                        // Terminamos
                        this.conectado = false;
                        break;

                    case INICIAR_SESION:
                        manejarInicioDeSesion();
                        break;

                    case CHAT:
                        String mensaje = dis.readUTF();

                        if (mensaje.startsWith("/")) {
                            // El usuario ha ingresado un comando
                            // Le quitamos la "/" inicial
                            manejarComando(mensaje.substring(1));
                            break;
                        }

                        Servidor.getServidor().enviarMensajeDeDifusion(username + ": " + mensaje);
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
                Servidor.getServidor().enviarMensajeDeDifusion("{0} se ha desconectado del juego.", usuario.getNombre());
                usuario.setConectado(false);
                usuario.guardar();
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
            dos.writeByte(DESCONECTAR);
            dos.writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.conectado = false;
    }

    public void enviarMensaje(String mensaje) {
        try {
            dos.writeByte(0x3);
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

                usuario.setConectado(true);
                usuario.guardar();

            } catch (Exception ex) {
                desconectar("Ocurrio un error al cargar el personaje.");
                Logger.getLogger(ConexionConCliente.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            Servidor.getServidor().enviarMensajeDeDifusion("{0} ha ingresado al juego.", nombre);

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
}
