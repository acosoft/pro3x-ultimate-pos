/*
 * Pro3x Community project
 * Copyright (C) 2009  Aleksandar Zgonjan
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, http://www.gnu.org/licenses/gpl.html
 * and open the template in the editor.
 */

package Pro3x.Live;

import Acosoft.Processing.DataBox.Dobavljac;
import java.util.LinkedList;
import java.util.List;


public class AkcijeDobavljaca
{
    private static List<SlusateljDobavljaca> slusatelji = new LinkedList<SlusateljDobavljaca>();

    public static void dodajSlusatelja(SlusateljDobavljaca slusatelj)
    {
        slusatelji.add(slusatelj);
    }

    public static void ukloniSlusatelja(SlusateljDobavljaca slusatelj)
    {
        slusatelji.remove(slusatelj);
    }

    public static void fireKreiranDobavljac(Object source, Dobavljac dobavljac)
    {
        for(SlusateljDobavljaca slusatelj : slusatelji)
            slusatelj.KreiranDobavljac(source, dobavljac);
    }

    public static void fireIzbrisanDobavljac(Object source, Dobavljac dobavljac)
    {
        for(SlusateljDobavljaca slusatelj : slusatelji)
            slusatelj.IzbrisanDobavljac(source, dobavljac);
    }

    public static void fireIzmjenjenDobavljac(Object source, Dobavljac dobavljac)
    {
        for(SlusateljDobavljaca slusatelj : slusatelji)
            slusatelj.IzmjenjenDobavljac(source, dobavljac);
    }
}




