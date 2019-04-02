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
public enum ParteCuerpo {
    TORSO(1, "el torso"),
    CABEZA(2, "la cabeza"),
    BRAZO_DERECHO(3, "el brazo derecho"),
    BRAZO_IZQUIERDO(4, "el brazo izquierdo"),
    PIERNA_DERECHA(5, "la pierna derecha"),
    PIERNA_IZQUIERDA(6, "la pierna izquierda");

    public static ParteCuerpo valueOf(int id) {
        for (ParteCuerpo o : ParteCuerpo.values()) {
            if (o.valor() == id) {
                return o;
            }
        }
        throw new IllegalArgumentException("Parte del cuerpo inválida (" + id + ")");
    }

    public static ParteCuerpo alAzar() {
        return valueOf(Logica.enteroAleatorio(1, 6));
    }

    protected final int id;
    protected final String nombre;

    private ParteCuerpo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int valor() {
        return id;
    }

    public String getDescripcion() {
        return nombre;
    }
}
