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
 * Modificadores de Raza
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ModificadoresRaza {

    protected int fuerza;
    protected int agilidad;
    protected int inteligencia;
    protected int carisma;
    protected int constitucion;

    /**
     * @return Modificador de fuerza
     */
    public int getFuerza() {
        return fuerza;
    }

    /**
     * @param fuerza Modificador de fuerza
     */
    public void setFuerza(int fuerza) {
        this.fuerza = fuerza;
    }

    /**
     * @return Modificador de agilidad
     */
    public int getAgilidad() {
        return agilidad;
    }

    /**
     * @param agilidad Modificador de agilidad
     */
    public void setAgilidad(int agilidad) {
        this.agilidad = agilidad;
    }

    /**
     * @return Modificador de inteligencia
     */
    public int getInteligencia() {
        return inteligencia;
    }

    /**
     * @param inteligencia Modificador de inteligencia
     */
    public void setInteligencia(int inteligencia) {
        this.inteligencia = inteligencia;
    }

    /**
     * @return Modificador de carisma
     */
    public int getCarisma() {
        return carisma;
    }

    /**
     * @param carisma Modificador de carisma
     */
    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    /**
     * @return Modificador de constitucion
     */
    public int getConstitucion() {
        return constitucion;
    }

    /**
     * @param constitucion Modificador de constitucion
     */
    public void setConstitucion(int constitucion) {
        this.constitucion = constitucion;
    }
}
