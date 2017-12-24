/**
 * 
 */
package org.dpr.mykeys.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.MkUtils;
import org.dpr.mykeys.app.certificate.CertificateBuilder;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

public class InternalKeystores {

	public static final Log log = LogFactory.getLog(InternalKeystores.class);
	public String password = "mKeys983178";
	static String USERDB =  "userDB.jks";
	static String cfgPath =  "userDB.jks";

	public InternalKeystores(String cfgPath, String profilsPath)  {
//        try {
//          //  this.pathAC = cfgPath +generateName(StoreModel.CASTORE);
//            this.pathCert = cfgPath + generateName(StoreModel.CERTSTORE);
//
//        } catch (NoSuchAlgorithmException e) {
//           throw new RuntimeException(e);
//        }
		this.cfgPath = cfgPath;
		this.pathProfils = profilsPath;
		this.pathUDB = cfgPath + USERDB;
	}

	private String generateName(StoreModel castore) throws NoSuchAlgorithmException {
		if (MkSession.user==null)
			throw new IllegalArgumentException("session is empty");
		String hdigest = new DigestUtils(SHA_256).digestAsHex((MkSession.user+castore.toString()).getBytes());

		return hdigest+".jks";

	}

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
		this.pathProfils = pathProfils;
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

		File f = new File(pathUDB);
		if (!f.exists())
			return false;
		return true;
	}
	public boolean existsCertDatabase() {

		File f = new File(pathCert);
		if (!f.exists())
			return false;
		return true;
	}
	public boolean existsACDatabase() {

		File f = new File(pathAC);
		if (!f.exists())
			return false;
		return true;
	}
	public boolean existsProfilDatabase() {

		File f = new File(pathProfils);
		if (!f.exists())
			return false;
		return true;
	}

	public KeyStoreInfo getUserDB() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);

		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathUDB);
		if (!existsUserDatabase()) {

			ksBuilder.create(pathUDB, pwd.toCharArray());

		}
		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathUDB, StoreModel.CERTSTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public void createUserDB() throws Exception {
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

	public void init() throws NoSuchAlgorithmException {
		if (null == pathAC) {
			this.pathAC = cfgPath + generateName(StoreModel.CASTORE);
		}
		if (null == pathCert) {
			this.pathCert = cfgPath + generateName(StoreModel.CERTSTORE);
		}

	}
}
