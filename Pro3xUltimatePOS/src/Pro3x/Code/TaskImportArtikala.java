/*
 * Pro3x Community project
 * Copyright (C) 2009  Aleksandar Zgonjan
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, http://www.gnu.org/licenses/gpl.html
 * and open the template in the editor.
 */

package Pro3x.Code;

import Acosoft.Processing.DataBox.GrupaArtikala;
import Acosoft.Processing.DataBox.PoreznaStopa;
import Acosoft.Processing.DataBox.Roba;
import Pro3x.Live.ArtikalEvents;
import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class TaskImportArtikala extends Task
{
    private File xf;

    public TaskImportArtikala(Application application, File file)
    {
        super(application);
        xf = TaskExportArtikala.ProvjeriExtenziju(file);
    }
    
    private PoreznaStopa UcitajPoreznuStopu(List<PoreznaStopa> stope, Element artikal)
    {
        String naziv = artikal.getAttribute(TaskExportArtikala.POREZ);
        PoreznaStopa stopa = null;

        for(PoreznaStopa temp : stope)
        {
            if(temp.getOpis().equals(naziv))
            {
                stopa = temp;
                break;
            }
        }

        return stopa;
    }

    private GrupaArtikala UcitajGrupu(List<GrupaArtikala> grupe, Element artikal)
    {
        String naziv = artikal.getAttribute(TaskExportArtikala.GRUPA);
        GrupaArtikala grupa = null;

        for(GrupaArtikala temp : grupe)
        {
            if(temp.getNaziv().equals(naziv))
            {
                grupa = temp;
                break;
            }
        }

        return grupa;
    }

    private Roba UcitajArtikal(List<Roba> robe, Element artikal, GrupaArtikala grupa, PoreznaStopa stopa)
    {
        String naziv = artikal.getAttribute(TaskExportArtikala.NAZIV);
        Roba roba = null;

        for(Roba temp : robe)
        {
            if(temp.getNaziv().equals(naziv))
            {
                roba = temp;
                break;
            }
        }

        if(roba == null)
        {
            roba = new Roba();

            roba.setPdv(stopa);
            roba.setCijena(Double.parseDouble(artikal.getAttribute(TaskExportArtikala.PRODAJNA)));
            roba.setGrupa(grupa);
            roba.setKljuc(UUID.randomUUID().toString());
            roba.setMjera(artikal.getAttribute(TaskExportArtikala.MJERA));
            roba.setNabavnaCijena(Double.parseDouble(artikal.getAttribute(TaskExportArtikala.NABAVNA)));
            roba.setNaziv(naziv);
            roba.setUlazniArtikal(false);
            roba.setSifra(artikal.getAttribute(TaskExportArtikala.SIFRA));

            return roba;
        }
        else
            return null;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage("Učitavam datoteku: " + xf.getAbsolutePath());
        Document xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xf);

        EntityManager proManager = Pro3x.Persistence.createEntityManagerFactory().createEntityManager();
        setMessage("Učitavam popis artikala");
        List<Roba> dbart = proManager.createNamedQuery("Roba.findAll").getResultList();
        List<GrupaArtikala> dbgrupe = proManager.createNamedQuery("GrupaArtikala.SveGrupe").getResultList();
        List<PoreznaStopa> dbstope = proManager.createNamedQuery("Porez.SveStope").getResultList();

        proManager.getTransaction().begin();

        NodeList xart = xdoc.getDocumentElement().getChildNodes();
        int count = xart.getLength();
        for(int i=0; i<count; i++)
        {
            Node nart = xart.item(i);
            if(nart instanceof Element)
            {
                Element eart = (Element)nart;
                setMessage("Učitavam artikal: " + eart.getAttribute(TaskExportArtikala.NAZIV));
                PoreznaStopa stopa = UcitajPoreznuStopu(dbstope, eart);
                GrupaArtikala grupa = UcitajGrupu(dbgrupe, eart);
                Roba roba = UcitajArtikal(dbart, eart, grupa, stopa);

                if(roba != null) 
                {
                    proManager.persist(roba);
                    setMessage("Kreiran artikal: " + roba.getNaziv());
                    ArtikalEvents.fireKreiranArtikal(this, roba);
                    Thread.sleep(250);
                }
            }
        }

        proManager.getTransaction().commit();
        setMessage("Svi novi artikli su uspješno učitani");

        return true;
    }

}
