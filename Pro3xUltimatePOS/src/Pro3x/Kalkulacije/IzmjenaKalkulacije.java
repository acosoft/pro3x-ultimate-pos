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

/*
 * IzmjenaKalkulacije.java
 *
 * Created on 17.11.2009., 00:37:50
 */

package Pro3x.Kalkulacije;

import Pro3x.Kalkulacije.Model.KarticaStavkeKalkulacije;
import Acosoft.Processing.Components.Code;
import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.InfoSekvenca;
import Acosoft.Processing.Components.NumberCellEditor;
import Acosoft.Processing.Components.NumberCellRenderer;
import Acosoft.Processing.Components.PercentCellEditor;
import Acosoft.Processing.Components.PercentCellRenderer;
import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.DataBox.Dobavljac;
import Acosoft.Processing.DataBox.KnjigaPopisa;
import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.Pro3App;
import Acosoft.Processing.Pro3Postavke;
import Acosoft.Processing.Pro3View;
import Pro3x.Code.Akcija;
import Pro3x.Configuration.General;
import Pro3x.Kalkulacije.Model.Kalkulacija;
import Pro3x.Kalkulacije.Model.StavkaKalkulacije;
import Pro3x.Live.AkcijeDobavljaca;
import Pro3x.Live.ArtikalEvents;
import Pro3x.Live.ArtikalEvents.ArtikalEventArgs;
import Pro3x.Live.EventArgs;
import Pro3x.Live.EventListener;
import Pro3x.Live.SlusateljDobavljaca;
import Pro3x.View.PregledKartice;
import Pro3x.View.PromjenaOpisaArtikla;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.sf.jasperreports.engine.JRException;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author nonstop
 */
public final class IzmjenaKalkulacije extends javax.swing.JPanel
{
    private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
    private boolean editMode = false;

    private EventListener izmjeneArtikala = new EventListener()
    {
        public void doWork(Object source, EventArgs eventArgs)
        {
            ArtikalEventArgs args = (ArtikalEventArgs) eventArgs;

            switch(args.getEventType())
            {
                case Izmjenjen:
                    if(popisArtikala.contains(args.getNovaRoba()))
                    {
                        int index = popisArtikala.indexOf(args.getStaraRoba());
                        popisArtikala.set(index, args.getNovaRoba());
                    }
                    break;
                case Izbrisan:
                    popisArtikala.remove(args.getStaraRoba());
                    break;
                case Kreiran:
                {
                    popisArtikala.add(args.getNovaRoba());
                    comArtikli.setSelectedItem(args.getNovaRoba());
                    break;
                }
            }
        }
    };

    private SlusateljDobavljaca izmjeneDobavljaca = new SlusateljDobavljaca()
    {

        public void KreiranDobavljac(Object source, Dobavljac dobavljac)
        {
            popisDobavljaca.add(dobavljac);
        }

        public void IzbrisanDobavljac(Object source, Dobavljac dobavljac)
        {
            popisDobavljaca.remove(dobavljac);
        }

        public void IzmjenjenDobavljac(Object source, Dobavljac dobavljac)
        {
            if(popisDobavljaca.contains(dobavljac))
            {
                int index = popisDobavljaca.indexOf(dobavljac);
                popisDobavljaca.set(index, dobavljac);
            }
        }
    };

    private boolean novaKalkulacija = false;
    
    /** Creates new form IzmjenaKalkulacije */
    public IzmjenaKalkulacije()
    {
        this(new Kalkulacija(), Pro3x.Persistence.createEntityManagerFactory().createEntityManager());

        novaKalkulacija = true;
        
        if(Pro3App.getApplication().getSekvencaKalkulacije().isAutoSekvenca())
        {
            oznakaKalkulacije.setText("Automatska oznaka");
            oznakaKalkulacije.setEditable(false);
        }

        editMode = false;
    }

    public IzmjenaKalkulacije(String id)
    {
        setManager(Pro3x.Persistence.createEntityManagerFactory().createEntityManager());
        setKalkulacija(manager.find(Kalkulacija.class, id));
        setStavke(ObservableCollections.observableList(kalkulacija.getStavke()));

        manager.getTransaction().begin();

        initComponents();

        PripremaPodataka(kalkulacija);
    }

    public IzmjenaKalkulacije(Kalkulacija kalkulacija, EntityManager manager)
    {
        setKalkulacija(kalkulacija);
        setManager(manager);
        setStavke(ObservableCollections.observableList(kalkulacija.getStavke()));

        manager.getTransaction().begin();

        initComponents();

        PripremaPodataka(kalkulacija);
    }

    protected Kalkulacija kalkulacija;
    protected EntityManager manager;
    private List<StavkaKalkulacije> stavke;
    protected Akcija zatvori;

    private void IzracunajUkupno()
    {
//        Double ukupnaProdajnaVrijednost = 0D;
//        Double ukupnaNabavnaVrijednost = 0D;
//
//        for(StavkaKalkulacije stavka : getStavke())
//        {
//            ukupnaProdajnaVrijednost += stavka.getUkupno();
//            ukupnaNabavnaVrijednost += stavka.getFakturnaSaPorezom();
//        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        
        String valuta;

        try
        {
            valuta = Pro3Postavke.getInfo().getValuta();
        }
        catch (Exception ex)
        {
            Logger.getLogger(IzmjenaKalkulacije.class.getName()).log(Level.SEVERE, null, ex);
            valuta = "kn";
        }

        iznosUkupno.setText("<html><body style=\"text-align: center\">" 
                + nf.format(getKalkulacija().getUkupnaProdajnaVrijednost()) + " " + valuta
                + "\n<div style=\"font-size:10pt; padding-bottom: 10px;\">Prodajna vrijednost</div>\n"
                + nf.format(getKalkulacija().getUkupnaNabavnaVrijednost()) + " " + valuta
                + "\n<div style=\"font-size:10pt;\">Nabavna vrijednost</div></body></html>");
    }

    public Akcija getZatvori()
    {
        return zatvori;
    }

    public void setZatvori(Akcija zatvori)
    {
        this.zatvori = zatvori;
    }

    public EntityManager getManager()
    {
        return manager;
    }

    public void setManager(EntityManager manager)
    {
        this.manager = manager;
    }


    public Kalkulacija getKalkulacija()
    {
        return kalkulacija;
    }

    public void setKalkulacija(Kalkulacija kalkulacija)
    {
        this.kalkulacija = kalkulacija;
    }

    public List<Dobavljac> getDobavljaci()
    {
        return popisDobavljaca;
    }

    private void PripremaPodataka(Kalkulacija kalkulacija)
    {
        ArtikalBoxEditor editor = (ArtikalBoxEditor) comArtikli.getEditor();
        editor.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Promjena(e);
            }
        });

        grid.setDefaultEditor(Double.class, new NumberCellEditor());
        grid.setDefaultRenderer(Double.class, new NumberCellRenderer());

        oznakaKalkulacije.setText(kalkulacija.getOznakaKalkulacije());

        datumDokumenta.setText(sf.format(getKalkulacija().getDatumDokumenta()));
        datumKalkulacije.setText(sf.format(getKalkulacija().getDatumIzrade()));
        dospijecePlacanja.setText(sf.format(getKalkulacija().getDatumDospijeca()));

        if (getKalkulacija().getDatumPlacanja() != null)
            datumPlacanja.setText(sf.format(getKalkulacija().getDatumPlacanja()));

        grid.getModel().addTableModelListener(new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
                IzracunajUkupno();
            }
        });
        
        IzracunajUkupno();

        ArtikalEvents.Events().addListener(izmjeneArtikala);
        AkcijeDobavljaca.dodajSlusatelja(izmjeneDobavljaca);
        
        editMode = true;

        cmdKartica.setVisible(!General.isUserMode());
        cmdDeklaracije.setVisible(General.isKoristiDeklaracije());
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(IzmjenaKalkulacije.class);
        queryArtikli = java.beans.Beans.isDesignTime() ? null : getManager().createQuery(resourceMap.getString("queryArtikli.query")); // NOI18N
        popisArtikala = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(new java.util.LinkedList(queryArtikli.getResultList()));
        queryDobavljac = java.beans.Beans.isDesignTime() ? null : getManager().createQuery(resourceMap.getString("queryDobavljac.query")); // NOI18N
        popisDobavljaca = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(new java.util.LinkedList(queryDobavljac.getResultList()));
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        comArtikli = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        cmdZatvori1 = new javax.swing.JButton();
        cmdZatvori = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        cmdDeklaracije = new javax.swing.JButton();
        cmdKartica = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        dobavljaci = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        oznakaDokumenta = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        datumDokumenta = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        dospijecePlacanja = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        iznosUkupno = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        datumKalkulacije = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        oznakaKalkulacije = new javax.swing.JTextField();
        datumPlacanja = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        kalkulacijuIzradio = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        grid = new javax.swing.JTable();

        setName("izmjena-kalkulacije"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel7.setDisplayedMnemonic('a');
        jLabel7.setLabelFor(comArtikli);
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel5.add(jLabel7);

        comArtikli.setEditable(true);
        comArtikli.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comArtikli.setEditor(new ArtikalBoxEditor());
        comArtikli.setName("comArtikli"); // NOI18N
        comArtikli.setPreferredSize(new java.awt.Dimension(200, 30));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${popisArtikala}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, comArtikli);
        bindingGroup.addBinding(jComboBoxBinding);

        comArtikli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Promjena(evt);
            }
        });
        jPanel5.add(comArtikli);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(IzmjenaKalkulacije.class, this);
        jButton1.setAction(actionMap.get("DodajStavkuKalkulacije")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel5.add(jButton1);

        jButton2.setAction(actionMap.get("IzbrisiStavku")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel5.add(jButton2);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        jSeparator2.setPreferredSize(new java.awt.Dimension(6, 30));
        jPanel5.add(jSeparator2);

        cmdZatvori1.setAction(actionMap.get("SpremiKalkulaciju")); // NOI18N
        cmdZatvori1.setText(resourceMap.getString("cmdZatvori1.text")); // NOI18N
        cmdZatvori1.setName("cmdZatvori1"); // NOI18N
        cmdZatvori1.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel5.add(cmdZatvori1);

        cmdZatvori.setAction(actionMap.get("Zatvori")); // NOI18N
        cmdZatvori.setText(resourceMap.getString("cmdZatvori.text")); // NOI18N
        cmdZatvori.setName("cmdZatvori"); // NOI18N
        cmdZatvori.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel5.add(cmdZatvori);

        jPanel6.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel4.setName("jPanel4"); // NOI18N

        jButton3.setAction(actionMap.get("IspisBarkoda")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel4.add(jButton3);

        cmdDeklaracije.setAction(actionMap.get("DeklaracijaArtikla")); // NOI18N
        cmdDeklaracije.setText(resourceMap.getString("cmdDeklaracije.text")); // NOI18N
        cmdDeklaracije.setName("cmdDeklaracije"); // NOI18N
        cmdDeklaracije.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel4.add(cmdDeklaracije);

        cmdKartica.setAction(actionMap.get("KarticaArtikla")); // NOI18N
        cmdKartica.setText(resourceMap.getString("cmdKartica.text")); // NOI18N
        cmdKartica.setName("cmdKartica"); // NOI18N
        cmdKartica.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel4.add(cmdKartica);

        jPanel6.add(jPanel4, java.awt.BorderLayout.LINE_START);

        add(jPanel6, java.awt.BorderLayout.PAGE_END);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 1));
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        dobavljaci.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        dobavljaci.setMinimumSize(new java.awt.Dimension(80, 30));
        dobavljaci.setName(""); // NOI18N
        dobavljaci.setPreferredSize(new java.awt.Dimension(80, 30));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${dobavljaci}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, dobavljaci);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${kalkulacija.dobavljac}"), dobavljaci, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel1.setDisplayedMnemonic('d');
        jLabel1.setLabelFor(dobavljaci);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        oznakaDokumenta.setMargin(new java.awt.Insets(2, 2, 2, 2));
        oznakaDokumenta.setName("oznakaDokumenta"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${kalkulacija.oznakaDokumenta}"), oznakaDokumenta, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        datumDokumenta.setMargin(new java.awt.Insets(2, 2, 2, 2));
        datumDokumenta.setName("datumDokumenta"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        dospijecePlacanja.setMargin(new java.awt.Insets(2, 2, 2, 2));
        dospijecePlacanja.setName("dospijecePlacanja"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datumDokumenta, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(dospijecePlacanja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(oznakaDokumenta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(dobavljaci, javax.swing.GroupLayout.Alignment.TRAILING, 0, 213, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dobavljaci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oznakaDokumenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datumDokumenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dospijecePlacanja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        jPanel7.add(jPanel3, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        iznosUkupno.setFont(resourceMap.getFont("iznosUkupno.font")); // NOI18N
        iznosUkupno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iznosUkupno.setText(resourceMap.getString("iznosUkupno.text")); // NOI18N
        iznosUkupno.setName("iznosUkupno"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iznosUkupno, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iznosUkupno, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        jPanel7.add(jPanel2, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        datumKalkulacije.setMargin(new java.awt.Insets(2, 2, 2, 2));
        datumKalkulacije.setName("datumKalkulacije"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel5.setDisplayedMnemonic('k');
        jLabel5.setLabelFor(oznakaKalkulacije);
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        oznakaKalkulacije.setText(resourceMap.getString("oznakaKalkulacije.text")); // NOI18N
        oznakaKalkulacije.setMargin(new java.awt.Insets(2, 2, 2, 2));
        oznakaKalkulacije.setName("oznakaKalkulacije"); // NOI18N

        datumPlacanja.setText(resourceMap.getString("datumPlacanja.text")); // NOI18N
        datumPlacanja.setMargin(new java.awt.Insets(2, 2, 2, 2));
        datumPlacanja.setName("datumPlacanja"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        kalkulacijuIzradio.setMargin(new java.awt.Insets(2, 2, 2, 2));
        kalkulacijuIzradio.setName("kalkulacijuIzradio"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${kalkulacija.izradio}"), kalkulacijuIzradio, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kalkulacijuIzradio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(datumPlacanja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(datumKalkulacije, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(oznakaKalkulacije, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oznakaKalkulacije, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datumKalkulacije, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datumPlacanja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kalkulacijuIzradio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        jPanel7.add(jPanel1, gridBagConstraints);

        add(jPanel7, java.awt.BorderLayout.PAGE_START);

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        grid.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        grid.setName("grid"); // NOI18N
        grid.setNextFocusableComponent(comArtikli);
        grid.setRowHeight(35);
        grid.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${stavke}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, grid);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${artikal}"));
        columnBinding.setColumnName("Artikal");
        columnBinding.setColumnClass(Acosoft.Processing.DataBox.Roba.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${kolicina}"));
        columnBinding.setColumnName("Kolicina");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mjera}"));
        columnBinding.setColumnName("Mjera");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cijena}"));
        columnBinding.setColumnName("Cijena");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rabat}"));
        columnBinding.setColumnName("Rabat");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fakturnaBezPoreza}"));
        columnBinding.setColumnName("Fakturna Bez Poreza");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${zavisniTroskovi}"));
        columnBinding.setColumnName("Zavisni Troskovi");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${marza}"));
        columnBinding.setColumnName("Marza");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cijenaBezPoreza}"));
        columnBinding.setColumnName("Cijena Bez Poreza");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cijenaSaPorezom}"));
        columnBinding.setColumnName("Cijena Sa Porezom");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ukupno}"));
        columnBinding.setColumnName("Ukupno");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(grid);
        grid.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("grid.columnModel.title0")); // NOI18N
        grid.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("grid.columnModel.kolicina")); // NOI18N
        grid.getColumnModel().getColumn(1).setCellEditor(new NumberCellEditor());
        grid.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRenderer());
        grid.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("grid.columnModel.title12")); // NOI18N
        grid.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("grid.columnModel.title1")); // NOI18N
        grid.getColumnModel().getColumn(3).setCellEditor(new NumberCellEditor(false));
        grid.getColumnModel().getColumn(3).setCellRenderer(new NumberCellRenderer(false, false));
        grid.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("grid.columnModel.title15")); // NOI18N
        grid.getColumnModel().getColumn(4).setCellEditor(new PercentCellEditor());
        grid.getColumnModel().getColumn(4).setCellRenderer(new PercentCellRenderer());
        grid.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("grid.columnModel.title4")); // NOI18N
        grid.getColumnModel().getColumn(5).setCellRenderer(new NumberCellRenderer());
        grid.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("grid.columnModel.title9")); // NOI18N
        grid.getColumnModel().getColumn(6).setCellEditor(new NumberCellEditor());
        grid.getColumnModel().getColumn(6).setCellRenderer(new NumberCellRenderer());
        grid.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("grid.columnModel.title10")); // NOI18N
        grid.getColumnModel().getColumn(7).setCellEditor(new PercentCellEditor());
        grid.getColumnModel().getColumn(7).setCellRenderer(new PercentCellRenderer(true));
        grid.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("grid.columnModel.title2")); // NOI18N
        grid.getColumnModel().getColumn(8).setCellEditor(new NumberCellEditor());
        grid.getColumnModel().getColumn(8).setCellRenderer(new NumberCellRenderer());
        grid.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("grid.columnModel.title3")); // NOI18N
        grid.getColumnModel().getColumn(9).setCellEditor(new NumberCellEditor());
        grid.getColumnModel().getColumn(9).setCellRenderer(new NumberCellRenderer());
        grid.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("grid.columnModel.title16")); // NOI18N
        grid.getColumnModel().getColumn(10).setCellRenderer(new NumberCellRenderer());

        jPanel8.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel8, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void Promjena(java.awt.event.ActionEvent evt)//GEN-FIRST:event_Promjena
    {//GEN-HEADEREND:event_Promjena
        if(evt.getActionCommand().equals(SearchEvent.searchCommand))
        {
            SearchEvent search = (SearchEvent) evt;
            String pattern = search.getSearchPattern().toLowerCase();

            for(Roba roba : popisArtikala)
            {
                if(roba.getNaziv().toLowerCase().startsWith(pattern)
                        || roba.getSifra().toLowerCase().startsWith(pattern))
                {
                    comArtikli.setSelectedItem(roba);
                    return;
                }
            }
        }
    }//GEN-LAST:event_Promjena

    public List<Roba> getPopisArtikala()
    {
        return popisArtikala;
    }

    @Action
    public void DodajStavkuKalkulacije()
    {
        Object item = comArtikli.getSelectedItem();

        if(item instanceof Roba)
        {
            Roba roba = (Roba) item;

            StavkaKalkulacije stavka = new StavkaKalkulacije();
            stavka.setKalkulacija(getKalkulacija());

            getStavke().add(stavka);
            stavka.setArtikal(roba);

            getManager().persist(stavka);

            int index = getStavke().indexOf(stavka);
            int view = grid.convertRowIndexToView(index);
            
            grid.setRowSelectionInterval(view, view);
            grid.setColumnSelectionInterval(0, 0);
            grid.requestFocusInWindow();

            Code.ScrollToLastRow(grid);

            IzracunajUkupno();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdDeklaracije;
    private javax.swing.JButton cmdKartica;
    private javax.swing.JButton cmdZatvori;
    private javax.swing.JButton cmdZatvori1;
    private javax.swing.JComboBox comArtikli;
    private javax.swing.JTextField datumDokumenta;
    private javax.swing.JTextField datumKalkulacije;
    private javax.swing.JTextField datumPlacanja;
    private javax.swing.JComboBox dobavljaci;
    private javax.swing.JTextField dospijecePlacanja;
    private javax.swing.JTable grid;
    private javax.swing.JLabel iznosUkupno;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField kalkulacijuIzradio;
    private javax.swing.JTextField oznakaDokumenta;
    private javax.swing.JTextField oznakaKalkulacije;
    private java.util.List<Roba> popisArtikala;
    private java.util.List<Dobavljac> popisDobavljaca;
    private javax.persistence.Query queryArtikli;
    private javax.persistence.Query queryDobavljac;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public List<StavkaKalkulacije> getStavke()
    {
        return stavke;
    }

    public void setStavke(List<StavkaKalkulacije> stavke)
    {
        this.stavke = stavke;
    }

    @Action
    public void Zatvori()
    {
        getZatvori().Izvrsi();
        getManager().close();
    }

    public void UkloniSlusatelja()
    {
        ArtikalEvents.Events().removeListener(izmjeneArtikala);
        AkcijeDobavljaca.ukloniSlusatelja(izmjeneDobavljaca);
    }

    @Action
    public void IzbrisiStavku()
    {
        if(grid.getSelectedRowCount() > 0)
        {
            int index = grid.getSelectedRow();
            int model = grid.convertRowIndexToModel(index);

            StavkaKalkulacije stavka = getStavke().get(model);
            getManager().remove(stavka);
            getStavke().remove(stavka);

            IzracunajUkupno();
        }
        else
            Tasks.showMessage("Izaberite stavku kalkulacije koju želite izbrisati.");
    }

    private void SpremiKartice()
    {
        for(StavkaKalkulacije stavka : getStavke())
        {
            for(KarticaStavkeKalkulacije kartica : stavka.getKartice())
                getManager().remove(kartica);

            stavka.getKartice().clear();

            KarticaStavkeKalkulacije kartica = stavka.KreirajKarticu();
            stavka.getKartice().add(kartica);
        }
    }
    
    private void spremiKnjiguPopisa()
    {
        KnjigaPopisa knjiga = new KnjigaPopisa();

        knjiga.setDokument("Kalkulacija " + getKalkulacija().getOznakaKalkulacije());
        knjiga.setDatum(getKalkulacija().getDatumIzrade());
        knjiga.setZaduzenje(getKalkulacija().getUkupnaProdajnaVrijednost());

        getManager().persist(knjiga);
        
        if(novaKalkulacija)
        {
            Tasks.showMessage("Ukupni iznos kalkulacije je dodan kao zaduđenje u knjigu popisa");
        }
        else
        {
            Tasks.showMessage("UPOZORENJE: Knjigu popisa morate ručno ažurirati u skladu sa promjenama kalkulacije!");
        }
    }

    @Action
    public void SpremiKalkulaciju()
    {
        if(!getManager().getTransaction().isActive())
            getManager().getTransaction().begin();
        
        if(oznakaKalkulacije.isEditable())
            getKalkulacija().setOznakaKalkulacije(oznakaKalkulacije.getText());
        else if(editMode == false)
        {
            InfoSekvenca sekvenca = Pro3App.getApplication().getSekvencaKalkulacije();
            getKalkulacija().setOznakaKalkulacije(sekvenca.SljedecaFormatiranaSekvenca());

            oznakaKalkulacije.setEditable(true);
            oznakaKalkulacije.setText(getKalkulacija().getOznakaKalkulacije());
        }

        SpremiKartice();
        spremiKnjiguPopisa();

        getManager().persist(getKalkulacija());
        getManager().getTransaction().commit();
        
        Tasks.showMessage("Kalkulacija je uspješno spremljena.");

        if(editMode == true)
            AkcijeKalkulacija.IzmjenjenaKalkulacija(this, getKalkulacija());
        else
            AkcijeKalkulacija.KreiranaKalkulacija(this, getKalkulacija());

        getManager().getTransaction().begin();
    }

    @Action
    public void KarticaArtikla()
    {
        int index = grid.getSelectedRow();
        if(index >= 0)
        {
            int model = grid.convertRowIndexToModel(index);
            Pro3View view = (Pro3View) Pro3App.getApplication().getMainView();
            view.Show(new PregledKartice(getStavke().get(model).getArtikal()));
        }
        else Tasks.showMessage("Izaberite stavku sa artiklom čiju karticu želite otvoriti.");
    }

    private Roba getOdabranuArtikal()
    {
        StavkaKalkulacije stavka = getOdabranaStavka();

        if(stavka != null)
            return stavka.getArtikal();
        else
            return null;
    }

    private StavkaKalkulacije getOdabranaStavka()
    {
        if(grid.getSelectedRowCount() > 0)
        {
            int view = grid.getSelectedRow();
            int model = grid.convertRowIndexToModel(view);

            return getStavke().get(model);
        }
        else return null;
    }

    @Action
    public void DeklaracijaArtikla()
    {
        Roba artikal = getOdabranuArtikal();

        if(artikal != null)
        {
            PromjenaOpisaArtikla deklaracija = new PromjenaOpisaArtikla(artikal , null);
            Pro3View view = (Pro3View) Pro3App.getApplication().getMainView();
            view.Show(deklaracija);
        }
        else Tasks.showMessage("Izaberite stavku sa artikom kome želite promjeniti deklaraciju.");
    }

    @Action
    public void IspisBarkoda()
    {
        StavkaKalkulacije stavka = getOdabranaStavka();

        if(stavka != null)
            try
            {
                PregledSvihKalkulacija.IspisBarkoda(stavka);
            }
            catch (JRException ex)
            {
                Logger.getLogger(IzmjenaKalkulacije.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionView(ex).setVisible(true);
            }
        else
            Tasks.showMessage("Izaberite stavku za ispis barkoda");
    }

}
