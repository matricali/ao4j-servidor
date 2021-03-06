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
public class ObjetoGenerico extends MetadataAbstracta {

    public ObjetoGenerico(int id, String nombre, ObjetoTipo tipo, int grhIndex, int grhSecundario, int maxItems) {
        super(id, nombre, tipo, grhIndex, grhSecundario, maxItems);
    }

    public ObjetoGenerico(ObjetoGenerico original) {
        super(original);
    }

    public ObjetoGenerico(int id, JSONObject data) {
        super(id, data);
    }
}
