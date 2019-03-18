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

import ar.net.argentum.servidor.mundo.Personaje;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Mapa {

    public String getNombre();

    public void setBaldosa(int x, int y, Baldosa baldosa);

    public void setBaldosa(Posicion posicion, Baldosa baldosa);

    public Baldosa getBaldosa(int x, int y);

    public Baldosa getBaldosa(Posicion posicion);

    public int getNumero();

    public Collection<Personaje> getPersonajes();
}
