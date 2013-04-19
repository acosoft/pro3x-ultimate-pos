/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

/**
 *
 * @author aco
 */
public class StavkaPrometa {
    
    private String naziv;
    private double kolicina;
    private double cijena;
    private double popust;
    private double ukupno;

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public double getKolicina() {
        return kolicina;
    }

    public void setKolicina(double kolicina) {
        this.kolicina = kolicina;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public double getPopust() {
        return popust;
    }

    public void setPopust(double popust) {
        this.popust = popust;
    }

    public double getUkupno() {
        return ukupno;
    }

    public void setUkupno(double ukupno) {
        this.ukupno = ukupno;
    }
}
