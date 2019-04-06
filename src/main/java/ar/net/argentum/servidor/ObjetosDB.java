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

import ar.net.argentum.servidor.objetos.Puerta;
import ar.net.argentum.servidor.objetos.Equipable;
import ar.net.argentum.servidor.objetos.ObjetoMetadataBasica;
import ar.net.argentum.servidor.objetos.Arma;
import ar.net.argentum.servidor.objetos.Cartel;
import ar.net.argentum.servidor.objetos.Casco;
import ar.net.argentum.servidor.objetos.Comestible;
import ar.net.argentum.servidor.objetos.Escudo;
import ar.net.argentum.servidor.objetos.Foro;
import ar.net.argentum.servidor.objetos.Pocion;
import ar.net.argentum.servidor.objetos.Vestimenta;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Carga en memoria una copia de todos los OBJETOS desde "OBJETOS.dat"
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ObjetosDB {

    private static final Logger LOGGER = Logger.getLogger(ObjetosDB.class);
    protected static ObjetoMetadata[] OBJETOS;

    public static ObjetoMetadata obtener(int id) {
        return OBJETOS[id];
    }

    public static ObjetoMetadata obtenerCopia(int id) {
        return obtener(id).copiar();
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

            OBJETOS = new ObjetoMetadata[cantObjetos + 1];

            String nombre;
            int grhIndex;
            int tipo;
            int animacion;
            ObjetoTipo tipoObjeto;
            ObjetoMetadata metadata;

            // Leer 1 por 1
            for (int i = 1; i < cantObjetos; ++i) {
                try {
                    String clave = "OBJ" + i;
                    if (!json.has(clave)) {
                        continue;
                    }
                    JSONObject jo = json.getJSONObject(clave);
                    if (jo != null) {
                        nombre = jo.getString("Name");
                        grhIndex = jo.getInt("GrhIndex");
                        tipo = Integer.valueOf(jo.getString("ObjType"));
                        tipoObjeto = ObjetoTipo.valueOf(tipo);

                        metadata = (ObjetoMetadata) tipoObjeto.getClase()
                                .getDeclaredConstructor(int.class, JSONObject.class)
                                .newInstance(i, jo);

//                        switch (tipoObjeto) {
//                            case PUERTA:
//                                metadata = new Puerta(i, jo);
//                                break;
//                            case ARMA:
//                                metadata = new Arma(i, jo);
//                                break;
//                            case ESCUDO:
//                                metadata = new Escudo(i, jo);
//                                break;
//                            case CASCO:
//                                metadata = new Casco(i, jo);
//                                break;
//                            case VESTIMENTA:
//                                metadata = new Vestimenta(i, jo);
//                                break;
//                            case ALIMENTO:
//                                metadata = new Comestible(i, jo);
//                                break;
//                            case CARTEL:
//                                metadata = new Cartel(i, jo);
//                                break;
//                            case FORO:
//                                metadata = new Foro(i, jo);
//                                break;
//                            case POCION:
//                                metadata = new Pocion(i, jo);
//                                break;
//
//                            default:
//                                metadata = new ObjetoMetadataBasica(i, nombre, ObjetoTipo.valueOf(tipo), grhIndex, 0, 10000);
//                        }
                        OBJETOS[i] = metadata;
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
