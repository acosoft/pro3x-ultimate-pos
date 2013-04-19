/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author aco
 */
public class DataModel<T> extends AbstractTableModel
{
    List<T> data;
    List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
    Class<T> contentType;

    public DataModel(List<T> data) 
    {
        this.data = data;
    }

    public void setContentType(Class<T> contentType) {
        this.contentType = contentType;
    }

    public Class<T> getContentType() {
        return contentType;
    }

    public int getRowCount() 
    {
        return data.size();
    }

    public int getColumnCount() 
    {
        return columns.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) 
    {
        try 
        {
            T item = data.get(rowIndex);
            ColumnInfo col = columns.get(columnIndex);
            
            if(col.getMethod() == null)
                return item;
            else
                return col.getMethod().invoke(item);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getHeader();
    }
    
    public void addColumn(String name, String method, TableCellRenderer renderer) throws NoSuchMethodException
    {
        if(method.isEmpty())
            columns.add(new ColumnInfo(name, null, renderer));
        else
            columns.add(new ColumnInfo(name, getContentType().getMethod(method), renderer));
    }
    
    public void addItem(T item)
    {
        data.add(item);
        fireTableDataChanged();
    }

    public void setData(List<T> data) 
    {
        this.data = data;
        fireTableDataChanged();
    }
    
    public void configureView(JTable grid)
    {
        for(int i=0; i<columns.size(); i++)
        {
            ColumnInfo info = columns.get(i);
            if(info.getRenderer() != null)
            {
                grid.getColumnModel().getColumn(i).setCellRenderer(info.getRenderer());
            }
        }
    }
    
    public T getItem(int index)
    {
        if(index >= 0 && index < data.size())
            return data.get(index);
        else
            return null;
    }
    
    public void refresh()
    {
        fireTableDataChanged();
    }
}
