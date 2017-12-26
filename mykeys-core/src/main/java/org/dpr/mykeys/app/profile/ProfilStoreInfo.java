package org.dpr.mykeys.app.profile;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.dpr.mykeys.app.BagInfo;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.keystore.StoreLocationType;

public class ProfilStoreInfo extends BagInfo implements NodeInfo {

	private String name;

	private String path;

	private boolean isOpen = false;

	private StoreModel storeModel = StoreModel.PROFILSTORE;

	private StoreFormat storeFormat;

	private StoreLocationType storeType = StoreLocationType.INTERNAL;
	// TODO
	private boolean isTemp = false;
	// TODO
	private boolean isProtected = false;

	private char[] password;

	public ProfilStoreInfo(String name, String path,  StoreFormat storeFormat) {
		this.name = name;
		this.path = path;
		this.storeFormat = storeFormat;
	}

	public ProfilStoreInfo(String name, String path, StoreFormat storeFormat, StoreLocationType storeType) {
		this.name = name;
		this.path = path;
		this.storeFormat = storeFormat;
		this.storeType = storeType;
	}

	public ProfilStoreInfo(File fic, StoreFormat storeFormat, char[] cs) {
		this.name = FilenameUtils.getName(fic.getPath());
		this.path = fic.getPath();
		this.storeFormat = storeFormat;
		password = cs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// affichage dans le jtree
		return name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the isOpen
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            the isOpen to set
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * @return the password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	

	/**
	 * Retourne le storeType.
	 * 
	 * @return StoreType - le storeType.
	 */
	public StoreModel getStoreModel() {
		return storeModel;
	}

	/**
	 * Affecte le storeType.
	 * 
	 * @param storeType
	 *            le storeType à affecter.
	 */
	public void setStoreModel(StoreModel storeType) {
		this.storeModel = storeType;
	}

	/**
	 * Retourne le storeFormat.
	 * 
	 * @return StoreFormat - le storeFormat.
	 */
	public StoreFormat getStoreFormat() {
		return storeFormat;
	}

	/**
	 * Affecte le storeFormat.
	 * 
	 * @param storeFormat
	 *            le storeFormat à affecter.
	 */
	public void setStoreFormat(StoreFormat storeFormat) {
		this.storeFormat = storeFormat;
	}

	/**
	 * @return the storeType
	 */
	public StoreLocationType getStoreType() {
		return storeType;
	}

	/**
	 * @param storeType
	 *            the storeType to set
	 */
	public void setStoreType(StoreLocationType storeType) {
		this.storeType = storeType;
	}

	@Override
	public List<? extends ChildInfo> getChildList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() throws KeyToolsException {
		// TODO Auto-generated method stub
		
	}

	
}
