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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author nonstop
 */
public class BooleanRenderer extends JCheckBox implements TableCellRenderer
{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
    {
        this.setHorizontalAlignment(CENTER);
        this.setSize(table.getColumnModel().getColumn(0).getWidth(), table.getHeight());
        this.setOpaque(true);
        
        if(isSelected)
            setBackground(table.getSelectionBackground());
        else if(row % 2 == 1)
            this.setBackground(Color.white);
        else
            setBackground(table.getBackground());
        
        
        if(value != null)
            setSelected((Boolean)value);
        else
            setSelected(false);
        
        return this;
    }

}
