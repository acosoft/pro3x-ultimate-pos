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

import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.Pro3App;
import java.awt.Component;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class RacunCellRenderer extends SubstanceDefaultTableCellRenderer
{

    @Override
    public Component getTableCellRendererComponent(JTable grid, Object value, boolean arg2, boolean arg3, int row, int col) 
    {
        Racun rac = (Racun)value;
        
        if(rac.getTemplate().isFiskalnaTransakcija() && rac.getJir() == null)
        {
            URL url = Pro3App.class.getResource("resources/network-error.png");
            setIcon(new ImageIcon(url));
        }
        else if(rac.getStorniran() != null)
        {
            URL url = RacunCellRenderer.class.getResource("resources/fileclose.png");
            setIcon(new ImageIcon(url));
        }
        else if(rac.getPlacen() == null)
        {
            URL url = RacunCellRenderer.class.getResource("resources/flag.png");
            setIcon(new ImageIcon(url));
        }
        else
        {
            URL url = RacunCellRenderer.class.getResource("resources/bookmark.png");
            setIcon(new ImageIcon(url));
        }
        
        return super.getTableCellRendererComponent(grid, value, arg2, arg3, row, col);
    }
    
}
