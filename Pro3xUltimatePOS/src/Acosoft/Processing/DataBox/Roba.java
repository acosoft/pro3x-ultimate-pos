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

import Acosoft.Processing.Pro3Postavke;
import Pro3x.Barcode.KCodeParser;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "roba")
@NamedQueries({@NamedQuery(name = "Roba.findAll", query = "SELECT r FROM Roba r"), 
    @NamedQuery(name="Roba.stanje", query="SELECT sum(k.kolicinaUlaz), sum(k.kolicinaIzlaz) FROM RobaKartica k WHERE k.roba = :roba"), 
    @NamedQuery(name = "Roba.findByKljuc", query = "SELECT r FROM Roba r WHERE r.kljuc = :kljuc"), 
    @NamedQuery(name = "Roba.findByNaziv", query = "SELECT r FROM Roba r WHERE r.naziv = :naziv"), 
    @NamedQuery(name = "Roba.findByMjera", query = "SELECT r FROM Roba r WHERE r.mjera = :mjera"), 
    @NamedQuery(name = "Roba.findByCijena", query = "SELECT r FROM Roba r WHERE r.cijena = :cijena")})
public class Roba implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "KLJUC")
    private String kljuc;
    @Column(name = "NAZIV")
    private String naziv;
    @Column(name = "MJERA")
    private String mjera;
    @Basic(optional = false)
    @Column(name = "CIJENA")
    private Double cijena = 0D;
    @Column(name = "povratna")
    private Boolean ulazniArtikal;
    @Column(name = "NABAVNA_CIJENA")
    private Double nabavnaCijena = 0D;

    @OneToMany(mappedBy = "roba", cascade = CascadeType.ALL)
    private Collection<RobaKartica> kartice;
    
    @OneToOne
    @JoinColumn(name="pdv")
    private PoreznaStopa pdv;
    
    @OneToOne
    @JoinColumn(name="pot")
    private PoreznaStopa pot;
    
    @OneToOne
    @JoinColumn(name="pdv_nabava")
    private PoreznaStopa pdvNabava;
        
    @Column(name="sifra")
    private String sifra;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="grupa")
    private GrupaArtikala grupa;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "artikal")
    private List<ArtikalNormativ> normativi;
    
    @OneToMany(mappedBy = "artikal", cascade=CascadeType.ALL)
    private List<PromjenaCijene> promjeneCijena;

    @Column(name="deklaracija", length=4000)
    protected String deklaracija;

    @Transient
    protected boolean promjenaDeklaracije;

    public boolean isPromjenaDeklaracije()
    {
        return promjenaDeklaracije;
    }

    public void setPromjenaDeklaracije(boolean promjenaDeklaracije)
    {
        this.promjenaDeklaracije = promjenaDeklaracije;
    }

    public String getDeklaracija()
    {
        return deklaracija;
    }

    public void setDeklaracija(String deklaracija)
    {
        this.deklaracija = deklaracija;
        this.promjenaDeklaracije = true;
    }
    
    public boolean isPdvDef()
    {
        return getPdv() != null;
    }
    
    public boolean isPotDef()
    {
        return getPot() != null;
    }

    public PoreznaStopa getPdvNabava() {
        return pdvNabava;
    }

    public void setPdvNabava(PoreznaStopa pdvNabava) {
        this.pdvNabava = pdvNabava;
    }
    
    public boolean isPdvNabavaDef()
    {
        return getPdvNabava() != null;
    }

    public Roba()
    {
        this(UUID.randomUUID().toString(), 0D);
    }

    public String getPrikazCijene()
    {
        NumberFormat nf = NumberFormat.getInstance();
        
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        String valuta;
        try
        {
            valuta = Pro3Postavke.getInfo().getValuta();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Roba.class.getName()).log(Level.SEVERE, null, ex);
            valuta = "kn";
        }

        return nf.format(getMaloprodajnaCijena()) +  " " + valuta;
    }

    public List<ArtikalNormativ> getNormativi()
    {
        return normativi;
    }

    public void setNormativi(List<ArtikalNormativ> normativi)
    {
        this.normativi = normativi;
    }

    public void setSifra(String sifra)
    {
        this.sifra = sifra;
    }

    public String getSifra()
    {
        return sifra;
    }

    public String getBarcode()
    {
        return KCodeParser.format(getSifra(), getMaloprodajnaCijena());
    }

    public void setGrupa(GrupaArtikala grupa)
    {
        this.grupa = grupa;
    }

    public GrupaArtikala getGrupa()
    {
        return this.grupa;
    }

    public Roba(String kljuc)
    {
        this(kljuc, 0D);
    }

    public Roba(String kljuc, Double cijena)
    {
        this.kljuc = kljuc;
        this.cijena = cijena;

        promjeneCijena = new LinkedList<PromjenaCijene>();
        normativi = new ArrayList<ArtikalNormativ>();
    }
    
    public String getKljuc()
    {
        return kljuc;
    }

    public void setKljuc(String kljuc)
    {
        this.kljuc = kljuc;
    }

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        this.naziv = naziv;
    }

    public String getMjera()
    {
        return mjera;
    }

    public void setMjera(String mjera)
    {
        this.mjera = mjera;
    }

    public Double getVrijednostNormativa()
    {
        Double suma = 0D;
        
        for(ArtikalNormativ normativ : getNormativi())
            suma += normativ.getKolicina() * normativ.getNormativ().getMaloprodajnaCijena();

        return suma;
    }
    
    public Double getCijenaBezPoreza()
    {
        return cijena;
    }

    public Double getCijena()
    {
        double temp = cijena * (1 + getDecimalnaPoreznaStopa());
        return Math.round(temp * 100) / 100D;
    }

    public Double getDecimalnaPoreznaStopa()
    {
        double stopa = 0;
        
        if(getPdv() != null)
            stopa += getPdv().getNormaliziraniPostotak();
        
        if(getPot() != null)
            stopa += getPot().getNormaliziraniPostotak();
        
        return stopa;
    }

    public Double getMaloprodajnaCijena()
    {
        return cijena * (1 + getDecimalnaPoreznaStopa());
    }

    public Double getMaloprodajnaCijenaZaokruzena()
    {
        return Math.round(getMaloprodajnaCijena() * 100) / 100D;
    }

    public void setCijena(Double cijena)
    {
        this.cijena = cijena / (1 + getDecimalnaPoreznaStopa());

        PromjenaCijene zapisnik = new PromjenaCijene();

        zapisnik.setArtikal(this);
        zapisnik.setMaloprodajnaCijena(getMaloprodajnaCijenaZaokruzena());
        zapisnik.setPocetak(Calendar.getInstance().getTime());

        getPromjeneCijena().add(zapisnik);
    }

    public void setMaloprodajnaCijenaSaPorezom(double cijena)
    {
        setCijena(cijena);
    }

    public Double getNabavnaCijenaBezPoreza()
    {
        return nabavnaCijena;
    }

    @Override
    public String toString()
    {
        //return "Acosoft.Processing.Data.Roba[kljuc=" + kljuc + "]";
        return naziv;
    }

    public Double getNabavnaCijena() 
    {
        if(isPdvNabavaDef())
        {
            double temp = nabavnaCijena * getPdvNabava().getNormaliziranaPoreznaStopa();
            return Math.round(temp * 100) / 100D;
        }
        else
        {
            return Math.round(nabavnaCijena * 100) / 100D;
        }
    }

    public void setNabavnaCijena(Double nabavnaCijena) {
        
        if(isPdvNabavaDef())
        {
            double temp = nabavnaCijena / getPdvNabava().getNormaliziranaPoreznaStopa();
            this.nabavnaCijena = Math.round(temp * 100) / 100D;
        }
        else
        {
            this.nabavnaCijena =  Math.round(nabavnaCijena * 100) / 100D;
        }
        
    }

    public Collection<RobaKartica> getKartice() {
        return kartice;
    }

    public void setKartice(Collection<RobaKartica> kartice) {
        this.kartice = kartice;
    }

    public Boolean getUlazniArtikal() {
        return ulazniArtikal;
    }

    public void setUlazniArtikal(Boolean ulazniArtikal) {
        this.ulazniArtikal = ulazniArtikal;
    }

    public PoreznaStopa getPdv() {
        return pdv;
    }

    public void setPdv(PoreznaStopa pdv) {
        this.pdv = pdv;
    }

    public PoreznaStopa getPot() {
        return pot;
    }

    public void setPot(PoreznaStopa pot) {
        this.pot = pot;
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
        final Roba other = (Roba) obj;
        if ((this.kljuc == null) ? (other.kljuc != null) : !this.kljuc.equals(other.kljuc))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + (this.kljuc != null ? this.kljuc.hashCode() : 0);
        return hash;
    }

    /**
     * @return the promjeneCijena
     */
    public List<PromjenaCijene> getPromjeneCijena()
    {
        return promjeneCijena;
    }

    /**
     * @param promjeneCijena the promjeneCijena to set
     */
    public void setPromjeneCijena(List<PromjenaCijene> promjeneCijena)
    {
        this.promjeneCijena = promjeneCijena;
    }

    public Double getKolicina(Date datum)
    {
        Double stanje = 0D;

        for(RobaKartica kartica : getKartice())
        {
            if(!kartica.isStornoKartica() && kartica.getDatum().compareTo(datum) <= 0)
            {
                stanje += kartica.getKolicinaUlaz();
                stanje -= kartica.getKolicinaIzlaz();
            }
        }

        return stanje;
    }
    
    public Double izracunajMarzu()
    {
        if(nabavnaCijena == 0)
        {
            return 0D;
        }
        else
        {
            return (getCijenaBezPoreza() - getNabavnaCijenaBezPoreza()) / getNabavnaCijenaBezPoreza();
        }
    }
}
