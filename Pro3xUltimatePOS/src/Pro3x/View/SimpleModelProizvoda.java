/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.Roba;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public class SimpleModelProizvoda extends AbstractTableModel {

    private List<Roba> proizvodi;

    public SimpleModelProizvoda() {
        proizvodi = new ArrayList<Roba>();
    }
    
    public List<Roba> getProizvodi() {
        return proizvodi;
    }

    public void setProizvodi(List<Roba> proizvodi) {
        this.proizvodi = proizvodi;
        
        fireTableDataChanged();
    }
    
    public Roba getProizvod(int index)
    {
        if(index == 0)
            return null;
        else
            return proizvodi.get(index - 1);
    }

    @Override
    public String getColumnName(int column) {
        List<String> headers = new ArrayList<String>();
        
        headers.add("Naziv");
        headers.add("Cijena");
        
        return headers.get(column);
    }
    
    @Override
    public int getRowCount() {
        return getProizvodi().size() + 1;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0)
            return String.class;
        else
            return Integer.class;   //TODO: hack, klasa za kolonu bi trebala biti double, ali u tom slučaju jtable odabire krivi cell renderer
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex == 0)
            return null;
        else if(columnIndex == 0)
            return getProizvodi().get(rowIndex - 1).getNaziv();
        else if(columnIndex == 1)
            return getProizvodi().get(rowIndex - 1).getMaloprodajnaCijenaZaokruzena();
        else
            return null;
    }
    
}
