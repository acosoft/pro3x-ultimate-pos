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

import java.awt.Rectangle;
import javax.swing.JTable;

/**
 *
 * @author nonstop
 */
public class Code 
{
    public static void ScrollToLastRow(JTable grid)
    {
        Rectangle rect = grid.getBounds();
        rect.setLocation(0, grid.getRowCount() * grid.getRowHeight());
        grid.scrollRectToVisible(rect);
    }
    
    public static void SelectLastRow(JTable grid)
    {
        int line = grid.getRowCount() - 1;
        grid.getSelectionModel().setSelectionInterval(line, line);
    }
}
