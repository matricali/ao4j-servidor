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
 * HabilidadBase
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class HabilidadBase implements Habilidad {

    /**
     * Cantidad de puntos del skill
     */
    protected int nivel = 1;
    /**
     * Nivel maximo posible para esta habilidad
     */
    protected int nivelMax = 100;
    /**
     * Experiencia actual
     */
    protected int experiencia = 0;
    /**
     * Experiencia necesaria para el siguiente nivel
     */
    protected int siguienteNivel = 100;

    @Override
    public int getNivel() {
        return nivel;
    }

    @Override
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    @Override
    public int getExperiencia() {
        return experiencia;
    }

    @Override
    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    @Override
    public int getSiguienteNivel() {
        return siguienteNivel;
    }

    @Override
    public void setSiguienteNivel(int siguienteNivel) {
        this.siguienteNivel = siguienteNivel;
    }

    @Override
    public boolean entrenar() {
        if (nivel >= nivelMax) {
            // Si ya tenemos la habilidad al maximo, entonces no entrenamos nada :D
            return false;
        }
        this.experiencia += 25;
        if (experiencia >= siguienteNivel) {
            ++this.nivel;
            this.experiencia = experiencia - siguienteNivel;
            this.siguienteNivel = (int) (siguienteNivel * 1.5);
            return true;
        }
        return false;
    }
}
