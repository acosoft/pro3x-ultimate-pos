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

import Acosoft.Processing.DataBox.Roba;
import javax.swing.SwingUtilities;


public class ArtikalEvents
{
    private static Event events = new Event();

    public static Event Events()
    {
        return events;
    }

    public static enum ArtikalEventType
    {
        Kreiran, Izbrisan, Izmjenjen
    }

    public static class ArtikalEventArgs implements EventArgs
    {
        private ArtikalEventType type;
        private Roba staraRoba;
        private Roba novaRoba;

        public ArtikalEventArgs(ArtikalEventType type, Roba novaRoba, Roba staraRoba)
        {
            this.type = type;
            this.staraRoba = staraRoba;
            this.novaRoba = novaRoba;
        }

        public ArtikalEventType getEventType()
        {
            return type;
        }

        public Roba getStaraRoba()
        {
            return staraRoba;
        }

        public Roba getNovaRoba()
        {
            return novaRoba;
        }
    }

    private static class RunEvent implements Runnable
    {
        private Object source;
        private Roba roba;

        public RunEvent(Object source, Roba roba)
        {
            this.source = source;
            this.roba = roba;
        }

        public void run()
        {
            events.fire(source, new ArtikalEventArgs(ArtikalEventType.Kreiran, roba, null));
        }
    }

    public static void fireKreiranArtikal(Object source, Roba novaRoba)
    {
        if(SwingUtilities.isEventDispatchThread())
            events.fire(source, new ArtikalEventArgs(ArtikalEventType.Kreiran, novaRoba, null));
        else
            SwingUtilities.invokeLater(new RunEvent(source, novaRoba));
    }

    public static void fireIzbrisanArtikal(Object source, Roba staraRoba)
    {
        events.fire(source, new ArtikalEventArgs(ArtikalEventType.Izbrisan, null, staraRoba));
    }

    public static void fireIzmjenjenArtikal(Object source, Roba novaRoba, Roba staraRoba)
    {
        events.fire(source, new ArtikalEventArgs(ArtikalEventType.Izmjenjen, novaRoba, staraRoba));
    }
}
