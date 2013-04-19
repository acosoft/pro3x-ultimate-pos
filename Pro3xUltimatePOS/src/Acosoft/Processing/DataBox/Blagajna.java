/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Acosoft.Processing.DataBox;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

@Entity
@Table(name="blagajna")
public class Blagajna implements Serializable {

    public Blagajna() {
        opis = "";
        ulaz = 0D;
        izlaz = 0D;
        racun = null;
        stanje = 0D;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String opis;
    
    @Column
    private Double ulaz;
    
    @Column
    private Double izlaz;
    
    @Column
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date datum;
    
    @Transient
    private Blagajna prethodna;
    
    @Transient
    private Double stanje;
    
    @ManyToOne
    private Racun racun;

    public Blagajna getPrethodna() {
        return prethodna;
    }

    public void setPrethodna(Blagajna prethodna) {
        
        if(prethodna == null)
        {
            stanje = 0D;
        }
        else
        {
            this.prethodna = prethodna;
            stanje = prethodna.getStanje();
        }
        
        stanje += this.getUlaz() - this.getIzlaz();
    }

    public Double getStanje()
    {
        return stanje;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Double getIzlaz() {
        return izlaz;
    }

    public void setIzlaz(Double izlaz) {
        this.izlaz = izlaz;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Racun getRacun() {
        return racun;
    }

    public void setRacun(Racun racun) {
        this.racun = racun;
        this.setUlaz(racun.getUkupno());
        this.setDatum(racun.getIzdan());
        this.setOpis("Racun " + racun.getOznaka());
    }

    public Double getUlaz() {
        return ulaz;
    }

    public void setUlaz(Double ulaz) {
        this.ulaz = ulaz;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
