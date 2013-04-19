/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Acosoft.Processing.DataBox;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author aco
 */
@Entity
@DiscriminatorValue(value="pot")
public class PotStavkaRacuna extends PoreznaStavkaRacuna
{
    
}
