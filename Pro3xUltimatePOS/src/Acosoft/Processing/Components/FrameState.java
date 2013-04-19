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
import java.io.Serializable;

/**
 *
 * @author nonstop
 */

public class FrameState
{
    private Rectangle _bounds;
    private State _state;
    
    public enum State implements Serializable { Normal, Minimized, Maximized }
    
    public Rectangle getBounds() {
        return _bounds;
    }

    public void setBounds(Rectangle bounds) {
        this._bounds = bounds;
    }

    public State getState() {
        return _state;
    }

    public void setState(State state) {
        this._state = state;
    }
}
