/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.Korisnik;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public class SimpleModelKupaca extends AbstractTableModel
{
    private List<Korisnik> kupci;
    private List<Korisnik> prikazaniKupci;
    private List<String> headers;
    private String filter = "";

    public SimpleModelKupaca() {
        this.kupci = new ArrayList<Korisnik>();
        this.prikazaniKupci = kupci;
        
        headers = new ArrayList<String>();
        
        headers.add("OIB");
        headers.add("Naziv");
        headers.add("Adresa");
    }
    
    public void setKupci(List<Korisnik> kupci)
    {
        this.kupci = kupci;
        this.prikazaniKupci = kupci;
        
        fireTableDataChanged();
    }
    
    public int indexKupca(String kljuc)
    {
        for (Korisnik korisnik : kupci) {
            if(korisnik.getKljuc().equals(kljuc))
            {
                return kupci.indexOf(korisnik) + 1; //uvećan za 1 zbog prvog praznog reda
            }
        }
        
        return -1;
    }
    
    public Korisnik getKupac(int index)
    {
        if(index == 0)
            return null;
        else
            return prikazaniKupci.get(index - 1);
    }
    
    public String getFilter()
    {
        return filter;
    }
    
    public void filter(String search)
    {
        filter = search;
        search = search.toLowerCase();
        
        prikazaniKupci = new ArrayList<Korisnik>();
        
        for (Korisnik korisnik : kupci) {
            if(korisnik.getMaticniBroj().startsWith(search) 
                    || korisnik.getNaziv().toLowerCase().contains(search) 
                    || korisnik.getLokacija().toLowerCase().contains(search) 
                    || korisnik.getAdresa().toLowerCase().contains(search))
                prikazaniKupci.add(korisnik);
        }
        
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return prikazaniKupci.size() + 1;
    }
    
    public List<String> getColumnHeaders()
    {
        return headers;
    }

    @Override
    public int getColumnCount() {
        return 3; // OIB | Naziv | Adresa
    }

    @Override
    public String getColumnName(int column) {
        return getColumnHeaders().get(column);
    }
    
    private String implode(String ... items)
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        
        for (String item : items) 
        {
            if(item.trim().length() > 0)
            {
                if(first)
                {
                    result.append(item);
                    first = false;
                }
                else
                {
                    result.append(", ").append(item);
                }
            }
        }
        
        return result.toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        if(rowIndex == 0)
        {
            return null;
        }
        else if(rowIndex < getRowCount())
        {
            int modelRowIndex = rowIndex - 1;
            
            if(columnIndex == 0)
                return prikazaniKupci.get(modelRowIndex).getMaticniBroj();
            else if(columnIndex == 1)
                return prikazaniKupci.get(modelRowIndex).getNaziv();
            else if(columnIndex == 2)
            {
                Korisnik korisnik = prikazaniKupci.get(modelRowIndex);
                return implode(korisnik.getAdresa(), korisnik.getLokacija());
            }
        }
        
        return null;
    }
    
}
