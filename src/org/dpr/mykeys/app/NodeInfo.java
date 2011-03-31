/**
 * 
 */
package org.dpr.mykeys.app;

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
 *         Créé le 30 nov. 2010 <BR>
 * <BR>
 * <BR>
 *         <i>Copyright : Tessi Informatique </i><BR>
 */
public interface NodeInfo {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name);

	/**
	 * @return the path
	 */
	public String getPath();

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path);

	/**
	 * @return the isOpen
	 */
	public boolean isOpen();

	/**
	 * @param isOpen
	 *            the isOpen to set
	 */
	public void setOpen(boolean isOpen);

}
