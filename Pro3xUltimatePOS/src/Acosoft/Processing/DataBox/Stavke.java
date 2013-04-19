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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "STAVKE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tip")
@DiscriminatorValue("v1")
@NamedQueries({
    @NamedQuery(name = "Stavke.findByKljuc", query = "SELECT s FROM Stavke s WHERE s.kljuc = :kljuc"), 
    @NamedQuery(name = "Stavke.findByCijena", query = "SELECT s FROM Stavke s WHERE s.cijena = :cijena"), 
    @NamedQuery(name = "Stavke.findByUkupno", query = "SELECT s FROM Stavke s WHERE s.ukupno = :ukupno"), 
    @NamedQuery(name = "Stavke.findByRoba", query = "SELECT s FROM Stavke s WHERE s.roba = :roba"), 
    @NamedQuery(name = "Stavke.findByKolicina", query = "SELECT s FROM Stavke s WHERE s.kolicina = :kolicina"),
    @NamedQuery(name = "Stavke.findByPopust", query = "SELECT s FROM Stavke s WHERE s.racunKljuc.storniran IS NULL "
        + "AND s.racunKljuc.izdan BETWEEN :pocetak AND :kraj AND s.popust <> 0")
})
@NamedQuery(name="Stavke.DnevniPromet", query="SELECT s.roba, SUM(s.kolicina), SUM(s.ukupno), SUM(s.iznos) FROM Racun r INNER JOIN r.stavke s WHERE r.template.tip = :vrstaUplate AND (r.izdan BETWEEN :pocetak AND :kraj) AND r.storniran IS null GROUP BY s.roba, s.cijena")
public class Stavke implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "KLJUC", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long kljuc;
    @Column(name = "CIJENA", nullable = false)
    private double cijena;
    
    @ManyToOne
    @JoinColumn(name="ROBA_KLJUC", nullable=false)
    private Roba robaInfo;
    
    @Column(name = "UKUPNO", nullable = false)
    private double ukupno;
    
    @Column(name = "ROBA", nullable = false)
    private String roba;
    @Column(name="MJERA")
    private String mjera;
    @Column(name="IZNOS")
    private Double iznos;
    @Column(name="POPUST")
    private Double popust;
    @Column(name = "KOLICINA", nullable = false)
    private double kolicina;
    
    @JoinColumn(name = "RACUN_KLJUC", referencedColumnName = "KLJUC")
    @ManyToOne
    private Racun racunKljuc;
    
    @JoinColumn(name = "grupa", referencedColumnName = "kljuc")
    @ManyToOne
    private GrupaArtikala grupa;

    @Transient
    protected UUID pomocniKljuc;

    //@OneToOne(mappedBy = "stavka", cascade=CascadeType.ALL, targetEntity=KarticaStavkeRacuna.class)
    
    @OneToMany(mappedBy="stavka", cascade= CascadeType.ALL, targetEntity=KarticaStavkeRacuna.class)
    private List<KarticaStavkeRacuna> kartice;

	public Roba getRobaInfo()
    {
        return robaInfo;
    }

    public void setRobaInfo(Roba robaInfo)
    {
        this.robaInfo = robaInfo;
    }

    public UUID getPomocniKljuc()
    {
        return pomocniKljuc;
    }

    public void setPomocniKljuc(UUID pomocniKljuc)
    {
        this.pomocniKljuc = pomocniKljuc;
    }

    public Stavke() 
    {
        this.pomocniKljuc = UUID.randomUUID();
        this.kartice = new ArrayList<KarticaStavkeRacuna>();
    }

    public Stavke(Long kljuc) {
        this.kljuc = kljuc;
        this.kartice = new ArrayList<KarticaStavkeRacuna>();
    }

    public Long getKljuc() {
        return kljuc;
    }

    public void setKljuc(Long kljuc) {
        this.kljuc = kljuc;
    }

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public double getUkupno() {
        return ukupno;
    }

    public void setUkupno(double ukupno) {
        this.ukupno = ukupno;
    }

    public String getRoba() {
        return roba;
    }

    public void setRoba(String roba) {
        this.roba = roba;
    }

    public double getKolicina() {
        return kolicina;
    }

    public void setKolicina(double kolicina) {
        this.kolicina = kolicina;
    }

    public Racun getRacunKljuc() {
        return racunKljuc;
    }

    public void setRacunKljuc(Racun racunKljuc) {
        this.racunKljuc = racunKljuc;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kljuc != null ? kljuc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Stavke))
        {
            return false;
        }
        Stavke other = (Stavke) object;

        if(this.kljuc == null && other.kljuc == null)
        {
            return this.pomocniKljuc == other.pomocniKljuc;
        }

        if ((this.kljuc == null && other.kljuc != null) || (this.kljuc != null && !this.kljuc.equals(other.kljuc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Acosoft.Processing.DataBox.Stavke[kljuc=" + kljuc + ", " +
                "roba=" + roba + "]";
    }

    public String getMjera() {
        return mjera;
    }

    public void setMjera(String mjera) {
        this.mjera = mjera;
    }

    public Double getIznos() {
        return iznos;
    }

    public void setIznos(Double iznos) {
        this.iznos = iznos;
    }

    public Double getPopust() {
        return popust;
    }

    public void setPopust(Double popust) {
        this.popust = popust;
    }
    
    @Column
    private Double maloprodajnaCijena;

    public Double getMaloprodajnaCijena()
    {
        return maloprodajnaCijena;
    }

    public void setMaloprodajnaCijena(Double maloprodajnaCijena)
    {
        this.maloprodajnaCijena = maloprodajnaCijena;
    }

    public GrupaArtikala getGrupa()
    {
        return this.grupa;
    }

    public void setGrupa(GrupaArtikala grupa)
    {
        this.grupa = grupa;
    }

    public List<KarticaStavkeRacuna> getKartice() {
        return kartice;
    }
}
