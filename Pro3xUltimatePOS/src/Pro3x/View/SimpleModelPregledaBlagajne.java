/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.Blagajna;
import Pro3x.Persistence;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public class SimpleModelPregledaBlagajne extends AbstractTableModel
{
    private Date datum;
    private List<Blagajna> data;
    private EntityManager manager;
    private List<String> columns;
    private boolean changed = false;
    private List<Blagajna> brisanje;
    
    public SimpleModelPregledaBlagajne() {
        columns = new ArrayList<String>();
        
        columns.add("Datum");
        columns.add("Opis");
        columns.add("Ulaz");
        columns.add("Izlaz");
        columns.add("Stanje");
        
        brisanje = new ArrayList<Blagajna>();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }
    
    public boolean isRacun(int index)
    {
        return data.get(index).getRacun() != null;
    }
    
    public void dodaj()
    {
        Blagajna prethodna = null;
        
        if(data.size() >= 1)
        {
            prethodna = data.get(data.size() - 1);
        }
        
        Blagajna blagajna = new Blagajna();
        blagajna.setDatum(datum);
        blagajna.setPrethodna(prethodna);
        
        data.add(blagajna);
        
        int index = data.size() - 1;
        fireTableRowsInserted(index, index);
        
        changed = true;
    }
    
    private void poveziBlagajne()
    {
        Blagajna prethodna = null;
        for (Blagajna blagajna : data) {
            blagajna.setPrethodna(prethodna);
            prethodna = blagajna;
        }
        
        fireTableDataChanged();
    }
    
    public void izbrisi(int index)
    {
        brisanje.add(data.remove(index));
        fireTableDataChanged();
        changed = true;
    }
    
    public boolean isChanged()
    {
        return changed;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
    
    public void refresh()
    {
        manager = Persistence.createEntityManagerFactory().createEntityManager();
        Calendar cal = Calendar.getInstance();
        cal.setTime(datum);
        
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date pocetak = cal.getTime();
        
        cal.add(Calendar.DATE, 1);
        
        Date kraj = cal.getTime();
        
        Query query = manager.createQuery("SELECT b FROM Blagajna b WHERE b.datum BETWEEN :pocetak AND :kraj");
        query.setParameter("pocetak", pocetak);
        query.setParameter("kraj", kraj);
        
        data = query.getResultList();
        brisanje = new ArrayList<Blagajna>();
        
        poveziBlagajne();
        
        changed = false;
        
        fireTableDataChanged();
    }
    
    public void save()
    {
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        
        for (Blagajna blagajna : brisanje) {
            manager.remove(blagajna);
        }
        
        for (Blagajna blagajna : data) {
            manager.persist(blagajna);
        }
        
        transaction.commit();
        
        changed = false;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Blagajna blagajna = data.get(rowIndex);
        return blagajna.getRacun() == null && columnIndex != 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Blagajna blagajna = data.get(rowIndex);
        
        if(columnIndex == 0)
        {
            return blagajna.getDatum();
        }
        else if(columnIndex == 1)
        {
            return blagajna.getOpis();
        }
        else if(columnIndex == 2)
        {
            return blagajna.getUlaz();
        }
        else if(columnIndex == 3)
        {
            return blagajna.getIzlaz();
        }
        else if(columnIndex == 4)
        {
            return blagajna.getStanje();
        }
        
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0)
            return Date.class;
        else if(columnIndex == 1)
            return String.class;
        else
            return Double.class;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
        Blagajna blagajna = data.get(rowIndex);
        
        if(columnIndex == 0)
        {
            blagajna.setDatum((Date)aValue);
        }
        else if(columnIndex == 1)
        {
            blagajna.setOpis((String)aValue);
        }
        else if(columnIndex == 2)
        {
            blagajna.setUlaz((Double)aValue);
            poveziBlagajne();
        }
        else if(columnIndex == 3)
        {
            blagajna.setIzlaz((Double)aValue);
            poveziBlagajne();
        }
        
        changed = true;
    }
}
