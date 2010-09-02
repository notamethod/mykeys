package org.app;


public class KeyStoreInfo {

    private String name;

    private String path;

    private String type;

    private boolean isOpen = false;

    private char[] password;

    public KeyStoreInfo(String name, String path, String type) {
	this.name = name;
	this.path = path;
	this.type = type;
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
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
	this.type = type;
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
    


}
