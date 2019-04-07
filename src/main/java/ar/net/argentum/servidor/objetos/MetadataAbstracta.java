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
package ar.net.argentum.servidor.objetos;

import ar.net.argentum.servidor.ObjetoMetadata;
import ar.net.argentum.servidor.ObjetoTipo;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * Metadata de un objeto
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class MetadataAbstracta implements ObjetoMetadata {

    protected int id;
    protected String nombre;
    protected ObjetoTipo tipo;
    protected int grhIndex;
    protected int grhSecundario;
    protected int maxItems;
    protected boolean newbie;
    protected boolean agarrable = true;

    public MetadataAbstracta(int id, JSONObject data) {
        this.id = id;
        this.tipo = ObjetoTipo.valueOf(Integer.valueOf(data.getString("ObjType")));
        this.nombre = data.getString("Name");
        this.grhIndex = data.getInt("GrhIndex");
        this.maxItems = 10000;
        this.agarrable = !data.has("Agarrable") || data.getString("Agarrable").equals("0");
        // El objeto es newbie?
        this.newbie = data.has("Newbie") && data.getString("Newbie").equals("1");
    }

    public MetadataAbstracta(int id, String nombre, ObjetoTipo tipo, int grhIndex, int grhSecundario, int maxItems) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.grhIndex = grhIndex;
        this.grhSecundario = grhSecundario;
        this.maxItems = maxItems;
    }

    public MetadataAbstracta(ObjetoMetadata original) {
        this.id = original.getId();
        this.nombre = original.getNombre();
        this.tipo = original.getTipo();
        this.grhIndex = original.getGrhIndex();
        this.grhSecundario = original.getGrhSecundario();
        this.maxItems = original.getMaxItems();
    }

    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @return the nombre
     */
    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the tipo
     */
    @Override
    public ObjetoTipo getTipo() {
        return tipo;
    }

    /**
     * @return the grhIndex
     */
    @Override
    public int getGrhIndex() {
        return grhIndex;
    }

    /**
     * @return the maxItems
     */
    @Override
    public int getMaxItems() {
        return maxItems;
    }

    /**
     * @return Verdadero si el objeto es NEWBIE
     */
    @Override
    public boolean isNewbie() {
        return newbie;
    }

    /**
     * @param newbie Verdadero si el objeto es NEWBIE
     */
    @Override
    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }

    /**
     * @return Verdadero si el objeto puede ser agarrado
     */
    @Override
    public boolean isAgarrable() {
        return agarrable;
    }

    /**
     * @param agarrable Verdadero si el objeto puede ser agarrado
     */
    @Override
    public void setAgarrable(boolean agarrable) {
        this.agarrable = agarrable;
    }

    @Override
    public int getGrhSecundario() {
        return grhSecundario;
    }

    @Override
    public void setGrhSecundario(int grafico) {
        this.grhSecundario = grafico;
    }

    @Override
    public ObjetoMetadata copiar() {
        try {
            Class<?> clase = tipo.getClase();
            return (ObjetoMetadata) clase
                    .getDeclaredConstructor(clase)
                    .newInstance(this);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MetadataAbstracta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
