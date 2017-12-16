/**
 * 
 */
package org.dpr.mykeys.app;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStoreException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.MkUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;

public class InternalKeystores {

	public static final Log log = LogFactory.getLog(InternalKeystores.class);
	public String password = "mKeys983178";

	public String getPassword() {
		return password;
	}

	private String pathAC;
	private String pathCert;
	private String pathProfils;
	private String pathUDB;

	public InternalKeystores(String pathUDB, String pathAC, String pathCert, String pathProfils) {
		this.pathAC = pathAC;
		this.pathCert = pathCert;
		this.pathProfils=pathProfils;
		this.pathUDB = pathUDB;
	}

	public String getACPath() {

		return pathAC;
	}

	public String getCertPath() {

		return pathCert;
	}

	public String getProfilsPath() {

		return pathProfils;
	}

	public KeyStoreInfo getStoreAC() {

		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathAC);
		if (!f.exists()) {

			try {

				InputStream is = (InternalKeystores.class.getResourceAsStream("/install/mykeysAc.jks"));
				MkUtils.copyFile(is, f);
				// InternalKeystores.class.getResource("/org.dpr.mykeys/config/myKeysAc.jks").getFile()getChannel();

			} catch (Exception e) {
				log.error(e);
			}

		}
		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathAC, StoreModel.CASTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public boolean existsUserDatabase() {

		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathUDB);
		if (!f.exists())
			return false;
		return true;
	}

	public KeyStoreInfo getUserDB() {

		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathUDB);

		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathAC, StoreModel.CASTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public CertificateValue createUserDB(CertificateValue cert) throws KeyStoreException {
		KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathUDB);
		if (!existsUserDatabase()) {
			try {
				ksBuilder.create(pathUDB, pwd.toCharArray());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathUDB, StoreModel.CERTSTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return null;
	}

	public KeyStoreInfo getStoreCertificate() throws KeyStoreException {
		KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathCert);
		// create keystore
		if (!f.exists()) {
			try {
				ksBuilder.create(pathCert, pwd.toCharArray());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathCert, StoreModel.CERTSTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public ProfilStoreInfo getStoreProfils() {

		ProfilStoreInfo kinfo = null;
		File f = new File(pathProfils);
		if (!f.exists()) {
			f.mkdirs();

		}
		kinfo = new ProfilStoreInfo(KSConfig.getMessage().getString("profil.name"), pathProfils,
				StoreFormat.PROPERTIES);
		kinfo.setPassword("null".toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public KeyStoreInfo getKeystoreInfo() {
		return new KeyStoreInfo(new File(pathAC), StoreFormat.JKS,
				KSConfig.getInternalKeystores().getPassword().toCharArray());
	}

}
