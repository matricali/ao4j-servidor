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
package ar.net.argentum.servidor.mundo;

import ar.net.argentum.general.UtilLegacy;
import ar.net.argentum.servidor.Baldosa;
import ar.net.argentum.servidor.Objeto;
import ar.net.argentum.servidor.Posicion;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Maneja un mapa en el formato original.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class MapaAntiguo extends MapaAbstracto {

    /**
     * Para poder realizar correctamente la lectura del mapa hay que tener en
     * cuenta dos puntos importantes:
     *
     * 1) que Visual Basic 6 y JAVA poseen diferentes tipos de datos, con sus
     * respectivas longitudes y rangos de valores.
     *
     * En VB6 un tipo de dato Integer ocupa 2 bytes solamente y su rango de
     * valores es de -32,768 a 32.767, lo que en JAVA es equivalente al tipo de
     * datos Short.
     *
     * 2)
     */
    private static final Logger LOGGER = LogManager.getLogger(MapaAntiguo.class);

    private static final byte MAPA_MIN_X = 1;
    private static final byte MAPA_MAX_X = 100;
    private static final byte MAPA_MIN_Y = 1;
    private static final byte MAPA_MAX_Y = 100;

    public MapaAntiguo(int numeroMapa) {
        super(numeroMapa, MAPA_MAX_X, MAPA_MAX_Y);

        LOGGER.info("Iniciando carga del mapa " + numeroMapa + "...");

        /**
         * Cargamos el archivo .map que contiene la informacion sobre las
         * diferentes capas, triggers e informacion de bloqueo
         */
        try (RandomAccessFile f = new RandomAccessFile("datos/mapas/mapa" + numeroMapa + ".map", "r")) {
            f.seek(0);

            short version = UtilLegacy.bigToLittle(f.readShort());
            byte[] cabecera = new byte[263];
            f.read(cabecera);

            f.readDouble();

            byte flags;
            byte bloq;

            for (int y = MAPA_MIN_Y; y <= MAPA_MAX_Y; ++y) {
                for (int x = MAPA_MIN_X; x <= MAPA_MAX_X; ++x) {
                    try {
                        Baldosa md = new BaldosaImpl(this);

                        flags = UtilLegacy.bigToLittle(f.readByte());

                        // Baldosa bloqueada?
                        md.setBloqueado((flags & 1) == 1);

                        // Grafico de la capa 1
                        md.setGrafico(1, UtilLegacy.bigToLittle(f.readShort()));

                        // Grafico de la capa 2
                        if ((flags & 2) == 2) {
                            md.setGrafico(2, UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setGrafico(2, 0);
                        }

                        // Grafico de la capa 3
                        if ((flags & 4) == 4) {
                            md.setGrafico(3, UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setGrafico(3, 0);
                        }

                        // Grafico de la capa 4
                        if ((flags & 8) == 8) {
                            md.setGrafico(4, UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setGrafico(4, 0);
                        }

                        if ((flags & 16) == 16) {
                            md.setTrigger(UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setTrigger((short) 0);
                        }

                        if (md.getCharindex() > 0) {
                            //EraseChar;
                        }

                        baldosas[x][y] = md;
                    } catch (IOException ex) {
                        LOGGER.fatal(ex);
                    }
                }
            }

            /**
             * Cargamos el archivo .dat que contiene informacion del mapa
             */
            {
                String archivoDat = "datos/mapas/mapa" + numeroMapa + ".dat";
                Properties prop = new Properties();
                try (FileInputStream fis = new FileInputStream(archivoDat)) {
                    prop.load(fis);
                } catch (FileNotFoundException ex) {
                    LOGGER.fatal("No se encontro el archivo con la informacion del mapa Nº" + numeroMapa, ex);
                } catch (IOException ex) {
                    LOGGER.fatal("Ocurrio un error al leer  " + archivoDat, ex);
                }
                this.nombre = prop.getProperty("Name", "");
                this.musica = Integer.valueOf(prop.getProperty("MusicNum", "0"));
                this.magiaSinEfecto = prop.getProperty("MagiaSinefecto", "0").equals("0");
                this.noEncriptarMP = prop.getProperty("NoEncriptarMP", "0").equals("1");
                this.terreno = Terreno.valueOf(prop.getProperty("Terreno", "BOSQUE"));
                this.zona = Zona.valueOf(prop.getProperty("Zona", "CAMPO"));
                this.restringir = prop.getProperty("Restringir", "No");
                this.respaldar = prop.getProperty("BackUp", "0").equals("1");
                this.asesinato = prop.getProperty("Pk", "0").equals("0");
            }

            /**
             * Cargamos el archivo .inf (Worldsave)
             */
            cargarRespaldo();

            LOGGER.info("Carga de mapa finalizada!");
        } catch (IOException ex) {
            LOGGER.fatal("Ocurrio un error al cargar el mapa Nº" + numeroMapa, ex);
        }
    }

    @Override
    public boolean guardarRespaldo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cargarRespaldo() {
        LOGGER.info("Cargando worldsave del mapa Nº " + numero + "...");

        try (RandomAccessFile f = new RandomAccessFile("datos/mapas/mapa" + numero + ".inf", "r")) {
            f.seek(0);

            // Cabecera del archivo
            f.readDouble();
            f.readShort();

            byte flags;
            for (int y = MAPA_MIN_Y; y <= MAPA_MAX_Y; ++y) {
                for (int x = MAPA_MIN_X; x <= MAPA_MAX_X; ++x) {
                    try {
                        Baldosa md = baldosas[x][y];

                        flags = UtilLegacy.bigToLittle(f.readByte());

                        if ((flags & 1) == 1) {
                            // Teletransporte
                            int tileExitMap = (int) UtilLegacy.bigToLittle(f.readShort());
                            int tileExitX = (int) UtilLegacy.bigToLittle(f.readShort());
                            int tileExitY = (int) UtilLegacy.bigToLittle(f.readShort());
                        }

                        if ((flags & 2) == 2) {
                            // Hay un NPC ?
                            short npcIndex = UtilLegacy.bigToLittle(f.readShort());
                            if (npcIndex > 0) {
                                // @TODO: Cargar NPC :D
                            }
                        }

                        if ((flags & 4) == 4) {
                            // Hay un objeto en el suelo
                            short objIndex = UtilLegacy.bigToLittle(f.readShort());
                            short objCantidad = UtilLegacy.bigToLittle(f.readShort());
                            md.setObjeto(new Objeto(objIndex, objCantidad));
                        }
                    } catch (IOException ex) {
                        LOGGER.fatal(ex);
                    }
                }
            }
            LOGGER.info("Carga de mapa finalizada!");
            return true;
        } catch (IOException ex) {
            LOGGER.fatal(ex);
        }
        return false;
    }

    public Map<Posicion, Objeto> getObjetos() {
        Map<Posicion, Objeto> objetos = new HashMap<>();

        for (int y = MAPA_MIN_Y; y <= MAPA_MAX_Y; ++y) {
            for (int x = MAPA_MIN_X; x <= MAPA_MAX_X; ++x) {
                Baldosa b = getBaldosa(x, y);
                if (b == null) {
                    continue;
                }
                if (b.hayObjeto()) {
                    Objeto obj = b.getObjeto();
                    objetos.put(new Posicion(x, y), obj);
                }
            }
        }
        return objetos;
    }
}
