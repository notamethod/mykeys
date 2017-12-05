/**
 * 
 */
package org.dpr.mykeys.app;

import java.util.List;

/**
 * @author Buck
 *
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
	
	 
	public List<? extends ChildInfo> getChildList();

	public boolean isProtected();

	public void open() throws KeyToolsException;



}
