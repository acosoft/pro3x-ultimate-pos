/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.DataBox.KnjigaPopisa;
import Pro3x.Code.ReportingServices;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public class SimpleModelKnjigePopisa extends SimpleModel<KnjigaPopisa>
{
    private List<String> columns;
    
    public SimpleModelKnjigePopisa(String namedQuery) {
        super(namedQuery);
        
        columns = new ArrayList<String>();
        columns.add("Datum");
        columns.add("Dokument");
        columns.add("Zaduzenje");
        columns.add("Promet");
    }
    
    @Override
    protected KnjigaPopisa createInstance() {
        return new KnjigaPopisa();
    }

    @Override
    public void ispis() {
        try {
            JasperPrint report = ReportingServices.LoadReport(NapredniPregledRacuna.class, 
                    "reports/knjiga-popisa.jasper", ReportingServices.getDefaultParams(), getItems());
            ReportingServices.ShowReport(report, "Ispis knjige popisa", "ispis-knjige-popisa");
        } catch (JRException ex) {
            Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            Tasks.showMessage("IZNIMKA: " + ex.getMessage());
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0)
        {
            return Date.class;
        }
        else if(columnIndex == 1)
        {
            return String.class;
        }
        else
            return Double.class;
    }
    
    @Override
    public String getColumnName(int column) {
        return this.columns.get(column);
    }
    
    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    protected void write(KnjigaPopisa item, int column, Object newValue) {
        if(column == 0)
        {
            item.setDatum((Date)newValue);
        }
        else if(column == 1)
        {
            item.setDokument((String)newValue);
        }
        else if(column == 2)
        {
            item.setZaduzenje((Double)newValue);
        }
        else if(column == 3)
        {
            item.setPromet((Double)newValue);
        }
    }

    @Override
    protected Object read(KnjigaPopisa item, int column) {
        
        if(column == 0)
        {
            return item.getDatum();
        }
        else if(column == 1)
        {
            return item.getDokument();
        }
        else if(column == 2)
        {
            return item.getZaduzenje().doubleValue();
        }
        else if(column == 3)
        {
            return item.getPromet().doubleValue();
        }
        else
        {
            return null;
        }
    }
    
}
