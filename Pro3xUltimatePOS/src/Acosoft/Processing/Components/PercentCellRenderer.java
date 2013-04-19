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

import java.text.NumberFormat;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 *
 * @author nonstop
 */
public class PercentCellRenderer extends SubstanceDefaultTableCellRenderer
{
    private boolean onlyDecimal;

    public PercentCellRenderer() {
        this(false);
    }
    
    public PercentCellRenderer(boolean onlyDecimal)
    {
        this.onlyDecimal = onlyDecimal;
        super.setHorizontalAlignment(PercentCellRenderer.RIGHT);
    }

    @Override
    protected void setValue(Object value) 
    {
        if(value instanceof Integer || value instanceof Double)
        {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);

            // TODO: ako je postotak manji od 1, onda se vjerojatno koristi novi format
            // izbačen stari format, postotak mora biti izražen kao decimalna vrijednost
            
            Double postotak;
            
            if((Double)value <= 1 || this.onlyDecimal)
                postotak = (Double)value * 100;
            else
                postotak = (Double)value;

            if(postotak != 0)
            {
                setText(nf.format(postotak) + "%");
            }
            else
                setText(nf.format(0D) + "%");
            //setText(NumberFormat.getInstance().format(value) + "%");
        }
        else
            setText("");
    }
    
}
