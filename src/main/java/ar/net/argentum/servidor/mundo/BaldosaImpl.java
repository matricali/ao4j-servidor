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

import ar.net.argentum.servidor.Baldosa;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class BaldosaImpl implements Baldosa {

    /**
     * Los graficos en sus capas
     */
    protected int capas[] = new int[4];
    /**
     * ID del objeto arrojado en esta posicion
     */
    protected int objeto;
    /**
     * ID del personaje parado en esta posicion
     */
    protected int charindex;
    /**
     * Se puede pisar este tile?
     */
    protected boolean bloqueado;

    /**
     * Atributos especiales del tile
     */
    protected short trigger;

    /**
     * @param capa
     * @return the capas
     */
    @Override
    public int getGrafico(int capa) {
        return capas[capa - 1];
    }

    /**
     * @param capa
     * @param grhIndex
     */
    @Override
    public void setGrafico(int capa, int grhIndex) {
        this.capas[capa - 1] = grhIndex;
    }

    /**
     * @return the objeto
     */
    @Override
    public int getObjeto() {
        return objeto;
    }

    /**
     * @param objeto the objeto to set
     */
    @Override
    public void setObjeto(int objeto) {
        this.objeto = objeto;
    }

    /**
     * @return the charindex
     */
    @Override
    public int getCharindex() {
        return charindex;
    }

    /**
     * @param charindex the charindex to set
     */
    @Override
    public void setCharindex(int charindex) {
        this.charindex = charindex;
    }

    /**
     * @return the bloqueado
     */
    @Override
    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * @param bloqueado the bloqueado to set
     */
    @Override
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
    
    @Override
    public void setBloqueado(byte bloq) {
        this.bloqueado = bloqueado;
    }

    /**
     * @return the trigger
     */
    @Override
    public short getTrigger() {
        return trigger;
    }

    /**
     * @param trigger the trigger to set
     */
    @Override
    public void setTrigger(short trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean isAgua() {

        // @TODO: Detectar si es agua o no
        return false;
    }

    @Override
    public boolean isTierra() {
        return !isAgua();
    }
}
