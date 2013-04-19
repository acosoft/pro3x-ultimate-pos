/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Pro3x.Code.VrsteUplate;

/**
 *
 * @author aco
 */
public class NacinPlacanja 
{

    public NacinPlacanja() {
        this("");
    }

    public NacinPlacanja(String opis) {
        this.opis = opis;
    }

    public NacinPlacanja(VrsteUplate vrsta) {
        this.vrsta = vrsta;
        this.opis = vrsta.getOpis();
    }
    
    private String opis;
    private VrsteUplate vrsta;

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public VrsteUplate getVrsta() {
        return vrsta;
    }

    public void setVrsta(VrsteUplate vrsta) {
        this.vrsta = vrsta;
    }
}
