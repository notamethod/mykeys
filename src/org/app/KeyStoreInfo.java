package org.app;


public class KeyStoreInfo {

    private String name;

    private String path;

    private boolean isOpen = false;

    private StoreType storeType;

    private StoreFormat storeFormat;

    private char[] password;

    public KeyStoreInfo(String name, String path, StoreType storeType, StoreFormat storeFormat) {
	this.name = name;
	this.path = path;
	this.storeType = storeType;
	this.storeFormat = storeFormat;
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

    public enum StoreType {
	CASTORE, CERTSTORE, KEYSTORE, P12STORE;

	public static StoreType fromValue(String v) {
	    return valueOf(v);
	}
	
	public static String getValue(StoreType type) {
	    return type.toString();
	}	
    }

    public enum StoreFormat {
	JKS, PKCS12;
	public static StoreFormat fromValue(String v) {
	    return valueOf(v);
	}
	public static String getValue(StoreFormat format) {
	    return format.toString();
	}	
    }

    /**
     * Retourne le storeType.
     * @return StoreType - le storeType.
     */
    public StoreType getStoreType() {
        return storeType;
    }

    /**
     * Affecte le storeType.
     * @param storeType le storeType à affecter.
     */
    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    /**
     * Retourne le storeFormat.
     * @return StoreFormat - le storeFormat.
     */
    public StoreFormat getStoreFormat() {
        return storeFormat;
    }

    /**
     * Affecte le storeFormat.
     * @param storeFormat le storeFormat à affecter.
     */
    public void setStoreFormat(StoreFormat storeFormat) {
        this.storeFormat = storeFormat;
    }
}
