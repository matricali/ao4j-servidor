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
 * Hacele caso a tu sed!
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Bebida extends MetadataAbstracta {

    protected int minSed;
    protected int maxSed;

    public Bebida(int id, JSONObject data) {
        super(id, data);
        this.minSed = Integer.valueOf(data.getString("MinAgu"));
        this.maxSed = data.has("MaxAgu") ? Integer.valueOf(data.getString("MaxAgu")) : minSed;
    }

    public Bebida(Bebida original) {
        super(original);
        this.minSed = original.getMinSed();
        this.maxSed = original.getMaxSed();
    }

    public int getMinSed() {
        return minSed;
    }

    public void setMinSed(int minSed) {
        this.minSed = minSed;
    }

    public int getMaxSed() {
        return maxSed;
    }

    public void setMaxSed(int maxSed) {
        this.maxSed = maxSed;
    }
}
