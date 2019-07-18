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
 * Representa una posicion en el mundo de Argentum Online
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Coordenada {

    @JsonProperty
    protected int mapa;
    @JsonProperty
    protected Posicion posicion;

    public Coordenada() {

    }

    public Coordenada(int mapa, Posicion posicion) {
        this.mapa = mapa;
        this.posicion = posicion;
    }

    public Coordenada(Coordenada original) {
        this.mapa = original.mapa;
        this.posicion = new Posicion(original.getPosicion());
    }

    /**
     * @return the mapa
     */
    public int getMapa() {
        return mapa;
    }

    /**
     * @param mapa the mapa to set
     */
    public void setMapa(int mapa) {
        this.mapa = mapa;
    }

    /**
     * @return the posicion
     */
    public Posicion getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public void setPosicion(int x, int y) {
        setPosicion(new Posicion(x, y));
    }
    
    @Override
    public String toString() {
        return String.format("%d-%d-%d", mapa, posicion.getX(), posicion.getY());
    }
} 
