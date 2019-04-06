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

    public static final int[] DISTRIBUCION_VIDA_E = {10, 20, 40, 20, 10};
    public static final int[] DISTRIBUCION_VIDA_S = {10, 40, 40, 10};

    public static final int COMBATE_ENERGIA_NECESARIA = 10;

    public static final int FRIO_INTERVALO = 5;
    public static final int FRIO_PORCENTAJE_STAMINA = 5;
    public static final int ENERGIA_INTERVALO = 5;
    public static final int ENERGIA_PORCENTAJE_RECUPERO = 5;
    public static final int NEWBIE_NIVEL_MAXIMO = 12;
    public static final int HAMBRE_INVERVALO = 650;
    public static final int HAMBRE_CANTIDAD = 10;
    public static final int SED_INTERVALO = 600;
    public static final int SED_CANTIDAD = 10;

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

    public static int calcularAumentoVida(String clase, int constitucion) {
        Clase c = Servidor.getServidor().getClase(clase);

        // Aumentamos la vida
        double promedio = c.getModificadores().getVida() - (21 - constitucion) * 0.5;
        int aux = Logica.enteroAleatorio(1, 100);
        int[] distVida = new int[5];

        if (promedio - Math.floor(promedio) == 0.5) {
            // Es promedio semientero
            distVida[0] = DISTRIBUCION_VIDA_S[0];
            distVida[1] = distVida[0] + DISTRIBUCION_VIDA_S[1];
            distVida[2] = distVida[1] + DISTRIBUCION_VIDA_S[2];
            distVida[3] = distVida[2] + DISTRIBUCION_VIDA_S[3];

            if (aux <= distVida[0]) {
                return (int) (promedio + 1.5);
            }
            if (aux <= distVida[1]) {
                return (int) (promedio + 0.5);
            }
            if (aux <= distVida[2]) {
                return (int) (promedio - 0.5);
            }
            return (int) (promedio - 1.5);
        }

        // Promedio entero
        distVida[0] = DISTRIBUCION_VIDA_E[0];
        distVida[1] = distVida[0] + DISTRIBUCION_VIDA_E[1];
        distVida[2] = distVida[1] + DISTRIBUCION_VIDA_E[2];
        distVida[3] = distVida[2] + DISTRIBUCION_VIDA_E[3];
        distVida[4] = distVida[3] + DISTRIBUCION_VIDA_E[4];

        if (aux <= distVida[0]) {
            return (int) (promedio + 2);
        }
        if (aux <= distVida[1]) {
            return (int) (promedio + 1);
        }
        if (aux <= distVida[2]) {
            return (int) (promedio);
        }
        if (aux <= distVida[3]) {
            return (int) (promedio - 1);
        }
        return (int) (promedio - 2);
    }

    public static int calcularAumentoEnergia(String clase) {
        int energia = 15;
        switch (clase) {
            case "LADRON":
            case "BANDIDO":
                return energia + 3;
            case "MAGO":
                return energia - 1;
            case "TRABAJADOR":
                return energia + 25;
            default:
                return energia;
        }
    }

    public static int calcularAumentoMana(String clase, int inteligencia) {
        switch (clase) {
            case "PALADIN":
            case "ASESINO":
                return inteligencia;
            case "MAGO":
                return (int) (inteligencia * 2.8);
            case "CLERIGO":
            case "DRUIDA":
            case "BARDO":
                return inteligencia * 2;
            case "BANDIDO":
                return inteligencia / 2 * 3;
            default:
                return 0;
        }
    }

    public static int calcularAumentoGolpe(String clase, int nivel) {
        switch (clase) {
            case "GUERRERO":
            case "CAZADOR":
                return (nivel > 35) ? 2 : 3;
            case "PIRATA":
                return 3;
            case "PALADIN":
            case "ASESINO":
            case "BANDIDO":
                return (nivel > 35) ? 1 : 3;
            case "MAGO":
                return 1;
            default:
                return 2;
        }
    }
}
