package org.dpr.mykeys.keystore;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.BagInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.ihm.components.ListPanel;

public class KeyStoreInfo extends BagInfo implements NodeInfo {
	
	public static final Log log = LogFactory.getLog(KeyStoreInfo.class);

	@Override
	public List<CertificateInfo> getChildList() {
		// TODO Auto-generated method stub
		List<CertificateInfo> certs = null;
		try {
			certs = getCertificates();
		} catch (KeyToolsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return certs;
	}

	@Override
	public boolean isProtected() {
		return true;
	}

	@Override
	public void open() throws KeyToolsException {
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		
		ks = kt.loadKeyStore(this.getPath(), this.getStoreFormat(), this.getPassword());
		
	}

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
	
	private List<CertificateInfo> getCertificates()
			throws KeyToolsException {
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		if (this.getPassword() == null
				&& this.getStoreFormat().equals(StoreFormat.PKCS12)) {
			return certs;
		}

		ks = kt.loadKeyStore(this.getPath(), this.getStoreFormat(),
				this.getPassword());

		log.trace("addcerts");
		Enumeration<String> enumKs;
		try {
			enumKs = ks.aliases();
			if (enumKs != null && enumKs.hasMoreElements()) {

				while (enumKs.hasMoreElements()) {
					String alias = enumKs.nextElement();

					CertificateInfo certInfo = new CertificateInfo(alias);
					kt.fillCertInfo(ks, certInfo, alias);
					certs.add(certInfo);
				}
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return certs;

	}
}
