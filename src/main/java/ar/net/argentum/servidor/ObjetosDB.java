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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Carga en memoria una copia de todos los objetos desde "objetos.dat"
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ObjetosDB {

    private static final Logger LOGGER = Logger.getLogger(ObjetosDB.class);
    protected static ObjetoMetadata objetos[];

    public static ObjetoMetadata obtener(int id) {
        return objetos[id];
    }

    public ObjetosDB(String archivo) {
        LOGGER.info("Cargando objetos (" + archivo + ")...");
        try {
            File f = new File(archivo);
            InputStream is = new FileInputStream(f);

            JSONTokener tokener = new JSONTokener(is);
            JSONObject json = new JSONObject(tokener);

            JSONObject init = json.getJSONObject("INIT");

            int cantObjetos = init.getInt("NumOBJs");
            LOGGER.info("hay " + cantObjetos + " para cargar");

            this.objetos = new ObjetoMetadata[cantObjetos + 1];

            String nombre;
            int grhIndex;
            int tipoObjeto;
            int animacion;

            // Leer 1 por 1
            for (int i = 1; i < cantObjetos; ++i) {
                try {
                    JSONObject jo = json.getJSONObject("OBJ" + i);
                    if (jo != null) {
                        nombre = jo.getString("Name");
                        grhIndex = jo.getInt("GrhIndex");
                        tipoObjeto = Integer.valueOf(jo.getString("ObjType"));
                        ObjetoMetadata nobjeto = new ObjetoMetadata(i, nombre, ObjetoTipo.valueOf(tipoObjeto), grhIndex, 0, 10000);
                        if (jo.has("Anim")) {
                            nobjeto.setAnimacion(Integer.valueOf(jo.getString("Anim")));
                        }
                        if (jo.has("NumRopaje")) {
                            nobjeto.setRopaje(Integer.valueOf(jo.getString("NumRopaje")));
                        }
                        if (jo.has("Newbie")) {
                            nobjeto.setNewbie(jo.getString("Newbie").equals("1"));
                        }
                        objetos[i] = nobjeto;
                        LOGGER.info("OBJ" + i + " - " + nombre);
                    }
                } catch (Exception ex) {
                    LOGGER.fatal(null, ex);
                }
            }

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }
}
