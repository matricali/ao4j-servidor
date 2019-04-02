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

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ResultadoGolpe {

    protected boolean exito;
    protected Causa causa;

    public ResultadoGolpe() {
        this(false, Causa.DESCONOCIDA);
    }

    public ResultadoGolpe(boolean exito, Causa causa) {
        this.exito = exito;
        this.causa = causa;
    }

    public static enum Causa {
        DESCONOCIDA,
        ATACANTE_FALLO,
        VICTIMA_ESQUIVO_ATAQUE,
        VICTIMA_RECHAZO_CON_ESCUDO;
    }

    /**
     * @return the exito
     */
    public boolean isExito() {
        return exito;
    }

    /**
     * @param exito the exito to set
     */
    public void setExito(boolean exito) {
        this.exito = exito;
    }

    /**
     * @return the causa
     */
    public Causa getCausa() {
        return causa;
    }

    /**
     * @param causa the causa to set
     */
    public void setCausa(Causa causa) {
        this.causa = causa;
    }
}
