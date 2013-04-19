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

import Pro3x.Live.ArtikalEvents;
import Pro3x.Live.ArtikalEvents.ArtikalEventArgs;
import Pro3x.Live.EventArgs;
import Pro3x.Live.EventListener;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author nonstop
 */
public class DropEditor extends AbstractCellEditor implements TableCellEditor
{
    private JComboBox combox;
//    private JTable xTable;
//    private int xRow;
//    private int xCol;
    
    private EventListener liveListener = new EventListener()
    {
        public void doWork(Object source, EventArgs eventArgs)
        {
            ArtikalEventArgs args = (ArtikalEventArgs)eventArgs;
            DefaultComboBoxModel model = (DefaultComboBoxModel)combox.getModel();

            switch(args.getEventType())
            {
                case Kreiran:        
                    model.addElement(args.getNovaRoba());
                break;
                case Izmjenjen:
                    int index = model.getIndexOf(args.getNovaRoba());
                    if(index >= 0)
                    {
                        model.removeElementAt(index);
                        model.insertElementAt(args.getNovaRoba(), index);
                    }
                break;
                case Izbrisan:
                    model.removeElement(args.getStaraRoba());
            }
        }
    };

    public DropEditor(Object[] data)
    {
        // String items[] = new String[]{"jedan", "dva", "tri"};
        combox = new JComboBox(data);
        ArtikalEvents.Events().addListener(liveListener);
    }

    public Object getCellEditorValue()
    {
        return (combox.getSelectedItem());
    }

    //TODO: ovo bi trebalo drugačije riješiti
    private ShowTableEditorListener listener = null;
    public void setShowTableEditorListener(ShowTableEditorListener s)
    {
        this.listener = s;
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        if(listener != null)
            listener.onShowTableEditor(table);
        
        combox.setSelectedItem(value);
        //combox.requestFocusInWindow();

        if(combox.getSelectedIndex() < 0 && combox.getModel().getSize() > 0)
            combox.setSelectedIndex(0);

        return combox;
    }

    public void dispose()
    {
        ArtikalEvents.Events().removeListener(liveListener);
    }
}
