/**
 * 
 */
package org.dpr.mykeys.app;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509CRL;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CrlValue;
import org.dpr.mykeys.app.crl.CrlTools;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.keystore.KeyStoreValue;

/**
 * @author Buck
 * 
 */
public class CommonServices {

    public void exportCert(KeyStoreValue ksInfo, StoreFormat pkcs12, String path, char[] password,
                           CertificateValue certInfo, boolean isExportCle) throws Exception {
		exportCert(ksInfo, pkcs12, path, password, certInfo, isExportCle, null);

	}

    public void exportCert(KeyStoreValue ksInfoIn, StoreFormat storeFormat, String path, char[] passwordExport,
                           CertificateValue certInfo, boolean isExportCle, char[] privKeyPwd) throws Exception {
		StoreModel storeModel = StoreModel.P12STORE;
        KeyStoreValue ksInfoOut = new KeyStoreValue("store", path, storeModel, storeFormat);
		ksInfoOut.setPassword(passwordExport);


		if (isExportCle && certInfo.getPrivateKey() == null) {
			KeyStoreHelper ksBuilder = new KeyStoreHelper(ksInfoIn);
			CertificateValue certInfoEx = new CertificateValue();
			certInfoEx.setAlias(certInfo.getAlias());
			certInfoEx.setCertificate(certInfo.getCertificate());
			certInfoEx.setCertificateChain(certInfo.getCertificateChain());
			certInfoEx.setPassword(privKeyPwd);

			char pwd[] = ksInfoIn.getPassword();
			if (ksInfoIn.getStoreType().equals(StoreLocationType.INTERNAL)) {
				// pwd=certInfoEx.getPassword();
				certInfoEx.setPassword(pwd);
			}

			certInfo.setPrivateKey(ksBuilder.getPrivateKey(ksInfoIn, certInfoEx.getAlias(), privKeyPwd));

			//certInfo = certInfoEx;
		}
		try {
			KeystoreBuilder ksBuilder = new KeystoreBuilder(storeFormat);
			ksInfoOut.setPassword(passwordExport);
			ksBuilder.create(path, ksInfoOut.getPassword()).addCert(
					ksInfoOut, certInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

    public void signData(KeyStoreValue kInfo, char[] password, CertificateValue certInfo, boolean isInclude) {

		KeyStoreHelper ksBuilder = new KeyStoreHelper(kInfo);
		KeyStore ks;
		try {
			ks = ksBuilder.loadKeyStore(kInfo.getPath(), kInfo.getStoreFormat(), kInfo.getPassword()).getKeystore();
			certInfo.setPrivateKey((PrivateKey) ks.getKey(certInfo.getAlias(), kInfo.getPassword()));
		} catch (KeyToolsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SignTools sTools = new SignTools();
		sTools.SignData(null, certInfo, "c:/dev/_test.sql", isInclude);

	}


    public void generateCrl(String aliasEmetteur, CrlValue crlValue) throws Exception {

		KeyStoreHelper ktools = new KeyStoreHelper();
		CertificateValue certSign;
		try {
			//FIXME
			certSign = ktools.findCertificateAndPrivateKeyByAlias(null, aliasEmetteur);
            X509CRL xCRL = CrlTools.generateCrl(certSign, crlValue);
            CrlTools.saveCRL(xCRL, crlValue.getPath());
		} catch (Exception e) {
			// log.error
			throw e;
		}

	}

}
