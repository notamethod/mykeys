package org.dpr.mykeys.app.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.app.certificate.CertificateBuilder;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.utils.ActionStatus;

public class KeyStoreHelper implements StoreService<KeyStoreInfo> {
	public static final Log log = LogFactory.getLog(KeyStoreHelper.class);

	public static final String[] KSTYPE_EXT_PKCS12 = { "p12", "pfx", "pkcs12" };
	public static final String KSTYPE_EXT_JKS = "jks";
	KeyStoreInfo ksInfo;

	public KeyStoreHelper(KeyStoreInfo ksInfo) {
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
			throw new ServiceException("can't load keystore "+ksInfo.getPath(), e);
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
			CertificateValue certInfo = new CertificateValue(alias, (X509Certificate) cert, ksin.getPassword());

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
			throw new ServiceException("can't open keystore" + ksName, e);
		}
	}

	public KeyStore getKeystore() throws ServiceException {

		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		try {
			return ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).get();
		} catch (KeyToolsException e) {
			throw new ServiceException("can't open keystore" + ksInfo.getPath(), e);
		}
	}

	public List<CertificateValue> getCertificates() throws ServiceException {
		List<CertificateValue> certs = new ArrayList<CertificateValue>();
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

					CertificateValue certInfo = fillCertInfo(ks, alias);
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
	public List<CertificateValue> getChildList() throws ServiceException {
		// TODO Auto-generated method stub
		List<CertificateValue> certs = null;
		certs = getCertificates();
		return certs;
	}

	public void addCertToKeyStore(X509Certificate[] xCerts, CertificateValue certInfo, char[] password) throws ServiceException {
		KeystoreBuilder ksb = new KeystoreBuilder();
	
		try {
			ksb.load(ksInfo).addCert(xCerts, ksInfo, certInfo, password);
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
				CertificateValue certInfo = new CertificateValue(alias, (X509Certificate) cert, charArray);

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
				CertificateValue certInfo = new CertificateValue(alias, cert, charArray);

				ksBuilder.addCert((X509Certificate) cert, ksInfo, certInfo);

			} catch (KeyToolsException | CertificateException | IOException e) {
				// TODO Auto-generated catch block
				throw new ServiceException(e);
			}
		}
	}

	public void removeCertificate(CertificateValue certificateInfo) throws KeyToolsException, KeyStoreException {
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		ksBuilder.load(ksInfo).removeCert(certificateInfo).save(ksInfo);
	}


	public CertificateValue fillCertInfo(KeyStore ks, String alias) throws ServiceException {
		CertificateValue certInfo = null;
		try {
			Certificate certificate = ks.getCertificate(alias);
			Certificate[] certs = ks.getCertificateChain(alias);

			certInfo = new CertificateValue(alias, (X509Certificate) certificate);
			if (ks.isKeyEntry(alias)) {
				certInfo.setContainsPrivateKey(true);

			}
			StringBuffer bf = new StringBuffer();
			if (certs == null) {
				String message = "chaine de certification nulle pour " + alias + " ("+certInfo.getName()+")";
				if (certInfo.isContainsPrivateKey()) 
					log.error(message);
				else
					log.info(message);
				// return null;
			} else {
				for (Certificate chainCert : certs) {
					bf.append(chainCert.toString());
				}
				certInfo.setCertChain(bf.toString());
				certInfo.setCertificateChain(certs);
			}

		} catch (KeyStoreException e) {
			throw new ServiceException("filling certificate Info impossible", e);
		}
		return certInfo;
	}

	public void exportPrivateKey(CertificateValue certInfo, char[] password, String fName) throws KeyToolsException {
		/* save the private key in a file */

		try {
			KeyStore ks = getKeystore();
			PrivateKey privateKey = null;
			if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
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

	public void exportPrivateKeyPEM(CertificateValue certInfo, KeyStoreInfo ksInfo, char[] password, String fName)
			throws KeyToolsException {
		/* save the private key in a file */

		try {
			KeyStore ks = getKeystore();
			PrivateKey privateKey = null;
			if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
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



	public void addCertToKeyStore(CertificateValue certificate, char[] password) throws ServiceException {
		if (StringUtils.isBlank(certificate.getAlias())) {
			BigInteger bi = KeyTools.RandomBI(30);
			certificate.setAlias(bi.toString(16));
		}
		KeystoreBuilder ksb = new KeystoreBuilder();
		try {
			ksb.load(ksInfo).addCert(ksInfo, certificate, password);
		} catch (KeyToolsException e) {
			throw new ServiceException(e);
		}
		
	}

	public CertificateValue findACByAlias(KeyStoreInfo storeAC, String alias) throws ServiceException {
		if (null == alias || alias.trim().isEmpty()) {
			return null;
		}
		KeystoreBuilder ksb = new KeystoreBuilder();
		CertificateValue certInfo = new CertificateValue();
		try {
			KeyStore ks = ksb.load(storeAC).get();

			Certificate certificate = ks.getCertificate(alias);
			Certificate[] certs = ks.getCertificateChain(alias);
			if (ks.isKeyEntry(alias)) {
				certInfo.setContainsPrivateKey(true);
				certInfo.setPrivateKey((PrivateKey) ks.getKey(alias, storeAC.getPassword()));

			}
			X509Certificate x509Cert = (X509Certificate) certificate;
			certInfo.setSubjectMap(x509Cert.getSubjectDN().getName());
			// CertificateInfo certInfo2 = new CertificateInfo(alias, (X509Certificate)
			// certificate);
			certInfo.setPublicKey(certificate.getPublicKey());
			StringBuffer bf = new StringBuffer();
			if (certs == null) {
				log.error("chaine de certification nulle pour" + alias + "("+x509Cert.getSubjectDN().getName()+")");
				return null;
			}
			for (Certificate chainCert : certs) {
				bf.append(chainCert.toString());
			}
			certInfo.setCertChain(bf.toString());
			certInfo.setCertificateChain(certs);

		} catch (KeyStoreException | KeyToolsException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
			throw new ServiceException(e);
		}
		return certInfo;
	}

	public CertificateValue findACByAlias(String issuer) throws ServiceException {
		return findACByAlias(ksInfo, issuer);
		
	}

}
