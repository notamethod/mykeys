package org.dpr.mykeys.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.test.ListAlgorithms;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.ihm.windows.TamperedWithException;

public class KeyStoreService {
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

	public void open() {
		// TODO Auto-generated method stub

	}

	public void changePassword(char[] newPwd) throws TamperedWithException, KeyToolsException {
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		try {
			ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword());
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
		if (ksInfo == null && format.equals(StoreFormat.PKCS12)) {
			return ActionStatus.ASK_PASSWORD;
		}
		KeyTools kt = new KeyTools();
		try {
			kt.loadKeyStore(path, format, ksInfo.getPassword());
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
	
	public void importX509Cert(String alias,  KeyStoreInfo ksin)
			throws KeyToolsException, FileNotFoundException, CertificateException, GeneralSecurityException {
		CertificateInfo certInfo = new CertificateInfo();
		

		KeyTools kt = new KeyTools();
		
		
		if (ksin.getStoreFormat().equals(StoreFormat.PKCS12)) {
			KeyStore ks = kt.loadKeyStore(ksin);
			String aliasOri = null;
			Enumeration<String> enumKs = ks.aliases();
			while (enumKs.hasMoreElements()) {
				aliasOri = enumKs.nextElement();
			}

			Certificate cert = ks.getCertificate(aliasOri);

			kt.fillCertInfo(certInfo, cert);
			if (alias ==null){
				alias =certInfo.getName();
			}
			certInfo.setAlias(alias);
			certInfo.setPassword(ksin.getPassword());
			certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
			certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, ksin.getPassword()));
			// addCertToKeyStore((X509Certificate)cert, ksInfo, certInfo);
			kt.addCertToKeyStoreNew((X509Certificate) cert, ksInfo, certInfo);
		} //TODO JKS


	}

	public ActionStatus importCertificates(KeyStoreInfo ksin) throws FileNotFoundException, CertificateException, KeyToolsException, GeneralSecurityException {
		ksin.setStoreFormat(findTypeKS(ksin.getPath()));
		if (ksin.getPassword()==null && StoreFormat.PKCS12.equals(ksin.getStoreFormat())) {
			return ActionStatus.ASK_PASSWORD;
		}
		importX509Cert(null, ksin);
		return null;
		
	}

}
