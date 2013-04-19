/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

import java.lang.reflect.Method;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author aco
 */
public class ColumnInfo 
{
    private String header;
    private Method method;
    private TableCellRenderer renderer;

    public ColumnInfo(String header, Method method, TableCellRenderer renderer) {
        this.header = header;
        this.method = method;
        this.renderer = renderer;
    }

    public TableCellRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(TableCellRenderer renderer) {
        this.renderer = renderer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
    
    
}
