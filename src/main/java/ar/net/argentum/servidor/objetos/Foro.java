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

import ar.net.argentum.servidor.ObjetoMetadata;
import org.json.JSONObject;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Foro extends ObjetoMetadataBasica {

    protected String foro;

    public Foro(int id, JSONObject data) {
        super(id, data);
        this.foro = data.getString("ID");
    }

    public Foro(Foro original) {
        super(original);
        this.foro = original.getForo();
    }

    public String getForo() {
        return foro;
    }

    public void setForo(String foro) {
        this.foro = foro;
    }
}
