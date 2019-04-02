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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;

/**
 * Reprensenta una entidad que puede realizar habilidades
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface Habilidoso {

    /**
     * Entrenar una habilidad
     *
     * @param id Identificador de la habilidad
     * @param acierto Verdadero si se ha tenido suerte al realizar la habilidad
     */
    void entrenarHabilidad(String id, boolean acierto);

    /**
     * Entrenar una habilidad
     *
     * @param skill Habilidad
     * @param acierto Verdadero si se ha tenido suerte al realizar la habilidad
     */
    public void entrenarHabilidad(Habilidad skill, boolean acierto);

    /**
     * Obtener un skill del usuario
     *
     * @param nombre
     * @return
     */
    @JsonIgnore
    Habilidad getSkill(String nombre);

    /**
     * @return Habilidades del personaje
     */
    HashMap<String, Habilidad> getSkills();

    /**
     * Calculamos si el usuario logra realizar una habilidad, si lo logra
     * tambien aumentamos la experiencia de la habilidad.
     *
     * @param id Identificador de la habilidad
     * @return Devuelve verdadero si el usuario ha logrado realizar la habilidad
     */
    boolean realizarHabilidad(String id);

    /**
     * @param skills Habilidades del personaje
     */
    void setSkills(HashMap<String, Habilidad> skills);

    /**
     * Aprender una nueva habilidad
     *
     * @param id Identificador de la habilidad
     * @return Instancia de la habilidad aprendida
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public Habilidad aprenderHabilidad(String id) throws InstantiationException, IllegalAccessException;

}
