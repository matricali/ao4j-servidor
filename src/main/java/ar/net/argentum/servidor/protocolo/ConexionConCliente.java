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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;

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

                System.out.println("Recibimos un paquete nuevo :D");

                // Manejamos el paquete recibido
                switch (tipoPaquete) {

                    case DESCONECTAR:
                        System.out.println("Recibimos un cierre de conexion.");
                        // Terminamos
                        this.conectado = false;
                        break;

                    case INICIAR_SESION:
                        System.out.println("Recibimos un inicio de sesion.");
                        // Inicio de sesion
                        Byte version = dis.readByte();

                        if (versionProtocolo != version) {
                            // Version de protocolo incompatible
                            desconectar("Protocolo incompatible.");
                            break;
                        }

                        String usuario = dis.readUTF();

                        if (usuario.isEmpty()) {
                            desconectar("Usuario invalido.");
                            break;
                        }

                        String password = dis.readUTF();

                        System.out.println("Inicio de sesion recibido -> " + usuario + ":" + password);
                        Servidor.getServidor().enviarMensajeDeDifusion("{0} ha ingresado al juego.", usuario);

                        // @TODO: Implementar inicio de sesion
                        this.username = usuario;
                        break;

                    case CHAT:
                        String mensaje = dis.readUTF();
                        System.out.println(">>" + mensaje);
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
            Servidor.getServidor().enviarMensajeDeDifusion("{0} se ha desconectado del juego.", username);
                    
            // Cerramos el socket
            socket.close();

            // Cerramos los recursos abiertos
            dis.close();
            dos.close();

            // Eliminamos la conexion de nuestra lista
            conexiones.remove(this);

        } catch (IOException e) {
            Servidor.getLogger().log(Level.SEVERE, null, e);
        }
    }

    public void desconectar() throws IOException {
        desconectar("");
    }

    public void desconectar(String message) throws IOException {
        dos.writeByte(DESCONECTAR);
        dos.writeUTF(message);
        this.conectado = false;
    }

    public void enviarChat(String mensaje) throws IOException {
        dos.writeByte(0x3);
        dos.writeUTF(mensaje);
    }
}
