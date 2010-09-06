/**
 * 
 */
package org.app;

import java.math.BigInteger;
import java.util.Date;

/**
 *<pre>
 *<b></b>.
 * 
 *<b>Description :</b>
 *    
 * 
 *</pre>
 * @author C. Roger<BR>
 *  <BR>
 * Créé le 3 sept. 2010 <BR>
 *  <BR>
 *  <BR>
 * <i>Copyright : Tessi Informatique </i><BR>
 */
public class CrlInfo
{
   Date thisUpdate = new Date();
   Date nextUpdate;
   
   BigInteger number = BigInteger.ONE;
/**
 * Retourne le thisUpdate.
 * @return Date - le thisUpdate.
 */
public Date getThisUpdate()
{
    return thisUpdate;
}
/**
 * Affecte le thisUpdate.
 * @param thisUpdate le thisUpdate à affecter.
 */
public void setThisUpdate(Date thisUpdate)
{
    this.thisUpdate = thisUpdate;
}
/**
 * Retourne le nextUpdate.
 * @return Date - le nextUpdate.
 */
public Date getNextUpdate()
{
    return nextUpdate;
}
/**
 * Affecte le nextUpdate.
 * @param nextUpdate le nextUpdate à affecter.
 */
public void setNextUpdate(Date nextUpdate)
{
    this.nextUpdate = nextUpdate;
}
/**
 * Retourne le number.
 * @return BigInteger - le number.
 */
public BigInteger getNumber()
{
    return number;
}
/**
 * Affecte le number.
 * @param number le number à affecter.
 */
public void setNumber(BigInteger number)
{
    this.number = number;
}

}
