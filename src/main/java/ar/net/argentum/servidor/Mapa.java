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

import ar.net.argentum.servidor.mundo.Terreno;
import ar.net.argentum.servidor.mundo.Zona;
import java.util.Collection;
import java.util.Map;

/**
 * Representa un mapa del mundo de Argentum Online
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Mapa {

    public int getVersion();

    /**
     * @return Numero del mapa
     */
    public int getNumero();

    /**
     * @return Nombre del mapa
     */
    public String getNombre();

    /**
     * @return ID de la musica ambiental
     */
    public int getMusica();

    /**
     * @return Verdadero si esta permitido utilizar magia en el mapa
     */
    public boolean magiaTieneEfecto();

    /**
     * @return Tipo de terreno
     */
    public Terreno getTerreno();

    /**
     * @return the zona
     */
    public Zona getZona();

    /**
     * @return the restringir
     */
    public String getRestriccion();

    /**
     * @return Verdadero si el mapa posee alguna restriccion
     */
    public boolean tieneRestriccion();

    /**
     * @return Verdadero si debe realizarse un worldsave de este mapa
     */
    public boolean respaldoActivado();

    /**
     * @return Verdadero si se puede asesinar personajes en el mapa
     */
    public boolean asesinatoPermitido();

    /**
     * Realizar un respaldo del mapa
     *
     * @return Verdadero si el respaldo se ha realizado exitosamente
     */
    public boolean guardarRespaldo();

    /**
     * Cargar respaldo del mapa
     *
     * @return Verdadero si la carga se ha realizado exitosamente
     */
    public boolean cargarRespaldo();

    /**
     * @param x
     * @param y
     * @return Baldosa ubicada en la posicion indicada
     */
    public Baldosa getBaldosa(int x, int y);

    /**
     * @param posicion
     * @return Baldosa ubicada en la posicion indicada
     */
    public Baldosa getBaldosa(Posicion posicion);

    /**
     * @return Coleccion de personajes ubicados en el mapa
     */
    public Collection<Personaje> getPersonajes();

    /**
     * @param x
     * @param y
     * @return Objeto de la posicion dada
     */
    public Objeto getObjeto(int x, int y);

    /**
     * @param pos
     * @return Objeto de la posicion dada
     */
    public Objeto getObjeto(Posicion pos);

    /**
     * Establece un objeto en la posicion dada y envia la informacion a los
     * clientes
     *
     * @param x
     * @param y
     * @param obj
     */
    public void setObjeto(int x, int y, Objeto obj);

    /**
     * Establece un objeto en la posicion dada y envia la informacion a los
     * clientes
     *
     * @param pos
     * @param obj
     */
    public void setObjeto(Posicion pos, Objeto obj);

    /**
     * Eliminar el objeto situado en la posicion dada
     *
     * @param x
     * @param y
     */
    public void quitarObjeto(int x, int y);

    /**
     * Eliminar el objeto situado en la posicion dada
     *
     * @param pos
     */
    public void quitarObjeto(Posicion pos);

    /**
     * @param x
     * @param y
     * @return Verdadero si hay un objeto en la posicion indicada
     */
    public boolean hayObjeto(int x, int y);

    /**
     * @param pos
     * @return Verdadero si hay un objeto en la posicion indicada
     */
    public boolean hayObjeto(Posicion pos);

    /**
     * Obtiene un hashmap con los objetos en el mapa
     *
     * @return
     */
    public Map<Posicion, Objeto> getObjetos();

    /**
     * Bloquea o desbloquea una posicion y le avisa a los clientes
     * @param x
     * @param y
     * @param b 
     */
    public void setBloqueado(int x, int y, boolean b);

    /**
     *  Bloquea o desbloquea una posicion y le avisa a los clientes
     * @param pos
     * @param b 
     */
    public void setBloqueado(Posicion pos, boolean b);
}
