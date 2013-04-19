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

package Pro3x.Kalkulacije.Model;

import Acosoft.Processing.DataBox.Dobavljac;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;


@Entity
@Table(name="kalkulacija")
@NamedQuery(name="Kalkulacija.Sve", query="SELECT k FROM Kalkulacija k")
public class Kalkulacija implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="kljuc")
    private String id;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "kalkulacija")
    protected List<StavkaKalkulacije> stavke;
    public static final String PROP_STAVKE = "stavke";

    public List<StavkaKalkulacije> getStavke()
    {
        return stavke;
    }

    public void setStavke(List<StavkaKalkulacije> stavke)
    {
        List<StavkaKalkulacije> oldStavke = this.stavke;
        this.stavke = stavke;
        propertyChangeSupport.firePropertyChange(PROP_STAVKE, oldStavke, stavke);
    }

    public Kalkulacija()
    {
        this.id = UUID.randomUUID().toString();
        this.stavke = new LinkedList<StavkaKalkulacije>();

        Calendar cal = Calendar.getInstance();
        setDatumDokumenta(cal.getTime());
        setDatumIzrade(cal.getTime());

        cal.add(Calendar.DATE, 7);
        setDatumDospijeca(cal.getTime());
    }

    public Double getUkupnaProdajnaVrijednost()
    {
        double suma = 0;

        for(StavkaKalkulacije stavka : getStavke())
            suma += ZaokruziBroj(stavka.getUkupno());

        return suma;
    }

    private Double ZaokruziBroj(Double broj)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        String prikaz = nf.format(broj);
        try
        {
            return nf.parse(prikaz).doubleValue();

        }
        catch (ParseException ex)
        {
            Logger.getLogger(Kalkulacija.class.getName()).log(Level.SEVERE, null, ex);
            return Math.round(broj * 100) / 100D;
        }
    }

    public Double getUkupniPorez()
    {
        double suma = 0D;

        for(StavkaKalkulacije stavka : getStavke())
            suma += stavka.getIznosPoreza();

        return suma;
    }

    public Double getUkupnaMarza()
    {
        double suma = 0D;
        
        for(StavkaKalkulacije stavka : getStavke())
            suma += stavka.getIznosMarze();
        
        return suma;
    }

    public Double getUkupnaNabavnaVrijednost()
    {
        double suma = 0D;

        for(StavkaKalkulacije stavka : getStavke())
            suma += ZaokruziBroj(stavka.getFakturnaSaPorezom());

        return suma;
    }

    public Double getUkupnaNabavnaVrijednostBezPoreza()
    {
        double suma = 0D;

        for(StavkaKalkulacije stavka : getStavke())
            //suma += ZaokruziBroj(stavka.getFakturnaBezPoreza());
            suma += stavka.getFakturnaBezPoreza();

        return suma;
    }

    @Column(name="datum_izrade")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date datumIzrade;
    public static final String PROP_DATUM_IZRADE = "datumIzrade";

    @Column(name="datum_dospijeca")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date datumDospijeca;
    public static final String PROP_DATUM_DOSPIJECA = "datumDospijeca";

    @Column(name="datum_placenja")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date datumPlacanja;
    public static final String PROP_DATUM_PLACANJA = "datumPlacanja";

    @Column(name="datum_dokumenta")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date datumDokumenta;
    public static final String PROP_DATUM_DOKUMENTA = "datumDokumenta";

    @Column(name="oznaka_kalkulacije")
    protected String oznakaKalkulacije;
    public static final String PROP_OZNAKA_KALKULACIJE = "oznakaKalkulacije";

    @Column(name="oznaka_dokumenta")
    protected String oznakaDokumenta;
    public static final String PROP_OZNAKA_DOKUMENTA = "oznakaDokumenta";

    @ManyToOne
    protected Dobavljac dobavljac;
    public static final String PROP_DOBAVLJAC = "dobavljac";

    @Column
    protected String izradio;
    public static final String PROP_IZRADIO = "izradio";

    public String getIzradio()
    {
        if(izradio == null)
            return "";
        else
            return izradio;
    }

    public void setIzradio(String izradio)
    {
        String oldIzradio = this.izradio;
        this.izradio = izradio;
        propertyChangeSupport.firePropertyChange(PROP_IZRADIO, oldIzradio, izradio);
    }

    public Dobavljac getDobavljac()
    {
        return dobavljac;
    }

    public void setDobavljac(Dobavljac dobavljac)
    {
        Dobavljac oldDobavljac = this.dobavljac;
        this.dobavljac = dobavljac;
        propertyChangeSupport.firePropertyChange(PROP_DOBAVLJAC, oldDobavljac, dobavljac);
    }


    public String getOznakaDokumenta()
    {
        if(oznakaDokumenta == null)
            return "";
        else
            return oznakaDokumenta;
    }

    public void setOznakaDokumenta(String oznakaDokumenta)
    {
        String oldOznakaDokumenta = this.oznakaDokumenta;
        this.oznakaDokumenta = oznakaDokumenta;
        propertyChangeSupport.firePropertyChange(PROP_OZNAKA_DOKUMENTA, oldOznakaDokumenta, oznakaDokumenta);
    }


    public String getOznakaKalkulacije()
    {
        return oznakaKalkulacije;
    }

    public void setOznakaKalkulacije(String oznakaKalkulacije)
    {
        String oldOznakaKalkulacije = this.oznakaKalkulacije;
        this.oznakaKalkulacije = oznakaKalkulacije;
        propertyChangeSupport.firePropertyChange(PROP_OZNAKA_KALKULACIJE, oldOznakaKalkulacije, oznakaKalkulacije);
    }


    public Date getDatumDokumenta()
    {
        return datumDokumenta;
    }

    public void setDatumDokumenta(Date datumDokumenta)
    {
        Date oldDatumDokumenta = this.datumDokumenta;
        this.datumDokumenta = datumDokumenta;
        propertyChangeSupport.firePropertyChange(PROP_DATUM_DOKUMENTA, oldDatumDokumenta, datumDokumenta);
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


    public Date getDatumIzrade()
    {
        return datumIzrade;
    }

    public void setDatumIzrade(Date datumIzrade)
    {
        Date oldDatumIzrade = this.datumIzrade;
        this.datumIzrade = datumIzrade;
        propertyChangeSupport.firePropertyChange(PROP_DATUM_IZRADE, oldDatumIzrade, datumIzrade);
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kalkulacija))
        {
            return false;
        }
        Kalkulacija other = (Kalkulacija) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Acosoft.Processing.DataBox.Kalkulacije.Kalkulacija[id=" + id + "]";
    }

}
