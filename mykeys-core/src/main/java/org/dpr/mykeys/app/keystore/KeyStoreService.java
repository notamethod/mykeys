package org.dpr.mykeys.app.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.app.certificate.CertificateBuilder;
import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.utils.ActionStatus;

public class KeyStoreService implements StoreService<KeyStoreInfo> {
	public static final Log log = LogFactory.getLog(KeyStoreService.class);

	public static final String[] KSTYPE_EXT_PKCS12 = { "p12", "pfx", "pkcs12" };
	public static final String KSTYPE_EXT_JKS = "jks";
	KeyStoreInfo ksInfo;

	public KeyStoreService(KeyStoreInfo ksInfo) {
		this.ksInfo = ksInfo;
	}

	public void setKsInfo(KeyStoreInfo ksInfo) {
		this.ksInfo = ksInfo;
	}

	public void open() throws ServiceException {
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		try {
			ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword());
		} catch (KeyToolsException e) {
			throw new ServiceException(e);
		}

	}

	public void changePassword(char[] newPwd) throws TamperedWithException, KeyToolsException {
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		try {
			ks = ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).get();
		} catch (KeyToolsException e) {
			throw new TamperedWithException(e);
		}
		ksInfo.setPassword(newPwd);
		// TODO:l create save file
		kt.saveKeyStore(ks, ksInfo);
	}

	public void findType() {

	}

	public ActionStatus loadKeystore(String path) {
		StoreFormat format = findTypeKS(path);
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		if (ksInfo == null && format.equals(StoreFormat.PKCS12)) {
			return ActionStatus.ASK_PASSWORD;
		}
		KeyTools kt = new KeyTools();
		try {
			ksBuilder.loadKeyStore(path, format, ksInfo.getPassword());
		} catch (KeyToolsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		return ActionStatus.OK;
		//
		// try {
		// kt.loadX509Certs(path);
		// } catch (UnrecoverableKeyException | KeyStoreException |
		// NoSuchAlgorithmException | KeyToolsException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public static StoreFormat findTypeKS(String filename) {

		try {
			String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
			if (ext.equalsIgnoreCase(KSTYPE_EXT_JKS)) {
				return StoreFormat.JKS;
			}
			for (String aliasType : KSTYPE_EXT_PKCS12) {
				if (ext.equalsIgnoreCase(aliasType)) {
					return StoreFormat.PKCS12;
				}
			}
			return null;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

	}

	public void importX509Cert(String alias, KeyStoreInfo ksin)
			throws KeyToolsException, FileNotFoundException, CertificateException, GeneralSecurityException {

		KeyTools kt = new KeyTools();
		KeystoreBuilder ksBuilder = new KeystoreBuilder();

		if (ksin.getStoreFormat().equals(StoreFormat.PKCS12)) {
			KeyStore ks = ksBuilder.load(ksin).get();
			String aliasOri = null;
			Enumeration<String> enumKs = ks.aliases();
			while (enumKs.hasMoreElements()) {
				aliasOri = enumKs.nextElement();
			}

			Certificate cert = ks.getCertificate(aliasOri);
			CertificateInfo certInfo = new CertificateInfo(alias, (X509Certificate) cert, ksin.getPassword());

			if (alias == null) {
				alias = certInfo.getName();
			}

			certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
			certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, ksin.getPassword()));
			// addCertToKeyStore((X509Certificate)cert, ksInfo, certInfo);
			ksBuilder.addCertToKeyStoreNew((X509Certificate) cert, ksInfo, certInfo);
		} // TODO JKS

	}

	public ActionStatus importCertificates(KeyStoreInfo ksin)
			throws FileNotFoundException, CertificateException, KeyToolsException, GeneralSecurityException {
		ksin.setStoreFormat(findTypeKS(ksin.getPath()));
		if (ksin.getPassword() == null && StoreFormat.PKCS12.equals(ksin.getStoreFormat())) {
			return ActionStatus.ASK_PASSWORD;
		}
		importX509Cert(null, ksin);
		return null;

	}

	/**
	 * @param ksName
	 * @param format
	 * @param pwd
	 * @return
	 * @throws ServiceException
	 * @Deprecated use ksinfo with service
	 */
	@Deprecated
	public KeyStore getKeystore(String ksName, StoreFormat format, char[] pwd) throws ServiceException {

		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		try {
			return ksBuilder.loadKeyStore(ksName, format, pwd).get();
		} catch (KeyToolsException e) {
			throw new ServiceException(e);
		}
	}

	public KeyStore getKeystore() throws ServiceException {

		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		try {
			return ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).get();
		} catch (KeyToolsException e) {
			throw new ServiceException(e);
		}
	}

	private List<CertificateInfo> getCertificates() throws KeyToolsException, ServiceException {
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		if (ksInfo.getPassword() == null && ksInfo.getStoreFormat().equals(StoreFormat.PKCS12)) {
			return certs;
		}

		ks = getKeystore();

		log.trace("addcerts");
		Enumeration<String> enumKs;
		try {
			enumKs = ks.aliases();
			if (enumKs != null && enumKs.hasMoreElements()) {

				while (enumKs.hasMoreElements()) {
					String alias = enumKs.nextElement();

					CertificateInfo certInfo = fillCertInfo(ks, alias);
					certs.add(certInfo);
				}
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return certs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.keystore.StoreService#getChildList()
	 */
	@Override
	public List<CertificateInfo> getChildList() throws ServiceException {
		// TODO Auto-generated method stub
		List<CertificateInfo> certs = null;
		try {
			certs = getCertificates();
		} catch (KeyToolsException e) {
			throw new ServiceException(e);
		}
		return certs;
	}

	public void addCertToKeyStore(X509Certificate[] xCerts, CertificateInfo certInfo) throws ServiceException {
		KeystoreBuilder ksb = new KeystoreBuilder();
		try {
			ksb.load(ksInfo).addCert(xCerts, ksInfo, certInfo);
		} catch (KeyToolsException e) {
			throw new ServiceException(e);
		}
	}

	public void importX509Cert(String alias, String fileName, StoreFormat storeFormat, char[] charArray)
			throws ServiceException {

		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		if (storeFormat == null || storeFormat.PKCS12.equals(storeFormat)) {
			try {
				KeyStore ks = ksBuilder.loadKeyStore(fileName, storeFormat, ksInfo.getPassword()).get();

				String aliasOri = null;
				Enumeration<String> enumKs = ks.aliases();
				while (enumKs.hasMoreElements()) {
					aliasOri = enumKs.nextElement();
				}
				Certificate cert = ks.getCertificate(aliasOri);
				CertificateInfo certInfo = new CertificateInfo(alias, (X509Certificate) cert, charArray);

				certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
				certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, charArray));
				// addCertToKeyStore((X509Certificate)cert, ksInfo, certInfo);

				ksBuilder.addCert((X509Certificate) cert, ksInfo, certInfo);
			} catch (KeyToolsException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (StoreFormat.JKS.equals(storeFormat)) {
			;
			try (InputStream is = new FileInputStream(new File(fileName))) {

				CertificateBuilder cb = new CertificateBuilder();
				X509Certificate cert = cb.load(is).get();
				CertificateInfo certInfo = new CertificateInfo(alias, cert, charArray);

				ksBuilder.addCert((X509Certificate) cert, ksInfo, certInfo);

			} catch (KeyToolsException | CertificateException | IOException e) {
				// TODO Auto-generated catch block
				throw new ServiceException(e);
			}
		}
	}

	public void removeCertificate(CertificateInfo certificateInfo) throws KeyToolsException, KeyStoreException {
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		ksBuilder.load(ksInfo).removeCert(certificateInfo).save(ksInfo);
	}

	public void fillCertInfo(KeyStore ks, CertificateInfo certInfo, String alias) {

		try {
			Certificate certificate = ks.getCertificate(alias);
			Certificate[] certs = ks.getCertificateChain(alias);
			if (ks.isKeyEntry(alias)) {
				certInfo.setContainsPrivateKey(true);

			}
			CertificateInfo certInfo2 = new CertificateInfo(alias, (X509Certificate) certificate);

			StringBuffer bf = new StringBuffer();
			if (certs == null) {
				log.warn("chaine de certification nulle pour" + alias);
				// return;
			}
			for (Certificate chainCert : certs) {
				bf.append(chainCert.toString());
			}
			certInfo.setCertChain(bf.toString());
			certInfo.setCertificateChain(certs);

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public CertificateInfo fillCertInfo(KeyStore ks, String alias) {
		CertificateInfo certInfo = null;
		try {
			Certificate certificate = ks.getCertificate(alias);
			Certificate[] certs = ks.getCertificateChain(alias);

			certInfo = new CertificateInfo(alias, (X509Certificate) certificate);
			if (ks.isKeyEntry(alias)) {
				certInfo.setContainsPrivateKey(true);

			}
			StringBuffer bf = new StringBuffer();
			if (certs == null) {
				log.warn("chaine de certification nulle pour" + alias);
				// return null;
			} else {
				for (Certificate chainCert : certs) {
					bf.append(chainCert.toString());
				}
				certInfo.setCertChain(bf.toString());
				certInfo.setCertificateChain(certs);
			}

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return certInfo;
	}

	public void exportPrivateKey(CertificateInfo certInfo, char[] password, String fName) throws KeyToolsException {
		/* save the private key in a file */

		try {
			KeyStore ks = getKeystore();
			PrivateKey privateKey = null;
			if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) {
				privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), ksInfo.getPassword());
			} else {
				privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), password);
			}
			byte[] privKey = privateKey.getEncoded();
			FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"));
			keyfos.write(privKey);
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé privée impossible:" + certInfo.getAlias(), e);
		}
	}

	public void exportPrivateKeyPEM(CertificateInfo certInfo, KeyStoreInfo ksInfo, char[] password, String fName)
			throws KeyToolsException {
		/* save the private key in a file */

		try {
			KeyStore ks = getKeystore();
			PrivateKey privateKey = null;
			if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) {
				privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), ksInfo.getPassword());
			} else {
				privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), password);
			}
			byte[] privKey = privateKey.getEncoded();

			List<String> lines = new ArrayList<String>();
			lines.add(KeyTools.BEGIN_KEY);
			// FileUtils.writeLines(file, lines)
			File f = new File(fName + ".pem.key");
			// FileOutputStream keyfos = new FileOutputStream(new File(fName
			// + ".pem"));
			byte[] b = Base64.encodeBase64(privKey);
			String tmpString = new String(b);
			String[] datas = tmpString.split("(?<=\\G.{64})");
			for (String data : datas) {
				lines.add(data);
			}

			lines.add(KeyTools.END_KEY);
			FileUtils.writeLines(f, lines);

			FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"));
			keyfos.write(privKey);
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé privée impossible:" + certInfo.getAlias(), e);
		}
	}

	public KeyStore importStore(String path, StoreFormat storeFormat, char[] password) throws KeyToolsException,
			UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, ServiceException {
		// TODO Auto-generated method stub
		switch (storeFormat) {
		case JKS:
		case PKCS12:
			return getKeystore(path, storeFormat, password);

		default:
			CertificateUtils.loadX509Certs(path);
			return null;

		}
	}

	public void importStore(File transferFile, StoreFormat format, char[] charArray) throws UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, KeyToolsException, ServiceException {
		importStore(transferFile.getPath(), format, charArray);

	}

	public CertificateInfo getCertificateACByAlias(String aliasEmetteur) throws Exception {

		KeyStore ks = null;

		char[] password = InternalKeystores.password.toCharArray();
		ks = getKeystore(InternalKeystores.getACPath(), StoreFormat.JKS, InternalKeystores.password.toCharArray());
		CertificateInfo infoEmetteur = fillCertInfo(ks, aliasEmetteur);
		infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(aliasEmetteur, password));
		return infoEmetteur;

	}

}
