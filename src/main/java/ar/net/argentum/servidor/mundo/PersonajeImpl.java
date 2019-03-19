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
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class PersonajeImpl implements Personaje {

    protected String nombre;
    protected Posicion posicion;
    protected int animacionCuerpo;
    protected int animacionCabeza;
    protected int animacionCasco;
    protected int animacionEscudo;
    protected int animacionArma;
    protected Orientacion orientacion;

    /**
     * Get the value of orientacion
     *
     * @return the value of orientacion
     */
    @Override
    public Orientacion getOrientacion() {
        return orientacion;
    }

    /**
     * Set the value of orientacion
     *
     * @param orientacion new value of orientacion
     */
    @Override
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    /**
     * @return the nombre
     */
    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the posicion
     */
    @Override
    public Posicion getPosicion() {
        return posicion;
    }

    /**
     * Establecer posicion
     *
     * @param posicion the posicion to set
     */
    @Override
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    /**
     * Establecer posicion
     *
     * @param x
     * @param y
     */
    @Override
    public void setPosicion(int x, int y) {
        this.posicion.setX(x);
        this.posicion.setY(y);
    }

    /**
     * @return the animacionCuerpo
     */
    @Override
    public int getAnimacionCuerpo() {
        return animacionCuerpo;
    }

    /**
     * @param animacionCuerpo the animacionCuerpo to set
     */
    @Override
    public void setAnimacionCuerpo(int animacionCuerpo) {
        this.animacionCuerpo = animacionCuerpo;
    }

    /**
     * @return the animacionCabeza
     */
    @Override
    public int getAnimacionCabeza() {
        return animacionCabeza;
    }

    /**
     * @param animacionCabeza the animacionCabeza to set
     */
    @Override
    public void setAnimacionCabeza(int animacionCabeza) {
        this.animacionCabeza = animacionCabeza;
    }

    /**
     * @return the animacionCasco
     */
    @Override
    public int getAnimacionCasco() {
        return animacionCasco;
    }

    /**
     * @param animacionCasco the animacionCasco to set
     */
    @Override
    public void setAnimacionCasco(int animacionCasco) {
        this.animacionCasco = animacionCasco;
    }

    /**
     * @return the animacionEscudo
     */
    @Override
    public int getAnimacionEscudo() {
        return animacionEscudo;
    }

    /**
     * @param animacionEscudo the animacionEscudo to set
     */
    @Override
    public void setAnimacionEscudo(int animacionEscudo) {
        this.animacionEscudo = animacionEscudo;
    }

    /**
     * @return the animacionArma
     */
    @Override
    public int getAnimacionArma() {
        return animacionArma;
    }

    /**
     * @param animacionArma the animacionArma to set
     */
    @Override
    public void setAnimacionArma(int animacionArma) {
        this.animacionArma = animacionArma;
    }

}
