// Pro3x Community project
// Copyright (C) 2009  Aleksandar Zgonjan
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, http://www.gnu.org/licenses/gpl.html

package Acosoft.Processing.Components;

import Acosoft.Processing.*;
import Pro3x.Kalkulacije.IzmjenaKalkulacije;
import Pro3x.Kalkulacije.PregledSvihKalkulacija;
import Pro3x.View.DnevniPromet;
import Pro3x.View.PregledKartice;
import Pro3x.View.PregledNeplacenihRacuna;
import Pro3x.View.PregledStanjaSkladista;
import Pro3x.View.SimpleInvoice;
import Pro3x.View.SimplePregledArtikala;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import org.jdesktop.application.SessionStorage;

/**
 *
 * @author nonstop
 */
public class DeskMeni
{
    private JDesktopPane desk;
    private JMenu izbornik;
    private static ImageIcon icon;
    private static HashMap<Class, ImageIcon> icone;

    {
        URL url = DeskMeni.class.getResource("resources/blockdevice.png");
        icon = new ImageIcon(url);
        icone = new HashMap<Class, ImageIcon>();

        icon = new ImageIcon(Pro3App.class.getResource("resources/flag.png"));
        icone.put(PregledNeplacenihRacuna.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/appointment.png"));
        icone.put(IzmjenaKalkulacije.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/rssNews.png"));
        icone.put(Pro3xRSS.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/db.png"));
        icone.put(Pro3x.View.DatabaseConfiguration.class, icon);
        
        icon = new ImageIcon(Pro3App.class.getResource("resources/printer1.png"));
        icone.put(Pro3Postavke.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/14_layer_novisible.png"));
        icone.put(Pro3x.View.PredlozakBiljeskeRacuna.class, icon);
        
        icon = new ImageIcon(Pro3App.class.getResource("resources/add_user.png"));
        icone.put(NoviKorisnik.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/folder_new.png"));
        icone.put(ProzorRoba.class, icon);
        
        icon = new ImageIcon(Pro3App.class.getResource("resources/fileopen.png"));
        icone.put(SimplePregledArtikala.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/assistant.png"));
        icone.put(Pro3x.View.IzmjenaPoreznihStopa.class, icon);


        icon = new ImageIcon(Pro3App.class.getResource("resources/7days.png"));
        icone.put(PregledSvihKalkulacija.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/todo.png"));
        icone.put(SimplePregledArtikala.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/todo.png"));
        icone.put(PregledStanjaSkladista.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/todo.png"));
        icone.put(PregledKartice.class, icon);
        
        icon = new ImageIcon(Pro3App.class.getResource("resources/bookmark_add.png"));
        icone.put(SimpleInvoice.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/month.png"));
        icone.put(PregledRacuna.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/enumList.png"));
        icone.put(Pro3xSequence.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/chart.png"));
        icone.put(DnevniPromet.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/emoticon.png"));
        icone.put(SimpleInvoice.class, icon);

        icon = new ImageIcon(Pro3App.class.getResource("resources/blockdevice.png"));
        icone.put(JInternalFrame.class, icon);        
    }
        
    private class CoList implements ContainerListener
    {
        private JMenu menu;
        private int count = 1;
        
        private ArrayList<JInternalFrame> iconFrames;
                
        public CoList(JMenu menu) {
            this.menu = menu;
            this.iconFrames = new ArrayList<JInternalFrame>();
        }
        
        private void PodesiPoziciju(JInternalFrame jf)
        {
            Rectangle old = jf.getBounds();
            
            try
            {
                SessionStorage aps = Pro3App.getApplication().getContext().getSessionStorage();

                aps.restore(jf, jf.getName() + "-stanje.xml");
                
                if((jf.getName() != null) && !(jf.getName().equals("ispisa-racuna") || jf.isResizable() || jf.isMaximum())) jf.pack();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(DeskMeni.class.getName()).log(Level.SEVERE, null, ex);
            }

            if(!old.getLocation().equals(new Point(0,0)))
                jf.setBounds(old);
            else if (jf.getLocation().equals(new Point(0,0)) && !jf.isMaximum()) 
                jf.setLocation(((count - 1) % 5) * 26, ((count - 1) % 5) * 26);
        }
        
        @Override
        public void componentAdded(ContainerEvent e) {
            
            if(e.getChild() instanceof JInternalFrame)
            {
                JInternalFrame jf = (JInternalFrame)e.getChild();

                if(iconFrames.contains(jf)) 
                {
                    iconFrames.remove(jf);
                    return;
                }

                jf.setTitle(Integer.toString(count) + ". " + jf.getTitle());
                PodesiPoziciju(jf);

                IzbornikProzor ip = new IzbornikProzor(jf);

                if(icone.containsKey(jf.getClass()))
                {
                    ImageIcon ico = icone.get(jf.getClass());
                    jf.setFrameIcon(ico);
                    ip.setIcon(ico);
                }
                else
                {
                    jf.setFrameIcon(icon);
                    ip.setIcon(icon);
                }
                
                count++;
                
                ip.setMargin(new Insets(5,2,4,2));
                ip.setIconTextGap(5);
                menu.add(ip);

                menu.setVisible(true);

                jf.addInternalFrameListener(new IFrameListener(menu, ip));
            }
            
            if(e.getChild() instanceof JInternalFrame.JDesktopIcon)
            {
                JInternalFrame.JDesktopIcon jf = (JInternalFrame.JDesktopIcon)e.getChild();
                iconFrames.add(jf.getInternalFrame());
            }
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            if(desk.getComponents().length == 1)
                count = 1;
            
            if(e.getChild() instanceof JInternalFrame.JDesktopIcon)
            {
                JInternalFrame.JDesktopIcon icon = 
                        (JInternalFrame.JDesktopIcon)e.getChild();
                iconFrames.remove(icon.getInternalFrame());
            }
        }
        
    }
       
    public DeskMeni(JDesktopPane desk, JMenu izbornik) 
    {
        this.desk = desk;
        this.izbornik = izbornik;
        
        this.izbornik.setVisible(false);
        this.izbornik.setMnemonic('P');
        desk.addContainerListener(new CoList(izbornik));        
    }

}
