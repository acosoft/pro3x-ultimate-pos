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
 * PregledSvihKalkulacija.java
 *
 * Created on 21.11.2009., 17:36:35
 */

package Pro3x.Kalkulacije;

import Acosoft.Processing.Components.Code;
import Acosoft.Processing.Components.DateCellRenderer;
import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.NumberCellRenderer;
import Acosoft.Processing.Components.PercentCellRenderer;
import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.Pro3App;
import Acosoft.Processing.Pro3Postavke;
import Acosoft.Processing.Pro3View;
import Pro3x.Code.ReportingServices;
import Pro3x.Configuration.General;
import Pro3x.Kalkulacije.Model.Kalkulacija;
import Pro3x.Kalkulacije.Model.StavkaKalkulacije;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.jdesktop.application.Action;

/**
 *
 * @author nonstop
 */
public class PregledSvihKalkulacija extends javax.swing.JInternalFrame {

    private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private SlusateljKalkulacija slusateljKalkulacija;

    /** Creates new form PregledSvihKalkulacija */
    public PregledSvihKalkulacija() {
        initComponents();

        if(General.isUserMode()) gridKalkulacije.setComponentPopupMenu(null);

        if(gridKalkulacije.getRowCount() > 0)
        {
            Code.ScrollToLastRow(gridKalkulacije);
            Code.SelectLastRow(gridKalkulacije);
        }

        slusateljKalkulacija = new SlusateljKalkulacija()
        {

            @Override
            public void IzbrisanaKalkulacija(Object izvor, Kalkulacija kalkulacija)
            {
                if(izvor != this) popisKalkulacija.remove(kalkulacija);
            }

            @Override
            public void KreiranaKalkulacija(Object izvor, Kalkulacija kalkulacija)
            {
                if(izvor != this) popisKalkulacija.add(kalkulacija);
            }

            @Override
            public void IzmjenjenaKalkulacija(Object izvor, Kalkulacija kalkulacija)
            {
                if(izvor != PregledSvihKalkulacija.this)
                {
                    int index = popisKalkulacija.indexOf(kalkulacija);
                    popisKalkulacija.set(index, kalkulacija);

                    int red = gridKalkulacije.getSelectedRow();

                    gridKalkulacije.clearSelection();
                    gridKalkulacije.setRowSelectionInterval(red, red);
                }
            }
        };

        AkcijeKalkulacija.DodajSlusatelja(slusateljKalkulacija);
        datumPlacanja.setText(sf.format(Calendar.getInstance().getTime()));
    }

    private DefaultTableColumnModel ConfigureColumns()
    {
        DefaultTableColumnModel model = new DefaultTableColumnModel();
        NumberCellRenderer numberRenderer = new NumberCellRenderer();
        PercentCellRenderer percentRenderer = new PercentCellRenderer();

        TableColumn column = new TableColumn(0);
        column.setHeaderValue("Artikal");
        model.addColumn(column);

        column = new TableColumn(1);
        column.setHeaderValue("Kolicina");
        column.setCellRenderer(numberRenderer);
        model.addColumn(column);

        column = new TableColumn(2);
        column.setHeaderValue("Mjera");
        model.addColumn(column);

        column = new TableColumn(3);
        column.setHeaderValue("Nabavna bez poreza");
        column.setCellRenderer(numberRenderer);
        model.addColumn(column);

        column = new TableColumn(4);
        column.setHeaderValue("Rabat");
        column.setCellRenderer(percentRenderer);
        model.addColumn(column);

        column = new TableColumn(5);
        column.setHeaderValue("Fakturna bez poreza");
        column.setCellRenderer(numberRenderer);
        model.addColumn(column);

//        column = new TableColumn(6);
//        column.setHeaderValue("Ukupna fakturna");
//        column.setCellRenderer(numberRenderer);
//        model.addColumn(column);

        column = new TableColumn(7);
        column.setHeaderValue("Marza");
        column.setCellRenderer(percentRenderer);
        model.addColumn(column);

//        column = new TableColumn(8);
//        column.setHeaderValue("Prodajna");
//        column.setCellRenderer(numberRenderer);
//        model.addColumn(column);

        column = new TableColumn(10);
        column.setHeaderValue("Osnovica");
        column.setCellRenderer(numberRenderer);
        model.addColumn(column);

        column = new TableColumn(9);
        column.setHeaderValue("Ukupno");
        column.setCellRenderer(numberRenderer);
        model.addColumn(column);
        
        

        return model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        manager = java.beans.Beans.isDesignTime() ? null : Pro3x.Persistence.createEntityManagerFactory().createEntityManager();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(PregledSvihKalkulacija.class);
        queryKalkulacije = java.beans.Beans.isDesignTime() ? null : manager.createQuery(resourceMap.getString("queryKalkulacije.query")); // NOI18N
        popisKalkulacija = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(new java.util.LinkedList(queryKalkulacije.getResultList()));
        mnuIspis = new javax.swing.JPopupMenu();
        mnuIspisOdabraneKalkulacije = new javax.swing.JMenuItem();
        mnuIspisKalkulacija = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        gridKalkulacije = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        gridStavke = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        datumPlacanja = new javax.swing.JTextField();
        cmdPlati = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        cmdIzmjeni = new javax.swing.JButton();
        cmdIzbrisi = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        cmdIspis = new javax.swing.JButton();
        cmdZatvori = new javax.swing.JButton();

        mnuIspis.setName("mnuIspis"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(PregledSvihKalkulacija.class, this);
        mnuIspisOdabraneKalkulacije.setAction(actionMap.get("IspisKalkulacije")); // NOI18N
        mnuIspisOdabraneKalkulacije.setText(resourceMap.getString("mnuIspisOdabraneKalkulacije.text")); // NOI18N
        mnuIspisOdabraneKalkulacije.setName("mnuIspisOdabraneKalkulacije"); // NOI18N
        mnuIspis.add(mnuIspisOdabraneKalkulacije);

        mnuIspisKalkulacija.setAction(actionMap.get("IspisKalkulacija")); // NOI18N
        mnuIspisKalkulacija.setIcon(resourceMap.getIcon("mnuIspisKalkulacija.icon")); // NOI18N
        mnuIspisKalkulacija.setText(resourceMap.getString("mnuIspisKalkulacija.text")); // NOI18N
        mnuIspisKalkulacija.setMargin(new java.awt.Insets(5, 2, 4, 20));
        mnuIspisKalkulacija.setName("mnuIspisKalkulacija"); // NOI18N
        mnuIspis.add(mnuIspisKalkulacija);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(resourceMap.getString("pregled-svih-kalkulacija.title")); // NOI18N
        setName("pregled-svih-kalkulacija"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 5));
        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setName("split-kalkulacije"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        gridKalkulacije.setComponentPopupMenu(mnuIspis);
        gridKalkulacije.setName("gridKalkulacije"); // NOI18N
        gridKalkulacije.setRowHeight(35);
        gridKalkulacije.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, popisKalkulacija, gridKalkulacije);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${datumIzrade}"));
        columnBinding.setColumnName("Datum Izrade");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${oznakaKalkulacije}"));
        columnBinding.setColumnName("Oznaka Kalkulacije");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${oznakaDokumenta}"));
        columnBinding.setColumnName("Oznaka Dokumenta");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${izradio}"));
        columnBinding.setColumnName("Izradio");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${dobavljac.naziv}"));
        columnBinding.setColumnName("Dobavljac.naziv");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${datumDospijeca}"));
        columnBinding.setColumnName("Datum Dospijeca");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${datumPlacanja}"));
        columnBinding.setColumnName("Datum Placanja");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ukupnaNabavnaVrijednost}"));
        columnBinding.setColumnName("Ukupna Nabavna Vrijednost");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ukupnaProdajnaVrijednost}"));
        columnBinding.setColumnName("Ukupna Prodajna Vrijednost");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(gridKalkulacije);
        gridKalkulacije.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title0")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(0).setCellRenderer(new DateCellRenderer("dd.MM.yyyy"));
        gridKalkulacije.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title1")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title2")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title3")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title4")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title5")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(5).setCellRenderer(new DateCellRenderer("dd.MM.yyyy"));
        gridKalkulacije.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title6")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(6).setCellRenderer(new DateCellRenderer("dd.MM.yyyy"));
        gridKalkulacije.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title7")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(7).setCellRenderer(new NumberCellRenderer());
        gridKalkulacije.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("gridKalkulacije.columnModel.title8")); // NOI18N
        gridKalkulacije.getColumnModel().getColumn(8).setCellRenderer(new NumberCellRenderer());

        jSplitPane1.setTopComponent(jScrollPane1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        gridStavke.setAutoCreateColumnsFromModel(false);
        gridStavke.setName("gridStavke"); // NOI18N
        gridStavke.setRowHeight(35);
        gridStavke.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedElement.stavke}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gridKalkulacije, eLProperty, gridStavke);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${artikal.naziv}"));
        columnBinding.setColumnName("Artikal.naziv");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${kolicina}"));
        columnBinding.setColumnName("Kolicina");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mjera}"));
        columnBinding.setColumnName("Mjera");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cijena}"));
        columnBinding.setColumnName("Cijena");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rabat}"));
        columnBinding.setColumnName("Rabat");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fakturnaBezPoreza}"));
        columnBinding.setColumnName("Fakturna Bez Poreza");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fakturnaSaPorezom}"));
        columnBinding.setColumnName("Fakturna Sa Porezom");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${marza}"));
        columnBinding.setColumnName("Marza");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cijenaSaPorezom}"));
        columnBinding.setColumnName("Cijena Sa Porezom");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ukupno}"));
        columnBinding.setColumnName("Ukupno");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${osnovica}"));
        columnBinding.setColumnName("Osnovica");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        jTableBinding.setSourceNullValue(new LinkedList<StavkaKalkulacije>());
        jTableBinding.setSourceUnreadableValue(new LinkedList<StavkaKalkulacije>());
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(gridStavke);
        gridStavke.setColumnModel(ConfigureColumns());

        jSplitPane1.setRightComponent(jScrollPane2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1);

        datumPlacanja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        datumPlacanja.setText(resourceMap.getString("datumPlacanja.text")); // NOI18N
        datumPlacanja.setMargin(new java.awt.Insets(2, 2, 2, 2));
        datumPlacanja.setName("datumPlacanja"); // NOI18N
        datumPlacanja.setPreferredSize(new java.awt.Dimension(200, 30));
        datumPlacanja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                datumPlacanjaFocusLost(evt);
            }
        });
        jPanel1.add(datumPlacanja);

        cmdPlati.setAction(actionMap.get("PotvrdiUplatu")); // NOI18N
        cmdPlati.setText(resourceMap.getString("cmdPlati.text")); // NOI18N
        cmdPlati.setName("cmdPlati"); // NOI18N
        cmdPlati.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdPlati);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        jSeparator2.setPreferredSize(new java.awt.Dimension(6, 30));
        jPanel1.add(jSeparator2);

        cmdIzmjeni.setAction(actionMap.get("IzmjeniKalkulaciju")); // NOI18N
        cmdIzmjeni.setText(resourceMap.getString("cmdIzmjeni.text")); // NOI18N
        cmdIzmjeni.setName("cmdIzmjeni"); // NOI18N
        cmdIzmjeni.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdIzmjeni);

        cmdIzbrisi.setAction(actionMap.get("IzbrisiKalkulaciju")); // NOI18N
        cmdIzbrisi.setText(resourceMap.getString("cmdIzbrisi.text")); // NOI18N
        cmdIzbrisi.setName("cmdIzbrisi"); // NOI18N
        cmdIzbrisi.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdIzbrisi);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setName("jSeparator1"); // NOI18N
        jSeparator1.setPreferredSize(new java.awt.Dimension(6, 30));
        jPanel1.add(jSeparator1);

        cmdIspis.setAction(actionMap.get("IspisKalkulacije")); // NOI18N
        cmdIspis.setText(resourceMap.getString("cmdIspis.text")); // NOI18N
        cmdIspis.setName("cmdIspis"); // NOI18N
        cmdIspis.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdIspis);

        cmdZatvori.setAction(actionMap.get("Zatvori")); // NOI18N
        cmdZatvori.setText(resourceMap.getString("cmdZatvori.text")); // NOI18N
        cmdZatvori.setName("cmdZatvori"); // NOI18N
        cmdZatvori.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdZatvori);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void datumPlacanjaFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_datumPlacanjaFocusLost
    {//GEN-HEADEREND:event_datumPlacanjaFocusLost
        try
        {
            Date datum = sf.parse(datumPlacanja.getText());
            datumPlacanja.setText(sf.format(datum));
        }
        catch (ParseException ex)
        {
            Logger.getLogger(PregledSvihKalkulacija.class.getName()).log(Level.SEVERE, null, ex);
            datumPlacanja.setText(sf.format(Calendar.getInstance().getTime()));
        }
    }//GEN-LAST:event_datumPlacanjaFocusLost

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt)//GEN-FIRST:event_formInternalFrameClosing
    {//GEN-HEADEREND:event_formInternalFrameClosing
        AkcijeKalkulacija.UkloniSlusatelja(slusateljKalkulacija);
    }//GEN-LAST:event_formInternalFrameClosing

    private Kalkulacija getOdabranaKalkulacija()
    {
        if(gridKalkulacije.getSelectedRow() >= 0)
        {
            int index = gridKalkulacije.getSelectedRow();
            int model = gridKalkulacije.convertRowIndexToModel(index);
            
            return popisKalkulacija.get(model);
        }
        else
            return null;
    }
    
    private List<Kalkulacija> getOdabraneKalkulacije()
    {
        List<Kalkulacija> kalkulacije = new ArrayList<Kalkulacija>();
        
        if(gridKalkulacije.getSelectedRowCount() > 0)
        {
            for (int vIndex : gridKalkulacije.getSelectedRows()) {
                int mIndex = gridKalkulacije.convertRowIndexToModel(vIndex);
                kalkulacije.add(popisKalkulacija.get(mIndex));
            }
        }
        
        return kalkulacije;
    }

    @Action
    public void PotvrdiUplatu()
    {
        int result = JOptionPane.showConfirmDialog(this, "Želite li potvrditi uplatu sa datumom "
                + datumPlacanja.getText() + "?", "Potvrda", JOptionPane.YES_NO_OPTION);

        if(result == JOptionPane.YES_OPTION)
        {
            Kalkulacija kalkulacija = getOdabranaKalkulacija();
            if(kalkulacija != null)
            {
                try
                {
                    Kalkulacija mk = manager.find(Kalkulacija.class, kalkulacija.getId());
                    Date datum = sf.parse(datumPlacanja.getText());
                    
                    mk.setDatumPlacanja(datum);
                    kalkulacija.setDatumPlacanja(datum);

                    manager.getTransaction().begin();
                    manager.persist(mk);
                    manager.getTransaction().commit();

                    Tasks.showMessage("Potvrda o plaćanju kalkulacije je uspješno spremljena");
                }
                catch (ParseException ex)
                {
                    Logger.getLogger(PregledSvihKalkulacija.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @Action
    public void IzmjeniKalkulaciju()
    {
        int index = gridKalkulacije.getSelectedRow();
        if(index >= 0)
        {
            int model = gridKalkulacije.convertRowIndexToModel(index);
            Kalkulacija kalkulacija = popisKalkulacija.get(model);

            Pro3View main = (Pro3View) Pro3App.getApplication().getMainView();
            main.Show(new PregledKalkulacije(kalkulacija.getId()));

            //AkcijeKalkulacija.IzmjenjenaKalkulacija(this, kalkulacija);
        }
    }

    @Action
    public void IzbrisiKalkulaciju()
    {
        Kalkulacija kalkulacija = getOdabranaKalkulacija();
        if(kalkulacija != null)
        {
            int rezultat = JOptionPane.showConfirmDialog(this, "Želite li izbrisati odabranu kalkulaciju?",
                    "Upozorenje", JOptionPane.YES_NO_OPTION);

            if(rezultat == JOptionPane.YES_OPTION)
            {
                Kalkulacija mk = manager.find(Kalkulacija.class, kalkulacija.getId());
                manager.getTransaction().begin();
                manager.remove(mk);
                manager.getTransaction().commit();

                popisKalkulacija.remove(kalkulacija);
                AkcijeKalkulacija.IzbrisanaKalkulacija(this, kalkulacija);
                Tasks.showMessage("Kalkulacija je uspješno izbrisana");
            }
        }
    }
    
    public void IspisViseKalkulacija()
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
                return "PDF Arhiva Kalkulacija ( *.pdf )";
            }
        });
            
        int odabir = xf.showSaveDialog(this);

        if(odabir == JFileChooser.APPROVE_OPTION)
        {
            try 
            {
                List<JasperPrint> arhiva = new ArrayList<JasperPrint>();
                
                InputStream rstream = PregledSvihKalkulacija.class.getResourceAsStream("resources/IspisKalkulacije.jasper");
                JasperReport jr = (JasperReport) JRLoader.loadObject(rstream);
                    
                for (Kalkulacija kalkulacija : getOdabraneKalkulacije()) 
                {
                    JRBeanCollectionDataSource js = new JRBeanCollectionDataSource(kalkulacija.getStavke());
                    arhiva.add(JasperFillManager.fillReport(jr, null, js));
                }
                
                File xfile;
                        
                if(xf.getSelectedFile().getAbsolutePath().endsWith(".pdf"))
                    xfile = xf.getSelectedFile();
                else
                    xfile = new File(xf.getSelectedFile().getAbsolutePath() + ".pdf");

                JRExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, arhiva);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE, xfile);
                exporter.exportReport();
                
                Tasks.showMessage("Arhiva kalkulacija je uspješno spremljena");
            } 
            catch (JRException ex) 
            {
                Logger.getLogger(PregledSvihKalkulacija.class.getName()).log(Level.SEVERE, null, ex);
                Tasks.showMessage("IZNIMKA: Neuspješan pokušaj spremanja arhive kalkulacija");
            }
        }
    }
    
    @Action
    public void IspisKalkulacije()
    {
        if(gridKalkulacije.getSelectedRowCount() == 1)
        {
            Kalkulacija kalkulacija = getOdabranaKalkulacija();
            
            try
            {
                JRBeanCollectionDataSource js = new JRBeanCollectionDataSource(kalkulacija.getStavke());
                InputStream rstream = PregledSvihKalkulacija.class.getResourceAsStream("resources/IspisKalkulacije.jasper");
                JasperReport jr = (JasperReport) JRLoader.loadObject(rstream);
                
                JasperPrint jp = JasperFillManager.fillReport(jr, null, js);
                
                ReportingServices.ShowReport(jp, "Ispis kalkulacije " + kalkulacija.getOznakaKalkulacije(),
                        "kalkulacija-ispis");
            }
            catch (JRException ex)
            {
                Logger.getLogger(PregledSvihKalkulacija.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(gridKalkulacije.getSelectedRowCount() > 1)
        {
            IspisViseKalkulacija();
        }
        else Tasks.showMessage("Izaberite kalkulaciju koju želite ispisati.");
    }

    @Action
    public void Zatvori()
    {
        try
        {
            setClosed(true);
        }
        catch (PropertyVetoException ex)
        {
            Logger.getLogger(PregledSvihKalkulacija.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public void IspisBarkod() throws JRException
    {
        int red = gridStavke.getSelectedRow();
        if(red >= 0)
        {
            StavkaKalkulacije stavka = getOdabranaKalkulacija().getStavke().get(red);
            IspisBarkoda(stavka);
        }
        else Tasks.showMessage("Izabrite stavku za ispis barkoda.");
    }

    public static void IspisBarkoda(StavkaKalkulacije stavka) throws JRException
    {
        List<StavkaKalkulacije> stavke = new LinkedList<StavkaKalkulacije>();
        stavke.add(stavka);

        JasperPrint jp = ReportingServices.LoadReport(PregledSvihKalkulacija.class,
                General.getBarcodeTemplate(), null, stavke);

        ReportingServices.ShowReport(jp, "Ispis barkoda stavke "
                + stavka.getArtikal().getNaziv() + " po kalkulaciji "
                + stavka.getKalkulacija().getOznakaKalkulacije(),
                "barkod-ispis");
    }

    private Map PripremiParametre(String opis) throws Exception
    {
        HashMap params = new HashMap();
        
        params.put("Logo", Pro3Postavke.getLogoStream());
        params.put("Zaglavlje", Pro3Postavke.getInfo().getZaglavlje());
        params.put("Opis", opis);

        return params;
    }

    @Action
    public void IspisKalkulacija()
    {
        int rows[] = gridKalkulacije.getSelectedRows();

        if(rows.length > 0)
        {
            LinkedList<Kalkulacija> priprema = new LinkedList<Kalkulacija>();

            for(int index : rows)
            {
                int model = gridKalkulacije.convertRowIndexToModel(index);
                priprema.add(popisKalkulacija.get(model));
            }

            try
            {
                String prva = priprema.get(0).getOznakaKalkulacije();
                String zadnja = priprema.get(priprema.size() - 1).getOznakaKalkulacije();
                String opis = MessageFormat.format("Kalkulacije od {0} do {1}", prva, zadnja);

                Map params = PripremiParametre(opis);
                JasperPrint print = ReportingServices.LoadReport(PregledSvihKalkulacija.class,
                        "resources/IspisOdabranihKalkulacija.jasper", params, priprema);
                ReportingServices.ShowReport(print, opis, "ispis-odabranih-kalkulacija");
            }
            catch (Exception ex)
            {
                Logger.getLogger(PregledSvihKalkulacija.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionView(ex).setVisible(true);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdIspis;
    private javax.swing.JButton cmdIzbrisi;
    private javax.swing.JButton cmdIzmjeni;
    private javax.swing.JButton cmdPlati;
    private javax.swing.JButton cmdZatvori;
    private javax.swing.JTextField datumPlacanja;
    private javax.swing.JTable gridKalkulacije;
    private javax.swing.JTable gridStavke;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.persistence.EntityManager manager;
    private javax.swing.JPopupMenu mnuIspis;
    private javax.swing.JMenuItem mnuIspisKalkulacija;
    private javax.swing.JMenuItem mnuIspisOdabraneKalkulacije;
    private java.util.List<Kalkulacija> popisKalkulacija;
    private javax.persistence.Query queryKalkulacije;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
