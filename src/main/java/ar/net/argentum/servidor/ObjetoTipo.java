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
public enum ObjetoTipo {
    otUseOnce(1),
    otWeapon(2),
    otarmadura(3),
    otArboles(4),
    otGuita(5),
    otPuertas(6),
    otContenedores(7),
    otCarteles(8),
    otLlaves(9),
    otForos(10),
    otPociones(11),
    otBebidas(13),
    otLeña(14),
    otFogata(15),
    otescudo(16),
    otcasco(17),
    otAnillo(18),
    otTeleport(19),
    otYacimiento(22),
    otMinerales(23),
    otPergaminos(24),
    otInstrumentos(26),
    otYunque(27),
    otFragua(28),
    otBarcos(31),
    otFlechas(32),
    otBotellaVacia(33),
    otBotellaLlena(34),
    otManchas(35), // No se usa
    otArbolElfico(36),
    otMochilas(37),
    otYacimientoPez(38),
    otCualquiera(1000);

    private final int tipo;

    ObjetoTipo(int tipo) {
        this.tipo = tipo;
    }

    public int valor() {
        return tipo;
    }
}
