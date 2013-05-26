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

import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.Pro3Postavke;
import Pro3x.Barcode.KCodeParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
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
import javax.persistence.Transient;


@Entity
@Table(name="kalkulacija_stavka")
@NamedQuery(name="StavkaKalkulacije.Sve", query="SELECT s FROM StavkaKalkulacije s")
public class StavkaKalkulacije implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="kljuc")
    private String id;

    @ManyToOne
    protected Roba artikal;
    public static final String PROP_ARTIKAL = "artikal";

    @Column(name="mjerna_jedinica")
    protected String mjera;
    public static final String PROP_MJERA = "mjera";

    @Column
    protected Double kolicina;
    public static final String PROP_KOLICINA = "kolicina";

    @Column
    protected Double cijena;
    public static final String PROP_CIJENA = "cijena";

    @Column
    protected Double iznos;
    public static final String PROP_IZNOS = "iznos";

    @Column
    protected Double rabat;
    public static final String PROP_RABAT = "rabat";

    @Column(name="iznos_rabata")
    protected Double iznosRabata;
    public static final String PROP_IZNOS_RABATA = "iznosRabata";

    @Column(name="zavisni_troskovi")
    protected Double zavisniTroskovi;
    public static final String PROP_ZAVISNI_TROSKOVI = "zavisniTroskovi";

    @Column
    protected Double marza;
    public static final String PROP_MARZA = "marza";

    @Column(name="iznos_marze")
    protected Double iznosMarze;
    public static final String PROP_IZNOS_MARZE = "iznosMarze";

    @Column
    protected Double osnovica;
    public static final String PROP_OSNOVICA = "osnovica";

    @Column(name="iznos_poreza")
    protected Double iznosPoreza;
    public static final String PROP_IZNOS_POREZA = "iznosPoreza";

    @Column
    protected Double ukupno;
    public static final String PROP_UKUPNO = "ukupno";

    @Column(name="cijena_bez_poreza")
    protected Double cijenaBezPoreza;
    public static final String PROP_CIJENA_BEZ_POREZA = "cijenaBezPoreza";

    @Column(name="cijena_sa_porezom")
    protected Double cijenaSaPorezom;
    public static final String PROP_CIJENA_SA_POREZOM = "cijenaSaPorezom";

    @Column(name="porezna_stopa")
    protected Double poreznaStopa;
    public static final String PROP_POREZNA_STOPA = "poreznaStopa";

    @Column(name="fakturna_vrijednost_bez_poreza")
    protected Double fakturnaBezPoreza;
    public static final String PROP_FAKTURNA_BEZ_POREZA = "fakturnaBezPoreza";

    @Column(name="fakturna_sa_porezom")
    protected Double fakturnaSaPorezom;
    public static final String PROP_FAKTURNA_SA_POREZOM = "fakturnaSaPorezom";

    @ManyToOne
    protected Kalkulacija kalkulacija;
    public static final String PROP_KALKULACIJA = "kalkulacija";
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "stavka")
    protected List<KarticaStavkeKalkulacije> kartice;
    public static final String PROP_KARTICE = "kartice";

    public KarticaStavkeKalkulacije KreirajKarticu()
    {
        KarticaStavkeKalkulacije kartica = new KarticaStavkeKalkulacije();
        kartica.setDatum(getKalkulacija().getDatumIzrade());

        kartica.setIzlaznaCijena(0D);
        kartica.setKolicinaIzlaz(0D);

        kartica.setUlaznaCijena(getCijena());
        kartica.setKolicinaUlaz(getKolicina());

        kartica.setOpis("Kalkulacija " + getKalkulacija().getOznakaKalkulacije()
                + " prema " + getKalkulacija().getOznakaDokumenta());

        kartica.setRoba(getArtikal());
        kartica.setStavka(this);

        //kartica.setStavkaKartice(null);

        return kartica;
    }

    public String getBarcode()
    {
        return KCodeParser.format(getArtikal().getSifra(), getCijenaSaPorezom());
    }

    public String getPrikazCijene()
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        try
        {
            String valuta = Pro3Postavke.getInfo().getValuta();
            return nf.format(getCijenaSaPorezom()) + " " + valuta;
        }
        catch (Exception ex)
        {
            Logger.getLogger(StavkaKalkulacije.class.getName()).log(Level.SEVERE, null, ex);
            return nf.format(getCijenaSaPorezom()) + " kn";
        }
    }

    public List<KarticaStavkeKalkulacije> getKartice()
    {
        return kartice;
    }

    public void setKartice(List<KarticaStavkeKalkulacije> kartice)
    {
        List<KarticaStavkeKalkulacije> oldKartice = this.kartice;
        this.kartice = kartice;
        propertyChangeSupport.firePropertyChange(PROP_KARTICE, oldKartice, kartice);
    }


    public Kalkulacija getKalkulacija()
    {
        return kalkulacija;
    }

    public void setKalkulacija(Kalkulacija kalkulacija)
    {
        Kalkulacija oldKalkulacija = this.kalkulacija;
        this.kalkulacija = kalkulacija;
        propertyChangeSupport.firePropertyChange(PROP_KALKULACIJA, oldKalkulacija, kalkulacija);
    }

    public Double getFakturnaSaPorezom()
    {
        return fakturnaSaPorezom;
    }

    public void setFakturnaSaPorezom(Double fakturnaSaPorezom)
    {
        Double oldFakturnaSaPorezom = this.fakturnaSaPorezom;
        this.fakturnaSaPorezom = fakturnaSaPorezom;
        firePropertyChange(PROP_FAKTURNA_SA_POREZOM, oldFakturnaSaPorezom, fakturnaSaPorezom);
    }

    public Double getFakturnaBezPoreza()
    {
        return fakturnaBezPoreza;
    }

    public void setFakturnaBezPoreza(Double fakturnaBezPoreza)
    {
        Double oldFakturnaVrijednost = this.fakturnaBezPoreza;
        this.fakturnaBezPoreza = fakturnaBezPoreza;
        firePropertyChange(PROP_FAKTURNA_BEZ_POREZA, oldFakturnaVrijednost, fakturnaBezPoreza);
    }

    public StavkaKalkulacije()
    {
        this.id = UUID.randomUUID().toString();
        this.cijena = 0D;
        this.cijenaBezPoreza = 0D;
        this.cijenaSaPorezom = 0D;
        this.iznos = 0D;
        this.iznosMarze = 0D;
        this.iznosPoreza = 0D;
        this.iznosRabata = 0D;
        this.kolicina = 0D;
        this.marza = 0D;
        this.mjera = "";
        this.osnovica = 0D;
        this.poreznaStopa = 0D;
        this.rabat = 0D;
        this.ukupno = 0D;
        this.zavisniTroskovi = 0D;
        this.fakturnaBezPoreza = 0D;
        this.fakturnaSaPorezom = 0D;
    }
    
    public Double getPoreznaStopa()
    {
        return poreznaStopa;
    }

    public void setPoreznaStopa(Double poreznaStopa)
    {
        Double oldPoreznaStopa = this.poreznaStopa;
        this.poreznaStopa = poreznaStopa;
        firePropertyChange(PROP_POREZNA_STOPA, oldPoreznaStopa, poreznaStopa);
    }

    public Double getCijenaSaPorezom()
    {
        return ZaokruziBroj(cijenaSaPorezom);
    }

    public void setCijenaSaPorezom(Double cijenaSaPorezom)
    {
        Double oldCijenaSaPorezom = this.cijenaSaPorezom;
        this.cijenaSaPorezom = cijenaSaPorezom;
        firePropertyChange(PROP_CIJENA_SA_POREZOM, oldCijenaSaPorezom, cijenaSaPorezom);

        PromjeniCijenuBezPoreza(getCijenaSaPorezom() / (1 + getPoreznaStopa()));
        KalkulacijaPremaProdajnimCijenama();
    }

    private Double ZaokruziBroj(Double broj)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        try
        {
            return nf.parse(nf.format(broj)).doubleValue();
        }
        catch (ParseException ex)
        {
            Logger.getLogger(StavkaKalkulacije.class.getName()).log(Level.SEVERE, null, ex);
            return broj;
        }
    }

    public Double getCijenaBezPoreza()
    {
        return cijenaBezPoreza;
    }

    public void setCijenaBezPoreza(Double cijenaBezPoreza)
    {
        Double oldCijenaBezPoreza = this.cijenaBezPoreza;
        this.cijenaBezPoreza = cijenaBezPoreza;
        firePropertyChange(PROP_CIJENA_BEZ_POREZA, oldCijenaBezPoreza, cijenaBezPoreza);

        PromjeniCijenuSaPorezom(getCijenaBezPoreza() * (1 + getPoreznaStopa()));
        KalkulacijaPremaProdajnimCijenama();
    }
    
    public Double getUkupno()
    {
        return ukupno;
    }

    public void setUkupno(Double ukupno)
    {
        Double oldUkupno = this.ukupno;
        this.ukupno = ukupno;
        firePropertyChange(PROP_UKUPNO, oldUkupno, ukupno);
    }

    public Double getIznosPoreza()
    {
        return iznosPoreza;
    }

    public void setIznosPoreza(Double iznosPoreza)
    {
        Double oldIznosPoreza = this.iznosPoreza;
        this.iznosPoreza = iznosPoreza;
        firePropertyChange(PROP_IZNOS_POREZA, oldIznosPoreza, iznosPoreza);
    }

    public Double getOsnovica()
    {
        return osnovica;
    }

    public void setOsnovica(Double osnovica)
    {
        Double oldOsnovica = this.osnovica;
        this.osnovica = osnovica;
        firePropertyChange(PROP_OSNOVICA, oldOsnovica, osnovica);
    }

    public Double getIznosMarze()
    {
        return iznosMarze;
    }

    public void setIznosMarze(Double iznosMarze)
    {
        Double oldIznosMarze = this.iznosMarze;
        this.iznosMarze = iznosMarze;
        firePropertyChange(PROP_IZNOS_MARZE, oldIznosMarze, iznosMarze);
    }

    public Double getMarza()
    {
        if(marza.isInfinite() || marza.isNaN())
            return 0D;
        else
            return marza;
    }

    public void setMarza(Double marza)
    {
        Double oldMarza = this.marza;
        this.marza = marza;
        firePropertyChange(PROP_MARZA, oldMarza, marza);

        PromjeniCijenuBezPoreza(getCijena() * (1 + getMarza()));
        PromjeniCijenuSaPorezom(getCijenaBezPoreza() * (1 + getPoreznaStopa()));
    }

    public Double getZavisniTroskovi()
    {
        return zavisniTroskovi;
    }

    //moze se zamjeniti sa getZavisniTroskovi
    public Double getKalkulacijaZavisnihTroskova()
    {
        return zavisniTroskovi;
    }

    public void setZavisniTroskovi(Double zavisniTroskovi)
    {
        Double oldZavisniTroskovi = this.zavisniTroskovi;
        this.zavisniTroskovi = zavisniTroskovi;

        PromjeniIznosMarze(getOsnovica() - getKalkulacijaZavisnihTroskova() - getFakturnaBezPoreza());
        
        if(getFakturnaBezPoreza() != 0)
            PromjeniMarzu(getIznosMarze() / (getFakturnaBezPoreza() + getKalkulacijaZavisnihTroskova()));
        else
            PromjeniMarzu(1D);

        firePropertyChange(PROP_ZAVISNI_TROSKOVI, oldZavisniTroskovi, zavisniTroskovi);
    }


    public Double getIznosRabata()
    {
        return iznosRabata;
    }

    public void setIznosRabata(Double iznosRabata)
    {
        Double oldIznosRabata = this.iznosRabata;
        this.iznosRabata = iznosRabata;
        firePropertyChange(PROP_IZNOS_RABATA, oldIznosRabata, iznosRabata);
    }

    public Double getRabat()
    {
        return rabat;
    }

    public void setRabat(Double rabat)
    {
        Double oldRabat = this.rabat;
        this.rabat = rabat;
        firePropertyChange(PROP_RABAT, oldRabat, rabat);

        PromjeniIznosRabata(getIznos() * getRabat());
        
        PromjeniFakturnuBezPoreza(getIznos() - getIznosRabata());
        PromjeniFakturnuSaPorezom(getFakturnaBezPoreza() * (1 + getPoreznaStopa()));
        
        PromjeniIznosMarze(getOsnovica() - getKalkulacijaZavisnihTroskova() - getFakturnaBezPoreza());
        
        if(getFakturnaBezPoreza() != 0)
            PromjeniMarzu(getIznosMarze() / (getFakturnaBezPoreza() + getKalkulacijaZavisnihTroskova()));
        else
            PromjeniMarzu(1D);
    }

    public Double getIznos()
    {
        return iznos;
    }

    public void setIznos(Double iznos)
    {
        Double oldIznos = this.iznos;
        this.iznos = iznos;
        firePropertyChange(PROP_IZNOS, oldIznos, iznos);
    }

    public Double getCijena()
    {
        return cijena;
    }

    public void setCijena(Double cijena)
    {
        Double oldCijena = this.cijena;
        this.cijena = cijena;
        firePropertyChange(PROP_CIJENA, oldCijena, cijena);

        PromjeniIznos(getCijena() * getKolicina());
        PromjeniIznosRabata(getIznos() * getRabat());

        PromjeniFakturnuBezPoreza(getIznos() - getIznosRabata());
        PromjeniFakturnuSaPorezom(getFakturnaBezPoreza() * (1 + getPoreznaStopa()));

        PromjeniIznosMarze(getOsnovica() - getKalkulacijaZavisnihTroskova() - getFakturnaBezPoreza());
        
        if(getFakturnaBezPoreza() != 0)
            PromjeniMarzu(getIznosMarze() / (getFakturnaBezPoreza() + getKalkulacijaZavisnihTroskova()));
        else
            PromjeniMarzu(1D);
    }

    public Double getKolicina()
    {
        return kolicina;
    }

    public void setKolicina(Double kolicina)
    {
        Double oldKolicina = this.kolicina;
        this.kolicina = kolicina;
        firePropertyChange(PROP_KOLICINA, oldKolicina, kolicina);

        PromjeniIznos(getCijena() * getKolicina());
        PromjeniIznosRabata(getIznos() * getRabat());
        PromjeniFakturnuBezPoreza(getIznos() - getIznosRabata());

        PromjeniOsnovicu(getCijenaBezPoreza() * getKolicina());
        PromjeniUkupno(getCijenaSaPorezom() * getKolicina());
        PromjeniIznosPoreza(getUkupno() - getOsnovica());

        PromjeniIznosMarze(getOsnovica() - getKalkulacijaZavisnihTroskova() - getFakturnaBezPoreza());

        if(getFakturnaBezPoreza() != 0)
            PromjeniMarzu(getIznosMarze() / (getFakturnaBezPoreza() + getKalkulacijaZavisnihTroskova()));
        else
            PromjeniMarzu(1D);

        PromjeniFakturnuSaPorezom(getFakturnaBezPoreza() * (1 + getPoreznaStopa()));
    }


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMjera()
    {
        return mjera;
    }

    public void setMjera(String mjera)
    {
        String oldMjera = this.mjera;
        this.mjera = mjera;
        firePropertyChange(PROP_MJERA, oldMjera, mjera);
    }


    public Roba getArtikal()
    {
        return artikal;
    }
    
    public void setArtikal(Roba artikal)
    {
        Roba oldArtikal = this.artikal;
        this.artikal = artikal;
        firePropertyChange(PROP_ARTIKAL, oldArtikal, artikal);

        PromjeniCijenu(artikal.getNabavnaCijenaBezPoreza());
        
        Double prodajnaBezPoreza = artikal.getMaloprodajnaCijenaZaokruzena() / (1 + artikal.getDecimalnaPoreznaStopa());
        PromjeniCijenuBezPoreza(prodajnaBezPoreza);
        PromjeniCijenuSaPorezom(artikal.getMaloprodajnaCijenaZaokruzena());

        PromjeniMjeru(artikal.getMjera());
        PromjeniPorezniStopu(artikal.getDecimalnaPoreznaStopa());
        
        PromjeniIznos(getCijena() * getKolicina());
        PromjeniIznosRabata(getIznos() * getRabat());
        PromjeniFakturnuBezPoreza(getIznos() - getIznosRabata());
        PromjeniFakturnuSaPorezom(getFakturnaBezPoreza() * (1 + getPoreznaStopa()));

        PromjeniIznosMarze(getKolicina() * getCijenaBezPoreza() - getKalkulacijaZavisnihTroskova() - getFakturnaBezPoreza());
        
        Double fakturna = getFakturnaBezPoreza();
        if(fakturna != 0)
            PromjeniMarzu(getIznosMarze() / (getFakturnaBezPoreza() + getKalkulacijaZavisnihTroskova()));
        else
            PromjeniMarzu(1D);

        PromjeniOsnovicu(getFakturnaBezPoreza() + getIznosMarze());
        PromjeniUkupno(getOsnovica() * (1 + getPoreznaStopa()));
        PromjeniIznosPoreza(getUkupno() - getOsnovica());
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

    private void KalkulacijaPremaProdajnimCijenama()
    {
        PromjeniOsnovicu(getCijenaBezPoreza() * getKolicina());
        PromjeniUkupno(getCijenaSaPorezom() * getKolicina());
        PromjeniIznosPoreza(getUkupno() - getOsnovica());
        PromjeniIznosMarze(getOsnovica() - getKalkulacijaZavisnihTroskova() - getFakturnaBezPoreza());
        
        if(getFakturnaBezPoreza() != 0)
            PromjeniMarzu(getIznosMarze() / (getFakturnaBezPoreza() + getKalkulacijaZavisnihTroskova()));
        else
            PromjeniMarzu(1D);
    }

    private void PromjeniMjeru(String nova)
    {
        String stara = getMjera();
        this.mjera = nova;
        firePropertyChange(PROP_MJERA, stara, nova);
    }

    private void PromjeniKolicinu(Double novaKolicina)
    {
        Double staraKolicina = getKolicina();
        this.kolicina = novaKolicina;
        firePropertyChange(PROP_KOLICINA, staraKolicina, novaKolicina);
    }

    private void PromjeniCijenu(Double novaCijena)
    {
        Double staraCijena = getCijena();
        this.cijena = novaCijena;
        firePropertyChange(PROP_CIJENA, staraCijena, novaCijena);
    }

    private void PromjeniIznos(Double noviIznos)
    {
        Double stariIznos = getIznos();
        this.iznos = noviIznos;
        firePropertyChange(PROP_IZNOS, stariIznos, noviIznos);
    }

    private void PromjeniRabat(Double noviRabat)
    {
        Double stariRabat = getRabat();
        this.rabat = noviRabat;
        firePropertyChange(PROP_RABAT, stariRabat, noviRabat);
    }

    private void PromjeniIznosRabata(Double noviIznosRabata)
    {
        Double stariIznosRabata = getIznosRabata();
        this.iznosRabata = noviIznosRabata;
        firePropertyChange(PROP_IZNOS_RABATA, stariIznosRabata, noviIznosRabata);
    }

    private void PromjeniFakturnuBezPoreza(Double nova)
    {
        Double stara = getFakturnaBezPoreza();
        this.fakturnaBezPoreza = nova;
        firePropertyChange(PROP_FAKTURNA_BEZ_POREZA, stara, nova);
    }

    private void PromjeniMarzu(Double nova)
    {
        Double stara = getMarza();
        this.marza = nova;
        firePropertyChange(PROP_MARZA, stara, nova);
    }

    private void PromjeniIznosMarze(Double nova)
    {
        Double stara = getIznosMarze();
        this.iznosMarze = nova;
        firePropertyChange(PROP_IZNOS_MARZE, stara, nova);
    }

    private void PromjeniOsnovicu(Double nova)
    {
        Double stara = getOsnovica();
        this.osnovica = nova;
        firePropertyChange(PROP_OSNOVICA, stara, nova);
    }

    private void PromjeniIznosPoreza(Double nova)
    {
        Double stara = getIznosPoreza();
        this.iznosPoreza = nova;
        firePropertyChange(PROP_IZNOS_POREZA, stara, nova);
    }

    private void PromjeniPorezniStopu(Double nova)
    {
        Double stara = getPoreznaStopa();
        this.poreznaStopa = nova;
        firePropertyChange(PROP_POREZNA_STOPA, stara, nova);
    }

    private void PromjeniUkupno(Double nova)
    {
        Double stara = getUkupno();
        this.ukupno = nova;
        firePropertyChange(PROP_UKUPNO, stara, nova);
    }

    private void PromjeniCijenuBezPoreza(Double nova)
    {
        Double stara = getCijenaBezPoreza();
        this.cijenaBezPoreza = nova;
        firePropertyChange(PROP_CIJENA_BEZ_POREZA, stara, nova);
    }

    private void PromjeniCijenuSaPorezom(Double nova)
    {
        Double stara = getCijenaSaPorezom();
        this.cijenaSaPorezom = nova;
        firePropertyChange(PROP_CIJENA_SA_POREZOM, stara, nova);
    }

    private void PromjeniFakturnuSaPorezom(Double nova)
    {
        Double stara = getFakturnaSaPorezom();
        this.fakturnaSaPorezom = nova;
        firePropertyChange(PROP_FAKTURNA_SA_POREZOM, stara, nova);
    }

    private void firePropertyChange(String property, Object stara, Object nova)
    {
        propertyChangeSupport.firePropertyChange(property, stara, nova);
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
        if (!(object instanceof StavkaKalkulacije))
        {
            return false;
        }
        StavkaKalkulacije other = (StavkaKalkulacije) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Acosoft.Processing.DataBox.Kalkulacije.StavkaKalkulacije[id=" + id + "]";
    }

}
