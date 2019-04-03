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

/**
 * Representa que se puede transportar o arrojar en el mundo
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Objeto {

    protected final int id;
    protected int cantidad;
    protected ObjetoMetadata metadata;

    public Objeto(int id) {
        this(id, 1);
    }

    public Objeto(int id, int cantidad) {
        this.id = id;
        this.metadata = ObjetosDB.obtenerCopia(id);
        this.cantidad = cantidad;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the cantidad
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the metadata
     */
    public ObjetoMetadata getMetadata() {
        return metadata;
    }
}
