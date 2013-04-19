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
import javax.persistence.EntityManager;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;


public class PoreznaStopaEditor extends DefaultCellEditor
{
    private EntityManager proManager;

    public PoreznaStopaEditor()
    {
        super(new JComboBox());
        JComboBox editor = (JComboBox)getComponent();

        proManager = Pro3x.Persistence.createEntityManagerFactory("procesingPU").createEntityManager();
        java.util.List stope = proManager.createQuery("SELECT s FROM PoreznaStopa s").getResultList();
        stope.add(0, null);
        
        editor.setModel(new DefaultComboBoxModel(stope.toArray()));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        JComboBox editor = (JComboBox)getComponent();
        
        if(value != null)
            editor.setSelectedItem(value);
        else
            editor.setSelectedIndex(0);

        return editor;

//        JComboBox editor = (JComboBox)getComponent();
//        rip = (RobaPorez)value;
//
//        if(rip != null)
//        {
//            editor.setSelectedItem(rip.getPoreznaStopa());
//            editor.requestFocusInWindow();
//        }
//        else
//            editor.setSelectedIndex(0);
//
//        return editor;
//        //return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue()
    {
        JComboBox editor = (JComboBox)getComponent();
        return editor.getSelectedItem();

//        PoreznaStopa stopa = (PoreznaStopa)super.getCellEditorValue();
//
//        if(rip != null)
//            rip.setPoreznaStopa(stopa);
//        else
//        {
//            rip = new RobaPorez();
//            rip.setPoreznaStopa(stopa);
//        }
//
//        return rip;
    }


}
