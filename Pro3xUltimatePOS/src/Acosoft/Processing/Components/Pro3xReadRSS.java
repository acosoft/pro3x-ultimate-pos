// Pro3x Community project
// Copyright (C) 2009  Aleksandar Zgonjan
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, http://www.gnu.org/licenses/gpl.html

package Acosoft.Processing.Components;

import Acosoft.Processing.Pro3App;
import Acosoft.Processing.Pro3View;
import Acosoft.Processing.Pro3xRSS;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author nonstop
 */
public class Pro3xReadRSS extends Task
{
    private final String location = "http://www.pro3x.com/index.php/feed/";
    private final String rssStore = "rss-info.xml";
    private SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public Pro3xReadRSS(Pro3App app)
    {
        super(app);
    }

    public Pro3xReadRSS(Pro3App app, boolean center)
    {
        super(app);
    }


    @Override
    protected void succeeded(Object result)
    {
        setMessage("Otvaram pregled RSS novosti");

        Pro3xRssInfo info = (Pro3xRssInfo) result;
        Pro3xRSS jf = new Pro3xRSS(info);

        ((Pro3View)((Pro3App)getApplication()).getMainView()).Show(jf);

        jf.setPreferredSize(new Dimension(785, 503));
        jf.setSize(new Dimension(785, 503));
        jf.setMinimumSize(new Dimension(785, 503));
        
        Dimension deskSize = jf.getDesktopPane().getSize();
        jf.setLocation(((int)deskSize.getWidth() - jf.getWidth()) / 2,
                (((int)deskSize.getHeight() - jf.getHeight()) / 2));

        if(jf.getX() < 0) jf.setLocation(0, jf.getY());
        if(jf.getY() < 0) jf.setLocation(jf.getX(), 0);
        
        setMessage("Pregled RSS novosti uspješno otvoren");
    }

    private String Extract(String tag, Element rssItem)
    {
        return rssItem.getElementsByTagName(tag).item(0).getTextContent();
    }

    @Override
    protected void failed(Throwable cause)
    {
        setMessage("Iznimka pri otvaranju RSS novosti: " + cause.getClass().getName());
        Logger.getLogger(Pro3xReadRSS.class.getName()).log(Level.SEVERE, null, cause);
    }

    private Pro3xRssInfo ReadRss() throws Exception
    {
        Pro3xRssInfo info = new Pro3xRssInfo();
        info.setLastRead(Calendar.getInstance().getTime());
        
        Document feed = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(location);
        NodeList items = feed.getElementsByTagName("item");

        int count = items.getLength(); // if(count > 3) count = 3;

        for(int i=0; i < count ; i++)
        {
            Element rssItem = (Element) items.item(i);
            Pro3xRssItem item = new Pro3xRssItem();

            String dateValue = Extract("pubDate", rssItem);
            item.setDatum(format.parse(dateValue));
            
            item.setNaslov(Extract("title", rssItem));
            item.setLokacija(Extract("link", rssItem));
            item.setOpis(Extract("description", rssItem));

            info.getItems().add(item);
        }

        LocalStorage store = getApplication().getContext().getLocalStorage();
        store.save(info, rssStore);

        return info;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage("Pokušavam učitati RSS novosti");
        Thread.sleep(2000);

        setMessage("Provjeravam lokalne RSS novosti");

        LocalStorage store = getApplication().getContext().getLocalStorage();
        Pro3xRssInfo rssData = null;

        try
        {
            // Provjeravanje lokalno spremljenih novosti?
            store.openInputFile(rssStore).close();

            setMessage("Učitavam lokalne RSS novosti");

            rssData = (Pro3xRssInfo)store.load(rssStore);

            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DAY_OF_MONTH, false);

            Calendar readCal = Calendar.getInstance();
            readCal.setTime(rssData.getLastRead());

            setMessage("Provjeravam datum učitavanja RSS novosti");
            if(readCal.before(cal))
            {
                try
                {
                    setMessage("Učitam RSS novosti sa servera");
                    rssData = ReadRss();
                }
                catch(Exception ex)
                {
                    Logger.getLogger(Pro3xReadRSS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        catch(Exception ex)
        {
            setMessage("Problem pri učivatanju lokalnih RSS novosti, pokušavam učitati sa servera");
            rssData = ReadRss();
        }

        setMessage("Otvaram prikaz RSS novosti.");

        return rssData;
    }

}
