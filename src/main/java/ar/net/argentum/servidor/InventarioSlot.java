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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class InventarioSlot {

    @JsonProperty
    protected int objetoId;
    @JsonProperty
    protected int cantidad;
    @JsonProperty
    protected boolean equipado;

    @JsonIgnore
    public ObjetoMetadata getObjeto() {
        return ObjetosDB.obtener(getObjetoId());
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean isEquipado() {
        return equipado;
    }

    /**
     * @return the objetoId
     */
    public int getObjetoId() {
        return objetoId;
    }

    /**
     * @param objetoId the objetoId to set
     */
    public void setObjetoId(int objetoId) {
        this.objetoId = objetoId;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @param equipado the equipado to set
     */
    public void setEquipado(boolean equipado) {
        this.equipado = equipado;
    }
}
