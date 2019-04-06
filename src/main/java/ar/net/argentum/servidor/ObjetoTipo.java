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

import ar.net.argentum.servidor.objetos.Arma;
import ar.net.argentum.servidor.objetos.Bebida;
import ar.net.argentum.servidor.objetos.Cartel;
import ar.net.argentum.servidor.objetos.Casco;
import ar.net.argentum.servidor.objetos.Comestible;
import ar.net.argentum.servidor.objetos.Escudo;
import ar.net.argentum.servidor.objetos.Foro;
import ar.net.argentum.servidor.objetos.ObjetoMetadataBasica;
import ar.net.argentum.servidor.objetos.Pocion;
import ar.net.argentum.servidor.objetos.Puerta;
import ar.net.argentum.servidor.objetos.Vestimenta;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public enum ObjetoTipo {
    ALIMENTO(1, Comestible.class),
    ARMA(2, Arma.class),
    VESTIMENTA(3, Vestimenta.class),
    ARBOL(4),
    DINERO(5),
    PUERTA(6, Puerta.class),
    CONTENEDOR(7),
    CARTEL(8, Cartel.class),
    LLAVE(9),
    FORO(10, Foro.class),
    POCION(11, Pocion.class),
    LIBRO(12),
    BEBIDA(13, Bebida.class),
    MADERA(14),
    FOGATA(15),
    ESCUDO(16, Escudo.class),
    CASCO(17, Casco.class),
    ANILLO(18),
    TELETRANSPORTE(19),
    MUEBLE(20),
    JOYA(21),
    YACIMIENTO(22),
    MINERAL(23),
    PERGAMINO(24),
    AURA(25),
    INSTRUMENTO(26),
    YUNQUE(27),
    FRAGUA(28),
    GEMA(29),
    FLOR(30),
    BARCO(31),
    FLECHA(32),
    BOTELLA_VACIA(33),
    BOTELLA_LLENA(34),
    MANCHA(35), // No se usa
    ARBOL_ELFICO(36),
    MOCHILA(37),
    YACIMIENTO_PEZ(38),
    CUALQUIERA(1000);

    private final int tipo;
    private final Class<?> clase;

    ObjetoTipo(int tipo, Class<?> clase) {
        this.tipo = tipo;
        this.clase = clase;
    }

    ObjetoTipo(int tipo) {
        this.tipo = tipo;
        this.clase = ObjetoMetadataBasica.class;
    }

    public int valor() {
        return tipo;
    }

    public Class<?> getClase() {
        return clase;
    }

    /**
     * Obtener tipo de objeto desde un un identificador entero dado
     *
     * @param valor
     * @return
     */
    public static ObjetoTipo valueOf(int valor) throws IllegalArgumentException {
        for (ObjetoTipo o : ObjetoTipo.values()) {
            if (o.valor() == valor) {
                return o;
            }
        }
        throw new IllegalArgumentException("Tipo de objeto invalido (" + valor + ")");
    }
}
