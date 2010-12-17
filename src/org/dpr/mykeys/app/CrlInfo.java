/**
 * 
 */
package org.dpr.mykeys.app;

import java.math.BigInteger;
import java.util.Date;

/**
 * <pre>
 * <b></b>.
 *  
 * <b>Description :</b>
 * 
 * 
 * </pre>
 * 
 * @author C. Roger<BR>
 * <BR>
 *         Cr�� le 3 sept. 2010 <BR>
 * <BR>
 * <BR>
 *         <i>Copyright : Tessi Informatique </i><BR>
 */
public class CrlInfo  implements NodeInfo{
    Date thisUpdate = new Date();
    Date nextUpdate;
    String name;
    String path;

    BigInteger number = BigInteger.ONE;

    /**
     * Retourne le thisUpdate.
     * 
     * @return Date - le thisUpdate.
     */
    public Date getThisUpdate() {
	return thisUpdate;
    }

    /**
     * Affecte le thisUpdate.
     * 
     * @param thisUpdate
     *            le thisUpdate � affecter.
     */
    public void setThisUpdate(Date thisUpdate) {
	this.thisUpdate = thisUpdate;
    }

    /**
     * Retourne le nextUpdate.
     * 
     * @return Date - le nextUpdate.
     */
    public Date getNextUpdate() {
	return nextUpdate;
    }

    /**
     * Affecte le nextUpdate.
     * 
     * @param nextUpdate
     *            le nextUpdate � affecter.
     */
    public void setNextUpdate(Date nextUpdate) {
	this.nextUpdate = nextUpdate;
    }

    /**
     * Retourne le number.
     * 
     * @return BigInteger - le number.
     */
    public BigInteger getNumber() {
	return number;
    }

    /**
     * Affecte le number.
     * 
     * @param number
     *            le number � affecter.
     */
    public void setNumber(BigInteger number) {
	this.number = number;
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @return
     * 
     * @see org.dpr.mykeys.app.NodeInfo#getName()
     */
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return name;
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @return
     * 
     * @see org.dpr.mykeys.app.NodeInfo#getPath()
     */
    @Override
    public String getPath()
    {
        // TODO Auto-generated method stub
        return path;
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @param name
     * 
     * @see org.dpr.mykeys.app.NodeInfo#setName(java.lang.String)
     */
    @Override
    public void setName(String name)
    {
        this.name=name;
        
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @param path
     * 
     * @see org.dpr.mykeys.app.NodeInfo#setPath(java.lang.String)
     */
    @Override
    public void setPath(String path)
    {
	 this.path=path;
        
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @return
     * 
     * @see org.dpr.mykeys.app.NodeInfo#isOpen()
     */
    @Override
    public boolean isOpen() {
	// TODO Auto-generated method stub
	return false;
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @param isOpen
     * 
     * @see org.dpr.mykeys.app.NodeInfo#setOpen(boolean)
     */
    @Override
    public void setOpen(boolean isOpen) {
	// TODO Auto-generated method stub
	
    }

}
