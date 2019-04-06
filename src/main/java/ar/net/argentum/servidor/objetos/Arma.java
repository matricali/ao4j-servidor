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
 * Metadata de un arma que posee daño
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Arma extends Equipable {

    protected int minDaño = 0;
    protected int maxDaño;
    protected boolean apuñala = false;

    public Arma(int id, JSONObject data) {
        super(id, data);
        this.tipo = ObjetoTipo.ARMA;
        if (data.has("MinHit")) {
            this.minDaño = Integer.valueOf(data.getString("MinHit"));
            this.maxDaño = data.has("MaxHit") ? Integer.valueOf(data.getString("MaxHit")) : minDaño;
        }

        if (data.has("Apuñala")) {
            this.apuñala = data.getString("Apuñala").equals("1");
        }
    }

    public Arma(Arma original) {
        super(original);
        this.minDaño = original.getMinDaño();
        this.maxDaño = original.getMaxDaño();
    }

    /**
     * @return Daño mínimo provocado por el arma
     */
    public int getMinDaño() {
        return minDaño;
    }

    /**
     * @param minDaño Daño mínimo provocado por el arma
     */
    public void setMinDaño(int minDaño) {
        this.minDaño = minDaño;
    }

    /**
     * @return Daño máximo provocado por el arma
     */
    public int getMaxDaño() {
        return maxDaño;
    }

    /**
     * @param maxDaño Daño máximo provocado por el arma
     */
    public void setMaxDaño(int maxDaño) {
        this.maxDaño = maxDaño;
    }

    /**
     * @return Verdadero si el arma puede apuñalar
     */
    public boolean isApuñala() {
        return apuñala;
    }

    /**
     * @param apuñala Verdadero si el arma puede apuñalar
     */
    public void setApuñala(boolean apuñala) {
        this.apuñala = apuñala;
    }
}
