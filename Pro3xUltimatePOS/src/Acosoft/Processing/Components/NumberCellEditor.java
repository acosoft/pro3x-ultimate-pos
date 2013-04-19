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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 * @author nonstop
 */
public class NumberCellEditor extends DefaultCellEditor
{
    NumberFormat nf = null;
    private boolean zaokruzivanje = false;
    
    public NumberCellEditor()
    {
        this(false);
    }

    public NumberCellEditor(boolean zaokruziNaDvijeDecimale)
    {
        super(new JTextField());

        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        zaokruzivanje = zaokruziNaDvijeDecimale;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JTextField jf = (JTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        
        if(value instanceof Double)
        {
            if(zaokruzivanje)
                jf.setText(nf.format(value));
            else
                jf.setText(NumberFormat.getInstance().format(value));
        }
        
        jf.setBorder(new LineBorder(Color.BLACK));
        jf.selectAll();
        
        return jf;
    }

    @Override
    public Object getCellEditorValue() 
    {
        try 
        {
            Object val = super.getCellEditorValue();

            if(zaokruzivanje)
                return nf.parse((String) val).doubleValue();
            else
                return NumberFormat.getInstance().parse((String)val).doubleValue();
        } 
        catch (ParseException ex) 
        {
            Logger.getLogger(NumberCellEditor.class.getName()).log(Level.SEVERE, null, ex);
            return 0D;
        }
    }
    
    
}
