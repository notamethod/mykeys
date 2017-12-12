package org.dpr.mykeys.app.certificate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.keystore.InternalKeystores;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;

public class CertificateHelper {
	KeyTools ktool;
	CertificateInfo certInfo;
	public static final Log log = LogFactory.getLog(CertificateHelper.class);

	public CertificateHelper(CertificateInfo certInfo) {
		super();
		this.certInfo = certInfo;
	}

	public X509Certificate[] generateX509() throws CertificateException {

		CertificateBuilder builder = new CertificateBuilder();
		X509Certificate[] xCerts;
		try {
			xCerts = builder.generate(certInfo, findACCertificateInfo(certInfo.getIssuer()), false).getCertificates();
		} catch (Exception e) {
			throw new CertificateException(e);
		}
		return xCerts;
	}

	public X509Certificate[] generateX509(boolean isAC) throws CertificateException, KeyToolsException {

		CertificateBuilder builder = new CertificateBuilder();
		X509Certificate[] xCerts;

		try {
			CertificateInfo issuer = findACCertificateInfo(certInfo.getIssuer());
			if (issuer == null) {
				issuer = certInfo;
			}
			xCerts = builder.generate(certInfo, issuer, isAC).getCertificates();
		} catch (Exception e) {
			log.error(e);
			throw new CertificateException(e);
		}
		return xCerts;
	}


	public X509Certificate[] generateCrlToFix() throws CertificateException {
		// if (ktool == null) {
		// ktool = new KeyTools();
		// }
		// X509Certificate[] xCerts;
		// try {
		// xCerts = ktool.genererX509(certInfo, certInfo.getIssuer(), false);
		// } catch (Exception e) {
		// throw new CertificateException(e);
		// }
		// return xCerts;
		return null;
	}

	public String keyUsageToString() {
		String value = "";
		boolean[] keyUsage = certInfo.getKeyUsage();
		boolean isKeyUsage = false;
		if (keyUsage == null) {
			return "null";
		}
		for (int i = 0; i < keyUsage.length; i++) {
			if (keyUsage[i]) {
				isKeyUsage = true;
				value = value + ", " + X509Constants.keyUsageLabel[i];
			}
		}
		if (isKeyUsage) {
			return value.substring(1, value.length());
		} else {
			return null;
		}

	}

	public X509Certificate[] genererX509(KeyStore ks, CertificateInfo infoEmetteur, CertificateInfo certInfo,
			String aliasEmetteur, boolean isAC) throws Exception {
		CertificateBuilder builder = new CertificateBuilder();
		Usage usage = null;
		KeyStoreInfo ksInfo = null;
		if (!StringUtils.isBlank(aliasEmetteur)) {
			char[] password = InternalKeystores.password.toCharArray();
			ksInfo = InternalKeystores.getACKeystore();

			infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(aliasEmetteur, password));
			usage = Usage.CODESIGNING;

		}
		return builder.generate(certInfo, infoEmetteur, isAC, usage).getCertificates();
	}

	public X509Certificate[] genererX509(CertificateInfo certInfo, CertificateInfo infoEmetteur, boolean isAC)
			throws Exception {
		CertificateBuilder builder = new CertificateBuilder();
		return builder.generate(certInfo, infoEmetteur, isAC, Usage.DEFAULT).getCertificates();
	}

	public CertificateInfo findACCertificateInfo(String alias)
			throws KeyToolsException, UnrecoverableKeyException, NoSuchAlgorithmException {
		if (null == alias || alias.trim().isEmpty()) {
			return null;
		}
		KeystoreBuilder ksb = new KeystoreBuilder();
		KeyStore ks = ksb.load(InternalKeystores.getACKeystore()).get();
		
		CertificateInfo certInfo = new CertificateInfo();
		try {
			Certificate certificate = ks.getCertificate(alias);
			Certificate[] certs = ks.getCertificateChain(alias);
			if (ks.isKeyEntry(alias)) {
				certInfo.setContainsPrivateKey(true);
				certInfo.setPrivateKey((PrivateKey) ks.getKey(alias, InternalKeystores.password.toCharArray()));

			}
			X509Certificate x509Cert = (X509Certificate) certificate;
			certInfo.setSubjectMap(x509Cert.getSubjectDN().getName());
			// CertificateInfo certInfo2 = new CertificateInfo(alias, (X509Certificate)
			// certificate);
			certInfo.setPublicKey(certificate.getPublicKey());
			StringBuffer bf = new StringBuffer();
			if (certs == null) {
				log.error("chaine de certification nulle pour" + alias);
				return null;
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
		return certInfo;
	}

	/**
	 * @param fic the CSR file (pem encoded)
	 * @param certModel
	 * @param strIssuer name of the issuer certificate (CN)
	 * @return
	 * @throws KeyToolsException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public CertificateInfo generateFromCSR(String fic, String strIssuer)
			throws KeyToolsException, CertificateException, IOException {
		CertificateBuilder builder = new CertificateBuilder();
		
	
		X509Certificate xCert;

		try {
			CertificateInfo issuer = findACCertificateInfo(strIssuer);

			Object pemcsr;
			BufferedReader buf = new BufferedReader(new FileReader(fic));

			xCert= builder.generateFromCSR(buf, issuer).get();
		} catch (Exception e) {
			throw new CertificateException("error on certificate generation fro csr file "+fic, e);
		}
		CertificateInfo cert = new CertificateInfo(new X509Certificate[] {xCert});
		
		return cert;

	}
	
	public CertificateInfo generateFromCSR(InputStream fic, String strIssuer)
			throws KeyToolsException, CertificateException, IOException {
		CertificateBuilder builder = new CertificateBuilder();
		
		X509Certificate xCert;

		try {
			CertificateInfo issuer = findACCertificateInfo(strIssuer);

			Object pemcsr;
			BufferedReader buf = new BufferedReader(new InputStreamReader(fic));

			xCert= builder.generateFromCSR(buf, issuer).get();
		} catch (Exception e) {
			throw new CertificateException("error on certificate generation fro csr file "+fic, e);
		}
		CertificateInfo cert = new CertificateInfo(new X509Certificate[] {xCert});
		
		return cert;

	}

}
