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

import Acosoft.Processing.DataBox.PoreznaStopa;
import java.awt.Component;
import javax.swing.JTable;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;


public class PoreznaStopaRenderer extends SubstanceDefaultTableCellRenderer
{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        String prikaz = "";
        if(value != null) prikaz = ((PoreznaStopa)value).getOpis();

        return super.getTableCellRendererComponent(table, prikaz, isSelected, hasFocus, row, column);

//        RobaPorez robaPorez = (RobaPorez)value;
//        if(robaPorez != null)
//        {
//            PoreznaStopa stopa = robaPorez.getPoreznaStopa();
//
//            String prikaz = "";
//            if(stopa != null) prikaz = stopa.getOpis();
//
//            return super.getTableCellRendererComponent(table, prikaz, isSelected, hasFocus, row, column);
//        }
//        else
//            return super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
    }
}
