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

package Pro3x.PomocniModel;

import Acosoft.Processing.DataBox.Roba;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "ROBA_KARTICA_NORMATIVA")
@NamedQueries({@NamedQuery(name = "KarticaNormativa.findAll", query = "SELECT k FROM KarticaNormativa k"), @NamedQuery(name = "KarticaNormativa.findBySid", query = "SELECT k FROM KarticaNormativa k WHERE k.sid = :sid"), @NamedQuery(name = "KarticaNormativa.findByIzlaznaCijena", query = "SELECT k FROM KarticaNormativa k WHERE k.izlaznaCijena = :izlaznaCijena"), @NamedQuery(name = "KarticaNormativa.findByUlazKolicina", query = "SELECT k FROM KarticaNormativa k WHERE k.ulazKolicina = :ulazKolicina"), @NamedQuery(name = "KarticaNormativa.findByOpis", query = "SELECT k FROM KarticaNormativa k WHERE k.opis = :opis"), @NamedQuery(name = "KarticaNormativa.findByDatum", query = "SELECT k FROM KarticaNormativa k WHERE k.datum = :datum"), @NamedQuery(name = "KarticaNormativa.findByIzlazKolicina", query = "SELECT k FROM KarticaNormativa k WHERE k.izlazKolicina = :izlazKolicina"), @NamedQuery(name = "KarticaNormativa.findByUlaznaCijena", query = "SELECT k FROM KarticaNormativa k WHERE k.ulaznaCijena = :ulaznaCijena")})
public class KarticaNormativa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SID")
    private Long sid;
    @Column(name = "IZLAZNA_CIJENA")
    private Double izlaznaCijena;
    @Column(name = "ULAZ_KOLICINA")
    private Double ulazKolicina;
    @Column(name = "OPIS")
    private String opis;
    @Column(name = "DATUM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @Column(name = "IZLAZ_KOLICINA")
    private Double izlazKolicina;
    @Column(name = "ULAZNA_CIJENA")
    private Double ulaznaCijena;

    @ManyToOne
    @JoinColumn(name = "ROBA_KLJUC", referencedColumnName = "KLJUC")
    private Roba roba;

    public KarticaNormativa()
    {
    }

    public KarticaNormativa(Long sid)
    {
        this.sid = sid;
    }

    public Long getSid()
    {
        return sid;
    }

    public void setSid(Long sid)
    {
        this.sid = sid;
    }

    public Double getIzlaznaCijena()
    {
        return izlaznaCijena;
    }

    public void setIzlaznaCijena(Double izlaznaCijena)
    {
        this.izlaznaCijena = izlaznaCijena;
    }

    public Double getUlazKolicina()
    {
        return ulazKolicina;
    }

    public void setUlazKolicina(Double ulazKolicina)
    {
        this.ulazKolicina = ulazKolicina;
    }

    public String getOpis()
    {
        return opis;
    }

    public void setOpis(String opis)
    {
        this.opis = opis;
    }

    public Date getDatum()
    {
        return datum;
    }

    public void setDatum(Date datum)
    {
        this.datum = datum;
    }

    public Double getIzlazKolicina()
    {
        return izlazKolicina;
    }

    public void setIzlazKolicina(Double izlazKolicina)
    {
        this.izlazKolicina = izlazKolicina;
    }

    public Double getUlaznaCijena()
    {
        return ulaznaCijena;
    }

    public void setUlaznaCijena(Double ulaznaCijena)
    {
        this.ulaznaCijena = ulaznaCijena;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (sid != null ? sid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KarticaNormativa))
        {
            return false;
        }
        KarticaNormativa other = (KarticaNormativa) object;
        if ((this.sid == null && other.sid != null) || (this.sid != null && !this.sid.equals(other.sid)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Pro3x.PomocniModel.KarticaNormativa[sid=" + sid + "]";
    }

    public Roba getRoba()
    {
        return roba;
    }

    public void setRoba(Roba roba)
    {
        this.roba = roba;
    }

}
