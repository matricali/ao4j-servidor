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
 * Metadata de un objeto
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ObjetoMetadata {

    protected int id;
    protected String nombre;
    protected ObjetoTipo tipo;
    protected int grhIndex;
    int grhSecundario;

    protected int maxItems;
    boolean apunala;
    boolean achuchilla;
    protected boolean newbie;
    protected int animacion;

    public ObjetoMetadata(int id, String nombre, ObjetoTipo tipo, int grhIndex, int grhSecundario, int maxItems) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.grhIndex = grhIndex;
        this.grhSecundario = grhSecundario;
        this.maxItems = maxItems;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the tipo
     */
    public ObjetoTipo getTipo() {
        return tipo;
    }

    /**
     * @return the grhIndex
     */
    public int getGrhIndex() {
        return grhIndex;
    }

    /**
     * @return the maxItems
     */
    public int getMaxItems() {
        return maxItems;
    }

    /**
     * @return Verdadero si el objeto es NEWBIE
     */
    public boolean isNewbie() {
        return newbie;
    }

    /**
     * @param newbie Verdadero si el objeto es NEWBIE
     */
    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }

    /**
     * ID de animacion, es usado por objetos que se pueden vestir
     *
     * @return Animacion
     */
    public int getAnimacion() {
        return animacion;
    }

    /**
     * Establecer ID de animacion, es usado por objetos que se pueden vestir
     *
     * @param animacion the animacion to set
     */
    public void setAnimacion(int animacion) {
        this.animacion = animacion;
    }

}
