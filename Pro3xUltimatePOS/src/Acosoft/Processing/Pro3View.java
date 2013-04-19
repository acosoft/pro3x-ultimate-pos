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

import Acosoft.Processing.Components.DeskMeni;
import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.NumberCellEditor;
import Acosoft.Processing.Components.PercentCellRenderer;
import Acosoft.Processing.Components.Pro3xReadRSS;
import Acosoft.Processing.Components.Tasks;
import FinaClient.FiskalizacijaPortType;
import FinaClient.FiskalizacijaService;
import Pro3x.Code.TaskExportArtikala;
import Pro3x.Code.TaskImportArtikala;
import Pro3x.Configuration.General;
import Pro3x.Fiscal.IzborCertifikata;
import Pro3x.Fiscal.IzmjenaOperatera;
import Pro3x.Fiscal.IzmjenaPoslovnogProstora;
import Pro3x.Fiscal.PopisOperatera;
import Pro3x.Kalkulacije.PregledSvihKalkulacija;
import Pro3x.Kalkulacije.PregledKalkulacije;
import Pro3x.KnjigaPopisa.PregledKnjigePopisa;
import Pro3x.KnjigaPopisa.TaskPripremaKnjigePopisa;
import Pro3x.KnjigaPopisa.ZapisKnjigePopisa;
import Pro3x.Persistence;
import Pro3x.View.DnevniPromet;
import Pro3x.View.Models.SimpleModelStanjaSkladista;
import Pro3x.View.NapredniPregledRacuna;
import Pro3x.View.NapredniPregledStanjaSkladista;
import Pro3x.View.PredlozakBiljeskeRacuna;
import Pro3x.View.PregledEntiteta;
import Pro3x.View.PregledNeplacenihRacuna;
import Pro3x.View.ShowDatabaseConfigTask;
import Pro3x.View.SimpleInvoice;
import Pro3x.View.SimpleModelArtikala;
import Pro3x.View.SimpleModelDobavljaca;
import Pro3x.View.SimpleModelGrupa;
import Pro3x.View.SimpleModelPoreznihStopa;
import Pro3x.View.SimplePregledArtikala;
import Pro3x.View.SimplePregledBlagajne;
import Pro3x.Zapisnici.PregledZapisnikaPromjeneCijena;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.xml.ws.BindingProvider;
import org.apache.poi.hssf.record.formula.functions.Echo;

/**
 * The application's main frame.
 */
public class Pro3View extends FrameView {

    public Pro3View(SingleFrameApplication app) {
        super(app);
        initComponents();
        
        new DeskMeni(desk, mnuProzori);

        statusPanel.setVisible(false);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) 
            {
                statusMessageLabel.setText("");
                statusPanel.setVisible(false);
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);


        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();

                statusPanel.setVisible(true);

                final String izvrsavamZadatke = "Izvršavam pozadinske zadatke";

                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    statusMessageLabel.setText(izvrsavamZadatke);
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    messageTimer.stop();
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                    
                    messageTimer.start();
                    
                    if(statusMessageLabel.getText().equals(izvrsavamZadatke))
                        statusMessageLabel.setText("Pozadinski zadaci su završeni");
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        
        try {
            Image image = ImageIO.read(Pro3View.class.getResource("resources/pro3x_small.png"));
            getFrame().setIconImage(image);
        } catch (Exception ex) {
            Logger.getLogger(Pro3View.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean sequence = General.isEditSequence() == true;

        mnuSekvencaKalkulacija.setVisible(sequence);
        mnuSekvenca.setVisible(sequence);
        separateSekvence.setVisible(sequence);
        
        separateStanje.setVisible(General.isKoristiMigracijuArtikala() && General.isSkraceniKodovi());

        mnuStanjeSkladista.setVisible(General.isStanjeSkladista() == true);

        mnuExportArtikala.setVisible(General.isKoristiMigracijuArtikala());
        mnuImportArtikala.setVisible(General.isKoristiMigracijuArtikala());
        separateKalkulacije.setVisible(General.isKoristiMigracijuArtikala());

        mnuZapisnici.setVisible(General.isKoristiZapisnikePromjeneCijena());
        mnuKnjigaPopisa.setVisible(General.isKoristiKnjigeuPopisa());
        
        Tasks.showMessage("Aplikacija je spremna za rad");
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = Pro3App.getApplication().getMainFrame();
            aboutBox = new Pro3AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        Pro3App.getApplication().show(aboutBox);
    }

    public void UskladiToolsMeni()
    {
        toolsMeni.setSelected(tools.isVisible());
    }
    
    public void Show(JInternalFrame frame)
    {
        desk.add(frame);
        frame.setVisible(true);
    }
    
    public void Show(JInternalFrame frame, boolean maximize)
    {
        try {
            frame.setVisible(true);
            desk.add(frame);
            frame.requestFocusInWindow();
            frame.setMaximum(maximize);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Pro3View.class.getName()).log(Level.SEVERE, null, ex);
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
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        tools = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator17 = new javax.swing.JToolBar.Separator();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        toolNovaKalkulacija = new javax.swing.JButton();
        toolPopisKalkulacija = new javax.swing.JButton();
        toolDobavljaci = new javax.swing.JButton();
        separateToolDobavljaci = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        desk = new javax.swing.JDesktopPane();
        logo = new javax.swing.JLabel();
        logo.setVisible(false);
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem21 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        mnuConfigureDatabase = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        toolsMeni = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();
        mnuSkladiste = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        mnuGrupeArtikala = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        jMenuItem22 = new javax.swing.JMenuItem();
        separateKalkulacije = new javax.swing.JSeparator();
        mnuExportArtikala = new javax.swing.JMenuItem();
        mnuImportArtikala = new javax.swing.JMenuItem();
        separateStanje = new javax.swing.JSeparator();
        mnuSekvencaKalkulacija = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuNovaKalkulacija = new javax.swing.JMenuItem();
        mnuStanjeSkladista = new javax.swing.JMenuItem();
        mnuPopisSvihKalkulacija = new javax.swing.JMenuItem();
        separatePopisDobavljaca = new javax.swing.JSeparator();
        mnuPopisDobavljaca = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        separateSekvence = new javax.swing.JSeparator();
        mnuSekvenca = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuGrupniPregled = new javax.swing.JMenuItem();
        mnuRobniPregled = new javax.swing.JMenuItem();
        mnuKnjigaPopisa = new javax.swing.JMenuItem();
        mnuZapisnici = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnuStanjeBlagajne = new javax.swing.JMenuItem();
        mnuProzori = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setMinimumSize(new java.awt.Dimension(640, 400));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
        mainPanel.setLayout(new java.awt.BorderLayout());

        tools.setRollover(true);
        tools.setName("Kratice"); // NOI18N
        tools.setVisible(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(Pro3View.class, this);
        jButton1.setAction(actionMap.get("AkcijaNoviRacun")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(Pro3View.class);
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHideActionText(true);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton1);

        jButton7.setAction(actionMap.get("AkcijaUnosNovogTouchRacuna")); // NOI18N
        jButton7.setIcon(resourceMap.getIcon("jButton7.icon")); // NOI18N
        jButton7.setToolTipText(resourceMap.getString("jButton7.toolTipText")); // NOI18N
        jButton7.setFocusable(false);
        jButton7.setHideActionText(true);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton7.setName("jButton7"); // NOI18N
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton7);

        jButton6.setAction(actionMap.get("AkcijaPregledNeplacenihRacuna")); // NOI18N
        jButton6.setIcon(resourceMap.getIcon("jButton6.icon")); // NOI18N
        jButton6.setToolTipText(resourceMap.getString("jButton6.toolTipText")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHideActionText(true);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton6);

        jButton5.setAction(actionMap.get("AkcijaPregledRacuna")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setToolTipText(resourceMap.getString("jButton5.toolTipText")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHideActionText(true);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton5);

        jSeparator17.setName("jSeparator17"); // NOI18N
        tools.add(jSeparator17);

        jButton10.setAction(actionMap.get("AkcijaNoviArtikal")); // NOI18N
        jButton10.setIcon(resourceMap.getIcon("jButton10.icon")); // NOI18N
        jButton10.setToolTipText(resourceMap.getString("jButton10.toolTipText")); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHideActionText(true);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton10.setName("jButton10"); // NOI18N
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton10);

        jButton9.setAction(actionMap.get("SimplePregledArtikala")); // NOI18N
        jButton9.setIcon(resourceMap.getIcon("jButton9.icon")); // NOI18N
        jButton9.setToolTipText(resourceMap.getString("jButton9.toolTipText")); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton9);

        jSeparator14.setName("jSeparator14"); // NOI18N
        tools.add(jSeparator14);

        toolNovaKalkulacija.setAction(actionMap.get("AkcijaNovaKalkulacija")); // NOI18N
        toolNovaKalkulacija.setIcon(resourceMap.getIcon("toolNovaKalkulacija.icon")); // NOI18N
        toolNovaKalkulacija.setFocusable(false);
        toolNovaKalkulacija.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolNovaKalkulacija.setMargin(new java.awt.Insets(15, 20, 15, 20));
        toolNovaKalkulacija.setName("toolNovaKalkulacija"); // NOI18N
        toolNovaKalkulacija.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(toolNovaKalkulacija);

        toolPopisKalkulacija.setAction(actionMap.get("AkcijaPregledSvihKalkulacija")); // NOI18N
        toolPopisKalkulacija.setIcon(resourceMap.getIcon("toolPopisKalkulacija.icon")); // NOI18N
        toolPopisKalkulacija.setFocusable(false);
        toolPopisKalkulacija.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolPopisKalkulacija.setMargin(new java.awt.Insets(15, 20, 15, 20));
        toolPopisKalkulacija.setName("toolPopisKalkulacija"); // NOI18N
        toolPopisKalkulacija.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(toolPopisKalkulacija);

        toolDobavljaci.setAction(actionMap.get("AkcijaPregledDobavljaca")); // NOI18N
        toolDobavljaci.setIcon(resourceMap.getIcon("toolDobavljaci.icon")); // NOI18N
        toolDobavljaci.setFocusable(false);
        toolDobavljaci.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolDobavljaci.setMargin(new java.awt.Insets(15, 20, 15, 20));
        toolDobavljaci.setName("toolDobavljaci"); // NOI18N
        toolDobavljaci.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(toolDobavljaci);

        separateToolDobavljaci.setName("separateToolDobavljaci"); // NOI18N
        tools.add(separateToolDobavljaci);

        jButton2.setAction(actionMap.get("AkcijaNoviKorisnik")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton2);

        jButton4.setAction(actionMap.get("AkcijaPregledKorisnika")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setMargin(new java.awt.Insets(15, 20, 15, 20));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tools.add(jButton4);

        mainPanel.add(tools, java.awt.BorderLayout.EAST);

        desk.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        desk.setDesktopManager(new Acosoft.Processing.Components.DesktopManager());
        desk.setMinimumSize(new java.awt.Dimension(640, 480));
        desk.setName("desk"); // NOI18N
        desk.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                PodesiLogo(evt);
            }
        });

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(resourceMap.getIcon("logo.icon")); // NOI18N
        logo.setText(resourceMap.getString("logo.text")); // NOI18N
        logo.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        logo.setAlignmentX(0.5F);
        logo.setDoubleBuffered(true);
        logo.setName("logo"); // NOI18N
        logo.setBounds(210, 80, 510, 150);
        desk.add(logo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        mainPanel.add(desk, java.awt.BorderLayout.CENTER);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setMnemonic('R');
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setMargin(new java.awt.Insets(5, 20, 4, 26));
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem21.setIcon(resourceMap.getIcon("jMenuItem21.icon")); // NOI18N
        jMenuItem21.setText(resourceMap.getString("jMenuItem21.text")); // NOI18N
        jMenuItem21.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem21.setName("jMenuItem21"); // NOI18N
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriRssReader(evt);
            }
        });
        fileMenu.add(jMenuItem21);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setIcon(resourceMap.getIcon("aboutMenuItem.icon")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setToolTipText(resourceMap.getString("aboutMenuItem.toolTipText")); // NOI18N
        aboutMenuItem.setIconTextGap(5);
        aboutMenuItem.setMargin(new java.awt.Insets(5, 2, 4, 2));
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        fileMenu.add(aboutMenuItem);

        jSeparator10.setName("jSeparator10"); // NOI18N
        fileMenu.add(jSeparator10);

        mnuConfigureDatabase.setIcon(resourceMap.getIcon("mnuConfigureDatabase.icon")); // NOI18N
        mnuConfigureDatabase.setText(resourceMap.getString("mnuConfigureDatabase.text")); // NOI18N
        mnuConfigureDatabase.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuConfigureDatabase.setName("mnuConfigureDatabase"); // NOI18N
        mnuConfigureDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConfigureDatabaseActionPerformed(evt);
            }
        });
        fileMenu.add(mnuConfigureDatabase);

        jSeparator11.setName("jSeparator11"); // NOI18N
        fileMenu.add(jSeparator11);

        jMenuItem11.setIcon(resourceMap.getIcon("jMenuItem11.icon")); // NOI18N
        jMenuItem11.setText(resourceMap.getString("jMenuItem11.text")); // NOI18N
        jMenuItem11.setIconTextGap(5);
        jMenuItem11.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        //jMenuItem11.setVisible(false);
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem11);

        jMenuItem23.setIcon(resourceMap.getIcon("jMenuItem23.icon")); // NOI18N
        jMenuItem23.setText(resourceMap.getString("jMenuItem23.text")); // NOI18N
        jMenuItem23.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem23.setName("jMenuItem23"); // NOI18N
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriIzmjenuPredlozaka(evt);
            }
        });
        fileMenu.add(jMenuItem23);

        toolsMeni.setAction(actionMap.get("ToogleTools")); // NOI18N
        toolsMeni.setSelected(true);
        toolsMeni.setIcon(resourceMap.getIcon("toolsMeni.icon")); // NOI18N
        toolsMeni.setMargin(new java.awt.Insets(5, 2, 4, 2));
        toolsMeni.setName("toolsMeni"); // NOI18N
        fileMenu.add(toolsMeni);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setIcon(resourceMap.getIcon("exitMenuItem.icon")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setToolTipText(resourceMap.getString("exitMenuItem.toolTipText")); // NOI18N
        exitMenuItem.setIconTextGap(5);
        exitMenuItem.setMargin(new java.awt.Insets(5, 2, 4, 2));
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setMargin(new java.awt.Insets(5, 26, 4, 26));
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem9.setAction(actionMap.get("OtvoriUnosNovogOperatera")); // NOI18N
        jMenuItem9.setIcon(resourceMap.getIcon("jMenuItem9.icon")); // NOI18N
        jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
        jMenuItem9.setIconTextGap(5);
        jMenuItem9.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenu2.add(jMenuItem9);

        jMenuItem8.setAction(actionMap.get("OtvoriPopisOperatera")); // NOI18N
        jMenuItem8.setIcon(resourceMap.getIcon("jMenuItem8.icon")); // NOI18N
        jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
        jMenuItem8.setIconTextGap(5);
        jMenuItem8.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenu2.add(jMenuItem8);

        jSeparator13.setName("jSeparator13"); // NOI18N
        jMenu2.add(jSeparator13);

        jMenuItem10.setAction(actionMap.get("OtvoriIzmjenuLokacije")); // NOI18N
        jMenuItem10.setIcon(resourceMap.getIcon("jMenuItem10.icon")); // NOI18N
        jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
        jMenuItem10.setIconTextGap(5);
        jMenuItem10.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenu2.add(jMenuItem10);

        jMenuItem12.setAction(actionMap.get("UcitajCertifikat")); // NOI18N
        jMenuItem12.setIcon(resourceMap.getIcon("jMenuItem12.icon")); // NOI18N
        jMenuItem12.setText(resourceMap.getString("jMenuItem12.text")); // NOI18N
        jMenuItem12.setIconTextGap(5);
        jMenuItem12.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenu2.add(jMenuItem12);

        jSeparator15.setName("jSeparator15"); // NOI18N
        jMenu2.add(jSeparator15);

        jMenuItem13.setAction(actionMap.get("TestirajFiskalnuVezu")); // NOI18N
        jMenuItem13.setIcon(resourceMap.getIcon("jMenuItem13.icon")); // NOI18N
        jMenuItem13.setText(resourceMap.getString("jMenuItem13.text")); // NOI18N
        jMenuItem13.setIconTextGap(5);
        jMenuItem13.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenu2.add(jMenuItem13);

        menuBar.add(jMenu2);

        jMenu1.setMnemonic('K');
        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setMargin(new java.awt.Insets(5, 26, 4, 26));
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(resourceMap.getIcon("jMenuItem1.icon")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setIconTextGap(5);
        jMenuItem1.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriUnosNovogKorisnika(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jMenu1.add(jSeparator7);

        jMenuItem2.setIcon(resourceMap.getIcon("jMenuItem2.icon")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setIconTextGap(5);
        jMenuItem2.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriPopisKorisnika(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        menuBar.add(jMenu1);

        mnuSkladiste.setMnemonic('S');
        mnuSkladiste.setText(resourceMap.getString("mnuSkladiste.text")); // NOI18N
        mnuSkladiste.setMargin(new java.awt.Insets(5, 26, 4, 26));
        mnuSkladiste.setName("mnuSkladiste"); // NOI18N

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setIcon(resourceMap.getIcon("jMenuItem4.icon")); // NOI18N
        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setIconTextGap(5);
        jMenuItem4.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriUnosNoveRobe(evt);
            }
        });
        mnuSkladiste.add(jMenuItem4);

        jMenuItem5.setAction(actionMap.get("SimplePregledArtikala")); // NOI18N
        jMenuItem5.setIcon(resourceMap.getIcon("jMenuItem5.icon")); // NOI18N
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setIconTextGap(5);
        jMenuItem5.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        mnuSkladiste.add(jMenuItem5);

        mnuGrupeArtikala.setIcon(resourceMap.getIcon("mnuGrupeArtikala.icon")); // NOI18N
        mnuGrupeArtikala.setText(resourceMap.getString("mnuGrupeArtikala.text")); // NOI18N
        mnuGrupeArtikala.setIconTextGap(5);
        mnuGrupeArtikala.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuGrupeArtikala.setName("mnuGrupeArtikala"); // NOI18N
        mnuGrupeArtikala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriPopisAparata(evt);
            }
        });
        mnuSkladiste.add(mnuGrupeArtikala);

        jSeparator12.setName("jSeparator12"); // NOI18N
        mnuSkladiste.add(jSeparator12);

        jMenuItem22.setIcon(resourceMap.getIcon("jMenuItem22.icon")); // NOI18N
        jMenuItem22.setText(resourceMap.getString("jMenuItem22.text")); // NOI18N
        jMenuItem22.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        mnuSkladiste.add(jMenuItem22);

        separateKalkulacije.setName("separateKalkulacije"); // NOI18N
        mnuSkladiste.add(separateKalkulacije);

        mnuExportArtikala.setAction(actionMap.get("ExportArtikala")); // NOI18N
        mnuExportArtikala.setIcon(resourceMap.getIcon("mnuExportArtikala.icon")); // NOI18N
        mnuExportArtikala.setText(resourceMap.getString("mnuExportArtikala.text")); // NOI18N
        mnuExportArtikala.setIconTextGap(5);
        mnuExportArtikala.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuExportArtikala.setName("mnuExportArtikala"); // NOI18N
        mnuSkladiste.add(mnuExportArtikala);

        mnuImportArtikala.setAction(actionMap.get("ImportArtikala")); // NOI18N
        mnuImportArtikala.setIcon(resourceMap.getIcon("mnuImportArtikala.icon")); // NOI18N
        mnuImportArtikala.setText(resourceMap.getString("mnuImportArtikala.text")); // NOI18N
        mnuImportArtikala.setIconTextGap(5);
        mnuImportArtikala.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuImportArtikala.setName("mnuImportArtikala"); // NOI18N
        mnuSkladiste.add(mnuImportArtikala);

        separateStanje.setName("separateStanje"); // NOI18N
        mnuSkladiste.add(separateStanje);

        mnuSekvencaKalkulacija.setAction(actionMap.get("IzmjenaSekvenceKalkulacija")); // NOI18N
        mnuSekvencaKalkulacija.setIcon(resourceMap.getIcon("mnuSekvencaKalkulacija.icon")); // NOI18N
        mnuSekvencaKalkulacija.setText(resourceMap.getString("mnuSekvencaKalkulacija.text")); // NOI18N
        mnuSekvencaKalkulacija.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuSekvencaKalkulacija.setName("mnuSekvencaKalkulacija"); // NOI18N
        mnuSkladiste.add(mnuSekvencaKalkulacija);

        jSeparator3.setName("jSeparator3"); // NOI18N
        mnuSkladiste.add(jSeparator3);

        mnuNovaKalkulacija.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuNovaKalkulacija.setIcon(resourceMap.getIcon("mnuNovaKalkulacija.icon")); // NOI18N
        mnuNovaKalkulacija.setText(resourceMap.getString("mnuNovaKalkulacija.text")); // NOI18N
        mnuNovaKalkulacija.setIconTextGap(5);
        mnuNovaKalkulacija.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuNovaKalkulacija.setName("mnuNovaKalkulacija"); // NOI18N
        mnuNovaKalkulacija.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNovaKalkulacijaActionPerformed(evt);
            }
        });
        mnuSkladiste.add(mnuNovaKalkulacija);

        mnuStanjeSkladista.setAction(actionMap.get("OtvoriStanjeSkladista")); // NOI18N
        mnuStanjeSkladista.setIcon(resourceMap.getIcon("mnuStanjeSkladista.icon")); // NOI18N
        mnuStanjeSkladista.setText(resourceMap.getString("mnuStanjeSkladista.text")); // NOI18N
        mnuStanjeSkladista.setIconTextGap(5);
        mnuStanjeSkladista.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuStanjeSkladista.setName("mnuStanjeSkladista"); // NOI18N
        mnuSkladiste.add(mnuStanjeSkladista);

        mnuPopisSvihKalkulacija.setIcon(resourceMap.getIcon("mnuPopisSvihKalkulacija.icon")); // NOI18N
        mnuPopisSvihKalkulacija.setText(resourceMap.getString("mnuPopisSvihKalkulacija.text")); // NOI18N
        mnuPopisSvihKalkulacija.setIconTextGap(5);
        mnuPopisSvihKalkulacija.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuPopisSvihKalkulacija.setName("mnuPopisSvihKalkulacija"); // NOI18N
        mnuPopisSvihKalkulacija.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriPregledDostavnica(evt);
            }
        });
        mnuSkladiste.add(mnuPopisSvihKalkulacija);

        separatePopisDobavljaca.setName("separatePopisDobavljaca"); // NOI18N
        mnuSkladiste.add(separatePopisDobavljaca);

        mnuPopisDobavljaca.setIcon(resourceMap.getIcon("mnuPopisDobavljaca.icon")); // NOI18N
        mnuPopisDobavljaca.setText(resourceMap.getString("mnuPopisDobavljaca.text")); // NOI18N
        mnuPopisDobavljaca.setIconTextGap(5);
        mnuPopisDobavljaca.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuPopisDobavljaca.setName("mnuPopisDobavljaca"); // NOI18N
        mnuPopisDobavljaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPopisDobavljacaActionPerformed(evt);
            }
        });
        mnuSkladiste.add(mnuPopisDobavljaca);

        menuBar.add(mnuSkladiste);

        helpMenu.setAction(actionMap.get("KnjigaPopisa")); // NOI18N
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setMargin(new java.awt.Insets(5, 26, 4, 26));
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setIcon(resourceMap.getIcon("jMenuItem3.icon")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setIconTextGap(5);
        jMenuItem3.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriUnosRacuna(evt);
            }
        });
        helpMenu.add(jMenuItem3);

        jMenuItem26.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem26.setIcon(resourceMap.getIcon("jMenuItem26.icon")); // NOI18N
        jMenuItem26.setText(resourceMap.getString("jMenuItem26.text")); // NOI18N
        jMenuItem26.setIconTextGap(5);
        jMenuItem26.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem26.setName("jMenuItem26"); // NOI18N
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriNoviTouchScreenRacun(evt);
            }
        });
        helpMenu.add(jMenuItem26);

        jSeparator4.setName("jSeparator4"); // NOI18N
        helpMenu.add(jSeparator4);

        jMenuItem6.setIcon(resourceMap.getIcon("jMenuItem6.icon")); // NOI18N
        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setIconTextGap(5);
        jMenuItem6.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriPopisNeplacenihRacuna(evt);
            }
        });
        helpMenu.add(jMenuItem6);

        jMenuItem7.setIcon(resourceMap.getIcon("jMenuItem7.icon")); // NOI18N
        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setIconTextGap(5);
        jMenuItem7.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtvoriStorniranjeRacuna(evt);
            }
        });
        helpMenu.add(jMenuItem7);

        separateSekvence.setName("separateSekvence"); // NOI18N
        helpMenu.add(separateSekvence);

        mnuSekvenca.setAction(actionMap.get("OtvoriDefinicijuSekvence")); // NOI18N
        mnuSekvenca.setIcon(resourceMap.getIcon("mnuSekvenca.icon")); // NOI18N
        mnuSekvenca.setText(resourceMap.getString("mnuSekvenca.text")); // NOI18N
        mnuSekvenca.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuSekvenca.setName("mnuSekvenca"); // NOI18N
        helpMenu.add(mnuSekvenca);

        jSeparator1.setName("jSeparator1"); // NOI18N
        helpMenu.add(jSeparator1);

        mnuGrupniPregled.setAction(actionMap.get("PregledGrupniDnevniPromet")); // NOI18N
        mnuGrupniPregled.setIcon(resourceMap.getIcon("mnuGrupniPregled.icon")); // NOI18N
        mnuGrupniPregled.setText(resourceMap.getString("mnuGrupniPregled.text")); // NOI18N
        mnuGrupniPregled.setIconTextGap(5);
        mnuGrupniPregled.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuGrupniPregled.setName("mnuGrupniPregled"); // NOI18N
        helpMenu.add(mnuGrupniPregled);

        mnuRobniPregled.setAction(actionMap.get("PregledDnevnogPrometa")); // NOI18N
        mnuRobniPregled.setIcon(resourceMap.getIcon("mnuRobniPregled.icon")); // NOI18N
        mnuRobniPregled.setText(resourceMap.getString("mnuRobniPregled.text")); // NOI18N
        mnuRobniPregled.setIconTextGap(5);
        mnuRobniPregled.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuRobniPregled.setName("mnuRobniPregled"); // NOI18N
        helpMenu.add(mnuRobniPregled);

        mnuKnjigaPopisa.setAction(actionMap.get("KnjigaPopisa")); // NOI18N
        mnuKnjigaPopisa.setIcon(resourceMap.getIcon("mnuKnjigaPopisa.icon")); // NOI18N
        mnuKnjigaPopisa.setText(resourceMap.getString("mnuKnjigaPopisa.text")); // NOI18N
        mnuKnjigaPopisa.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuKnjigaPopisa.setName("mnuKnjigaPopisa"); // NOI18N
        helpMenu.add(mnuKnjigaPopisa);

        mnuZapisnici.setAction(actionMap.get("OtvoriPregledZapisnikaPromjeneCijena")); // NOI18N
        mnuZapisnici.setIcon(resourceMap.getIcon("mnuZapisnici.icon")); // NOI18N
        mnuZapisnici.setText(resourceMap.getString("mnuZapisnici.text")); // NOI18N
        mnuZapisnici.setIconTextGap(5);
        mnuZapisnici.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuZapisnici.setName("mnuZapisnici"); // NOI18N
        helpMenu.add(mnuZapisnici);

        jSeparator5.setName("jSeparator5"); // NOI18N
        helpMenu.add(jSeparator5);

        mnuStanjeBlagajne.setAction(actionMap.get("AkcijaSimplePregledBlagajne")); // NOI18N
        mnuStanjeBlagajne.setIcon(resourceMap.getIcon("mnuStanjeBlagajne.icon")); // NOI18N
        mnuStanjeBlagajne.setText(resourceMap.getString("mnuStanjeBlagajne.text")); // NOI18N
        mnuStanjeBlagajne.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuStanjeBlagajne.setName("mnuStanjeBlagajne"); // NOI18N
        helpMenu.add(mnuStanjeBlagajne);

        menuBar.add(helpMenu);

        mnuProzori.setText(resourceMap.getString("mnuProzori.text")); // NOI18N
        mnuProzori.setMargin(new java.awt.Insets(5, 26, 4, 26));
        mnuProzori.setName("mnuProzori"); // NOI18N

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem16.setIcon(resourceMap.getIcon("jMenuItem16.icon")); // NOI18N
        jMenuItem16.setText(resourceMap.getString("jMenuItem16.text")); // NOI18N
        jMenuItem16.setIconTextGap(5);
        jMenuItem16.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        mnuProzori.add(jMenuItem16);

        jMenuItem15.setIcon(resourceMap.getIcon("jMenuItem15.icon")); // NOI18N
        jMenuItem15.setText(resourceMap.getString("jMenuItem15.text")); // NOI18N
        jMenuItem15.setIconTextGap(5);
        jMenuItem15.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        mnuProzori.add(jMenuItem15);

        jSeparator9.setName("jSeparator9"); // NOI18N
        mnuProzori.add(jSeparator9);

        jMenuItem17.setIcon(resourceMap.getIcon("jMenuItem17.icon")); // NOI18N
        jMenuItem17.setText(resourceMap.getString("jMenuItem17.text")); // NOI18N
        jMenuItem17.setIconTextGap(5);
        jMenuItem17.setMargin(new java.awt.Insets(5, 2, 4, 2));
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        mnuProzori.add(jMenuItem17);

        jSeparator8.setName("jSeparator8"); // NOI18N
        mnuProzori.add(jSeparator8);

        menuBar.add(mnuProzori);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.GridBagLayout());

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        statusPanel.add(statusPanelSeparator, gridBagConstraints);

        statusMessageLabel.setMaximumSize(new java.awt.Dimension(100, 18));
        statusMessageLabel.setMinimumSize(new java.awt.Dimension(100, 18));
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusMessageLabel.setPreferredSize(new java.awt.Dimension(100, 25));
        statusMessageLabel.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        statusPanel.add(statusMessageLabel, gridBagConstraints);

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusAnimationLabel.setIcon(resourceMap.getIcon("statusAnimationLabel.icon")); // NOI18N
        statusAnimationLabel.setMaximumSize(new java.awt.Dimension(25, 25));
        statusAnimationLabel.setMinimumSize(new java.awt.Dimension(25, 25));
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusAnimationLabel.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        statusPanel.add(statusAnimationLabel, gridBagConstraints);

        progressBar.setMaximumSize(new java.awt.Dimension(250, 25));
        progressBar.setMinimumSize(new java.awt.Dimension(250, 25));
        progressBar.setName("progressBar"); // NOI18N
        progressBar.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        statusPanel.add(progressBar, gridBagConstraints);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(tools);
    }// </editor-fold>//GEN-END:initComponents

    private void OtvoriUnosNovogKorisnika(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriUnosNovogKorisnika
    {//GEN-HEADEREND:event_OtvoriUnosNovogKorisnika
        NoviKorisnik x = new NoviKorisnik();
        desk.add(x);
        x.show();
    }//GEN-LAST:event_OtvoriUnosNovogKorisnika
    
    private void OtvoriPopisKorisnika(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriPopisKorisnika
    {//GEN-HEADEREND:event_OtvoriPopisKorisnika
        PopisKorisnika x = new PopisKorisnika();
        desk.add(x);
        x.show();
    }//GEN-LAST:event_OtvoriPopisKorisnika

    private void OtvoriUnosNoveRobe(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriUnosNoveRobe
    {//GEN-HEADEREND:event_OtvoriUnosNoveRobe
        ProzorRoba x = new ProzorRoba();
        desk.add(x);
        x.show();
    }//GEN-LAST:event_OtvoriUnosNoveRobe

    private void OtvoriUnosRacuna(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriUnosRacuna
    {//GEN-HEADEREND:event_OtvoriUnosRacuna
        
        NoviTouchRacun(true);
        
//        if(Pro3App.getApplication().isAktivanRegistracijskiKljuc())
//        {
//            Touche x = new Touche(true);
//            desk.add(x);
//            x.show();
//        }
//        else
//        {
//            JOptionPane.showMessageDialog(null, "Registracijski ključ je istekao! \nKreiranje novih računa je onemogućeno.",
//                            "Pro3x Ultimate POS", JOptionPane.INFORMATION_MESSAGE);
//        }
        
//        NoviRacun x = new NoviRacun();
//        ToucheAdmin x = new ToucheAdmin();
        
    }//GEN-LAST:event_OtvoriUnosRacuna

    private void OtvoriPopisNeplacenihRacuna(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OtvoriPopisNeplacenihRacuna
        //NeplaceniRacuni x = new NeplaceniRacuni();
        PregledNeplacenihRacuna x = new PregledNeplacenihRacuna();
        desk.add(x);
        x.show();
    }//GEN-LAST:event_OtvoriPopisNeplacenihRacuna

    private void OtvoriStorniranjeRacuna(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OtvoriStorniranjeRacuna
        Show(new NapredniPregledRacuna());
    }//GEN-LAST:event_OtvoriStorniranjeRacuna

    private void mnuPopisDobavljacaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuPopisDobavljacaActionPerformed
    {//GEN-HEADEREND:event_mnuPopisDobavljacaActionPerformed
        PregledEntiteta xp = new PregledEntiteta(new SimpleModelDobavljaca());
        xp.setTitle("Popis dobavljača");
        xp.setTableTitle("Uređivanje popisa dobavljača");
        Show(xp);
}//GEN-LAST:event_mnuPopisDobavljacaActionPerformed

    private void mnuNovaKalkulacijaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuNovaKalkulacijaActionPerformed
    {//GEN-HEADEREND:event_mnuNovaKalkulacijaActionPerformed
        Show(new PregledKalkulacije());
}//GEN-LAST:event_mnuNovaKalkulacijaActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        Pro3Postavke x = new Pro3Postavke();
        desk.add(x);
        x.show();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void OtvoriPregledDostavnica(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OtvoriPregledDostavnica
        
        Show(new PregledSvihKalkulacija());
    }//GEN-LAST:event_OtvoriPregledDostavnica

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        for(JInternalFrame jf : desk.getAllFrames())
            jf.dispose();            
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        JInternalFrame jf = desk.getSelectedFrame();
        if(jf != null) jf.dispose();
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        int lokacija = 0;
        double step = 26D;
        
        for(JInternalFrame jf : desk.getAllFrames())
        {
            try {
                jf.setLocation(lokacija, lokacija);
                jf.setSelected(true);

                lokacija += step;
            } catch (PropertyVetoException ex) {
                Logger.getLogger(Pro3View.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void OtvoriPopisAparata(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OtvoriPopisAparata
        PregledEntiteta x = new PregledEntiteta(new SimpleModelGrupa());
        desk.add(x);
        x.show();
    }//GEN-LAST:event_OtvoriPopisAparata

    private void PodesiLogo(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_PodesiLogo
        logo.setLocation((desk.getWidth() - logo.getWidth()) / 2, 
                (desk.getHeight() - logo.getHeight()) / 2 - 20);
        
        logo.setVisible(true);
    }//GEN-LAST:event_PodesiLogo

    private void OtvoriRssReader(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriRssReader
    {//GEN-HEADEREND:event_OtvoriRssReader
        Pro3App app = (Pro3App) getApplication();
        app.getContext().getTaskService().execute(new Pro3xReadRSS(app));
    }//GEN-LAST:event_OtvoriRssReader

    private void mnuConfigureDatabaseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuConfigureDatabaseActionPerformed
    {//GEN-HEADEREND:event_mnuConfigureDatabaseActionPerformed
        Pro3App app = Pro3App.getApplication();
        app.getContext().getTaskService().execute(new ShowDatabaseConfigTask(app));
    }//GEN-LAST:event_mnuConfigureDatabaseActionPerformed

    public void PregledPoreznihStopa()
    {
        PregledEntiteta x = new PregledEntiteta(new SimpleModelPoreznihStopa());
        
        x.setTableTitle("Uređivanje popisa poreznih stopa");
        x.setTitle("Izmjena poreznih stopa");
        x.setName("simple-izmjena-poreznih-stopa-v1");
        
        x.setRenderer(Double.class, new PercentCellRenderer());
        x.setEditor(Double.class, new NumberCellEditor(false));
        
        Show(x);
    }
    
    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem22ActionPerformed
    {//GEN-HEADEREND:event_jMenuItem22ActionPerformed
        PregledPoreznihStopa();
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void OtvoriIzmjenuPredlozaka(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriIzmjenuPredlozaka
    {//GEN-HEADEREND:event_OtvoriIzmjenuPredlozaka
        Show(new PredlozakBiljeskeRacuna());
    }//GEN-LAST:event_OtvoriIzmjenuPredlozaka

    public void NoviTouchRacun(boolean r1mode)
    {
        if(Pro3App.getApplication().isAktivanRegistracijskiKljuc())
        {
            SimpleInvoice x = new SimpleInvoice(!r1mode);
            desk.add(x);
            x.show();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Kreiranje novih računa je onemogućeno.\nMolimo vas da zatražite novi registracijski ključ.",
                            "Pro3x Ultimate POS", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void OtvoriNoviTouchScreenRacun(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OtvoriNoviTouchScreenRacun
    {//GEN-HEADEREND:event_OtvoriNoviTouchScreenRacun
        NoviTouchRacun(false);
}//GEN-LAST:event_OtvoriNoviTouchScreenRacun

    @Action
    public void OtvoriDefinicijuSekvence()
    {
        Pro3xSequence seq = new Pro3xSequence();
        Show(seq);
    }

    @Action
    public void ToogleTools()
    {
        BasicToolBarUI tui = (BasicToolBarUI)tools.getUI();
        boolean selected;

        if(tui.isFloating())
            selected = tools.getTopLevelAncestor().isVisible();
        else
            selected = tools.isVisible();

        selected = !selected;
        
        toolsMeni.setSelected(selected);
        
        if(tui.isFloating())
            tools.getTopLevelAncestor().setVisible(selected);
        else
            tools.setVisible(selected);
    }

    @Action
    public void AkcijaNoviRacun()
    {
        NoviTouchRacun(true);
        //NoviTouchRacun(false);
        //OtvoriUnosRacuna(null);
    }

    @Action
    public void AkcijaPregledRacuna()
    {
        OtvoriStorniranjeRacuna(null);
    }

    @Action
    public void AkcijaPregledNeplacenihRacuna()
    {
        OtvoriPopisNeplacenihRacuna(null);
    }

    @Action
    public void AkcijaNoviArtikal()
    {
        OtvoriUnosNoveRobe(null);
    }

    @Action
    public void AkcijaNovaKalkulacija()
    {
       mnuNovaKalkulacijaActionPerformed(null);
    }

    @Action
    public void AkcijaPregledSvihKalkulacija()
    {
        OtvoriPregledDostavnica(null);
    }

    @Action
    public void AkcijaPregledDobavljaca()
    {
        mnuPopisDobavljacaActionPerformed(null);
    }

    @Action
    public void AkcijaNoviKorisnik()
    {
        OtvoriUnosNovogKorisnika(null);
    }

    @Action
    public void AkcijaPregledKorisnika()
    {
        OtvoriPopisKorisnika(null);
    }

    @Action
    public void PregledDnevnogPrometa()
    {
        JInternalFrame frame = new DnevniPromet();
        frame.setTitle("Grupni pregled dnevnog prometa");
        frame.setName("grupni-pregled-dnevnog-prometa");
        Show(frame);
    }

    @Action
    public void AkcijaUnosNovogTouchRacuna()
    {
        //Show(new Touche());
        OtvoriNoviTouchScreenRacun(null);
    }

    @Action
    public void OtvoriSekvencuR1Racuna()
    {
        Pro3xSequence seq = new Pro3xSequence(Pro3App.getApplication().getSekvencaR1Racuna());
        seq.setName("izmjena-sekvence-r1-racuna");
        seq.setTitle("Definicija sekvence R1 računa");
        Show(seq);
    }

    @Action
    public void PregledGrupniDnevniPromet()
    {
        JInternalFrame frame = new DnevniPromet("Stavke.DnevniPromet");
        frame.setTitle("Robni pregled dnevnog prometa");
        frame.setName("robni-pregled-dnevnog-prometa");
        Show(frame);
    }

    @Action
    public void ExportArtikala()
    {
        JFileChooser jf = new JFileChooser();
        jf.setMultiSelectionEnabled(false);

        jf.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                if(f.getName().endsWith(".pro3x"))
                    return true;
                else
                    return false;
            }

            @Override
            public String getDescription()
            {
                return "Pro3x Popis Artikala ( *.prox )";
            }
        });

        int result = jf.showSaveDialog(this.getRootPane());
        if(result == JFileChooser.APPROVE_OPTION)
            getApplication().getContext().getTaskService().execute
                    (new TaskExportArtikala(getApplication(), jf.getSelectedFile()));
    }

    @Action
    public void ImportArtikala()
    {
        JFileChooser jf = new JFileChooser();
        jf.setMultiSelectionEnabled(false);

        jf.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                if(f.getName().endsWith(".pro3x"))
                    return true;
                else
                    return false;
            }

            @Override
            public String getDescription()
            {
                return "Pro3x Popis Artikala ( *.prox )";
            }
        });

        int result = jf.showOpenDialog(this.getRootPane());
        if(result == JFileChooser.APPROVE_OPTION)
            getApplication().getContext().getTaskService().execute
                    (new TaskImportArtikala(getApplication(), jf.getSelectedFile()));
    }

    @Action
    public void IzmjenaSekvenceKalkulacija()
    {
        Pro3xSequence sekvenca = new Pro3xSequence(Pro3App.getApplication().getSekvencaKalkulacije());
        
        sekvenca.setName("sekvenca-kalkulacija.xml");
        sekvenca.setTitle("Definicija sekvence kalkulacija");

        Show(sekvenca);
    }

    @Action
    public void OtvoriPregledZapisnikaPromjeneCijena()
    {
        Show(new PregledZapisnikaPromjeneCijena());
    }

    @Action
    public Task KnjigaPopisa()
    {
        return new KnjigaPopisaTask(getApplication());
    }

    private class KnjigaPopisaTask extends TaskPripremaKnjigePopisa
    {
        KnjigaPopisaTask(org.jdesktop.application.Application app)
        {
            super(app);
        }

        @Override protected void succeeded(Object result)
        {
            List<ZapisKnjigePopisa> knjigaPopisa = (List<ZapisKnjigePopisa>) result;
            Show(new PregledKnjigePopisa(knjigaPopisa));
        }
    }

    @Action
    public void OtvoriUnosNovogOperatera() {
        Show(new IzmjenaOperatera(Persistence.createEntityManagerFactory().createEntityManager()));
    }

    @Action
    public void UcitajCertifikat() {
        Show(new IzborCertifikata());
    }

    @Action
    public void OtvoriIzmjenuLokacije() {
        Show(new IzmjenaPoslovnogProstora());
    }

    @Action
    public Task SimplePregledArtikala() {
        return new SimplePregledArtikalaTask(getApplication());
    }

    private class SimplePregledArtikalaTask extends org.jdesktop.application.Task<Object, Void> {
        SimplePregledArtikalaTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override protected Object doInBackground() {
            
            setMessage("Pripremam vezu sa bazom podataka");
            setProgress(10);
            
            EntityManager manager = Pro3x.Persistence.createEntityManagerFactory().createEntityManager();
            Query query = manager.createNamedQuery("Roba.findAll");
            query.setHint("eclipselink.refresh", "true");
            
            setMessage("Pripremam popis artikala");
            setProgress(50);
            SimpleModelArtikala model = new SimpleModelArtikala(manager, query);
            
            setMessage("Pripremam prikaz popisa artikala");
            setProgress(90);
            
            return model;
        }
        @Override protected void succeeded(Object result) {
            
            if(result instanceof SimpleModelArtikala)
            {
                setMessage("Prikaz artikala je spreman");
                setProgress(100);
                Show(new SimplePregledArtikala((SimpleModelArtikala)result));
            }
        }

        @Override
        protected void failed(Throwable cause) {
            new ExceptionView(cause).setVisible(true);
        }
    }

    @Action
    public void OtvoriPopisOperatera() {
        PopisOperatera x = new PopisOperatera();
        Show(x);
    }

    @Action
    public Task TestirajFiskalnuVezu() {
        return new TestirajFiskalnuVezuTask(getApplication());
    }

    private class TestirajFiskalnuVezuTask extends org.jdesktop.application.Task<Object, Void> {
        TestirajFiskalnuVezuTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override protected Object doInBackground() {
            
            FiskalizacijaService service = new FiskalizacijaService();
            FiskalizacijaPortType port = service.getFiskalizacijaPortType();
            
            if(General.isFiskalizacijaProdukcija())
            {
                BindingProvider bp = (BindingProvider) port;
                Map<String, Object> context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://cis.porezna-uprava.hr:8449/FiskalizacijaService");
            }
            
            String result = port.echo("mali zeleni");
            
            if(result.equals("mali zeleni"))
            {
                return new Boolean(true);
            }
            
            return null;
        }
        @Override protected void succeeded(Object result) {
            setMessage("Echo test je uspješno završen");
        }

        @Override
        protected void failed(Throwable cause) {
            setMessage("Test nije završio sa očekivanim rezultatom");
        }
    }

    @Action
    public Task OtvoriStanjeSkladista() {
        return new OtvoriStanjeSkladistaTask(getApplication());
    }

    private class OtvoriStanjeSkladistaTask extends org.jdesktop.application.Task<SimpleModelStanjaSkladista, Void> {
        
        private EntityManager manager;
        
        OtvoriStanjeSkladistaTask(org.jdesktop.application.Application app) {
            super(app);
            manager = Persistence.createEntityManagerFactory().createEntityManager();
        }
        @Override protected SimpleModelStanjaSkladista doInBackground() {

            Query query = manager.createQuery("SELECT r, sum(k.kolicinaUlaz), sum(k.kolicinaIzlaz) FROM Roba r, IN(r.kartice) k GROUP BY r");
            List data = query.getResultList();
            
            return new SimpleModelStanjaSkladista(data);
        }
        @Override protected void succeeded(SimpleModelStanjaSkladista result) {
            Show(new  NapredniPregledStanjaSkladista(result));
        }
    }

    @Action
    public void AkcijaSimplePregledBlagajne() {
        Show(new SimplePregledBlagajne(Calendar.getInstance().getTime()));
    }






    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desk;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator17;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuConfigureDatabase;
    private javax.swing.JMenuItem mnuExportArtikala;
    private javax.swing.JMenuItem mnuGrupeArtikala;
    private javax.swing.JMenuItem mnuGrupniPregled;
    private javax.swing.JMenuItem mnuImportArtikala;
    private javax.swing.JMenuItem mnuKnjigaPopisa;
    private javax.swing.JMenuItem mnuNovaKalkulacija;
    private javax.swing.JMenuItem mnuPopisDobavljaca;
    private javax.swing.JMenuItem mnuPopisSvihKalkulacija;
    private javax.swing.JMenu mnuProzori;
    private javax.swing.JMenuItem mnuRobniPregled;
    private javax.swing.JMenuItem mnuSekvenca;
    private javax.swing.JMenuItem mnuSekvencaKalkulacija;
    private javax.swing.JMenu mnuSkladiste;
    private javax.swing.JMenuItem mnuStanjeBlagajne;
    private javax.swing.JMenuItem mnuStanjeSkladista;
    private javax.swing.JMenuItem mnuZapisnici;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSeparator separateKalkulacije;
    private javax.swing.JSeparator separatePopisDobavljaca;
    private javax.swing.JSeparator separateSekvence;
    private javax.swing.JSeparator separateStanje;
    private javax.swing.JToolBar.Separator separateToolDobavljaci;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton toolDobavljaci;
    private javax.swing.JButton toolNovaKalkulacija;
    private javax.swing.JButton toolPopisKalkulacija;
    private javax.swing.JToolBar tools;
    private javax.swing.JCheckBoxMenuItem toolsMeni;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
