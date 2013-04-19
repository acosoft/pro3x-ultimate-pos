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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

/**
 *
 * @author nonstop
 */
public class IzbornikProzor extends JMenuItem
{
    private JInternalFrame iframe;
    
    public IzbornikProzor(JInternalFrame iframe)
    {
        super(iframe.getTitle());
        
        this.iframe = iframe;
        
        this.addActionListener(new ActionListener() 
        {    
            public void actionPerformed(ActionEvent e) {
                try {
                    IzbornikProzor ip = (IzbornikProzor) e.getSource();
                    ip.iframe.setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(IzbornikProzor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void SelectFrame()
    {
        try {
            iframe.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(IzbornikProzor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
}
