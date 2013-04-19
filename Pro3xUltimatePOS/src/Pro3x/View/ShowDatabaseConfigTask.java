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

package Pro3x.View;

import Acosoft.Processing.Pro3App;
import Acosoft.Processing.Pro3View;
import Pro3x.Configuration.BazaPodataka;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;


public class ShowDatabaseConfigTask extends Task
{
    private ResourceMap resources = null;

    public ShowDatabaseConfigTask(Application app)
    {
        super(app);
        resources = app.getContext().getResourceManager().getResourceMap(ShowDatabaseConfigTask.class);
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage(resources.getString("CheckDatabaseConfiguration"));

        String path = DatabaseConfiguration.ConfigPath;
        BazaPodataka baza = (BazaPodataka)getApplication().getContext()
                .getLocalStorage().load(path);

        return baza;
    }

    @Override
    protected void succeeded(Object result)
    {
        if(result == null)
        {
            setMessage(resources.getString("ConfiguringDefaultDatabase"));
            DatabaseConfiguration config = new DatabaseConfiguration();
            ((Pro3View)Pro3App.getApplication().getMainView()).Show(config);
        }
        else
        {
            setMessage(resources.getString("ReadingUserConfiguration"));
            DatabaseConfiguration config = new DatabaseConfiguration((BazaPodataka)result);
            ((Pro3View)Pro3App.getApplication().getMainView()).Show(config);
        }
    }

    @Override
    protected void failed(Throwable cause)
    {
        setMessage(resources.getString("DatabaseConfigurationException", cause.toString()));
    }
}
