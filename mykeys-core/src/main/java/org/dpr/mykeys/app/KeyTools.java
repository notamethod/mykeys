package org.dpr.mykeys.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;

public class KeyTools {
	// FIXME:en création de magasin si l'extension est saisie ne pas la mettre 2
	// fois.
	// FIXME: ne pas autoriser la saisie de la clé privée dans les magasins
	// internes
	final Log log = LogFactory.getLog(KeyTools.class);

	static String PUBKEYSTORE = "keystorePub.p12";

	static String TYPE_JKS = "JKS";

	static String TYPE_P12 = "PKCS12";

	static String EXT_JKS = ".jks";

	public static String EXT_P12 = ".p12";
	public static String EXT_PEM = ".pem";
	public static String EXT_DER = ".der";

	static String X509_TYPE = "X.509";

	public static final String BEGIN_PEM = "-----BEGIN CERTIFICATE-----";
	public static final String END_PEM = "-----END CERTIFICATE-----";

	public static final String BEGIN_KEY = "-----BEGIN RSA PRIVATE KEY-----";

	public static final String END_KEY = "-----END RSA PRIVATE KEY-----";

	private static final int NUM_ALLOWED_INTERMEDIATE_CAS = 0;

	public static void main(String[] args) {
		KeyTools test = new KeyTools();
		// test.KeyPairGen("RSA", 512, new CertificateInfo());
		// KeyStore ks= test.loadKeyStore("keystorePub.p12", TYPE_P12);
		// X509Certificate cert = test.genererX509();
		// test.saveCert("cert001", ks, cert);
		// test.saveKeyStore(ks, "password".toCharArray());
		// test.createKeyStore("JKS");
		// test.createKeyStore(TYPE_P12, "keystorePub.p12");
		Security.addProvider(new BouncyCastleProvider());
//		try {
//			test.generateCrl2();
//		} catch (UnrecoverableKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CRLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SignatureException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyToolsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// Set aa = Security.getProvider("BC").getServices();
		// Object o = Security.getProvider("BC").get("Signature");
		//
		// Set bb = Security.getProvider("BC").keySet();
		// Set cc = Security.getProvider("BC").getServices();

	}

	/**
	 * KeyPAirGen with String as keyLength
	 * 
	 * @param algo
	 * @param keyLength
	 * @param certModel
	 */
	public void keyPairGen(String algo, String keyLength, CertificateInfo certModel) {
		int kl = Integer.valueOf(keyLength).intValue();
		keyPairGen(algo, kl, certModel);
	}

	/**
	 * Key pair generation
	 * 
	 * @param algo
	 * @param keyLength
	 * @param certModel
	 */
	public void keyPairGen(String algo, int keyLength, CertificateInfo certModel) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("generating keypair: " + algo + " keypair: " + keyLength);
			}

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo, "BC");
			keyGen.initialize(keyLength);

			KeyPair keypair = keyGen.genKeyPair();
			certModel.setPrivateKey(keypair.getPrivate());
			certModel.setPublicKey(keypair.getPublic());

		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}

	}

	

	@Deprecated
	public void addCertToKeyStoreOld(X509Certificate cert, KeyStoreInfo ksInfo, CertificateInfo certInfo)
			throws KeyToolsException {
//		KeyStore kstore = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword());
//		saveCert(kstore, cert, certInfo);
//		saveKeyStore(kstore, ksInfo);
	}



	/**
	 * .
	 * 
	 * 
	 * @param kstore
	 * @param xCerts
	 * @param certInfo
	 * @throws KeyToolsException
	 */
	@Deprecated
	protected void saveCertChain(KeyStore kstore, X509Certificate[] xCerts, CertificateInfo certInfo)
			throws KeyToolsException {
		try {
			if (certInfo.getPrivateKey() == null) {
				// kstore.setCertificateEntry(certInfo.getAlias(), cert);
			} else {
				// FIXME: isinternal: password = kspwd
				kstore.setKeyEntry(certInfo.getAlias(), certInfo.getPrivateKey(), certInfo.getPassword(), xCerts);
			}

			// ks.setCertificateEntry(alias, cer);
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Sauvegarde du certificat impossible:" + certInfo.getAlias(), e);
		}

	}
	
	public void saveKeyStore(KeyStore ks, KeyStoreInfo ksInfo) throws KeyToolsException {

		try {
			OutputStream fos = new FileOutputStream(new File(ksInfo.getPath()));
			ks.store(fos, ksInfo.getPassword());
			fos.close();
		} catch (FileNotFoundException e) {
			throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
		} catch (CertificateException e) {
			throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
		} catch (IOException e) {
			throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
		}
	}

	@Deprecated
	public void saveCert(KeyStore kstore, X509Certificate cert, CertificateInfo certInfo) throws KeyToolsException {
		try {
			// X509Certificate x509Cert = (X509Certificate) cert;
			Certificate[] chaine = new Certificate[] { cert };
			if (certInfo.getPrivateKey() == null) {
				kstore.setCertificateEntry(certInfo.getAlias(), cert);
			} else {

				kstore.setKeyEntry(certInfo.getAlias(), certInfo.getPrivateKey(), certInfo.getPassword(), chaine);
			}

			// ks.setCertificateEntry(alias, cer);
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Sauvegarde du certificat impossible:" + certInfo.getAlias(), e);
		}
	}


	public String getKey(String alias, KeyStore keyStore, char[] motDePasse) throws GeneralSecurityException {

		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, motDePasse);
		if (privateKey != null) {
			return ("Clé privée  trouvée");
		} else {
			return ("Clé privée absente ");
		}
	}

	public PrivateKey getPrivateKey(String alias, KeyStore keyStore, char[] motDePasse)
			throws GeneralSecurityException {
		//
		// PrivateKeyEntry pkEntry = (PrivateKeyEntry) keyStore.getEntry(alias,
		// new KeyStore.PasswordProtection(motDePasse));
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, motDePasse);
		if (privateKey != null) {
			return privateKey;
		} else {
			throw new GeneralSecurityException("Clé privée absente ");

		}
	}

	

	public void importX509CertOld(String alias, KeyStoreInfo ksInfo, String fileName, String typeCert, char[] charArray)
			throws KeyToolsException, FileNotFoundException, CertificateException, GeneralSecurityException {
		return ;

	}

	



	// PolicyInformation pi = new PolicyInformation(new
	// ASN1ObjectIdentifier(certProfile.getCertificatePolicyId()));

	// -- Parche para el policyNotice
	// PolicyInformation pi =
	// getPolicyInformation(certProfile.getCertificatePolicyId(),
	// certProfile.getCpsUrl(), certProfile.getUserNoticeText());
	// -- /Parche para el policyNotice

	

	public void exportPublicKeyOld(CertificateInfo certInfo, String fName) throws KeyToolsException {
		/* save the public key in a file */
		try {
			// path, certInfo.getAlias()
			byte[] pubKey = certInfo.getPublicKey().getEncoded();
			FileOutputStream keyfos = new FileOutputStream(new File(fName + ".pub"));
			keyfos.write(pubKey);
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé publique impossible:" + certInfo.getAlias(), e);
		}
	}

	public void exportDer(CertificateInfo certInfo, String fName) throws KeyToolsException {
		/* save the public key in a file */
		try {

			FileOutputStream keyfos = new FileOutputStream(new File(fName + ".der"));
			keyfos.write(certInfo.getCertificate().getEncoded());
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé publique impossible:" + certInfo.getAlias(), e);
		}
	}

	public void exportPem(CertificateInfo certInfo, String fName) throws KeyToolsException {
		/* save the public key in a file */
		try {
			List<String> lines = new ArrayList<String>();
			lines.add(BEGIN_PEM);
			// FileUtils.writeLines(file, lines)
			File f = new File(fName + ".pem");
			// FileOutputStream keyfos = new FileOutputStream(new File(fName
			// + ".pem"));
			byte[] b = Base64.encodeBase64(certInfo.getCertificate().getEncoded());
			String tmpString = new String(b);
			String[] datas = tmpString.split("(?<=\\G.{64})");
			for (String data : datas) {
				lines.add(data);
			}

			lines.add(END_PEM);
			FileUtils.writeLines(f, lines);
			// keyfos.write(certInfo.getCertificate().getEncoded());
			// keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé publique impossible:" + certInfo.getAlias(), e);
		}
	}

	/**
	 * get a random BigInteger
	 * 
	 * @param numBits
	 * @return
	 */
	public static BigInteger RandomBI(int numBits) {
		SecureRandom random = new SecureRandom();
		// byte bytes[] = new byte[20];
		// random.nextBytes(bytes);
		BigInteger bi = new BigInteger(numBits, random);
		return bi;

	}

	



	public CRLDistPoint getDistributionPoints(X509Certificate certX509) {

		X509CertificateObject certificateImpl = (X509CertificateObject) certX509;

		byte[] extension = certificateImpl.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

		if (extension == null) {
			if (log.isWarnEnabled()) {
				log.warn("Pas de CRLDistributionPoint pour: " + certificateImpl.getSubjectDN());//
			}
			return null;
		}

		CRLDistPoint distPoints = null;

		try {
			distPoints = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(extension));
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Extension de CRLDistributionPoint non reconnue pour: " + certificateImpl.getSubjectDN());//
			}
			if (log.isDebugEnabled()) {
				log.debug(e);
			}

		}
		return distPoints;

	}



	public X509CRL generateCrl(X509Certificate certSign, CrlInfo crlInfo, Key privateKey) throws KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException, InvalidKeyException, CRLException, IllegalStateException, SignatureException {

		Calendar calendar = Calendar.getInstance();

		X509V2CRLGenerator crlGen = new X509V2CRLGenerator();

		Date now = new Date();
		Date nextUpdate = calendar.getTime();

		// crlGen.setIssuerDN((X500Principal) certSign.getIssuerDN());
		crlGen.setIssuerDN(certSign.getSubjectX500Principal());
		String signAlgo = "SHA1WITHRSAENCRYPTION";
		crlGen.setThisUpdate(crlInfo.getThisUpdate());
		crlGen.setNextUpdate(crlInfo.getNextUpdate());
		crlGen.setSignatureAlgorithm(signAlgo);
		// BigInteger bi = new BigInteger("816384897");
		// crlGen.addCRLEntry(BigInteger.ONE, now,
		// CRLReason.privilegeWithdrawn);
		BigInteger bi = new BigInteger("155461028");
		crlGen.addCRLEntry(bi, new Date(), CRLReason.privilegeWithdrawn);

		crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
				new AuthorityKeyIdentifierStructure(certSign));
		crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(crlInfo.getNumber()));

		X509CRL crl = crlGen.generate((PrivateKey) privateKey, "BC");
		// OutputStream os = new FileOutputStream(new
		// File("./certificats/crlrevoke.crl"));
		// os.write(crl.getEncoded());
		return crl;

	}

	public void revokeCert(X509Certificate cert, X509CRL crl) throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException,
			CRLException, IllegalStateException, SignatureException, KeyToolsException {

		// crl.

	}



	// public void timeStamp(KeyStoreInfo ksInfo, CertificateInfo certInfo){
	// TimeStampTokenGenerator ts = new TimeStampTokenGenerator(
	// }

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * @param certSign
	 * @param crlInfo
	 * @return
	 * @throws CertificateParsingException
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws IllegalStateException
	 * @throws CRLException
	 * @throws InvalidKeyException
	 */
	public X509CRL generateCrl(CertificateInfo certSign, CrlInfo crlInfo)
			throws CertificateParsingException, InvalidKeyException, CRLException, IllegalStateException,
			NoSuchProviderException, NoSuchAlgorithmException, SignatureException {

		X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
		// crlGen.setIssuerDN((X500Principal) certSign.getIssuerDN());
		crlGen.setIssuerDN(certSign.getCertificate().getSubjectX500Principal());
		String signAlgo = "SHA1WITHRSAENCRYPTION";
		crlGen.setThisUpdate(crlInfo.getThisUpdate());
		crlGen.setNextUpdate(crlInfo.getNextUpdate());
		crlGen.setSignatureAlgorithm(signAlgo);

		crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
				new AuthorityKeyIdentifierStructure(certSign.getCertificate()));
		crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(crlInfo.getNumber()));

		X509CRL crl = crlGen.generate((PrivateKey) certSign.getPrivateKey(), "BC");
		return crl;
	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * 
	 * @param xCerts
	 * @throws IOException
	 * @throws CRLException
	 */
	public void saveCRL(X509CRL crl, File crlFile) throws CRLException, IOException {
		OutputStream output = new FileOutputStream(crlFile);
		IOUtils.write(crl.getEncoded(), output);

	}

	



	private void setDurationOld(CertificateInfo certModel, X509V3CertificateGenerator certGen) {

		if (null == certModel.getNotBefore()) {
			certModel.setNotBefore(new Date());
		}
		certGen.setNotBefore(certModel.getNotBefore());
		if (null == certModel.getNotAfter()) {

			LocalDateTime ldt = LocalDateTime.ofInstant(certModel.getNotBefore().toInstant(), ZoneId.systemDefault());

			ZonedDateTime zdt = ldt.plusYears(certModel.getDuration()).atZone(ZoneId.systemDefault());
			certModel.setNotAfter(Date.from(zdt.toInstant()));
		}
		certGen.setNotAfter((certModel.getNotAfter()));
	}


}
