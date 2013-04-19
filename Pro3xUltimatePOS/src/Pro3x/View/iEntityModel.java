/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

/**
 *
 * @author aco
 */
public interface iEntityModel {

    void add();

    int getColumnCount();

    String getColumnName(int column);

    int getRowCount();

    Object getValueAt(int rowIndex, int columnIndex);

    boolean isCellEditable(int rowIndex, int columnIndex);

    boolean isChanged();

    void refresh();

    void remove(int index);

    void save();

    void setValueAt(Object aValue, int rowIndex, int columnIndex);
    
}
