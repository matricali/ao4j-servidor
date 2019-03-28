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
 * Modificadores de clases.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ModificadoresClase {

    protected float evasion;
    protected float ataqueArmas;
    protected float ataqueProyectiles;
    protected float ataqueMano;
    protected float dañoArmas;
    protected float dañoProyectiles;
    protected float dañoMano;
    protected float escudo;
    protected float vida;

    /**
     * @return Modificador de evasion
     */
    public float getEvasion() {
        return evasion;
    }

    /**
     * @return Modificador de combate con armas
     */
    public float getAtaqueArmas() {
        return ataqueArmas;
    }

    /**
     * @return Modificador de ataque con armas de proyectiles
     */
    public float getAtaqueProyectiles() {
        return ataqueProyectiles;
    }

    /**
     * @return Modificador de ataque con artes marciales
     */
    public float getAtaqueMano() {
        return ataqueMano;
    }

    /**
     * @return Modificador de daño en combate con armas
     */
    public float getDañoArmas() {
        return dañoArmas;
    }

    /**
     * @return Modificador de daño utilizando armas de proyectiles
     */
    public float getDañoProyectiles() {
        return dañoProyectiles;
    }

    /**
     * @return Modificador de daño con artes marciales
     */
    public float getDañoMano() {
        return dañoMano;
    }

    /**
     * @return Modificador de evasion con escudo
     */
    public float getEscudo() {
        return escudo;
    }

    /**
     * @return Modificador de aumento de vida por nivel
     */
    public float getVida() {
        return vida;
    }

}
