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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Usuario {

    /**
     * Compruebe si esta registrado un usuario.
     *
     * @param nombre Nombre de usuario
     * @return bool Verdadero si el usuario ya existe
     */
    public static boolean existePersonaje(String nombre) {
        File f = getArchivo(nombre);
        return f.exists() && !f.isDirectory();
    }

    public static boolean nombreValido(String nombre) {
        if (nombre.isEmpty() || nombre.length() > 16) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]+$");
        Matcher matcher = pattern.matcher(nombre);
        return matcher.find();
    }

    public static Usuario cargar(String nombre) throws Exception {
        if (!existePersonaje(nombre)) {
            throw new Exception("No existe el personaje!");
        }
        ObjectMapper mapper = new ObjectMapper();
        Usuario usuario = mapper.readValue(getArchivo(nombre), Usuario.class);
        return usuario;
    }
    protected String nombre;
    protected String password;
    protected boolean conectado;

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the conectado
     */
    public boolean isConectado() {
        return conectado;
    }

    /**
     * @param conectado the conectado to set
     */
    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
    
    public void guardar() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(Usuario.getArchivo(nombre), this);
        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static File getArchivo(String nombre) {
        return new File("datos/personajes/" + nombre.toLowerCase()+ ".json");
    }
}
