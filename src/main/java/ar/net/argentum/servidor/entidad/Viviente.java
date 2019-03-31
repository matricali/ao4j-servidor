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
package ar.net.argentum.servidor.entidad;

import ar.net.argentum.servidor.MinMax;

/**
 * Entidad que tiene vida y puede matar
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Viviente {

    /**
     * @return Vida de la entidad
     */
    public MinMax getVida();

    /**
     * @return Devuelve verdadero si la entidad esta muerta.
     */
    public boolean isMuerto();

    /**
     * Matar a la entidad
     */
    public void matar();
}
