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

import Pro3x.Fiscal.Model.Operater;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "RACUN")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tip")
@DiscriminatorValue(value="racun-vr6")
@NamedQueries({ 
    @NamedQuery(name = "Racun.findByKljuc", query = "SELECT r FROM Racun r WHERE r.kljuc = :kljuc"),
    @NamedQuery(name = "Racun.findByValuta", query = "SELECT r FROM Racun r WHERE r.valuta = :valuta"), 
    @NamedQuery(name = "Racun.findByMaticniBroj", query = "SELECT r FROM Racun r WHERE r.maticniBroj = :maticniBroj"), 
    @NamedQuery(name = "Racun.findByPorez", query = "SELECT r FROM Racun r WHERE r.porez = :porez"), 
    @NamedQuery(name = "Racun.findByOtpremnica", query = "SELECT r FROM Racun r WHERE r.otpremnica = :otpremnica"), 
    @NamedQuery(name = "Racun.findByNaziv", query = "SELECT r FROM Racun r WHERE r.naziv = :naziv"), 
    @NamedQuery(name = "Racun.findByTelefon", query = "SELECT r FROM Racun r WHERE r.telefon = :telefon"), 
    @NamedQuery(name = "Racun.findByAdresa", query = "SELECT r FROM Racun r WHERE r.adresa = :adresa"), 
    @NamedQuery(name = "Racun.findByMobitel", query = "SELECT r FROM Racun r WHERE r.mobitel = :mobitel"), 
    @NamedQuery(name = "Racun.findByUkupno", query = "SELECT r FROM Racun r WHERE r.ukupno = :ukupno"), 
    @NamedQuery(name = "Racun.findByUplata", query = "SELECT r FROM Racun r WHERE r.uplata = :uplata"), 
    @NamedQuery(name = "Racun.findByIzdan", query = "SELECT r FROM Racun r WHERE r.izdan = :izdan"), 
    @NamedQuery(name = "Racun.findByPlacen", query = "SELECT r FROM Racun r WHERE r.placen = :placen"), 
    @NamedQuery(name = "Racun.findByOznaka", query = "SELECT r FROM Racun r WHERE r.oznaka = :oznaka"), 
    @NamedQuery(name = "Racun.findByStorniran", query = "SELECT r FROM Racun r WHERE r.storniran = :storniran"), 
    @NamedQuery(name = "Racun.findByLokacija", query = "SELECT r FROM Racun r WHERE r.lokacija = :lokacija"),
    @NamedQuery(name = "Racun.findByRaspon", query = "SELECT r FROM Racun r WHERE r.izdan BETWEEN :pocetak AND :kraj ORDER BY r.izdan ASC"),
    @NamedQuery(name = "Racun.SviNeplaceni", query = "SELECT r FROM Racun r WHERE r.placen IS NULL AND r.storniran IS NULL"),
    @NamedQuery(name="Racun.Svi", query="SELECT r FROM Racun r") })
public class Racun implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "KLJUC", nullable = false)
    private String kljuc;
    @Column(name = "VALUTA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date valuta;
    @Column(name = "MATICNI_BROJ")
    private String maticniBroj;
    @Column(name = "POREZ")
    private Double porez;
    @Column(name = "OTPREMNICA")
    private String otpremnica;
    @Column(name = "NAZIV")
    private String naziv;
    @Column(name = "TELEFON")
    private String telefon;
    @Column(name = "ADRESA")
    private String adresa;
    @Column(name = "MOBITEL")
    private String mobitel;
    @Column(name = "UKUPNO")
    private Double ukupno;
    @Column(name = "UPLATA")
    private String uplata;
    @Column(name = "IZDAN")
    @Temporal(TemporalType.TIMESTAMP)
    private Date izdan;
    @Column(name = "PLACEN")
    @Temporal(TemporalType.TIMESTAMP)
    private Date placen;
    @Column(name = "OZNAKA")
    private String oznaka;
    @Column(name = "STORNIRAN")
    @Temporal(TemporalType.TIMESTAMP)
    private Date storniran;
    @Column(name = "LOKACIJA")
    private String lokacija;
    @Column(name = "KORISNIK_KLJUC")
    private String KorisnikKljuc;
    @Column(name= "NAPOMENA", length=4000)
    private String napomena;
    
    @Column(name="zaglavlje", length=2000)
    private String zaglavlje;
    
    @OneToMany(mappedBy = "racunKljuc", cascade=CascadeType.ALL)
    private List<Stavke> stavke;

    @OneToMany(mappedBy="racun", cascade=CascadeType.ALL)
    private List<PoreznaStavkaRacuna> porezneStavke;

    @OneToMany(mappedBy = "racun", cascade=CascadeType.ALL)
    private List<RacunNaplata> racunNaplate;
    
    @OneToOne
    @JoinColumn(name="template")
    private Template template;
    
    @ManyToOne
    @JoinColumn(name="operater")
    private Operater operater;
    
    @Column(name="jir")
    private String jir;
    
    @Column(name="zki")
    private String zki;
    
    @Column(name="fiskalna_lokacija")
    private String fiskalnaLokacija;
    
    @Column(name="fiskalni_uredaj")
    private String fisklaniUredaj;
    
    @Column(name="verzija")
    private int verzija;
    
    @Column(name="putanja_predloska")
    private String putanjaPredloska;
    
    @OneToMany(mappedBy = "racun", cascade=CascadeType.ALL)
    private List<Blagajna> blagajna;

    public String getPutanjaPredloska() {
        return putanjaPredloska;
    }

    public void setPutanjaPredloska(String putanjaPredloska) {
        this.putanjaPredloska = putanjaPredloska;
    }

    public List<Blagajna> getBlagajna() {
        return blagajna;
    }

    public void setBlagajna(List<Blagajna> blagajna) {
        this.blagajna = blagajna;
    }

    public String getZaglavlje() {
        return zaglavlje;
    }

    public void setZaglavlje(String zaglavlje) {
        this.zaglavlje = zaglavlje;
    }
    
    public int getVerzija() {
        return verzija;
    }

    public void setVerzija(int verzija) {
        this.verzija = verzija;
    }
    
    public String getFiskalnaLokacija() {
        return fiskalnaLokacija;
    }

    public void setFiskalnaLokacija(String fiskalnaLokacija) {
        this.fiskalnaLokacija = fiskalnaLokacija;
    }

    public String getFisklaniUredaj() {
        return fisklaniUredaj;
    }

    public void setFisklaniUredaj(String fisklaniUredaj) {
        this.fisklaniUredaj = fisklaniUredaj;
    }
    
    public String getZki() {
        return zki;
    }

    public void setZki(String zki) {
        this.zki = zki;
    }
    
    public String getJir() {
        return jir;
    }

    public void setJir(String jir) {
        this.jir = jir;
    }
    
    public Operater getOperater() {
        return operater;
    }

    public void setOperater(Operater operater) {
        this.operater = operater;
    }
    
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Double getDugovanje()
    {
        if(getPlacen() != null || getStorniran() != null)
            return 0D;
        else if(getRacunNaplate().size() == 0)
        {
            return Math.round(getUkupno() * 100) / 100D;
        }
        else
        {
            Double dugovanje = 0D;

            for(RacunNaplata naplata : getRacunNaplate())
                if(naplata.getDatumPlacanja() == null)
                    dugovanje += Math.round(naplata.getIznos() * 100) / 100D;

            return dugovanje;
        }
    }

    public List<RacunNaplata> getRacunNaplate()
    {
        if(racunNaplate == null)
            return new LinkedList<RacunNaplata>();
        else
        {
            Collections.sort(racunNaplate, new Comparator<RacunNaplata>()
            {
                public int compare(RacunNaplata o1, RacunNaplata o2)
                {
                    return o1.getDatumDospijeca().compareTo(o2.getDatumDospijeca());
                }
            });

            return racunNaplate;
        }
    }

    public void setRacunNaplate(List<RacunNaplata> racunNaplate)
    {
        this.racunNaplate = racunNaplate;
    }

    public Racun() 
    {
        this.racunNaplate = new LinkedList<RacunNaplata>();
        this.stavke = new LinkedList<Stavke>();
        this.porezneStavke = new ArrayList<PoreznaStavkaRacuna>();
        this.blagajna = new ArrayList<Blagajna>();
    }

    public Racun(String kljuc)
    {
        this.kljuc = kljuc;
        this.racunNaplate = new LinkedList<RacunNaplata>();
    }

    public String getKljuc() {
        return kljuc;
    }

    public void setKljuc(String kljuc) {
        this.kljuc = kljuc;
    }

    public Date getValuta() {
        return valuta;
    }

    public void setValuta(Date valuta) {
        this.valuta = valuta;
    }

    public String getMaticniBroj() {
        return maticniBroj;
    }

    public void setMaticniBroj(String maticniBroj) {
        this.maticniBroj = maticniBroj;
    }

    public Double getPorez() {
        return porez;
    }

    public void setPorez(Double porez) {
        this.porez = porez;
    }

    public String getOtpremnica() {
        return otpremnica;
    }

    public void setOtpremnica(String otpremnica) {
        this.otpremnica = otpremnica;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getMobitel() {
        return mobitel;
    }

    public void setMobitel(String mobitel) {
        this.mobitel = mobitel;
    }

    public Double getUkupno() {
        return ukupno;
    }

    public String getOpis()
    {
        if(getUkupno() >= 0)
            return "Ukupno";
        else
            return "Isplata";
    }

    public void setUkupno(Double ukupno) {
        this.ukupno = Math.round(ukupno * 100) / 100D;
    }

    public String getUplata() {
        return uplata;
    }

    public void setUplata(String uplata) {
        this.uplata = uplata;
    }

    public Date getIzdan() {
        return izdan;
    }

    public void setIzdan(Date izdan) {
        this.izdan = izdan;
    }

    public Date getPlacen() {
        return placen;
    }

    public void setPlacen(Date placen) {
        this.placen = placen;
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public Date getStorniran() {
        return storniran;
    }

    public void setStorniran(Date storniran) {
        this.storniran = storniran;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public List<Stavke> getStavke() {
        return stavke;
    }

    public void setStavke(List<Stavke> stavkeCollection) {
        this.stavke = stavkeCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kljuc != null ? kljuc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Racun)) {
            return false;
        }
        Racun other = (Racun) object;
        if ((this.kljuc == null && other.kljuc != null) || (this.kljuc != null && !this.kljuc.equals(other.kljuc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getOznaka();
    }

    public String getKorisnikKljuc() {
        return KorisnikKljuc;
    }

    public void setKorisnikKljuc(String KorisnikKljuc) {
        this.KorisnikKljuc = KorisnikKljuc;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String Napomena) {
        this.napomena = Napomena;
    }

    public Racun getEntity() 
    {
        return this;
    }

    public List<PoreznaStavkaRacuna> getPorezneStavke()
    {
        return porezneStavke;
    }

    public void setPorezneStavke(List<PoreznaStavkaRacuna> porezneStavke)
    {
        this.porezneStavke = porezneStavke;
    }
}
