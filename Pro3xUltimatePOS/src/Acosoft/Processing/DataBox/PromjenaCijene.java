/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Acosoft.Processing.DataBox;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author Aco
 */
@Entity
@Table(name="roba_promjena_cijene")
@NamedQuery(name="PromjenaCijene.SvePromjeneZaPeriod", query="SELECT p FROM PromjenaCijene p WHERE p.pocetak BETWEEN :pocetak AND :kraj ORDER BY p.pocetak ASC")
public class PromjenaCijene implements Serializable {
    private static final long serialVersionUID = 1L;

    public PromjenaCijene()
    {
        this.id = UUID.randomUUID().toString();
    }

    @Id
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Column
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date pocetak;
    public static final String PROP_POCETAK = "pocetak";

    protected Double maloprodajnaCijena;
    public static final String PROP_MALOPRODAJNA_CIJENA = "maloprodajnaCijena";

    public Double getMaloprodajnaCijena()
    {
        return maloprodajnaCijena;
    }

    public void setMaloprodajnaCijena(Double maloprodajnaCijena)
    {
        Double oldMaloprodajnaCijena = this.maloprodajnaCijena;
        this.maloprodajnaCijena = maloprodajnaCijena;
        propertyChangeSupport.firePropertyChange(PROP_MALOPRODAJNA_CIJENA, oldMaloprodajnaCijena, maloprodajnaCijena);
    }

    @ManyToOne
    protected Roba artikal;
    public static final String PROP_ARTIKAL = "artikal";

    public Roba getArtikal()
    {
        return artikal;
    }

    public void setArtikal(Roba artikal)
    {
        Roba oldArtikal = this.artikal;
        this.artikal = artikal;
        propertyChangeSupport.firePropertyChange(PROP_ARTIKAL, oldArtikal, artikal);
    }


    public Date getPocetak()
    {
        return pocetak;
    }

    public void setPocetak(Date pocetak)
    {
        Date oldPocetak = this.pocetak;
        this.pocetak = pocetak;
        propertyChangeSupport.firePropertyChange(PROP_POCETAK, oldPocetak, pocetak);
    }

    @Transient
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Transient
    protected PromjenaCijene prethodna = null;

    public PromjenaCijene getPrethodna()
    {
        return prethodna;
    }

    public void setPrethodna(PromjenaCijene prethodna)
    {
        this.prethodna = prethodna;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PromjenaCijene))
        {
            return false;
        }
        PromjenaCijene other = (PromjenaCijene) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Acosoft.Processing.DataBox.PromjenaCijene[id=" + id + "]";
    }

}
