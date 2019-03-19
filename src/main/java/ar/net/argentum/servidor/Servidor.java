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
package ar.net.argentum.servidor;

import ar.net.argentum.servidor.configuracion.ConfiguracionGeneral;
import ar.net.argentum.servidor.mundo.Personaje;
import ar.net.argentum.servidor.mundo.UtilMapas;
import ar.net.argentum.servidor.protocolo.ConexionConCliente;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Servidor {

    protected static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());
    private static int ultimoCharindex = 2;
    private static Servidor instancia;

    public static Servidor getServidor() {
        if (null == instancia) {
            try {
                instancia = new Servidor();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return instancia;
    }

    public static void main(String[] args) throws IOException {
        Servidor servidor = Servidor.getServidor();
        servidor.iniciar();
    }

    private final ObjetosDB objetosdb;
    private final ConfiguracionGeneral configuracionGeneral;
    private ServerSocket serverSocket;
    private Map<Usuario, ConexionConCliente> conexiones;
    private final Map<Integer, Mapa> mapas;
    private final Map<Integer, Personaje> personajes;

    private Servidor() throws IOException {
        // Iniciar configuracion
        this.configuracionGeneral = new ConfiguracionGeneral("config.properties");
        this.objetosdb = new ObjetosDB("datos/objetos.json");
        this.mapas = new HashMap<>();
        this.personajes = new HashMap<>();
        cargarMapas();
    }

    public void iniciar() {
        LOGGER.log(Level.INFO, "Iniciando servidor en el puerto " + configuracionGeneral.getPuerto() + "...");
        try {
            // Iniciar socket, el puerto por defecto es 7666
            this.serverSocket = new ServerSocket(configuracionGeneral.getPuerto());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);

            // Si no podemos iniciar el socket ya no hay nada que hacer :(
            return;
        }

        // Creamos una lista para mantener las conexiones
        this.conexiones = new HashMap<>();

        // Bucle infinito
        while (true) {
            Socket socket = null;

            try {
                // Recibir conexiones entrantes
                socket = serverSocket.accept();

                LOGGER.log(Level.INFO, "Se ha conectado un nuevo cliente: " + socket);
                LOGGER.log(Level.INFO, "Asignando nuevo Thread al cliente");

                // Creamos un objeto Usuario
                Usuario usuario = new Usuario();
                // Crear nuevo Thread
                ConexionConCliente t = new ConexionConCliente(socket, usuario, conexiones);

                // Agregamos el nuevo thread a la lista de conexiones
                conexiones.put(usuario, t);

                // Iniciar el hilo 
                t.start();

            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                if (null != socket) {
                    // Si creamos el Socket entonces hay que cerrarlo
                    try {
                        socket.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                    }
                }
            }
        }
    }

    public void enviarMensajeDeDifusion(String mensaje) {
        LOGGER.log(Level.INFO, "[DIFUSION] " + mensaje);
        for (Map.Entry<Usuario, ConexionConCliente> entry : conexiones.entrySet()) {
            entry.getValue().enviarMensaje(mensaje);
        }
    }

    public void enviarMensajeDeDifusion(String mensaje, Object... args) {
        enviarMensajeDeDifusion(MessageFormat.format(mensaje, args));
    }

    private void cargarMapas() {
        File directorio = new File("datos/mapas");
        for (File f : directorio.listFiles()) {
            if (f.getName().endsWith(".map")) {
                String nmapa = f.getName().substring(0, f.getName().length() - 4).substring(4);
                int numMapa = Integer.valueOf(nmapa);
                Mapa mapa = UtilMapas.cargarMapa(numMapa, f.getName());
                mapas.put(numMapa, mapa);
            }
        }
    }

    public Mapa getMapa(int numMapa) {
        return mapas.get(numMapa);
    }

    /**
     * Devuelve la conexion establecida con un usuario en particular
     *
     * @param usuario Usuario del cual obtener la conexion
     * @return Conexion establecida con el usuario
     */
    public ConexionConCliente getConexion(Usuario usuario) {
        return conexiones.get(usuario);
    }

    public static synchronized int crearCharindex() {
        return ultimoCharindex++;
    }
    
    public void todosMenosUsuarioArea(Usuario usuario, EnvioAUsuario envio) {
        for (Map.Entry<Usuario, ConexionConCliente> entry : conexiones.entrySet()) {
            if (usuario.equals(entry.getKey())) {
                continue;
            }
            envio.enviar(entry.getKey(), entry.getValue());
        }
    }
}
