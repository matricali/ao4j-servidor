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
 * Representa un par de enteros que representan un minimo y un maximo.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class MinMax {

    protected int min;
    protected int max;

    public MinMax() {

    }

    public MinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @return true si lla barra esta completa
     */
    public boolean estaCompleto() {
        return min >= max;
    }

    /**
     * Aumentar la barra, si llega al maximo devuelve verdadero
     *
     * @param cantidad
     * @return
     */
    public boolean aumentar(int cantidad) {
        this.min += cantidad;
        if (min >= max) {
            this.min = max;
            return true;
        }
        return false;
    }

    /**
     * Disminuir la barra, si llega al minimo devuelve verdadero
     *
     * @param cantidad
     * @return
     */
    public boolean disminuir(int cantidad) {
        this.min -= cantidad;
        if (min <= 0) {
            this.min = 0;
            return true;
        }
        return false;
    }
}
