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

import ar.net.argentum.servidor.objetos.Equipable;
import ar.net.argentum.servidor.objetos.Arma;
import ar.net.argentum.servidor.entidad.Atacable;
import ar.net.argentum.servidor.entidad.Atacante;
import ar.net.argentum.servidor.habilidades.Meditar;
import ar.net.argentum.servidor.mundo.Orientacion;
import ar.net.argentum.servidor.objetos.Bebida;
import ar.net.argentum.servidor.objetos.Comestible;
import ar.net.argentum.servidor.objetos.Pocion;
import ar.net.argentum.servidor.objetos.Vestimenta;
import ar.net.argentum.servidor.protocolo.ConexionConCliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Representa un juegador
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Usuario extends Personaje implements Atacante, Atacable, GanaExperiencia, Habilidoso, RecibeChat {

    private static final Logger LOGGER = Logger.getLogger(Usuario.class);

    /**
     * Compruebe si esta registrado un usuario.
     *
     * @param nombre Nombre de usuario
     * @return bool Verdadero si el usuario ya existe
     */
    public static boolean existePersonaje(String nombre) {
        File f = getArchivo(nombre);
        return f.exists() && !f.isDirectory();
    }

    public static boolean nombreValido(String nombre) {
        if (nombre.isEmpty() || nombre.length() > 16) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]+$");
        Matcher matcher = pattern.matcher(nombre);
        return matcher.find();
    }

    public static Usuario cargar(String nombre) throws Exception {
        if (!existePersonaje(nombre)) {
            throw new Exception("No existe el personaje!");
        }
        ObjectMapper mapper = new ObjectMapper();
        Usuario usuario = mapper.readValue(getArchivo(nombre), Usuario.class);
        return usuario;
    }

    private static File getArchivo(String nombre) {
        return new File("datos/personajes/" + nombre.toLowerCase() + ".json");
    }

    protected final int userindex;
    protected String password;
    protected boolean conectado;

    // GanaExperiencia
    protected int nivel = 1;
    protected int experienciaActual = 0;
    protected int experienciaSiguienteNivel = 300;

    protected int dinero = 0;

    // PortaObjetos
    protected ConcurrentHashMap<Integer, InventarioSlot> inventario;
    protected int inventarioCantSlots = 20;

    // Estadisticas
    protected MinMax mana = new MinMax();
    protected MinMax stamina = new MinMax();
    protected MinMax hambre = new MinMax();
    protected MinMax sed = new MinMax();
    protected MinMax golpe = new MinMax();

    protected boolean navegando = false;
    protected boolean meditando = false;
    protected boolean descansando = false;
    protected boolean newbie = true;
    protected boolean desnudo = false;

    protected String raza;
    protected String genero;
    protected String clase;
    protected int cabezaOriginal = 1;

    // Habilidoso
    protected HashMap<String, Habilidad> skills = new HashMap<>();

    protected HashMap<String, Integer> atributos = new HashMap<>();

    // Equipable
    protected int WeaponEqpObjIndex = 0;
    protected int WeaponEqpSlot = 0;
    protected int ShieldEqpObjIndex = 0;
    protected int ShieldEqpSlot = 0;
    protected int HelmEqpObjIndex = 0;
    protected int HelmEqpSlot = 0;
    protected int ArmorEqpObjIndex = 0;
    protected int ArmorEqpSlot = 0;

    // Contadores
    protected int contadorFrio = 0;
    protected int contadorEnergia = 0;
    protected int contadorSanar = 0;
    protected int contadorHambre = 0;
    protected int contadorSed = 0;

    public Usuario() {
        this.userindex = Servidor.crearUserindex();
        alCrear();
    }

    /**
     * @return Password del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password Password del usuario
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Verdadero si el usuario esta conectado
     */
    public boolean isConectado() {
        return conectado;
    }

    /**
     * @param conectado Verdadero si el usuario esta conectado
     */
    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    /**
     * Guardar el estado actual del personaje
     */
    public void guardar() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(Usuario.getArchivo(nombre), this);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public InventarioSlot getInventarioSlot(int slot) {
        return inventario.get(slot);
    }

    public void setInventarioSlot(int slot, final InventarioSlot inv) {
        inventario.put(slot, inv);
        // @TODO: Mandar paquete aca
    }

    @Override
    public int getNivel() {
        return nivel;
    }

    /**
     * @param nivel Nuevo nivel del usuario
     */
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    @Override
    public int getExperienciaActual() {
        return experienciaActual;
    }

    /**
     * @see ganarExperiencia
     * @param experiencia Establecer experiencia del usuario
     */
    public void setExperienciaActual(int experiencia) {
        this.experienciaActual = experiencia;
    }

    @Override
    public int getExperienciaSiguienteNivel() {
        return experienciaSiguienteNivel;
    }

    /**
     * @see ganarExperiencia
     * @param experiencia Establecer experiencia necesaria para pasar de nivel
     */
    public void setExperienciaSiguienteNivel(int experiencia) {
        this.experienciaSiguienteNivel = experiencia;
    }

    /**
     * Aumentar los puntos de experiencia del usuario, si esta conectado le
     * enviamos un mensaje de notificacion
     *
     * @param exp Cantidad de experiencia
     */
    @Override
    public void ganarExperiencia(int exp) {
        if (nivel >= Balance.NIVEL_MAX) {
            // Si ya tenemos el nivel maximo no podemos ganar experiencia.
            return;
        }
        this.experienciaActual += exp;
        enviarMensaje("§3Has ganado {0} puntos de experiencia!", exp);
        while (experienciaActual >= experienciaSiguienteNivel) {
            // Alcanzamos un nuevo nivel
            ++this.nivel;
            this.experienciaActual -= experienciaSiguienteNivel;
            this.experienciaSiguienteNivel = Balance.calcularExperienciaParaPasarNivel(nivel, experienciaSiguienteNivel);
            alSubirDeNivel();
        }
        getConexion().enviarUsuarioExperiencia();
    }

    /**
     * Evento que se produce al subir de nivel
     */
    protected void alSubirDeNivel() {
        enviarMensaje("§aHas subido de nivel!");
        emitirSonido(Sonidos.SND_NIVEL);
        getConexion().enviarUsuarioExperiencia();

        // Aumentar vida
        int aumentoVida = Balance.calcularAumentoVida(getClase(), getAtributos().get("constitucion"));
        if (aumentoVida > 0) {
            getVida().aumentarMax(aumentoVida);
            enviarMensaje("Has ganado {0} puntos de vida.", aumentoVida);
        }

        // Aumentamos el la energia
        int aumentoStamina = Balance.calcularAumentoEnergia(getClase());
        if (aumentoStamina > 0) {
            getStamina().aumentarMax(aumentoStamina);
            enviarMensaje("Has ganado {0} puntos de energia.", aumentoStamina);
        }

        // Aumentar mana
        int aumentoMana = Balance.calcularAumentoMana(getClase(), getAtributos().get("inteligencia"));
        if (aumentoMana > 0) {
            getMana().aumentarMax(aumentoMana);
            enviarMensaje("Has ganado {0} puntos de mana.", aumentoMana);
        }

        // Aumentamos el golpe
        // @TODO: Golpe en nivel 36
        int aumentarGolpe = Balance.calcularAumentoGolpe(getClase(), getNivel());
        if (aumentarGolpe > 0) {
            getGolpe().aumentarMax(aumentarGolpe);
            getGolpe().aumentar(aumentarGolpe);
            enviarMensaje("Tu golpe maximo aumento en {0} puntos.", aumentarGolpe);
            enviarMensaje("Tu golpe minimo aumento en {0} puntos.", aumentarGolpe);
        }

        // @TODO: Aumentar skills libres
        // Enviamos la informacion al cliente
        getConexion().enviarUsuarioStats();

        // Verificar si deja de ser NEWBIE
        if (getNivel() > 12 && isNewbie()) {
            // Dejamos de ser NEWBIE
            setNewbie(false);
            // Quitamos los objetos NEWBIE
            for (Map.Entry<Integer, InventarioSlot> slot : inventario.entrySet()) {
                if (slot.getValue().getObjeto().isNewbie()) {
                    // Si el objeto es newbie, entonces lo eliminamos del inventario
                    inventarioQuitarObjeto(slot.getKey());
                }
            }
        }

        guardar();
    }

    /**
     * @return Mana del usuario
     */
    public MinMax getMana() {
        return mana;
    }

    /**
     * @return Energia del usuario
     */
    public MinMax getStamina() {
        return stamina;
    }

    /**
     * @return Hambre del usuario
     */
    public MinMax getHambre() {
        return hambre;
    }

    /**
     * @return Sed del usuario
     */
    public MinMax getSed() {
        return sed;
    }

    /**
     * @return Golpe minimo y maximo
     */
    public MinMax getGolpe() {
        return golpe;
    }

    /**
     * @return Verdadero si el usuario esta navegando
     */
    public boolean isNavegando() {
        return navegando;
    }

    /**
     * @param navegando Verdadero si el usuario esta navegando
     */
    public void setNavegando(boolean navegando) {
        this.navegando = navegando;
    }

    /**
     * @return Verdadero si el usuario esta meditando
     */
    @JsonIgnore
    public boolean isMeditando() {
        return meditando;
    }

    /**
     * Activa o desactiva el estado de meditacion
     *
     * @param meditando Verdadero si el usuario esta meditando
     */
    @JsonIgnore
    public void setMeditando(boolean meditando) {
        this.meditando = meditando;
        if (meditando) {
            final int efecto = Meditar.getEfecto(nivel);
            // Le avisamos al cliente que inicie la animacion de meditar
            getConexion().enviarPersonajeAnimacion(1, efecto, -1);
            // Le avisamos a los otros clientes que inicien la animacion sobre el personaje
            Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
                conexion.enviarPersonajeAnimacion(getCharindex(), efecto, -1);
            });
        } else {
            enviarMensaje("§7Dejas de meditar.");
            // Le avisamos al cliente que pare la animacin
            getConexion().enviarPersonajeAnimacion(1, 0, 0);
            // Le avisamos a los otros clientes que eliminen la animacion que le corresponde a este usuario
            Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
                conexion.enviarPersonajeAnimacion(getCharindex(), 0, 0);
            });
        }
    }

    /**
     * @return the descansando
     */
    @JsonIgnore
    public boolean isDescansando() {
        return descansando;
    }

    /**
     * @param descansando the descansando to set
     */
    @JsonIgnore
    public void setDescansando(boolean descansando) {
        this.descansando = descansando;
        if (descansando) {
            enviarMensaje("Has comenzado a descascar.");
        } else {
            enviarMensaje("Has dejado de descansar.");
        }
    }

    @Override
    public boolean mover(Orientacion orientacion) {

        // @TODO: Cancelar /salir
        if (isParalizado()) {
            enviarMensaje("No puedes moverte porque estás paralizado.");
            return false;
        }
        if (isMeditando()) {
            // Detenemos la meditacion
            setMeditando(false);
            return false;
        }
        if (isDescansando()) {
            // Dejamos de descansar
            setDescansando(false);
        }
        // @TODO: Solo el ladron y el bandido pueden caminar ocultos

        return super.mover(orientacion);
    }

    /**
     * Enviar un mensaje al usuario
     *
     * @param mensaje
     */
    @Override
    public void enviarMensaje(String mensaje) {
        getConexion().enviarMensaje(mensaje);
    }

    /**
     * Enviar un mensaje al usuario con formato
     *
     * @param mensaje
     * @param args
     */
    @Override
    public void enviarMensaje(String mensaje, Object... args) {
        enviarMensaje(MessageFormat.format(mensaje, args));
    }

    /**
     * El usuario realiza un golpe de ataque
     *
     * @return
     */
    public boolean doGolpear() {
        if (isMuerto()) {
            enviarMensaje("§7Estas muerto!!");
            return false;
        }
        // @TODO: Verificar intervalos
        // @TODO: Cancelar /salir
        if (isMeditando()) {
            // No podes golpear si estas meditando
            return false;
        }
        if (isDescansando()) {
            // Dejamos de descansar
            setDescansando(false);
        }
        // @TODO: Sacar ocultarse

        // Verificamos que tengamos energia para luchar
        if (getStamina().getMin() < Balance.COMBATE_ENERGIA_NECESARIA) {
            enviarMensaje("§7Estás muy {0} para luchar.", "MUJER".equals(getGenero()) ? "cansada" : "cansado");
            return false;
        }

        Posicion nuevaPosicion = Logica.calcularPaso(getCoordenada().getPosicion(), orientacion);
        Baldosa b = getMapaActual().getBaldosa(nuevaPosicion);

        if (b.getCharindex() == 0) {
            // Arrojamos un golpe al aire, enviamos el sonido y disminuimos la energia
            emitirSonido(Sonidos.SND_SWING);
            getStamina().disminuir(Balance.COMBATE_ENERGIA_NECESARIA);
            getConexion().enviarUsuarioStats();
            return true;
        }

        if (b.getCharindex() == getCharindex()) {
            enviarMensaje("No podes atacarte a tí mismo!");
            return false;
        }

        Personaje objetivo = b.getPersonaje();

        if (objetivo == null) {
            return false;
        }

        if (!Logica.enRangoDeVision(getCoordenada().getPosicion(), objetivo.getCoordenada().getPosicion())) {
            enviarMensaje("§7Estas demasiado lejos para atacar.");
            return false;
        }

        if (!(objetivo instanceof Atacable)) {
            enviarMensaje("§4No puedes atacar a {0}.", objetivo.getNombre());
            return false;
        }

        if (objetivo.isMuerto()) {
            enviarMensaje("§7No puedes atacar a un espiritu.");
            return false;
        }

        golpea((Atacable) objetivo);
        return true;
    }

    public ResultadoGolpe golpea(Atacable objetivo) {

        Atacable victima = (Atacable) objetivo;
        ResultadoGolpe resultado = new ResultadoGolpe();

        if (!realizarHabilidad("CombateConArmas")) {
            enviarMensaje("§c¡¡¡Has fallado el golpe!!!");
            emitirSonido(Sonidos.SND_SWING);
            getStamina().disminuir(Balance.COMBATE_ENERGIA_NECESARIA);
            getConexion().enviarUsuarioStats();

            resultado.setExito(false);
            resultado.setCausa(ResultadoGolpe.Causa.ATACANTE_FALLO);

            victima.recibeAtaque(this, resultado);
            return resultado;
        }

        // Gastamos energia al dar el golpe
        getStamina().disminuir(Balance.COMBATE_ENERGIA_NECESARIA);
        getConexion().enviarUsuarioStats();

        // Realizamos el ataque sobre la victima
        resultado = victima.recibeAtaque(this, resultado);

        if (!resultado.isExito()) {
            switch (resultado.getCausa()) {
                case VICTIMA_RECHAZO_CON_ESCUDO:
                    enviarMensaje("§c¡¡¡{0} rechazó el ataque con su escudo!!!", ((Personaje) objetivo).getNombre());
                    break;
                case VICTIMA_ESQUIVO_ATAQUE:
                    enviarMensaje("§c¡¡¡Has fallado el golpe!!!");
                    break;
            }
            emitirSonido(Sonidos.SND_SWING);

            return resultado;
        }

        // Si llegamos hasta acá es porque conectamos el golpe
        int daño;
        int dañoApuñalamiento = 0;
        if (WeaponEqpObjIndex > 0) {
            // Obtenemos el arma que tenemos equipada
            ObjetoMetadata obj = inventario.get(WeaponEqpSlot).getObjeto();
            Arma metadataArma = (Arma) obj;
            daño = Logica.calcularDañoFisico(getClase(), getAtributos().get("fuerza"), getGolpe().getMin(), getGolpe().getMax(), metadataArma.getMinDaño(), metadataArma.getMaxDaño());
            if (metadataArma.isApuñala()) {
                if (realizarHabilidad("Apuñalar")) {
                    dañoApuñalamiento = daño * 2;
                    daño += dañoApuñalamiento;
                }
            }
        } else {
            daño = Logica.enteroAleatorio(getGolpe().getMin(), getGolpe().getMax());
        }

        emitirSonido(Sonidos.SND_IMPACTO);
        getStamina().disminuir(Balance.COMBATE_ENERGIA_NECESARIA);
        getConexion().enviarUsuarioStats();
        if (victima instanceof Personaje) {
            Personaje pv = (Personaje) victima;
            Servidor.getServidor().todosArea(pv.getCoordenada(), 10, (charindex, conexion) -> {
                conexion.enviarPersonajeAnimacion(pv.getCharindex(), 14, 2);
            });
        }

        // Le provocamos daño a la victima
        ParteCuerpo lugar = ParteCuerpo.alAzar();
        int dañoFinal = victima.recibeDañoFisico(this, lugar, daño);
        enviarMensaje("§4¡¡Le has pegado a {0} en {1} por {2}!!", ((Personaje) objetivo).getNombre(), lugar.getDescripcion(), dañoFinal);

        return resultado;
    }

    @Override
    public ResultadoGolpe recibeAtaque(Atacante atacante, ResultadoGolpe resultado) {
        if (!resultado.isExito() && resultado.getCausa() == ResultadoGolpe.Causa.ATACANTE_FALLO) {
            enviarMensaje("§c¡¡{0} te atacó y falló!!", atacante.getNombre());
            return resultado;
        }

        if (getEscudo() != 0) {
            // Intento rechazar con escudo
            if (realizarHabilidad("DefensaConEscudos")) {

                enviarMensaje("§c¡¡¡Has rechazado el ataque de {0} con el escudo!!!", atacante.getNombre());
                emitirSonido(Sonidos.SND_ESCUDO);

                return new ResultadoGolpe(false, ResultadoGolpe.Causa.VICTIMA_RECHAZO_CON_ESCUDO);
            }
        }

        // Intento esquivar el ataque
        if (realizarHabilidad("TacticasDeCombate")) {
            enviarMensaje("§c¡¡{0} te atacó y falló!!", atacante.getNombre());
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
        enviarMensaje("§4¡¡{0} te ha pegado en {1} por {2}!!", atacante.getNombre(), lugar.getDescripcion(), daño);

        boolean muere = getVida().disminuir(daño);

        // Actualizamos la barra de vida
        getConexion().enviarUsuarioStats();

        if (muere) {
            // Se murio, disparamos el evento
            alMorir();
            // Disparamos el evento indicandole al atacante que nos ha matado
            atacante.alMatar(this);
        }

        return daño;
    }

    @Override
    public void alMatar(Atacable victima) {
        enviarMensaje("§4Has matado a {0}", victima.getNombre());

        if (victima instanceof Usuario) {
            // @TODO: Incrementar frags
            ganarExperiencia(((Usuario) victima).getNivel() * 2);
            return;
        }

        // @TODO: Procesar matar NPC, ganar experiencia, etc.
        ganarExperiencia(100);
    }

    /**
     * Desconectar al usuario
     */
    public void desconectar() {
        getConexion().desconectar();
    }

    /**
     * Desconectar al usuario enviando un mensaje
     *
     * @param mensaje Mensaje a enviar al usuario
     */
    public void desconectar(String mensaje) {
        getConexion().desconectar(mensaje);
    }

    /**
     * Evento que se produce cuando el usuario se conecta
     *
     * @return
     */
    public boolean alConectarse() {
        if (getCoordenada().getMapa() == 0) {
            getCoordenada().setMapa(1);
            getCoordenada().getPosicion().setX(50);
            getCoordenada().getPosicion().setY(50);
        }

        Mapa mapa = getMapaActual();
        Baldosa baldosa = mapa.getBaldosa(getCoordenada().getPosicion());

        if (baldosa.getCharindex() != 0) {
            // Ya hay alguien parado en esa posicion
            desconectar("Hay alguien parado en tu posicion, intenta luego.");
        }

        // Posicionamos al personaje en el mundo
        baldosa.setCharindex(getCharindex());

        setConectado(true);
        guardar();

        Servidor.getServidor().enviarMensajeDeDifusion("§7{0} ha ingresado al juego.", nombre);

        getConexion().enviarUsuarioNombre();
        getConexion().enviarUsuarioCambiaMapa();
        getConexion().enviarUsuarioPosicion();
        getConexion().enviarUsuarioExperiencia();
        getConexion().enviarUsuarioStats();
        getConexion().usuarioInventarioActualizar();

        // Avisamos a los clientes conectados que dibujen este personaje
        Servidor.getServidor().todosMenosUsuarioArea(this, (u, conexion) -> {
            conexion.enviarPersonajeCrear(
                    getCharindex(),
                    getOrientacion().valor(),
                    getCoordenada().getPosicion().getX(),
                    getCoordenada().getPosicion().getY(),
                    getCuerpo(),
                    getCabeza(),
                    getArma(),
                    getEscudo(),
                    getCasco());
        });

        // Le indicamos al cliente de este usuario que dibuje los otros personajes en el area
        for (ConexionConCliente conn : Servidor.getServidor().getConexiones()) {
            Usuario u = conn.getUsuario();
            try {
                getConexion().enviarPersonajeCrear(
                        getCharindex() == u.getCharindex() ? 1 : u.getCharindex(),
                        u.getOrientacion().valor(),
                        u.getCoordenada().getPosicion().getX(),
                        u.getCoordenada().getPosicion().getY(),
                        u.getCuerpo(),
                        u.getCabeza(),
                        u.getArma(),
                        u.getEscudo(),
                        u.getCasco());
            } catch (Exception ex) {
                LOGGER.fatal(null, ex);
            }
        }

        // Enviamos los objetos que hay en el mapa
        for (Map.Entry<Posicion, Objeto> entry : mapa.getObjetos().entrySet()) {
            getConexion().enviarMundoObjeto(
                    entry.getKey().getX(),
                    entry.getKey().getY(),
                    entry.getValue());
        }

        return true;
    }

    /**
     * Evento que se produce cuando el usuario se deconecta
     */
    public void alDesconectarse() {
        if (!isConectado()) {
            // Si el usuario no estaba conectado no tenemos que hacer nada mas.
            return;
        }
        setConectado(false);
        guardar();

        Servidor.getServidor().enviarMensajeDeDifusion("§7{0} se ha desconectado del juego.", getNombre());
        Mapa mapa = Servidor.getServidor().getMapa(getCoordenada().getMapa());

        // Eliminamos el personaje del mundo
        mapa.getBaldosa(getCoordenada().getPosicion()).setCharindex(0);
        
        // Eliminamos el personaje de la lista de personajes
        Servidor.getServidor().eliminarPersonaje(this);

        // Le avisamos a los otros clientes que eliminen el personaje que le corresponde a este usuario
        Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
            conexion.enviarPersonajeQuitar(getCharindex());
        });
    }

    /**
     * Devuelve la instancia de la conexion con el cliente de este usuario
     *
     * @return
     */
    @JsonIgnore
    public ConexionConCliente getConexion() {
        return Servidor.getServidor().getConexion(this);
    }

    /**
     * /meditar
     */
    public void meditar() {
        if (meditando) {
            // El usuario ya estaba meditando
            setMeditando(false);
            return;
        }

        if (isMuerto()) {
            enviarMensaje("§7Estas muerto!! Solo puedes meditar cuando estas vivo.");
            return;
        }
        if (mana.max == 0) {
            enviarMensaje("§7Solo las clases magicas conocen el arte de la meditacion.");
            return;
        }
        // Enviamos el efecto
        setMeditando(true);
    }

    /**
     * Actualizamos todo lo que tengamos que actualizar del usuario
     */
    public void tick() {
        if (isMuerto()) {
            return;
        }

        doRecuperarVida();
        doFrio();
        doHambre();
        doSed();
        doRecuperarEnergia();

        if (isMeditando()) {
            doMeditar();
        }
    }

    /**
     * Procesar contador de frio
     */
    protected void doFrio() {
        if (!isDesnudo()) {
            // Si el personaje esta vestido, entonces no tiene frio
            return;
        }
        if (contadorFrio < Balance.FRIO_INTERVALO) {
            ++contadorFrio;
            return;
        }
        alTenerFrio();
    }

    /**
     * Evento que se produce cuando el usuario tiene frio
     */
    protected void alTenerFrio() {
        contadorFrio = 0;
        int energia = Logica.porcentaje(getStamina().getMax(), Balance.FRIO_PORCENTAJE_STAMINA);
        getStamina().disminuir(energia);
        getConexion().enviarUsuarioStats();
    }

    /**
     * Procesar contador de recupero de energia
     */
    protected void doRecuperarEnergia() {
        if (isDesnudo()) {
            // Si el personaje esta desnudo, entonces no recupera energia
            return;
        }
        if (getHambre().getMin() == 0 || getSed().getMin() == 0) {
            // Si tiene hambre o sed, entonces no recupera energia
            return;
        }
        if (contadorEnergia < Balance.ENERGIA_INTERVALO) {
            ++contadorEnergia;
            return;
        }
        alRecuperarEnergia();
    }

    /**
     * Evento que se produce cuando el usuario recupera energia
     */
    protected void alRecuperarEnergia() {
        contadorEnergia = 0;
        if (!getStamina().estaCompleto()) {
            int energia = Logica.porcentaje(getStamina().getMax(), Balance.ENERGIA_PORCENTAJE_RECUPERO);
            getStamina().aumentar(energia);
            getConexion().enviarUsuarioStats();
        }
    }

    protected void doMeditar() {
        if (mana.estaCompleto()) {
            enviarMensaje("§7Has terminado de meditar.");
            setMeditando(false);
        }

        if (realizarHabilidad("meditar")) {
            int cantidad = Logica.porcentaje(mana.getMax(), Balance.PORCENTAJE_RECUPERO_MANA);
            enviarMensaje("§bHas recuperado {0} puntos de mana!", cantidad);
            mana.aumentar(cantidad);
            getConexion().enviarUsuarioStats();
        }
    }

    /**
     * Procesar contador de recupero de vida
     */
    protected void doRecuperarVida() {
        if (getStamina().getMin() == 0) {
            // Si el personaje no tiene energia, entonces no recupera vida
            return;
        }
        if (contadorSanar < 25) { // 50
            ++contadorSanar;
            return;
        }
        alRecuperarVida();
    }

    /**
     * Evento que se produce cuando el usuario recupera vida
     */
    protected void alRecuperarVida() {
        contadorSanar = 0;
        if (!getVida().estaCompleto()) {
            int aumentoSalud = Logica.porcentaje(getVida().getMax(), 5);
            getVida().aumentar(aumentoSalud);
            getConexion().enviarUsuarioStats();
            enviarMensaje("§a¡Has sanado!");
        }
    }

    /**
     * Procesar contador de hambre
     */
    protected void doHambre() {
        if (contadorHambre < Balance.HAMBRE_INVERVALO) {
            ++contadorHambre;
            return;
        }
        alTenerHambre();
    }

    /**
     * Evento que se produce cuando tiene hambre
     */
    protected void alTenerHambre() {
        contadorHambre = 0;
        getHambre().disminuir(Balance.HAMBRE_CANTIDAD);
        getConexion().enviarUsuarioStats();
    }

    /**
     * Procesar contador de frio
     */
    protected void doSed() {
        if (contadorSed < Balance.SED_INTERVALO) {
            ++contadorSed;
            return;
        }
        alTenerSed();
    }

    /**
     * Evento que se produce cuando tiene sed
     */
    protected void alTenerSed() {
        contadorSed = 0;
        getSed().disminuir(Balance.SED_CANTIDAD);
        getConexion().enviarUsuarioStats();
    }

    /**
     * @return Habilidades del personaje
     */
    @Override
    public HashMap<String, Habilidad> getSkills() {
        return skills;
    }

    /**
     * @param skills Habilidades del personaje
     */
    @Override
    public void setSkills(HashMap<String, Habilidad> skills) {
        this.skills = skills;
    }

    /**
     * Obtener un skill del usuario
     *
     * @param nombre
     * @return
     */
    @JsonIgnore
    @Override
    public Habilidad getSkill(String nombre) {
        return skills.get(nombre);
    }

    /**
     * Aprender una nueva habilidad
     *
     * @param id Identificador de la habilidad
     * @return Instancia de la habilidad aprendida
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @Override
    public Habilidad aprenderHabilidad(String id) throws InstantiationException, IllegalAccessException {
        final Habilidad skill = Habilidades.crear(Habilidades.Soportadas.para(id));
        enviarMensaje("§bHas aprendido la habilidad de " + skill.getNombre());
        enviarMensaje(skill.getDescripcion());
        skills.put(id, skill);
        return skill;
    }

    /**
     * Entrenar una habilidad
     *
     * @param id Identificador de la habilidad
     * @param acierto Verdadero si se ha tenido suerte al realizar la habilidad
     */
    @Override
    public void entrenarHabilidad(String id, boolean acierto) {
        Habilidad skill = getSkill(id);
        if (skill == null) {
            try {
                // Todavia no habiamos aprendido esta hablidad
                skill = aprenderHabilidad(id);
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.fatal(null, ex);
                return;
            }
        }
        entrenarHabilidad(skill, acierto);
    }

    /**
     * Entrenar una habilidad
     *
     * @param skill Habilidad
     * @param acierto Verdadero si se ha tenido suerte al realizar la habilidad
     */
    @Override
    public void entrenarHabilidad(Habilidad skill, boolean acierto) {
        if (skill.entrenar(acierto)) {
            // Subimos de nivel al entrenar la habilidad
            enviarMensaje("§bHas mejorado tu skill {0} en un punto! Ahora tienes {1} pts.", skill.getNombre(), skill.getNivel());
            ganarExperiencia(Balance.calcularExperienciaGanadaAlSubirHabilidad(skill.getNivel()));
        }
    }

    /**
     * Calculamos si el usuario logra realizar una habilidad, si lo logra
     * tambien aumentamos la experiencia de la habilidad.
     *
     * @param id Identificador de la habilidad
     * @return Devuelve verdadero si el usuario ha logrado realizar la habilidad
     */
    @Override
    public boolean realizarHabilidad(String id) {
        Habilidad skill = getSkill(id);
        if (skill == null) {
            try {
                // Todavia no habiamos aprendido esta hablidad
                skill = aprenderHabilidad(id);
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.fatal(null, ex);
                return false;
            }
        }
        boolean resultado = skill.realizar(this);

        // Ganamos experiencia en nuestra habilidad
        entrenarHabilidad(skill, resultado);

        return resultado;
    }

    /**
     * @return Verdadero si el usuario es newbie
     */
    public boolean isNewbie() {
        return newbie;
    }

    /**
     * @param newbie Verdadero si el usuario es newbie
     */
    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }

    public void setInventario(ConcurrentHashMap<Integer, InventarioSlot> inventario) {
        this.inventario = inventario;
    }

    public ConcurrentHashMap<Integer, InventarioSlot> getInventario() {
        return inventario;
    }

    /**
     * Intentar equipar el objeto que se encuentra en un hueco especifico del
     * inventario
     *
     * @param invslot
     * @return
     */
    public boolean inventarioEquiparSlot(int invslot) {
        if (isMuerto()) {
            enviarMensaje("§7No puedes equiparte objetos si estas muerto.");
            return false;
        }

        if (!inventario.containsKey(invslot)) {
            // Slot invalido
            return false;
        }

        InventarioSlot slot = getInventarioSlot(invslot);

        if (slot == null) {
            return false;
        }

        // Si esta equipado lo quitamos
        if (slot.isEquipado()) {
            return inventarioDesequiparSlot(invslot);
        }

        if (slot.getObjeto().isNewbie() && !isNewbie()) {
            enviarMensaje("§7Solo los newbies pueden usar este objeto.");
            return false;
        }

        switch (slot.getObjeto().getTipo()) {
            case ARMA:
                // Si tiene otra arma equipada, entonces la desequipamos
                if (WeaponEqpObjIndex > 0) {
                    inventarioDesequiparSlot(WeaponEqpSlot);
                }

                WeaponEqpObjIndex = slot.getObjetoId();
                WeaponEqpSlot = invslot;

                // Enviamos un sonido en el area
                emitirSonido(Sonidos.SND_SACARARMA);

                setArma(((Arma) slot.getObjeto()).getAnimacionAltos());
                actualizarApariencia();
                break;
            case ESCUDO:
                // Si tiene otro escudo equipado, entonces lo desequipamos
                if (ShieldEqpObjIndex > 0) {
                    inventarioDesequiparSlot(ShieldEqpSlot);
                }

                ShieldEqpObjIndex = slot.getObjetoId();
                ShieldEqpSlot = invslot;

                setEscudo(((Equipable) slot.getObjeto()).getAnimacionAltos());
                actualizarApariencia();
                break;
            case CASCO:
                // Si tiene otro casco equipado, entonces lo desequipamos
                if (HelmEqpObjIndex > 0) {
                    inventarioDesequiparSlot(HelmEqpSlot);
                }

                HelmEqpObjIndex = slot.getObjetoId();
                HelmEqpSlot = invslot;

                setCasco(((Equipable) slot.getObjeto()).getAnimacionAltos());
                actualizarApariencia();
                break;
            case VESTIMENTA:
                // Si tiene otra ropa equipada, entonces la desequipamos
                if (ArmorEqpObjIndex > 0) {
                    inventarioDesequiparSlot(ArmorEqpSlot);
                }

                ArmorEqpObjIndex = slot.getObjetoId();
                ArmorEqpSlot = invslot;

                setCuerpo(((Vestimenta) slot.getObjeto()).getRopaje());
                setDesnudo(false);
                actualizarApariencia();
                break;
            default:
                return false;
        }

        // Si llegamos hasta aca es porque logramos equipar el objeto
        slot.setEquipado(true);
        getConexion().usuarioInventarioActualizarSlot(invslot);
        return true;
    }

    public boolean inventarioDesequiparSlot(int invslot) {
        if (!inventario.containsKey(invslot)) {
            // Slot invalido
            return false;
        }

        InventarioSlot slot = getInventarioSlot(invslot);

        if (slot == null) {
            return false;
        }

        switch (slot.getObjeto().getTipo()) {
            case ARMA:
                WeaponEqpSlot = 0;
                WeaponEqpObjIndex = 0;
                slot.setEquipado(false);
                if (!mimetizado) {
                    setArma(0);
                    actualizarApariencia();
                }
                break;
            case ESCUDO:
                ShieldEqpSlot = 0;
                ShieldEqpObjIndex = 0;
                slot.setEquipado(false);
                if (!mimetizado) {
                    setEscudo(0);
                    actualizarApariencia();
                }
                break;
            case CASCO:
                HelmEqpSlot = 0;
                HelmEqpObjIndex = 0;
                slot.setEquipado(false);
                if (!mimetizado) {
                    setCasco(0);
                    actualizarApariencia();
                }
                break;
            case VESTIMENTA:
                ArmorEqpSlot = 0;
                ArmorEqpObjIndex = 0;
                setDesnudo(true);
                slot.setEquipado(false);
                if (!mimetizado) {
                    int cuerpoDesnudo = Servidor.getServidor().getRaza(getRaza()).getCuerpo(getGenero());
                    setCuerpo(cuerpoDesnudo);
                    actualizarApariencia();
                }
                break;
        }

        getConexion().usuarioInventarioActualizarSlot(invslot);
        return true;
    }

    /**
     * Utilizar un objeto del inventario
     *
     * @param invslot ID del hueco
     */
    public boolean inventarioUsarItem(int invslot) {
        if (isMuerto()) {
            enviarMensaje("§7Estas muerto!! Solo puedes usar items cuando estas vivo.");
            return false;
        }

        InventarioSlot slot = getInventarioSlot(invslot);

        if (slot == null) {
            return false;
        }

        ObjetoMetadata obj = slot.getObjeto();

        if (obj == null) {
            return false;
        }

        switch (obj.getTipo()) {
            case ALIMENTO:
                if (inventarioQuitarObjeto(invslot, 1)) {
                    Comestible comida = (Comestible) obj;

                    getHambre().aumentar(Logica.enteroAleatorio(comida.getMinHambre(), comida.getMaxHambre()));
                    getConexion().enviarUsuarioStats();

                    if (obj.getId() == 1 || obj.getId() == 64 || obj.getId() == 506) {
                        emitirSonido(Sonidos.SND_COMIDA_2);
                    } else {
                        emitirSonido(Sonidos.SND_COMIDA_1);
                    }
                    return true;
                }
                break;

            case BEBIDA:
                if (inventarioQuitarObjeto(invslot, 1)) {
                    Bebida bebida = (Bebida) obj;

                    getSed().aumentar(Logica.enteroAleatorio(bebida.getMinSed(), bebida.getMaxSed()));
                    getConexion().enviarUsuarioStats();
                    emitirSonido(Sonidos.SND_BEBER);

                    return true;
                }
                break;

            case POCION:
                if (inventarioQuitarObjeto(invslot, 1)) {
                    Pocion pocion = (Pocion) obj;

                    switch (pocion.getTipoPocion()) {
                        case NEGRA:
                            // La negra te mata!!
                            matar();
                            break;
                        case AUMENTA_VIDA:
                            int aumentoVida = Logica.enteroAleatorio(pocion.getMinModificador(), pocion.getMaxModificador());
                            getVida().aumentar(aumentoVida);
                            break;
                        case AUMENTA_MANA:
                            int aumentoMana = Logica.enteroAleatorio(pocion.getMinModificador(), pocion.getMaxModificador());
                            getMana().aumentar(aumentoMana);
                            break;
                    }

                    getConexion().enviarUsuarioStats();
                    emitirSonido(Sonidos.SND_BEBER);

                    return true;
                }
                break;
        }

        return false;
    }

    /**
     * Utilizar un objeto del inventario
     *
     * @param invslot ID del hueco
     * @param cantidad
     * @return Verdadero si se ha logrado arrojar el item al suelo
     */
    public boolean inventarioTirarObjeto(int invslot, int cantidad) {
        InventarioSlot slot = getInventarioSlot(invslot);

        if (slot == null) {
            return false;
        }

        ObjetoMetadata meta = slot.getObjeto();

        if (meta == null) {
            return false;
        }

        if (slot.isEquipado()) {
            inventarioDesequiparSlot(invslot);
        }

        int cantActual = slot.getCantidad();
        int arrojar = cantidad;

        if (cantActual < cantidad) {
            arrojar = cantActual;
        }

        Mapa mapa = getMapaActual();

        if (mapa.hayObjeto(getCoordenada().getPosicion())) {
            // Ya hay un objeto en esta posicion, no podemos arrojas uno nuevo
            return false;
        }

        slot.setCantidad(cantActual - arrojar);
        if (slot.getCantidad() == 0) {
            inventario.remove(invslot);
        }
        getConexion().usuarioInventarioActualizarSlot(invslot);

        Objeto obj = new Objeto(meta.getId(), arrojar);
        mapa.setObjeto(getCoordenada().getPosicion(), obj);

        return false;
    }

    public boolean inventarioAgarrarObjeto() {
        if (isMuerto()) {
            // Si esta muerto no puede agarrar objetos.
            return false;
        }

        Mapa mapa = getMapaActual();
        Posicion pos = getCoordenada().getPosicion();

        if (!mapa.hayObjeto(pos)) {
            // No hay ningun objeto para agarrar
            enviarMensaje("§7No hay nada aqui.");
            return false;
        }

        Objeto obj = mapa.getObjeto(pos);

        if (!obj.getMetadata().isAgarrable()) {
            // El objeto no se puede agarrar
            enviarMensaje("§7No puedes agarrar este objeto.");
            return false;
        }

        if (obj.getMetadata().getTipo() == ObjetoTipo.DINERO) {
            // Las monedas las agregamos directamente a la billetera
            agregarDinero(obj.getCantidad());
            mapa.quitarObjeto(pos);
            return true;
        }

        // Intentamos meter el objeto en el inventario
        if (!inventarioMeterObjeto(obj)) {
            enviarMensaje("§cNo puedes cargar mas objetos.");
            return false;
        }

        mapa.quitarObjeto(pos);
        return true;
    }

    public boolean inventarioMeterObjeto(Objeto obj) {
        // Ya tenemos un objeto del mismo tipo?
        for (Map.Entry<Integer, InventarioSlot> entry : inventario.entrySet()) {
            if (entry.getValue().getObjetoId() == obj.getId()) {
                InventarioSlot slot = entry.getValue();
                if (slot.getCantidad() + obj.getCantidad() <= slot.getObjeto().getMaxItems()) {
                    // Entra el objeto en el pilon existente
                    slot.setCantidad(slot.getCantidad() + obj.cantidad);
                    getConexion().usuarioInventarioActualizarSlot(entry.getKey());
                    return true;
                }
            }
        }

        // Buscamos un slot libre
        for (int i = 0; i < inventarioCantSlots; ++i) {
            if (!inventario.containsKey(i)) {
                // Encontramos un slot vacio
                InventarioSlot slot = new InventarioSlot();
                slot.setCantidad(obj.cantidad);
                slot.setObjetoId(obj.getId());
                slot.setEquipado(false);
                inventario.put(i, slot);
                getConexion().usuarioInventarioActualizarSlot(i);
                return true;
            }
        }

        // No tenemos lugar donde poner el objeto
        return false;
    }

    /**
     * Eliminamos un objeto del inventario, si esta equipado lo desequipamos
     *
     * @param invslot
     * @return Verdadero si se ha eliminado un objeto correctamente
     */
    public boolean inventarioQuitarObjeto(int invslot) {
        if (!inventario.containsKey(invslot)) {
            return false;
        }
        InventarioSlot slot = inventario.get(invslot);
        if (slot.isEquipado()) {
            // Si esta equipado, primero lo tenemos que desequipar
            inventarioDesequiparSlot(invslot);
        }
        inventario.remove(invslot);
        getConexion().usuarioInventarioActualizarSlot(invslot);
        return true;
    }

    /**
     * Intentamos eliminar una cantidad especifica de objetos del inventario.
     *
     * @param invslot
     * @param cantidad
     * @return Verdadero si se ha eliminado la cantidad de objetos deseada
     */
    public boolean inventarioQuitarObjeto(int invslot, int cantidad) {
        if (!inventario.containsKey(invslot)) {
            return false;
        }

        InventarioSlot slot = inventario.get(invslot);

        if (slot.getCantidad() < cantidad) {
            // No tenemos suficiente cantidad
            return false;
        }

        // Reducimos la cantidad
        slot.setCantidad(slot.getCantidad() - cantidad);

        if (slot.getCantidad() <= 0) {
            // Nos quedams sin objetos, eliminaremos el slot directamente
            return inventarioQuitarObjeto(invslot);
        }

        getConexion().usuarioInventarioActualizarSlot(invslot);
        return true;
    }

    /**
     * Envia a todos los clientes cercanos el cambio de apariencia sobre el
     * personaje del usuario
     */
    @Override
    public void actualizarApariencia() {
        for (ConexionConCliente conn : Servidor.getServidor().getConexiones()) {
            conn.enviarPersonajeCambiar(
                    getCharindex() == conn.getUsuario().getCharindex() ? 1 : getCharindex(),
                    getOrientacion().valor(),
                    getCuerpo(),
                    getCabeza(),
                    getArma(),
                    getEscudo(),
                    getCasco());
        }
    }

    @Override
    public void matar() {
        vida.setMin(0);
        getConexion().enviarUsuarioStats();
        // Evento
        alMorir();
    }

    public boolean alMorir() {
        // @TODO: Disparar evento cancelable
        setMuerto(true);
        enviarMensaje("§4Has muerto!");
        // @TODO: Arrojar items al suelo
        // Desequipamos todo
        for (Map.Entry<Integer, InventarioSlot> entry : inventario.entrySet()) {
            inventarioDesequiparSlot(entry.getKey());
        }

        // Enviamos el sonido (Esta parte es la mejor)
        emitirSonido("MUJER".equals(getGenero()) ? Sonidos.SND_MUERTE_MUJER : Sonidos.SND_MUERTE_HOMBRE);

        // Damos la apariencia de fantasma
        setCuerpo(8);
        setCabeza(500);
        actualizarApariencia();
        return true;
    }

    /**
     * @return Mapa con atributos del usuario
     */
    public HashMap<String, Integer> getAtributos() {
        return atributos;
    }

    /**
     * @param atributos Mapa con atributos del usuario
     */
    public void setAtributos(HashMap<String, Integer> atributos) {
        this.atributos = atributos;
    }

    /**
     * @return the raza
     */
    public String getRaza() {
        return raza;
    }

    /**
     * @param raza the raza to set
     */
    public void setRaza(String raza) {
        this.raza = raza;
    }

    /**
     * @return the clase
     */
    public String getClase() {
        return clase;
    }

    /**
     * @param clase the clase to set
     */
    public void setClase(String clase) {
        this.clase = clase;
    }

    /**
     * @return the desnudo
     */
    public boolean isDesnudo() {
        return desnudo;
    }

    /**
     * @param desnudo the desnudo to set
     */
    public void setDesnudo(boolean desnudo) {
        this.desnudo = desnudo;
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void agregarDinero(int cantidad) {
        this.setDinero(this.getDinero() + cantidad);
    }

    /**
     * @return the dinero
     */
    public int getDinero() {
        return dinero;
    }

    /**
     * @param dinero the dinero to set
     */
    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    private boolean doNavegar() {
//        Posicion siguientePaso = Logica.calcularPaso(getCoordenada().getPosicion(), getOrientacion());
//        if (siguientePaso == null) {
//            return false;
//        }
//        Mapa mapa = Servidor.getServidor().getMapa(getCoordenada().getMapa());
//        if (mapa == null) {
//            return false;
//        }
//        Baldosa baldosa = mapa.getBaldosa(siguientePaso);
//        if (baldosa == null) {
//            return false;
//        }
//        if (isNavegando()) {
//            if (baldosa.isAgua()) {
//                enviarMensaje("No te podes bajar del barco en el agua.");
//                return false;
//            }
//            
//        }
        return false;
    }

    public void revivir() {
        if (!isMuerto()) {
            // Si no esta muerto, entonces no hacemos nada.
            return;
        }
        setMuerto(false);
        getVida().setMin(1);
        getConexion().enviarUsuarioStats();

        setCuerpo(Servidor.getServidor().getRaza(getRaza()).getCuerpo(getGenero()));
        setCabeza(getCabezaOriginal());
        actualizarApariencia();

        emitirSonido(Sonidos.SND_RESUCITAR);
        enviarMensaje("§3Has sido revivido!");
    }

    public int getCabezaOriginal() {
        return cabezaOriginal;
    }

    public void setCabezaOriginal(int cabezaOriginal) {
        this.cabezaOriginal = cabezaOriginal;
    }
}
