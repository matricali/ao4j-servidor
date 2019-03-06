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
import ar.net.argentum.servidor.protocolo.ConexionConCliente;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Servidor {

    private final ConfiguracionGeneral configuracionGeneral;
    private ServerSocket serverSocket;

    public Servidor() throws IOException {
        // Iniciar configuracion
        this.configuracionGeneral = new ConfiguracionGeneral("config.properties");
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

        // Bucle infinito
        while (true) {
            Socket s = null;

            try {
                // Recibir conexiones entrantes
                s = serverSocket.accept();

                System.out.println("Se ha conectado un nuevo cliente: " + s);

                // Obtener Streams de entrada y salida
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Asignando nuevo Thread al cliente");

                // Crear nuevo Thread
                Thread t = new ConexionConCliente(s, dis, dos);

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

    public static void main(String[] args) throws IOException {
        Servidor servidor = new Servidor();
        servidor.iniciar();
    }

    public static Logger getLogger() {
        return Logger.getLogger(Servidor.class.getName());
    }
}
