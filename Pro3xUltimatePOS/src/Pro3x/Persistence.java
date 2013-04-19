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

package Pro3x;

import Acosoft.Processing.Pro3App;
import Pro3x.Configuration.BazaPodataka;
import Pro3x.View.DatabaseConfiguration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import org.jdesktop.application.LocalStorage;

public class Persistence
{
    public static EntityManagerFactory createEntityManagerFactory()
    {
        return createEntityManagerFactory("procesingPU");
    }

    public static EntityManagerFactory createEntityManagerFactory(String unit)
    {
        try
        {
            LocalStorage storage = Pro3App.getApplication().getContext().getLocalStorage();

            if(!java.beans.Beans.isDesignTime())
            {
                BazaPodataka baza = (BazaPodataka)storage.load(DatabaseConfiguration.ConfigPath);

                HashMap<String, String> args = new HashMap<String, String>();

                args.put("eclipselink.jdbc.user", baza.getKorisnik());
                args.put("eclipselink.jdbc.password", baza.getZaporka());
                args.put("eclipselink.jdbc.driver", baza.getDriver());
                args.put("eclipselink.jdbc.url", baza.getConnection());

                return javax.persistence.Persistence.createEntityManagerFactory(unit, args);
            }

            return null;
        }
        catch(Exception ex)
        {
            Logger.getLogger(Pro3x.Persistence.class.getName()).log(Level.WARNING, null, ex);
            return javax.persistence.Persistence.createEntityManagerFactory(unit);
        }
    }
}
