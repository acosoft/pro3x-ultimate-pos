/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.Code;

import Acosoft.Processing.DataBox.PromjenaCijene;
import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.DataBox.RobaKartica;
import Acosoft.Processing.DataBox.Stavke;
import Acosoft.Processing.Pro3App;
import Pro3x.Zapisnici.PregledZapisnikaPromjeneCijena;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.SwingUtilities;
import org.jdesktop.application.Task;

public class TaskPromjeneCijena extends Task
{
    private Date pocetak;
    private Date kraj;
    private EntityManager manager;
    private HashMap<Roba, List<PromjenaCijene>> promjene = new HashMap<Roba, List<PromjenaCijene>>();
    private List<ZapisnikPromjeneCijene> zapisnici = new LinkedList<ZapisnikPromjeneCijene>();
    private PregledZapisnikaPromjeneCijena pregled;

    public TaskPromjeneCijena(Date pocetak, Date kraj, PregledZapisnikaPromjeneCijena pregled)
    {
        super(Pro3App.getApplication());
        
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.pregled = pregled;
    }

    private void PromjeniPregled()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if(pregled != null) pregled.setZapisnici(zapisnici);
            }
        });
    }

    private void Pauza()
    {
        Pauza(10);
    }

    private void Pauza(long vrijeme)
    {
        try
        {
            Thread.sleep(vrijeme);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(TaskPromjeneCijena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage("Pripremam vezu sa bazom podataka.");
        Pauza();

        manager = Pro3x.Persistence.createEntityManagerFactory().createEntityManager();
        Query queryKartice = manager.createNamedQuery("Stavke.findByPopust");

        queryKartice.setParameter("pocetak", pocetak);
        queryKartice.setParameter("kraj", kraj);

        setMessage("Pripremam kartice artikala.");
        Pauza();

        List<Stavke> stavke = queryKartice.getResultList();

        setMessage("Pripremam povijest promjene cijena.");
        Pauza();
       
        int count = stavke.size() ;
        int trenutna = 0;

        for(Stavke stavka : stavke)
        {
            setMessage(MessageFormat.format("Obrađujem stavku {0} od {1}.", ++trenutna, count));
            setProgress(trenutna, 1, count + 1);
            Pauza();

            setMessage("Kreiram zapisnik o promjeni cijene artikla " + stavka.getRoba());
            Pauza();

            ZapisnikPromjeneCijene zapisnik = new ZapisnikPromjeneCijene();

            zapisnik.setDatum(stavka.getRacunKljuc().getIzdan());
            zapisnik.setArtikal(stavka.getRobaInfo());
            zapisnik.setKolicina(stavka.getKolicina());
            zapisnik.setStaraCijena(stavka.getMaloprodajnaCijena() + stavka.getPopust());
            zapisnik.setNovaCijena(stavka.getMaloprodajnaCijena());
            
            //TODO: potrebna nova implementacija zbog promjene načina rada sa normativima
            //zapisnik.setKartica(stavka.getKartice());

            zapisnici.add(zapisnik);

            PromjeniPregled();
        }
        
        setMessage(MessageFormat.format("Završeno kreiranje zapisnika. Ukupno kreirano {0} zapisnika o promjeni cijena.", count));

        manager.close();
        return null;
    }

    @Override
    protected void succeeded(Object result)
    {
        if(pregled != null) PromjeniPregled();
        super.succeeded(result);
    }
}
