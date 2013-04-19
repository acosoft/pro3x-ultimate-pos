/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.KnjigaPopisa;

import Acosoft.Processing.DataBox.KarticaStavkeRacuna;
import Acosoft.Processing.DataBox.Racun;
import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.DataBox.RobaKartica;
import Pro3x.Kalkulacije.Model.Kalkulacija;
import Pro3x.Kalkulacije.Model.KarticaStavkeKalkulacije;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author Aco
 */
public class TaskPripremaKnjigePopisa extends Task<Object, Object>
{
    public static final int DNEVNI_PROMET_VRIJEME = 20;
    private static final int TIMEOUT = 10;
    private EntityManager manager = Pro3x.Persistence.createEntityManagerFactory().createEntityManager();

    public TaskPripremaKnjigePopisa(Application application)
    {
        super(application);
    }

    private boolean IsteCijene(double stara, double nova)
    {
        final int zaokruzivanje = 100;
        return Math.round(stara * zaokruzivanje) == Math.round(nova * zaokruzivanje);
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage("Pripremam knjigu popisa. Pripremanje podataka može potrajati i nekoliko minuta.");
        Thread.sleep(3000);

        List<Roba> artikli = manager.createNamedQuery("Roba.findAll").getResultList();
        List<ZapisKnjigePopisa> knjigaPopisa = new LinkedList<ZapisKnjigePopisa>();

        int brojArtikala = artikli.size();
        int index = 1;

        for(Roba artikal : artikli)
        {
            Double cijena = 0D;
            Double kolicina = 0D;
            int indexKartice = 1;
            boolean imaCijenu = false;

            List<RobaKartica> kartice = (List<RobaKartica>) artikal.getKartice();
            SortirajKarticaArtikala(kartice);
            int brojKartica = kartice.size();
            
            setMessage(MessageFormat.format("Pripremam artikal {0}/{1} {2}", index, brojArtikala, artikal.getNaziv()));
            if(brojArtikala > 0) setProgress(index++, 1, brojArtikala);

            for(RobaKartica kartica : kartice)
            {
                //setMessage(MessageFormat.format("Pripremam karticu {0}/{1} artikla {2}", indexKartice++, brojKartica, artikal.getNaziv()));
                kolicina += kartica.getKolicinaUlaz() - kartica.getKolicinaIzlaz();
                Thread.sleep(TIMEOUT);

                if(kartica instanceof KarticaStavkeKalkulacije)
                {
                    KarticaStavkeKalkulacije karticaKalkulacije = (KarticaStavkeKalkulacije) kartica;
                    Double novaCijena = karticaKalkulacije.getStavka().getCijenaSaPorezom();

                    if(imaCijenu == false)
                    {
                        cijena = novaCijena;
                        imaCijenu = true;
                    }
                    else if(!IsteCijene(cijena, novaCijena))
                    {
                        ZapisKnjigePopisa zapis = new ZapisKnjigePopisa();
                        zapis.setDatum(karticaKalkulacije.getDatum());
                        zapis.setDokument(new DokumentGeneric("Kalkulacija. Promjena cijene"));

                        //izmjena cijene po kalkulaciji, zapisnik za trenutnu količinu na skladištu
                        if(novaCijena > cijena)
                            zapis.setZaduzenje((novaCijena - cijena) * kolicina);
                        else
                            zapis.setRazduzenje((cijena - novaCijena) * kolicina);

                        knjigaPopisa.add(zapis);
                        cijena = novaCijena;
                    }
                }
                else if(kartica instanceof KarticaStavkeRacuna)
                {
                    Double cijenaStavke = kartica.getIzlaznaCijena();

                    if(!IsteCijene(cijena, cijenaStavke))
                    {
                        ZapisKnjigePopisa zapis = new ZapisKnjigePopisa();
                        zapis.setDatum(kartica.getDatum());
                        zapis.setDokument(new DokumentGeneric("Račun. Promjena cijene"));

                        //izmjena cijene po računu, zapisnik samo za prodanu količinu robe
                        if(cijenaStavke > cijena)
                            zapis.setZaduzenje((cijenaStavke - cijena) * kartica.getKolicinaIzlaz());
                        else
                            zapis.setRazduzenje((cijena - cijenaStavke) * kartica.getKolicinaIzlaz());

                        knjigaPopisa.add(zapis);
                    }
                }
            }
        }

        DodajDnevnePromete(knjigaPopisa);
        DodajKalkulacije(knjigaPopisa);
        SortirajZapise(knjigaPopisa);

        return knjigaPopisa;
    }

    private void SortirajKarticaArtikala(List<RobaKartica> kartice)
    {
        Collections.sort(kartice, new Comparator<RobaKartica>() {

            public int compare(RobaKartica o1, RobaKartica o2)
            {
                return o1.getDatum().compareTo(o2.getDatum());
            }
        });
    }

    private void DodajDnevnePromete(List<ZapisKnjigePopisa> knjigaPopisa) throws InterruptedException, ParseException
    {
        String opis = "Pripremam dnevne promete po računima";
        setMessage(opis);
        setProgress(0);

        Thread.sleep(TIMEOUT);

        List<Racun> racuni = manager.createNamedQuery("Racun.Svi").getResultList();
        HashMap<String, Double> dnevniPromet = new HashMap<String, Double>();
        HashMap<String, Double> r1Promet = new HashMap<String, Double>();

        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");

        int brojRacuna = racuni.size();
        int index = 1;

        //izračun dnevnih prometa
        for(Racun racun : racuni)
        {
            setMessage(opis);
            setProgress(index, 1, brojRacuna);
            Thread.sleep(TIMEOUT);

            String datum = sf.format(racun.getIzdan());

            if(racun.getStorniran() == null && MaloprodajniRacun(racun) == true)
            {
                Double promet = dnevniPromet.get(datum);
                if(promet == null) promet = 0D;

                dnevniPromet.put(datum, promet + racun.getUkupno());
            }
            else if(racun.getStorniran() == null && MaloprodajniRacun(racun) == false)
            {
                Double promet = r1Promet.get(datum);
                if(promet == null) promet = 0D;

                r1Promet.put(datum, promet + racun.getUkupno());
            }
        }

        //zapisi dnevnih prometa
        for(String datum : dnevniPromet.keySet())
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sf.parse(datum));
            cal.set(Calendar.HOUR, DNEVNI_PROMET_VRIJEME);
            
            ZapisKnjigePopisa zapis = new ZapisKnjigePopisa();
            zapis.setDatum(cal.getTime());
            zapis.setDokument(new DokumentGeneric("Dnevni promet"));
            zapis.setRazduzenje(dnevniPromet.get(datum));

            knjigaPopisa.add(zapis);
        }

        //zapisi bezgotovinskom prometa
        for(String datum : r1Promet.keySet())
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sf.parse(datum));
            cal.set(Calendar.HOUR, DNEVNI_PROMET_VRIJEME);

            ZapisKnjigePopisa zapis = new ZapisKnjigePopisa();
            zapis.setDatum(cal.getTime());
            zapis.setDokument(new DokumentGeneric("Bezgotovinski R1 promet"));
            zapis.setRazduzenje(r1Promet.get(datum));

            knjigaPopisa.add(zapis);
        }
    }

    private void DodajKalkulacije(List<ZapisKnjigePopisa> knjigaPopisa) throws InterruptedException
    {
        String opis = "Pripremam kalkulacije";
        setMessage(opis);
        setProgress(0);
        Thread.sleep(TIMEOUT);

        List<Kalkulacija> kalkulacije = manager.createNamedQuery("Kalkulacija.Sve").getResultList();
        int brojKalkulacija = kalkulacije.size();
        int index = 1;

        for(Kalkulacija kalkulacija : kalkulacije)
        {
            setMessage(opis);
            setProgress(index, 1, brojKalkulacija);
            Thread.sleep(TIMEOUT);

            ZapisKnjigePopisa zapis = new ZapisKnjigePopisa();
            zapis.setDatum(kalkulacija.getDatumIzrade());
            zapis.setDokument(new DokumentGeneric(MessageFormat.format("Kalkulacija {0} prema {1}", kalkulacija.getOznakaKalkulacije(), kalkulacija.getOznakaDokumenta())));
            zapis.setZaduzenje(kalkulacija.getUkupnaProdajnaVrijednost());

            knjigaPopisa.add(zapis);
        }

    }

    private void SortirajZapise(List<ZapisKnjigePopisa> knjigaPopisa)
    {
        Collections.sort(knjigaPopisa, new Comparator<ZapisKnjigePopisa>()
        {
            public int compare(ZapisKnjigePopisa o1, ZapisKnjigePopisa o2)
            {
                return o1.getDatum().compareTo(o2.getDatum());
            }
        });
    }

    private boolean MaloprodajniRacun(Racun racun)
    {
        if(racun.getMaticniBroj().isEmpty())
            return true;
        else
            return false;
    }

}
