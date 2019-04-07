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
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Cartel extends MetadataAbstracta {

    protected String texto = "";
    protected int fondo = 0;

    public Cartel(int id, JSONObject data) {
        super(id, data);
        if (data.has(texto)) {
            this.texto = data.getString("Texto");
        }
        if (data.has("VGrande")) {
            this.fondo = Integer.valueOf(data.getString("VGrande"));
        }
    }

    public Cartel(Cartel original) {
        super(original);
        this.texto = original.getTexto();
        this.fondo = original.getFondo();
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getFondo() {
        return fondo;
    }

    public void setFondo(int fondo) {
        this.fondo = fondo;
    }
}
