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
 * Metadata de un arma que posee daño
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class Armadura extends Equipable {

    protected int minDefensa = 0;
    protected int maxDefensa = 0;

    public Armadura(JSONObject data) {
        super(data);
        if (data.has("MinDef") && data.has("MaxDef")) {
            this.minDefensa = Integer.valueOf(data.getString("MinDef"));
            this.maxDefensa = Integer.valueOf(data.getString("MaxDef"));
            if (minDefensa > maxDefensa) {
                this.minDefensa = maxDefensa;
            }
        }
    }

    public Armadura(Armadura original) {
        super(original);
        this.minDefensa = original.getMinDefensa();
        this.maxDefensa = original.getMaxDefensa();
    }

    /**
     * @return Defensa mínima
     */
    public int getMinDefensa() {
        return minDefensa;
    }

    /**
     * @param minDefensa Defensa mínima
     */
    public void setMinDefensa(int minDefensa) {
        this.minDefensa = minDefensa;
    }

    /**
     * @return Defensa máxima
     */
    public int getMaxDefensa() {
        return maxDefensa;
    }

    /**
     * @param maxDefensa Defensa máxima
     */
    public void setMaxDefensa(int maxDefensa) {
        this.maxDefensa = maxDefensa;
    }
}
