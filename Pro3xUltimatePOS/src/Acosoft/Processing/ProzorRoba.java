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

import Acosoft.Processing.Components.Tasks;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import Acosoft.Processing.DataBox.*;
import Pro3x.Barcode.AutoSifra;
import Pro3x.BasicView;
import Pro3x.Configuration.General;
import Pro3x.Live.ArtikalEvents;
import Pro3x.View.PromjenaOpisaArtikla;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.jdesktop.application.Action;

/**
 *
 * @author nonstop
 */
public class ProzorRoba extends BasicView {
    private static final String LOKACIJA_TECAJA = "unos-artikla-tecaj.xml";

    private NumberFormat nf;
    private EntityManager manager;
    
    private NumberFormat n2d;
    private boolean noviArtikal = false;
    private double staraCijena = 0D;
    
    public ProzorRoba()
    {
        this(Pro3x.Persistence.createEntityManagerFactory().createEntityManager(), noviArtikal());
        noviArtikal = true;
        
        if(comGrupa.getItemCount() > 0)
            comGrupa.setSelectedIndex(0);
    }
    
    private static Roba noviArtikal()
    {
        Roba roba = new Roba();
        
        roba.setSifra(Pro3App.getApplication().getAutoSifre().sljedecaFormatiranaSifra());
        roba.setNaziv("Novi artikal");
        roba.setMjera("kom");
        
        return roba;
    }

    @Override
    public EntityManager getProManager() {
        return manager;
    }
    
    /** Creates new form ProzorRoba */
    public ProzorRoba(EntityManager manager, Roba roba)
    {
        nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(2);
        //nf.setMaximumFractionDigits(2);
        
        n2d = NumberFormat.getNumberInstance();
        n2d.setMinimumFractionDigits(2);
        n2d.setMaximumFractionDigits(2);
        
        this.manager = manager;
        setModel(roba);

        initComponents();
        
        addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                tabs.setSelectedIndex(0);
            }
        });

        if(!General.isKoristiDeklaracije())
        {
            tabs.removeTabAt(2);
        }
        
        sifra.setText(roba.getSifra());
        robaNaziv.setText(roba.getNaziv());
        robaJedinica.setText(roba.getMjera());
        comGrupa.setSelectedItem(roba.getGrupa());
        
        pdvNabava.setSelectedItem(roba.getPdvNabava());
        nabavnaBezPoreza.setText(nf.format(roba.getNabavnaCijenaBezPoreza()));
        nabavnaUkupno.setText(n2d.format(roba.getNabavnaCijena()));
        
        poreznaStopa.setSelectedItem(roba.getPdv());
        potrosnja.setSelectedItem(roba.getPot());
        procjenaMarze.setText(nf.format(roba.izracunajMarzu() * 100));
        prodajnaBezPoreza.setText(nf.format(roba.getCijenaBezPoreza()));
        prodajnaUkupno.setText(n2d.format(roba.getMaloprodajnaCijenaZaokruzena()));
        
        staraCijena = roba.getMaloprodajnaCijenaZaokruzena();
        
        getRootPane().setDefaultButton(cmdSpremi);
    }
    
    //pripreme za prelazak na mvc
    protected Roba artikal;

    public final Roba getModel()
    {
        return artikal;
    }

    public final void setModel(Roba artikal)
    {
        this.artikal = artikal;
    }
    
    public void setNabavnaBezPoreza(Double cijena)
    {
        nabavnaBezPoreza.setText(nf.format(cijena));
    }
    
    public Double getNabavnaBezPoreza()
    {
        try {
            return nf.parse(nabavnaBezPoreza.getText()).doubleValue();
        } catch (ParseException ex) {
            setNabavnaBezPoreza(0D);
            return 0D;
        }
    }

    public void setNabavna(Double cijena)
    {
        nabavnaUkupno.setText(n2d.format(cijena));
    }
    
    public Double getNabavna()
    {
        try {
            return n2d.parse(nabavnaUkupno.getText()).doubleValue();
        } catch (ParseException ex) {
            setNabavna(0D);
            return 0D;
        }
    }
    
    public void setProdajnaBezPoreza(Double cijena)
    {
        prodajnaBezPoreza.setText(nf.format(cijena));
    }
    
    public Double getProdajnaBezPoreza()
    {
        try {
            return nf.parse(prodajnaBezPoreza.getText()).doubleValue();
        } catch (ParseException ex) {
            setProdajnaBezPoreza(0D);
            return 0D;
        }
    }
    
    public void setProdajna(double cijena)
    {
        prodajnaUkupno.setText(n2d.format(cijena));
    }
    
    public Double getProdajna()
    {
        try {
            return nf.parse(prodajnaUkupno.getText()).doubleValue();
        } catch (ParseException ex) {
            setProdajna(0);
            return 0D;
        }
    }
    
    public PoreznaStopa getPdvNabava()
    {
        return (PoreznaStopa) pdvNabava.getSelectedItem();
    }
    
    public PoreznaStopa getPdv()
    {
        return (PoreznaStopa) poreznaStopa.getSelectedItem();
    }

    public PoreznaStopa getPot()
    {
        return (PoreznaStopa) potrosnja.getSelectedItem();
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(ProzorRoba.class);
        queryStope = java.beans.Beans.isDesignTime() ? null : getProManager().createQuery(resourceMap.getString("queryStope.query")); // NOI18N
        listStope = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : queryStope.getResultList();
        queryGrupe = java.beans.Beans.isDesignTime() ? null : getProManager().createQuery(resourceMap.getString("queryGrupe.query")); // NOI18N
        popSekvencaSifre = new javax.swing.JPopupMenu();
        mnuSljedecaSifra = new javax.swing.JMenuItem();
        mnuPromjenaSifre = new javax.swing.JMenuItem();
        popTecaj = new javax.swing.JPopupMenu();
        mnuSpremiTecaj = new javax.swing.JMenuItem();
        listaGrupaArtikala = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : queryGrupe.getResultList();
        tabs = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        robaJedinica = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        sifra = new javax.swing.JTextField();
        robaNaziv = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        comGrupa = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        prodajnaBezPoreza = new javax.swing.JTextField();
        procjenaMarze = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        prodajnaUkupno = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        poreznaStopa = new javax.swing.JComboBox();
        potrosnja = new javax.swing.JComboBox();
        potrosnjaOpis = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        nabavnaBezPoreza = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        nabavnaUkupno = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        pdvNabava = new javax.swing.JComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0), new java.awt.Dimension(100, 200), new java.awt.Dimension(32767, 32767));
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        deklaracijaArtikla = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        cmdSpremi = new javax.swing.JButton();
        cmdZatvori = new javax.swing.JButton();

        listStope.add(0, null);

        popSekvencaSifre.setName("popSekvencaSifre"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(ProzorRoba.class, this);
        mnuSljedecaSifra.setAction(actionMap.get("PovuciNovuSifru")); // NOI18N
        mnuSljedecaSifra.setIcon(resourceMap.getIcon("mnuSljedecaSifra.icon")); // NOI18N
        mnuSljedecaSifra.setText(resourceMap.getString("mnuSljedecaSifra.text")); // NOI18N
        mnuSljedecaSifra.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuSljedecaSifra.setName("mnuSljedecaSifra"); // NOI18N
        popSekvencaSifre.add(mnuSljedecaSifra);

        mnuPromjenaSifre.setAction(actionMap.get("PostaviAutoSifre")); // NOI18N
        mnuPromjenaSifre.setIcon(resourceMap.getIcon("mnuPromjenaSifre.icon")); // NOI18N
        mnuPromjenaSifre.setText(resourceMap.getString("mnuPromjenaSifre.text")); // NOI18N
        mnuPromjenaSifre.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuPromjenaSifre.setName("mnuPromjenaSifre"); // NOI18N
        popSekvencaSifre.add(mnuPromjenaSifre);

        popTecaj.setName("popTecaj"); // NOI18N

        mnuSpremiTecaj.setAction(actionMap.get("ZapamtiTecaj")); // NOI18N
        mnuSpremiTecaj.setIcon(resourceMap.getIcon("mnuSpremiTecaj.icon")); // NOI18N
        mnuSpremiTecaj.setText(resourceMap.getString("mnuSpremiTecaj.text")); // NOI18N
        mnuSpremiTecaj.setToolTipText(resourceMap.getString("mnuSpremiTecaj.toolTipText")); // NOI18N
        mnuSpremiTecaj.setMargin(new java.awt.Insets(5, 2, 4, 2));
        mnuSpremiTecaj.setName("mnuSpremiTecaj"); // NOI18N
        popTecaj.add(mnuSpremiTecaj);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(resourceMap.getString("izmjena-artikla-v8.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(550, 600));
        setName("izmjena-artikla-v8"); // NOI18N
        setPreferredSize(new java.awt.Dimension(550, 600));

        tabs.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 0, 5));
        tabs.setName(""); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 10));
        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel9.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel10.add(jLabel9, gridBagConstraints);

        robaJedinica.setMargin(new java.awt.Insets(2, 2, 2, 2));
        robaJedinica.setName("robaJedinica"); // NOI18N
        robaJedinica.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                OdabirMjerneJedinice(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel10.add(robaJedinica, gridBagConstraints);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel1.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel10.add(jLabel1, gridBagConstraints);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel3.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel10.add(jLabel3, gridBagConstraints);

        sifra.setComponentPopupMenu(popSekvencaSifre);
        sifra.setMargin(new java.awt.Insets(2, 2, 2, 2));
        sifra.setName("sifra"); // NOI18N
        sifra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sifraFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel10.add(sifra, gridBagConstraints);

        robaNaziv.setMargin(new java.awt.Insets(2, 2, 2, 2));
        robaNaziv.setName("robaNaziv"); // NOI18N
        robaNaziv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                robaNazivFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel10.add(robaNaziv, gridBagConstraints);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel8.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel10.add(jLabel8, gridBagConstraints);

        comGrupa.setName(""); // NOI18N
        comGrupa.setPreferredSize(new java.awt.Dimension(41, 32));

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE, listaGrupaArtikala, comGrupa);
        bindingGroup.addBinding(jComboBoxBinding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel10.add(comGrupa, gridBagConstraints);

        jPanel1.add(jPanel10, java.awt.BorderLayout.CENTER);

        jPanel6.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        tabs.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel12.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 12, 10));
        jPanel12.setName("jPanel12"); // NOI18N
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel10.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel12.add(jLabel10, gridBagConstraints);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(resourceMap.getString("jLabel2.toolTipText")); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel2.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel12.add(jLabel2, gridBagConstraints);

        prodajnaBezPoreza.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        prodajnaBezPoreza.setText(resourceMap.getString("prodajnaBezPoreza.text")); // NOI18N
        prodajnaBezPoreza.setMargin(new java.awt.Insets(2, 2, 2, 2));
        prodajnaBezPoreza.setName("prodajnaBezPoreza"); // NOI18N
        prodajnaBezPoreza.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                FokusProdajnaCijena(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                IzracunajUkupnuProdajnuCijenu(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel12.add(prodajnaBezPoreza, gridBagConstraints);

        procjenaMarze.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        procjenaMarze.setText(resourceMap.getString("procjenaMarze.text")); // NOI18N
        procjenaMarze.setMargin(new java.awt.Insets(2, 2, 2, 2));
        procjenaMarze.setName("procjenaMarze"); // NOI18N
        procjenaMarze.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                MarzaFocused(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                MarzaLostFocus(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel12.add(procjenaMarze, gridBagConstraints);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel6.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel12.add(jLabel6, gridBagConstraints);

        prodajnaUkupno.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        prodajnaUkupno.setText(resourceMap.getString("prodajnaUkupno.text")); // NOI18N
        prodajnaUkupno.setMargin(new java.awt.Insets(2, 2, 2, 2));
        prodajnaUkupno.setName("prodajnaUkupno"); // NOI18N
        prodajnaUkupno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                prodajnaUkupnoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                prodajnaUkupnoFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel12.add(prodajnaUkupno, gridBagConstraints);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel7.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel12.add(jLabel7, gridBagConstraints);

        poreznaStopa.setMinimumSize(new java.awt.Dimension(41, 30));
        poreznaStopa.setName(""); // NOI18N
        poreznaStopa.setPreferredSize(new java.awt.Dimension(41, 32));

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStope, poreznaStopa);
        bindingGroup.addBinding(jComboBoxBinding);

        poreznaStopa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izracunajNoveNabavneCijene(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel12.add(poreznaStopa, gridBagConstraints);

        potrosnja.setMinimumSize(new java.awt.Dimension(41, 30));
        potrosnja.setName(""); // NOI18N
        potrosnja.setPreferredSize(new java.awt.Dimension(41, 32));

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStope, potrosnja);
        bindingGroup.addBinding(jComboBoxBinding);

        potrosnja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izracunajNoveNabavneCijene(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel12.add(potrosnja, gridBagConstraints);

        potrosnjaOpis.setText(resourceMap.getString("potrosnjaOpis.text")); // NOI18N
        potrosnjaOpis.setMaximumSize(new java.awt.Dimension(200, 18));
        potrosnjaOpis.setMinimumSize(new java.awt.Dimension(200, 18));
        potrosnjaOpis.setName("potrosnjaOpis"); // NOI18N
        potrosnjaOpis.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel12.add(potrosnjaOpis, gridBagConstraints);

        jPanel3.add(jPanel12, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel5.add(jPanel3, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 12, 10));
        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new java.awt.GridBagLayout());

        nabavnaBezPoreza.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        nabavnaBezPoreza.setText(resourceMap.getString("nabavnaBezPoreza.text")); // NOI18N
        nabavnaBezPoreza.setMargin(new java.awt.Insets(2, 2, 2, 2));
        nabavnaBezPoreza.setName("nabavnaBezPoreza"); // NOI18N
        nabavnaBezPoreza.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                FokusNabavnaCijena(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UkupnaNabavnaVrijednost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel11.add(nabavnaBezPoreza, gridBagConstraints);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(resourceMap.getString("jLabel4.toolTipText")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel11.add(jLabel4, gridBagConstraints);

        nabavnaUkupno.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        nabavnaUkupno.setText(resourceMap.getString("nabavnaUkupno.text")); // NOI18N
        nabavnaUkupno.setMargin(new java.awt.Insets(2, 2, 2, 2));
        nabavnaUkupno.setName("nabavnaUkupno"); // NOI18N
        nabavnaUkupno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                FocusUkupnaNabavnaCijena(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nabavnaUkupnoFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel11.add(nabavnaUkupno, gridBagConstraints);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel11.add(jLabel5, gridBagConstraints);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel11.setMinimumSize(new java.awt.Dimension(200, 18));
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(200, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel11.add(jLabel11, gridBagConstraints);

        pdvNabava.setMinimumSize(new java.awt.Dimension(41, 30));
        pdvNabava.setName(""); // NOI18N
        pdvNabava.setPreferredSize(new java.awt.Dimension(41, 32));

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStope, pdvNabava);
        bindingGroup.addBinding(jComboBoxBinding);

        pdvNabava.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdvNabavaIzracunajNoveUkupneCijene(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel11.add(pdvNabava, gridBagConstraints);

        jPanel2.add(jPanel11, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel5.add(jPanel2, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(filler1, gridBagConstraints);

        tabs.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        deklaracijaArtikla.setColumns(20);
        deklaracijaArtikla.setRows(5);
        deklaracijaArtikla.setMargin(new java.awt.Insets(2, 2, 2, 2));
        deklaracijaArtikla.setName("deklaracijaArtikla"); // NOI18N
        jScrollPane1.setViewportView(deklaracijaArtikla);

        jPanel7.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel13.setFont(new java.awt.Font("Ubuntu", 0, 24));
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        jLabel13.setName("jLabel13"); // NOI18N
        jPanel7.add(jLabel13, java.awt.BorderLayout.PAGE_START);

        tabs.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cmdSpremi.setText(resourceMap.getString("cmdSpremi.text")); // NOI18N
        cmdSpremi.setMaximumSize(new java.awt.Dimension(90, 30));
        cmdSpremi.setMinimumSize(new java.awt.Dimension(90, 30));
        cmdSpremi.setName("cmdSpremi"); // NOI18N
        cmdSpremi.setPreferredSize(new java.awt.Dimension(90, 30));
        cmdSpremi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SpremiPromjene(evt);
            }
        });
        jPanel8.add(cmdSpremi);

        cmdZatvori.setText(resourceMap.getString("cmdZatvori.text")); // NOI18N
        cmdZatvori.setMaximumSize(new java.awt.Dimension(90, 30));
        cmdZatvori.setMinimumSize(new java.awt.Dimension(90, 30));
        cmdZatvori.setName("cmdZatvori"); // NOI18N
        cmdZatvori.setPreferredSize(new java.awt.Dimension(90, 30));
        cmdZatvori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Zatvori(evt);
            }
        });
        jPanel8.add(cmdZatvori);

        getContentPane().add(jPanel8, java.awt.BorderLayout.PAGE_END);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void spremiKnjiguPopisa()
    {
        double stanje = getStanje();
        
        if(noviArtikal == false && stanje > 0 && getNabavna() > 0)
        {
            double promjenaCijene = getModel().getMaloprodajnaCijenaZaokruzena() - staraCijena;
            
            if(promjenaCijene != 0)
            {
                KnjigaPopisa knjiga = new KnjigaPopisa();
                knjiga.setDokument(MessageFormat.format("Promjena cijene, {0}, količina {1} {2}", getModel().getNaziv(), stanje, getModel().getMjera()));
                knjiga.setZaduzenje(stanje * promjenaCijene);

                getProManager().persist(knjiga);
                
                Tasks.showMessage("Zapis o promjeni cijene je dodan u knjigu popisa");
            }
        }
    }
    
    private double getStanje()
    {
        Query stanje = getProManager().createNamedQuery("Roba.stanje");
        stanje.setParameter("roba", getModel());
        Object result[] = (Object[]) stanje.getSingleResult();
        
        if(result != null)
        {
            Double ulaz = (Double)result[0];
            Double izlaz = (Double)result[1];

            if(ulaz == null) ulaz = 0D;
            if(izlaz == null) izlaz = 0D;
            
            return ulaz - izlaz;
        }
        else
        {
            return 0;
        }
    }
    
    private void SpremiPromjene(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SpremiPromjene
    {//GEN-HEADEREND:event_SpremiPromjene
        Roba x = getModel();

        x.setSifra(sifra.getText());

        x.setNaziv(robaNaziv.getText());
        x.setUlazniArtikal(false);
        x.setMjera(robaJedinica.getText());
        
        x.setGrupa((GrupaArtikala)comGrupa.getSelectedItem());

        x.setPdv((PoreznaStopa)poreznaStopa.getSelectedItem());
        x.setPot(getPot());
        x.setCijena(getProdajna());
        
        x.setPdvNabava(getPdvNabava());
        x.setNabavnaCijena(getNabavna());
        
        x.setDeklaracija(deklaracijaArtikla.getText());
  
        getProManager().getTransaction().begin();

        getProManager().persist(x);
        
        spremiKnjiguPopisa();
        
        getProManager().getTransaction().commit();

        getProManager().refresh(x);

        ArtikalEvents.fireKreiranArtikal(this, x);
        
        Zatvori(null);
}//GEN-LAST:event_SpremiPromjene

    private void Zatvori(java.awt.event.ActionEvent evt)//GEN-FIRST:event_Zatvori
    {//GEN-HEADEREND:event_Zatvori
        try {
            setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(ProzorRoba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Zatvori

    private void UkupnaNabavnaVrijednost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_UkupnaNabavnaVrijednost
    {//GEN-HEADEREND:event_UkupnaNabavnaVrijednost
        FormatirajPolje(nabavnaBezPoreza, nf);
        IzracunajUkupnuNabavnuCijenu();
    }//GEN-LAST:event_UkupnaNabavnaVrijednost

    private void IzracunajUkupnuProdajnuCijenu(java.awt.event.FocusEvent evt)//GEN-FIRST:event_IzracunajUkupnuProdajnuCijenu
    {//GEN-HEADEREND:event_IzracunajUkupnuProdajnuCijenu
        FormatirajPolje(prodajnaBezPoreza, nf);
        IzracunajUkupnuProdajnuCijenu();
    }//GEN-LAST:event_IzracunajUkupnuProdajnuCijenu

    private void FokusNabavnaCijena(java.awt.event.FocusEvent evt)//GEN-FIRST:event_FokusNabavnaCijena
    {//GEN-HEADEREND:event_FokusNabavnaCijena
        nabavnaBezPoreza.selectAll();
    }//GEN-LAST:event_FokusNabavnaCijena

    private void FokusProdajnaCijena(java.awt.event.FocusEvent evt)//GEN-FIRST:event_FokusProdajnaCijena
    {//GEN-HEADEREND:event_FokusProdajnaCijena
        prodajnaBezPoreza.selectAll();
    }//GEN-LAST:event_FokusProdajnaCijena

    private void FocusUkupnaNabavnaCijena(java.awt.event.FocusEvent evt)//GEN-FIRST:event_FocusUkupnaNabavnaCijena
    {//GEN-HEADEREND:event_FocusUkupnaNabavnaCijena
        nabavnaUkupno.selectAll();
    }//GEN-LAST:event_FocusUkupnaNabavnaCijena

    private void nabavnaUkupnoFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_nabavnaUkupnoFocusLost
    {//GEN-HEADEREND:event_nabavnaUkupnoFocusLost
        FormatirajPolje(nabavnaUkupno, n2d);
        IzracunajNabavnuCijenuBezPoreza();
    }//GEN-LAST:event_nabavnaUkupnoFocusLost

    private void prodajnaUkupnoFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_prodajnaUkupnoFocusLost
    {//GEN-HEADEREND:event_prodajnaUkupnoFocusLost
        FormatirajPolje(prodajnaUkupno, n2d);
        IzracunajProdajnuCijenuBezPoreza();
    }//GEN-LAST:event_prodajnaUkupnoFocusLost

    private void prodajnaUkupnoFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_prodajnaUkupnoFocusGained
    {//GEN-HEADEREND:event_prodajnaUkupnoFocusGained
        prodajnaUkupno.selectAll();
    }//GEN-LAST:event_prodajnaUkupnoFocusGained

    private void MarzaFocused(java.awt.event.FocusEvent evt)//GEN-FIRST:event_MarzaFocused
    {//GEN-HEADEREND:event_MarzaFocused
        procjenaMarze.selectAll();
    }//GEN-LAST:event_MarzaFocused

    private void MarzaLostFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_MarzaLostFocus
    {//GEN-HEADEREND:event_MarzaLostFocus
        FormatirajPolje(procjenaMarze, nf);
        IzracunProdajneCijeneNaOsnovuMarze();
    }//GEN-LAST:event_MarzaLostFocus

    private void robaNazivFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_robaNazivFocusGained
    {//GEN-HEADEREND:event_robaNazivFocusGained
        robaNaziv.selectAll();
    }//GEN-LAST:event_robaNazivFocusGained

    private void sifraFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_sifraFocusGained
    {//GEN-HEADEREND:event_sifraFocusGained
        sifra.selectAll();
    }//GEN-LAST:event_sifraFocusGained

    private void OdabirMjerneJedinice(java.awt.event.FocusEvent evt)//GEN-FIRST:event_OdabirMjerneJedinice
    {//GEN-HEADEREND:event_OdabirMjerneJedinice
        robaJedinica.selectAll();
    }//GEN-LAST:event_OdabirMjerneJedinice

    private void pdvNabavaIzracunajNoveUkupneCijene(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdvNabavaIzracunajNoveUkupneCijene
        
        if(getPdvNabava() != null)
        {
            setNabavnaBezPoreza(getNabavna() / getPdvNabava().getNormaliziranaPoreznaStopa());
        }
        else
        {
            setNabavnaBezPoreza(getNabavna());
        }
        
        IzracunajMarzu();
    }//GEN-LAST:event_pdvNabavaIzracunajNoveUkupneCijene

    private void izracunajNoveNabavneCijene(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_izracunajNoveNabavneCijene
        IzracunajNabavnuCijenuBezPoreza();
        IzracunajProdajnuCijenuBezPoreza();
    }//GEN-LAST:event_izracunajNoveNabavneCijene

    private void FormatirajPolje(JTextField field, NumberFormat format)
    {
        try
        {
            Double vrijednost = format.parse(field.getText()).doubleValue();
            field.setText(format.format(vrijednost));
        }
        catch (ParseException ex)
        {
            Logger.getLogger(ProzorRoba.class.getName()).log(Level.SEVERE, null, ex);
            field.setText("0,00");
        }
    }
    
    private void FormatirajPolje(JTextField field)
    {
        FormatirajPolje(field, nf);
    }

    private void IzracunProdajneCijeneNaOsnovuMarze()
    {
        if(getNabavnaBezPoreza() != 0)
        {
            setProdajnaBezPoreza(getNabavnaBezPoreza() * (1 + getMarza() / 100));
            IzracunajUkupnuProdajnuCijenu();
        }
    }

    private void IzracunajProdajnuCijenuBezPoreza()
    {
        setProdajnaBezPoreza(getProdajna() / getDecimalnaPreznaStopa());
        IzracunajMarzu();
    }

    private void IzracunajUkupnuProdajnuCijenu()
    {
        setProdajna(getProdajnaBezPoreza() * getDecimalnaPreznaStopa());
        IzracunajMarzu();
    }

    private void IzracunajNabavnuCijenuBezPoreza()
    {
        try
        {
            if(getPdvNabava() != null)
            {
                setNabavnaBezPoreza(getNabavna() / getPdvNabava().getNormaliziranaPoreznaStopa());
            }
            else
            {
                setNabavnaBezPoreza(getNabavna());
            }
            
            IzracunajMarzu();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ProzorRoba.class.getName()).log(Level.SEVERE, null, ex);
            nabavnaBezPoreza.setText("0,00");
            nabavnaUkupno.setText("0,00");
        }
    }
    
    public Double getMarza()
    {
        try {
            return n2d.parse(procjenaMarze.getText()).doubleValue();
        } catch (ParseException ex) {
            setMarza(0D);
            return 0D;
        }
    }
    
    public void setMarza(Double marza)
    {
        procjenaMarze.setText(nf.format(marza));
    }

    private void IzracunajMarzu()
    {
        if(getNabavnaBezPoreza() == 0)
            setMarza(0D);
        else
            setMarza(((getProdajnaBezPoreza() - getNabavnaBezPoreza()) / getNabavnaBezPoreza()) * 100);
    }
    
    private Double getDecimalnaPreznaStopa()
    {
        Double stopa = 1D;
        
        if(getPdv() != null)
        {
            stopa += getPdv().getNormaliziraniPostotak();
        }
        
        if(getPot() != null)
        {
            stopa += getPot().getNormaliziraniPostotak();
        }
        
        return stopa;
    }

    private void IzracunajUkupnuNabavnuCijenu()
    {
        try
        {
            if(getPdvNabava() != null)
            {
                setNabavna(getNabavnaBezPoreza() * getPdvNabava().getNormaliziranaPoreznaStopa());
            }
            else
            {
                setNabavna(getNabavnaBezPoreza());
            }
            
            IzracunajMarzu();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ProzorRoba.class.getName()).log(Level.SEVERE, null, ex);
            nabavnaBezPoreza.setText("0,00");
            nabavnaUkupno.setText("0,00");
        }
    }

    @Action
    public void PostaviAutoSifre()
    {
        int novaSifra = Integer.parseInt(sifra.getText());
        if(novaSifra >= 0 && novaSifra < 10000)
        {
            AutoSifra auto = Pro3App.getApplication().getAutoSifre();
            auto.setSekvenca(novaSifra - 1);
            Tasks.showMessage("Sekvenca auto šifri postavljena na novu vrijednost");

            sifra.setText(auto.sljedecaFormatiranaSifra());
        }
        else Tasks.showMessage("Automatsko šifriranje je ograničen ona 100 0000 atikala.");
    }

    @Action
    public void PovuciNovuSifru()
    {
        sifra.setText(Pro3App.getApplication().getAutoSifre().sljedecaFormatiranaSifra());
    }

    @Action
    public void IzmjeniDeklaraciju()
    {
        Pro3View view = (Pro3View) getApplication().getMainView();
        view.Show(new PromjenaOpisaArtikla(getModel(), this));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdSpremi;
    private javax.swing.JButton cmdZatvori;
    private javax.swing.JComboBox comGrupa;
    private javax.swing.JTextArea deklaracijaArtikla;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private java.util.List<Acosoft.Processing.DataBox.PoreznaStopa> listStope;
    private java.util.List<GrupaArtikala> listaGrupaArtikala;
    private javax.swing.JMenuItem mnuPromjenaSifre;
    private javax.swing.JMenuItem mnuSljedecaSifra;
    private javax.swing.JMenuItem mnuSpremiTecaj;
    private javax.swing.JTextField nabavnaBezPoreza;
    private javax.swing.JTextField nabavnaUkupno;
    private javax.swing.JComboBox pdvNabava;
    private javax.swing.JPopupMenu popSekvencaSifre;
    private javax.swing.JPopupMenu popTecaj;
    private javax.swing.JComboBox poreznaStopa;
    private javax.swing.JComboBox potrosnja;
    private javax.swing.JLabel potrosnjaOpis;
    private javax.swing.JTextField procjenaMarze;
    private javax.swing.JTextField prodajnaBezPoreza;
    private javax.swing.JTextField prodajnaUkupno;
    private javax.persistence.Query queryGrupe;
    private javax.persistence.Query queryStope;
    private javax.swing.JTextField robaJedinica;
    private javax.swing.JTextField robaNaziv;
    private javax.swing.JTextField sifra;
    private javax.swing.JTabbedPane tabs;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
