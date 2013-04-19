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

import Acosoft.Processing.Pro3App;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;


public class SingleInstanceLock
{
    private FileLock lock;

    public SingleInstanceLock() throws Exception
    {
        File lockFile = new File(Pro3App.getApplication().getContext().getLocalStorage().getDirectory(), "lock.pro3x");
        if(!lockFile.exists())
        {
            lockFile.getParentFile().mkdirs();
            lockFile.createNewFile();
        }

        lock = new RandomAccessFile(lockFile, "rw").getChannel().tryLock();

        if(lock == null) throw new Exception("Aplikacija je već pokrenuta!");
    }

    public void unlock() throws Exception
    {
        lock.release();
        lock.channel().close();
    }
}
