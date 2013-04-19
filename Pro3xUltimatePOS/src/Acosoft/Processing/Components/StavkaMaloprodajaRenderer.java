/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Acosoft.Processing.Components;

import Acosoft.Processing.DataBox.Stavke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.jvnet.substance.utils.border.SubstanceBorder;

/**
 *
 * @author aco
 */
public class StavkaMaloprodajaRenderer extends JLabel implements TableCellRenderer
{
    private SubstanceBorder border;
    
    public StavkaMaloprodajaRenderer() {
        this.setPreferredSize(new Dimension(100, 36));
        this.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), 24));
        
        this.border = new SubstanceBorder();
        border.setAlpha(0);
        
        this.setBorder(border);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        if(hasFocus)
        {
            border.setAlpha(1);
        }
        else
        {
            border.setAlpha(0);
        }
        
        if(value instanceof Stavke)
        {
            this.setText(((Stavke)value).getRoba());
        }
        else
        {
            this.setText("");
        }
        
        return this;
    }

}
