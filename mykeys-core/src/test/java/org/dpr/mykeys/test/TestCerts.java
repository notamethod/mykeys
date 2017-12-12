package org.dpr.mykeys.test;

import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.TimeStampManager;
import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.junit.Test;


public class TestCerts {

	final static Log log = LogFactory.getLog(Test.class);



	@Test
	public void ImportCert() {
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

			 e.printStackTrace();
			fail();
	

		}

	}

	@Test
	public  void loadKS() throws ServiceException {
		// String path = "data/test01.jks";
		// KeyStoreInfo ksInfo = new KeyStoreInfo("aa", path,
		// StoreModel.CERTSTORE, StoreFormat.JKS);
		String path = System.getProperty("user.dir");

		URL url = TestCerts.class.getResource("/data/test01.jks");

		try {
			log.trace(url.toURI().getPath());
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			log.error(e2);
			fail();
		}

		// log.trace(String. Test.class.getPackage().getName());
		// String name = Test.class.getPackage().getName().replace('.',
		// File.separatorChar);
		// File f = new File(path, name+File.separator+"data");
		// f = new File(f.getAbsolutePath(), "test01.jks");
		// log.trace(f.getAbsolutePath());
		KeyTools kt = new KeyTools();
		KeyStore ks = null;
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		String fileName = null;
		try {
			fileName = url.toURI().getPath().substring(1);
		} catch (URISyntaxException e2) {
			log.error(e2);
			fail();
		}
		Path resourceDirectory = Paths.get("src/test/resources/data/test01.jks");
		fileName = resourceDirectory.toAbsolutePath().toString();
		KeyStoreInfo ksInfo = new KeyStoreInfo("aa", fileName,
				StoreModel.CERTSTORE, StoreFormat.JKS);
		ksInfo.setPassword("1234".toCharArray());
		try {
			ks = ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
					ksInfo.getPassword()).get();

		} catch (Exception e1) {

			log.error(e1);
			fail();
		}

		Enumeration<String> enumKs = null;
		try {
			enumKs = ks.aliases();
		} catch (KeyStoreException e) {
			log.error(e);
			fail();
		}
		if (enumKs != null && enumKs.hasMoreElements()) {

			while (enumKs.hasMoreElements()) {
				String alias = enumKs.nextElement();
				if (log.isDebugEnabled()) {
					log.debug(alias);
				}
				//
				CertificateInfo certInfo = fillCertInfo(ksInfo, ks, alias);

			}
		}

	}

	private static CertificateInfo fillCertInfo(KeyStoreInfo ksInfo, KeyStore ks, String alias) throws ServiceException {
		KeyStoreHelper ksv = new KeyStoreHelper(ksInfo);
		return ksv.fillCertInfo(ks, alias);
		
	}

	@Test
	public void TimeStamp() throws ServiceException {
		Security.addProvider(new BouncyCastleProvider());

		KeyTools kt = new KeyTools();
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		KeyStore ks = null;
		String fileName = null;
		Path resourceDirectory = Paths.get("src/test/resources/data/test01.jks");
		fileName = resourceDirectory.toAbsolutePath().toString();
		KeyStoreInfo ksInfo = new KeyStoreInfo("aa", fileName,
				StoreModel.CERTSTORE, StoreFormat.JKS);
		ksInfo.setPassword("1234".toCharArray());
		try {
			ks = ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
					ksInfo.getPassword()).get();

		} catch (Exception e1) {

			log.error(e1);
			fail();

		}
		CertificateInfo certInfo = null;
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
				certInfo = fillCertInfo(ksInfo, ks, alias);
			}
		}
		try {
			TimeStampToken tsp = TimeStampManager.getTimeStampToken(4);
			log.trace(tsp);
		} catch (Exception e) {
			log.error(e);
			fail();
		}

	}
}
