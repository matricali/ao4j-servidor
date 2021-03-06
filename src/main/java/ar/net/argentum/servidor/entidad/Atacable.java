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
package ar.net.argentum.servidor.entidad;

import ar.net.argentum.servidor.ParteCuerpo;
import ar.net.argentum.servidor.ResultadoGolpe;

/**
 * Una entidad que puede ser objetivo de daño
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Atacable {

    /**
     * La entidad recibe un ataque
     *
     * @param atacante
     * @param resultado
     * @return Devuelte un nuevo ResultadoGolpe luego de aplicar posibles
     * defensas
     */
    public ResultadoGolpe recibeAtaque(Atacante atacante, ResultadoGolpe resultado);

    /**
     * La entidad recibe daño fisico
     *
     * @param atacante
     * @param lugar Lugar del cuerpo donde se recibe el ataque
     * @param daño Cantidad de daño inicial
     * @return Cantidad de daño final aplicado luego de aplicar modificadores
     */
    public int recibeDañoFisico(Atacante atacante, ParteCuerpo lugar, int daño);

    /**
     * @return Nombre para mostrar
     */
    public String getNombre();
}
