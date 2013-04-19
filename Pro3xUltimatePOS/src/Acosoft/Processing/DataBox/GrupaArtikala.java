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

package Acosoft.Processing.DataBox;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="grupe_artikala")
@NamedQuery(name="GrupaArtikala.SveGrupe", query="SELECT g FROM GrupaArtikala g")
public class GrupaArtikala implements java.io.Serializable
{
    @OneToMany(mappedBy = "grupa", cascade=CascadeType.ALL)
    private List<Roba> artikliGrupe;

    @Id
    @Column(name="kljuc")
    private String kljuc;

    @Column(name="oznaka")
    private String oznaka;

    public GrupaArtikala()
    {
        setKljuc(UUID.randomUUID().toString());
    }

    public String getNaziv()
    {
        return oznaka;
    }

    public void setNaziv(String oznaka)
    {
        this.oznaka = oznaka;
    }

    public String getKljuc() {
        return kljuc;
    }

    public void setKljuc(String kljuc) {
        this.kljuc = kljuc;
    }

    @Override
    public String toString() {
            return getNaziv();
    }
}
