package Pro3x.View.Models;

import Acosoft.Processing.DataBox.Roba;
import Pro3x.View.SimpleModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public class SimpleModelStanjaSkladista extends AbstractTableModel
{
    private List<String> columns;
    private List data;
    
    public SimpleModelStanjaSkladista(List data) {
        columns = new ArrayList<String>();
        columns.add("Naziv");
        columns.add("Kolicina");
        columns.add("Mjera");
        columns.add("Cijena");
        columns.add("Ukupno");
        
        this.data = data;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0 || columnIndex == 2)
            return String.class;
        else
            return Double.class;
    }
    
    public List<StanjeArtikla> getStanje()
    {
        List<StanjeArtikla> stanje = new ArrayList<StanjeArtikla>();
        
        for (Object item : data) {
            stanje.add(new StanjeArtikla(item));
        }
        
        return stanje;
    }
    
    public Roba getArtikal(int rowIndex)
    {
        Object[] value = (Object[]) data.get(rowIndex);
        return (Roba) value[0];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object[] values = (Object[]) data.get(rowIndex);
        Roba roba = (Roba) values[0];
        
        Double ulaz = (Double) values[1];
        Double izlaz = (Double) values[2];
        Double stanje = ulaz - izlaz;

        if(columnIndex == 0)
        {
            return roba.getNaziv();
        }
        else if(columnIndex == 1)
        {
            return stanje;
        }
        else if(columnIndex == 2)
        {
            return roba.getMjera();
        }
        else if(columnIndex == 3)
        {
            return roba.getMaloprodajnaCijenaZaokruzena();
        }
        else if(columnIndex == 4)
        {
            return stanje * roba.getMaloprodajnaCijenaZaokruzena();
        }
        
        return "";
    }
}
