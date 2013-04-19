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

package Acosoft.Processing.Components;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;


public class PromjeniObavijest extends Task
{
    private String poruka;

    public PromjeniObavijest(Application app, String poruka)
    {
        super(app);
        this.poruka = poruka;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        setMessage(poruka);
        return null;
    }
}
