/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.KnjigaPopisa;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

/**
 *
 * @author Aco
 */
public class CijenaArtikla
{

    protected Date datum;
    public static final String PROP_DATUM = "datum";

    protected Double cijena;
    public static final String PROP_CIJENA = "cijena";

    public Double getCijena()
    {
        return cijena;
    }

    public void setCijena(Double cijena)
    {
        Double oldCijena = this.cijena;
        this.cijena = cijena;
        propertyChangeSupport.firePropertyChange(PROP_CIJENA, oldCijena, cijena);
    }

    public Date getDatum()
    {
        return datum;
    }

    public void setDatum(Date datum)
    {
        Date oldDatum = this.datum;
        this.datum = datum;
        propertyChangeSupport.firePropertyChange(PROP_DATUM, oldDatum, datum);
    }

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
