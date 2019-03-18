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
package ar.net.argentum.servidor.mundo;

import ar.net.argentum.servidor.Posicion;

/**
 * Representa un personaje que camina por el mundo :D
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Personaje {

    /**
     * @return the animacionArma
     */
    int getAnimacionArma();

    /**
     * @return the animacionCabeza
     */
    int getAnimacionCabeza();

    /**
     * @return the animacionCasco
     */
    int getAnimacionCasco();

    /**
     * @return the animacionCuerpo
     */
    int getAnimacionCuerpo();

    /**
     * @return the animacionEscudo
     */
    int getAnimacionEscudo();

    /**
     * @return the nombre
     */
    String getNombre();

    /**
     * @return the posicion
     */
    Posicion getPosicion();

    /**
     * @param animacionArma the animacionArma to set
     */
    void setAnimacionArma(int animacionArma);

    /**
     * @param animacionCabeza the animacionCabeza to set
     */
    void setAnimacionCabeza(int animacionCabeza);

    /**
     * @param animacionCasco the animacionCasco to set
     */
    void setAnimacionCasco(int animacionCasco);

    /**
     * @param animacionCuerpo the animacionCuerpo to set
     */
    void setAnimacionCuerpo(int animacionCuerpo);

    /**
     * @param animacionEscudo the animacionEscudo to set
     */
    void setAnimacionEscudo(int animacionEscudo);

    /**
     * @param nombre the nombre to set
     */
    void setNombre(String nombre);

    /**
     * @param posicion the posicion to set
     */
    void setPosicion(Posicion posicion);
    
    /**
     * @param x
     * @param y
     */
    void setPosicion(int x, int y);

    /**
     * Get the value of orientacion
     *
     * @return the value of orientacion
     */
    Orientacion getOrientacion();

    /**
     * Set the value of orientacion
     *
     * @param orientacion new value of orientacion
     */
    void setOrientacion(Orientacion orientacion);

}
