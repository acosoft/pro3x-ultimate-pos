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
public class ZapisKnjigePopisa
{

    public ZapisKnjigePopisa()
    {
        this.setZaduzenje(0D);
        this.setRazduzenje(0D);
    }

    protected Date datum;
    public static final String PROP_DATUM = "datum";

    protected ZapisDokument Dokument;
    public static final String PROP_DOKUMENT = "Dokument";

    protected Double zaduzenje;
    public static final String PROP_ZADUZENJE = "zaduzenje";

    protected Double razduzenje;
    public static final String PROP_RAZDUZENJE = "razduzenje";

    public Double getRazduzenje()
    {
        return razduzenje;
    }

    public void setRazduzenje(Double razduzenje)
    {
        Double oldRazduzenje = this.razduzenje;
        this.razduzenje = razduzenje;
        propertyChangeSupport.firePropertyChange(PROP_RAZDUZENJE, oldRazduzenje, razduzenje);
    }

    public Double getZaduzenje()
    {
        return zaduzenje;
    }

    public void setZaduzenje(Double zaduzenje)
    {
        Double oldZaduzenje = this.zaduzenje;
        this.zaduzenje = zaduzenje;
        propertyChangeSupport.firePropertyChange(PROP_ZADUZENJE, oldZaduzenje, zaduzenje);
    }

    public ZapisDokument getDokument()
    {
        return Dokument;
    }

    public void setDokument(ZapisDokument Dokument)
    {
        ZapisDokument oldDokument = this.Dokument;
        this.Dokument = Dokument;
        propertyChangeSupport.firePropertyChange(PROP_DOKUMENT, oldDokument, Dokument);
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
