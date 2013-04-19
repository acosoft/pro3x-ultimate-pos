/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NapredniPregledRacuna.java
 *
 * Created on 24.03.2012., 19:07:54
 */
package Pro3x.View;

import Acosoft.Processing.Components.Code;
import Acosoft.Processing.Components.DateCellRenderer;
import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.NumberCellRenderer;
import Acosoft.Processing.Components.PercentCellRenderer;
import Acosoft.Processing.Components.RacunCellRenderer;
import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.DataBox.Stavke;
import Pro3x.BasicView;
import Pro3x.Code.ReportingServices;
import Pro3x.Fiscal.FiskalniRacun;
import Pro3x.View.Models.DataModel;
import Pro3x.View.Models.StavkaPrometa;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import Pro3x.View.Models.DnevniPromet;
import Pro3x.View.Models.SegmentPrometa;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author aco
 */
public class NapredniPregledRacuna extends BasicView
{

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private String raspon;
    private Date pocetak = null;
    private Date kraj = null;
    private Racun odabraniRacun = null;
    private final String zadaniRazlog = "Unesi razlog";

    public Racun getOdabraniRacun()
    {
        return odabraniRacun;
    }

    public void setOdabraniRacun(Racun odabraniRacun)
    {
        this.odabraniRacun = odabraniRacun;
    }

    public Date getKraj()
    {
        return kraj;
    }

    public void setKraj(Date kraj)
    {
        this.kraj = kraj;
    }

    public Date getPocetak()
    {
        return pocetak;
    }

    public void setPocetak(Date pocetak)
    {
        this.pocetak = pocetak;
    }

    public String getRaspon()
    {
        return this.raspon;
    }

    public final void setRaspon(String raspon)
    {
        try
        {
            String[] parts = raspon.split("-");

            Date pocetakRaspona = sdf.parse(parts[0]);
            Date krajRaspona = sdf.parse(parts[1]);

            setPocetak(pocetakRaspona);
            setKraj(krajRaspona);

            String stariRaspon = this.raspon;
            this.raspon = trenutniRaspon();

            firePropertyChange("raspon", stariRaspon, this.raspon);
        }
        catch (ParseException ex)
        {
            Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private String trenutniRaspon()
    {
        return sdf.format(getPocetak()) + "-" + sdf.format(getKraj());
    }

    private String podesiRaspon()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        setPocetak(cal.getTime());

        cal.add(Calendar.MONTH, 1);        
        setKraj(cal.getTime());

        return trenutniRaspon();
    }

    private void pratiPromjenuRaspona()
    {
        txtRaspon.addFocusListener(new FocusAdapter()
        {

            @Override
            public void focusLost(FocusEvent e)
            {
                setRaspon(txtRaspon.getText());
                getRootPane().setDefaultButton(null);
            }

            @Override
            public void focusGained(FocusEvent e)
            {
                getRootPane().setDefaultButton(cmdOsvjezi);
            }
        });

        addPropertyChangeListener(new PropertyChangeListener()
        {

            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getPropertyName().equals("raspon"))
                {
                    txtRaspon.setText((String) evt.getNewValue());
                }
            }
        });

        txtRaspon.setText(this.raspon);
    }

    private void pratiPromjenuRacuna()
    {
        gridRacuni.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    if (gridRacuni.getSelectedRowCount() > 0)
                    {
                        int index = gridRacuni.getSelectionModel().getLeadSelectionIndex();
                        int mindex = gridRacuni.convertRowIndexToModel(index);

                        if (mindex >= 0)
                        {
                            DataModel<Racun> model = (DataModel<Racun>) gridRacuni.getModel();
                            setOdabraniRacun(model.getItem(mindex));

                            DataModel<Stavke> stavke = (DataModel<Stavke>) gridStavke.getModel();
                            List<Stavke> data = (List<Stavke>) getOdabraniRacun().getStavke();
                            stavke.setData(data);
                        }
                        else
                        {
                            setOdabraniRacun(null);
                        }
                    }
                }
            }
        });
    }

    /** Creates new form NapredniPregledRacuna */
    public NapredniPregledRacuna()
    {
        initComponents();

        setRaspon(podesiRaspon());
        pratiPromjenuRaspona();

        podesiKoloneRacuna();
        osvjeziPregledRacuna();

        if (gridRacuni.getRowCount() > 0)
        {
            Code.SelectLastRow(gridRacuni);
            Code.ScrollToLastRow(gridRacuni);
        }
    }

    private void podesiKoloneStavki() throws NoSuchMethodException
    {
        DataModel<Stavke> model = new DataModel<Stavke>(new ArrayList<Stavke>());
        model.setContentType(Stavke.class);

        model.addColumn("Roba", "getRoba", null);
        model.addColumn("Kolicina", "getKolicina", new NumberCellRenderer());
        model.addColumn("Mjera", "getMjera", null);
        model.addColumn("Cijena", "getMaloprodajnaCijena", new NumberCellRenderer());
        model.addColumn("Popust", "getPopust", new PercentCellRenderer());
        model.addColumn("Osnovica", "getIznos", new NumberCellRenderer());
        model.addColumn("Ukupno", "getUkupno", new NumberCellRenderer());

        gridStavke.setModel(model);
        model.configureView(gridStavke);
    }

    private void podesiKoloneRacuna()
    {
        Query query = getProManager().createNamedQuery("Racun.findByRaspon");

        query.setParameter("pocetak", getPocetak());
        query.setParameter("kraj", getKraj());

        List<Racun> data = query.getResultList();

        DataModel<Racun> model = new DataModel<Racun>(data);
        model.setContentType(Racun.class);

        try
        {
            model.addColumn("Oznaka", "", new RacunCellRenderer());
            model.addColumn("Izdan", "getIzdan", new DateCellRenderer("dd.MM.yyyy"));
            model.addColumn("Naziv", "getNaziv", null);
            model.addColumn("Adresa", "getAdresa", null);
            model.addColumn("Lokacija", "getLokacija", null);
            //model.addColumn("Storniran", "getStorniran", new DateCellRenderer("dd.MM.yyyy"));
            model.addColumn("Ukupno", "getUkupno", new NumberCellRenderer());

            gridRacuni.setModel(model);
            model.configureView(gridRacuni);
            pratiPromjenuRacuna();

            podesiKoloneStavki();
        }
        catch (NoSuchMethodException ex)
        {
            Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            Tasks.showMessage(ex.getMessage());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panCommands = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtRaspon = new javax.swing.JTextField();
        cmdOsvjezi = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        cmdPromet1 = new javax.swing.JButton();
        cmdPromet = new javax.swing.JButton();
        cmdIspis = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        cmdZatvori = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        gridRacuni = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        gridStavke = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(NapredniPregledRacuna.class);
        setTitle(resourceMap.getString("napredni-pregled-racuna-v2.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(480, 320));
        setName("napredni-pregled-racuna-v2"); // NOI18N
        setPreferredSize(new java.awt.Dimension(640, 400));

        panCommands.setName("panCommands"); // NOI18N
        panCommands.setLayout(new java.awt.BorderLayout());

        jPanel2.setMinimumSize(new java.awt.Dimension(271, 40));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        txtRaspon.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaspon.setName("txtRaspon"); // NOI18N
        txtRaspon.setPreferredSize(new java.awt.Dimension(200, 30));
        jPanel2.add(txtRaspon);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(NapredniPregledRacuna.class, this);
        cmdOsvjezi.setAction(actionMap.get("osvjeziPregledRacuna")); // NOI18N
        cmdOsvjezi.setText(resourceMap.getString("cmdOsvjezi.text")); // NOI18N
        cmdOsvjezi.setName("cmdOsvjezi"); // NOI18N
        cmdOsvjezi.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel2.add(cmdOsvjezi);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMinimumSize(new java.awt.Dimension(6, 30));
        jSeparator3.setName("jSeparator3"); // NOI18N
        jSeparator3.setPreferredSize(new java.awt.Dimension(6, 30));
        jPanel2.add(jSeparator3);

        jButton2.setAction(actionMap.get("ProvjeraFiskalnihRacuna")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setToolTipText(resourceMap.getString("jButton2.toolTipText")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(100, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2izbrisiRacun(evt);
            }
        });
        jPanel2.add(jButton2);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(100, 30));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izbrisiRacun(evt);
            }
        });
        jPanel2.add(jButton1);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setMinimumSize(new java.awt.Dimension(6, 30));
        jSeparator2.setName("jSeparator2"); // NOI18N
        jSeparator2.setPreferredSize(new java.awt.Dimension(6, 30));
        jPanel2.add(jSeparator2);

        cmdPromet1.setAction(actionMap.get("ispisPrometaMaterijalno")); // NOI18N
        cmdPromet1.setText(resourceMap.getString("cmdPromet1.text")); // NOI18N
        cmdPromet1.setToolTipText(resourceMap.getString("cmdPromet1.toolTipText")); // NOI18N
        cmdPromet1.setMaximumSize(new java.awt.Dimension(85, 30));
        cmdPromet1.setName("cmdPromet1"); // NOI18N
        cmdPromet1.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel2.add(cmdPromet1);

        cmdPromet.setAction(actionMap.get("ispisPrometa")); // NOI18N
        cmdPromet.setText(resourceMap.getString("cmdPromet.text")); // NOI18N
        cmdPromet.setToolTipText(resourceMap.getString("cmdPromet.toolTipText")); // NOI18N
        cmdPromet.setMaximumSize(new java.awt.Dimension(85, 30));
        cmdPromet.setName("cmdPromet"); // NOI18N
        cmdPromet.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel2.add(cmdPromet);

        cmdIspis.setText(resourceMap.getString("cmdIspis.text")); // NOI18N
        cmdIspis.setMaximumSize(new java.awt.Dimension(85, 30));
        cmdIspis.setName("cmdIspis"); // NOI18N
        cmdIspis.setPreferredSize(new java.awt.Dimension(100, 30));
        cmdIspis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ispisRacuna(evt);
            }
        });
        jPanel2.add(cmdIspis);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMinimumSize(new java.awt.Dimension(6, 30));
        jSeparator1.setName("jSeparator1"); // NOI18N
        jSeparator1.setPreferredSize(new java.awt.Dimension(6, 30));
        jPanel2.add(jSeparator1);

        cmdZatvori.setText(resourceMap.getString("cmdZatvori.text")); // NOI18N
        cmdZatvori.setMargin(new java.awt.Insets(5, 5, 5, 5));
        cmdZatvori.setMaximumSize(new java.awt.Dimension(85, 30));
        cmdZatvori.setName("cmdZatvori"); // NOI18N
        cmdZatvori.setPreferredSize(new java.awt.Dimension(100, 30));
        cmdZatvori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zatvori(evt);
            }
        });
        jPanel2.add(cmdZatvori);

        panCommands.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(panCommands, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 5));
        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        gridRacuni.setAutoCreateRowSorter(true);
        gridRacuni.setName("gridRacuni"); // NOI18N
        gridRacuni.setRowHeight(35);
        gridRacuni.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        gridRacuni.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(gridRacuni);

        jSplitPane1.setTopComponent(jScrollPane1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        gridStavke.setName("gridStavke"); // NOI18N
        gridStavke.setRowHeight(35);
        jScrollPane2.setViewportView(gridStavke);

        jSplitPane1.setRightComponent(jScrollPane2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Roba FindByKey(String key)
    {
        return getProManager().find(Roba.class, key);
        
//        for(Roba r : robaList)
//        {
//            if(r.getKljuc().equals(key))
//                return r;
//        }
//        
//        return null;
    }
    
    private void zatvori(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zatvori
    {//GEN-HEADEREND:event_zatvori
        try
        {
            setClosed(true);
        }
        catch (PropertyVetoException ex)
        {
            Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_zatvori

    private void ispisRacuna(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ispisRacuna
    {//GEN-HEADEREND:event_ispisRacuna
        int count = gridRacuni.getSelectedRowCount();
        
        if(count == 0)
        {
            Tasks.showMessage("Izaberite račune koje želite ispisati");
        }
        else if(count == 1)
        {
            ReportingServices.IspisRacuna(getOdabraniRacun());
        }
        else
        {
            JFileChooser xf = new JFileChooser();
            
            xf.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f)
                {
                    return f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory();
                }

                @Override
                public String getDescription()
                {
                    return "PDF Arhiva Računa ( *.pdf )";
                }
            });
            
            int odabir = xf.showSaveDialog(this);

            if(odabir == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    List<JasperPrint> arhiva = new ArrayList<JasperPrint>();

                    DataModel<Racun> model = (DataModel<Racun>) gridRacuni.getModel();
                    int rows[] = gridRacuni.getSelectedRows();

                    for (int i = 0; i < count; i++)
                    {
                        int index = gridRacuni.convertRowIndexToModel(rows[i]);
                        Racun racun = model.getItem(index);

                        arhiva.add(ReportingServices.pripremaIspisaRacuna(racun));
                    }

                    File xfile;
                    
                    if(xf.getSelectedFile().getAbsolutePath().endsWith(".pdf"))
                    {
                        xfile = xf.getSelectedFile();
                    }
                    else
                    {
                        xfile = new File(xf.getSelectedFile().getAbsolutePath() + ".pdf");
                    }
                    
                    JRExporter exporter = new JRPdfExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, arhiva);
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE, xfile);
                    exporter.exportReport();
                    
                    Tasks.showMessage("Arhiva odabranih računa je uspješno kreirana.");

                }
                catch (Exception ex)
                {
                    Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
                    new ExceptionView(ex).setVisible(true);
                }
            }
            

        }
    }//GEN-LAST:event_ispisRacuna

    private void izbrisiRacun(java.awt.event.ActionEvent evt)//GEN-FIRST:event_izbrisiRacun
    {//GEN-HEADEREND:event_izbrisiRacun
        int count = gridRacuni.getSelectedRowCount();
        
        if(count == 0)
        {
            Tasks.showMessage("Izaberite račune koje želite izbrisati.");
        }
        else
        {
            int odabir = JOptionPane.showConfirmDialog(this, 
                    "Želite li izbrisati odabrane račune?", "Upozorenje", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if(odabir == JOptionPane.YES_OPTION)
            {
                getProManager().getTransaction().begin();
                
                DataModel<Racun> model = (DataModel<Racun>) gridRacuni.getModel();
                
                int rows[] = gridRacuni.getSelectedRows();
                
                for(int i=0; i<count; i++)
                {
                    int index = gridRacuni.convertRowIndexToModel(rows[i]);
                    Racun racun = model.getItem(index);
                    getProManager().remove(racun);
                }
                
                getProManager().getTransaction().commit();
                
                osvjeziPregledRacuna();
                
                Tasks.showMessage("Račun je uspješno izbrisan.");
            }
        }
    }//GEN-LAST:event_izbrisiRacun

    private void jButton2izbrisiRacun(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2izbrisiRacun
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2izbrisiRacun
    
    @Action
    public final void osvjeziPregledRacuna()
    {
        setRaspon(txtRaspon.getText());

        Query query = getProManager().createNamedQuery("Racun.findByRaspon");

        setRaspon(trenutniRaspon());

        query.setParameter("pocetak", getPocetak());
        query.setParameter("kraj", getKraj());

        List<Racun> data = query.getResultList();

        DataModel<Racun> model = (DataModel<Racun>) gridRacuni.getModel();
        model.setData(data);
    }
    
    @Action
    public Task ispisPrometaMaterijalno() {
        return new IspisPrometaTask(org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class), 1);
    }

    @Action
    public Task ispisPrometa() {
        return new IspisPrometaTask(org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class), 0);
    }

    private class IspisPrometaTask extends org.jdesktop.application.Task<Object, Void> {
        
        private List<Racun> racuni;
        private HashMap<String, StavkaPrometa> izvjestaj;
        private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
        private Racun min;
        private Racun max;
        private int tip;
        
        private HashMap<String, DnevniPromet> dnevniPrometi;
        
        IspisPrometaTask(org.jdesktop.application.Application app, int tip) {
            super(app);
            
            this.tip = tip;
            
            dnevniPrometi = new HashMap<String, DnevniPromet>();
            izvjestaj = new HashMap<String, StavkaPrometa>();
            racuni = new ArrayList<Racun>();
            
            DataModel<Racun> model = (DataModel<Racun>) gridRacuni.getModel();
            
            for(int index : gridRacuni.getSelectedRows())
            {
                Racun racun = model.getItem(gridRacuni.convertRowIndexToModel(index));
                racuni.add(racun);
            }
        }
        
        private void sleep()
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void dodajDnevniPromet(Racun r)
        {
            String datum = sf.format(r.getIzdan());
            DnevniPromet promet;
            
            if(dnevniPrometi.containsKey(datum))
            {
                promet = dnevniPrometi.get(datum);
            }
            else
            {
                promet = new DnevniPromet();
                dnevniPrometi.put(datum, promet);
            }
            
            promet.dodaj(r);
        }
        
        private void obradiRacun(Racun r)
        {
            dodajDnevniPromet(r);
            
            if(min == null) 
                min = r;
            else if(min.getIzdan().after(r.getIzdan()))
                min = r;
            
            if(max == null) 
                max = r;
            else if(max.getIzdan().before(r.getIzdan()))
                max = r;
            
            for(Stavke stavka : r.getStavke())
            {
                Roba roba = stavka.getRobaInfo();
                String kljuc = roba.getKljuc();
                StavkaPrometa promet;
                
                if(izvjestaj.containsKey(kljuc))
                {
                    promet = izvjestaj.get(kljuc);
                }
                else
                {
                    promet = new StavkaPrometa();
                    izvjestaj.put(kljuc, promet);
                    promet.setNaziv(roba.getNaziv());
                }
                
                double poreznaStopa;
                
                if(stavka.getIznos() != 0)
                    poreznaStopa = stavka.getUkupno() / stavka.getIznos();
                else
                    poreznaStopa = 1;
                
                promet.setCijena(promet.getCijena() + stavka.getIznos());
                
                promet.setKolicina(promet.getKolicina() + stavka.getKolicina());
                promet.setPopust(promet.getPopust() + stavka.getIznos() - stavka.getUkupno());
                promet.setUkupno(promet.getUkupno() + stavka.getUkupno());
            }
        }
        
        @Override protected Object doInBackground() {

            setMessage("Sortiram račune po datumima");
            sleep();
            
            Collections.sort(racuni, new Comparator<Racun>() {

                public int compare(Racun o1, Racun o2) {
                    return o1.getIzdan().compareTo(o2.getIzdan());
                }
            });
            
            setMessage("Pripremam obradu računa");
            int count = racuni.size();
            int trenutni = 0;
            
            for(Racun racun : racuni)
            {
                setProgress(trenutni++, 0, count);
                
                if(racun.getStorniran() == null)
                {
                    setMessage("Pripremam promet po računu: " + racun.getOznaka());
                    obradiRacun(racun);
                }
                else
                {
                    setMessage("Preskačem stornirani račun: " + racun.getOznaka());
                }
                
                sleep();
            }
            
            setMessage("Grupiram segmente izvještaja");
            
            List<SegmentPrometa> segmenti = new ArrayList<SegmentPrometa>();
            
            for(DnevniPromet promet : dnevniPrometi.values())
            {
                segmenti.addAll(promet.getSegmenti());
            }
            
            setMessage("Sortiram segmente izvještaja");
            
            Collections.sort(segmenti, new Comparator<SegmentPrometa>() {

                @Override
                public int compare(SegmentPrometa o1, SegmentPrometa o2) {
                    return o1.getDatum().compareTo(o2.getDatum());
                }
            });

            setMessage("Pripremam prikaz prometa po odabranim računima");
            return segmenti;  // return your result
        }
        
        @Override protected void succeeded(Object result) {
            
            List<SegmentPrometa> segmenti = (List<SegmentPrometa>) result;
            if(segmenti.size() > 0)
            {
                if(tip == 0)
                {
                    ReportingServices.showDnevniPromet(segmenti);
                }
                else
                {
                    if(izvjestaj.size() > 0)
                    {
                        List data = new ArrayList(izvjestaj.values());
                        ReportingServices.showPromet(data, min.getOznaka(), max.getOznaka());
                    }
                    else
                    {
                        setMessage("Nema podataka za prikaz izvještaja");
                    }
                }
            }
        }
    }

    @Action
    public Task ProvjeraFiskalnihRacuna() {
        return new ProvjeraFiskalnihRacunaTask(org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class));
    }

    private class ProvjeraFiskalnihRacunaTask extends org.jdesktop.application.Task<Object, Void> {
        
        private List<Racun> racuni;
        
        ProvjeraFiskalnihRacunaTask(org.jdesktop.application.Application app) {
            super(app);
            
            racuni = new ArrayList<Racun>();
            
            int[] rows = gridRacuni.getSelectedRows();
            DataModel<Racun> model = (DataModel<Racun>) gridRacuni.getModel();
            
            for (int row : rows) {
                racuni.add(model.getItem(gridRacuni.convertRowIndexToModel(row)));
            }
        }
        
        private void sleep(int milisecond)
        {
            try {
                Thread.sleep(milisecond);
            } catch (InterruptedException ex) {
                Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override protected Object doInBackground() {
            int count = racuni.size();
            int current = 0;
            
            for (Racun racun : racuni) 
            {
                setProgress(current++, 0, count);
                sleep(10);
                
                if(racun.getTemplate().isFiskalnaTransakcija() && racun.getJir() == null)
                {
                    setMessage("Ponovno slanje računa " + racun.getOznaka());
                    FiskalniRacun xf = new FiskalniRacun(racun);
                    
                    try 
                    {
                        xf.fiskaliziraj();
                        sleep(1000);
                        
                        EntityManager manager = getProManager();
                        EntityTransaction transaction = manager.getTransaction();
                        transaction.begin();
                        manager.persist(racun);
                        transaction.commit();
                    } 
                    catch (Exception ex) 
                    {
                        Logger.getLogger(NapredniPregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
                        setMessage("Iznimka prilikom fiskalizacije računa " + racun.getOznaka());
                        sleep(3000);
                    }
                }
            }
            
            return null;
        }
        
        @Override protected void succeeded(Object result) {
            
            setMessage("Fiskalizacija računa završena");
            
        }
    }


   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdIspis;
    private javax.swing.JButton cmdOsvjezi;
    private javax.swing.JButton cmdPromet;
    private javax.swing.JButton cmdPromet1;
    private javax.swing.JButton cmdZatvori;
    private javax.swing.JTable gridRacuni;
    private javax.swing.JTable gridStavke;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel panCommands;
    private javax.swing.JTextField txtRaspon;
    // End of variables declaration//GEN-END:variables
}
