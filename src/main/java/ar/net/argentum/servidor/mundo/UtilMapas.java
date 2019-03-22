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
import ar.net.argentum.servidor.Mapa;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.log4j.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class UtilMapas {

    private static final Logger LOGGER = Logger.getLogger(UtilMapas.class);

    public static Mapa cargarMapa(int num_mapa, String archivo) {

        LOGGER.info("Iniciando carga del mapa " + num_mapa + "...");

        try (RandomAccessFile f = new RandomAccessFile("datos/mapas/mapa" + num_mapa + ".map", "r")) {
            f.seek(0);

            Mapa mapa = new MapaImpl(num_mapa, 100, 100);

            short version = UtilLegacy.bigToLittle(f.readShort());
            byte[] cabecera = new byte[263];
            f.read(cabecera);

            byte byflags = 0;
            short tempint;

            tempint = UtilLegacy.bigToLittle(f.readShort());
            tempint = UtilLegacy.bigToLittle(f.readShort());
            tempint = UtilLegacy.bigToLittle(f.readShort());
            tempint = UtilLegacy.bigToLittle(f.readShort());

            byte bloq;
            short tempshort;

            for (int y = 1; y < 90; y++) {
                for (int x = 1; x < 90; x++) {
                    try {
                        Baldosa md = new BaldosaImpl();

                        byflags = UtilLegacy.bigToLittle(f.readByte());
                        bloq = (byte) (byflags & 1);
                        md.setBloqueado(bloq);

                        // Grafico de la capa 1
                        md.setGrafico(1, UtilLegacy.bigToLittle(f.readShort()));

                        // Graficoo de la capa 2
                        if ((byte) (byflags & 2) != 0) {
                            md.setGrafico(2, UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setGrafico(2, 0);
                        }

                        // Grafico de la capa 3
                        if ((byte) (byflags & 4) != 0) {
                            tempshort = UtilLegacy.bigToLittle(f.readShort());
                            md.setGrafico(3, UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setGrafico(3, 0);
                        }

                        // Grafico de la capa 4
                        if ((byte) (byflags & 8) != 0) {
                            md.setGrafico(4, UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setGrafico(4, 0);
                        }

                        if ((byte) (byflags & 16) != 0) {
                            md.setTrigger(UtilLegacy.bigToLittle(f.readShort()));
                        } else {
                            md.setTrigger((short) 0);
                        }

                        if (md.getCharindex() > 0) {
                            //EraseChar;
                        }

                        mapa.setBaldosa(x, y, md);
                    } catch (IOException ex) {
                        LOGGER.fatal(null, ex);
                    }
                }
            }
            LOGGER.info("Carga de mapa finalizada!");
            return mapa;
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return null;
    }
}
