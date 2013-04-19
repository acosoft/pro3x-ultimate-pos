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

import Acosoft.Processing.Components.ExceptionView;
import Acosoft.Processing.Components.FrameState;
import Acosoft.Processing.Components.InfoSekvenca;
import Acosoft.Processing.Components.SekvencaRacuna;
import Acosoft.Processing.Components.SingleInstanceLock;
import Acosoft.Processing.Components.Tasks;
import Pro3x.Barcode.AutoSifra;
import Pro3x.Code.Helpdesk;
import Pro3x.Configuration.BazaPodataka;
import Pro3x.Licences.LicenceInfo;
import Pro3x.View.DatabaseConfiguration;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicToolBarUI;
import org.jdesktop.application.Application;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SessionStorage.Property;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;

public class Pro3App extends SingleFrameApplication {

    @Override protected void startup() 
    {
        Pro3View view = new Pro3View(this);
        show(view);
    }
    
    protected static boolean expiredLicence = true;

    public static boolean isExpiredLicence()
    {
        return expiredLicence;
    }
    
    protected static boolean licenced = false;

    public static boolean isLicenced()
    {
        return licenced;
    }
    
    protected static LicenceInfo licenceInfo;
    
    public static LicenceInfo getLicence()
    {
        return licenceInfo;
    }
    
    public static String getLicencePath()
    {
        Pro3App app = Pro3App.getApplication();

        File licence = new File(app.getContext().getLocalStorage().getDirectory(), registracijskiKljuc);
        return licence.getAbsolutePath();
    }
    
    public static void ProvjeriPotpisLicence()
    {
        Pro3App app = Pro3App.getApplication();

        String licencePath = getLicencePath();
        boolean regFileFound = Pro3x.Licences.Validation.verifySignature(licencePath, "Pro3x Ultimate POS");

        if(regFileFound == true)
        {
            licenceInfo = Pro3x.Licences.Validation.readLicence(licencePath);
            expiredLicence = licenceInfo.verifyDate(Calendar.getInstance().getTime()) == false;

            if(expiredLicence == false)
            {
                licenced = true;    
            }
        }
        else
            licenceInfo = null;
    }

    public static Pro3App getApplication() {
        return Application.getInstance(Pro3App.class);
    }
    
    private static SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
    
    public static String DanasnjiDatum()
    {
        Date dx = new Date(System.currentTimeMillis());
        return sf.format(dx);        
    }
    
    public static DateFormat FormatDatuma()
    {
        return sf;
    }

    private static class ShowException implements Runnable
    {
        private Throwable info;
        
        public ShowException(Throwable info)
        {
            this.info = info;
        }
        
        public void run()
        {
            ExceptionView view = new ExceptionView(info);
            view.setLocationRelativeTo(null);
            view.setModal(true);
            view.setVisible(true);
        }
    }

    private InfoSekvenca sekvenca;
    public InfoSekvenca getSekvencaRacuna()
    {
        return sekvenca;
    }

    private InfoSekvenca r1sekvenca;
    public InfoSekvenca getSekvencaR1Racuna()
    {
        return r1sekvenca;
    }

    private InfoSekvenca kalkulacije;
    public InfoSekvenca getSekvencaKalkulacije()
    {
        return kalkulacije;
    }

    private AutoSifra autoSifre;

    public AutoSifra getAutoSifre()
    {
        return autoSifre;
    }

    private static final String sekvencaPutanja = "sekvenca-racuna.xml";
    private static final String sekvencaR1racuna = "sekvenca-r1-racuna.xml";
    private static final String sekvencaKalkulacija = "sekvenca-kalkulacija.xml";
    private static final String autoSifraDatoteka = "auto-sifra.xml";
    private static final String registracijskiKljuc = "registracijski-kljuc.xml";

//    public String getLokacijaRegistracijskogKljuca()
//    {
//        File xf = new File(getContext().getLocalStorage().getDirectory(), registracijskiKljuc);
//        return xf.getAbsolutePath();
//    }

//    public boolean isRegistriran()
//    {
//        return new File(getLokacijaRegistracijskogKljuca()).exists();
//    }
    
    //private LicenceInfo kljuc = null;
    
//    public void ProvjeriRegistracijskiKljuc()
//    {
//        try
//        {
//            boolean potpis = Pro3x.Licences.Validation.verifySignature(getLokacijaRegistracijskogKljuca(), "Pro3x Ultimate POS");
//            kljuc = Pro3x.Licences.Validation.readLicence(getLokacijaRegistracijskogKljuca());
//
//            Calendar danas = Calendar.getInstance();
//            Calendar temp = Calendar.getInstance();
//
//            temp.setTime(kljuc.getValidfrom());
//            boolean pocetak = danas.after(temp);
//
//            temp.setTime(kljuc.getValidto());
//            boolean kraj = danas.before(temp);
//
//            aktivan = potpis && pocetak && kraj;
//        }
//        catch (Exception e)
//        {
//            aktivan = false;
//        }
//    }

    public boolean isAktivanRegistracijskiKljuc()
    {
        return isLicenced() && !isExpiredLicence();
    }

    public LicenceInfo getRegistracijskiKljuc()
    {
        //return kljuc;
        return licenceInfo;
    }

    @Override
    protected void initialize(String[] args) {
        super.initialize(args);

        UcitajSekvencuGotovinskihRacuna();
        //UcitajSekvencuR1Racuna();
        UcitajSekvencuKalkulacija();
        UcitajHelpdeskInfo();
        UcitajAutoSifre();
        
        //ProvjeriRegistracijskiKljuc();
        ProvjeriPotpisLicence();
               
        SessionStorage aps = Pro3App.getApplication().getContext().getSessionStorage();
        aps.putProperty(JInternalFrame.class, new Property() 
        {
            public Object getSessionState(Component c)
            {
                if(c != null)
                {
                    JInternalFrame frame = (JInternalFrame)c;
                    FrameState fs = new FrameState();
                    
                    if(frame.isMaximum())
                    {
                        fs.setBounds(frame.getNormalBounds());
                        fs.setState(FrameState.State.Maximized);
                    }
                    else
                    {
                        fs.setBounds(frame.getBounds());
                        fs.setState(FrameState.State.Normal);
                    }
                    
                    return fs;
                }
                else
                    return null;
            }

            public void setSessionState(Component c, Object state) 
            {
                if(state instanceof FrameState)
                {
                    JInternalFrame frame = (JInternalFrame) c;
                    FrameState fs = (FrameState) state;
                    frame.setBounds(fs.getBounds());

                    try 
                    {
                        if (fs.getState() == FrameState.State.Maximized) 
                            frame.setMaximum(true);
                    } 
                    catch (PropertyVetoException ex) 
                    {
                        Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        aps.putProperty(JToolBar.class, new Property() {

            public Object getSessionState(Component comp)
            {
                if(comp instanceof JToolBar)
                {
                    JToolBar tools = getMainView().getToolBar();

                    ArrayList info = new ArrayList();
                    BorderLayout bord = (BorderLayout) tools.getParent().getLayout();

                    info.add(bord.getConstraints(tools));
                    info.add(new Integer(tools.getOrientation()));
                    info.add(new Boolean(tools.isVisible()));

                    return info;
                }

                return null;
            }

            public void setSessionState(Component comp, Object state)
            {
                if(comp instanceof JToolBar && state != null)
                {
                    try
                    {
                        JToolBar tools = getMainView().getToolBar();
                        BorderLayout bord = (BorderLayout)tools.getParent().getLayout();

                        ArrayList info = (ArrayList)state;
                        bord.removeLayoutComponent(tools);

                        bord.addLayoutComponent(tools, info.get(0));
                        tools.setOrientation((Integer)info.get(1));
                        tools.setVisible((Boolean)info.get(2));
                    }
                    catch(Exception ex)
                    {
                        //zanemari iznimku, toolbar će se prikazati na uobičajenoj lokaciji
                    }
                }
            }
        });
        
        aps.putProperty(JComboBox.class, new Property() {

            public Object getSessionState(Component c) 
            {
                if(c instanceof JComboBox && (!c.getName().isEmpty()))
                {
                    return new Integer(((JComboBox)c).getSelectedIndex());
                }
                return new Integer(0);
            }

            public void setSessionState(Component c, Object state) 
            {
                if(c instanceof JComboBox && state instanceof Integer
                        && !c.getName().isEmpty())
                {
                    JComboBox jc = (JComboBox)c;
                    Integer index = (Integer)state;
                    
                    if(jc.getItemCount() > index.intValue())
                        jc.setSelectedIndex(index.intValue());
                }
            }
        });
        
        aps.putProperty(JCheckBox.class, new Property() {

            public Object getSessionState(Component c) 
            {
                if(!c.getName().isEmpty())
                {
                    JCheckBox check = (JCheckBox)c;
                    return new Boolean(check.isSelected());
                }
                else return new Boolean(false);
            }

            public void setSessionState(Component c, Object state) 
            {
                if(!c.getName().isEmpty())
                {
                    JCheckBox check = (JCheckBox)c;
                    check.setSelected(((Boolean)state).booleanValue());
                }
            }
        });

        ProvjeriPostavkePristupaBazi();
    }

    private final String helpConfigPath = "helpdesk.xml";

    public Helpdesk getHelpdeskInfo()
    {
        return helpdesk;
    }

    private Helpdesk helpdesk;

    private void SpremiHelpdeskInfo()
    {
        try
        {
            LocalStorage store = getContext().getLocalStorage();
            store.save(getHelpdeskInfo(), helpConfigPath);
        }
        catch (IOException ex1)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    private void UcitajHelpdeskInfo()
    {
        LocalStorage store = getContext().getLocalStorage();
        try
        {
            helpdesk = (Helpdesk) store.load(helpConfigPath);
            if(helpdesk == null)
                throw new Exception("Neprihvatljiva konfiguracija email podrške.");
        }
        catch (Exception ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);

            helpdesk = new Helpdesk();

            helpdesk.setFromMail("");
            helpdesk.setToMail("support@pro3x.com");
            helpdesk.setSubject("Pro3x Ultimate POS -  Korisnička podrška");
            helpdesk.setMailServer("");
        }
    }
    
    public void SpremiSekvencuRacuna()
    {
        try {
            LocalStorage store = getContext().getLocalStorage();
            store.save(getSekvencaRacuna(), sekvencaPutanja);
        } catch (IOException ex) {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SpremiSekvencuKalkulacija()
    {
        try {
            LocalStorage store = getContext().getLocalStorage();
            store.save(getSekvencaKalkulacije(), sekvencaKalkulacija);
        } catch (IOException ex) {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void shutdown()
    {
        //spremi sekvencu prije zatvaranja aplikacije
        try
        {
            LocalStorage store = getContext().getLocalStorage();
            
            SpremiSekvencuRacuna();
            SpremiSekvencuKalkulacija();
                      
            store.save(getAutoSifre(), autoSifraDatoteka);

            SpremiHelpdeskInfo();

            //spremi stanje toolbara
            JToolBar tools = getMainView().getToolBar();
            BasicToolBarUI tui = (BasicToolBarUI)tools.getUI();
        
            if(!tui.isFloating())
                getContext().getSessionStorage().save(getMainView().getToolBar(), "toolbar-stanje.xml");

            Pro3x.Persistence.createEntityManagerFactory().close();
            
            if(getMainFrame().getExtendedState() == JFrame.NORMAL)
            {
                store.save(getMainFrame().getSize(), "pro3x-main-frame.xml");
                store.save(getMainFrame().getLocation(), "pro3x-main-frame-location.xml");
                
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try
        {
            lock.unlock();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.shutdown();
    }

    private void PodesiPristupZadanojBazi(LocalStorage store) throws IOException
    {
        BazaPodataka postavke = new BazaPodataka();

        File path = new File(store.getDirectory(), "storage");
        File nautilus = new File(path, "nautilus");
        String connection = "jdbc:derby:" + nautilus.getAbsolutePath() + ";create=true";
        postavke.setConnection(connection);
        postavke.setKorisnik("root");
        postavke.setZaporka("login");
        postavke.setDriver("org.apache.derby.jdbc.EmbeddedDriver");

        store.save(postavke, DatabaseConfiguration.ConfigPath);
    }

    private void ProvjeriPostavkePristupaBazi()
    {
        try
        {
            LocalStorage store = getContext().getLocalStorage();
            BazaPodataka postavke = (BazaPodataka) store.load(DatabaseConfiguration.ConfigPath);

            if(postavke == null)
                PodesiPristupZadanojBazi(store);
            else if(postavke.getConnection().isEmpty() && postavke.getDriver().isEmpty()
                    && postavke.getKorisnik().isEmpty() && postavke.getZaporka().isEmpty())
                PodesiPristupZadanojBazi(store);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
            getContext().getTaskService().execute(new Task(this)
            {
                @Override
                protected Object doInBackground() throws Exception
                {
                    ((Pro3View)getMainView()).Show(new DatabaseConfiguration());
                    JOptionPane.showMessageDialog(getMainFrame(), "Došlo je do iznimke" +
                            " pri podešavanju pristupa bazi podataka.\n" +
                            "Provjerite postavke i ponovno pokrenite aplikaciju.",
                            "Upozorenje", JOptionPane.OK_OPTION);

                    return null;
                }
            });
        }
    }
    
    private void PrilagodiVelicinuProzora(JFrame jf)
    {
        LocalStorage store = getApplication().getContext().getLocalStorage();

        try
        {
            if(store.load("start.xml") == null)
                throw new Exception();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);

            Dimension screen = jf.getToolkit().getScreenSize();
            Dimension frameSize = new Dimension((int)screen.getWidth() - 50,
                    (int)screen.getHeight() - 50);

            jf.setState(JFrame.NORMAL);
            jf.validate();

            jf.setLocation(new Point(0, 0));
            jf.setSize(frameSize);

            jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jf.validate();
        }
        finally
        {
            try { store.save(Calendar.getInstance().getTime(), "start.xml"); }
            catch (IOException ex) { Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex); }
        }
        
//        try {
//             System.out.print(getContext().getLocalStorage().getDirectory().getAbsolutePath());
//            LocalStorage store = getContext().getLocalStorage();
//            Dimension mainSize = (Dimension) store.load("pro3x-main-frame.xml");
//            jf.setState(JFrame.NORMAL);
//            
//            int width = (int) mainSize.getWidth();
//            int height = (int) mainSize.getHeight();
//            
//            jf.setSize(width, height);
//            
//            Point lokacija = (Point) store.load("pro3x-main-frame-location.xml");
//            jf.setLocation(lokacija);
//            jf.validate();
//        } 
//        catch (IOException ex) 
//        {
//            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
//            jf.setSize(640, 480);
//        }
    }
    
    @Override
    protected void ready() 
    {
        JFrame main = getApplication().getMainFrame();
        PrilagodiVelicinuProzora(main);
        
        main.validate();

        //Pro3App app = getApplication();
        //TaskService service = app.getContext().getTaskService();
    }
    
    private static SingleInstanceLock lock;

    //org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel
    public static void main(String[] args) {        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        System.out.println("Default locale: " + Locale.getDefault().toString());
        System.setProperty("sun.awt.noerasebackground", "true");
        
        System.out.println(System.getProperty("user.home"));
        
        try
        {
            lock = new SingleInstanceLock();
        }
        catch (final Exception ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Pro3x Ultimate POS", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            return;
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                e.printStackTrace();
                
                if(SwingUtilities.isEventDispatchThread())
                {
                    ShowException view = new ShowException(e);
                    view.run();
                }
                else
                    SwingUtilities.invokeLater(new ShowException(e));
            }
        });
        
        launch(Pro3App.class, null);
    }

    private void UcitajSekvencuGotovinskihRacuna()
    {
        //učitavanje sekvence računa
        try
        {
            LocalStorage store = getContext().getLocalStorage();
            sekvenca = (InfoSekvenca) store.load(sekvencaPutanja);
            if (sekvenca == null)
            {
                sekvenca = new SekvencaRacuna();
                
                Toolkit.getDefaultToolkit().beep();
                Tasks.showMessage("Pokrenuta nova sekvenca gotovinskih računa.");
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
            sekvenca = new InfoSekvenca() {

                @Override
                public void spremi() {
                    SpremiSekvencuRacuna();
                }
            };
        }
    }

//    private void UcitajSekvencuR1Racuna()
//    {
//        //učitavanje sekvence računa
//        try
//        {
//            LocalStorage store = getContext().getLocalStorage();
//            r1sekvenca = (InfoSekvenca) store.load(sekvencaR1racuna);
//            if (r1sekvenca == null)
//            {
//                r1sekvenca = new InfoSekvenca();
//            }
//        }
//        catch (IOException ex)
//        {
//            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
//            r1sekvenca = new InfoSekvenca();
//        }
//    }

    private void UcitajSekvencuKalkulacija()
    {
        //učitavanje sekvence računa
        try
        {
            LocalStorage store = getContext().getLocalStorage();
            kalkulacije = (InfoSekvenca) store.load(sekvencaKalkulacija);
            if (kalkulacije == null)
            {
                kalkulacije = new InfoSekvenca();
                
                kalkulacije.setAutoSekvenca(true);
                kalkulacije.setResetiranje(InfoSekvenca.ResetiranjeSekvence.Godisnje);

                Tasks.showMessage("Pokrenuta nova sekvenca kalkulacija.");
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
            kalkulacije = new InfoSekvenca();
            
            kalkulacije.setAutoSekvenca(true);
            kalkulacije.setResetiranje(InfoSekvenca.ResetiranjeSekvence.Godisnje);

            Toolkit.getDefaultToolkit().beep();
            Tasks.showMessage("Greška prilikom učitavanj sekvence kalkulacija.");
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex1)
            {
                Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
            Tasks.showMessage("Pokrenuta nova sekvenca kalkulacija.");
        }
    }

    public void UcitajAutoSifre()
    {
        try
        {
            LocalStorage store = getContext().getLocalStorage();
            autoSifre = (AutoSifra) store.load(autoSifraDatoteka);

            if (autoSifre == null)
            {
                autoSifre = new AutoSifra();
                Tasks.showMessage("Pokrenuta nova sekvenca automatskog sifriranja.");
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Pro3App.class.getName()).log(Level.SEVERE, null, ex);
            autoSifre = new AutoSifra();
            Tasks.showMessage("Pokrenuta nova sekvenca automatskog sifriranja.");
        }
    }
   
}
