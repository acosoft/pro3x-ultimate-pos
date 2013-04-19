/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.DataBox.GrupaArtikala;
import Pro3x.Persistence;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public abstract class SimpleModel<T> extends AbstractTableModel
{
    private EntityManager manager;
    private Query query;
    private List<T> grupe;
    
    private List<T> izmjenjene;
    private List<T> izbrisane;
    
    public SimpleModel(String namedQuery) {
        manager = Persistence.createEntityManagerFactory().createEntityManager();
        query = manager.createNamedQuery(namedQuery);
        query.setHint("eclipselink.refresh", "true");
        
        grupe = new LinkedList<T>();
        izmjenjene = new LinkedList<T>();
        izbrisane = new LinkedList<T>();
    }

    public final void refresh()
    {
        grupe = (List<T>) query.getResultList();
        
        izmjenjene.clear();
        izbrisane.clear();
        
        fireTableDataChanged();
    }
    
    protected abstract T createInstance();
    
    public void add()
    {
        T grupa = createInstance();
        grupe.add(grupa);
        
        izmjenjene.add(grupa);
        
        int index = grupe.size() - 1;
        fireTableRowsInserted(index, index);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
    public void remove(int index)
    {
        izbrisane.add(grupe.remove(index));
        
        fireTableRowsDeleted(index, index);
    }
    
    public boolean isChanged()
    {
        return izmjenjene.size() > 0 || izbrisane.size() > 0;
    }
    
    public void save() 
    {
        EntityTransaction transaction = manager.getTransaction();
        
        try {
            transaction.begin();
            
            for (T grupa : izmjenjene) {
                manager.persist(grupa);
            }
            
            for (T grupa : izbrisane) {
                manager.remove(grupa);
            }
            
            transaction.commit();
            
            izmjenjene.clear();
            izbrisane.clear();
            
        } catch (Exception e) {
            if(transaction.isActive())
            {
                transaction.rollback();
            }
            
            new ExceptionView(e).setVisible(true);
        }
    }
    
    @Override
    public int getRowCount() {
        return grupe.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        T grupa = grupe.get(rowIndex);
        write(grupa, columnIndex, aValue);
        izmjenjene.add(grupa);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T item = grupe.get(rowIndex);
        return read(item, columnIndex);
    }
    
    protected abstract void write(T item, int column, Object newValue);
    protected abstract Object read(T item, int column);
}
