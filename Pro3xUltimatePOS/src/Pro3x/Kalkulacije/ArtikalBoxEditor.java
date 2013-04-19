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

package Pro3x.Kalkulacije;

import Acosoft.Processing.DataBox.Roba;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

public class ArtikalBoxEditor implements ComboBoxEditor
{
    private JTextField editor = new JTextField();
    private Object item;

    public ArtikalBoxEditor()
    {
        editor.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
//                    Object stari = item;
                    fireSearchAction(editor.getText());

                    Roba roba = (Roba) item;
                    editor.setText(roba.getNaziv());
                    editor.selectAll();
                    e.consume();
                    
//                    if(!item.equals(stari))
//                    {
//                    }
                }

                //super.keyPressed(e);
            }
        });

        editor.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if(item instanceof Roba)
                {
                    editor.setText(((Roba)item).getNaziv());
                    editor.selectAll();
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if(item instanceof Roba)
                    editor.setText(((Roba)item).getNaziv());
            }
        });
    }

    public JTextField getEditorComponent()
    {
        return editor;
    }

    public void setItem(Object anObject)
    {
        if(anObject instanceof Roba)
        {
            item = anObject;
            editor.setText(((Roba)anObject).getNaziv());
        }
        else
            editor.setText("");
    }

    public Object getItem()
    {
        return item;
    }

    public void selectAll()
    {
        editor.selectAll();
    }

    private List<ActionListener> listeners = new LinkedList<ActionListener>();

    public void addActionListener(ActionListener l)
    {
        listeners.add(l);
    }

    public void removeActionListener(ActionListener l)
    {
        listeners.remove(l);
    }

    public void fireSearchAction(String searchPattern)
    {
        for(ActionListener listener : listeners)
            listener.actionPerformed(new SearchEvent(this, searchPattern));
    }
}
