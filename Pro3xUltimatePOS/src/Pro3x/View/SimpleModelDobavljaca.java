/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.Dobavljac;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aco
 */
public class SimpleModelDobavljaca extends SimpleModel<Dobavljac> 
{
    private List<String> columns;

    public SimpleModelDobavljaca() {
        this("Dobavljac.Svi");
    }
    
    public SimpleModelDobavljaca(String namedQuery) {
        super(namedQuery);
        columns = new ArrayList<String>();
        columns.add("Naziv");
        columns.add("Adresa");
        columns.add("Lokacija");
        columns.add("OIB");
        columns.add("Žiro račun");
    }
    
    @Override
    protected Dobavljac createInstance() {
        return new Dobavljac();
    }
    
    private List<String> getColumns()
    {
        return columns;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return getColumns().get(column);
    }
    
    @Override
    protected void write(Dobavljac item, int column, Object newValue) {
        switch(column)
        {
            case 0: item.setNaziv((String)newValue); break;
            case 1: item.setAdresa((String)newValue); break;
            case 2: item.setLokacija((String)newValue); break;
            case 3: item.setMaticniBroj((String)newValue); break;
            case 4: item.setZiro((String)newValue); break;
        }
    }

    @Override
    protected Object read(Dobavljac item, int column) {
        switch(column)
        {
            case 0: return item.getNaziv();
            case 1: return item.getAdresa();
            case 2: return item.getLokacija();
            case 3: return item.getMaticniBroj();
            case 4: return item.getZiro();
        }
        
        return null;
    }
    
}
