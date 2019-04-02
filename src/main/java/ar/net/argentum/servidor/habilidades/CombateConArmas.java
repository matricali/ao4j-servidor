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
package ar.net.argentum.servidor.habilidades;

import ar.net.argentum.servidor.HabilidadBase;
import ar.net.argentum.servidor.Habilidoso;
import ar.net.argentum.servidor.Logica;
import ar.net.argentum.servidor.Servidor;
import ar.net.argentum.servidor.Usuario;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class CombateConArmas extends HabilidadBase {

    @Override
    public String getNombre() {
        return "Combate con armas";
    }

    @Override
    public String getDescripcion() {
        return "Puedes realizar ataques para seguir entrenando esta habilidad.";
    }

    @Override
    public boolean realizar(Habilidoso objetivo) {
        int dificultad = 1;
        float modificador = 0.5f;
        int agilidad = 10;
        float poderAtaque = 0f;

        if (objetivo instanceof Usuario) {
            Usuario usuario = (Usuario) objetivo;
            agilidad = usuario.getAtributos().get("agilidad");
            modificador = Servidor.getServidor().getClase(usuario.getClase()).getModificadores().getAtaqueArmas();

            if (nivel < 31) {
                poderAtaque = nivel * modificador;
            } else if (nivel < 61) {
                poderAtaque = nivel + agilidad * modificador;
            } else if (nivel < 91) {
                poderAtaque = nivel + 2 * agilidad * modificador;
            } else {
                poderAtaque = nivel + 3 * agilidad * modificador;
            }

            poderAtaque = poderAtaque + ((float) Math.max(getNivel() - 12, 0) * 2.5f);
            
            int probabilidadExito = (int) Math.max(10, Math.min(90, 50 + poderAtaque * 0.4));
            return (Logica.enteroAleatorio(1, 100) <= probabilidadExito);
        }

        return Logica.verdaderoAleatorio(dificultad);
    }
}
