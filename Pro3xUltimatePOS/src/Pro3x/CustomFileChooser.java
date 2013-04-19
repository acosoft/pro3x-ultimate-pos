/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 *
 * @author Aco
 */
public class CustomFileChooser extends JFileChooser
{
    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException
    {
        Point pozicija = getLocation();
        
        JDialog dijalog = super.createDialog(parent);
        dijalog.setLocation(pozicija);
        
        return dijalog;
    }
}
