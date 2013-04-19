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

package Acosoft.Processing.Components;

import Acosoft.Processing.DataBox.Korisnik;
import Pro3x.Live.KorisnikEvents;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;

public class RunNoviKorisnik implements Runnable
{
    private Korisnik noviKorisnik;
    private Korisnik stariKorisnik;
    private JInternalFrame source;

    public RunNoviKorisnik(JInternalFrame source, Korisnik noviKorisnik, Korisnik stariKorisnik)
    {
        this.noviKorisnik = noviKorisnik;
        this.stariKorisnik = stariKorisnik;
        this.source = source;
    }

    public void run()
    {
        if(stariKorisnik != null)
            KorisnikEvents.fireIzmjenenKorisnik(source, noviKorisnik, stariKorisnik);
        else
            KorisnikEvents.fireKreiranKorisnik(source, noviKorisnik);
        
        try
        {
            source.setClosed(true);
        }
        catch (Exception ex)
        {
            Logger.getLogger(RunNoviKorisnik.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
