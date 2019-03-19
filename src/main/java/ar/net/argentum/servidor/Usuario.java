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

import ar.net.argentum.servidor.mundo.Orientacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Usuario {

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

        if (usuario.getCoordenada() == null) {
            System.out.println("coordenada es null");
        } else {
            if (usuario.getCoordenada().getPosicion() == null) {
                System.out.println("posicion es null");
            } else {
                System.out.println("posicion:" + usuario.getCoordenada().getPosicion().getX() + "," + usuario.getCoordenada().getPosicion().getY());
            }
        }

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
    protected final int charindex;

    public Usuario() {
        this.charindex = Servidor.crearCharindex();
    }

     @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.nombre);
        hash = 29 * hash + this.charindex;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Usuario other = (Usuario) obj;
        if (this.charindex != other.charindex) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        return true;
    }

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
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the meditando
     */
    @JsonIgnore
    public boolean isMeditando() {
        return meditando;
    }

    /**
     * @param meditando the meditando to set
     */
    @JsonIgnore
    public void setMeditando(boolean meditando) {
        this.meditando = meditando;
        if (meditando) {
            Servidor.getServidor().getConexion(this).enviarMensaje("Has comenzado a meditar.");
        } else {
            Servidor.getServidor().getConexion(this).enviarMensaje("Dejas de meditar.");
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
            Servidor.getServidor().getConexion(this).enviarMensaje("Has comenzado a descascar.");
        } else {
            Servidor.getServidor().getConexion(this).enviarMensaje("Has dejado de descansar.");
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
    public void mover(Orientacion orientacion) {
        Posicion nuevaPosicion = Logica.calcularPaso(getCoordenada().getPosicion(), orientacion);

        if (!Logica.isPosicionValida(coordenada.getMapa(), nuevaPosicion.getX(), nuevaPosicion.getY(), false, true)) {
            Servidor.getServidor().getConexion(this).enviarUsuarioPosicion();
            return;
        }

        Servidor.getServidor().todosMenosUsuarioArea(this, (usuario, conexion) -> {
            conexion.enviarPersonajeCaminar(charindex, orientacion.valor());
        });
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
}
