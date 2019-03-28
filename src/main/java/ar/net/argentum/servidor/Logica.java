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

import ar.net.argentum.servidor.mundo.Orientacion;
import java.util.Random;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Logica {

    public static final int MinXBorder = 5;
    public static final int MaxXBorder = 95;
    public static final int MinYBorder = 5;
    public static final int MaxYBorder = 95;
    protected static final Random RANDOM = new Random();

    /**
     * Calcula la nueva posicion al dar un paso en una direccion especifica
     * desde una posicion dada
     *
     * @see HeadtoPos
     *
     * @param posicion
     * @param orientacion
     * @return
     */
    public static Posicion calcularPaso(Posicion posicion, Orientacion orientacion) {
        Posicion nPosicion = new Posicion(posicion);

        switch (orientacion) {
            case NORTE:
                nPosicion.agregarY(-1);
                break;
            case SUR:
                nPosicion.agregarY(1);
                break;
            case ESTE:
                nPosicion.agregarX(1);
                break;
            case OESTE:
                nPosicion.agregarX(-1);
                break;
        }

        return nPosicion;
    }

    public static boolean isPosicionValida(int numMapa, int x, int y, boolean puedeAgua, boolean puedeTierra) {

        Mapa mapa = Servidor.getServidor().getMapa(numMapa);

        // Es un mapa valido?
        if (mapa == null) {
            System.out.println("Mapa invalido.");
            return false;
        }

        if (!isDentroDelLimite(numMapa, x, y)) {
            System.out.println("Fuera de los limites del mapa.");
            return false;
        }

        Baldosa baldosa = mapa.getBaldosa(x, y);

        if (baldosa == null) {
            System.out.println("Baldosa invalida.");
            return false;
        }

        // Esta bloqueado?
        if (baldosa.isBloqueado()) {
            System.out.println("Baldosa bloqueada.");
            return false;
        }

        // Hay alguien parado en la nueva posicion?
        if (baldosa.getCharindex() > 0) {
            System.out.println("Ya hay alguien parado en la baldosa.");
            return false;
        }

        // Puede caminar por el agua?
        if (baldosa.isAgua() && !puedeAgua) {
            System.out.println("El usuario no puede ir por el agua.");
            return false;
        }

        // Puede caminar por la tierra?
        if (baldosa.isTierra() && !puedeTierra) {
            System.out.println("El usuario no puede ir por la tierra.");
            return false;
        }

        return true;
    }

    /**
     * Verifica si la posicion esta dentro de los limites validos del mapa
     *
     * @see InMapBounds
     *
     * @param numMapa
     * @param x
     * @param y
     * @return
     */
    public static boolean isDentroDelLimite(int numMapa, int x, int y) {
        return !(x < MinXBorder || x > MaxXBorder || y < MinYBorder || y > MaxYBorder);
    }

    /**
     * Devuelve un valor verdadero aleatorio en base a la dificultad dada
     *
     * @param dificultad
     * @return
     */
    public static boolean verdaderoAleatorio(int dificultad) {
        return enteroAleatorio(1, dificultad) == 1;
    }

    public static int enteroAleatorio(int minimo, int maximo) {
        return RANDOM.nextInt(maximo - minimo) + minimo;
    }

    public static int porcentaje(int total, int porc) {
        return total * porc / 100;
    }

    public static int calcularDistancia(Posicion a, Posicion b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    public static int calcularDistancia(Coordenada c1, Coordenada c2) {
        return calcularDistancia(c1.getPosicion(), c2.getPosicion())
                + (Math.abs(c1.getMapa() - c2.getMapa()) * 100);
    }
}
