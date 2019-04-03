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

import ar.net.argentum.servidor.entidad.Paralizable;
import ar.net.argentum.servidor.entidad.Viviente;
import ar.net.argentum.servidor.mundo.Orientacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.log4j.Logger;

/**
 * Representa un personaje abstracto que tiene apriencia y se mueve por el mundo
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class Personaje implements Viviente, Paralizable {

    private static final Logger LOGGER = Logger.getLogger(Personaje.class);

    protected final int charindex;
    protected String nombre;
    protected Coordenada coordenada;
    protected Orientacion orientacion = Orientacion.SUR;

    // Apariencia, todos los personajes la tienen
    protected int cuerpo;
    protected int cabeza;
    protected int arma;
    protected int escudo;
    protected int casco;

    // Viviente
    protected MinMax vida = new MinMax();
    protected boolean muerto = false;
    protected boolean mimetizado = false;

    // Paralizable
    protected boolean paralizado = false;

    public Personaje() {
        this.charindex = Servidor.crearCharindex();
        Servidor.getServidor().agregarPersonaje(this);
    }

    /**
     * @return ID del personaje creado
     */
    @JsonIgnore
    public int getCharindex() {
        return charindex;
    }

    /**
     * @return Nombre para mostrar de la entidad
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre Nombre para mostrar de la entidad
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return Coordenadas del personaje
     */
    public Coordenada getCoordenada() {
        return coordenada;
    }

    /**
     * @param coordenada Coordenadas del personaje
     */
    public void setCoordenada(Coordenada coordenada) {
        this.coordenada = coordenada;
    }

    /**
     * @return Orientacion del personaje
     */
    public Orientacion getOrientacion() {
        return orientacion;
    }

    /**
     * @param orientacion Orientacion del personaje
     */
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    /**
     * @return ID de la animacion del cuerpo
     */
    public int getCuerpo() {
        return cuerpo;
    }

    /**
     * @param cuerpo ID de la animacion del cuerpo
     */
    public void setCuerpo(int cuerpo) {
        this.cuerpo = cuerpo;
    }

    /**
     * @return ID de la animacion de la cabeza
     */
    public int getCabeza() {
        return cabeza;
    }

    /**
     * @param cabeza ID de la animacion de la cabeza
     */
    public void setCabeza(int cabeza) {
        this.cabeza = cabeza;
    }

    /**
     * @return the arma
     */
    public int getArma() {
        return arma;
    }

    /**
     * @param arma the arma to set
     */
    public void setArma(int arma) {
        this.arma = arma;
    }

    /**
     * @return the escudo
     */
    public int getEscudo() {
        return escudo;
    }

    /**
     * @param escudo the escudo to set
     */
    public void setEscudo(int escudo) {
        this.escudo = escudo;
    }

    /**
     * @return the casco
     */
    public int getCasco() {
        return casco;
    }

    /**
     * @param casco the casco to set
     */
    public void setCasco(int casco) {
        this.casco = casco;
    }

    @Override
    public MinMax getVida() {
        return vida;
    }

    @Override
    public boolean isMuerto() {
        return muerto;
    }

    public void setMuerto(boolean muerto) {
        this.muerto = muerto;
    }

    /**
     * @return Verdadero si la entidad esta paralizada
     */
    @Override
    public boolean isParalizado() {
        return paralizado;
    }

    /**
     * @param paralizado Verdadero si la entidad esta paralizada
     */
    @Override
    public void setParalizado(boolean paralizado) {
        this.paralizado = paralizado;
    }

    /**
     * Mover el personaje en una direccion dada y enviar el mensaje al cliente
     * de todos los usuarios en el area.
     *
     * @see MoveUserChar
     *
     * @param orientacion
     * @return Verdadero si el personaje se ha movido
     */
    public boolean mover(Orientacion orientacion) {

        Posicion nuevaPosicion = Logica.calcularPaso(getCoordenada().getPosicion(), orientacion);

        if (!Logica.isPosicionValida(coordenada.getMapa(), nuevaPosicion.getX(), nuevaPosicion.getY(), false, true)) {
            return false;
        }

        // Acualizamos la posicion y orientacion del personaje
        getMapaActual().getBaldosa(getCoordenada().getPosicion()).setCharindex(0);
        getMapaActual().getBaldosa(nuevaPosicion).setCharindex(getCharindex());

        this.coordenada.setPosicion(nuevaPosicion);
        setOrientacion(orientacion);

        // Le avisamos a los otros clientes que el personaje se movio
        Servidor.getServidor().todosMenosUsuarioArea(charindex, (usuario, conexion) -> {
            conexion.enviarPersonajeCaminar(charindex, orientacion.valor());
        });

        return true;
    }

    /**
     * Envia a todos los clientes cercanos el cambio de apariencia sobre el
     * personaje
     */
    public void actualizarApariencia() {
        Servidor.getServidor().todosMapa(getCoordenada().getMapa(), (usuario, conexion) -> {
            conexion.enviarPersonajeCambiar(
                    getCharindex() == conexion.getUsuario().getCharindex() ? 1 : getCharindex(),
                    getOrientacion().valor(),
                    getCuerpo(),
                    getCabeza(),
                    getArma(),
                    getEscudo(),
                    getCasco());
        });
    }

    protected Mapa getMapaActual() {
        return Servidor.getServidor().getMapa(getCoordenada().getMapa());
    }

    /**
     * @see Sonidos
     * @param sonido 
     */
    public void emitirSonido(int sonido) {
        Servidor.getServidor().todosMapa(getCoordenada().getMapa(), (charindex, conexion) -> {
            conexion.enviarMundoReproducirSonido(sonido, getCoordenada().getPosicion());
        });
    }
}
