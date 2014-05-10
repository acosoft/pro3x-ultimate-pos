/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SimpleInvoice.java
 *
 * Created on 05.01.2013., 22:47:22
 */
package Pro3x.View;

import Acosoft.Processing.Components.InfoRacun;
import Acosoft.Processing.Components.NumberCellEditor;
import Acosoft.Processing.Components.NumberCellRenderer;
import Acosoft.Processing.Components.PercentCellEditor;
import Acosoft.Processing.Components.PercentCellRenderer;
import Acosoft.Processing.Components.StavkaMaloprodajaRenderer;
import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.DataBox.ArtikalNormativ;
import Acosoft.Processing.DataBox.Blagajna;
import Acosoft.Processing.DataBox.KarticaStavkeNormativaRacuna;
import Acosoft.Processing.DataBox.KarticaStavkeRacuna;
import Acosoft.Processing.DataBox.KnjigaPopisa;
import Acosoft.Processing.DataBox.Korisnik;
import Acosoft.Processing.DataBox.PoreznaStavkaRacuna;
import Acosoft.Processing.DataBox.PoreznaStopa;
import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.DataBox.Stavke;
import Acosoft.Processing.DataBox.Template;
import Acosoft.Processing.NoviKorisnik;
import Acosoft.Processing.Pro3App;
import Acosoft.Processing.Pro3Postavke;
import Acosoft.Processing.Pro3View;
import Pro3x.Barcode.WeightCodeParser;
import Pro3x.BasicView;
import Pro3x.Code.ReportingServices;
import Pro3x.Configuration.General;
import Pro3x.Fiscal.FiskalniRacun;
import Pro3x.Fiscal.IzborCertifikata;
import Pro3x.Fiscal.IzmjenaPoslovnogProstora;
import Pro3x.Fiscal.KeyInfo;
import Pro3x.Fiscal.LocationInfo;
import Pro3x.Fiscal.Model.Operater;
import Pro3x.Live.EventArgs;
import Pro3x.Live.EventListener;
import Pro3x.Live.KorisnikEvents;
import Pro3x.Persistence;
import Pro3x.View.Models.SimpleTableModelEvent;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.Task;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author aco
 */
public class SimpleInvoice extends BasicView {

    private Operater odabraniOperater;
    private Racun racun;
    private Stavke odabranaStavka;
    private SimpleModelProizvoda modelProizvoda;
    private String trenutniIznosUplate = "";
    private boolean gotovinskiRacun;
    private String trenutniSearchQuery = "";
    private String trenutniKupacSearchQuery = "";
    private List<Template> naciniPlacanja;
    
    public SimpleInvoice()
    {
        this(new Racun(), true);
    }
    
    public SimpleInvoice(boolean gotovinskiRacun)
    {
        this(new Racun(), gotovinskiRacun);
    }
    
    /** Creates new form SimpleInvoice */
    public SimpleInvoice(Racun racun, boolean gotovinskiRacun) {
        
        this.racun = racun;
        this.modelProizvoda = new SimpleModelProizvoda();
        this.gotovinskiRacun = gotovinskiRacun;
        
        initComponents();
        
        if(gotovinskiRacun == false)    
            cmdGotovinskiRacun.setVisible(false);
        
        grid.setDefaultRenderer(Stavke.class, new StavkaMaloprodajaRenderer());
        grid.setDefaultRenderer(Integer.class, new NumberCellRenderer(false, true));
        grid.setDefaultRenderer(Double.class, new NumberCellRenderer(false, true));
        
        grid.getColumnModel().getColumn(1).setCellEditor(new NumberCellEditor(false));
        grid.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRenderer(false, true, 2, 6));
        
        grid.getColumnModel().getColumn(4).setCellEditor(new PercentCellEditor());
        grid.getColumnModel().getColumn(4).setCellRenderer(new PercentCellRenderer());
        
        gridPretrazivanje.setDefaultRenderer(Double.class, new NumberCellRenderer(false, true));
        gridPretrazivanje.setDefaultRenderer(Integer.class, new NumberCellRenderer(false, true));
        
        if(listaOperatera.getModel().getSize() > 0)
        {
            listaOperatera.setSelectedIndex(0);
        }
        
        listaNacinaPlacanja.setSelectedIndex(0);
        
        SimpleModelKupaca model = (SimpleModelKupaca) gridKupci.getModel();
        model.setKupci(kupciQuery.getResultList());
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gridKupci.setRowHeight(50);
                gridKupci.setRowSelectionInterval(0, 0);

                gridPretrazivanje.setRowHeight(50);
                
                ApplicationActionMap actions = Pro3App.getInstance().getContext().getActionMap(SimpleInvoice.class, SimpleInvoice.this);
                
                javax.swing.Action unosRacuna = actions.get("UnosRacuna");
                javax.swing.Action dodajOdabraniProizvod = actions.get("DodajOdabraniProizvod");
                javax.swing.Action izborKupca = actions.get("IzborKupca");
                
                gridPretrazivanje.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "odaberi");
                gridPretrazivanje.getActionMap().put("odaberi", dodajOdabraniProizvod);
                
                gridPretrazivanje.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "povratak");
                gridPretrazivanje.getActionMap().put("povratak", unosRacuna);

                gridKupci.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "nastavak");
                gridKupci.getActionMap().put("nastavak", actions.get("IzborNacinaPlacanja"));
                
                gridKupci.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "povratak");
                gridKupci.getActionMap().put("povratak", unosRacuna);
                
                searchKupci.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "povratak");
                searchKupci.getActionMap().put("povratak", unosRacuna);
                
                listaNacinaPlacanja.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "povratak");
                listaNacinaPlacanja.getActionMap().put("povratak", izborKupca);
                
                napomena.getInputMap().put(KeyStroke.getKeyStroke("UP"), "prethodni");
                napomena.getActionMap().put("prethodni", actions.get("IzborPrethodnogNacinaPlacanja"));
                
                napomena.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "sljedeci");
                napomena.getActionMap().put("sljedeci", actions.get("IzborSljedecegNacinaPlacanja"));
                
                zaporkaOperatera.requestFocus();
            }
        });
        
        PratiIzmjene();
    }
    
    public String getTrenutniSearchQuery() {
        return trenutniSearchQuery;
    }
    
    private void PodesiZadanuNaredbu()
    {
        if(getTrenutniSearchQuery().isEmpty())
        {
            if(isGotovinskiRacun())
            {
                getRootPane().setDefaultButton(cmdGotovinskiRacun);
            }
            else
            {
                getRootPane().setDefaultButton(cmdSpremi);
            }
        }
        else
        {
            getRootPane().setDefaultButton(cmdDodaj);
        }
    }

    public void setTrenutniSearchQuery(String trenutniSearchQuery) {
        this.trenutniSearchQuery = trenutniSearchQuery;
        
        if(trenutniSearchQuery.startsWith("*"))
        {
            if(getBrojStavki() > 0)
            {
                cmdDodaj.setText("Količina");
            }
            else
            {
                Tasks.showMessage("UPOZORENJE: Morate unesti stavku prije promjene količine");
                cmdDodaj.setText("Dodaj");
                searchQuery.setText("");
            }
        }
        else if(trenutniSearchQuery.startsWith("-"))
        {
            if(getBrojStavki() > 0)
                cmdDodaj.setText("Popust");
            else
            {
                Tasks.showMessage("UPOZORENJE: Morate unesti stavku prije primjene popusta");
                cmdDodaj.setText("Dodaj");
                searchQuery.setText("");
            }
        }
        else if(trenutniSearchQuery.length() == 0 || trenutniSearchQuery.matches("\\d*"))
        {
            cmdDodaj.setText("Dodaj");
        }
        else
        {
            cmdDodaj.setText("Pretraži");
        }
        
        PodesiZadanuNaredbu();
    }

    public String getTrenutniKupacSearchQuery() {
        return trenutniKupacSearchQuery;
    }
    
    public void PodesiKupacZadanaNaredba()
    {
        SimpleModelKupaca model = (SimpleModelKupaca) gridKupci.getModel();

        if(model.getFilter().equals(searchKupci.getText()))
        {
            getRootPane().setDefaultButton(cmdKupciNastavak);
        }
        else
        {
            getRootPane().setDefaultButton(cmdKupciSearch);
        }
    }

    public void setTrenutniKupacSearchQuery(String trenutniKupacSearchQuery) {
        
        if(!this.trenutniKupacSearchQuery.equals(trenutniKupacSearchQuery))
        {
            this.trenutniKupacSearchQuery = trenutniKupacSearchQuery;
            PodesiKupacZadanaNaredba();
        }
    }

    @Action
    public void IzborPrethodnogNacinaPlacanja()
    {
        if(listaNacinaPlacanja.getSelectedIndex() > 0)
        {
            listaNacinaPlacanja.setSelectedIndex(listaNacinaPlacanja.getSelectedIndex() - 1);
            listaNacinaPlacanja.ensureIndexIsVisible(listaNacinaPlacanja.getSelectedIndex());
        }
    }
    
    @Action
    public void IzborSljedecegNacinaPlacanja()
    {
        if(listaNacinaPlacanja.getSelectedIndex() < listaNacinaPlacanja.getModel().getSize() - 1)
        {
            listaNacinaPlacanja.setSelectedIndex(listaNacinaPlacanja.getSelectedIndex() + 1);
            listaNacinaPlacanja.ensureIndexIsVisible(listaNacinaPlacanja.getSelectedIndex());
        }
    }
    
    public String getTrenutniIznosUplate() {
        return trenutniIznosUplate;
    }

    public void setTrenutniIznosUplate(String trenutniIznosUplate) {
        
        if(!this.trenutniIznosUplate.equals(trenutniIznosUplate))
        {
            this.trenutniIznosUplate = trenutniIznosUplate;
            IzracunajOstatak();
        }
    }
    
    public SimpleModelProizvoda getModelProizvoda()
    {
        return modelProizvoda;
    }
    
    private void PratiIzmjene()
    {
        KorisnikEvents.Events().addListener(new EventListener() {

            @Override
            public void doWork(Object source, EventArgs eventArgs) {
                SimpleModelKupaca model = (SimpleModelKupaca) gridKupci.getModel();
                
                List<Korisnik> kupci = kupciQuery.getResultList();
                model.setKupci(kupci);
                
                gridKupci.setRowSelectionInterval(0, 0);
            }
        });
        
        grid.getModel().addTableModelListener(new TableModelListener() {
            
            @Override
            public void tableChanged(TableModelEvent e) {
                
                if(e instanceof SimpleTableModelEvent && ((SimpleTableModelEvent)e).isClearDisplay())
                {
                    PromijeniPrikazKupcu("", 0, 0, "CLR");
                }
                else
                {
                    SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
                
                    if(model.getBrojStavki() > 0)
                    {
                        List<Stavke> stavke = model.getStavke();
                        Stavke stavka = stavke.get(stavke.size() - 1);
                        PromijeniPrikazKupcu(stavka.getRoba(), stavka.getKolicina(), stavka.getMaloprodajnaCijena() * (1 - stavka.getPopust()));
                    }

                    IzracunajUkupno();
                }
            }
        });
    }
    
    public int getGridRowHeight()
    {
        return 50;
    }
    
    public Converter<Stavke, Object> createConverter()
    {
        return new Converter<Stavke, Object>() {

            @Override
            public Object convertForward(Stavke value) {
                return value;
            }

            @Override
            public Stavke convertReverse(Object value) {
                if(value == null)
                    return null;
                else if(((HashMap)value).get("column0") instanceof Stavke)
                    return (Stavke) ((HashMap)value).get("column0");
                else
                    return null;
            }
        };
    }

    public Stavke getOdabranaStavka() {
        return odabranaStavka;
    }

    public void setOdabranaStavka(Stavke odabranaStavka) {
        this.odabranaStavka = odabranaStavka;
    }
    
    public Racun getRacun() {
        return racun;
    }

    public void setRacun(Racun racun) {
        this.racun = racun;
    }

    public Operater getOdabraniOperater() {
        return odabraniOperater;
    }
    
    public TableModel getModelStavki()
    {
        return new SimpleModelStavki(getRacun(), null);
    }
    
    public TableModel getModelKupaca()
    {
        return new SimpleModelKupaca();
    }

    public void setOdabraniOperater(Operater odabraniOperater) {
        this.odabraniOperater = odabraniOperater;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        keyboard = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        manager = java.beans.Beans.isDesignTime() ? null : Persistence.createEntityManagerFactory().createEntityManager();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(SimpleInvoice.class);
        queryOperateri = java.beans.Beans.isDesignTime() ? null : manager.createQuery(resourceMap.getString("queryOperateri.query")); // NOI18N
        popisOperatera = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : queryOperateri.getResultList();
        queryProizvodi = java.beans.Beans.isDesignTime() ? null : manager.createQuery(resourceMap.getString("queryProizvodi.query")); // NOI18N
        popisProizvoda = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : queryProizvodi.getResultList();
        kupciQuery = java.beans.Beans.isDesignTime() ? null : manager.createQuery(resourceMap.getString("kupciQuery.query")); // NOI18N
        kupciQuery.setHint("eclipselink.refresh", "true");
        templateQuery = java.beans.Beans.isDesignTime() ? null : manager.createQuery(resourceMap.getString("templateQuery.query")); // NOI18N
        templates = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : templateQuery.getResultList();
        izborOperatera = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaOperatera = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        zaporkaOperatera = new javax.swing.JPasswordField();
        prijavaOperatera = new javax.swing.JButton();
        prijavaOperatera1 = new javax.swing.JButton();
        unosRacuna = new javax.swing.JPanel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        racunUkupno = new javax.swing.JLabel();
        javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        grid = new javax.swing.JTable();
        javax.swing.JPanel jPanel10 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel11 = new javax.swing.JPanel();
        searchQuery = new javax.swing.JTextField();
        cmdDodaj = new javax.swing.JButton();
        cmdIzbrisi = new javax.swing.JButton();
        cmdGotovinskiRacun = new javax.swing.JButton();
        cmdSpremi = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        izborKupca = new javax.swing.JPanel();
        javax.swing.JPanel jPanel18 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        izborKupcaUkupno = new javax.swing.JLabel();
        javax.swing.JPanel jPanel17 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        gridKupci = new javax.swing.JTable();
        javax.swing.JPanel jPanel19 = new javax.swing.JPanel();
        searchKupci = new javax.swing.JTextField();
        cmdKupciSearch = new javax.swing.JButton();
        cmdKupciDodaj = new javax.swing.JButton();
        cmdKupciBack = new javax.swing.JButton();
        cmdKupciNastavak = new javax.swing.JButton();
        cmdKupciZatvori = new javax.swing.JButton();
        izborNacinaPlacanja = new javax.swing.JPanel();
        javax.swing.JPanel jPanel12 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        listaNacinaPlacanja = new javax.swing.JList();
        javax.swing.JPanel jPanel16 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        izborNacinaPlacanjaUkupno = new javax.swing.JLabel();
        javax.swing.JPanel jPanel13 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel14 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel15 = new javax.swing.JPanel();
        nacinPlacanjaPovratak = new javax.swing.JButton();
        cmdNacinPlacanjaSpremi = new javax.swing.JButton();
        nacinPlacanjaZatvori = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        napomena = new javax.swing.JTextField();
        zakljucenRacun = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        opisUnosa = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        iznosUplate = new javax.swing.JTextField();
        cmdNoviRacun = new javax.swing.JButton();
        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        opisIznosZaVratiti = new javax.swing.JLabel();
        iznosZaVratiti = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        iznosRacuna = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        izborProizvoda = new javax.swing.JPanel();
        javax.swing.JPanel jPanel24 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel25 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane5 = new javax.swing.JScrollPane();
        gridPretrazivanje = new javax.swing.JTable();
        javax.swing.JPanel jPanel26 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel27 = new javax.swing.JPanel();
        cmdPretrazivanjePovratak = new javax.swing.JButton();
        cmdPretrazivanjeOdaberi = new javax.swing.JButton();
        cmdPretrazivanjeOdaberi1 = new javax.swing.JButton();

        keyboard.setName("keyboard"); // NOI18N
        keyboard.setLayout(new java.awt.GridBagLayout());

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButton2.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton2);

        jButton1.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton1);

        jButton3.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton3);

        jButton4.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton4);

        jButton5.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton5);

        jButton6.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton6);

        jButton7.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton7);

        jButton8.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton8);

        jButton9.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton9);

        jButton10.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jButton10.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton10);

        jButton11.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N
        jButton11.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton11);

        jButton12.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N
        jButton12.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.add(jButton12);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        keyboard.add(jPanel3, gridBagConstraints);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButton13.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setName("jButton13"); // NOI18N
        jButton13.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton13);

        jButton14.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setName("jButton14"); // NOI18N
        jButton14.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton14);

        jButton15.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setName("jButton15"); // NOI18N
        jButton15.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton15);

        jButton16.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N
        jButton16.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton16);

        jButton17.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N
        jButton17.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton17);

        jButton18.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton18.setName("jButton18"); // NOI18N
        jButton18.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton18);

        jButton19.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton19.setName("jButton19"); // NOI18N
        jButton19.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton19);

        jButton20.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setName("jButton20"); // NOI18N
        jButton20.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton20);

        jButton21.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton21.setName("jButton21"); // NOI18N
        jButton21.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton21);

        jButton22.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setName("jButton22"); // NOI18N
        jButton22.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton22);

        jButton23.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton23.setName("jButton23"); // NOI18N
        jButton23.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton23);

        jButton24.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton24.setName("jButton24"); // NOI18N
        jButton24.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel4.add(jButton24);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        keyboard.add(jPanel4, gridBagConstraints);

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButton25.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton25.setName("jButton25"); // NOI18N
        jButton25.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton25);

        jButton26.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton26.setName("jButton26"); // NOI18N
        jButton26.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton26);

        jButton27.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton27.setName("jButton27"); // NOI18N
        jButton27.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton27);

        jButton28.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton28.setName("jButton28"); // NOI18N
        jButton28.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton28);

        jButton29.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton29.setName("jButton29"); // NOI18N
        jButton29.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton29);

        jButton30.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton30.setName("jButton30"); // NOI18N
        jButton30.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton30);

        jButton31.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton31.setName("jButton31"); // NOI18N
        jButton31.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton31);

        jButton32.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton32.setName("jButton32"); // NOI18N
        jButton32.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel5.add(jButton32);

        jButton33.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton33.setName("jButton33"); // NOI18N
        jButton33.setPreferredSize(new java.awt.Dimension(125, 60));
        jPanel5.add(jButton33);

        jButton36.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton36.setText(resourceMap.getString("jButton36.text")); // NOI18N
        jButton36.setName("jButton36"); // NOI18N
        jButton36.setPreferredSize(new java.awt.Dimension(125, 60));
        jPanel5.add(jButton36);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        keyboard.add(jPanel5, gridBagConstraints);

        manager.setFlushMode(javax.persistence.FlushModeType.COMMIT);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                ZatvaranjeForme(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        izborOperatera.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        izborOperatera.setName("izbor-operatera"); // NOI18N
        izborOperatera.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listaOperatera.setFont(resourceMap.getFont("listaOperatera.font")); // NOI18N
        listaOperatera.setFixedCellHeight(50);
        listaOperatera.setName("listaOperatera"); // NOI18N

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, popisOperatera, listaOperatera);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        bindingGroup.addBinding(jListBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${odabraniOperater}"), listaOperatera, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"), "odabrani-operater");
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(listaOperatera);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 10, 0));
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel2.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        izborOperatera.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.BorderLayout(5, 5));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel6.add(jLabel2, java.awt.BorderLayout.NORTH);

        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        zaporkaOperatera.setFont(resourceMap.getFont("zaporkaOperatera.font")); // NOI18N
        zaporkaOperatera.setText(resourceMap.getString("zaporkaOperatera.text")); // NOI18N
        zaporkaOperatera.setName("zaporkaOperatera"); // NOI18N
        zaporkaOperatera.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                zaporkaFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                zaporkaBlur(evt);
            }
        });
        zaporkaOperatera.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                zaporkaIzborOperatera(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(zaporkaOperatera, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(SimpleInvoice.class, this);
        prijavaOperatera.setAction(actionMap.get("Prijava")); // NOI18N
        prijavaOperatera.setFont(resourceMap.getFont("prijavaOperatera.font")); // NOI18N
        prijavaOperatera.setText(resourceMap.getString("prijavaOperatera.text")); // NOI18N
        prijavaOperatera.setMargin(new java.awt.Insets(5, 10, 5, 10));
        prijavaOperatera.setName("prijavaOperatera"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(prijavaOperatera, gridBagConstraints);

        prijavaOperatera1.setAction(actionMap.get("Zatvori")); // NOI18N
        prijavaOperatera1.setFont(resourceMap.getFont("prijavaOperatera1.font")); // NOI18N
        prijavaOperatera1.setText(resourceMap.getString("prijavaOperatera1.text")); // NOI18N
        prijavaOperatera1.setMargin(new java.awt.Insets(5, 10, 5, 10));
        prijavaOperatera1.setName("prijavaOperatera1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(prijavaOperatera1, gridBagConstraints);

        jPanel7.add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel6.add(jPanel7, java.awt.BorderLayout.SOUTH);

        izborOperatera.add(jPanel6, java.awt.BorderLayout.SOUTH);

        getContentPane().add(izborOperatera, "izbor-operatera");

        unosRacuna.setName("unosRacuna"); // NOI18N
        unosRacuna.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 5), javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.insideBorder.title")))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        racunUkupno.setFont(resourceMap.getFont("racunUkupno.font")); // NOI18N
        racunUkupno.setText(resourceMap.getString("racunUkupno.text")); // NOI18N
        racunUkupno.setName("racunUkupno"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 30, 30);
        jPanel1.add(racunUkupno, gridBagConstraints);

        unosRacuna.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 5));
        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        grid.setFont(resourceMap.getFont("grid.font")); // NOI18N
        grid.setModel(getModelStavki());
        grid.setName("grid"); // NOI18N
        grid.setRowHeight(50);
        grid.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        grid.getTableHeader().setReorderingAllowed(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${odabranaStavka}"), grid, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"), "odabrana-stavka");
        binding.setConverter(createConverter());
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(grid);

        jPanel9.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.BorderLayout());

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel10.add(jLabel4, java.awt.BorderLayout.PAGE_START);

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new java.awt.GridBagLayout());

        searchQuery.setFont(resourceMap.getFont("jButton35.font")); // NOI18N
        searchQuery.setName("searchQuery"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${trenutniSearchQuery}"), searchQuery, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        searchQuery.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                changeDefaultAction(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                removeDefaultAction(evt);
            }
        });
        searchQuery.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchQueryChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel11.add(searchQuery, gridBagConstraints);

        cmdDodaj.setAction(actionMap.get("DodajStavku")); // NOI18N
        cmdDodaj.setFont(resourceMap.getFont("cmdDodaj.font")); // NOI18N
        cmdDodaj.setText(resourceMap.getString("cmdDodaj.text")); // NOI18N
        cmdDodaj.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdDodaj.setMaximumSize(new java.awt.Dimension(103, 60));
        cmdDodaj.setName("cmdDodaj"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel11.add(cmdDodaj, gridBagConstraints);

        cmdIzbrisi.setAction(actionMap.get("IzbrisiStavku")); // NOI18N
        cmdIzbrisi.setFont(resourceMap.getFont("cmdIzbrisi.font")); // NOI18N
        cmdIzbrisi.setText(resourceMap.getString("cmdIzbrisi.text")); // NOI18N
        cmdIzbrisi.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdIzbrisi.setMaximumSize(new java.awt.Dimension(103, 60));
        cmdIzbrisi.setName("cmdIzbrisi"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel11.add(cmdIzbrisi, gridBagConstraints);

        cmdGotovinskiRacun.setAction(actionMap.get("SpremiGotovinskiRacun")); // NOI18N
        cmdGotovinskiRacun.setFont(resourceMap.getFont("cmdGotovinskiRacun.font")); // NOI18N
        cmdGotovinskiRacun.setText(resourceMap.getString("cmdGotovinskiRacun.text")); // NOI18N
        cmdGotovinskiRacun.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdGotovinskiRacun.setName("cmdGotovinskiRacun"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel11.add(cmdGotovinskiRacun, gridBagConstraints);

        cmdSpremi.setAction(actionMap.get("IzborKupca")); // NOI18N
        cmdSpremi.setFont(resourceMap.getFont("cmdSpremi.font")); // NOI18N
        cmdSpremi.setText(resourceMap.getString("cmdSpremi.text")); // NOI18N
        cmdSpremi.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdSpremi.setName("cmdSpremi"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel11.add(cmdSpremi, gridBagConstraints);

        jButton35.setAction(actionMap.get("Zatvori")); // NOI18N
        jButton35.setFont(resourceMap.getFont("jButton35.font")); // NOI18N
        jButton35.setText(resourceMap.getString("jButton35.text")); // NOI18N
        jButton35.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButton35.setMaximumSize(new java.awt.Dimension(110, 51));
        jButton35.setMinimumSize(new java.awt.Dimension(110, 51));
        jButton35.setName("jButton35"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel11.add(jButton35, gridBagConstraints);

        jPanel10.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel9.add(jPanel10, java.awt.BorderLayout.PAGE_END);

        unosRacuna.add(jPanel9, java.awt.BorderLayout.CENTER);

        getContentPane().add(unosRacuna, "unos-racuna");

        izborKupca.setName("izborKupca"); // NOI18N
        izborKupca.setLayout(new java.awt.BorderLayout());

        jPanel18.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 0));
        jPanel18.setName("jPanel18"); // NOI18N
        jPanel18.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 10, 0));
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel18.add(jLabel6, java.awt.BorderLayout.CENTER);

        izborKupcaUkupno.setFont(new java.awt.Font("Ubuntu", 0, 48));
        izborKupcaUkupno.setText(resourceMap.getString("izborKupcaUkupno.text")); // NOI18N
        izborKupcaUkupno.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        izborKupcaUkupno.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 10, 10));
        izborKupcaUkupno.setName("izborKupcaUkupno"); // NOI18N
        jPanel18.add(izborKupcaUkupno, java.awt.BorderLayout.LINE_END);

        izborKupca.add(jPanel18, java.awt.BorderLayout.PAGE_START);

        jPanel17.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel17.setName("jPanel17"); // NOI18N
        jPanel17.setLayout(new java.awt.BorderLayout());

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        gridKupci.setFont(new java.awt.Font("Ubuntu", 0, 24));
        gridKupci.setModel(getModelKupaca());
        gridKupci.setName("gridKupci"); // NOI18N
        gridKupci.setRowHeight(getGridRowHeight());
        gridKupci.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        gridKupci.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                gridKupciFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                gridKupciBlur(evt);
            }
        });
        jScrollPane4.setViewportView(gridKupci);

        jPanel17.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        izborKupca.add(jPanel17, java.awt.BorderLayout.CENTER);

        jPanel19.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        jPanel19.setName("jPanel19"); // NOI18N
        jPanel19.setLayout(new java.awt.GridBagLayout());

        searchKupci.setFont(resourceMap.getFont("searchKupci.font")); // NOI18N
        searchKupci.setName("searchKupci"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${trenutniKupacSearchQuery}"), searchKupci, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        searchKupci.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                seachKupciFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchKupciBlur(evt);
            }
        });
        searchKupci.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchKupciQueryChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel19.add(searchKupci, gridBagConstraints);

        cmdKupciSearch.setAction(actionMap.get("SearchKupaca")); // NOI18N
        cmdKupciSearch.setFont(resourceMap.getFont("cmdKupciSearch.font")); // NOI18N
        cmdKupciSearch.setText(resourceMap.getString("cmdKupciSearch.text")); // NOI18N
        cmdKupciSearch.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdKupciSearch.setName("cmdKupciSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel19.add(cmdKupciSearch, gridBagConstraints);

        cmdKupciDodaj.setAction(actionMap.get("NoviKupac")); // NOI18N
        cmdKupciDodaj.setFont(resourceMap.getFont("cmdKupciDodaj.font")); // NOI18N
        cmdKupciDodaj.setText(resourceMap.getString("cmdKupciDodaj.text")); // NOI18N
        cmdKupciDodaj.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdKupciDodaj.setMaximumSize(new java.awt.Dimension(119, 50));
        cmdKupciDodaj.setMinimumSize(new java.awt.Dimension(119, 50));
        cmdKupciDodaj.setName("cmdKupciDodaj"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel19.add(cmdKupciDodaj, gridBagConstraints);

        cmdKupciBack.setAction(actionMap.get("UnosRacuna")); // NOI18N
        cmdKupciBack.setFont(resourceMap.getFont("cmdKupciBack.font")); // NOI18N
        cmdKupciBack.setText(resourceMap.getString("cmdKupciBack.text")); // NOI18N
        cmdKupciBack.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdKupciBack.setName("cmdKupciBack"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel19.add(cmdKupciBack, gridBagConstraints);

        cmdKupciNastavak.setAction(actionMap.get("IzborNacinaPlacanja")); // NOI18N
        cmdKupciNastavak.setFont(resourceMap.getFont("cmdKupciNastavak.font")); // NOI18N
        cmdKupciNastavak.setText(resourceMap.getString("cmdKupciNastavak.text")); // NOI18N
        cmdKupciNastavak.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdKupciNastavak.setName("cmdKupciNastavak"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel19.add(cmdKupciNastavak, gridBagConstraints);

        cmdKupciZatvori.setAction(actionMap.get("Zatvori")); // NOI18N
        cmdKupciZatvori.setFont(resourceMap.getFont("cmdKupciZatvori.font")); // NOI18N
        cmdKupciZatvori.setText(resourceMap.getString("cmdKupciZatvori.text")); // NOI18N
        cmdKupciZatvori.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdKupciZatvori.setName("cmdKupciZatvori"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel19.add(cmdKupciZatvori, gridBagConstraints);

        izborKupca.add(jPanel19, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(izborKupca, "izbor-kupca");

        izborNacinaPlacanja.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        izborNacinaPlacanja.setName("izborNacinaPlacanja"); // NOI18N
        izborNacinaPlacanja.setLayout(new java.awt.BorderLayout(5, 0));

        jPanel12.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 5));
        jPanel12.setName("jPanel12"); // NOI18N
        jPanel12.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        listaNacinaPlacanja.setFont(resourceMap.getFont("listaNacinaPlacanja.font")); // NOI18N
        listaNacinaPlacanja.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listaNacinaPlacanja.setFixedCellHeight(50);
        listaNacinaPlacanja.setName("listaNacinaPlacanja"); // NOI18N
        listaNacinaPlacanja.setNextFocusableComponent(napomena);

        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, templates, listaNacinaPlacanja, "odabir-nacina-placanja");
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${tip}"));
        bindingGroup.addBinding(jListBinding);

        listaNacinaPlacanja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                listaNacinaPlacanjaFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                listaNacinaPlacanjaBlur(evt);
            }
        });
        jScrollPane3.setViewportView(listaNacinaPlacanja);

        jPanel12.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel16.setName("jPanel16"); // NOI18N
        jPanel16.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(new java.awt.Font("Ubuntu", 0, 36));
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 10, 0));
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel16.add(jLabel3, java.awt.BorderLayout.CENTER);

        izborNacinaPlacanjaUkupno.setFont(resourceMap.getFont("izborNacinaPlacanjaUkupno.font")); // NOI18N
        izborNacinaPlacanjaUkupno.setText(resourceMap.getString("izborNacinaPlacanjaUkupno.text")); // NOI18N
        izborNacinaPlacanjaUkupno.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        izborNacinaPlacanjaUkupno.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 10, 5));
        izborNacinaPlacanjaUkupno.setName("izborNacinaPlacanjaUkupno"); // NOI18N
        izborNacinaPlacanjaUkupno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel16.add(izborNacinaPlacanjaUkupno, java.awt.BorderLayout.LINE_END);
        izborNacinaPlacanjaUkupno.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel5.AccessibleContext.accessibleName")); // NOI18N

        jPanel12.add(jPanel16, java.awt.BorderLayout.PAGE_START);

        izborNacinaPlacanja.add(jPanel12, java.awt.BorderLayout.CENTER);

        jPanel13.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setLayout(new java.awt.BorderLayout());

        jPanel15.setName("jPanel15"); // NOI18N
        jPanel15.setLayout(new java.awt.GridBagLayout());

        nacinPlacanjaPovratak.setAction(actionMap.get("IzborKupca")); // NOI18N
        nacinPlacanjaPovratak.setFont(resourceMap.getFont("prijavaOperatera3.font")); // NOI18N
        nacinPlacanjaPovratak.setText(resourceMap.getString("nacinPlacanjaPovratak.text")); // NOI18N
        nacinPlacanjaPovratak.setMargin(new java.awt.Insets(5, 10, 5, 10));
        nacinPlacanjaPovratak.setName("nacinPlacanjaPovratak"); // NOI18N
        nacinPlacanjaPovratak.setNextFocusableComponent(cmdNacinPlacanjaSpremi);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel15.add(nacinPlacanjaPovratak, gridBagConstraints);

        cmdNacinPlacanjaSpremi.setAction(actionMap.get("SpremiRacun")); // NOI18N
        cmdNacinPlacanjaSpremi.setFont(resourceMap.getFont("cmdNacinPlacanjaSpremi.font")); // NOI18N
        cmdNacinPlacanjaSpremi.setText(resourceMap.getString("cmdNacinPlacanjaSpremi.text")); // NOI18N
        cmdNacinPlacanjaSpremi.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdNacinPlacanjaSpremi.setName("cmdNacinPlacanjaSpremi"); // NOI18N
        cmdNacinPlacanjaSpremi.setNextFocusableComponent(nacinPlacanjaZatvori);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel15.add(cmdNacinPlacanjaSpremi, gridBagConstraints);

        nacinPlacanjaZatvori.setAction(actionMap.get("Zatvori")); // NOI18N
        nacinPlacanjaZatvori.setFont(resourceMap.getFont("nacinPlacanjaZatvori.font")); // NOI18N
        nacinPlacanjaZatvori.setText(resourceMap.getString("nacinPlacanjaZatvori.text")); // NOI18N
        nacinPlacanjaZatvori.setMargin(new java.awt.Insets(5, 10, 5, 10));
        nacinPlacanjaZatvori.setName("nacinPlacanjaZatvori"); // NOI18N
        nacinPlacanjaZatvori.setNextFocusableComponent(listaNacinaPlacanja);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel15.add(nacinPlacanjaZatvori, gridBagConstraints);

        jPanel14.add(jPanel15, java.awt.BorderLayout.LINE_END);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel14.add(jLabel11, java.awt.BorderLayout.PAGE_START);

        napomena.setFont(new java.awt.Font("Ubuntu", 0, 24));
        napomena.setText(resourceMap.getString("napomena.text")); // NOI18N
        napomena.setName("napomena"); // NOI18N
        napomena.setNextFocusableComponent(nacinPlacanjaPovratak);
        napomena.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                napomenaFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                napomenaBlur(evt);
            }
        });
        jPanel14.add(napomena, java.awt.BorderLayout.CENTER);

        jPanel13.add(jPanel14, java.awt.BorderLayout.SOUTH);

        izborNacinaPlacanja.add(jPanel13, java.awt.BorderLayout.SOUTH);

        getContentPane().add(izborNacinaPlacanja, "nacin-placanja");

        zakljucenRacun.setName("zakljucenRacun"); // NOI18N
        zakljucenRacun.setLayout(new java.awt.BorderLayout());

        jPanel23.setName("jPanel23"); // NOI18N
        jPanel23.setLayout(new java.awt.GridBagLayout());

        jPanel21.setMaximumSize(new java.awt.Dimension(10000, 500));
        jPanel21.setName("jPanel21"); // NOI18N
        jPanel21.setLayout(new javax.swing.BoxLayout(jPanel21, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel5.setFont(new java.awt.Font("Ubuntu", 0, 72));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setMaximumSize(new java.awt.Dimension(10000, 56));
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel21.add(jLabel5);

        jPanel23.add(jPanel21, new java.awt.GridBagConstraints());

        jPanel28.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 3, 5, 3));
        jPanel28.setName("jPanel28"); // NOI18N
        jPanel28.setLayout(new java.awt.BorderLayout());

        opisUnosa.setText(resourceMap.getString("opisUnosa.text")); // NOI18N
        opisUnosa.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 3, 0));
        opisUnosa.setName("opisUnosa"); // NOI18N
        jPanel28.add(opisUnosa, java.awt.BorderLayout.PAGE_START);

        jPanel32.setName("jPanel32"); // NOI18N
        jPanel32.setLayout(new java.awt.GridBagLayout());

        iznosUplate.setFont(resourceMap.getFont("iznosUplate.font")); // NOI18N
        iznosUplate.setName("iznosUplate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${trenutniIznosUplate}"), iznosUplate, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        iznosUplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                iznosUplateFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                iznosUplateBlur(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel32.add(iznosUplate, gridBagConstraints);

        cmdNoviRacun.setAction(actionMap.get("UnosNovogRacuna")); // NOI18N
        cmdNoviRacun.setFont(new java.awt.Font("Ubuntu", 0, 24));
        cmdNoviRacun.setText(resourceMap.getString("cmdNoviRacun.text")); // NOI18N
        cmdNoviRacun.setMargin(new java.awt.Insets(0, 15, 0, 15));
        cmdNoviRacun.setName("cmdNoviRacun"); // NOI18N
        cmdNoviRacun.setNextFocusableComponent(iznosUplate);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel32.add(cmdNoviRacun, gridBagConstraints);

        jPanel28.add(jPanel32, java.awt.BorderLayout.PAGE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel23.add(jPanel28, gridBagConstraints);

        jPanel29.setName("jPanel29"); // NOI18N
        jPanel29.setLayout(new java.awt.BorderLayout());

        jPanel30.setMinimumSize(new java.awt.Dimension(211, 10));
        jPanel30.setName("jPanel30"); // NOI18N

        opisIznosZaVratiti.setFont(new java.awt.Font("Ubuntu", 0, 24));
        opisIznosZaVratiti.setText(resourceMap.getString("opisIznosZaVratiti.text")); // NOI18N
        opisIznosZaVratiti.setName("opisIznosZaVratiti"); // NOI18N
        jPanel30.add(opisIznosZaVratiti);

        iznosZaVratiti.setFont(resourceMap.getFont("iznosZaVratiti.font")); // NOI18N
        iznosZaVratiti.setText(resourceMap.getString("iznosZaVratiti.text")); // NOI18N
        iznosZaVratiti.setName("iznosZaVratiti"); // NOI18N
        jPanel30.add(iznosZaVratiti);

        jPanel29.add(jPanel30, java.awt.BorderLayout.LINE_END);

        jPanel31.setName("jPanel31"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Ubuntu", 0, 24));
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel31.add(jLabel7);

        iznosRacuna.setFont(new java.awt.Font("Ubuntu", 0, 24));
        iznosRacuna.setText(resourceMap.getString("iznosRacuna.text")); // NOI18N
        iznosRacuna.setName("iznosRacuna"); // NOI18N
        jPanel31.add(iznosRacuna);

        jPanel29.add(jPanel31, java.awt.BorderLayout.LINE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel23.add(jPanel29, gridBagConstraints);

        zakljucenRacun.add(jPanel23, java.awt.BorderLayout.CENTER);

        jPanel22.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel22.setName("jPanel22"); // NOI18N
        jPanel22.setLayout(new java.awt.BorderLayout());

        jSeparator1.setName("jSeparator1"); // NOI18N
        jPanel22.add(jSeparator1, java.awt.BorderLayout.PAGE_START);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel22.add(jLabel8, java.awt.BorderLayout.PAGE_END);

        zakljucenRacun.add(jPanel22, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(zakljucenRacun, "zakljucak-racuna");

        izborProizvoda.setName("izborProizvoda"); // NOI18N
        izborProizvoda.setLayout(new java.awt.BorderLayout());

        jPanel24.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 0));
        jPanel24.setName("jPanel24"); // NOI18N
        jPanel24.setLayout(new java.awt.BorderLayout());

        jLabel9.setFont(new java.awt.Font("Ubuntu", 0, 36));
        jLabel9.setText("Pretraživanje"); // NOI18N
        jLabel9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 10, 0));
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel24.add(jLabel9, java.awt.BorderLayout.CENTER);

        izborProizvoda.add(jPanel24, java.awt.BorderLayout.PAGE_START);

        jPanel25.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel25.setName("jPanel25"); // NOI18N
        jPanel25.setLayout(new java.awt.BorderLayout());

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        gridPretrazivanje.setFont(new java.awt.Font("Ubuntu", 0, 24));
        gridPretrazivanje.setModel(getModelProizvoda());
        gridPretrazivanje.setName("gridPretrazivanje"); // NOI18N
        gridPretrazivanje.setRowHeight(getGridRowHeight());
        gridPretrazivanje.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        gridPretrazivanje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                gridPretrazivanjeFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                gridPretrazivanjeBlur(evt);
            }
        });
        jScrollPane5.setViewportView(gridPretrazivanje);

        jPanel25.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        izborProizvoda.add(jPanel25, java.awt.BorderLayout.CENTER);

        jPanel26.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        jPanel26.setName("jPanel26"); // NOI18N
        jPanel26.setLayout(new java.awt.BorderLayout());

        jPanel27.setName("jPanel27"); // NOI18N
        jPanel27.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        cmdPretrazivanjePovratak.setAction(actionMap.get("UnosRacuna")); // NOI18N
        cmdPretrazivanjePovratak.setFont(new java.awt.Font("Ubuntu", 0, 24));
        cmdPretrazivanjePovratak.setText(resourceMap.getString("cmdPretrazivanjePovratak.text")); // NOI18N
        cmdPretrazivanjePovratak.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdPretrazivanjePovratak.setName("cmdPretrazivanjePovratak"); // NOI18N
        jPanel27.add(cmdPretrazivanjePovratak);

        cmdPretrazivanjeOdaberi.setAction(actionMap.get("DodajOdabraniProizvod")); // NOI18N
        cmdPretrazivanjeOdaberi.setFont(new java.awt.Font("Ubuntu", 0, 24));
        cmdPretrazivanjeOdaberi.setText(resourceMap.getString("cmdPretrazivanjeOdaberi.text")); // NOI18N
        cmdPretrazivanjeOdaberi.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdPretrazivanjeOdaberi.setName("cmdPretrazivanjeOdaberi"); // NOI18N
        jPanel27.add(cmdPretrazivanjeOdaberi);

        cmdPretrazivanjeOdaberi1.setAction(actionMap.get("Zatvori")); // NOI18N
        cmdPretrazivanjeOdaberi1.setFont(new java.awt.Font("Ubuntu", 0, 24));
        cmdPretrazivanjeOdaberi1.setText(resourceMap.getString("cmdPretrazivanjeOdaberi1.text")); // NOI18N
        cmdPretrazivanjeOdaberi1.setMargin(new java.awt.Insets(5, 10, 5, 10));
        cmdPretrazivanjeOdaberi1.setName("cmdPretrazivanjeOdaberi1"); // NOI18N
        jPanel27.add(cmdPretrazivanjeOdaberi1);

        jPanel26.add(jPanel27, java.awt.BorderLayout.LINE_END);

        izborProizvoda.add(jPanel26, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(izborProizvoda, "pretrazivanje");

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void searchQueryChanged(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchQueryChanged
        
        int index = grid.getSelectedRow();
         
        if(evt.getKeyCode() == KeyEvent.VK_UP)
        {
            if(index > 0)
            {
                index = index - 1;
                grid.setRowSelectionInterval(index, index);
                grid.scrollRectToVisible(grid.getCellRect(grid.getSelectedRow(), 0, true));
            }
        }
        else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if(index < grid.getRowCount() - 1)
            {
                index = index + 1;
                grid.setRowSelectionInterval(index, index);
                grid.scrollRectToVisible(grid.getCellRect(grid.getSelectedRow(), 0, true));
            }
        }
        else if(evt.getKeyCode() == KeyEvent.VK_DELETE)
        {
            IzbrisiStavku();
        }
    }//GEN-LAST:event_searchQueryChanged

    private void changeDefaultAction(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_changeDefaultAction
        
        PodesiZadanuNaredbu();
        
    }//GEN-LAST:event_changeDefaultAction

    private void removeDefaultAction(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_removeDefaultAction
        
        getRootPane().setDefaultButton(null);
        
    }//GEN-LAST:event_removeDefaultAction

    private void zaporkaFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_zaporkaFocus

        getRootPane().setDefaultButton(prijavaOperatera);
        
    }//GEN-LAST:event_zaporkaFocus

    private void seachKupciFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_seachKupciFocus
        
        searchKupci.selectAll();
        PodesiKupacZadanaNaredba();
    }//GEN-LAST:event_seachKupciFocus

    private void searchKupciBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKupciBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_searchKupciBlur

    private void searchKupciQueryChanged(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKupciQueryChanged
        
        int index = gridKupci.getSelectedRow();
        
        if(evt.getKeyCode() == KeyEvent.VK_UP)
        {
            if(index > 0)
            {
                index -= 1;
                gridKupci.setRowSelectionInterval(index, index);
                gridKupci.scrollRectToVisible(gridKupci.getCellRect(index, 0, true));
                
                //searchKupci.setText("");
            }
            
        }
        else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if(index < gridKupci.getRowCount() - 1)
            {
                index += 1;
                gridKupci.setRowSelectionInterval(index, index);
                gridKupci.scrollRectToVisible(gridKupci.getCellRect(index, 0, true));
                
                //searchKupci.setText("");
            }
        }
    }//GEN-LAST:event_searchKupciQueryChanged

    private void listaNacinaPlacanjaFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listaNacinaPlacanjaFocus
        
        getRootPane().setDefaultButton(cmdNacinPlacanjaSpremi);
        
    }//GEN-LAST:event_listaNacinaPlacanjaFocus

    private void listaNacinaPlacanjaBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listaNacinaPlacanjaBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_listaNacinaPlacanjaBlur

    @Action
    public void UnosNovogRacuna()
    {
        SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
        
        setRacun(new Racun());
        model.setRacun(getRacun());
        
        SimpleModelKupaca modelKupaca = (SimpleModelKupaca) gridKupci.getModel();
        modelKupaca.filter("");
        searchKupci.setText("");
        
        grid.setRowSelectionInterval(0, 0);
        gridKupci.setRowSelectionInterval(0, 0);
        listaNacinaPlacanja.setSelectedIndex(0);
        
        getRacun().setOperater(getOdabraniOperater());
        
        UnosRacuna();
    }
    
    private void zaporkaBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_zaporkaBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_zaporkaBlur

    private void zaporkaIzborOperatera(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_zaporkaIzborOperatera
        
        int index = listaOperatera.getSelectedIndex();
        
        if(evt.getKeyCode() == KeyEvent.VK_UP)
        {
            if(index > 0) listaOperatera.setSelectedIndex(index - 1);
        }
        else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if(index + 1 < listaOperatera.getModel().getSize())
                listaOperatera.setSelectedIndex(index + 1);
        }
        
    }//GEN-LAST:event_zaporkaIzborOperatera

    private void gridPretrazivanjeFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gridPretrazivanjeFocus
        getRootPane().setDefaultButton(cmdPretrazivanjeOdaberi);
    }//GEN-LAST:event_gridPretrazivanjeFocus

    private void gridPretrazivanjeBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gridPretrazivanjeBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_gridPretrazivanjeBlur

    private void gridKupciFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gridKupciFocus
        getRootPane().setDefaultButton(cmdKupciNastavak);
    }//GEN-LAST:event_gridKupciFocus

    private void gridKupciBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gridKupciBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_gridKupciBlur

    private void iznosUplateFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_iznosUplateFocus
        getRootPane().setDefaultButton(cmdNoviRacun);
        iznosUplate.selectAll();
    }//GEN-LAST:event_iznosUplateFocus

    private void iznosUplateBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_iznosUplateBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_iznosUplateBlur

    private void napomenaFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_napomenaFocus
        getRootPane().setDefaultButton(cmdNacinPlacanjaSpremi);
    }//GEN-LAST:event_napomenaFocus

    private void napomenaBlur(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_napomenaBlur
        getRootPane().setDefaultButton(null);
    }//GEN-LAST:event_napomenaBlur

    private void ZatvaranjeForme(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ZatvaranjeForme
        PromijeniPrikazKupcu("", 0, 0, "CLR");
    }//GEN-LAST:event_ZatvaranjeForme

    @Action
    public void Zatvori() {
        try {
            setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SimpleInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public Task Prijava() {
        return new PrijavaTask(org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class));
    }

    private class PrijavaTask extends org.jdesktop.application.Task<Object, Void> {
        
        private Operater operater;
        private String zaporka;
        
        PrijavaTask(org.jdesktop.application.Application app) {
            super(app);
            
            operater = getOdabraniOperater();
            zaporka = String.valueOf(zaporkaOperatera.getPassword());
            zaporkaOperatera.setText("");
        }
        @Override protected Object doInBackground() {
            
            setMessage("Provjeravam upisanu zaporku ...");
            
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleInvoice.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(zaporka.equals(operater.getPassword()))
            {
                return true;
            }
            else
            {
                setMessage("UPOZORENJE: Netočna zaporka!");
                return false;
            }
        }
        @Override protected void succeeded(Object result) {
            if((Boolean)result == true)
            {
                CardLayout layout = (CardLayout) SimpleInvoice.this.getContentPane().getLayout();
                layout.show(SimpleInvoice.this.getContentPane(), "unos-racuna");
                
                getRacun().setOperater(operater);
                
                grid.setRowSelectionInterval(0, 0);
                setMessage("Uspješna prijava.");
                
                searchQuery.requestFocus();
                
                SimpleInvoice.this.setTitle(SimpleInvoice.this.getTitle() + " - " + operater.getName());
            }
        }
    }
    
    public double getUkupno()
    {
        SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
        double ukupno = 0D;
        
        for(Stavke stavka : model.getStavke())
        {
            ukupno += stavka.getUkupno();
        }
        
        return ukupno;
    }
    
    private void IzracunajUkupno()
    {
        double ukupno = getUkupno();
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        
        String ukupniIznos = nf.format(ukupno) + " " + Pro3Postavke.getValuta();
        
        getRacun().setUkupno(ukupno);
        
        racunUkupno.setText(ukupniIznos);
        izborNacinaPlacanjaUkupno.setText("= " + ukupniIznos);
        izborKupcaUkupno.setText("= " + ukupniIznos);
        iznosRacuna.setText(ukupniIznos);
        
        iznosUplate.setText("0,00");
        
        IzracunajOstatak();
        
        int row = grid.getSelectedRow();
        grid.scrollRectToVisible(grid.getCellRect(row, 0, true));
    }
    
    public void IzborProizvoda(List<Roba> proizvodi)
    {
        if(proizvodi.size() > 0)
        {
            CardLayout layout = (CardLayout) SimpleInvoice.this.getContentPane().getLayout();
            layout.show(SimpleInvoice.this.getContentPane(), "pretrazivanje");

            getModelProizvoda().setProizvodi(proizvodi);
            gridPretrazivanje.requestFocus();
            gridPretrazivanje.setRowSelectionInterval(0, 0);
        }
        else
        {
            Tasks.showMessage("UPOZORENJE: Nema proizvoda koji zadovoljavaju postavljeni upit");
            searchQuery.selectAll();
        }
    }
    
    public static void PromijeniPrikazKupcu(String opis, double kolicina, double iznos, String naredba)
    {
        if(General.isKoristiDisplayZaKupca())
        {
            String userdir =  General.getPoslinkDisplayZaKupca(); //System.getProperty("poslink");
            
            if(userdir != null)
            {
                File xf = new File(userdir, "poslink.txt");

                try
                {
                    FileWriter xfw = new FileWriter(xf, false);
                    xfw.write(opis + "\n");

                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);

                    if(kolicina > 0)
                    {
                        xfw.write(nf.format(kolicina) + " x " + nf.format(iznos) + Pro3Postavke.getInfo().getValuta() + "\n");
                    }
                    else
                    {
                        if(iznos > 0)
                        {
                            xfw.write(nf.format(iznos) + Pro3Postavke.getInfo().getValuta() + "\n");
                        }
                        else
                        {
                            xfw.write("\n");
                        }
                    }

                    xfw.write(naredba);
                    xfw.close();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public void PromijeniPrikazKupcu(String opis, double kolicina, double iznos)
    {
        PromijeniPrikazKupcu(opis, kolicina, iznos, "");
    }
    
    @Action
    public void DodajStavku() {
        String search = searchQuery.getText();
        
        WeightCodeParser parser = new WeightCodeParser();
        if(parser.parse(search))
        {
            SimpleModelStavki stavke = (SimpleModelStavki) grid.getModel();
            
            for (Roba proizvod : popisProizvoda) {
                if(proizvod.getSifra().equals(parser.getSifra()))
                {
                    stavke.dodajStavku(proizvod, parser.getKolicina());
                    searchQuery.setText("");
                    return;
                }
            }
            
            Tasks.showMessage("Ne mogu pronaći artikal pod šifrom: " + parser.getSifra());
            searchQuery.setText("");
            return;
        }
        
        if(getOdabranaStavka() != null)
        {
            int index = grid.getSelectedRow();
            NumberFormat nf = NumberFormat.getNumberInstance();
            
            Pattern xKolicina = Pattern.compile("\\*(.*)");
            Matcher matcher = xKolicina.matcher(search);

            if(matcher.matches())
            {
                try {
                    double kolicina = nf.parse(matcher.group(1)).doubleValue();
                    
                    if(kolicina == 0)
                    {
                        searchQuery.setText("");
                        IzbrisiStavku();
                        return;
                    }
                    
                    Stavke stavka = getOdabranaStavka();
                    
                    stavka.setKolicina(kolicina);
                    stavka.setIznos(stavka.getMaloprodajnaCijena() * stavka.getKolicina());
                    stavka.setUkupno(stavka.getIznos() * (1 - stavka.getPopust()));
                    
                    ((SimpleModelStavki)grid.getModel()).fireTableDataChanged();
                    
                    grid.setRowSelectionInterval(index, index);
                    searchQuery.setText("");
                    IzracunajUkupno();
                    return;
                } catch (ParseException ex) {
                    Logger.getLogger(SimpleInvoice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            Pattern xPopust = Pattern.compile("-(.*)%{0,1}");
            matcher = xPopust.matcher(search);
            
            if(matcher.matches())
            {
                try {
                    double popust = nf.parse(matcher.group(1)).doubleValue() / 100;
                    
                    Stavke stavka = getOdabranaStavka();
                    
                    stavka.setPopust(popust);
                    stavka.setUkupno(stavka.getIznos() * (1 - stavka.getPopust()));
                    
                    ((SimpleModelStavki)grid.getModel()).fireTableDataChanged();
                    grid.setRowSelectionInterval(index, index);
                    searchQuery.setText("");
                    IzracunajUkupno();
                    return;
                } catch (ParseException ex) {
                    Logger.getLogger(SimpleInvoice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if(search.length() > 0)
        {
            for(Roba roba : popisProizvoda)
            {
                if(roba.getSifra().equals(search))
                {
                    SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
                    model.dodajStavku(roba);

                    int count = grid.getRowCount() - 1;
                    grid.setRowSelectionInterval(count, count);
                    searchQuery.setText("");
                    IzracunajUkupno();
                    return;
                }
            }
            
            List<Roba> rezultati = new ArrayList<Roba>();
            String searchLowerCase = search.toLowerCase();
            
            for(Roba roba : popisProizvoda)
            {
                if(roba.getNaziv().toLowerCase().startsWith(searchLowerCase))
                {
                    rezultati.add(roba);
                }
            }
            
            if(rezultati.size() == 1)
            {
                SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
                model.dodajStavku(rezultati.get(0));

                int count = grid.getRowCount() - 1;
                grid.setRowSelectionInterval(count, count);
                searchQuery.setText("");
                IzracunajUkupno();
                return;
            }
            else
            {
                IzborProizvoda(rezultati);
                searchQuery.selectAll();
                return;
            }
        }
//        else if(search.length() == 0)
//        {
//            IzborKupca();
//            return;
//        }
        
        searchQuery.setText("");
        Tasks.showMessage("UPOZORENJE: Nepoznati uzorak");
    }
    
    public boolean isGotovinskiRacun()
    {
        return gotovinskiRacun;
    }
    
    public List<Template> getNaciniPlacanja()
    {
        return naciniPlacanja;
    }

    @Action
    public void IzbrisiStavku() {
        int index = grid.getSelectedRow();
        int mIndex = grid.convertRowIndexToModel(index);
        SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
        model.izbrisiStavku(mIndex);

        if(index > grid.getRowCount() - 1)
        {
            index = grid.getRowCount() - 1;
        }

        grid.setRowSelectionInterval(index, index);
        IzracunajUkupno();
    }

    @Action
    public void UnosRacuna() {
        CardLayout layout = (CardLayout) this.getContentPane().getLayout();
        layout.show(this.getContentPane(), "unos-racuna");
        
        searchQuery.requestFocus();
    }

    @Action
    public void IzborNacinaPlacanja() {
        CardLayout layout = (CardLayout) this.getContentPane().getLayout();
        layout.show(this.getContentPane(), "nacin-placanja");
        
        napomena.requestFocus();
    }
    
    private void PorukaNedovoljanBrojStavki()
    {
        Tasks.showMessage("Morate upisati barem jednu stavku prije spremanja računa.");
    }
    
    private int getBrojStavki()
    {
        SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
        return model.getBrojStavki();
    }
    
    @Action
    public void IzborKupca() {

        if(getBrojStavki() > 0)
        {
            CardLayout layout = (CardLayout) this.getContentPane().getLayout();
            layout.show(this.getContentPane(), "izbor-kupca");

            searchKupci.requestFocus();
        }
        else
        {
            PorukaNedovoljanBrojStavki();
        }

    }

    @Action
    public void ZakljucakRacuna() {
        getRootPane().setDefaultButton(null);
        
        if(getOdabraniTemplate().isGotovinskaTransakcija())
        {
            CardLayout layout = (CardLayout) this.getContentPane().getLayout();
            layout.show(this.getContentPane(), "zakljucak-racuna");

            iznosUplate.requestFocus();
        }
        else
            UnosNovogRacuna();
    }

    @Action
    public void SearchKupaca() {
        
        SimpleModelKupaca model = (SimpleModelKupaca) gridKupci.getModel();
        
        model.filter(searchKupci.getText());

        if(model.getRowCount() == 2)
            gridKupci.setRowSelectionInterval(1, 1);
        else
            gridKupci.setRowSelectionInterval(0, 0);
        
        searchKupci.selectAll();
        
        PodesiKupacZadanaNaredba();
    }

    @Action
    public void NoviKupac() {
        NoviKorisnik x = new NoviKorisnik();
        
        Pro3View view = (Pro3View) getApplication().getMainView();
        view.Show(x);
    }

    @Action
    public void DodajOdabraniProizvod() {
        int index = gridPretrazivanje.getSelectedRow();
        
        SimpleModelStavki model = (SimpleModelStavki) grid.getModel();
        Roba proizvod = getModelProizvoda().getProizvod(index);
        
        if(proizvod != null)
        {
            model.dodajStavku(proizvod);
            
            int zadnjiRed = grid.getRowCount() - 1;
            grid.setRowSelectionInterval(zadnjiRed, zadnjiRed);
            grid.scrollRectToVisible(grid.getCellRect(zadnjiRed, 0, true));
            
            IzracunajUkupno();
        }
        
        searchQuery.setText("");
        UnosRacuna();
    }
    
    private String valuta(String iznos)
    {
        return iznos + " " + Pro3Postavke.getValuta();
    }

    @Action
    public void IzracunajOstatak() {
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        
        try 
        {
            double upisaniIznosUplate = nf.parse(iznosUplate.getText()).doubleValue();
            double ukupnoZaVratiti = upisaniIznosUplate - Math.abs(getRacun().getUkupno());

            iznosZaVratiti.setText(nf.format(Math.abs(ukupnoZaVratiti)) + " " + Pro3Postavke.getValuta());

            if(getRacun().getUkupno() > 0)
            {
                opisUnosa.setText("Iznos uplate");
                if(ukupnoZaVratiti > 0)
                    opisIznosZaVratiti.setText("Ukupno za vratiti: ");
                else
                    opisIznosZaVratiti.setText("Nedostaje: ");
            }
            else
            {
                opisUnosa.setText("Isplaćeni iznos");
                
                if(ukupnoZaVratiti > 0)
                    opisIznosZaVratiti.setText("Ukupno za primiti: ");
                else
                    opisIznosZaVratiti.setText("Nedostaje: ");
            }
            
        } 
        catch (ParseException ex) 
        {
            Logger.getLogger(SimpleInvoice.class.getName()).log(Level.SEVERE, null, ex);
            iznosZaVratiti.setText("0,00 " + Pro3Postavke.getValuta());
            opisIznosZaVratiti.setText("Ukupno za vratiti: ");
        }
        
    }

    @Action
    public Task SpremiRacun() {
        return new SpremiRacunTask(org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class));
    }
    
    public Template getOdabraniTemplate()
    {
        return (Template) templates.get(listaNacinaPlacanja.getSelectedIndex());
    }

    private class SpremiRacunTask extends org.jdesktop.application.Task<Object, Void> {
        
        private Racun racun;
        private EntityManager manager;
        private Korisnik kupac;
        private Template izabraniTemplateRacuna;
        private String napomenaRacuna;
        
        SpremiRacunTask(org.jdesktop.application.Application app) {
            super(app);
            
            racun = getRacun();
            manager = SimpleInvoice.this.manager;
            
            SimpleModelKupaca modelKupaca = (SimpleModelKupaca) gridKupci.getModel();
            kupac = modelKupaca.getKupac(gridKupci.getSelectedRow());
            izabraniTemplateRacuna = getOdabraniTemplate();
            napomenaRacuna = napomena.getText();
            
            PromijeniPrikazKupcu("Ukupno", -1, racun.getUkupno(), "EOF");
            
            ZakljucakRacuna();
        }
        
        public void SpremiPodatkeKupca()
        {
            if(kupac == null)
            {
                racun.setNaziv("");
                racun.setAdresa("");
                racun.setKorisnikKljuc("");
                racun.setLokacija("");
                racun.setMaticniBroj("");
                racun.setTelefon("");
                racun.setMobitel("");
            }
            else
            {
                racun.setNaziv(kupac.getNaziv());
                racun.setAdresa(kupac.getAdresa());
                racun.setKorisnikKljuc(kupac.getKljuc());
                racun.setLokacija(kupac.getLokacija());
                racun.setMaticniBroj(kupac.getMaticniBroj());
                racun.setTelefon(kupac.getTelefon());
                racun.setMobitel(kupac.getMobitel());
            }
        }
        
        public void SpremiPorezneStavke()
        {
            HashMap<String, PoreznaStavkaRacuna> porezneStavke = new HashMap<String, PoreznaStavkaRacuna>();
            
            for (Stavke stavka : racun.getStavke()) {
                Roba proizvod = stavka.getRobaInfo();
                
                if(proizvod.isPdvDef()) dodajPoreznuStavku(porezneStavke, proizvod.getPdv(), stavka, proizvod);
                if(proizvod.isPotDef()) dodajPoreznuStavku(porezneStavke, proizvod.getPot(), stavka, proizvod);
                
                SpremiKarticuProizvoda((Stavke) stavka, proizvod);
                SpremiKarticeNormativa((Stavke) stavka, proizvod);
                SpremiKnjiguPopisa(stavka);
            }
            
            double iznosPoreza = 0;
            
            for (PoreznaStavkaRacuna poreznaStavka : racun.getPorezneStavke()) {
                iznosPoreza += poreznaStavka.getIznos();
            }
            
            racun.setPorez(iznosPoreza);
        }

        private void dodajPoreznuStavku(HashMap<String, PoreznaStavkaRacuna> porezneStavke, PoreznaStopa porez, Stavke stavka, Roba proizvod) {
            PoreznaStavkaRacuna poreznaStavka;

            if(porezneStavke.containsKey(porez.getKljuc()))
                poreznaStavka = porezneStavke.get(porez.getKljuc());
            else
            {
                poreznaStavka = new PoreznaStavkaRacuna();

                poreznaStavka.setRacun(racun);
                poreznaStavka.setStopa(porez.getNormaliziraniPostotak());
                poreznaStavka.setNaziv(porez.getOpis());

                poreznaStavka.setOsnovica(0D);
                poreznaStavka.setIznos(0D);

                racun.getPorezneStavke().add(poreznaStavka);
                porezneStavke.put(porez.getKljuc(), poreznaStavka);
            }

            double osnovica = stavka.getKolicina() * proizvod.getCijenaBezPoreza();

            poreznaStavka.setOsnovica(poreznaStavka.getOsnovica() + osnovica);
            poreznaStavka.setIznos(poreznaStavka.getIznos() + osnovica * porez.getNormaliziraniPostotak());
        }
        
        private void SpremiKnjiguPopisa(Stavke stavka)
        {
            if(stavka.getPopust() > 0 && (stavka.getRobaInfo().getNabavnaCijena() > 0 || stavka.getRobaInfo().getNormativi().size() > 0))
            {
                KnjigaPopisa knjiga = new KnjigaPopisa();
                knjiga.setDokument(MessageFormat.format("Račun {0}, {1}, {2} {3}, popust {4}%",
                        racun.getOznaka(), stavka.getRobaInfo().getNaziv(), stavka.getKolicina(), stavka.getMjera(),
                        stavka.getPopust() * 100));
                
                knjiga.setZaduzenje(stavka.getUkupno() - stavka.getIznos());
                manager.persist(knjiga);
            }
        }

        private void SpremiKarticuProizvoda(Stavke stavka, Roba proizvod)
        {
            KarticaStavkeRacuna kartica = new KarticaStavkeRacuna();
            Template template = racun.getTemplate();
            
            kartica.setDatum(racun.getIzdan());
            kartica.setIzlaznaCijena(stavka.getUkupno());
            kartica.setKolicinaIzlaz(stavka.getKolicina());
            kartica.setKolicinaUlaz(0D);
            kartica.setUlaznaCijena(0D);
            kartica.setOpis(template.format(racun.getOznaka(), racun.getNaziv()));
            
            kartica.setRoba(proizvod);
            kartica.setStavka(stavka);
            
            stavka.getKartice().add(kartica);
        }
        
        private double izracunajPoreznuStopu(Roba roba)
        {
            if(roba.getPdv() != null)
            {
                return roba.getPdv().getNormaliziranaPoreznaStopa();
            }
            
            return 0;
        }
        
        private void SpremiKarticeNormativa(Stavke stavka, Roba proizvod)
        {
            for (ArtikalNormativ normativ : proizvod.getNormativi()) {
                KarticaStavkeNormativaRacuna kartica = new KarticaStavkeNormativaRacuna();
                
                kartica.setStavka(stavka);
                kartica.setDatum(racun.getIzdan());
                
                double kolicina = stavka.getKolicina() * normativ.getKolicina();
                double cijena = normativ.getNormativ().getMaloprodajnaCijena() * kolicina;
                kartica.setIzlaznaCijena(cijena);
                kartica.setKolicinaIzlaz(kolicina);
                kartica.setKolicinaUlaz(0D);
                kartica.setUlaznaCijena(0D);
                
                String opis = MessageFormat.format("Normativ stavke {0} po računu {1}", stavka.getRoba(), racun.getOznaka());
                kartica.setOpis(opis);
                
                kartica.setRoba(normativ.getNormativ());
                kartica.setStavka(stavka);
                
                stavka.getKartice().add(kartica);
            }
        }
        
        public void SpremiDetaljeRacuna() throws Exception
        {
            racun.setIzdan(Calendar.getInstance().getTime());
            racun.setKljuc(UUID.randomUUID().toString());
            racun.setOtpremnica("");
            
            racun.setOznaka(((Pro3App)getApplication()).getSekvencaRacuna().SljedecaFormatiranaSekvenca());
            
            InfoRacun info = Pro3Postavke.getInfo();
            racun.setNapomena(izabraniTemplateRacuna.formatirajNapomenu(racun.getOznaka(), napomenaRacuna, info.getZiroRacun(), info.getInformacije(), info.getNazivTvrtke()));
            racun.setZaglavlje(info.getZaglavlje());
            racun.setVerzija(2);
            
            if(izabraniTemplateRacuna.isPlacen())
            {
                racun.setPlacen(racun.getIzdan());
            }
            else if(izabraniTemplateRacuna.getRokPlacanja() > 0)
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, izabraniTemplateRacuna.getRokPlacanja());
                racun.setValuta(cal.getTime());                
            }
            
            LocationInfo location = IzmjenaPoslovnogProstora.loadLocationInfo();
            racun.setFiskalnaLokacija(location.getNaziv());
            racun.setFisklaniUredaj(location.getOznakaUredaja());

            racun.setTemplate(izabraniTemplateRacuna);
            racun.setPutanjaPredloska(izabraniTemplateRacuna.getPredlozak());
        }
        
        private double IzracunIznosaKojiSeNeOporezuje()
        {
            return 0;
        }
        
        private double IzracunIznosOslobodjenPdva()
        {
            return 0;
        }
        
        @Override 
        protected Object doInBackground() throws Exception {
            
            EntityTransaction transaction = manager.getTransaction();
            
            try {
                setMessage("Spremam detalje računa");
                setProgress(10);

                SpremiDetaljeRacuna();
                
                setMessage("Spremam podatke o kupcu");
                setProgress(20);

                SpremiPodatkeKupca();
                
                setMessage("Spremam stavke računa");
                setProgress(30);

                SpremiPorezneStavke();

                LocationInfo info = IzmjenaPoslovnogProstora.loadLocationInfo();
                KeyInfo keyInfo = IzborCertifikata.loadKeyInfo();
                
                if(izabraniTemplateRacuna.getNacinPlacanja().equals("G"))
                {
                    Blagajna blagajna = new Blagajna();
                    blagajna.setRacun(racun);
                    racun.getBlagajna().add(blagajna);
                }
                
                if(izabraniTemplateRacuna.isFiskalnaTransakcija() && keyInfo.isFiskalizacijaAktivirana())
                {
                    setMessage("Pripremam fiskalni zahtjev za izdavanje JIR oznake");
                    setProgress(40);
                    
                    try 
                    {
                        FiskalniRacun fr = new FiskalniRacun(racun);
                        fr.fiskaliziraj();
                        
                        setMessage("Fiskalni zahtjev je uspješno izvršen");
                    } 
                    catch (Exception x) 
                    {
                        setMessage(x.getMessage());
                        Logger.getLogger(SimpleInvoice.class.getName()).log(Level.WARNING, x.getMessage(), x);
                        setMessage("UPOZORENJE: Iznimka prilikom fiskalizacije računa. Račun će biti izdan bez JIR oznake!");
                    }
                    
                    setProgress(50);
                    Thread.sleep(1000);
                }

                //ponude imaju null način plaćanja i ne spremaju se u samom programu
                if(izabraniTemplateRacuna.getNacinPlacanja() != null)
                {
                    transaction.begin();
                    manager.persist(racun);
                    transaction.commit();
                }
                
                return racun;
            } 
            catch (Exception ex) 
            {
                if(transaction.isActive()) transaction.rollback();
                Logger.getLogger(SimpleInvoice.class.getName()).log(Level.SEVERE, null, ex);
                
                throw new Exception("IZNIMKA: Račun nije moguće spremiti", ex);
            }
        }
        @Override protected void succeeded(Object result) {
            
            if(result instanceof Racun)
            {
                ReportingServices.IspisRacuna(racun, true);
                setMessage("Račun uspješno poslan na ispis");
                setProgress(100);
            }
        }
    }

    @Action
    public void SpremiGotovinskiRacun() {
        
        if(getBrojStavki() > 0)
        {
            gridKupci.setRowSelectionInterval(0, 0);
            listaNacinaPlacanja.setSelectedIndex(0);

            Pro3App.getApplication().getContext().getTaskService().execute(SpremiRacun());
        }
        else
        {
            PorukaNedovoljanBrojStavki();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdDodaj;
    private javax.swing.JButton cmdGotovinskiRacun;
    private javax.swing.JButton cmdIzbrisi;
    private javax.swing.JButton cmdKupciBack;
    private javax.swing.JButton cmdKupciDodaj;
    private javax.swing.JButton cmdKupciNastavak;
    private javax.swing.JButton cmdKupciSearch;
    private javax.swing.JButton cmdKupciZatvori;
    private javax.swing.JButton cmdNacinPlacanjaSpremi;
    private javax.swing.JButton cmdNoviRacun;
    private javax.swing.JButton cmdPretrazivanjeOdaberi;
    private javax.swing.JButton cmdPretrazivanjeOdaberi1;
    private javax.swing.JButton cmdPretrazivanjePovratak;
    private javax.swing.JButton cmdSpremi;
    private javax.swing.JTable grid;
    private javax.swing.JTable gridKupci;
    private javax.swing.JTable gridPretrazivanje;
    private javax.swing.JPanel izborKupca;
    private javax.swing.JLabel izborKupcaUkupno;
    private javax.swing.JPanel izborNacinaPlacanja;
    private javax.swing.JLabel izborNacinaPlacanjaUkupno;
    private javax.swing.JPanel izborOperatera;
    private javax.swing.JPanel izborProizvoda;
    private javax.swing.JLabel iznosRacuna;
    private javax.swing.JTextField iznosUplate;
    private javax.swing.JLabel iznosZaVratiti;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel keyboard;
    private javax.persistence.Query kupciQuery;
    private javax.swing.JList listaNacinaPlacanja;
    private javax.swing.JList listaOperatera;
    private javax.persistence.EntityManager manager;
    private javax.swing.JButton nacinPlacanjaPovratak;
    private javax.swing.JButton nacinPlacanjaZatvori;
    private javax.swing.JTextField napomena;
    private javax.swing.JLabel opisIznosZaVratiti;
    private javax.swing.JLabel opisUnosa;
    private java.util.List<Operater> popisOperatera;
    private java.util.List<Roba> popisProizvoda;
    private javax.swing.JButton prijavaOperatera;
    private javax.swing.JButton prijavaOperatera1;
    private javax.persistence.Query queryOperateri;
    private javax.persistence.Query queryProizvodi;
    private javax.swing.JLabel racunUkupno;
    private javax.swing.JTextField searchKupci;
    private javax.swing.JTextField searchQuery;
    private javax.persistence.Query templateQuery;
    private java.util.List<Template> templates;
    private javax.swing.JPanel unosRacuna;
    private javax.swing.JPanel zakljucenRacun;
    private javax.swing.JPasswordField zaporkaOperatera;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
