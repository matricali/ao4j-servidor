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

import ar.net.argentum.servidor.habilidades.Apuñalar;
import ar.net.argentum.servidor.habilidades.CombateConArmas;
import ar.net.argentum.servidor.habilidades.DefensaConEscudos;
import ar.net.argentum.servidor.habilidades.Meditar;
import ar.net.argentum.servidor.habilidades.TacticasDeCombate;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public final class Habilidades {

    public static Habilidad crear(final Soportadas supp) throws InstantiationException, IllegalAccessException {
        if (supp == null) {
            return null;
        }
        return (Habilidad) supp.getClase().newInstance();
    }

    private Habilidades() {
        // Evitamos la instanciacion 
    }

    /**
     * Habilidades soportadas nativamente.
     *
     * @TODO en un futuro se tendrian que poder cargar en tiempo de ejecucion
     * nuevas clases que implementen la interfaz Habilidad
     */
    public static enum Soportadas {
        MEDITAR("meditar", Meditar.class),
        DEFENSA_CON_ESCUDOS("DefensaConEscudos", DefensaConEscudos.class),
        TACTICAS_DE_COMBATE("TacticasDeCombate", TacticasDeCombate.class),
        COMBATE_CON_ARMAS("CombateConArmas", CombateConArmas.class),
        APUÑALAR("Apuñalar", Apuñalar.class);

        private final Class<?> clase;
        private final String identificador;

        private Soportadas(final String id, final Class<?> clazz) {
            this.identificador = id;
            this.clase = clazz;
        }

        public Class<?> getClase() {
            return clase;
        }

        public static Soportadas para(final String name) {
            for (final Soportadas s : values()) {
                if (s.identificador.equals(name)) {
                    return s;
                }
            }
            // @TODO: Implementar levantar usando nombre completo de la clase
            return null; // Hay que devolver algo :D
        }
    }
}
