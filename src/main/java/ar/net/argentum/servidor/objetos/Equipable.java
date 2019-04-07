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
package ar.net.argentum.servidor.objetos;

import org.json.JSONObject;

/**
 * Los objetos equipables son objetos que se pueden "vestir"
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class Equipable extends MetadataAbstracta {

    protected int ropaje = 0;
    protected int animacionAltos = 0;
    protected int animacionBajos = 0;

    public Equipable(int id, JSONObject data) {
        super(id, data);
        if (data.has("NumRopaje")) {
            this.ropaje = Integer.valueOf(data.getString("NumRopaje"));
        }
        if (data.has("Anim")) {
            this.animacionAltos = Integer.valueOf(data.getString("Anim"));
        }
        if (data.has("RazaEnanaAnim")) {
            this.animacionBajos = Integer.valueOf(data.getString("RazaEnanaAnim"));
        } else if (animacionAltos > 0) {
            this.animacionBajos = animacionAltos;
        }
    }

    public Equipable(Equipable original) {
        super(original);
        this.ropaje = original.getRopaje();
        this.animacionAltos = original.getAnimacionAltos();
        this.animacionBajos = original.getAnimacionBajos();
    }

    /**
     * @return ID de la animacion
     */
    public int getRopaje() {
        return ropaje;
    }

    /**
     * @param ropaje ID de la animacion
     */
    public void setRopaje(int ropaje) {
        this.ropaje = ropaje;
    }

    /**
     * @return ID de animacion para personajes altos, es usado por objetos que
     * se pueden vestir
     */
    public int getAnimacionAltos() {
        return animacionAltos;
    }

    /**
     * @param animacionAltos ID de animacion para personajes altos, es usado por
     * objetos que se pueden vestir
     */
    public void setAnimacionAltos(int animacionAltos) {
        this.animacionAltos = animacionAltos;
    }

    /**
     * @return ID de animacion para personajes bajos, es usado por objetos que
     * se pueden vestir
     */
    public int getAnimacionBajos() {
        return animacionBajos;
    }

    /**
     * @param animacionBajos ID de animacion para personajes bajos, es usado por
     * objetos que
     */
    public void setAnimacionBajos(int animacionBajos) {
        this.animacionBajos = animacionBajos;
    }
}
