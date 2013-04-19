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

import Acosoft.Processing.DataBox.Roba;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TaskExportArtikala extends Task
{
    public static final String ARTIKAL = "artikal";
    public static final String ARTIKLI_ROOT = "artikli";
    public static final String GRUPA = "grupa";
    public static final String MJERA = "mjera";
    public static final String NABAVNA = "nabavna";
    public static final String NAZIV = "naziv";
    public static final String POREZ = "porez";
    public static final String PRO3X_EXTENSION = ".pro3x";
    public static final String PRODAJNA = "prodajna";
    public static final String SIFRA = "sifra";


    private File xf;

    public TaskExportArtikala(Application application, File file)
    {
        super(application);
        xf = ProvjeriExtenziju(file);

    }

    public static File ProvjeriExtenziju(File file)
    {
        if(!file.getName().endsWith(".pro3x"))
            return new File(file.getAbsolutePath() + PRO3X_EXTENSION);
        else
            return file;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage("Pripremam strukturu XML documenta");
        Document xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = xdoc.createElement(ARTIKLI_ROOT);
        xdoc.appendChild(root);

        setMessage("Učitavam popis artikala");
        EntityManager proManager = Pro3x.Persistence.createEntityManagerFactory().createEntityManager();
        List<Roba> artikli = proManager.createNamedQuery("Roba.findAll").getResultList();

        for(Roba art : artikli)
        {
            setMessage("Spremam artikal: " + art.getNaziv());
            Element arte = xdoc.createElement(ARTIKAL);
            arte.setAttribute(NAZIV, art.getNaziv());
            arte.setAttribute(MJERA, art.getMjera());
            arte.setAttribute(SIFRA, art.getSifra());
            arte.setAttribute(PRODAJNA, art.getCijena().toString());
            arte.setAttribute(NABAVNA, art.getNabavnaCijena().toString());
            arte.setAttribute(GRUPA, FormatiranjeGrupe(art));
            arte.setAttribute(POREZ, FormatiranjePoreza(art));
            root.appendChild(arte);
        }
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(xdoc);
        StreamResult result = new StreamResult(xf);
        trans.transform(source, result);

        setMessage("Popis artikala je spremljen u datoteku: " + xf.getAbsolutePath());
        return true;
    }

    private String FormatiranjeGrupe(Roba art)
    {
        if(art.getGrupa() != null)
            return art.getGrupa().getNaziv();
        else
            return "";
    }

    private String FormatiranjePoreza(Roba art)
    {
        if(art.getPdv() != null)
            return art.getPdv().getOpis();
        else
            return "";
    }
}
