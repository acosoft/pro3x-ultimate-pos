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

import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 *
 * @author nonstop
 */
public class NumberCellRenderer extends SubstanceDefaultTableCellRenderer
{
    public NumberCellRenderer()
    {
        this(false);
    }
    
    private boolean tryIntRendering;
    public NumberCellRenderer(boolean tryIntRendering)
    {
        this(false, true);
    }

    private boolean zaokruzivanje = false;
    private NumberFormat nf;

    public NumberCellRenderer(boolean tryIntRendering, boolean zaokruzi)
    {
        this(tryIntRendering, zaokruzi, 2, 2);
    }
    
    public NumberCellRenderer(boolean tryIntRendering, boolean zaokruzi, int minBrojDecimala, int maxBrojDecimala)
    {
        this.tryIntRendering = tryIntRendering;
        this.zaokruzivanje = zaokruzi;
        nf = NumberFormat.getInstance();
        
        if(zaokruzivanje == true)
        {
            nf.setMinimumFractionDigits(minBrojDecimala);
            nf.setMaximumFractionDigits(maxBrojDecimala);
        }
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        setHorizontalAlignment(RIGHT);
        return  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    @Override
    protected void setValue(Object value)
    {      
        super.setValue(value);
        
        try
        {
            if(value instanceof String)
            {
                Double xf = nf.parse((String) value).doubleValue();
                super.setText(nf.format(xf));
            }
            else if(value instanceof Double || value instanceof Float)
            {
                super.setValue(value);
                
                Number x = (Number)value;
                if(x.intValue() == x.floatValue() && tryIntRendering == true)
                    super.setText(String.valueOf(x.intValue()));
                else
                {
                    if(zaokruzivanje)
                        super.setText(nf.format(value));
                    else
                    {
                        NumberFormat mini = NumberFormat.getInstance();
                        mini.setGroupingUsed(false);
                        mini.setMinimumFractionDigits(2);
                        super.setText(mini.format(value));
                    }
                }
            }
        }
        catch (ParseException ex)
        {
            Logger.getLogger(NumberCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
