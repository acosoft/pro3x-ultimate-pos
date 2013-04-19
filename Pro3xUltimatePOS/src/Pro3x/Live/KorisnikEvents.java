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

import Acosoft.Processing.DataBox.Korisnik;

public class KorisnikEvents
{
    public static enum EventType { Kreiran, Izbrisan, Izmjenjen };

    public static class KorisnikEventArgs implements EventArgs
    {
        public KorisnikEventArgs(EventType type, Korisnik novi, Korisnik stari)
        {
            this.type = type;
            this.novi = novi;
            this.stari = stari;
        }        

        private EventType type;
        public EventType getType()
        {
            return type;
        }

        private Korisnik stari;
        public Korisnik getStariKorisnik()
        {
            return stari;
        }

        private Korisnik novi;
        public Korisnik getNoviKorisnik()
        {
            return novi;
        }
    }

    public static void fireKreiranKorisnik(Object source, Korisnik novi)
    {
        events.fire(source, new KorisnikEventArgs(EventType.Kreiran, novi, null));
    }

    public static void fireIzmjenenKorisnik(Object source, Korisnik novi, Korisnik stari)
    {
        events.fire(source, new KorisnikEventArgs(EventType.Izmjenjen, novi, stari));
    }

    public static void fireIzbrisanKorisnik(Object source, Korisnik stari)
    {
        events.fire(source, new KorisnikEventArgs(EventType.Izbrisan, null, stari));
    }

    private static Event events = new Event();

    public static Event Events()
    {
        return events;
    }
}
