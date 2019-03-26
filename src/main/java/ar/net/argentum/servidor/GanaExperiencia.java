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
 * Define algo que tiene experiencia y puede ganar experiencia
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface GanaExperiencia {

    /**
     * @return Puntos de experiencia actuales
     */
    public int getExperienciaActual();

    /**
     * @return Puntos de experiencia necesarios para alcanzar el siguiente nivel
     */
    public int getExperienciaSiguienteNivel();

    /**
     * @return Nivel de experiencia actual
     */
    public int getNivel();

    /**
     * Aumentar la experiencia la cantidad de puntos dados, si la experiencia
     * actual sobrepasa la cantidad de puntos necesarios para alcanzar el
     * siguiente nivel entonces aumentamos este ultimo, reiniciamos la
     * experiencia actual y calculamos la experiencia la nueva experiencia
     * necesaria para subir de nivel
     *
     * @param exp Puntos de experiencia a incrementar
     */
    public void ganarExperiencia(int exp);

}
