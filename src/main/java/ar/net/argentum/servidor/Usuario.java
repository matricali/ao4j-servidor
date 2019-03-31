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
import ar.net.argentum.servidor.mundo.Orientacion;
import ar.net.argentum.servidor.protocolo.ConexionConCliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Representa un juegador
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Usuario extends Personaje implements Atacable, GanaExperiencia {

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
    protected HashMap<Integer, InventarioSlot> inventario;
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

    public Usuario() {
        this.userindex = Servidor.crearUserindex();
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
        enviarMensaje("Has ganado {0} puntos de experiencia!", exp);
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
        enviarMensaje("Has subido de nivel!");
        getConexion().enviarMundoReproducirSonido(Sonidos.SND_NIVEL);
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
            int efecto = 6;

            // Le avisamos al cliente que inicie la animacion de meditar
            getConexion().enviarPersonajeAnimacion(1, efecto, -1);
            // Le avisamos a los otros clientes que inicien la animacion sobre el personaje
            Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
                conexion.enviarPersonajeAnimacion(getCharindex(), efecto, -1);
            });
        } else {
            enviarMensaje("Dejas de meditar.");
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
    public void enviarMensaje(String mensaje) {
        getConexion().enviarMensaje(mensaje);
    }

    /**
     * Enviar un mensaje al usuario con formato
     *
     * @param mensaje
     * @param args
     */
    public void enviarMensaje(String mensaje, Object... args) {
        enviarMensaje(MessageFormat.format(mensaje, args));
    }

    /**
     * El usuario realiza un golpe de ataque
     *
     * @return
     */
    public boolean golpea() {
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
            if ("MUJER".equals(getGenero())) {
                enviarMensaje("Estas muy cansada para luchar.");
                return false;
            }
            enviarMensaje("Estas muy cansado para luchar.");
            return false;
        }

        Posicion nuevaPosicion = Logica.calcularPaso(getCoordenada().getPosicion(), orientacion);
        Mapa m = Servidor.getServidor().getMapa(getCoordenada().getMapa());
        Baldosa b = m.getBaldosa(nuevaPosicion);

        if (b.getCharindex() == 0) {
            // Arrojamos un golpe al aire, enviamos el sonido y disminuimos la energia
            getConexion().enviarMundoReproducirSonido(Sonidos.SND_SWING);
            getStamina().disminuir(Balance.COMBATE_ENERGIA_NECESARIA);
            getConexion().enviarUsuarioStats();
            return true;
        }

        if (b.getCharindex() == getCharindex()) {
            enviarMensaje("No podes atacarte a vos mismo.");
            return false;
        }

        Personaje victima = Servidor.getServidor().getPersonaje(charindex);

        enviarMensaje("Le pegaste a " + victima.getNombre());
        getStamina().disminuir(Balance.COMBATE_ENERGIA_NECESARIA);
        getConexion().enviarUsuarioStats();

//        // Le avisamos a los otros clientes que el usuario se movio
//        Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
//            conexion.enviarPersonajeCaminar(charindex, orientacion.valor());
//        });
        return true;
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

        Mapa mapa = Servidor.getServidor().getMapa(getCoordenada().getMapa());
        Baldosa baldosa = mapa.getBaldosa(getCoordenada().getPosicion());

        if (baldosa.getCharindex() != 0) {
            // Ya hay alguien parado en esa posicion
            desconectar("Hay alguien parado en tu posicion, intenta luego.");
        }

        // Posicionamos al personaje en el mundo
        baldosa.setCharindex(getCharindex());

        setConectado(true);
        guardar();

        Servidor.getServidor().enviarMensajeDeDifusion("\u00a78{0} ha ingresado al juego.", nombre);

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

        Servidor.getServidor().enviarMensajeDeDifusion("\u00a78{0} se ha desconectado del juego.", getNombre());

        Mapa mapa = Servidor.getServidor().getMapa(getCoordenada().getMapa());

        // Eliminamos el personaje del mundo
        mapa.getBaldosa(getCoordenada().getPosicion()).setCharindex(0);

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
            enviarMensaje("Estas muerto!! Solo puedes meditar cuando estas vivo.");
            return;
        }
        if (mana.max == 0) {
            enviarMensaje("Solo las clases magicas conocen el arte de la meditacion.");
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

        doFrio();
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
            enviarMensaje("Has terminado de meditar.");
            setMeditando(false);
        }

        if (realizarHabilidad("meditar")) {
            int cantidad = Logica.porcentaje(mana.getMax(), Balance.PORCENTAJE_RECUPERO_MANA);
            enviarMensaje("Has recuperado {0} puntos de mana!", cantidad);
            mana.aumentar(cantidad);
            getConexion().enviarUsuarioStats();
        }
    }

    /**
     * @return Habilidades del personaje
     */
    public HashMap<String, Habilidad> getSkills() {
        return skills;
    }

    /**
     * @param skills Habilidades del personaje
     */
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
    public Habilidad aprenderHabilidad(String id) throws InstantiationException, IllegalAccessException {
        final Habilidad skill = Habilidades.crear(Habilidades.Soportadas.para(id));
        enviarMensaje("Has aprendido la habilidad de " + skill.getNombre());
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

    private void entrenarHabilidad(Habilidad skill, boolean acierto) {
        if (skill.entrenar(acierto)) {
            // Subimos de nivel al entrenar la habilidad
            enviarMensaje("Has mejorado tu skill {0} en un punto! Ahora tienes {1} pts.", skill.getNombre(), skill.getNivel());
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
        boolean resultado = skill.realizar();

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

    public void setInventario(HashMap<Integer, InventarioSlot> inventario) {
        this.inventario = inventario;
    }

    public HashMap<Integer, InventarioSlot> getInventario() {
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
            enviarMensaje("No puedes equiparte objetos si estas muerto.");
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
            enviarMensaje("Solo los newbies pueden usar este objeto.");
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
                Servidor.getServidor().todosArea(getCoordenada(), 30, (usuario, conexion) -> {
                    conexion.enviarMundoReproducirSonido(Sonidos.SND_SACARARMA, getCoordenada().getPosicion());
                });

                setArma(slot.getObjeto().getAnimacion());
                actualizarApariencia();
                break;
            case ESCUDO:
                // Si tiene otro escudo equipado, entonces lo desequipamos
                if (ShieldEqpObjIndex > 0) {
                    inventarioDesequiparSlot(ShieldEqpSlot);
                }

                ShieldEqpObjIndex = slot.getObjetoId();
                ShieldEqpSlot = invslot;

                setEscudo(slot.getObjeto().getAnimacion());
                actualizarApariencia();
                break;
            case CASCO:
                // Si tiene otro casco equipado, entonces lo desequipamos
                if (HelmEqpObjIndex > 0) {
                    inventarioDesequiparSlot(HelmEqpSlot);
                }

                HelmEqpObjIndex = slot.getObjetoId();
                HelmEqpSlot = invslot;

                setCasco(slot.getObjeto().getAnimacion());
                actualizarApariencia();
                break;
            case VESTIMENTA:
                // Si tiene otra ropa equipada, entonces la desequipamos
                if (ArmorEqpObjIndex > 0) {
                    inventarioDesequiparSlot(ArmorEqpSlot);
                }

                ArmorEqpObjIndex = slot.getObjetoId();
                ArmorEqpSlot = invslot;

                setCuerpo(slot.getObjeto().getRopaje());
                setDesnudo(false);
                actualizarApariencia();
                break;
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
                    int cuerpoDesnudo = Servidor.getServidor().getRaza(getRaza()).getCuerpo();
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
        InventarioSlot slot = getInventarioSlot(invslot);

        if (slot == null) {
            return false;
        }

        ObjetoMetadata obj = slot.getObjeto();

        if (obj == null) {
            return false;
        }

        switch (obj.getTipo()) {

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
            enviarMensaje("No hay nada aqui.");
            return false;
        }

        Objeto obj = mapa.getObjeto(pos);

        if (!obj.getMetadata().isAgarrable()) {
            // El objeto no se puede agarrar
            enviarMensaje("No puedes agarrar este objeto.");
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
            enviarMensaje("No puedes cargar mas objetos.");
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
     * Envia a todos los clientes cercanos el cambio de apariencia sobre el
     * personaje del usuario
     */
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

    public void alMorir() {
        enviarMensaje("Has muerto!");
        // @TODO: Dar cuerpo de fantasmita
        // @TODO: Arrojar items al suelo
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

    @Override
    public boolean recibeAtaque(Personaje atacante) {
        return false;
    }
}
