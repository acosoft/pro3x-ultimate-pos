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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
//@Table(name="roba_kartica")
@Table(name="kartice_artikala")
//@NamedQueries({
//    @NamedQuery(name="RobaKartica.Sve", query="SELECT k FROM RobaKartica k"),
//    @NamedQuery(name="RobaKartica.IzbrisiKarticu", query="DELETE FROM RobaKartica r WHERE r.sid = :sid")
//})
@NamedQuery(name="RobaKartica.SveKarticeZaPeriod", query="SELECT k FROM RobaKartica k WHERE k.datum BETWEEN :pocetak AND :kraj")
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
public class RobaKartica implements java.io.Serializable 
{
    private static Long sequence = System.currentTimeMillis();

    @Id
    @Column(name = "sid", nullable=false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long sid;
    
    //@JoinColumn(name="roba_kljuc", referencedColumnName="KLJUC")
    @ManyToOne(targetEntity = Roba.class)
    private Roba roba;
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "datum")
    private Date datum;
    @Column(name = "ulaz_kolicina")
    private Double kolicinaUlaz;
    @Column(name = "izlaz_kolicina")
    private Double kolicinaIzlaz;
    @Column(name = "opis")
    private String opis;
    @Column(name = "ulazna_cijena")
    private Double ulaznaCijena;
    @Column(name = "izlazna_cijena")
    private Double izlaznaCijena;

//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "kartica")
//    protected KarticaStavke stavkaKartice;

//    private synchronized Long SljedecaSekvenca()
//    {
//        sequence += 1;
//        return sequence;
//    }

    public RobaKartica()
    {
        //this.sid = SljedecaSekvenca();
    }

//    public KarticaStavke getStavkaKartice()
//    {
//        return stavkaKartice;
//    }
//
//    public void setStavkaKartice(KarticaStavke stavkaKartice)
//    {
//        this.stavkaKartice = stavkaKartice;
//    }

    public Long getSid()
    {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Double getKolicinaUlaz() {
        if(kolicinaUlaz != null)
            return kolicinaUlaz;
        else
            return 0D;
    }
    
    public Double getPregledKolicineUlaz()
    {
        if(this.getKolicinaIzlaz() < 0)
            return -this.getKolicinaIzlaz();
        else if (this.getKolicinaUlaz() < 0)
            return 0D;
        else
            return this.getKolicinaUlaz();
    }
    
    public Double getPregledKolicineIzlaz()
    {
        if(this.getKolicinaUlaz() < 0)
            return - this.getKolicinaUlaz();
        else if(this.getKolicinaIzlaz() < 0)
            return 0D;
        else
            return this.getKolicinaIzlaz();
    }

    public void setKolicinaUlaz(Double kolicinaUlaz) {
        this.kolicinaUlaz = kolicinaUlaz;
    }

    public Double getKolicinaIzlaz() {
        if(kolicinaIzlaz != null)
            return kolicinaIzlaz;
        else
            return 0D;
    }

    public void setKolicinaIzlaz(Double kolicinaIzlaz) {
        this.kolicinaIzlaz = kolicinaIzlaz;
    }

    public Double getUlaznaCijena() {
        if(ulaznaCijena != null)
            return ulaznaCijena;
        else
            return 0D;
    }

    public Double getUlaznaVrijednost()
    {
        return getUlaznaCijena();
    }

    public void setUlaznaCijena(Double ulaznaCijena) {
        this.ulaznaCijena = ulaznaCijena;
    }

    public Double getIzlaznaCijena() {
        return izlaznaCijena;
    }

    public void setIzlaznaCijena(Double izlaznaCijena) {
        this.izlaznaCijena = izlaznaCijena;
    }

    public Roba getRoba() {
        return roba;
    }

    public void setRoba(Roba roba) {
        this.roba = roba;
    }

    public Double getMaloprodajnaCijena()
    {
        return Math.round(getIzlaznaCijena() / getKolicinaIzlaz() * 100) / 100D;
    }
    
    public Double getReportMaloprodajnaCijena()
    {
        if(getKolicinaIzlaz() != 0)
            return getMaloprodajnaCijena();
        else
            return 0D;
    }

    public boolean isStornoKartica()
    {
        if(getKolicinaIzlaz() != 0D && getKolicinaUlaz() != 0)
            return true;
        else
            return false;
    }
}
