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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="porezna_stopa")
@NamedQuery(name="Porez.SveStope", query="SELECT p FROM PoreznaStopa p")
public class PoreznaStopa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="kljuc",  nullable=false)
    private String kljuc;
    @Column(name="opis")
    private String opis;
    @Column(name="iznos")
    private double postotak;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="pocetak")
    private Date pocetakPrimjene;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="kraj", nullable=true)
    private Date krajPrimjene;
    
    public String getKljuc()
    {
        return kljuc;
    }

    public void setKljuc(String id)
    {
        this.kljuc = id;
    }

    @Override
    public String toString()
    {
        return getOpis();
    }

    public String getOpis()
    {
        return opis;
    }

    public void setOpis(String opis)
    {
        this.opis = opis;
    }

    public double getPostotak()
    {
        return postotak;
    }
    
    public double getNormaliziraniPostotak()
    {
        if(postotak <= 1)
            return postotak;
        else
            return postotak / 100D;
    }
    
    public double getNormaliziranaPoreznaStopa()
    {
        if(postotak <= 1)
        {
            return 1 + postotak;
        }
        else
        {
            return 1 + postotak / 100D;
        }
    }

    public void setPostotak(double postotak)
    {
        this.postotak = postotak;
    }

    public Date getPocetakPrimjene()
    {
        return pocetakPrimjene;
    }

    public void setPocetakPrimjene(Date pocetakPrimjene)
    {
        this.pocetakPrimjene = pocetakPrimjene;
    }

    public Date getKrajPrimjene()
    {
        return krajPrimjene;
    }

    public void setKrajPrimjene(Date krajPrimjene)
    {
        this.krajPrimjene = krajPrimjene;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final PoreznaStopa other = (PoreznaStopa) obj;
        if ((this.kljuc == null) ? (other.kljuc != null) : !this.kljuc.equals(other.kljuc))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 41 * hash + (this.kljuc != null ? this.kljuc.hashCode() : 0);
        return hash;
    }



}
