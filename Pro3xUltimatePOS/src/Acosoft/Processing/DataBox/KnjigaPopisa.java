/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Acosoft.Processing.DataBox;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="knjiga_popisa")
@NamedQueries({@NamedQuery(name = "KnjigaPopisa.findAll", query = "SELECT k FROM KnjigaPopisa k ORDER BY k.datum ASC")})
public class KnjigaPopisa implements Serializable 
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="oznaka")
    private int oznaka;
    @Transient
    private int redniBroj;
    @Column(name="datum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @Column(name="dokument")
    private String dokument;
    @Column(name="zaduzenje", precision=20, scale=2)
    private BigDecimal zaduzenje;
    @Column(name="promet", precision=20, scale=2)
    private BigDecimal promet;
    @Transient
    private BigDecimal stanje;
    @Transient
    private KnjigaPopisa prethodniUnos;

    public KnjigaPopisa() {
        stanje = new BigDecimal(0);
        zaduzenje = new BigDecimal(0);
        promet = new BigDecimal(0);
        datum = Calendar.getInstance().getTime();
    }
    
    public KnjigaPopisa getPrethodniUnos() {
        return prethodniUnos;
    }

    public void setPrethodniUnos(KnjigaPopisa prethodniUnos) {
        this.prethodniUnos = prethodniUnos;
        this.stanje = this.stanje.add(prethodniUnos.stanje);
    }
    
    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getDokument() {
        return dokument;
    }

    public void setDokument(String dokument) {
        this.dokument = dokument;
    }

    public int getOznaka() {
        return oznaka;
    }

    public void setOznaka(int oznaka) {
        this.oznaka = oznaka;
    }

    public BigDecimal getPromet() {
        return promet;
    }

    public void setPromet(BigDecimal promet) {
        this.promet = promet;
    }
    
    public void setPromet(double promet)
    {
        this.promet = new BigDecimal(promet);
        this.promet = this.promet.setScale(2, RoundingMode.DOWN);
    }

    public int getRedniBroj() {
        return redniBroj;
    }

    public void setRedniBroj(int redniBroj) {
        this.redniBroj = redniBroj;
    }

    public BigDecimal getStanje() {
        return stanje;
    }

    public BigDecimal getZaduzenje() {
        return zaduzenje;
    }

    public void setZaduzenje(BigDecimal zaduzenje) {
        this.zaduzenje = zaduzenje;
    }
    
    public void setZaduzenje(double iznos)
    {
        zaduzenje = new BigDecimal(iznos);
        zaduzenje = zaduzenje.setScale(2, RoundingMode.UP);
    }
}
