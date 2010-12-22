/**
 * 
 */
package org.dpr.mykeys.app;

/**
 *<pre>
 * <b></b>.
 * 
 * <b>Description :</b>
 * 
 * 
 *</pre>
 * 
 * @author C. Roger<BR>
 * <BR>
 *         Créé le 20 déc. 2010 <BR>
 * <BR>
 * <BR>
 *         <i>Copyright : Tessi Informatique </i><BR>
 */
public class BagInfo implements NodeInfo {

    String name;
    String path;

    /**
     * . <BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param fileName
     */
    public BagInfo(String fileName) {
	String name = fileName.substring(fileName.lastIndexOf("\\") + 1,
		fileName.length());
	this.path = fileName;
	this.name = name;
    }

    /**
     * .
     * 
     *<BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
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
     *<BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param isOpen
     * 
     * @see org.dpr.mykeys.app.NodeInfo#setOpen(boolean)
     */
    @Override
    public void setOpen(boolean isOpen) {
	// TODO Auto-generated method stub

    }

    /**
     * Retourne le name.
     * 
     * @return String - le name.
     */
    public String getName() {
	return name;
    }

    /**
     * Affecte le name.
     * 
     * @param name
     *            le name à affecter.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Retourne le path.
     * 
     * @return String - le path.
     */
    public String getPath() {
	return path;
    }

    /**
     * Affecte le path.
     * 
     * @param path
     *            le path à affecter.
     */
    public void setPath(String path) {
	this.path = path;
    }

}
