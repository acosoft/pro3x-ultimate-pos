/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.Calculations;

import Pro3x.Kalkulacije.Model.StavkaKalkulacije;
import Pro3x.Persistence;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author Aco
 */
public class PromjenaModelaZaokruzivanjaStavkiKalkulacija extends Task
{

    public PromjenaModelaZaokruzivanjaStavkiKalkulacija(Application application)
    {
        super(application);
    }

    private Double ZaokruziBroj(Double broj)
    {
        return Math.round(broj * 100) / 100D;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage("Pripremam vezu sa bazom podataka");
        EntityManager manager = Persistence.createEntityManagerFactory().createEntityManager();

        setMessage("Učitavam stavke kalkulacija");
        Query query = manager.createNamedQuery("StavkaKalkulacije.Sve");
        List<StavkaKalkulacije> stavke = query.getResultList();
        int count = stavke.size();
        int trenutna = 1;

        manager.getTransaction().begin();
        for(StavkaKalkulacije stavka : stavke)
        {
            setMessage(MessageFormat.format("Obrađujem stavku {0} od {1}", trenutna++, count));
            stavka.setCijenaSaPorezom(ZaokruziBroj(stavka.getCijenaSaPorezom()));
            stavka.setKolicina(stavka.getKolicina());
            manager.persist(stavka);

            Thread.sleep(25);
        }
        manager.getTransaction().commit();
        setMessage("Sve stavke su uspješno obrađene");
        return null;
    }
}
