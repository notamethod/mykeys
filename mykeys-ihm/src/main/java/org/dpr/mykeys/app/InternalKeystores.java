
package org.dpr.mykeys.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

/**
 * Helpers for internal keystores.
 * Since this version, keystores are "protected" by the same password as previous version
 * but private keys are protected by user password.
 */
public class InternalKeystores {

	private static final Log log = LogFactory.getLog(InternalKeystores.class);
	private static final String password = "mKeys983178";
	public static String MK1_PASSWORD = "mKeys983178";
	private static String USERDB = "userDB.jks";
	private static String cfgPath;
	public static String MK1_STORE_AC = "mykeysAc.jks";
	public static String MK1_STORE_CERT = "mykeysCert.jks";

    private KeyStoreValue storeAC;


	public InternalKeystores(String cfgPath, String profilsPath)  {

		InternalKeystores.cfgPath = cfgPath;
		this.pathProfils = profilsPath;
		this.pathUDB = cfgPath + USERDB;
	}

    private String generateName(StoreModel storeModel) {
		if (MkSession.user==null)
			throw new IllegalArgumentException("session is empty");
        String hdigest = new DigestUtils(SHA_256).digestAsHex((MkSession.user + storeModel.toString()).getBytes());

		return hdigest+".jks";

	}

	public String getPassword() {
		return password;
	}

	private String pathAC;
	private String pathCert;
	private String pathProfils;
	private String pathUDB;
    private String pathPKI;



	public String getACPath() {

		return pathAC;
	}

	public String getCertPath() {

		return pathCert;
	}

    public String getPKIPath() {

        return pathPKI;
    }

	public String getProfilsPath() {

		return pathProfils;
	}

	public KeyStoreValue getStoreAC() {

		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreValue kinfo;
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
		kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathAC, StoreModel.CASTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

    public KeyStoreValue getStorePKI() {

        KeyTools kt = new KeyTools();
        String pwd = password;
        KeyStoreValue kinfo;
        File f = new File(pathPKI);
        if (!f.exists()) {

            try {

                InputStream is = (InternalKeystores.class.getResourceAsStream("/install/mykeysAc.jks"));
                MkUtils.copyFile(is, f);
                // InternalKeystores.class.getResource("/org.dpr.mykeys/config/myKeysAc.jks").getFile()getChannel();

            } catch (Exception e) {
                log.error(e);
            }
        }
        kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathPKI, StoreModel.PKISTORE,
                StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }
	public boolean existsUserDatabase() {

		File f = new File(pathUDB);
		return f.exists();
	}

	public boolean existsCertDatabase() {

		File f = new File(pathCert);
		return f.exists();
	}

	public boolean existsACDatabase() {

		File f = new File(pathAC);
		return f.exists();
	}

    public boolean existsPKIDatabase() {

        File f = new File(pathPKI);
        return f.exists();
    }

	public boolean existsProfilDatabase() {

		File f = new File(pathProfils);
		return f.exists();
	}

	public KeyStoreValue getUserDB() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);

		String pwd = password;
		KeyStoreValue kinfo;
		File f = new File(pathUDB);
		if (!existsUserDatabase()) {

			ksBuilder.create(pathUDB, pwd.toCharArray());

		}
		kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathUDB, StoreModel.CERTSTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public void createUserDB() throws Exception {
		KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreValue kinfo;
		File f = new File(pathUDB);
		if (!existsUserDatabase()) {
			try {
				ksBuilder.create(pathUDB, pwd.toCharArray());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathUDB, StoreModel.CERTSTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);

	}

	public KeyStoreValue getStoreCertificate() throws KeyStoreException {
		KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
		String pwd = password;
		KeyStoreValue kinfo;
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
		kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathCert, StoreModel.CERTSTORE,
				StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public ProfilStoreInfo getStoreProfils() {

		ProfilStoreInfo kinfo;
		File f = new File(pathProfils);
		if (!f.exists()) {
			f.mkdirs();

		}
		kinfo = new ProfilStoreInfo(Messages.getString("certificateTemplate.name"), pathProfils,
				StoreFormat.PROPERTIES);
		kinfo.setPassword("null".toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public KeyStoreValue getKeystoreInfo() {
		return new KeyStoreValue(new File(pathAC), StoreFormat.JKS,
				KSConfig.getInternalKeystores().getPassword().toCharArray());
	}

	public void init() {
		if (null == pathAC) {
			this.pathAC = cfgPath + generateName(StoreModel.CASTORE);
		}
		if (null == pathCert) {
			this.pathCert = cfgPath + generateName(StoreModel.CERTSTORE);
		}
        if (null == pathPKI) {
            this.pathPKI = cfgPath + generateName(StoreModel.PKISTORE);
        }

	}
}
