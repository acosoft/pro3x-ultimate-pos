/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.Fiscal;

import java.util.Date;

/**
 *
 * @author aco
 */
public class LocationInfo {
    private String naziv;
    private String oznakaUredaja;
    private String ulica;
    private String broj;
    private String dodatakBroju;
    private String postanskiBroj;
    private String grad;
    private String naselje;
    private String radnoVrijeme;
    private String oibTvrtke;
    private Date datumPromjene;
    private boolean pdvObveznik;

    public LocationInfo() {
        pdvObveznik = false;
    }

    public String getOznakaUredaja() {
        return oznakaUredaja;
    }

    public void setOznakaUredaja(String oznakaUredaja) {
        this.oznakaUredaja = oznakaUredaja;
    }
    
    public boolean isPdvObveznik() {
        return pdvObveznik;
    }

    public void setPdvObveznik(boolean pdvObveznik) {
        this.pdvObveznik = pdvObveznik;
    }
    
    public Date getDatumPromjene() {
        return datumPromjene;
    }

    public void setDatumPromjene(Date datumPromjene) {
        this.datumPromjene = datumPromjene;
    }
    
    public String getOibTvrtke() {
        return oibTvrtke;
    }

    public void setOibTvrtke(String oibTvrtke) {
        this.oibTvrtke = oibTvrtke;
    }
    
    public String getBroj() {
        return broj;
    }

    public void setBroj(String broj) {
        this.broj = broj;
    }

    public String getDodatakBroju() {
        return dodatakBroju;
    }

    public void setDodatakBroju(String dodatakBroju) {
        this.dodatakBroju = dodatakBroju;
    }

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
    }

    public String getNaselje() {
        return naselje;
    }

    public void setNaselje(String naselje) {
        this.naselje = naselje;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getPostanskiBroj() {
        return postanskiBroj;
    }

    public void setPostanskiBroj(String postanskiBroj) {
        this.postanskiBroj = postanskiBroj;
    }

    public String getRadnoVrijeme() {
        return radnoVrijeme;
    }

    public void setRadnoVrijeme(String radnoVrijeme) {
        this.radnoVrijeme = radnoVrijeme;
    }

    public String getUlica() {
        return ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }
}
