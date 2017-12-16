package org.dpr.mykeys.test;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;

public class Test {

	final static Log log = LogFactory.getLog(Test.class);

	/**
	 * .
	 * 
	 * @param args
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws ServiceException {
		loadKS();
		// ImportCert();

	}

	private static void ImportCert() {
		KeyTools kt = new KeyTools();
		try {
			String typeCert = null;

			String alias = "aaa";
			String path = "c:/dev/empty.jks";
			String pathCert = "c:/dev/cpi.cer";
			KeyStoreInfo ksInfo = new KeyStoreInfo("aa", path,
					StoreModel.CERTSTORE, StoreFormat.JKS);
			KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);
			kserv.importX509Cert(alias, pathCert, StoreFormat.UNKNOWN,
					"111".toCharArray());

		} catch (Exception e) {

			// e.printStackTrace();

		}

	}

	private static void loadKS() throws ServiceException {
		String path = "mag1.jks";
		KeyStoreInfo ksInfo = new KeyStoreInfo("aa", path,
				StoreModel.CERTSTORE, StoreFormat.JKS);

		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		KeyStoreHelper ksBuilder = new KeyStoreHelper(ksInfo);
		ksInfo.setPassword("111".toCharArray());
		try {
			ks = ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
					ksInfo.getPassword());

		} catch (Exception e1) {

			e1.printStackTrace();

		}

		Enumeration<String> enumKs = null;
		try {
			enumKs = ks.aliases();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (enumKs != null && enumKs.hasMoreElements()) {

			while (enumKs.hasMoreElements()) {
				String alias = enumKs.nextElement();
				if (log.isDebugEnabled()) {
					log.debug(alias);
				}
				//
				CertificateValue certInfo = fillCertInfo(ksInfo, ks, alias);

			}
		}

	}
	
	private static CertificateValue fillCertInfo(KeyStoreInfo ksInfo, KeyStore ks, String alias) throws ServiceException {
		KeyStoreHelper ksv = new KeyStoreHelper(ksInfo);
		return ksv.fillCertInfo(ks, alias);
		
	}
}
