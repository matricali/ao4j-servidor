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
import ar.net.argentum.servidor.mundo.Personaje;
import ar.net.argentum.servidor.protocolo.ConexionConCliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Usuario implements Atacable {

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

    @JsonProperty
    protected String nombre;
    @JsonProperty
    protected String password;
    @JsonProperty
    protected boolean conectado;
    @JsonProperty
    protected Coordenada coordenada;
    @JsonProperty
    protected Map<Integer, InventarioSlot> inventario;
    @JsonProperty
    protected int cuerpo;
    @JsonProperty
    protected int cabeza;
    @JsonProperty
    protected int arma;
    @JsonProperty
    protected int escudo;
    @JsonProperty
    protected int casco;
    // Estadisticas
    @JsonProperty
    protected MinMax vida = new MinMax();
    @JsonProperty
    protected MinMax mana = new MinMax();
    @JsonProperty
    protected MinMax stamina = new MinMax();
    @JsonProperty
    protected MinMax hambre = new MinMax();
    @JsonProperty
    protected MinMax sed = new MinMax();
    @JsonProperty
    protected Orientacion orientacion = Orientacion.SUR;
    @JsonProperty
    protected boolean paralizado = false;
    @JsonProperty
    protected boolean navegando = false;
    protected boolean meditando = false;
    protected boolean descansando = false;
    protected boolean muerto = false;
    protected final int charindex;
    protected final int userindex;
    protected HashMap<String, Habilidad> skills = new HashMap<>();

    public Usuario() {
        this.charindex = Servidor.crearCharindex();
        this.userindex = Servidor.crearUserindex();
    }

//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 53 * hash + this.charindex;
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Usuario other = (Usuario) obj;
//        if (this.charindex != other.charindex) {
//            return false;
//        }
//        return true;
//    }
    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the conectado
     */
    public boolean isConectado() {
        return conectado;
    }

    /**
     * @param conectado the conectado to set
     */
    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public void guardar() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(Usuario.getArchivo(nombre), this);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    private static File getArchivo(String nombre) {
        return new File("datos/personajes/" + nombre.toLowerCase() + ".json");
    }

    public InventarioSlot getInventarioSlot(int slot) {
        return inventario.get(slot);
    }

    public void setInventarioSlot(int slot, final InventarioSlot inv) {
        inventario.put(slot, inv);
    }
//
//    @SuppressWarnings("unchecked")
//    @JsonProperty("inventario")
//    private void unpackNested(Map<String, Object> brand) {
//        this.brandName = (String) brand.get("name");
//        Map<String, String> owner = (Map<String, String>) brand.get("owner");
//        this.ownerName = owner.get("name");
//    }

    /**
     * @return the coordenada
     */
    public Coordenada getCoordenada() {
        return coordenada;
    }

    /**
     * @return the cuerpo
     */
    public int getCuerpo() {
        return cuerpo;
    }

    /**
     * @param cuerpo the cuerpo to set
     */
    public void setCuerpo(int cuerpo) {
        this.cuerpo = cuerpo;
    }

    /**
     * @return the cabeza
     */
    public int getCabeza() {
        return cabeza;
    }

    /**
     * @param cabeza the cabeza to set
     */
    public void setCabeza(int cabeza) {
        this.cabeza = cabeza;
    }

    /**
     * @return the vida
     */
    public MinMax getVida() {
        return vida;
    }

    /**
     * @return the mana
     */
    public MinMax getMana() {
        return mana;
    }

    /**
     * @return the stamina
     */
    public MinMax getStamina() {
        return stamina;
    }

    /**
     * @return the hambre
     */
    public MinMax getHambre() {
        return hambre;
    }

    /**
     * @return the sed
     */
    public MinMax getSed() {
        return sed;
    }

    /**
     * @return the orientacion
     */
    public Orientacion getOrientacion() {
        return orientacion;
    }

    /**
     * @param orientacion the orientacion to set
     */
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    /**
     * @return the paralizado
     */
    public boolean isParalizado() {
        return paralizado;
    }

    /**
     * @param paralizado the paralizado to set
     */
    public void setParalizado(boolean paralizado) {
        this.paralizado = paralizado;
    }

    /**
     * @return the navegando
     */
    public boolean isNavegando() {
        return navegando;
    }

    /**
     * @param navegando the navegando to set
     */
    public void setNavegando(boolean navegando) {
        this.navegando = navegando;
    }
    
        /**
     * @return Devuelve verdadero si el personaje esta muerto.
     */
    public boolean isMuerto() {
        return muerto;
    }

    /**
     * @param muerto
     */
    public void setMuerto(boolean muerto) {
        this.muerto = muerto;
    }

    /**
     * @return the meditando
     */
    @JsonIgnore
    public boolean isMeditando() {
        return meditando;
    }

    /**
     * Activa o desactiva el estado de meditacion
     *
     * @param meditando
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

    /**
     * Mover el usuario en una direccion dada y enviar el mensaje al cliente de
     * todos los usuarios en el area.
     *
     * @see MoveUserChar
     *
     * @param orientacion
     */
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

        Posicion nuevaPosicion = Logica.calcularPaso(getCoordenada().getPosicion(), orientacion);
        if (!Logica.isPosicionValida(coordenada.getMapa(), nuevaPosicion.getX(), nuevaPosicion.getY(), false, true)) {
            enviarMensaje("Posicion invalida.");
            return false;
        }

        // Acualizamos la posicion del usuario
        this.coordenada.setPosicion(nuevaPosicion);

        // Le avisamos a los otros clientes que el usuario se movio
        Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
            conexion.enviarPersonajeCaminar(charindex, orientacion.valor());
        });

        return true;
    }

    /**
     * @return the charindex
     */
    @JsonIgnore
    public int getCharindex() {
        return charindex;
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

    /**
     * @param coordenada the coordenada to set
     */
    public void setCoordenada(Coordenada coordenada) {
        this.coordenada = coordenada;
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

        Posicion nuevaPosicion = Logica.calcularPaso(getCoordenada().getPosicion(), orientacion);
        Mapa m = Servidor.getServidor().getMapa(getCoordenada().getMapa());
        Baldosa b = m.getBaldosa(nuevaPosicion);

        if (b.getCharindex() == 0) {
            enviarMensaje("Le pegaste al aire capo");
            return true;
        }

        if (b.getCharindex() == getCharindex()) {
            enviarMensaje("No podes atacarte a vos mismo.");
            return false;
        }

        Personaje victima = Servidor.getServidor().getPersonaje(charindex);

        enviarMensaje("Le pegaste a " + victima.getNombre());

//        // Le avisamos a los otros clientes que el usuario se movio
//        Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
//            conexion.enviarPersonajeCaminar(charindex, orientacion.valor());
//        });
        return true;
    }

    @Override
    public boolean recibeAtaque(Personaje atacante) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

        getConexion().enviarUsuarioNombre();
        getConexion().enviarUsuarioCambiaMapa();
        getConexion().enviarUsuarioPosicion();
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

        return true;
    }

    /**
     * Evento que se produce cuando el usuario se deconecta
     */
    public void alDesconectarse() {
        Servidor.getServidor().enviarMensajeDeDifusion("\u00a78{0} se ha desconectado del juego.", getNombre());
        setConectado(false);
        guardar();

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
        if (meditando) {
            doMeditar();
        }
    }

    protected void doMeditar() {
        if (mana.estaCompleto()) {
            enviarMensaje("Has terminado de meditar.");
            setMeditando(false);
        }

        int MeditarSkill = 1;
        int Suerte = 0;

        if ((MeditarSkill <= 10)) {
            Suerte = 35;
        } else if ((MeditarSkill <= 20)) {
            Suerte = 30;
        } else if ((MeditarSkill <= 30)) {
            Suerte = 28;
        } else if ((MeditarSkill <= 40)) {
            Suerte = 24;
        } else if ((MeditarSkill <= 50)) {
            Suerte = 22;
        } else if ((MeditarSkill <= 60)) {
            Suerte = 20;
        } else if ((MeditarSkill <= 70)) {
            Suerte = 18;
        } else if ((MeditarSkill <= 80)) {
            Suerte = 15;
        } else if ((MeditarSkill <= 90)) {
            Suerte = 10;
        } else if ((MeditarSkill < 100)) {
            Suerte = 7;
        } else {
            Suerte = 5;
        }

        if (Logica.verdaderoAleatorio(Suerte)) {
            int cantidad = Logica.porcentaje(mana.getMax(), Balance.PORCENTAJE_RECUPERO_MANA);
            enviarMensaje("Has recuperado {0} puntos de mana!", cantidad);
            mana.aumentar(cantidad);
            getConexion().enviarUsuarioStats();
        }
        subirSkill("meditar");
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
    public Habilidad aprenderSkill(String id) throws InstantiationException, IllegalAccessException {
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
     */
    public void subirSkill(String id) {
        Habilidad skill = getSkill(id);
        if (skill == null) {
            try {
                // Todavia no habiamos aprendido esta hablidad
                skill = aprenderSkill(id);
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.fatal(null, ex);
                return;
            }
        }
        if (skill.entrenar()) {
            enviarMensaje("Has mejorado tu skill {0} en un punto! Ahora tienes {1} pts.", skill.getNombre(), skill.getNivel());
        }
    }
}
