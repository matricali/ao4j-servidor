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
package ar.net.argentum.servidor.habilidades;

import ar.net.argentum.servidor.HabilidadBase;
import ar.net.argentum.servidor.Habilidoso;
import ar.net.argentum.servidor.Logica;
import ar.net.argentum.servidor.entidad.Atacable;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Meditar extends HabilidadBase {

    @Override
    public String getNombre() {
        return "Meditar";
    }

    @Override
    public String getDescripcion() {
        return "";
    }

    @Override
    public boolean realizar(Habilidoso objetivo) {
        int dificultad;

        if ((nivel <= 10)) {
            dificultad = 35;
        } else if ((nivel <= 20)) {
            dificultad = 30;
        } else if ((nivel <= 30)) {
            dificultad = 28;
        } else if ((nivel <= 40)) {
            dificultad = 24;
        } else if ((nivel <= 50)) {
            dificultad = 22;
        } else if ((nivel <= 60)) {
            dificultad = 20;
        } else if ((nivel <= 70)) {
            dificultad = 18;
        } else if ((nivel <= 80)) {
            dificultad = 15;
        } else if ((nivel <= 90)) {
            dificultad = 10;
        } else if ((nivel < 100)) {
            dificultad = 7;
        } else {
            dificultad = 5;
        }

        return Logica.verdaderoAleatorio(dificultad);
    }

    public static int getEfecto(int nivel) {
        if (nivel < 15) {
            return 4;
        } else if (nivel < 25) {
            return 5;
        } else if (nivel < 35) {
            return 6;
        } else if (nivel < 45) {
            return 16;
        }
        return 34;
    }

    @Override
    public boolean realizar(Habilidoso origen, Atacable victima) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
