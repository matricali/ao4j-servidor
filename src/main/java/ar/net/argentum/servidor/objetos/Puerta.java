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
package ar.net.argentum.servidor.objetos;

import ar.net.argentum.servidor.ObjetoTipo;
import org.json.JSONObject;

/**
 * Metadata de un objeto
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Puerta extends MetadataAbstracta {

    /**
     * Por defecto las puertas no deberian poder agarrarse
     */
    protected final int puertaCerrada;
    protected final int puertaAbierta;
    protected boolean cerrada;
    protected final int llave;

    public Puerta(int id, JSONObject data) {
        super(id, data);
        this.tipo = ObjetoTipo.PUERTA;
        this.agarrable = false; // Las puertas no deberian porder agarrarse
        if (data.has("IndexAbierta")) {
            this.puertaAbierta = Integer.valueOf(data.getString("IndexAbierta"));
        } else {
            this.puertaAbierta = id;
        }
        if (data.has("IndexCerrada")) {
            this.puertaCerrada = Integer.valueOf(data.getString("IndexCerrada"));
        } else {
            this.puertaCerrada = id;
        }
        this.cerrada = data.getString("Abierta").equals("1");
        this.llave = Integer.valueOf(data.getString("Llave"));
    }

    public Puerta(int id, String nombre, int grhIndex, int puertaCerrada, int puertaAbierta, boolean cerrada, int llave) {
        super(id, nombre, ObjetoTipo.PUERTA, grhIndex, 0, 1);
        this.puertaAbierta = puertaAbierta;
        this.puertaCerrada = puertaCerrada;
        this.cerrada = cerrada;
        this.llave = llave;
    }

    public Puerta(Puerta original) {
        super(original);
        this.puertaAbierta = original.getPuertaAbierta();
        this.puertaCerrada = original.getPuertaCerrada();
        this.cerrada = original.isCerrada();
        this.llave = original.getLlave();
    }

    /**
     * @return the puertaCerrada
     */
    public int getPuertaCerrada() {
        return puertaCerrada;
    }

    /**
     * @return the puertaAbierta
     */
    public int getPuertaAbierta() {
        return puertaAbierta;
    }

    /**
     * @return the cerrada
     */
    public boolean isCerrada() {
        return cerrada;
    }

    /**
     * @return the llave
     */
    public int getLlave() {
        return llave;
    }
}
