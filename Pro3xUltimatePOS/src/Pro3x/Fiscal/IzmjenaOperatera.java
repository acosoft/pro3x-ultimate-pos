/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IzmjenaOperatera.java
 *
 * Created on 21.12.2012., 22:58:26
 */
package Pro3x.Fiscal;

import Acosoft.Processing.Components.Tasks;
import Pro3x.BasicView;
import Pro3x.Fiscal.Model.Operater;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.jdesktop.application.Action;

/**
 *
 * @author aco
 */
public class IzmjenaOperatera extends BasicView {

    private Operater operater;
    private EntityManager manager;
    
    public IzmjenaOperatera(EntityManager manager)
    {
        this(new Operater(), manager);
        this.setTitle("Novi operater");
        
        checkPromjenaZaporke.setSelected(true);
        checkPromjenaZaporke.setVisible(false);
    }
    
    /** Creates new form IzmjenaOperatera */
    public IzmjenaOperatera(Operater operater, EntityManager manager) {
        this.setOperater(operater);
        this.manager = manager;
        
        initComponents();
        
        checkPromjenaZaporke.setSelected(false);
        getRootPane().setDefaultButton(cmdSpremi);
    }

    public Operater getOperater() {
        return operater;
    }

    public void setOperater(Operater operater) {
        this.operater = operater;
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

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        zaporka = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        zaporkaPonovno = new javax.swing.JPasswordField();
        naziv = new javax.swing.JTextField();
        naziv1 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        checkPromjenaZaporke = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        cmdSpremi = new javax.swing.JButton();
        cmdZatvori = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getResourceMap(IzmjenaOperatera.class);
        setTitle(resourceMap.getString("izmjena-operatera.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(600, 300));
        setName("izmjena-operatera"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 0, 1));
        jPanel2.setMinimumSize(new java.awt.Dimension(500, 200));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 10));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        zaporka.setMargin(new java.awt.Insets(2, 2, 2, 2));
        zaporka.setName("zaporka"); // NOI18N
        zaporka.setNextFocusableComponent(zaporkaPonovno);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkPromjenaZaporke, org.jdesktop.beansbinding.ELProperty.create("${selected}"), zaporka, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        jPanel6.add(zaporka, gridBagConstraints);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel6.add(jLabel4, gridBagConstraints);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel6.add(jLabel2, gridBagConstraints);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel6.add(jLabel3, gridBagConstraints);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel6.add(jLabel1, gridBagConstraints);

        zaporkaPonovno.setText(resourceMap.getString("zaporkaPonovno.text")); // NOI18N
        zaporkaPonovno.setMargin(new java.awt.Insets(2, 2, 2, 2));
        zaporkaPonovno.setName("zaporkaPonovno"); // NOI18N
        zaporkaPonovno.setNextFocusableComponent(cmdSpremi);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkPromjenaZaporke, org.jdesktop.beansbinding.ELProperty.create("${selected}"), zaporkaPonovno, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        jPanel6.add(zaporkaPonovno, gridBagConstraints);

        naziv.setMargin(new java.awt.Insets(2, 2, 2, 2));
        naziv.setName("naziv"); // NOI18N
        naziv.setNextFocusableComponent(naziv1);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${operater.name}"), naziv, org.jdesktop.beansbinding.BeanProperty.create("text"), "naziv"); // NOI18N
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        jPanel6.add(naziv, gridBagConstraints);

        naziv1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        naziv1.setName("naziv1"); // NOI18N
        naziv1.setNextFocusableComponent(checkPromjenaZaporke);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${operater.oib}"), naziv1, org.jdesktop.beansbinding.BeanProperty.create("text"), "oib");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        jPanel6.add(naziv1, gridBagConstraints);

        jPanel3.add(jPanel6, java.awt.BorderLayout.PAGE_START);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setName("jPanel5"); // NOI18N

        checkPromjenaZaporke.setText(resourceMap.getString("text")); // NOI18N
        checkPromjenaZaporke.setMargin(new java.awt.Insets(3, 5, 3, 5));
        checkPromjenaZaporke.setName(""); // NOI18N
        checkPromjenaZaporke.setNextFocusableComponent(zaporka);
        jPanel5.add(checkPromjenaZaporke);

        jPanel4.add(jPanel5, java.awt.BorderLayout.LINE_START);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Acosoft.Processing.Pro3App.class).getContext().getActionMap(IzmjenaOperatera.class, this);
        cmdSpremi.setAction(actionMap.get("Spremi")); // NOI18N
        cmdSpremi.setText(resourceMap.getString("cmdSpremi.text")); // NOI18N
        cmdSpremi.setName("cmdSpremi"); // NOI18N
        cmdSpremi.setNextFocusableComponent(cmdZatvori);
        cmdSpremi.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdSpremi);

        cmdZatvori.setAction(actionMap.get("Zatvori")); // NOI18N
        cmdZatvori.setText(resourceMap.getString("cmdZatvori.text")); // NOI18N
        cmdZatvori.setName("cmdZatvori"); // NOI18N
        cmdZatvori.setNextFocusableComponent(naziv);
        cmdZatvori.setPreferredSize(new java.awt.Dimension(90, 30));
        jPanel1.add(cmdZatvori);

        jPanel4.add(jPanel1, java.awt.BorderLayout.LINE_END);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_END);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void Zatvori() {
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(IzmjenaOperatera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public void Spremi() {
        
        String novaZaporka = new String(zaporka.getPassword());
        String novaZaporkaPonovno = new String(zaporkaPonovno.getPassword());
        
        if(novaZaporka.equals(novaZaporkaPonovno))
        {
            if(novaZaporka.isEmpty() == false)
            {
                getOperater().setPassword(novaZaporka);
            }
            
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            manager.persist(this.getOperater());
            transaction.commit();
            
            firePropertyChange("operater", null, operater);

            Tasks.showMessage("Detalji operatera su uspješno spremljeni");
            try {
                this.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(IzmjenaOperatera.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            zaporka.setText("");
            zaporkaPonovno.setText("");
            
            Tasks.showMessage("Unesene zaporke nisu jednake. Ponovite unos zaporki.");
        }
       
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkPromjenaZaporke;
    private javax.swing.JButton cmdSpremi;
    private javax.swing.JButton cmdZatvori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField naziv;
    private javax.swing.JTextField naziv1;
    private javax.swing.JPasswordField zaporka;
    private javax.swing.JPasswordField zaporkaPonovno;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
