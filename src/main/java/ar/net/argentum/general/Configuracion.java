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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Configuracion {

    protected static final Logger LOGGER = LogManager.getLogger(Configuracion.class);

    protected Properties prop;

    public Configuracion(String archivo) {
        this.prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(archivo);

            // Cargar archivo dentro de un objeto java.util.Properties
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.fatal(ex);
        } finally {
            // Cerramos el archivo apropiadamente.
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOGGER.fatal(e);
                }
            }
        }
    }

    public int obtenerEntero(String nombreCampo, int valorPorDefecto) {
        try {
            String valor = prop.getProperty(nombreCampo);
            if (null == valor) {
                return valorPorDefecto;
            }
            return Integer.parseInt(prop.getProperty(nombreCampo));
        } catch (NumberFormatException ex) {
            LOGGER.fatal(ex);
        }
        return valorPorDefecto;
    }
}
