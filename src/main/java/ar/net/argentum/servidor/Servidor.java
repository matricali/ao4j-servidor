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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Servidor {

    protected static final Logger LOGGER = Logger.getLogger(Servidor.class);
    private static int ultimoCharindex = 2;
    private static int ultimoUseridex = 0;
    private static Servidor instancia;

    public static Servidor getServidor() {
        if (null == instancia) {
            try {
                instancia = new Servidor();
            } catch (IOException ex) {
                LOGGER.fatal(null, ex);
            }
        }
        return instancia;
    }

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Servidor servidor = Servidor.getServidor();
        servidor.iniciar();
    }

    private final ObjetosDB objetosdb;
    private final ConfiguracionGeneral configuracionGeneral;
    private final LinkedList<ConexionConCliente> conexiones;
    private final Map<Integer, Mapa> mapas;
    private final ConcurrentMap<Integer, Personaje> personajes;
    private final int intervaloEventos = 40;
    private long timerEventos = getTimer();
    private ServerSocket serverSocket;

    private Servidor() throws IOException {
        // Iniciar configuracion
        this.configuracionGeneral = new ConfiguracionGeneral("config.properties");
        this.objetosdb = new ObjetosDB("datos/objetos.json");
        this.mapas = new ConcurrentHashMap<>();
        this.personajes = new ConcurrentHashMap<>();
        // Creamos una lista para mantener las conexiones
        this.conexiones = new LinkedList<>();
        cargarMapas();
    }

    public void iniciar() {
        LOGGER.info("Iniciando servidor en el puerto " + configuracionGeneral.getPuerto() + "...");
        try {
            // Iniciar socket, el puerto por defecto es 7666
            this.serverSocket = new ServerSocket(configuracionGeneral.getPuerto());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);

            // Si no podemos iniciar el socket ya no hay nada que hacer :(
            return;
        }

        Thread logica = new Thread() {
            @Override
            public void run() {
                LOGGER.info("Iniciando thread de logica");
                while (true) {
                    try {
                        procesarEventos();
                    } catch (Exception ex) {
                        LOGGER.fatal(null, ex);
                    }
                }
            }
        };

        logica.start();

        // Bucle infinito
        while (true) {
            Socket socket = null;

            try {
                // Recibir conexiones entrantes
                socket = serverSocket.accept();

                LOGGER.info("Se ha conectado un nuevo cliente: " + socket);
                LOGGER.info("Asignando nuevo Thread al cliente");

                // Crear nuevo Thread
                ConexionConCliente t = new ConexionConCliente(socket);

                // Agregamos el nuevo thread a la lista de conexiones
                conexiones.add(t);

                // Iniciar el hilo 
                t.start();

            } catch (IOException ex) {
                LOGGER.fatal(null, ex);
                if (null != socket) {
                    // Si creamos el Socket entonces hay que cerrarlo
                    try {
                        socket.close();
                    } catch (IOException e) {
                        LOGGER.fatal(null, e);
                    }
                }
            }
        }
    }

    public void enviarMensajeDeDifusion(String mensaje) {
        LOGGER.info("[DIFUSION] " + mensaje);
        for (ConexionConCliente conn : conexiones) {
            if (conn.isConectado()) {
                conn.enviarMensaje(mensaje);
            }
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
    public synchronized ConexionConCliente getConexion(Usuario usuario) {
        for (ConexionConCliente conn : conexiones) {
            if (conn.getUsuario().getCharindex() == usuario.getCharindex()) {
                return conn;
            }
        }
        return null;
    }

    public synchronized void eliminarConexion(Usuario usuario) {
        LOGGER.info("Eliminando conexiones (" + conexiones.size() + ")\n" + conexiones);
        for (ConexionConCliente conn : conexiones) {
            if (conn.getUsuario().getCharindex() == usuario.getCharindex()) {
                conexiones.remove(conn);
                return;
            }
        }
        LOGGER.info("Quedaron asi (" + conexiones.size() + ")\n" + conexiones);
    }

    public synchronized List<ConexionConCliente> getConexiones() {
        return conexiones;
    }

    public static synchronized int crearUserindex() {
        ultimoUseridex++;

        LOGGER.info("Generado nuevo userindex >>" + ultimoUseridex);
        return ultimoUseridex;
    }

    public static synchronized int crearCharindex() {
        ultimoCharindex++;

        LOGGER.info("Generado nuevo charindex >>" + ultimoCharindex);
        return ultimoCharindex;
    }

    public void todosMenosUsuarioArea(Usuario usuario, EnvioAUsuario envio) {
        for (ConexionConCliente conn : conexiones) {
            if (conn.getUsuario() == null) {
                // Hay conexiones establebecidas que todavia no estan jugando
                continue;
            }
            if (conn.getUsuario().getCharindex() == usuario.getCharindex()) {
                continue;
            }
            envio.enviar(conn.getUsuario().getCharindex(), conn);
        }
    }

    public int getJugadoresConectados() {
        return conexiones.size();
    }

    /**
     * Obtiene la instancia del personaje en base al charindex
     *
     * @param charindex
     * @return
     */
    public Personaje getPersonaje(int charindex) {
        return personajes.get(charindex);
    }

    /**
     * @return Obtiene el valor actual del reloj de la maquina virtual en
     * ejecucion
     * @see System.nanoTime
     */
    private long getTimer() {
        return System.nanoTime() / 1000000;
    }

    public void procesarEventos() {
        if (getTimer() - timerEventos < intervaloEventos) {
            return;
        }
        this.timerEventos = getTimer();

        List<ConexionConCliente> lista = getConexiones();
        // Procesar eventos de los usuarios
        for (ConexionConCliente conexion : lista) {
            if (conexion.getUsuario() != null && conexion.getUsuario().isConectado()) {
                conexion.getUsuario().tick();
            }
        }
    }
}
