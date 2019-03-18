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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Carga en memoria una copia de todos los objetos desde "objetos.dat"
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ObjetosDB {

    protected static ObjetoMetadata objetos[];
    
    public static ObjetoMetadata obtener(int id) {
        return objetos[id];
    }

    public ObjetosDB(String archivo) {
        System.out.println("Cargando objetos (" + archivo + ")...");
        try {
            File f = new File(archivo);
            InputStream is = new FileInputStream(f);

            JSONTokener tokener = new JSONTokener(is);
            JSONObject json = new JSONObject(tokener);

            JSONObject init = json.getJSONObject("INIT");

            int cantObjetos = init.getInt("NumOBJs");
            System.out.println("hay " + cantObjetos + " para cargar");

            this.objetos = new ObjetoMetadata[cantObjetos + 1];

            String nombre;
            int grhIndex;

            // Leer 1 por 1
            for (int i = 1; i < cantObjetos; ++i) {
                try {
                    JSONObject jo = json.getJSONObject("OBJ" + i);
                    if (jo != null) {
                        nombre = jo.getString("Name");
                        grhIndex = jo.getInt("GrhIndex");
                        ObjetoMetadata nobjeto = new ObjetoMetadata(i, nombre, ObjetoTipo.otUseOnce, grhIndex, 0, 10000);
                        objetos[i] = nobjeto;
                        System.out.println("OBJ" + i + " - " + nombre);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ObjetosDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ObjetosDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
