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
import ar.net.argentum.servidor.Usuario;
import ar.net.argentum.servidor.entidad.Atacable;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Apuñalar extends HabilidadBase {

    @Override
    public String getNombre() {
        return "Apuñalar";
    }

    @Override
    public String getDescripcion() {
        return "Puedes realizar ataques con armas punzantes para seguir entrenando esta habilidad.";
    }

    @Override
    public boolean realizar(Habilidoso origen, Atacable victima) {
        int dificultad = (int) (0.0361 * nivel + 4.39);

        if (origen instanceof Usuario) {
            Usuario usuario = (Usuario) origen;
            switch (usuario.getClase()) {
                case "ASESINO":
                    dificultad = (int) (((0.00003 * nivel - 0.002) * nivel + 0.098) * nivel + 4.25);
                    break;
                case "CLERIGO":
                case "PALADIN":
                case "PIRATA":
                    dificultad = (int) (((0.000003 * nivel - 0.0006) * nivel + 0.0107) * nivel + 4.93);
                    break;
                case "BARDO":
                    dificultad = (int) (((0.000002 * nivel - 0.0002) * nivel + 0.032) * nivel + 4.81);
                    break;
            }
        }

        return (Logica.enteroAleatorio(1, 100) < dificultad);
    }

    @Override
    public boolean realizar(Habilidoso habilidoso) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
