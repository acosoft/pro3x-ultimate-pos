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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Helpdesk
{

    protected String mailServer;
    public static final String PROP_MAIL_SERVER = "mailServer";
    protected String fromMail;
    public static final String PROP_FROM_MAIL = "fromMail";
    protected String toMail;
    public static final String PROP_TO_MAIL = "toMail";
    protected String subject;
    public static final String PROP_SUBJECT = "subject";

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        String oldSubject = this.subject;
        this.subject = subject;
        propertyChangeSupport.firePropertyChange(PROP_SUBJECT, oldSubject, subject);
    }

    public String getToMail()
    {
        return toMail;
    }

    public void setToMail(String toMail)
    {
        String oldToMail = this.toMail;
        this.toMail = toMail;
        propertyChangeSupport.firePropertyChange(PROP_TO_MAIL, oldToMail, toMail);
    }


    public String getFromMail()
    {
        return fromMail;
    }

    public void setFromMail(String fromMail)
    {
        String oldFromMail = this.fromMail;
        this.fromMail = fromMail;
        propertyChangeSupport.firePropertyChange(PROP_FROM_MAIL, oldFromMail, fromMail);
    }

    public String getMailServer()
    {
        return mailServer;
    }

    public void setMailServer(String mailServer)
    {
        String oldMailServer = this.mailServer;
        this.mailServer = mailServer;
        propertyChangeSupport.firePropertyChange(PROP_MAIL_SERVER, oldMailServer, mailServer);
    }

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
