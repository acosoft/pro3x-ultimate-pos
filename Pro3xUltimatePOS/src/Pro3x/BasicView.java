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
import javax.persistence.EntityManager;
import javax.swing.JInternalFrame;


public class BasicView extends JInternalFrame
{
    private EntityManager proManager;

    public BasicView()
    {
        proManager = Pro3xEvolutionManager();
    }

    protected final EntityManager Pro3xEvolutionManager()
    {
        return Pro3x.Persistence.createEntityManagerFactory("procesingPU").createEntityManager();
    }

    public EntityManager getProManager()
    {
        return proManager;
    }

    public void setProManager(EntityManager proManager)
    {
        this.proManager = proManager;
    }

    public Pro3App getApplication()
    {
        return Pro3App.getApplication();
    }
}
