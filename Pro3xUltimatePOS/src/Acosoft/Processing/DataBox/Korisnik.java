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

package Acosoft.Processing.DataBox;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "korisnik")
@NamedQueries({@NamedQuery(name = "Korisnik.findAll", query = "SELECT k FROM Korisnik k"), @NamedQuery(name = "Korisnik.findByKljuc", query = "SELECT k FROM Korisnik k WHERE k.kljuc = :kljuc"), @NamedQuery(name = "Korisnik.findByNaziv", query = "SELECT k FROM Korisnik k WHERE k.naziv = :naziv"), @NamedQuery(name = "Korisnik.findByAdresa", query = "SELECT k FROM Korisnik k WHERE k.adresa = :adresa"), @NamedQuery(name = "Korisnik.findByLokacija", query = "SELECT k FROM Korisnik k WHERE k.lokacija = :lokacija"), @NamedQuery(name = "Korisnik.findByMaticniBroj", query = "SELECT k FROM Korisnik k WHERE k.maticniBroj = :maticniBroj"), @NamedQuery(name = "Korisnik.findByTelefon", query = "SELECT k FROM Korisnik k WHERE k.telefon = :telefon"), @NamedQuery(name = "Korisnik.findByMobitel", query = "SELECT k FROM Korisnik k WHERE k.mobitel = :mobitel"), @NamedQuery(name = "Korisnik.findByEmail", query = "SELECT k FROM Korisnik k WHERE k.email = :email")})
public class Korisnik implements Serializable, Comparable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "kljuc")
    private String kljuc;
    @Column(name = "naziv")
    private String naziv;
    @Column(name = "adresa")
    private String adresa;
    @Column(name = "lokacija")
    private String lokacija;
    @Column(name = "maticni_broj")
    private String maticniBroj;
    @Column(name = "telefon")
    private String telefon;
    @Column(name = "mobitel")
    private String mobitel;
    @Column(name = "email")
    private String email;
   
    public Korisnik()
    {
    }

    public Korisnik(String kljuc)
    {
        this.kljuc = kljuc;
    }

    public String getKljuc()
    {
        return kljuc;
    }

    public void setKljuc(String kljuc)
    {
        String oldKljuc = this.kljuc;
        this.kljuc = kljuc;
        changeSupport.firePropertyChange("kljuc", oldKljuc, kljuc);
    }

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        String oldNaziv = this.naziv;
        this.naziv = naziv;
        changeSupport.firePropertyChange("naziv", oldNaziv, naziv);
    }

    public String getAdresa()
    {
        return adresa;
    }

    public void setAdresa(String adresa)
    {
        String oldAdresa = this.adresa;
        this.adresa = adresa;
        changeSupport.firePropertyChange("adresa", oldAdresa, adresa);
    }

    public String getLokacija()
    {
        return lokacija;
    }

    public void setLokacija(String lokacija)
    {
        String oldLokacija = this.lokacija;
        this.lokacija = lokacija;
        changeSupport.firePropertyChange("lokacija", oldLokacija, lokacija);
    }

    public String getMaticniBroj()
    {
        return maticniBroj;
    }

    public void setMaticniBroj(String maticniBroj)
    {
        String oldMaticniBroj = this.maticniBroj;
        this.maticniBroj = maticniBroj;
        changeSupport.firePropertyChange("maticniBroj", oldMaticniBroj, maticniBroj);
    }

    public String getTelefon()
    {
        return telefon;
    }

    public void setTelefon(String telefon)
    {
        String oldTelefon = this.telefon;
        this.telefon = telefon;
        changeSupport.firePropertyChange("telefon", oldTelefon, telefon);
    }

    public String getMobitel()
    {
        return mobitel;
    }

    public void setMobitel(String mobitel)
    {
        String oldMobitel = this.mobitel;
        this.mobitel = mobitel;
        changeSupport.firePropertyChange("mobitel", oldMobitel, mobitel);
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        String oldEmail = this.email;
        this.email = email;
        changeSupport.firePropertyChange("email", oldEmail, email);
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (kljuc != null ? kljuc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Korisnik))
        {
            return false;
        }
        Korisnik other = (Korisnik) object;
        if ((this.kljuc == null && other.kljuc != null) || (this.kljuc != null && !this.kljuc.equals(other.kljuc)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        //return "Acosoft.Processing.Korisnik[kljuc=" + kljuc + "]";
        
        if(getNaziv().isEmpty())
            return "";
        else if(getAdresa().isEmpty())
            return getNaziv();
        else if(getLokacija().isEmpty())
            return getNaziv() + ", " + getAdresa();
        else
            return getNaziv() + ", " + getAdresa() + ", " + getLokacija();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

    public int compareTo(Object drugiKorisnik)
    {
        if(drugiKorisnik instanceof Korisnik)
            return ((Korisnik)drugiKorisnik).getNaziv().compareTo(this.naziv) * -1;
        else
            return -1;
    }
}
