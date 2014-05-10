/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.DataBox.Stavke;
import Pro3x.View.Models.SimpleTableModelEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aco
 */
public class SimpleModelStavki extends AbstractTableModel {

    private Racun racun;

    public SimpleModelStavki(Racun racun, List<Roba> proizvodi) {
        this.racun = racun;
    }
    
    public Racun getRacun() {
        return racun;
    }

    public void setRacun(Racun racun) {
        this.racun = racun;
        
        SimpleTableModelEvent event = new SimpleTableModelEvent(this);
        event.setClearDisplay(true);
        fireTableChanged(event);
    }

    public List<Stavke> getStavke() {
        return (List<Stavke>) racun.getStavke();
    }

    @Override
    public int getRowCount() {
        int size = getStavke().size();
        
        if(size == 0)
            return 1;
        else
            return size;
    }
    
    public int getBrojStavki()
    {
        return getStavke().size();
    }
    
    public void izbrisiStavku(int index)
    {
        if(index < getStavke().size())
        {
            getStavke().remove(index);
            
            SimpleTableModelEvent event = new SimpleTableModelEvent(this);
            event.setClearDisplay(true);
            fireTableChanged(event);
        }
    }

    private ArrayList<String> getColumnHeaders()
    {
        ArrayList<String> headers = new ArrayList<String>();

        headers.add("Naziv");
        headers.add("Količina");
        headers.add("Cijena");
        headers.add("Iznos");
        headers.add("Popust");
        headers.add("Ukupno");

        return headers;
    }

    @Override
    public int getColumnCount() {
        return this.getColumnHeaders().size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return getColumnHeaders().get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0)
            return Stavke.class;
        else
            return Integer.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(rowIndex < racun.getStavke().size() && (columnIndex == 1 || columnIndex == 4))
            return true;
        else
            return false;
    }
    
    public void dodajStavku(Roba roba)
    {
        dodajStavku(roba, 1);
    }
    
    public void dodajStavku(Roba roba, double kolicina)
    {
        dodajStavku(roba, kolicina, 0);
    }
    
    public void dodajStavku(Roba roba, double kolicina, double popust)
    {
        Stavke stavka = new Stavke();
        stavka.setRoba(roba.getNaziv());
        stavka.setRobaInfo(roba);
        
        stavka.setGrupa(roba.getGrupa());
        
        stavka.setMaloprodajnaCijena(roba.getMaloprodajnaCijenaZaokruzena());
        stavka.setKolicina(kolicina);
        stavka.setPopust(popust);
        
        stavka.setIznos(stavka.getMaloprodajnaCijena() * kolicina);
        stavka.setUkupno(stavka.getIznos() * (1-stavka.getPopust()));
        
        stavka.setRacunKljuc(racun);
        stavka.setMjera(roba.getMjera());
        
        getStavke().add(stavka);
        
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        if(rowIndex < getStavke().size())
        {
            Stavke stavka = (Stavke) racun.getStavke().toArray()[rowIndex];

            if(columnIndex == 0)
                return stavka;
            else if(columnIndex == 1)
                return stavka.getKolicina();
            else if(columnIndex == 2)
                return stavka.getMaloprodajnaCijena();
            else if (columnIndex == 3)
                return stavka.getIznos();
            else if(columnIndex == 4)
                return stavka.getPopust();
            else if(columnIndex == 5)
                return stavka.getUkupno();
        }
        
        return null;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(rowIndex < getStavke().size())
        {
            Stavke stavka = (Stavke) getStavke().get(rowIndex);
            
            //promjena količine
            if(columnIndex == 1)
            {
                Double kolicina = (Double) aValue;
                
                stavka.setKolicina(kolicina.doubleValue());
                stavka.setIznos(stavka.getMaloprodajnaCijena() * stavka.getKolicina());
                stavka.setUkupno(stavka.getIznos() * (1 - stavka.getPopust()));
            }
            //promjena popusta
            else if(columnIndex == 4)
            {
                double popust = ((Double)aValue).doubleValue();
                stavka.setPopust(popust);
                stavka.setUkupno(stavka.getIznos() * (1 - stavka.getPopust()));
            }
            
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
}
