/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.Roba;
import Pro3x.Configuration.General;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public class SimpleModelArtikala extends AbstractTableModel
{
    private EntityManager manager;
    private Query query;
    
    private List<Roba> proizvodi;
    private List<Roba> prikazani;
    private List<iEntityColumn> columns;
    
    private String filter = "";
    
    public SimpleModelArtikala(EntityManager manager, Query query) {
        this.manager = manager;
        this.query = query;
        
        columns = new ArrayList<iEntityColumn>();
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                return item.getSifra();
            }

            @Override
            public String getHeader() {
                return "Šifra";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                return item.getNaziv();
            }

            @Override
            public String getHeader() {
                return "Naziv";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                return item.getMjera();
            }

            @Override
            public String getHeader() {
                return "Mjera";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, Double>() {

            @Override
            public Double read(Roba item) {
                return item.getNabavnaCijena();
            }

            @Override
            public String getHeader() {
                return "Nabavna";
            }

            @Override
            public Class getColumnClass() {
                return Double.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                if(item.isPdvNabavaDef())
                    return item.getPdvNabava().getOpis();
                else
                    return "";
            }

            @Override
            public String getHeader() {
                return "PDV";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, Double>() {
            
            @Override
            public Double read(Roba item) {
                return item.getMaloprodajnaCijenaZaokruzena();
            }

            @Override
            public String getHeader() {
                return "Prodajna";
            }

            @Override
            public Class getColumnClass() {
                return Double.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                if(item.isPdvDef())
                {
                    return item.getPdv().getOpis();
                }
                else
                    return "";
            }
            
            @Override
            public String getHeader() {
                return "Pdv";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                if(item.isPotDef())
                    return item.getPot().getOpis();
                else
                    return "";
            }

            @Override
            public String getHeader() {
                return "POT";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        if(General.isKoristiPorezNaPotrosnju())
        {
            columns.add(new iEntityColumn<Roba, String>() {

                @Override
                public String read(Roba item) {
                    if(item.isPotDef())
                    {
                        return item.getPot().getOpis();
                    }
                    else
                    {
                        return "";
                    }
                }

                @Override
                public String getHeader() {
                    return "Pot";
                }

                @Override
                public Class getColumnClass() {
                    return String.class;
                }
            });
        }
        
        columns.add(new iEntityColumn<Roba, String>() {

            @Override
            public String read(Roba item) {
                if(item.getGrupa() != null)
                    return item.getGrupa().getNaziv();
                else
                    return "";
            }

            @Override
            public String getHeader() {
                return "Grupa";
            }

            @Override
            public Class getColumnClass() {
                return String.class;
            }
        });
        
        refresh();
    }
    
    public EntityManager getManager()
    {
        return manager;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
        refresh();
    }
    
    public void delete(int index) throws Exception
    {
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        
        try {

            manager.remove(prikazani.get(index));
            transaction.commit();
            
            refresh();
            
        } catch (Exception e) {
            
            if(transaction.isActive())
            {
                transaction.rollback();
            }
            
            throw new Exception("Redak nije moguće brisati", e);
        }
    }
    
    public final void refresh()
    {
        proizvodi = query.getResultList();
        
        if(filter.isEmpty())
        {
            prikazani = proizvodi;
        }
        else
        {
            prikazani = new ArrayList<Roba>();
            
            for (Roba roba : proizvodi) {
                if(roba.getNaziv().toLowerCase().contains(filter) || roba.getSifra().toLowerCase().contains(filter))
                {
                    prikazani.add(roba);
                }
            }
        }
        
        fireTableDataChanged();
    }
    
    public Roba getItem(int index)
    {
        return prikazani.get(index);
    }
    
    private List<iEntityColumn> getColumns()
    {
        return columns;
    }

    @Override
    public String getColumnName(int column) {
        return getColumns().get(column).getHeader();
    }
    
    @Override
    public int getRowCount() {
        return prikazani.size();
    }

    @Override
    public int getColumnCount() {
        return getColumns().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Roba item = prikazani.get(rowIndex);
        return getColumns().get(columnIndex).read(item);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getColumns().get(columnIndex).getColumnClass();
    }
}
