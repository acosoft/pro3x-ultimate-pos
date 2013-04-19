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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author Aco
 */
@Entity
@Table(name="racun_naplata")
public class RacunNaplata implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;


    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable=false)
    protected Date datumDospijeca;
    public static final String PROP_DATUM_DOSPIJECA = "datumDospijeca";

    @Temporal(javax.persistence.TemporalType.DATE)
    protected Date datumPlacanja;
    public static final String PROP_DATUM_PLACANJA = "datumPlacanja";

    @Column(nullable=false)
    protected Double iznos;
    public static final String PROP_IZNOS = "iznos";

    @ManyToOne
    protected Racun racun;
    public static final String PROP_RACUN = "racun";
    
    public Racun getRacun()
    {
        return racun;
    }

    public void setRacun(Racun racun)
    {
        Racun oldRacun = this.racun;
        this.racun = racun;
        propertyChangeSupport.firePropertyChange(PROP_RACUN, oldRacun, racun);
    }

    public Double getIznos()
    {
        return iznos;
    }

    public void setIznos(Double iznos)
    {
        Double oldIznos = this.iznos;
        this.iznos = iznos;
        propertyChangeSupport.firePropertyChange(PROP_IZNOS, oldIznos, iznos);
    }

    public Date getDatumPlacanja()
    {
        return datumPlacanja;
    }

    public void setDatumPlacanja(Date datumPlacanja)
    {
        Date oldDatumPlacanja = this.datumPlacanja;
        this.datumPlacanja = datumPlacanja;
        propertyChangeSupport.firePropertyChange(PROP_DATUM_PLACANJA, oldDatumPlacanja, datumPlacanja);
    }


    public Date getDatumDospijeca()
    {
        return datumDospijeca;
    }

    public void setDatumDospijeca(Date datumDospijeca)
    {
        Date oldDatumDospijeca = this.datumDospijeca;
        this.datumDospijeca = datumDospijeca;
        propertyChangeSupport.firePropertyChange(PROP_DATUM_DOSPIJECA, oldDatumDospijeca, datumDospijeca);
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


    public RacunNaplata()
    {
        this.id = UUID.randomUUID().toString();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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
        if (!(object instanceof RacunNaplata))
        {
            return false;
        }
        RacunNaplata other = (RacunNaplata) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Acosoft.Processing.DataBox.RacunNaplata[id=" + id + "]";
    }

}
