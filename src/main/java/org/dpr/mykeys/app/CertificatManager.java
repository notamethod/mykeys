package org.dpr.mykeys.app;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.components.ListPanel;

public class CertificatManager {
	public static final Log log = LogFactory.getLog(CertificatManager.class);

	public static List<CertificateInfo> getCertificates(KeyStoreInfo ksInfo) throws Exception {

	
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		if (ksInfo.getPassword() == null
				&& ksInfo.getStoreFormat().equals(StoreFormat.PKCS12)) {
			return new ArrayList<CertificateInfo>();
		}
		try {
			ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
					ksInfo.getPassword());
			return getCertificatesFromKeystore(kt, ks);
		} catch (KeyToolsException e1) {
			log.warn(e1);
			return getCertificatesFromFactory(kt, ksInfo);
		}



	}

	private static List<CertificateInfo> getCertificatesFromFactory(KeyTools kt, KeyStoreInfo 
			ksInfo) throws Exception {

		return kt.loadX509Certs(ksInfo.getPath());

		
	}

	private static List<CertificateInfo> getCertificatesFromKeystore(KeyTools kt, KeyStore ks) {
		log.trace("addcerts");
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
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
