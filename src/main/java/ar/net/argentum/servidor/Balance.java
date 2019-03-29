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
 * balance.dat
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class Balance {

    public static final int PORCENTAJE_RECUPERO_MANA = 6;
    public static final int EXP_SKILL_ACIERTO = 50;
    public static final int EXP_SKILL_FALLO = 20;
    /**
     * Nivel maximo posible para los usuarios
     */
    public static final int NIVEL_MAX = 50;

    public static final int COMBATE_ENERGIA_NECESARIA = 10;
    public static final int FRIO_INTERVALO = 5;
    public static final int FRIO_PORCENTAJE_STAMINA = 5;
    public static final int ENERGIA_INTERVALO = 5;
    public static final int ENERGIA_PORCENTAJE_RECUPERO = 5;

    /**
     * Calcular la nueva experiencia para pasar de nivel.
     *
     * @since Nueva subida de exp x lvl. Pablo (ToxicWaste)
     *
     * @param nuevoNivel
     * @param experienciaActual
     * @return Nueva experiencia para pasar de nivel
     */
    public static int calcularExperienciaParaPasarNivel(int nuevoNivel, int experienciaActual) {
        if (nuevoNivel < 15) {
            return (int) (experienciaActual * 1.4);
        } else if (nuevoNivel < 21) {
            return (int) (experienciaActual * 1.35);
        } else if (nuevoNivel < 26) {
            return (int) (experienciaActual * 1.3);
        } else if (nuevoNivel < 35) {
            return (int) (experienciaActual * 1.2);
        } else if (nuevoNivel < 40) {
            return (int) (experienciaActual * 1.3);
        }
        return (int) (experienciaActual * 1.375);
    }

    /**
     * Calculamos la recompensa en puntos de experiencia al subir de nivel una
     * habilidad
     *
     * @return Puntos de experiencia a ganar
     */
    public static int calcularExperienciaGanadaAlSubirHabilidad(int nivelHabilidad) {
        return 50 * nivelHabilidad;
    }
}
