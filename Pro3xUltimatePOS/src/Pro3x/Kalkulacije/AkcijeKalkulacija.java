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

package Pro3x.Kalkulacije;

import Pro3x.Kalkulacije.Model.Kalkulacija;
import java.util.LinkedList;


public class AkcijeKalkulacija
{
    private static LinkedList<SlusateljKalkulacija> slusatelji = new LinkedList<SlusateljKalkulacija>();

    public static void DodajSlusatelja(SlusateljKalkulacija slusatelj)
    {
        slusatelji.add(slusatelj);
    }

    public static void UkloniSlusatelja(SlusateljKalkulacija slusatelj)
    {
        slusatelji.remove(slusatelj);
    }

    public static void IzbrisanaKalkulacija(Object izvor, Kalkulacija kalkulacija)
    {
        for(SlusateljKalkulacija slusatelj : slusatelji)
            slusatelj.IzbrisanaKalkulacija(izvor, kalkulacija);
    }

    public static void IzmjenjenaKalkulacija(Object izvor, Kalkulacija kalkulacija)
    {
        for(SlusateljKalkulacija slusatelj : slusatelji)
            slusatelj.IzmjenjenaKalkulacija(izvor, kalkulacija);
    }

    public static void KreiranaKalkulacija(Object izvor, Kalkulacija kalkulacija)
    {
        for(SlusateljKalkulacija slusatelj : slusatelji)
            slusatelj.KreiranaKalkulacija(izvor, kalkulacija);
    }
}

