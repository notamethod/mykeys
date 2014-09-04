package org.dpr.mykeys.app;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.PkiTools.TypeObject;

public class KeyStoreInfo extends BagInfo implements NodeInfo {

	private String name;

	private String path;

	private boolean isOpen = false;

	private StoreModel storeModel = StoreModel.CERTSTORE;

	private StoreFormat storeFormat;

	private StoreType storeType = StoreType.EXTERNAL;
    //TODO	
	private boolean isTemp =false;
	//TODO
	private boolean isProtected =false;

	private char[] password;

	public KeyStoreInfo(String name, String path, StoreModel storeModel,
			StoreFormat storeFormat) {
		this.name = name;
		this.path = path;
		this.storeModel = storeModel;
		this.storeFormat = storeFormat;
	}

	public KeyStoreInfo(String name, String path, StoreModel storeModel,
			StoreFormat storeFormat, StoreType storeType) {
		this.name = name;
		this.path = path;
		this.storeModel = storeModel;
		this.storeFormat = storeFormat;
		this.storeType = storeType;
	}
	
	   public KeyStoreInfo(File fic, StoreFormat storeFormat, char[] cs) {
	               this.name = FilenameUtils.getName(fic.getPath());
	               this.path = fic.getPath();
	               this.storeFormat = storeFormat;
	               password=cs;
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

	public enum StoreModel {
		CASTORE, CERTSTORE, KEYSTORE, P12STORE;

		public static StoreModel fromValue(String v) {
			return valueOf(v);
		}

		public static String getValue(StoreModel type) {
			return type.toString();
		}
	}

	public enum StoreFormat {
		JKS, PKCS12, PEM, DER, UNKNOWN;
		public static StoreFormat fromValue(String v) {
			StoreFormat fmt = null;
			try {
				fmt = valueOf(v);
			} catch (Exception e) {
				fmt = UNKNOWN;
			}
			return fmt;

		}

		public static String getValue(StoreFormat format) {
			return format.toString();
		}

		public static StoreFormat fromValue(TypeObject typeObject) {
			switch (typeObject) {
			case MAGP12:
				return PKCS12;

            case MAGCER:
            	return DER;
  
			default:
				return UNKNOWN;
			}
		}
	}

	public enum StoreType {
		INTERNAL, EXTERNAL;
		public static StoreType fromValue(String v) {
			return valueOf(v);
		}

		public static String getValue(StoreType type) {
			return type.toString();
		}
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
	public StoreType getStoreType() {
		return storeType;
	}

	/**
	 * @param storeType
	 *            the storeType to set
	 */
	public void setStoreType(StoreType storeType) {
		this.storeType = storeType;
	}
}
