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

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;


public class DateCellEditor extends DefaultCellEditor
{
    private SimpleDateFormat sdf;

    public DateCellEditor(String format)
    {
        super(new JTextField());
        sdf = new SimpleDateFormat(format);
        ((JTextField)getComponent()).setHorizontalAlignment(JTextField.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        JTextField editor = (JTextField)getComponent();
        
        if(value != null)
            editor.setText(sdf.format(value));
        else
            editor.setText(sdf.format(Calendar.getInstance().getTime()));
                
        //return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        return editor;
    }

    @Override
    public Object getCellEditorValue()
    {
        try
        {
            Object value = super.getCellEditorValue();
            return sdf.parse((String) value);
        }
        catch (ParseException ex)
        {
            Logger.getLogger(DateCellEditor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
