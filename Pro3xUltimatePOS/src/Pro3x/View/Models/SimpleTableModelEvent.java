/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author aco
 */
public class SimpleTableModelEvent extends TableModelEvent
{
    private boolean clearDisplay;
    
    public SimpleTableModelEvent(TableModel source) {
        super(source);
    }

    public boolean isClearDisplay() {
        return clearDisplay;
    }

    public void setClearDisplay(boolean clearDisplay) {
        this.clearDisplay = clearDisplay;
    }
}
