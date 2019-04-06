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

import java.util.Map;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Raza {

    protected String nombre;
    protected String descripcion = "";
    protected Map<String, Integer> cuerpo;
    protected ModificadoresRaza modificadores;

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
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the modificadores
     */
    public ModificadoresRaza getModificadores() {
        return modificadores;
    }

    /**
     * @param modificadores the modificadores to set
     */
    public void setModificadores(ModificadoresRaza modificadores) {
        this.modificadores = modificadores;
    }

    /**
     * @return the cuerpo
     */
    public Map<String, Integer> getCuerpo() {
        return cuerpo;
    }

    /**
     * @param genero
     * @return Cuerpo para el genero especificado
     */
    public int getCuerpo(String genero) {
        return cuerpo.get(genero);
    }

    /**
     * @param cuerpo the cuerpo to set
     */
    public void setCuerpo(Map<String, Integer> cuerpo) {
        this.cuerpo = cuerpo;
    }

}
