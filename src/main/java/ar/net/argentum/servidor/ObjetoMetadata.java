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
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface ObjetoMetadata {

    /**
     * ID de animacion, es usado por objetos que se pueden vestir
     *
     * @return Animacion
     */
    int getAnimacion();

    /**
     * @return the grhIndex
     */
    int getGrhIndex();

    /**
     * @return the id
     */
    int getId();

    /**
     * @return the maxItems
     */
    int getMaxItems();

    /**
     * @return the nombre
     */
    String getNombre();

    /**
     * @return ID de cuerpo para la vestimenta
     */
    int getRopaje();

    /**
     * @return the tipo
     */
    ObjetoTipo getTipo();

    /**
     * @return Verdadero si el objeto puede ser agarrado
     */
    boolean isAgarrable();

    /**
     * @return Verdadero si el objeto es NEWBIE
     */
    boolean isNewbie();

    /**
     * @param agarrable Verdadero si el objeto puede ser agarrado
     */
    void setAgarrable(boolean agarrable);

    /**
     * Establecer ID de animacion, es usado por objetos que se pueden vestir
     *
     * @param animacion the animacion to set
     */
    void setAnimacion(int animacion);

    /**
     * @param newbie Verdadero si el objeto es NEWBIE
     */
    void setNewbie(boolean newbie);

    /**
     * @param ropaje ID de cuerpo para la vestimenta
     */
    void setRopaje(int ropaje);

    public int getGrhSecundario();

    public void setGrhSecundario(int grafico);
}
