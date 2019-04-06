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

import org.json.JSONObject;

/**
 * Pociones
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Pocion extends ObjetoMetadataBasica {

    protected int minModificador = 0;
    protected int maxModificador = 0;
    protected Tipo tipoPocion;
    protected int duracionEfecto = 0;

    public Pocion(int id, JSONObject data) {
        super(id, data);
        this.tipoPocion = Tipo.valueOf(Integer.valueOf(data.getString("TipoPocion")));
        if (data.has("MinModificador")) {
            this.minModificador = Integer.valueOf(data.getString("MinModificador"));
        }
        if (data.has("MaxModificador")) {
            this.maxModificador = Integer.valueOf(data.getString("MaxModificador"));
        }
        if (data.has("DuracionEfecto")) {
            this.duracionEfecto = Integer.valueOf(data.getString("DuracionEfecto"));
        }
    }

    public Pocion(Pocion original) {
        super(original);
        this.tipoPocion = original.getTipoPocion();
        this.minModificador = original.getMinModificador();
        this.maxModificador = original.getMaxModificador();
        this.duracionEfecto = original.getDuracionEfecto();
    }

    public int getMinModificador() {
        return minModificador;
    }

    public void setMinModificador(int minModificador) {
        this.minModificador = minModificador;
    }

    public int getMaxModificador() {
        return maxModificador;
    }

    public void setMaxModificador(int maxModificador) {
        this.maxModificador = maxModificador;
    }

    public Tipo getTipoPocion() {
        return tipoPocion;
    }

    public void setTipoPocion(Tipo tipoPocion) {
        this.tipoPocion = tipoPocion;
    }

    public int getDuracionEfecto() {
        return duracionEfecto;
    }

    public void setDuracionEfecto(int duracionEfecto) {
        this.duracionEfecto = duracionEfecto;
    }

    /**
     * Tipos de pociones
     */
    public enum Tipo {
        AUMENTA_AGILIDAD(1),
        AUMENTA_FUERZA(2),
        AUMENTA_VIDA(3),
        AUMENTA_MANA(4),
        CURA_VENENO(5),
        NEGRA(6);

        protected int id;

        Tipo(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Tipo valueOf(int id) throws IllegalArgumentException {
            for (Tipo o : Tipo.values()) {
                if (o.id == id) {
                    return o;
                }
            }
            throw new IllegalArgumentException("Tipo de poción invalido. (" + id + ")");
        }
    }
}
