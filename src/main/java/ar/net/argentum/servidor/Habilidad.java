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

import ar.net.argentum.servidor.entidad.Atacable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Representa una habilidad que se puede entrenar.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.MINIMAL_CLASS)
public interface Habilidad {

    /**
     * @return Nivel actual
     */
    int getNivel();

    /**
     * @param nivel
     */
    void setNivel(int nivel);

    /**
     * @return Nombre de la habilidad
     */
    @JsonIgnore
    public String getNombre();

    /**
     * @return Descripcion para mostrar
     */
    @JsonIgnore
    public String getDescripcion();

    /**
     * @return Experiencia actual
     */
    public int getExperiencia();

    /**
     * @param experiencia
     */
    public void setExperiencia(int experiencia);

    /**
     * @return Experiencia necesaria para el siguiente nivel
     */
    public int getSiguienteNivel();

    /**
     * @param siguienteNivel Experiencia necesaria para el siguiente nivel
     */
    public void setSiguienteNivel(int siguienteNivel);

    /**
     * Ganar experiencia en esta habilidad
     *
     * @param acierto Verdadero si se ha tenido suerte al realizar la habilidad
     *
     * @return Devuelve verdadero si se ha conseguido un nuevo punto al entrenar
     * esta habilidad
     */
    public boolean entrenar(boolean acierto);

    /**
     * Intentar realizar la habilidad
     *
     * @param habilidoso Habilidoso que realiza la habilidad
     * @return Devuelve verdadero si logro realizar la habilidad
     */
    public boolean realizar(Habilidoso habilidoso);

    /**
     * Intentar realizar la habilidad sobre un Atacable
     *
     * @param origen Habilidoso que realiza la habilidad
     * @param victima Atacable que es objetivo de esta habilidad
     *
     * @return Devuelve verdadero si logro realizar la habilidad
     */
    public boolean realizar(Habilidoso origen, Atacable victima);
}
