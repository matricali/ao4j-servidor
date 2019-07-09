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
import ar.net.argentum.servidor.entidad.Atacante;
import ar.net.argentum.servidor.mundo.Orientacion;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Un personaje controlado por el servidor
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class NPC extends Personaje implements Atacable, Atacante {

    private static final Logger LOGGER = LogManager.getLogger(NPC.class);
    private int contadorMovimiento;
    private int contadorAtaque;
    private int objetivo = 0;

    public NPC() {
        super();
        alCrear();
    }

    public NPC(NPC original, int x, int y) {
        this();
        this.nombre = original.getNombre();
        this.vida.setMax(original.getVida().getMax());
        this.vida.llenar();
        this.cuerpo = original.getCuerpo();
        this.cabeza = original.getCabeza();
        this.escudo = original.getEscudo();
        this.arma = original.getArma();
        this.casco = original.getCasco();
        this.coordenada = new Coordenada();
        coordenada.setMapa(original.getCoordenada().getMapa());
        coordenada.setPosicion(x, y);
        this.orientacion = Orientacion.SUR;
    }

    @Override
    public void matar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultadoGolpe recibeAtaque(Atacante atacante, ResultadoGolpe resultado) {
        // @TODO: Defenderse

        if (getEscudo() != 0) {
            if (Logica.verdaderoAleatorio(20)) {
                emitirSonido(Sonidos.SND_ESCUDO);
                return new ResultadoGolpe(false, ResultadoGolpe.Causa.VICTIMA_RECHAZO_CON_ESCUDO);
            }
        }

        // Intento esquivar el ataque
        if (Logica.verdaderoAleatorio(20)) {
            emitirSonido(Sonidos.SND_SWING);
            return new ResultadoGolpe(false, ResultadoGolpe.Causa.VICTIMA_ESQUIVO_ATAQUE);
        }

        // No logramos evitar el ataque
        resultado.setCausa(ResultadoGolpe.Causa.DESCONOCIDA);
        resultado.setExito(true);
        return resultado;
    }

    @Override
    public int recibeDañoFisico(Atacante atacante, ParteCuerpo lugar, int daño) {
        // @TODO: Aplicar defensa
        boolean muere = getVida().disminuir(daño);

        if (muere) {
            // Se murio, disparamos el evento
            alMorir();
            // Disparamos el evento indicandole al atacante que nos ha matado
            atacante.alMatar(this);
        }

        return daño;
    }

    private void alMorir() {
        // @TODO: Disparar evento cancelable
        setMuerto(true);

        // Enviamos el sonido (Esta parte es la mejor)
        emitirSonido(Sonidos.SND_MUERTE_HOMBRE);

        // Respawnear
        respawnear();

        Mapa m = getMapaActual();
        Baldosa b = m.getBaldosa(getCoordenada().getPosicion());
        b.setPersonaje(null);
        m.getPersonajes().remove(this);
        Servidor.getServidor().eliminarPersonaje(this);

        Servidor.getServidor().todosMapa(m, (charindex, conexion) -> {
            conexion.enviarPersonajeQuitar(getCharindex());
        });

        // Soltar objetos
        Objeto obj = new Objeto(12, Logica.enteroAleatorio(10, 30));
        if (!b.hayObjeto()) {
            m.setObjeto(getCoordenada().getPosicion(), obj);
        }
    }

    protected void respawnear() {
        boolean exito = false;
        int x = 5;
        int y = 5;
        Mapa m = getMapaActual();
        Baldosa b = null;
        while (!exito) {
            x = Logica.enteroAleatorio(10, 90);
            y = Logica.enteroAleatorio(10, 90);
            b = m.getBaldosa(x, y);
            if (!b.hayAlguien() && !b.isBloqueado() && b.getTrigger() != 3) {
                exito = true;
            }
        }

        NPC npc = new NPC(this, x, y);

        b.setPersonaje(npc);
        m.getPersonajes().add(npc);

        LOGGER.debug(String.format("NPC %d %s respawneado en %d-%d–%d como NPC %d", getCharindex(), getNombre(), m.getNumero(), x, y, npc.getCharindex()));

        Servidor.getServidor().todosMapa(m, (charindex, conexion) -> {
            conexion.enviarPersonajeCrear(npc);
        });
    }

    public int getExperiencia() {
        return 200;
    }

    /**
     * Realiza la actualizacion de los estados
     */
    public void tick() {
        // @TODO: Veneno, Paralizis, etc.
        inteligenciaArtificial();
    }

    /**
     * Aplicamos inteligencia articial
     */
    protected void inteligenciaArtificial() {
        if (contadorMovimiento < 3) {
            ++contadorMovimiento;
        } else {
            try {
                inteligenciaMoverse();
            } catch (Exception ex) {
                LOGGER.fatal("Error en IA inteligenciaMoverse", ex);
            }
        }

        if (contadorAtaque < 5) {
            ++contadorAtaque;
        } else {
            try {
                inteligenciaAtacar();
            } catch (Exception ex) {
                LOGGER.fatal("Error en IA inteligenciaAtacar", ex);
            }
        }
    }

    public void inteligenciaMoverse() {
        if (objetivo == 0) {
            // No tenemos ningun objeto, intentamos encontrar algunoo
            Personaje p = inteligenciaBuscarObjetivo(10);
            if (p != null) {
                this.objetivo = p.getCharindex();
            }
        }
        if (objetivo != 0) {
            // Tenemos un objetivo, nos intentaremos mover hasta el
            Personaje p = Servidor.getServidor().getPersonaje(objetivo);

            if (p == null || p.isMuerto()) {
                this.objetivo = 0;
                return;
            }

            Orientacion o = Logica.buscarOrientacion(getCoordenada().getPosicion(), p.getCoordenada().getPosicion());
            if (o != null) {
                // Nos movemos hacia nuestro objetivo
                mover(o);
            }
            return;
        }
        // No tenemos ningun objetivo
        // @TODO moverse forma aleatoria
    }

    /**
     * Busca un objetivo en un rango de vision dado
     *
     * @param radio
     * @return Personaje objetivo
     */
    public Personaje inteligenciaBuscarObjetivo(int radio) {
        int x = getCoordenada().getPosicion().getX();
        int y = getCoordenada().getPosicion().getY();

        int minX = x - radio;
        int maxX = x + radio;
        int minY = y - radio;
        int maxY = y + radio;

        if (minX < Logica.MAPA_BORDE_X_MIN) {
            minX = Logica.MAPA_BORDE_X_MIN;
        }
        if (maxX > Logica.MAPA_BORDE_X_MAX) {
            maxX = Logica.MAPA_BORDE_X_MAX;
        }
        if (minY < Logica.MAPA_BORDE_Y_MIN) {
            minY = Logica.MAPA_BORDE_Y_MIN;
        }
        if (maxY > Logica.MAPA_BORDE_Y_MAX) {
            maxY = Logica.MAPA_BORDE_Y_MAX;
        }

        // Recorremos las baldosas buscando un objetivo
        int o = 0;
        Mapa m = getMapaActual();
        Baldosa b;
        for (int nY = minY; nY <= maxY; ++nY) {
            for (int nX = minX; nX <= maxX; ++nX) {
                b = m.getBaldosa(nX, nY);
                if (b.hayAlguien()) {
                    Personaje p = b.getPersonaje();
                    if (p.getCharindex() == getCharindex()) {
                        // Que no se ataque a el mismo (?)
                        continue;
                    }
                    if (p instanceof Atacable && !p.isMuerto()) {
                        return p;
                    }
                }
            }
        }
        // No encontramos un nuevo objetivo
        return null;
    }

    public void inteligenciaMoverseAlAzar() {

    }

    public void inteligenciaAtacar() {
        Posicion pos;

        for (Orientacion h : Orientacion.values()) {
            pos = Logica.calcularPaso(getCoordenada().getPosicion(), h);
            if (!Logica.isDentroDelLimite(getCoordenada().getMapa(), pos.getX(), pos.getY())) {
                continue;
            }
            Mapa m = getMapaActual();
            Baldosa b = m.getBaldosa(pos);
            if (b.hayAlguien()) {
                Personaje victima = b.getPersonaje();
                if (!(victima instanceof Atacable)) {
                    continue;
                }
                if (victima.isMuerto()) {
                    continue;
                }
                if (getOrientacion() != h) {
                    if (isParalizado()) {
                        // Si esta paralizado no se puede mover
                        continue;
                    }
                    // Cambiamos la orientacion del personaje
                    setOrientacion(h);
                    Servidor.getServidor().todosMapa(m, (charindex, conexion) -> {
                        conexion.enviarPersonajeCambiar(
                                getCharindex(),
                                getOrientacion().valor(),
                                getCuerpo(),
                                getCabeza(),
                                getArma(),
                                getEscudo(),
                                getCasco());
                    });
                }
                golpea((Atacable) victima);
                break;
            }
        }
        contadorAtaque = 0;
    }

    public ResultadoGolpe golpea(Atacable objetivo) {

        Atacable victima = (Atacable) objetivo;
        ResultadoGolpe resultado = new ResultadoGolpe();

        if (Logica.verdaderoAleatorio(50)) {

            emitirSonido(Sonidos.SND_SWING);
            resultado.setExito(false);
            resultado.setCausa(ResultadoGolpe.Causa.ATACANTE_FALLO);
            victima.recibeAtaque(this, resultado);
            return resultado;
        }

        // Realizamos el ataque sobre la victima
        resultado = victima.recibeAtaque(this, resultado);

        if (!resultado.isExito()) {
            emitirSonido(Sonidos.SND_SWING);
            return resultado;
        }

        // Si llegamos hasta acá es porque conectamos el golpe
        int daño = Logica.enteroAleatorio(10, 20);

        emitirSonido(Sonidos.SND_IMPACTO);

        if (victima instanceof Personaje) {
            Personaje pv = (Personaje) victima;
            Servidor.getServidor().todosArea(pv.getCoordenada(), 10, (charindex, conexion) -> {
                conexion.enviarPersonajeAnimacion(charindex == pv.getCharindex() ? 1 : pv.getCharindex(), 14, 2);
            });
        }

        // Le provocamos daño a la victima
        ParteCuerpo lugar = ParteCuerpo.alAzar();
        victima.recibeDañoFisico(this, lugar, daño);
        return resultado;
    }

    @Override
    public void alMatar(Atacable victima) {
        if (victima instanceof Personaje) {
            if (((Personaje) victima).getCharindex() == objetivo) {
                // Matamos a nuestro objetivo
                this.objetivo = 0;
            }
        }
    }

    @Override
    public boolean mover(Orientacion orientacion) {
        if (super.mover(orientacion)) {
            // Reiniciamos el contador de movimiento
            contadorMovimiento = 0;
            return true;
        }
        return false;
    }
}
