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

import Acosoft.Processing.Pro3App;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.jdesktop.application.SessionStorage;

/**
 *
 * @author nonstop
 */
public class IFrameListener extends InternalFrameAdapter
{
    private IzbornikProzor ip;
    private JMenu root;
    
    public IFrameListener(JMenu root, IzbornikProzor ip)
    {
        this.ip = ip;
        this.root = root;
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) 
    {
        try 
        {
            if(!e.getInternalFrame().isIcon())
            {
                SessionStorage aps = Pro3App.getApplication().getContext().getSessionStorage();
                aps.save(e.getInternalFrame(), e.getInternalFrame().getName() + "-stanje.xml");
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(IFrameListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        Container jc = ip.getParent();
        jc.remove(ip);

        IzbornikProzor lip = null;
        for(Component m : jc.getComponents())
            if(m instanceof IzbornikProzor) lip = (IzbornikProzor)m;
        
        if(lip == null) 
        {
            this.root.setVisible(false);
            this.root.getTopLevelAncestor().requestFocusInWindow();
        }
        else
            lip.SelectFrame();
        
        super.internalFrameClosed(e);
    }
    
}
