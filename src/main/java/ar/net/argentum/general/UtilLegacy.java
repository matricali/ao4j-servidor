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
package ar.net.argentum.general;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utilidades para cargar archivos legado de la antigua version de Argentum
 * Online desarrollada en Visual Basic
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class UtilLegacy {

    /**
     * Convertir un entero de big-endian a little-endian
     *
     * @see https://es.wikipedia.org/wiki/Endianness
     * @param bigendian
     * @return
     */
    public static int bigToLittle(int bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getInt(0);
    }

    /**
     * Convertir un float de big-endian a little-endian
     *
     * @see https://es.wikipedia.org/wiki/Endianness
     * @param bigendian
     * @return
     */
    public static float bigToLittle(float bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putFloat(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getFloat(0);
    }

    /**
     * Convertir un short de big-endian a little-endian
     *
     * @see https://es.wikipedia.org/wiki/Endianness
     * @param bigendian
     * @return
     */
    public static short bigToLittle(short bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(2);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getShort(0);
    }

    /**
     * Convertir un byte de big-endian a little-endian
     *
     * @see https://es.wikipedia.org/wiki/Endianness
     * @param bigendian
     * @return
     */
    public static byte bigToLittle(byte bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(1);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.get(0);
    }
}
