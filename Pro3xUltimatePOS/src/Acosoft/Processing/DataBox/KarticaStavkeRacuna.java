/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Acosoft.Processing.DataBox;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Aco
 */
@Entity
@DiscriminatorValue(value="racun")
public class KarticaStavkeRacuna extends RobaKartica implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="stavka_racuna")
    protected Stavke stavka;
    
    public Stavke getStavka() {
        return stavka;
    }

    public void setStavka(Stavke stavka) {
        this.stavka = stavka;
    }

    public void Storniraj()
    {
        setKolicinaUlaz(getKolicinaIzlaz());
        setUlaznaCijena(getIzlaznaCijena());
    }
    
    @Override
    public String toString() {
        return "Acosoft.Processing.DataBox.KarticaStavkeRacuna[id=" + getSid() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KarticaStavkeRacuna other = (KarticaStavkeRacuna) obj;
        if (this.sid != other.sid && (this.sid == null || !this.sid.equals(other.sid))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.sid != null ? this.sid.hashCode() : 0);
        return hash;
    }
}
