/*
 * Pro3x Community project
 * Copyright (C) 2009  Aleksandar Zgonjan
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, http://www.gnu.org/licenses/gpl.html
 * and open the template in the editor.
 */

package Pro3x.Code;

import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.Pro3App;
import Acosoft.Processing.Pro3Postavke;
import Acosoft.Processing.Pro3View;
import Pro3x.Barcode.BarcodeInfo;
import Pro3x.Kalkulacije.PregledSvihKalkulacija;
import Pro3x.View.SimpleInvoice;
import java.awt.Dimension;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;


public class ReportingServices
{
    public static void ShowReport(JasperPrint report, String opis, String naziv)
    {
        JRViewer jv = new JRViewer(report);
        
        JInternalFrame jf = new JInternalFrame(opis, true, true, true, true);

        jf.setName(naziv);
        jf.setMinimumSize(new Dimension(640, 400));
        jf.add(jv);

        jf.pack();

        Pro3View xp = (Pro3View)Pro3App.getApplication().getMainView();
        xp.Show(jf);

        jf.validate();
    }
    
    public enum Reports
    {
        ZapisnikPromjenaCijena("resources/ZapisnikPromjenaCijena.jasper"),
        KarticaArtikla("resources/KarticaArtikla.jasper");
        
        private String path;
        
        private Reports(String path)
        {
            this.path = path;
        }
        
        public String getPath()
        {
            return this.path;
        }
        
        public JasperPrint load(Map params, List data) throws JRException
        {
            return ReportingServices.LoadReport(ReportingServices.class, path, params, data);
        }
    }

    public static JasperPrint LoadReport(Class type, String report, Map params, List data) throws JRException
    {
        InputStream reportStream = type.getResourceAsStream(report);
        JasperReport reportDefinition = (JasperReport) JRLoader.loadObject(reportStream);
        JRBeanCollectionDataSource dataList = new JRBeanCollectionDataSource(data);

        return JasperFillManager.fillReport(reportDefinition, params, dataList);
    }

    public static void PrintBarcode(BarcodeInfo info)
    {
        try
        {
            List<BarcodeInfo> items = new LinkedList<BarcodeInfo>();
            items.add(info);
            JasperPrint jp = ReportingServices.LoadReport(PregledSvihKalkulacija.class, "resources/IspisArtikalBarkod.jasper", null, items);
            ReportingServices.ShowReport(jp, "Ispis barkoda " + info.getNaziv(), "barkod-ispis");
        }
        catch (JRException ex)
        {
            Logger.getLogger(ReportingServices.class.getName()).log(Level.SEVERE, null, ex);
            new ExceptionView(ex).setVisible(true);
        }
    }
    
    public static void IspisRacuna(Racun racun, boolean printOnly)
    {
        try 
        {
            JasperPrint print = pripremaIspisaRacuna(racun);
            
            if(printOnly)
                JasperPrintManager.printReport(print, false);
            else
                ShowReport(print, "Ispis računa", "simple-ispis-racuna-v1");
        } 
        catch (JRException ex) 
        {
            Logger.getLogger(ReportingServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void IspisRacuna(Racun racun)
    {
        IspisRacuna(racun, false);
    }
    
    public static JasperPrint pripremaIspisaRacuna(Racun racun)
    {
        try {
            List<Racun> racuni = new ArrayList<Racun>();
            racuni.add(racun);
            
            return LoadReport(SimpleInvoice.class, racun.getPutanjaPredloska(), null, racuni);
        } catch (JRException ex) {
            Logger.getLogger(ReportingServices.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private static Map getDefaultParams()
    {
        HashMap params = new HashMap();
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            
            params.put("Logo", Pro3Postavke.getLogo());
            params.put("Zaglavlje", Pro3Postavke.getInfo().getZaglavlje());
            params.put("Datum", sdf.format(Calendar.getInstance().getTime()));
        } 
        catch (Exception ex)
        {
            Tasks.showMessage("Iznimka: Neuspješna priprema osnovih parametara izvještaja.");
            Logger.getLogger(ReportingServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return params;
    }
    
    public static void showPromet(List data, String prviRacun, String posljednjiRacun)
    {
        try
        {
            Map params = getDefaultParams();
            
            params.put("pocetak", prviRacun);
            params.put("kraj", posljednjiRacun);
            
            JasperPrint report = LoadReport(Pro3App.class, "resources/promet.jasper", params, data);
            ShowReport(report, "Ispis prometa", "ispis-prometa");
        }
        catch (JRException ex)
        {
            Logger.getLogger(ReportingServices.class.getName()).log(Level.SEVERE, null, ex);
            Tasks.showMessage("Iznimka: NeuspjeĹˇna priprema ispisa Prometa");
        }
    }
    
    public static void showDnevniPromet(List data)
    {
        try
        {
            Map params = getDefaultParams();
            
            JasperPrint report = LoadReport(Pro3App.class, "resources/promet-po-danima.jasper", params, data);
            ShowReport(report, "Ispis prometa", "ispis-prometa");
        }
        catch (JRException ex)
        {
            Logger.getLogger(ReportingServices.class.getName()).log(Level.SEVERE, null, ex);
            Tasks.showMessage("Iznimka: NeuspjeĹˇna priprema ispisa Prometa");
        }
    }
}
