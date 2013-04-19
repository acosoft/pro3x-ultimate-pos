/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Acosoft.Processing.Components;

/**
 *
 * @author nonstop
 */
public class InfoSavjetnik
{
    private boolean Korisnici = true;
    private boolean Skladiste = true;
    private boolean Ispis = true;
    private boolean Dobavljaci = true;

    /**
     * @return the Korisnici
     */
    public boolean isKorisnici()
    {
        return Korisnici;
    }

    /**
     * @param Korisnici the Korisnici to set
     */
    public void setKorisnici(boolean Korisnici)
    {
        this.Korisnici = Korisnici;
    }

    /**
     * @return the Skladiste
     */
    public boolean isSkladiste()
    {
        return Skladiste;
    }

    /**
     * @param Skladiste the Skladiste to set
     */
    public void setSkladiste(boolean Skladiste)
    {
        this.Skladiste = Skladiste;
    }

    /**
     * @return the Ispis
     */
    public boolean isIspis()
    {
        return Ispis;
    }

    /**
     * @param Ispis the Ispis to set
     */
    public void setIspis(boolean Ispis)
    {
        this.Ispis = Ispis;
    }

    /**
     * @return the Dobavljaci
     */
    public boolean isDobavljaci()
    {
        return Dobavljaci;
    }

    /**
     * @param Dobavljaci the Dobavljaci to set
     */
    public void setDobavljaci(boolean Dobavljaci)
    {
        this.Dobavljaci = Dobavljaci;
    }

}
