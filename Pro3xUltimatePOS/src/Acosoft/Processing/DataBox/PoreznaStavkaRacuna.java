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

package Acosoft.Processing.DataBox;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="racun_porezne_stavke")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tip")
@DiscriminatorValue(value="pdv")
@NamedQuery(name="PoreznaStavkaRacuna.IzbrisiStavkeRacuna", query="DELETE FROM PoreznaStavkaRacuna p WHERE p.racun = :racun")
public class PoreznaStavkaRacuna implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long oznaka;
    @ManyToOne
    @JoinColumn(name="racun", referencedColumnName="KLJUC")
    private Racun racun;
    @Column(name="naziv")
    private String naziv;
    @Column(name="osnovica", precision=20, scale=6)
    private BigDecimal osnovica;
    @Column(name="iznos", precision=20, scale=6)
    private BigDecimal iznos;
    
    @Column(name="stopa", nullable=true, precision=3, scale=2)
    private BigDecimal stopa;

    public Double getStopa() {
        return stopa.doubleValue();
    }

    public void setStopa(Double stopa) {
        this.stopa = new BigDecimal(stopa).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getOznaka()
    {
        return oznaka;
    }

    public void setOznaka(Long id)
    {
        this.oznaka = id;
    }

    public Racun getRacun()
    {
        return racun;
    }

    public void setRacun(Racun racun)
    {
        this.racun = racun;
    }

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        this.naziv = naziv;
    }

    public Double getOsnovica()
    {
        return osnovica.doubleValue();
    }

    public void setOsnovica(Double osnovica)
    {
        this.osnovica = new BigDecimal(osnovica).setScale(6, RoundingMode.HALF_UP);
    }

    public Double getIznos()
    {
        return iznos.doubleValue();
    }

    public void setIznos(Double iznos)
    {
        this.iznos = new BigDecimal(iznos).setScale(6, RoundingMode.HALF_UP);
    }
}
