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

package Acosoft.Processing.DataBox;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="artikal_normativi")
public class ArtikalNormativ implements Serializable
{
    private static final long serialVersionUID = 1L;

    public ArtikalNormativ()
    {
        id = UUID.randomUUID().toString();
        kolicina = 0D;
    }

    @Id
    private String id;
    
    @ManyToOne
    protected Roba normativ;
    public static final String PROP_NORMATIV = "normativ";
    public static final String PROP_VRIJEDNOST = "vrijednost";

    public Roba getNormativ()
    {
        return normativ;
    }

    public void setNormativ(Roba normativ)
    {
        Roba oldNormativ = this.normativ;
        this.normativ = normativ;
        propertyChangeSupport.firePropertyChange(PROP_NORMATIV, oldNormativ, normativ);
    }


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    @Deprecated
    public Double getVrijednost()
    {
        return this.kolicina * getNormativ().getMaloprodajnaCijena();
    }

    @Column
    protected Double kolicina;
    public static final String PROP_KOLICINA = "kolicina";

    public Double getKolicina()
    {
        return kolicina;
    }

    public void setKolicina(Double kolicina)
    {
        Double staraVrijednsot = getVrijednost();
        Double oldKolicina = this.kolicina;

        this.kolicina = kolicina;
        propertyChangeSupport.firePropertyChange(PROP_KOLICINA, oldKolicina, kolicina);
        propertyChangeSupport.firePropertyChange(PROP_VRIJEDNOST, staraVrijednsot, getVrijednost());
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
        if (!(object instanceof ArtikalNormativ))
        {
            return false;
        }
        ArtikalNormativ other = (ArtikalNormativ) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Acosoft.Processing.DataBox.Normativ[id=" + id + "]";
    }

}
