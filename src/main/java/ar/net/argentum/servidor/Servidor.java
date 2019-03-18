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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Servidor {

    private static Servidor instancia;

    public static Servidor getServidor() {
        if (null == instancia) {
            try {
                instancia = new Servidor();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instancia;
    }

    public static void main(String[] args) throws IOException {
        Servidor servidor = Servidor.getServidor();
        servidor.iniciar();
    }

    public static Logger getLogger() {
        return Logger.getLogger(Servidor.class.getName());
    }

    private final ObjetosDB objetosdb;
    private final ConfiguracionGeneral configuracionGeneral;
    private ServerSocket serverSocket;
    private ArrayList<ConexionConCliente> conexiones;
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
        Servidor.getLogger().log(Level.INFO, "Iniciando servidor en el puerto " + configuracionGeneral.getPuerto() + "...");
        try {
            // Iniciar socket, el puerto por defecto es 7666
            this.serverSocket = new ServerSocket(configuracionGeneral.getPuerto());
        } catch (IOException ex) {
            Servidor.getLogger().log(Level.SEVERE, null, ex);

            // Si no podemos iniciar el socket ya no hay nada que hacer :(
            return;
        }

        // Creamos una lista para mantener las conexiones
        this.conexiones = new ArrayList<>();

        // Bucle infinito
        while (true) {
            Socket s = null;

            try {
                // Recibir conexiones entrantes
                s = serverSocket.accept();

                System.out.println("Se ha conectado un nuevo cliente: " + s);
                System.out.println("Asignando nuevo Thread al cliente");

                // Crear nuevo Thread
                ConexionConCliente t = new ConexionConCliente(s, conexiones);

                // Agregamos el nuevo thread a la lista de conexiones
                conexiones.add(t);

                // Iniciar el hilo 
                t.start();

            } catch (IOException ex) {
                Servidor.getLogger().log(Level.SEVERE, null, ex);
                if (null != s) {
                    // Si creamos el Socket entonces hay que cerrarlo
                    try {
                        s.close();
                    } catch (IOException e) {
                        Servidor.getLogger().log(Level.SEVERE, null, e);
                    }
                }
            }
        }
    }

    public void enviarMensajeDeDifusion(String mensaje) {
        System.out.println(mensaje);
        for (ConexionConCliente usuario : conexiones) {
            usuario.enviarMensaje(mensaje);
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
}
