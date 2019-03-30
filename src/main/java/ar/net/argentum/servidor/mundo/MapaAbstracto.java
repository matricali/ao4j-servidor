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
import ar.net.argentum.servidor.Objeto;
import ar.net.argentum.servidor.Posicion;
import ar.net.argentum.servidor.Servidor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Representa un mapa
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class MapaAbstracto implements Mapa {
    
    protected int numero;
    protected int version;
    protected String nombre;
    protected int musica;
    protected boolean magiaSinEfecto;
    protected boolean noEncriptarMP;
    protected Terreno terreno;
    protected Zona zona;
    protected String restringir;
    protected boolean respaldar;
    protected boolean asesinato;
    
    protected Baldosa[][] baldosas;
    protected Collection<Personaje> personajes;

    /**
     * Crea una nueva instancia
     *
     * @param numeroMapa
     * @param x Tamaño horizontal
     * @param y Tamaño vertical
     */
    public MapaAbstracto(int numeroMapa, int x, int y) {
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
    public Collection<Personaje> getPersonajes() {
        return personajes;
    }
    
    @Override
    public int getMusica() {
        return musica;
    }
    
    @Override
    public boolean magiaTieneEfecto() {
        return !magiaSinEfecto;
    }
    
    @Override
    public Terreno getTerreno() {
        return terreno;
    }
    
    @Override
    public Zona getZona() {
        return zona;
    }
    
    @Override
    public String getRestriccion() {
        return restringir;
    }
    
    @Override
    public boolean tieneRestriccion() {
        return !restringir.equalsIgnoreCase("No");
    }
    
    @Override
    public boolean respaldoActivado() {
        return respaldar;
    }
    
    @Override
    public boolean asesinatoPermitido() {
        return asesinato;
    }
    
    @Override
    public int getVersion() {
        return version;
    }
    
    @Override
    public Objeto getObjeto(int x, int y) {
        return getBaldosa(x, y).getObjeto();
    }
    
    @Override
    public Objeto getObjeto(Posicion pos) {
        return getBaldosa(pos).getObjeto();
    }
    
    @Override
    public void setObjeto(int x, int y, Objeto obj) {
        getBaldosa(x, y).setObjeto(obj);
        Servidor.getServidor().todosMapa(numero, (usuario, conexion) -> {
            conexion.enviarMundoObjeto(x, y, obj);
        });
    }
    
    @Override
    public void setObjeto(Posicion pos, Objeto obj) {
        setObjeto(pos.getX(), pos.getY(), obj);
    }
    
    @Override
    public void quitarObjeto(int x, int y) {
        getBaldosa(x, y).setObjeto(null);
        Servidor.getServidor().todosMapa(numero, (usuario, conexion) -> {
            conexion.enviarMundoObjeto(x, y, null);
        });
    }
    
    @Override
    public void quitarObjeto(Posicion pos) {
        quitarObjeto(pos.getX(), pos.getY());
    }
    
    @Override
    public boolean hayObjeto(int x, int y) {
        return getBaldosa(x, y).hayObjeto();
    }
    
    @Override
    public boolean hayObjeto(Posicion pos) {
        return hayObjeto(pos.getX(), pos.getY());
    }
}
