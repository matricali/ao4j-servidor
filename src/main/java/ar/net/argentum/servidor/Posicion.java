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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa una posicion en eje XY
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Posicion {

    @JsonProperty
    protected int x;
    @JsonProperty
    protected int y;

    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Posicion(Posicion original) {
        this.x = original.getX();
        this.y = original.getY();
    }

    public Posicion() {
        this(0, 0);
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    public void agregarX(int x) {
        this.x += x;
    }

    public void agregarY(int y) {
        this.y += y;
    }
}
