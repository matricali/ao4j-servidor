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
public interface Baldosa {

    public void setGrafico(int capa, int grhIndex);

    /**
     *
     * @param objeto
     */
    public void setObjeto(Objeto objeto);

    public void setCharindex(int charindex);

    public void setBloqueado(boolean bloqueado);

    public int getGrafico(int capa);

    /**
     * @return Objeto arrojado en la baldosa
     */
    public Objeto getObjeto();

    /**
     * @return Verdadero si hay un objeto en la baldosa
     */
    public boolean hayObjeto();

    public int getCharindex();

    public boolean isBloqueado();

    public short getTrigger();

    public void setTrigger(short trigger);

    public boolean isAgua();

    public boolean isTierra();

    /**
     * @return Verdadero si hay un personaje en la baldosa
     */
    public boolean hayAlguien();

    /**
     * @return Instancia del Personaje parado en la baldosa
     */
    public Personaje getPersonaje();

    /**
     * Parar un personaje en la baldosa
     *
     * @param p
     */
    public void setPersonaje(Personaje p);

    /**
     * @return Instancia del Mapa al cual pertenece la baldosa
     */
    public Mapa getMapa();
}
