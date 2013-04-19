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

import Pro3x.Kalkulacije.Model.Kalkulacija;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="DOBAVLJAC")
@NamedQueries(@NamedQuery(name="Dobavljac.Svi", query="SELECT d FROM Dobavljac d"))
public class Dobavljac implements Serializable
{
    @Id
    @Column(name="KLJUC", nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer kljuc;
    @Column(name="NAZIV")
    private String naziv;
    @Column(name="ADRESA")
    private String adresa;
    @Column(name="LOKACIJA")
    private String lokacija;
    @Column(name="MATICNI_BROJ")
    private String maticniBroj;
    @Column(name="ZIRO")
    private String ziro;
    @OneToMany(mappedBy = "dobavljac")
    private List<Kalkulacija> kalkulacije;

    @Transient
    private UUID usporedjivanje = UUID.randomUUID();

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
        final Dobavljac other = (Dobavljac) obj;
        
        if(this.kljuc == null && other.kljuc == null)
        {
            return this.usporedjivanje.equals(other.usporedjivanje);
        }
        
        if (this.kljuc != other.kljuc && (this.kljuc == null || !this.kljuc.equals(other.kljuc)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (this.kljuc != null ? this.kljuc.hashCode() : 0);
        return hash;
    }

    public Integer getKljuc() {
        return kljuc;
    }

    public void setKljuc(Integer kljuc) {
        this.kljuc = kljuc;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public String getMaticniBroj() {
        return maticniBroj;
    }

    public void setMaticniBroj(String maticniBroj) {
        this.maticniBroj = maticniBroj;
    }

    public String getZiro() {
        return ziro;
    }

    public void setZiro(String ziro) {
        this.ziro = ziro;
    }

    @Override
    public String toString() {
        return getNaziv();
    }

    /**
     * @return the kalkulacije
     */
    public List<Kalkulacija> getKalkulacije()
    {
        return kalkulacije;
    }

    /**
     * @param kalkulacije the kalkulacije to set
     */
    public void setKalkulacije(List<Kalkulacija> kalkulacije)
    {
        this.kalkulacije = kalkulacije;
    }
}
