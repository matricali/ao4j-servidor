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

/**
 * Representa una entidad que ataca Atacables
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Atacante {

    /**
     * Recibe la notificacion que ha matado a alguien
     *
     * @param victima
     */
    public void alMatar(Atacable victima);

    /**
     * @return Nombre para mostrar del atacante
     */
    public String getNombre();
}
