// Pro3x Community project
// Copyright (C) 2009  Aleksandar Zgonjan
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, http://www.gnu.org/licenses/gpl.html

package Acosoft.Processing;

import Acosoft.Processing.Components.NumberCellRenderer;
import Acosoft.Processing.Components.DateCellRenderer;
import Acosoft.Processing.Components.Code;
import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.NoEditCell;
import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.DataBox.Stavke;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityTransaction;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import Acosoft.Processing.Components.RacunCellRenderer;
import Acosoft.Processing.Components.Tasks;
import Acosoft.Processing.DataBox.KarticaStavkeRacuna;
import Acosoft.Processing.DataBox.PoreznaStavkaRacuna;
import Pro3x.BasicView;
import Pro3x.Code.AgregacijaRacuna;
import Pro3x.Code.ReportingServices;
import Pro3x.Code.VrsteUplate;
import Pro3x.Configuration.General;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperPrint;
import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.Binding;

/**
 *
 * @author  nonstop
 */

public class PregledRacuna extends BasicView
{
    private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
    /** Creates new form StornoRacuna */

    public PregledRacuna() {
        initComponents();
        //getRootPane().setDefaultButton(cmdIspis);

        datumPregleda.setText(sf.format(Calendar.getInstance().getTime()));

        if(!General.isUserMode())
            grid.setComponentPopupMenu(adminOptions);
    }

    public void OdaberiPosljednjiRacun()
    {
        Code.ScrollToLastRow(grid);
        Code.SelectLastRow(grid);
    }

    public List<Racun> ListaRacuna()
    {
        if(java.beans.Beans.isDesignTime())
            return java.util.Collections.emptyList();
        else
        {
            Calendar calendar = Calendar.getInstance();
            return ListaRacuna(calendar);
        }
    }

    public List<Racun> ListaRacuna(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date pocetak = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date kraj = calendar.getTime();

        return ListaRacuna(pocetak, kraj);
    }

    public List<Racun> ListaRacuna(Date pocetak, Date kraj)
    {
        racunQuery.setParameter("pocetak", pocetak);
        racunQuery.setParameter("kraj", kraj);

        return org.jdesktop.observablecollections.ObservableCollections.observableList(new java.util.LinkedList(racunQuery.getResultList()));
    }

    private List<AgregacijaRacuna> PripremiAgregacijeUsluga()
    {
//        List<AgregacijaRacuna> aggregacija = new LinkedList<AgregacijaRacuna>();
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.GotovinskaUplata));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.BezGotovinskaUplata));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.Amex));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.Diners));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.Maestro));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.Visa));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.Mastercard));
//        aggregacija.add(new AgregacijaUsluga(VrsteUplate.Storno));
//        return aggregacija;
        return null;
    }

    private List<AgregacijaRacuna> PripremiAgregacije()
    {
        List<AgregacijaRacuna> aggregacija = new LinkedList<AgregacijaRacuna>();

        aggregacija.add(new AgregacijaRacuna(VrsteUplate.GotovinskaUplata));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.BezGotovinskaUplata));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.Amex));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.Diners));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.Maestro));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.Visa));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.Mastercard));
        aggregacija.add(new AgregacijaRacuna(VrsteUplate.Storno));
        return aggregacija;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        proManager = getProManager();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(PregledRacuna.class);
        racunQuery = java.beans.Beans.isDesignTime() ? null : proManager.createQuery(resourceMap.getString("racunQuery.query")); // NOI18N
        racunList = ListaRacuna();
        robaQuery = java.beans.Beans.isDesignTime() ? null : proManager.createQuery(resourceMap.getString("robaQuery.query")); // NOI18N
        robaList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : robaQuery.getResultList();
        adminOptions = new javax.swing.JPopupMenu();
        mnuIzbrisi = new javax.swing.JMenuItem();
        mnuIzmjeni = new javax.swing.JMenuItem();
        separateProvjere = new javax.swing.JSeparator();
        mnuIspis = new javax.swing.JMenuItem();
        mnuIspisRada = new javax.swing.JMenuItem();
        mnuIspisPrometa = new javax.swing.JMenuItem();
        izborDatuma = new javax.swing.JPopupMenu();
        menuPrethodniDan = new javax.swing.JMenuItem();
        mnuDanasnjiDan = new javax.swing.JMenuItem();
        mnuSljedeciDan = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuPrethodniMjesec = new javax.swing.JMenuItem();
        mnuTrenutniMjesec = new javax.swing.JMenuItem();
        mnuSljedeciMjesec = new javax.swing.JMenuItem();
        cmdZatvori = new javax.swing.JButton();
        cmdStorniraj = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        grid = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        stavke = new javax.swing.JTable();
        razlog = new javax.swing.JTextField();
        cmdOsvjezi = new javax.swing.JButton();
        datumPregleda = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();

        adminOptions.setName("adminOptions"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(PregledRacuna.class, this);
        mnuIzbrisi.setAction(actionMap.get("IzbrisiRacune")); // NOI18N
        mnuIzbrisi.setIcon(resourceMap.getIcon("mnuIzbrisi.icon")); // NOI18N
        mnuIzbrisi.setText(resourceMap.getString("mnuIzbrisi.text")); // NOI18N
        mnuIzbrisi.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuIzbrisi.setName("mnuIzbrisi"); // NOI18N
        adminOptions.add(mnuIzbrisi);

        mnuIzmjeni.setAction(actionMap.get("IzmjeniRacun")); // NOI18N
        mnuIzmjeni.setIcon(resourceMap.getIcon("mnuIzmjeni.icon")); // NOI18N
        mnuIzmjeni.setText(resourceMap.getString("mnuIzmjeni.text")); // NOI18N
        mnuIzmjeni.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuIzmjeni.setName("mnuIzmjeni"); // NOI18N
        adminOptions.add(mnuIzmjeni);

        separateProvjere.setName("separateProvjere"); // NOI18N
        adminOptions.add(separateProvjere);

        mnuIspis.setAction(actionMap.get("IspisiRacun")); // NOI18N
        mnuIspis.setIcon(resourceMap.getIcon("mnuIspis.icon")); // NOI18N
        mnuIspis.setText(resourceMap.getString("mnuIspis.text")); // NOI18N
        mnuIspis.setMargin(new java.awt.Insets(5, 2, 4, 25));
        mnuIspis.setName("mnuIspis"); // NOI18N
        adminOptions.add(mnuIspis);

        mnuIspisRada.setAction(actionMap.get("IspisPrometaUsluga")); // NOI18N
        mnuIspisRada.setIcon(resourceMap.getIcon("mnuIspisRada.icon")); // NOI18N
        mnuIspisRada.setText(resourceMap.getString("mnuIspisRada.text")); // NOI18N
        mnuIspisRada.setMargin(new java.awt.Insets(5, 2, 4, 25));
        mnuIspisRada.setName("mnuIspisRada"); // NOI18N
        mnuIspisRada.setVisible(!General.isSkriveniNormativi());
        adminOptions.add(mnuIspisRada);

        mnuIspisPrometa.setAction(actionMap.get("IspisOdabranihRacuna")); // NOI18N
        mnuIspisPrometa.setIcon(resourceMap.getIcon("mnuIspisPrometa.icon")); // NOI18N
        mnuIspisPrometa.setText(resourceMap.getString("mnuIspisPrometa.text")); // NOI18N
        mnuIspisPrometa.setMargin(new java.awt.Insets(5, 2, 4, 25));
        mnuIspisPrometa.setName("mnuIspisPrometa"); // NOI18N
        adminOptions.add(mnuIspisPrometa);

        izborDatuma.setName("izborDatuma"); // NOI18N

        menuPrethodniDan.setAction(actionMap.get("PrethodniDan")); // NOI18N
        menuPrethodniDan.setText(resourceMap.getString("menuPrethodniDan.text")); // NOI18N
        menuPrethodniDan.setName("menuPrethodniDan"); // NOI18N
        izborDatuma.add(menuPrethodniDan);

        mnuDanasnjiDan.setAction(actionMap.get("TrenutniDan")); // NOI18N
        mnuDanasnjiDan.setText(resourceMap.getString("mnuDanasnjiDan.text")); // NOI18N
        mnuDanasnjiDan.setName("mnuDanasnjiDan"); // NOI18N
        izborDatuma.add(mnuDanasnjiDan);

        mnuSljedeciDan.setAction(actionMap.get("SljedeciDan")); // NOI18N
        mnuSljedeciDan.setText(resourceMap.getString("mnuSljedeciDan.text")); // NOI18N
        mnuSljedeciDan.setName("mnuSljedeciDan"); // NOI18N
        izborDatuma.add(mnuSljedeciDan);

        jSeparator3.setName("jSeparator3"); // NOI18N
        izborDatuma.add(jSeparator3);

        mnuPrethodniMjesec.setAction(actionMap.get("PrethodniMjesec")); // NOI18N
        mnuPrethodniMjesec.setText(resourceMap.getString("mnuPrethodniMjesec.text")); // NOI18N
        mnuPrethodniMjesec.setName("mnuPrethodniMjesec"); // NOI18N
        izborDatuma.add(mnuPrethodniMjesec);

        mnuTrenutniMjesec.setAction(actionMap.get("TrenutniMjesec")); // NOI18N
        mnuTrenutniMjesec.setText(resourceMap.getString("mnuTrenutniMjesec.text")); // NOI18N
        mnuTrenutniMjesec.setName("mnuTrenutniMjesec"); // NOI18N
        izborDatuma.add(mnuTrenutniMjesec);

        mnuSljedeciMjesec.setAction(actionMap.get("SljedeciMjesec")); // NOI18N
        mnuSljedeciMjesec.setText(resourceMap.getString("mnuSljedeciMjesec.text")); // NOI18N
        mnuSljedeciMjesec.setName("mnuSljedeciMjesec"); // NOI18N
        izborDatuma.add(mnuSljedeciMjesec);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(resourceMap.getString("popis-racuna.title")); // NOI18N
        setName("popis-racuna"); // NOI18N

        cmdZatvori.setMnemonic('X');
        cmdZatvori.setText(resourceMap.getString("cmdZatvori.text")); // NOI18N
        cmdZatvori.setName("cmdZatvori"); // NOI18N
        cmdZatvori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Zatvori(evt);
            }
        });

        cmdStorniraj.setText(resourceMap.getString("cmdStorniraj.text")); // NOI18N
        cmdStorniraj.setName("cmdStorniraj"); // NOI18N

        jSplitPane1.setDividerLocation(275);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        grid.setAutoCreateRowSorter(true);
        grid.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        grid.setName("grid"); // NOI18N
        grid.setRowHeight(24);
        grid.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, racunList, grid);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${entity}"));
        columnBinding.setColumnName("Entity");
        columnBinding.setColumnClass(Acosoft.Processing.DataBox.Racun.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${izdan}"));
        columnBinding.setColumnName("Izdan");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${naziv}"));
        columnBinding.setColumnName("Naziv");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresa}"));
        columnBinding.setColumnName("Adresa");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lokacija}"));
        columnBinding.setColumnName("Lokacija");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${storniran}"));
        columnBinding.setColumnName("Storniran");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ukupno}"));
        columnBinding.setColumnName("Ukupno");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(grid);
        grid.getColumnModel().getColumn(0).setPreferredWidth(100);
        grid.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("grid.columnModel.title0")); // NOI18N
        grid.getColumnModel().getColumn(0).setCellRenderer(new RacunCellRenderer());
        grid.getColumnModel().getColumn(1).setPreferredWidth(150);
        grid.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("grid.columnModel.title1")); // NOI18N
        grid.getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer("dd.MM.yyyy HH:mm"));
        grid.getColumnModel().getColumn(2).setPreferredWidth(200);
        grid.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("grid.columnModel.title2")); // NOI18N
        grid.getColumnModel().getColumn(3).setPreferredWidth(200);
        grid.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("grid.columnModel.title3")); // NOI18N
        grid.getColumnModel().getColumn(4).setPreferredWidth(200);
        grid.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("grid.columnModel.title4")); // NOI18N
        grid.getColumnModel().getColumn(5).setPreferredWidth(100);
        grid.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("grid.columnModel.title5")); // NOI18N
        grid.getColumnModel().getColumn(5).setCellRenderer(new DateCellRenderer("dd.MM.yyyy"));
        grid.getColumnModel().getColumn(6).setPreferredWidth(100);
        grid.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("grid.columnModel.title6")); // NOI18N
        grid.getColumnModel().getColumn(6).setCellRenderer(new NumberCellRenderer());

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        stavke.setAutoCreateColumnsFromModel(false);
        stavke.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        stavke.setName("stavke"); // NOI18N
        stavke.setRowHeight(24);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedElement.stavkeCollection}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, grid, eLProperty, stavke, "stavkeRacuna");
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${roba}"));
        columnBinding.setColumnName("Roba");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${kolicina}"));
        columnBinding.setColumnName("Kolicina");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mjera}"));
        columnBinding.setColumnName("Mjera");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${maloprodajnaCijena}"));
        columnBinding.setColumnName("Maloprodajna Cijena");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${iznos}"));
        columnBinding.setColumnName("Iznos");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${popust}"));
        columnBinding.setColumnName("Popust");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ukupno}"));
        columnBinding.setColumnName("Ukupno");
        columnBinding.setColumnClass(Double.class);
        jTableBinding.setSourceNullValue(new LinkedList());
        jTableBinding.setSourceUnreadableValue(new LinkedList());
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(stavke);

        jSplitPane1.setRightComponent(jScrollPane2);

        razlog.setText(resourceMap.getString("razlog.text")); // NOI18N
        razlog.setName("razlog"); // NOI18N
        razlog.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                razlogFocusGained(evt);
            }
        });

        cmdOsvjezi.setAction(actionMap.get("Osvjezi")); // NOI18N
        cmdOsvjezi.setText(resourceMap.getString("cmdOsvjezi.text")); // NOI18N
        cmdOsvjezi.setName("cmdOsvjezi"); // NOI18N

        datumPregleda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        datumPregleda.setText(resourceMap.getString("datumPregleda.text")); // NOI18N
        datumPregleda.setComponentPopupMenu(izborDatuma);
        datumPregleda.setName("datumPregleda"); // NOI18N
        datumPregleda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                datumPregledaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                datumPregledaFocusLost(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setName("jSeparator1"); // NOI18N

        jButton1.setAction(actionMap.get("PrikaziIzbornik")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(datumPregleda, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdOsvjezi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(razlog, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdStorniraj)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdZatvori)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdZatvori)
                        .addComponent(razlog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdStorniraj)
                        .addComponent(datumPregleda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)
                        .addComponent(cmdOsvjezi))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Zatvori(java.awt.event.ActionEvent evt)
    {
        try {
            setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void RacunIzmjenjen(Racun racun)
    {
        int line = racunList.indexOf(racun);
        racun = proManager.find(Racun.class, racun.getKljuc());
        proManager.refresh(racun);
        
        racunList.set(line, racun);

        Binding rsb = bindingGroup.getBinding("stavkeRacuna");
        rsb.unbind();
        rsb.bind();
    }

    @Action
    public void IspisiRacun()
    {
        int line = grid.getSelectedRow();
        line = grid.getRowSorter().convertRowIndexToModel(line);

        Racun xr = racunList.get(line);

        Long count = new Long((long) xr.getStavke().size());
        
        ReportingServices.IspisRacuna(xr);
    }
            
    
    private boolean obrisiRazlog = true;

    private void razlogFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_razlogFocusGained
    {//GEN-HEADEREND:event_razlogFocusGained
        if(obrisiRazlog)
        {
            obrisiRazlog = false;
            razlog.setText("");
        }
    }//GEN-LAST:event_razlogFocusGained

    private void datumPregledaFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_datumPregledaFocusLost
    {//GEN-HEADEREND:event_datumPregledaFocusLost
        try
        {
            getRootPane().setDefaultButton(null);
            String raspon = datumPregleda.getText();

            if(raspon.contains("-"))
            {
                String[] dijelovi = raspon.split("-");

                String pocetak = dijelovi[0];
                String kraj = dijelovi[1];

                String prikaz = sf.format(sf.parse(pocetak))
                        + "-" + sf.format(sf.parse(kraj));

                datumPregleda.setText(prikaz);
            }
            else datumPregleda.setText(sf.format(sf.parse(datumPregleda.getText())));
        }
        catch (ParseException ex)
        {
            Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            
            Calendar calendar = Calendar.getInstance();
            datumPregleda.setText(sf.format(calendar.getTime()));
            ListaRacuna(calendar);
        }
    }//GEN-LAST:event_datumPregledaFocusLost

    private void datumPregledaFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_datumPregledaFocusGained
    {//GEN-HEADEREND:event_datumPregledaFocusGained
        getRootPane().setDefaultButton(cmdOsvjezi);
    }//GEN-LAST:event_datumPregledaFocusGained
    
    public TableColumnModel ConfigureColumns()
    {
        TableColumnModel cols = new DefaultTableColumnModel();
        cols.setColumnMargin(0);
        
        NoEditCell noEdit = new NoEditCell();
        
        TableColumn col = new TableColumn(0, 200);
        col.setHeaderValue("Roba");
        col.setCellEditor(noEdit);
        cols.addColumn(col);
        
        col = new TableColumn(1);
        col.setCellEditor(noEdit);
        col.setHeaderValue("Količina");
        col.setCellRenderer(new NumberCellRenderer(true));
        
        cols.addColumn(col);
        
        col = new TableColumn(2);
        col.setCellEditor(noEdit);
        col.setHeaderValue("Mjera");
        cols.addColumn(col);
        
        col = new TableColumn(3);
        col.setCellEditor(noEdit);
        col.setHeaderValue("Cijena");
        col.setCellRenderer(new NumberCellRenderer());
        cols.addColumn(col);
        
//        col = new TableColumn(5);
//        col.setCellEditor(noEdit);
//        col.setHeaderValue("Popust");
//        col.setCellRenderer(new PercentCellRenderer());
//        cols.addColumn(col);

//        col = new TableColumn(4);
//        col.setCellEditor(noEdit);
//        col.setHeaderValue("Osnovica");
//        col.setCellRenderer(new NumberCellRenderer());
//        cols.addColumn(col);
        
        col = new TableColumn(6);
        col.setCellEditor(noEdit);
        col.setHeaderValue("Ukupno");
        col.setCellRenderer(new NumberCellRenderer());
        cols.addColumn(col);
        
        return cols;
    }

    @Action
    public void IzmjeniRacun()
    {
        int line = grid.getSelectedRow();
        if(line >= 0)
        {
            Racun racun = racunList.get(grid.convertRowIndexToModel(line));
            proManager.refresh(racun);
            
            //TODO: implementirati mogućnost izmjene računa
            //((Pro3View)getApplication().getMainView()).Show(new Pro3x.View.IzmjenaRacuna(racun.getKljuc(), this));
        }
    }

//    private void IzbrisiKarticeRacuna(Racun racun, EntityManager manager)
//    {
//        for(Stavke stavka : racun.getStavkeCollection())
//            manager.remove(stavka.getKarticaStavke());
//    }

    @Action
    public void IzbrisiRacune()
    {
        int[] lines = grid.getSelectedRows();

        if(lines.length > 0)
        {
            int result = JOptionPane.showConfirmDialog(this, "Želite li izbrisati odabrane račune?",
                    "Upozorenje", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(result == JOptionPane.YES_OPTION)
            {
                Binding bind = bindingGroup.getBinding("stavkeRacuna");
                bind.unbind();

                proManager.getTransaction().begin();

                List<Racun> removeList = new LinkedList<Racun>();
                for(int line : lines)
                {
                    int model = grid.convertRowIndexToModel(line);
                    Racun racun = racunList.get(model);
                    removeList.add(racun);

                    racun = proManager.getReference(Racun.class, racun.getKljuc());
                    proManager.refresh(racun);

//                    IzbrisiKarticeRacuna(racun, proManager);

                    proManager.remove(racun);
                }

                proManager.getTransaction().commit();
                racunList.removeAll(removeList);

                int count = grid.getRowCount();
                if(count > 0) grid.setRowSelectionInterval(count - 1, count - 1);

                bind.bind();
            }
        }
    }

    @Action
    public void Osvjezi()
    {
        Calendar calendar = Calendar.getInstance();

        try
        {
            if(datumPregleda.getText().contains("-"))
            {
                String[] datumi = datumPregleda.getText().split("-");
                racunList.clear();
                
                Date pocetak = sf.parse(datumi[0]);
                Date kraj = sf.parse(datumi[1]);
                racunList.addAll(ListaRacuna(pocetak, kraj));
            }
            else
            {
                calendar.setTime(sf.parse(datumPregleda.getText()));
                
                racunList.clear();
                racunList.addAll(ListaRacuna(calendar));
            }
        }
        catch (ParseException ex)
        {
            Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            datumPregleda.setText(sf.format(calendar.getTime()));
        }

        if(grid.getRowCount() > 0)
        {
            Code.SelectLastRow(grid);
            Code.ScrollToLastRow(grid);
        }
    }

    private Map PripremiParametre(String opis) throws Exception
    {
        Map params = new HashMap();
        
        String zaglavlje = Pro3Postavke.getInfo().getZaglavlje();
        params.put("Zaglavlje", zaglavlje);

        params.put("Opis", opis);
        params.put("Logo", Pro3Postavke.getLogoStream());
        
        return params;
    }

    private void SortirajRacune(List<Racun> racuni)
    {
        Collections.sort(racuni, new Comparator<Racun>()
        {
            public int compare(Racun o1, Racun o2)
            {
                return o1.getIzdan().compareTo(o2.getIzdan());
            }
        });
    }

    @Action
    public void IspisOdabranihRacuna()
    {
        int[] rows = grid.getSelectedRows();

        if(rows.length > 0)
        {
            List<Racun> priprema = new LinkedList<Racun>();
            List<AgregacijaRacuna> agregacija = PripremiAgregacije();

            for(int index : rows)
            {
                Racun racun = racunList.get(grid.convertRowIndexToModel(index));
                priprema.add(racun);

                if(racun.getStorniran() == null)
                {
                    boolean obradjen = false;

                    for(AgregacijaRacuna agg : agregacija)
                    {
                        if(agg.DodajRacun(racun) == true)
                        {
                            obradjen = true;
                            break;
                        }
                    }

                    final String upozorenje = "Upozorenje: Nepoznata vrsta prometa po računu ";
                    if(obradjen == false) Tasks.showMessage(upozorenje + racun.getOznaka());
                }
            }

            SortirajRacune(priprema);

            try
            {
                String prvi = sf.format(priprema.get(0).getIzdan());
                String zadnji = sf.format(priprema.get(priprema.size() - 1).getIzdan());
                String opis = MessageFormat.format("Promet po računima od {0} do {1}", prvi, zadnji);

                JasperPrint print = ReportingServices.LoadReport(PregledRacuna.class, 
                        "resources/IspisOdabranihRacuna.jasper", PripremiParametre(opis), agregacija);
                ReportingServices.ShowReport(print, opis, "ispis-odabranih-racuna");
            }
            catch (Exception ex)
            {
                Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionView(ex).setVisible(true);
            }
        }
    }

    @Action
    public void IspisPrometaUsluga()
    {
        int[] rows = grid.getSelectedRows();

        if(rows.length > 0)
        {
            List<Racun> priprema = new LinkedList<Racun>();
            List<AgregacijaRacuna> agregacija = PripremiAgregacijeUsluga();

            for(int index : rows)
            {
                Racun racun = racunList.get(grid.convertRowIndexToModel(index));
                priprema.add(racun);

                if(racun.getStorniran() == null)
                {
                    boolean obradjen = false;

                    for(AgregacijaRacuna agg : agregacija)
                    {
                        if(agg.DodajRacun(racun) == true)
                        {
                            obradjen = true;
                            break;
                        }
                    }

                    final String upozorenje = "Upozorenje: Nepoznata vrsta prometa po računu ";
                    if(obradjen == false) Tasks.showMessage(upozorenje + racun.getOznaka());
                }
            }

            SortirajRacune(priprema);

            try
            {
                String prvi = sf.format(priprema.get(0).getIzdan());
                String zadnji = sf.format(priprema.get(priprema.size() - 1).getIzdan());
                String opis = MessageFormat.format("Usluge po računima od {0} do {1}", prvi, zadnji);

                JasperPrint print = ReportingServices.LoadReport(PregledRacuna.class,
                        "resources/IspisOdabranihRacuna.jasper", PripremiParametre(opis), agregacija);
                ReportingServices.ShowReport(print, opis, "ispis-odabranih-racuna");
            }
            catch (Exception ex)
            {
                Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionView(ex).setVisible(true);
            }
        }
    }

    private Calendar getPocetakRaspona()
    {
        try
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sf.parse(datumPregleda.getText()));
            return cal;
        } catch (ParseException ex)
        {
            Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            return Calendar.getInstance();
        }
    }

    private Calendar getKrajRaspona()
    {
        try
        {
            Calendar cal = Calendar.getInstance();
            String raspon = datumPregleda.getText();

            if(raspon.contains("-"))
            {
                String[] dijelovi = raspon.split("-");
                raspon = dijelovi[1];
            }

            cal.setTime(sf.parse(raspon));
            return cal;
        } catch (ParseException ex)
        {
            Logger.getLogger(PregledRacuna.class.getName()).log(Level.SEVERE, null, ex);
            return Calendar.getInstance();
        }
    }

    @Action
    public void PrethodniDan()
    {
        Calendar cal = getPocetakRaspona();
        cal.add(Calendar.DATE, -1);

        datumPregleda.setText(sf.format(cal.getTime()));
    }

    @Action
    public void TrenutniDan()
    {
        datumPregleda.setText(sf.format(Calendar.getInstance().getTime()));
    }

    @Action
    public void SljedeciDan()
    {
        Calendar cal = getKrajRaspona();
        cal.add(Calendar.DATE, 1);

        datumPregleda.setText(sf.format(cal.getTime()));
    }

    private void NormalizirajVrijemeKalendara(Calendar cal)
    {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private void PrikaziRaspon(Date pocetak, Date kraj)
    {
        datumPregleda.setText(sf.format(pocetak) + "-" + sf.format(kraj));
    }

    @Action
    public void PrethodniMjesec()
    {
        Calendar cal = getPocetakRaspona();
        NormalizirajVrijemeKalendara(cal);

        cal.set(Calendar.DATE, 1);
        Date kraj = cal.getTime();

        cal.add(Calendar.MONTH, -1);
        Date pocetak = cal.getTime();

        PrikaziRaspon(pocetak, kraj);
    }

    @Action
    public void TrenutniMjesec()
    {
        Calendar cal = Calendar.getInstance();
        NormalizirajVrijemeKalendara(cal);

        cal.set(Calendar.DATE, 1);
        Date pocetak = cal.getTime();

        cal.add(Calendar.MONTH, 1);
        Date kraj = cal.getTime();

        PrikaziRaspon(pocetak, kraj);
    }

    @Action
    public void SljedeciMjesec()
    {
        Calendar cal = getKrajRaspona();
        NormalizirajVrijemeKalendara(cal);

        cal.set(Calendar.DATE, 1);
        Date pocetak = cal.getTime();

        cal.add(Calendar.MONTH, 1);
        Date kraj = cal.getTime();

        PrikaziRaspon(pocetak, kraj);
    }

    @Action
    public void PrikaziIzbornik()
    {
        izborDatuma.show(datumPregleda, 0, -izborDatuma.getWidth() - datumPregleda.getHeight());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu adminOptions;
    private javax.swing.JButton cmdOsvjezi;
    private javax.swing.JButton cmdStorniraj;
    private javax.swing.JButton cmdZatvori;
    private javax.swing.JTextField datumPregleda;
    private javax.swing.JTable grid;
    private javax.swing.JPopupMenu izborDatuma;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem menuPrethodniDan;
    private javax.swing.JMenuItem mnuDanasnjiDan;
    private javax.swing.JMenuItem mnuIspis;
    private javax.swing.JMenuItem mnuIspisPrometa;
    private javax.swing.JMenuItem mnuIspisRada;
    private javax.swing.JMenuItem mnuIzbrisi;
    private javax.swing.JMenuItem mnuIzmjeni;
    private javax.swing.JMenuItem mnuPrethodniMjesec;
    private javax.swing.JMenuItem mnuSljedeciDan;
    private javax.swing.JMenuItem mnuSljedeciMjesec;
    private javax.swing.JMenuItem mnuTrenutniMjesec;
    private javax.persistence.EntityManager proManager;
    private java.util.List<Acosoft.Processing.DataBox.Racun> racunList;
    private javax.persistence.Query racunQuery;
    private javax.swing.JTextField razlog;
    private java.util.List<Acosoft.Processing.DataBox.Roba> robaList;
    private javax.persistence.Query robaQuery;
    private javax.swing.JSeparator separateProvjere;
    private javax.swing.JTable stavke;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    
}
