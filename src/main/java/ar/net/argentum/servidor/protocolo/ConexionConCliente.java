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
import ar.net.argentum.servidor.Logica;
import ar.net.argentum.servidor.Objeto;
import ar.net.argentum.servidor.ObjetoMetadata;
import ar.net.argentum.servidor.Personaje;
import ar.net.argentum.servidor.Posicion;
import ar.net.argentum.servidor.Servidor;
import ar.net.argentum.servidor.Usuario;
import ar.net.argentum.servidor.mundo.Orientacion;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;
import org.apache.log4j.Logger;

/**
 * Clase encargada de la comunicacion entre el cliente y el servidor
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ConexionConCliente extends Thread {

    protected static final byte PQT_DESCONECTAR = 0x1;
    protected static final byte PQT_INICIAR_SESION = 0x2;
    protected static final byte PQT_CHAT = 0x3;
    protected static final byte PQT_ACTUALIZAR_INVENTARIO = 0x4;
    protected static final byte PQT_CAMBIA_MUNDO = 0x5;
    protected static final byte PQT_USUARIO_NOMBRE = 0x6;
    protected static final byte PQT_USUARIO_POSICION = 0x7;
    protected static final byte PQT_USUARIO_STATS = 0x8;
    protected static final byte PQT_MUNDO_REPRODUCIR_ANIMACION = 0x9;
    protected static final byte PQT_USUARIO_CAMINAR = 0x10;
    protected static final byte PQT_USUARIO_CAMBIAR_DIRECCION = 0x11;
    protected static final byte PQT_PERSONAJE_CREAR = 0x12;
    protected static final byte PQT_PERSONAJE_CAMBIAR = 0x13;
    protected static final byte PQT_PERSONAJE_CAMINAR = 0x14;
    protected static final byte PQT_PERSONAJE_ANIMACION = 0x15;
    protected static final byte PQT_PERSONAJE_QUITAR = 0x16;
    protected static final byte PQT_CLICK = 0x17;
    protected static final byte PQT_USUARIO_GOLPEA = 0x18;
    protected static final byte PQT_USUARIO_EXPERIENCIA = 0x19;
    protected static final byte PQT_USUARIO_EQUIPAR_SLOT = 0x20;
    protected static final byte PQT_MUNDO_REPRODUCIR_SONIDO = 0x21;
    protected static final byte PQT_MUNDO_OBJETO = 0x22;
    protected static final byte PQT_USUARIO_TIRAR_OBJETO = 0x24;
    protected static final byte PQT_USUARIO_AGARRAR_OBJETO = 0x25;

    protected static final Logger LOGGER = Logger.getLogger(ConexionConCliente.class);
    /**
     * Conexion
     */
    protected final Socket socket;

    /**
     * Buffer de entrada
     */
    protected final DataInputStream dis;

    /**
     * Buffer de salida
     */
    protected final DataOutputStream dos;

    /**
     * El usuario esta conectado?
     */
    protected boolean conectado = false;

    /**
     * Version del protocolo implementado
     */
    protected final byte versionProtocolo = 0x1;

    /**
     * Nombre de usuario
     */
    protected String username = "";

    /**
     * Instancia del usuario
     */
    protected Usuario usuario;

    /**
     * Crear un nuevo thread para manejar la comunicacion con un usuario
     *
     * @param s Instancia del socket
     * @throws IOException
     */
    public ConexionConCliente(Socket s) throws IOException {
        this.socket = s;
//        this.conexiones = conexiones;

        // Obtener Streams de entrada y salida
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {
        LOGGER.info("Nuevo thread iniciado.");

        this.conectado = true;

        while (conectado) {
            try {
                // Obtenemos el tipo de paquete recibido
                byte tipoPaquete;

                try {
                    tipoPaquete = dis.readByte();
                } catch (EOFException | SocketException ex) {
                    LOGGER.fatal(null, ex);
                    this.conectado = false;
                    break;
                }

                // Manejamos el paquete recibido
                switch (tipoPaquete) {

                    case PQT_DESCONECTAR:
                        LOGGER.info("Recibimos un cierre de conexion.");

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

                    case PQT_USUARIO_CAMINAR:
                        manejarUsuarioCaminar();
                        break;

                    case PQT_USUARIO_CAMBIAR_DIRECCION:
                        manejarUsuarioCambiarDireccion();
                        break;

                    case PQT_CLICK:
                        manejarClick();
                        break;

                    case PQT_USUARIO_GOLPEA:
                        manejarUsuarioGolpea();
                        break;

                    case PQT_USUARIO_EQUIPAR_SLOT:
                        manejarUsuarioEquiparSlot();
                        break;

                    case PQT_USUARIO_TIRAR_OBJETO:
                        manejarUsuarioTirarObjeto();
                        break;

                    case PQT_USUARIO_AGARRAR_OBJETO:
                        manejarUsuarioAgarrarObjeto();
                        break;

                    default:
                        LOGGER.fatal("Recibimos un paquete que no supimos manejar!");
                        desconectar("Paquete invalido.");
                        break;
                }
            } catch (IOException e) {
                LOGGER.fatal(null, e);
            }
        }

        try {
            // Cerramos el socket
            socket.close();

            // Cerramos los recursos abiertos
            dis.close();
            dos.close();

            if (usuario != null) {
                // Disparamos el evento
                usuario.alDesconectarse();
            }
            // Eliminamos la conexion de nuestra lista
            Servidor.getServidor().eliminarConexion(this);
        } catch (IOException e) {
            LOGGER.fatal(null, e);
        }
    }

    /**
     * Desconectar al usuario
     */
    public void desconectar() {
        desconectar("");
    }

    /**
     * Desconectar al usuario enviando un mensaje
     *
     * @param mensaje Mensaje a enviar al usuario
     */
    public void desconectar(String mensaje) {
        try {
            dos.writeByte(PQT_DESCONECTAR);
            dos.writeUTF(mensaje);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        this.conectado = false;
    }

    public void enviarMensaje(String mensaje) {
        try {
            dos.writeByte(PQT_CHAT);
            dos.writeUTF(mensaje);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
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
                enviarMensaje("Hay {0} jugadores conectados.", Servidor.getServidor().getJugadoresConectados());
                return true;

            case "MEDITAR":
                usuario.meditar();
                return true;
        }

        enviarMensaje("Comando invalido!");
        return false;
    }

    public boolean manejarInicioDeSesion() {
        try {
            LOGGER.info("Recibimos un inicio de sesion.");
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
                Usuario u = Usuario.cargar(nombre);

                if (!u.getPassword().equals(password)) {
                    desconectar("Contraseña incorrecta.");
                    return false;
                }

                if (u.isConectado()) {
                    desconectar("El usuario ya se encuentra conectado.");
                    return false;
                }

                this.username = nombre;
                this.usuario = u;

                // Disparamos el evento que indica que el usuario se conecto
                usuario.alConectarse();
                return true;
            } catch (Exception ex) {
                desconectar("Ocurrio un error al cargar el personaje.");
                LOGGER.fatal("Ocurrio un error al cargar el personaje.", ex);
                return false;
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
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

        try {

            if (is == null) {
                dos.writeByte(PQT_ACTUALIZAR_INVENTARIO);
                dos.writeInt(slot);
                dos.writeInt(0);
                dos.writeInt(0);
                dos.writeInt(0);
                dos.writeUTF("");
                dos.writeInt(0);
                dos.writeBoolean(false);
                return;
            }

            ObjetoMetadata om = is.getObjeto();
            if (om == null) {
                return;
            }

            dos.writeByte(PQT_ACTUALIZAR_INVENTARIO);
            dos.writeInt(slot);
            dos.writeInt(om.getId());
            dos.writeInt(om.getTipo().valor());
            dos.writeInt(om.getGrhIndex());
            dos.writeUTF(om.getNombre());
            dos.writeInt(is.getCantidad());
            dos.writeBoolean(is.isEquipado());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioCambiaMapa() {
        try {
            dos.writeByte(PQT_CAMBIA_MUNDO);
            dos.writeInt(usuario.getCoordenada().getMapa());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioNombre() {
        try {
            dos.writeByte(PQT_USUARIO_NOMBRE);
            dos.writeUTF(usuario.getNombre());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
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
            LOGGER.fatal(null, ex);
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
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioExperiencia() {
        try {
            dos.writeByte(PQT_USUARIO_EXPERIENCIA);
            dos.writeInt(usuario.getNivel());
            dos.writeInt(usuario.getExperienciaActual());
            dos.writeInt(usuario.getExperienciaSiguienteNivel());

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarMundoReproducirAnimacion(int x, int y, int animacion) {
        try {
            dos.writeByte(PQT_MUNDO_REPRODUCIR_ANIMACION);
            dos.writeInt(animacion);
            dos.writeInt(x);
            dos.writeInt(y);

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarPersonajeCrear(int charindex, int heading, int x, int y, int cuerpo, int cabeza, int arma, int escudo, int casco) {
        try {
            dos.writeByte(PQT_PERSONAJE_CREAR);
            dos.writeInt(charindex);
            dos.writeInt(heading);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(cuerpo);
            dos.writeInt(cabeza);
            dos.writeInt(arma);
            dos.writeInt(escudo);
            dos.writeInt(casco);

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarPersonajeAnimacion(int charindex, int efecto, int repeticiones) {
        try {
            dos.writeByte(PQT_PERSONAJE_ANIMACION);
            dos.writeInt(charindex);
            dos.writeInt(efecto);
            dos.writeInt(repeticiones);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarPersonajeCaminar(int charindex, int heading) {
        try {
            dos.writeByte(PQT_PERSONAJE_CAMINAR);
            dos.writeInt(charindex);
            dos.writeInt(heading);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarPersonajeQuitar(int charindex) {
        try {
            dos.writeByte(PQT_PERSONAJE_QUITAR);
            dos.writeInt(charindex);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarPersonajeCambiar(int charindex, int heading, int cuerpo, int cabeza, int arma, int escudo, int casco) {
        try {
            dos.writeByte(PQT_PERSONAJE_CAMBIAR);
            dos.writeInt(charindex);
            dos.writeInt(heading);
            dos.writeInt(cuerpo);
            dos.writeInt(cabeza);
            dos.writeInt(arma);
            dos.writeInt(escudo);
            dos.writeInt(casco);

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarMundoReproducirSonido(int sonido) {
        enviarMundoReproducirSonido(
                sonido,
                getUsuario().getCoordenada().getPosicion());
    }

    public void enviarMundoReproducirSonido(int sonido, Posicion pos) {
        enviarMundoReproducirSonido(
                sonido,
                pos.getX(),
                pos.getY());
    }

    public void enviarMundoReproducirSonido(int sonido, int x, int y) {
        try {
            dos.writeByte(PQT_MUNDO_REPRODUCIR_SONIDO);
            dos.writeInt(sonido);
            dos.writeInt(x);
            dos.writeInt(y);

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public boolean manejarUsuarioCaminar() {
        try {
//            if (dis.available() < 1) {
//                throw new Error("Data insuficiente");
//            }

            byte heading = dis.readByte();

            // @TODO: Prevenir speed hack
            if (!usuario.mover(Orientacion.valueOf(heading))) {
                enviarUsuarioPosicion();
            }
            return true;
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }

    public boolean manejarUsuarioCambiarDireccion() {
        try {
//            if (dis.available() < 1) {
//                throw new Error("Data insuficiente");
//            }

            byte heading = dis.readByte();

            // @TODO: Prevenir speed hack
            // @TODO: Cancelar /salir
            if (usuario.isMeditando()) {
                // Detenemos la meditacion
                usuario.setMeditando(false);
                // @TODO: Enviar efecto 0
                return true;
            }

            // Paramos de descansar
            if (usuario.isDescansando()) {
                usuario.setDescansando(false);
            }

            // @TODO: Solo el ladron y el bandido pueden caminar ocultos
            // Establecemos la nueva orientacion
            usuario.setOrientacion(Orientacion.valueOf(heading));

            // Le avisamos a los cercanos
            Servidor.getServidor().todosMenosUsuarioArea(usuario, (u, conexion) -> {
                conexion.enviarPersonajeCambiar(
                        usuario.getCharindex(),
                        usuario.getOrientacion().valor(),
                        usuario.getCuerpo(),
                        usuario.getCabeza(),
                        usuario.getArma(),
                        usuario.getEscudo(),
                        usuario.getCasco());
            });
            return true;
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }

    public boolean isConectado() {
        return socket.isConnected();
    }

    public boolean manejarClick() {
        try {
            int x = dis.readInt();
            int y = dis.readInt();
            LOGGER.debug("PQT_CLICK<<" + x + "<<" + y);

            if (Math.abs(getUsuario().getCoordenada().getPosicion().getX() - x) > Logica.RANGO_VISION_X
                    || Math.abs(getUsuario().getCoordenada().getPosicion().getY() - y) > Logica.RANGO_VISION_Y) {
                // Esta fuera de su rango de vision. Cheater?
                return false;
            }

            if (!Logica.isDentroDelLimite(getUsuario().getCoordenada().getMapa(), x, y)) {
                // No es una posicion valida
                return false;
            }

            // Comenzamos a buscar que hay en la baldosa indicada
            try {
                Baldosa b = Servidor.getServidor().getMapa(usuario.getCoordenada().getMapa()).getBaldosa(x, y);
                boolean encontramos = false;
                if (b.hayObjeto()) {
                    encontramos = true;
                    // Hay un objeto en la posicion indicada
                    Objeto obj = b.getObjeto();
                    switch (obj.getMetadata().getTipo()) {
                        case PUERTA:
                        case FORO:
                        case CARTEL:
                        case ARBOL:
                        case YACIMIENTO:
                        case TELETRANSPORTE:
                            enviarMensaje(obj.getMetadata().getNombre());
                            break;
                        default:
                            enviarMensaje("{0} - {1}", obj.getMetadata().getNombre(), obj.getCantidad());
                    }
                }

                if (b.getCharindex() > 0) {
                    encontramos = true;
                    Personaje p = b.getPersonaje();
                    if (p != null) {
                        // @TODO: Verificar si es usuario o NPC
                        enviarMensaje("Ves a {0}", p.getNombre());
                        return true;
                    }
                }

                if (!encontramos) {
                    enviarMensaje("No hay nada interesante.");
                }
                return true;
            } catch (Exception ex) {
                LOGGER.fatal(null, ex);
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }

    public boolean manejarUsuarioGolpea() {
        LOGGER.debug("PQT_USUARIO_GOLPEA<<");
        // @TODO: Prevenir speed hack
        usuario.doGolpear();
        return true;
    }

    public boolean manejarUsuarioEquiparSlot() {
        try {
            int invslot = dis.readInt();
            LOGGER.debug("PQT_USUARIO_EQUIPAR_SLOT<<" + invslot);
            usuario.inventarioEquiparSlot(invslot);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }
    public void enviarMundoObjeto(int x, int y, int objIndex, int grhIndex,
            int cantidad, String nombre) {

        LOGGER.debug("PQT_MUNDO_OBJETO>>" + x + ">>" + y + ">>" + objIndex + ">>"
                + grhIndex + ">>" + cantidad + ">>" + nombre);
        try {
            dos.writeByte(PQT_MUNDO_OBJETO);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(objIndex);
            dos.writeInt(grhIndex);
            dos.writeInt(cantidad);
            dos.writeUTF(nombre);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarMundoObjeto(int x, int y, Objeto obj) {
        if (obj == null) {
            // Quitamos el objeto
            enviarMundoObjeto(x, y, 0, 0, 0, "");
            return;
        }
        enviarMundoObjeto(x, y, obj.getId(), obj.getMetadata().getGrhIndex(),
                obj.getCantidad(), obj.getMetadata().getNombre());
    }

    public boolean manejarUsuarioTirarObjeto() {
        try {
            int invslot = dis.readInt();
            int cantidad = dis.readInt();
            LOGGER.debug("PQT_USUARIO_TIRAR_OBJETO<<" + invslot + "<<" + cantidad);
            usuario.inventarioTirarObjeto(invslot, cantidad);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }

    public boolean manejarUsuarioAgarrarObjeto() {

        LOGGER.debug("PQT_USUARIO_AGARRAR_OBJETO<<");
        usuario.inventarioAgarrarObjeto();

        return false;
    }
}
