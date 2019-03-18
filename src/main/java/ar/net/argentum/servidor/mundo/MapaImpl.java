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
package ar.net.argentum.servidor.mundo;

import ar.net.argentum.servidor.Baldosa;
import ar.net.argentum.servidor.Mapa;
import ar.net.argentum.servidor.Posicion;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Representa un mapa
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class MapaImpl implements Mapa {

    protected int numero;
    protected String nombre;
    protected Baldosa[][] baldosas;
    protected Collection<Personaje> personajes;

    /**
     * Crea una nueva instancia
     *
     * @param numeroMapa
     * @param x Tamaño horizontal
     * @param y Tamaño vertical
     */
    public MapaImpl(int numeroMapa, int x, int y) {
        this.numero = numeroMapa;
        this.baldosas = new Baldosa[x + 1][y + 1];
        this.personajes = new LinkedList<>();
    }

    @Override
    public Baldosa getBaldosa(int x, int y) {
        return baldosas[x][y];
    }

    @Override
    public Baldosa getBaldosa(Posicion posicion) {
        return baldosas[posicion.getX()][posicion.getY()];
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public void setBaldosa(int x, int y, Baldosa baldosa) {
        baldosas[x][y] = baldosa;
    }

    @Override
    public void setBaldosa(Posicion posicion, Baldosa baldosa) {
        setBaldosa(posicion.getX(), posicion.getY(), baldosa);
    }

    @Override
    public Collection<Personaje> getPersonajes() {
        return personajes;
    }

}
