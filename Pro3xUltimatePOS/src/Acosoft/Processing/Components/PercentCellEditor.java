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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;


public class PercentCellEditor extends DefaultCellEditor
{
    private NumberFormat nf;

    public PercentCellEditor()
    {
        super(new JTextField());

        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
    }

    public JTextField getEditorComponent()
    {
        return (JTextField) editorComponent;
    }

    @Override
    public Object getCellEditorValue()
    {
        try
        {
            String prikaz = getEditorComponent().getText();
            Double postotak = nf.parse(prikaz).doubleValue() / 100;
            return postotak;
        }
        catch (ParseException ex)
        {
            return 0D;
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        if(value instanceof Double)
        {
            Double postotak = (Double) value;
            getEditorComponent().setText(nf.format(postotak * 100));
        }
        else
            getEditorComponent().setText("0,00");

        getEditorComponent().selectAll();
        return getEditorComponent();
        //return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}
