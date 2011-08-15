package org.dpr.mykeys.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.PolicyQualifierId;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.UserNotice;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyStoreInfo.StoreType;

public class KeyTools {
	final Log log = LogFactory.getLog(KeyTools.class);

	static String PUBKEYSTORE = "keystorePub.p12";

	static String TYPE_JKS = "JKS";

	static String TYPE_P12 = "PKCS12";

	static String EXT_JKS = ".jks";

	static String EXT_P12 = ".p12";

	static String X509_TYPE = "X.509";

	static final String BEGIN_PEM = "-----BEGIN CERTIFICATE-----";
	static final String END_PEM = "-----END CERTIFICATE-----";

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
		try {
			test.generateCrl2();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CRLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyToolsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void keyPairGen(String algo, String keyLength,
			CertificateInfo certModel) {
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
				log.debug("generating keypair: " + algo + " keypair: "
						+ keyLength);
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

	/**
	 * Create a keystore of type 'ksType' with filename 'name'
	 * 
	 * @param format
	 *            .toString()
	 * @param name
	 * @param password
	 * @throws Exception
	 */
	public KeyStore createKeyStore(StoreFormat format, String name,
			char[] password) throws Exception {
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(format.toString());

			ks.load(null, password);
			OutputStream fos = new FileOutputStream(new File(name));
			ks.store(fos, password);
			fos.close();
		} catch (Exception e) {
			throw new Exception(e);
		}
		return ks;

	}

	public KeyStore loadKeyStore(String ksName, String type, char[] pwd)
			throws KeyToolsException {
		// KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		KeyStore ks = null;
		try {
			try {
				ks = KeyStore.getInstance(type, "BC");
			} catch (Exception e) {
				ks = KeyStore.getInstance("JKS");
			}

			// get user password and file input stream

			java.io.FileInputStream fis = new java.io.FileInputStream(ksName);
			ks.load(fis, pwd);
			fis.close();
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Echec du chargement de:" + ksName, e);

		} catch (FileNotFoundException e) {
			throw new KeyToolsException("Fichier non trouvé:" + ksName + ", "
					+ e.getCause(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyToolsException("Format inconnu:" + ksName + ", "
					+ e.getCause(), e);
		} catch (CertificateException e) {
			throw new KeyToolsException("Echec du chargement de:" + ksName
					+ ", " + e.getCause(), e);
		} catch (IOException e) {
			throw new KeyToolsException("Echec du chargement de:" + ksName
					+ ", " + e.getCause(), e);
		}
		return ks;

	}

	public KeyStore loadKeyStore2(String ksName, String type, char[] pwd)
			throws KeyToolsException {
		// KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		KeyStore ks = null;
		try {
			try {
				ks = KeyStore.getInstance(type, "BC");
			} catch (Exception e) {
				ks = KeyStore.getInstance("JKS");
			}

			// get user password and file input stream

			java.io.FileInputStream fis = new java.io.FileInputStream(ksName);
			ks.load(fis, pwd);
			fis.close();
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Echec du chargement de:" + ksName, e);

		} catch (FileNotFoundException e) {
			throw new KeyToolsException("Fichier non trouvé:" + ksName, e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyToolsException("Format inconnu:" + ksName, e);
		} catch (CertificateException e) {
			throw new KeyToolsException("Echec du chargement de:" + ksName, e);
		} catch (IOException e) {
			throw new KeyToolsException("Echec du chargement de:" + ksName, e);
		}
		return ks;

	}

	public X509Certificate genererX509(CertificateInfo certModel, boolean isAC)
			throws Exception {

		keyPairGen(certModel.getAlgoPubKey(), certModel.getKeyLength(),
				certModel);

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(RandomBI(30));
		certGen.setIssuerDN(new X509Principal(new X509Principal(certModel
				.subjectMapToX509Name())));
		certGen.setPublicKey(certModel.getPublicKey());
		certGen.setNotBefore(certModel.getNotBefore());
		certGen.setNotAfter(certModel.getNotAfter());

		certGen.setSubjectDN(new X509Principal(certModel.subjectMapToX509Name()));
		certGen.setSignatureAlgorithm(certModel.getAlgoSig());

		if (isAC) {
			certGen.addExtension(X509Extensions.BasicConstraints, true,
					new BasicConstraints(true));
		} else {
			certGen.addExtension(X509Extensions.BasicConstraints, true,
					new BasicConstraints(false));
		}
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				certModel.getIntKeyUsage()));
		// certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
		// new AuthorityKeyIdentifierStructure( caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
				new SubjectKeyIdentifierStructure(certModel.getPublicKey()));

		PolicyInformation pi = getPolicyInformation(certModel.getPolicyID(),
				certModel.getPolicyCPS(), certModel.getPolicyNotice());

		DERSequence seq = new DERSequence(pi);
		certGen.addExtension(X509Extensions.CertificatePolicies.getId(), false,
				seq);

		if (certModel.getCrlDistributionURL() != null) {
			DistributionPoint[] dp = new DistributionPoint[1];
			DEROctetString oct = new DEROctetString(certModel
					.getCrlDistributionURL().getBytes());
			DistributionPointName dpn = new DistributionPointName(
					new GeneralNames(new GeneralName(GeneralName.dNSName,
							certModel.getCrlDistributionURL())));
			dp[0] = new DistributionPoint(dpn, null, null);
			certGen.addExtension(X509Extensions.CRLDistributionPoints, true,
					new CRLDistPoint(dp));
		}

		// certGen.addExtension(X509Extensions.CertificatePolicies, false,
		// pi);
		// PolicyInformation pi = new PolicyInformation(new
		// DERObjectIdentifier(certProfile.getCertificatePolicyId()));

		// -- Parche para el policyNotice
		// PolicyInformation pi =
		// getPolicyInformation(certProfile.getCertificatePolicyId(),
		// certProfile.getCpsUrl(), certProfile.getUserNoticeText());
		// -- /Parche para el policyNotice

		// PolicyInformation pol = new
		// PolicyInformation(X509Extensions.AuthorityKeyIdentifier);

		// certGen.addExtension(X509Extensions.CertificatePolicies, false,
		// new CertificatePolicies( certModel.getPublicKey()));

		// gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
		// new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

		X509Certificate cert = certGen.generate(certModel.getPrivateKey());
		try {
			cert.checkValidity(new Date());
		} catch (CertificateExpiredException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
		} catch (CertificateNotYetValidException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
		}

		cert.verify(certModel.getPublicKey());

		return cert;

	}

	public X509Certificate[] genererX509(CertificateInfo certModel,
			CertificateInfo certIssuer, boolean isAC) throws Exception {

		keyPairGen(certModel.getAlgoPubKey(), certModel.getKeyLength(),
				certModel);

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		BigInteger bi = RandomBI(30);
		certGen.setSerialNumber(bi);
		if (StringUtils.isBlank(certModel.getAlias())) {
			certModel.setAlias(bi.toString(16));
		}
		if (certIssuer.getCertificate() != null) {
			certGen.setIssuerDN(certIssuer.getCertificate()
					.getSubjectX500Principal());
		} else {
			certGen.setIssuerDN(new X509Principal(new X509Principal(certModel
					.subjectMapToX509Name())));
		}

		certGen.setPublicKey(certModel.getPublicKey());
		certGen.setNotBefore(certModel.getNotBefore());
		certGen.setNotAfter(certModel.getNotAfter());

		certGen.setSubjectDN(new X509Principal(certModel.subjectMapToX509Name()));
		certGen.setSignatureAlgorithm(certModel.getAlgoSig());

		if (isAC) {
			certGen.addExtension(X509Extensions.BasicConstraints, true,
					new BasicConstraints(true));
		} else {
			certGen.addExtension(X509Extensions.BasicConstraints, true,
					new BasicConstraints(false));
		}
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				certModel.getIntKeyUsage()));
		// certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
		// new AuthorityKeyIdentifierStructure( caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
				new SubjectKeyIdentifierStructure(certModel.getPublicKey()));

		// FIXME: à vérifier en cas de auto signé
		if (certIssuer.getCertificate() != null) {
			certGen.addExtension(
					X509Extensions.AuthorityKeyIdentifier,
					false,
					new AuthorityKeyIdentifierStructure(certIssuer
							.getCertificate()));
		} else {
			certGen.addExtension(
					X509Extensions.AuthorityKeyIdentifier,
					false,
					new AuthorityKeyIdentifierStructure(certModel
							.getPublicKey()));
		}

		if (certModel.getPolicyCPS() != null) {
			PolicyInformation pi = getPolicyInformation(
					certModel.getPolicyID(), certModel.getPolicyCPS(),
					certModel.getPolicyNotice());

			DERSequence seq = new DERSequence(pi);
			certGen.addExtension(X509Extensions.CertificatePolicies.getId(),
					false, seq);
		}
		// gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
		// new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

		// point de distribution des CRL
		if (certModel.getCrlDistributionURL() != null) {
			DistributionPoint[] dp = new DistributionPoint[1];
			DEROctetString oct = new DEROctetString(certModel
					.getCrlDistributionURL().getBytes());
			DistributionPointName dpn = new DistributionPointName(
					new GeneralNames(new GeneralName(GeneralName.dNSName,
							certModel.getCrlDistributionURL())));
			dp[0] = new DistributionPoint(dpn, null, null);
			certGen.addExtension(X509Extensions.CRLDistributionPoints, true,
					new CRLDistPoint(dp));
		} else {
			if (certIssuer.getCertificate() != null) {
				CRLDistPoint dpoint = getDistributionPoints(certIssuer
						.getCertificate());
				if (dpoint != null) {
					certGen.addExtension(X509Extensions.CRLDistributionPoints,
							true, dpoint);
				}
			}
		}

		X509Certificate cert = certGen.generate(certIssuer.getPrivateKey());
		cert.checkValidity(new Date());

		cert.verify(certIssuer.getPublicKey());
		X509Certificate[] certChain = null;
		// FIXME: gérer la chaine de l'émetteur
		if (certIssuer.getCertificate() != null) {
			certChain = new X509Certificate[2];
			certChain[0] = cert;
			certChain[1] = certIssuer.getCertificate();
		} else {
			certChain = new X509Certificate[] { cert };
		}

		return certChain;

	}

	@Deprecated
	public void addCertToKeyStore(X509Certificate cert, KeyStoreInfo ksInfo,
			CertificateInfo certInfo) throws KeyToolsException {
		KeyStore kstore = loadKeyStore(ksInfo.getPath(),
				ksInfo.getStoreFormat(), ksInfo.getPassword());
		saveCert(kstore, cert, certInfo);
		saveKeyStore(kstore, ksInfo);
	}

	public void addCertToKeyStoreNew(X509Certificate cert, KeyStoreInfo ksInfo,
			CertificateInfo certInfo) throws KeyToolsException {
		KeyStore kstore = loadKeyStore(ksInfo.getPath(),
				ksInfo.getStoreFormat(), ksInfo.getPassword());
		saveCertChain(kstore, cert, certInfo);
		saveKeyStore(kstore, ksInfo);
	}

	public void addCertToKeyStoreNew(X509Certificate[] xCerts,
			KeyStoreInfo ksInfo, CertificateInfo certInfo)
			throws KeyToolsException {
		KeyStore kstore = loadKeyStore(ksInfo.getPath(),
				ksInfo.getStoreFormat(), ksInfo.getPassword());
		saveCertChain(kstore, xCerts, certInfo);
		saveKeyStore(kstore, ksInfo);
	}

	public void deleteCertificate(KeyStoreInfo ksInfo,
			CertificateInfo certificateInfo) throws KeyToolsException,
			KeyStoreException {

		KeyStore ks = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
				ksInfo.getPassword());
		ks.deleteEntry(certificateInfo.getAlias());
		saveKeyStore(ks, ksInfo);

	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * <pre>
	 * <b>Algorithme : </b>
	 * DEBUT
	 *    
	 * FIN
	 * </pre>
	 * 
	 * @param kstore
	 * @param xCerts
	 * @param certInfo
	 * @throws KeyToolsException
	 */
	private void saveCertChain(KeyStore kstore, X509Certificate[] xCerts,
			CertificateInfo certInfo) throws KeyToolsException {
		try {
			if (certInfo.getPrivateKey() == null) {
				// kstore.setCertificateEntry(certInfo.getAlias(), cert);
			} else {

				kstore.setKeyEntry(certInfo.getAlias(),
						certInfo.getPrivateKey(), certInfo.getPassword(),
						xCerts);
			}

			// ks.setCertificateEntry(alias, cer);
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Sauvegarde du certificat impossible:"
					+ certInfo.getAlias(), e);
		}

	}

	public void saveKeyStore(KeyStore ks, KeyStoreInfo ksInfo)
			throws KeyToolsException {

		try {
			OutputStream fos = new FileOutputStream(new File(ksInfo.getPath()));
			ks.store(fos, ksInfo.getPassword());
			fos.close();
		} catch (FileNotFoundException e) {
			throw new KeyToolsException(
					"Echec de sauvegarde du magasin impossible:"
							+ ksInfo.getPath(), e);
		} catch (KeyStoreException e) {
			throw new KeyToolsException(
					"Echec de sauvegarde du magasin impossible:"
							+ ksInfo.getPath(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyToolsException(
					"Echec de sauvegarde du magasin impossible:"
							+ ksInfo.getPath(), e);
		} catch (CertificateException e) {
			throw new KeyToolsException(
					"Echec de sauvegarde du magasin impossible:"
							+ ksInfo.getPath(), e);
		} catch (IOException e) {
			throw new KeyToolsException(
					"Echec de sauvegarde du magasin impossible:"
							+ ksInfo.getPath(), e);
		}
	}

	@Deprecated
	public void saveCert(KeyStore kstore, X509Certificate cert,
			CertificateInfo certInfo) throws KeyToolsException {
		try {
			// X509Certificate x509Cert = (X509Certificate) cert;
			Certificate[] chaine = new Certificate[] { cert };
			if (certInfo.getPrivateKey() == null) {
				kstore.setCertificateEntry(certInfo.getAlias(), cert);
			} else {

				kstore.setKeyEntry(certInfo.getAlias(),
						certInfo.getPrivateKey(), certInfo.getPassword(),
						chaine);
			}

			// ks.setCertificateEntry(alias, cer);
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Sauvegarde du certificat impossible:"
					+ certInfo.getAlias(), e);
		}
	}

	public void saveCertChain(KeyStore kstore, X509Certificate cert,
			CertificateInfo certInfo) throws KeyToolsException {
		try {
			// pas bonne chaine
			// X509Certificate x509Cert = (X509Certificate) cert;
			Certificate[] chaine = certInfo.getCertificateChain();
			if (certInfo.getPrivateKey() == null) {
				kstore.setCertificateEntry(certInfo.getAlias(), cert);
			} else {

				kstore.setKeyEntry(certInfo.getAlias(),
						certInfo.getPrivateKey(), certInfo.getPassword(),
						chaine);
			}

			// ks.setCertificateEntry(alias, cer);
		} catch (KeyStoreException e) {
			throw new KeyToolsException("Sauvegarde du certificat impossible:"
					+ certInfo.getAlias(), e);
		}
	}

	public void fillCertInfo(KeyStore ks, CertificateInfo certInfo, String alias) {

		try {
			Certificate certificate = ks.getCertificate(alias);
			fillCertInfo(certInfo, certificate);
			if (ks.isKeyEntry(alias)) {
				certInfo.setContainsPrivateKey(true);

			}
			Certificate[] certs = ks.getCertificateChain(alias);
			StringBuffer bf = new StringBuffer();
			if (certs == null) {
				log.error("chaine de certification nulle pour" + alias);
				return;
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

	public void fillCertInfo(CertificateInfo certInfo, Certificate certificate) {

		if (certificate instanceof X509Certificate) {
			certInfo.setCertificate((X509Certificate) certificate);
			Map<DERObjectIdentifier, String> oidMap = new HashMap<DERObjectIdentifier, String>();
			X509Certificate certX509 = (X509Certificate) certificate;
			certInfo.setAlgoPubKey(certificate.getPublicKey().getAlgorithm());
			certInfo.setAlgoSig(certX509.getSigAlgName());
			certInfo.setSignature(certX509.getSignature());
			if (certX509.getPublicKey() instanceof RSAPublicKey) {
				certInfo.setKeyLength(((RSAPublicKey) certX509.getPublicKey())
						.getModulus().bitLength());
				String aa = ((RSAPublicKey) certX509.getPublicKey())
						.getModulus().toString(16);
			}
			certInfo.setPublicKey(certX509.getPublicKey());

			// certInfo.setKeyLength("0");
			certX509.getSubjectX500Principal().getName("RFC2253");
			// certX509.getSubjectX500Principal().getName("RFC2253", map2);
			// certInfo.setX509PrincipalMap();
			// ASN1Set set = new ASN1Set();
			X509Name name = new X509Name(certX509.getSubjectX500Principal()
					.getName("RFC2253"));
			// X509AttributeCertStoreSelector
			// certX509.get
			// certX509.getBasicConstraints();
			// certX509.getExtendedKeyUsage()

			// certGen.addExtension("2.5.29.15", true, new X509KeyUsage(
			// X509KeyUsage.encipherOnly));
			// certInfo.setPrivateKey(privateKey)
			certInfo.x509NameToMap(name);
			certInfo.setKeyUsage(certX509.getKeyUsage());
			certInfo.setNotBefore(certX509.getNotBefore());
			certInfo.setNotAfter(certX509.getNotAfter());
			X509Util.getExtensions(certX509);
			// certX509.getExtensionValue();
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				md.update(certX509.getEncoded());

				certInfo.setDigestSHA1(md.digest());
				md = MessageDigest.getInstance("SHA-256");
				md.update(certX509.getEncoded());

				certInfo.setDigestSHA256(md.digest());

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public String getKey(String alias, KeyStore keyStore, char[] motDePasse)
			throws GeneralSecurityException {

		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, motDePasse);
		if (privateKey != null) {
			return ("Clé privée  trouvée");
		} else {
			return ("Clé privée absente ");
		}
	}
	
	public PrivateKey getPrivateKey(String alias, KeyStore keyStore, char[] motDePasse)
			throws GeneralSecurityException {

		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, motDePasse);
		if (privateKey != null) {
			return privateKey;
		} else {
			throw new GeneralSecurityException("Clé privée absente ");
		
		}
	}	

	/**
	 * Chargement certificat X509 à partir d'un flux.
	 * 
	 * <BR>
	 * 
	 * <pre>
	 * b&gt;Algorithme : &lt;/b&gt;
	 * EBUT
	 *    
	 * IN
	 * </pre>
	 * 
	 * @param aCertStream
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static X509Certificate loadX509Cert(InputStream aCertStream)
			throws GeneralSecurityException {
		// création d'une fabrique de certificat X509
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		// chargement du certificat
		X509Certificate cert = (X509Certificate) cf
				.generateCertificate(aCertStream);
		return cert;
	}

	private static Set<X509Certificate> loadX509Certs(InputStream aCertStream)
			throws GeneralSecurityException {

		CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

		// chargement du certificat
		Collection<X509Certificate> certs = (Collection<X509Certificate>) cf
				.generateCertificates(aCertStream);
		Set<X509Certificate> certificates = new HashSet<X509Certificate>(certs);
		return certificates;
	}

	public void importX509Cert(String alias, KeyStoreInfo ksInfo,
			String fileName, String typeCert, char[] charArray)
			throws KeyToolsException, KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException {
		CertificateInfo certInfo = new CertificateInfo();
		if (typeCert.equals(TYPE_P12)) {
			KeyStore ks = loadKeyStore(fileName, TYPE_P12, charArray);
			String aliasOri = null;
			Enumeration<String> enumKs = ks.aliases();
			while (enumKs.hasMoreElements()) {
				aliasOri = enumKs.nextElement();
			}

			Certificate cert = ks.getCertificate(aliasOri);

			fillCertInfo(certInfo, cert);
			certInfo.setAlias(alias);
			certInfo.setPassword(charArray);
			certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
			certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, charArray));
			// addCertToKeyStore((X509Certificate)cert, ksInfo, certInfo);
			addCertToKeyStoreNew((X509Certificate) cert, ksInfo, certInfo);
		} else {
			InputStream is = null;
			try {
				is = new FileInputStream(new File(fileName));
				X509Certificate cert = loadX509Cert(is);

				fillCertInfo(certInfo, cert);
				certInfo.setAlias(alias);
				addCertToKeyStore(cert, ksInfo, certInfo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyToolsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void loadX509Certs(String fileName) throws KeyToolsException,
			KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {

		NodeInfo nInfo = new BagInfo(fileName);

		InputStream is = null;
		try {
			is = new FileInputStream(new File(fileName));
			Set<X509Certificate> certs = loadX509Certs(is);

			for (X509Certificate cert : certs) {
				CertificateInfo certInfo = new CertificateInfo();
				fillCertInfo(certInfo, cert);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	PolicyInformation getPolicyInformation(String policyOID, String cps,
			String unotice) {

		ASN1EncodableVector qualifiers = new ASN1EncodableVector();

		if (!StringUtils.isEmpty(unotice)) {
			UserNotice un = new UserNotice(null, new DisplayText(
					DisplayText.CONTENT_TYPE_BMPSTRING, unotice));
			PolicyQualifierInfo pqiUNOTICE = new PolicyQualifierInfo(
					PolicyQualifierId.id_qt_unotice, un);
			qualifiers.add(pqiUNOTICE);
		}
		if (!StringUtils.isEmpty(cps)) {
			PolicyQualifierInfo pqiCPS = new PolicyQualifierInfo(cps);
			qualifiers.add(pqiCPS);
		}

		PolicyInformation policyInformation = new PolicyInformation(
				new DERObjectIdentifier(policyOID), new DERSequence(qualifiers));

		return policyInformation;

	}

	// PolicyInformation pi = new PolicyInformation(new
	// DERObjectIdentifier(certProfile.getCertificatePolicyId()));

	// -- Parche para el policyNotice
	// PolicyInformation pi =
	// getPolicyInformation(certProfile.getCertificatePolicyId(),
	// certProfile.getCpsUrl(), certProfile.getUserNoticeText());
	// -- /Parche para el policyNotice

	public void exportPrivateKey(CertificateInfo certInfo, KeyStoreInfo ksInfo,
			char[] password, String fName) throws KeyToolsException {
		/* save the private key in a file */

		try {
			KeyStore ks = loadKeyStore(ksInfo.getPath(),
					ksInfo.getStoreFormat(), ksInfo.getPassword());
			PrivateKey privateKey = null;
			if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) {
				privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(),
						ksInfo.getPassword());
			} else {
				privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(),
						password);
			}
			byte[] privKey = privateKey.getEncoded();
			FileOutputStream keyfos = new FileOutputStream(new File(fName
					+ ".key"));
			keyfos.write(privKey);
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé privée impossible:"
					+ certInfo.getAlias(), e);
		}
	}

	public void exportPublicKeyOld(CertificateInfo certInfo, String fName)
			throws KeyToolsException {
		/* save the public key in a file */
		try {
			// path, certInfo.getAlias()
			byte[] pubKey = certInfo.getPublicKey().getEncoded();
			FileOutputStream keyfos = new FileOutputStream(new File(fName
					+ ".pub"));
			keyfos.write(pubKey);
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé publique impossible:"
					+ certInfo.getAlias(), e);
		}
	}

	public void exportDer(CertificateInfo certInfo, String fName)
			throws KeyToolsException {
		/* save the public key in a file */
		try {

			FileOutputStream keyfos = new FileOutputStream(new File(fName
					+ ".der"));
			keyfos.write(certInfo.getCertificate().getEncoded());
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new KeyToolsException("Export de la clé publique impossible:"
					+ certInfo.getAlias(), e);
		}
	}

	public void exportPem(CertificateInfo certInfo, String fName)
			throws KeyToolsException {
		/* save the public key in a file */
		try {
			List<String> lines = new ArrayList<String>();
			lines.add(BEGIN_PEM);
			// FileUtils.writeLines(file, lines)
			File f = new File(fName + ".pem");
			// FileOutputStream keyfos = new FileOutputStream(new File(fName
			// + ".pem"));
			byte[] b = Base64.encodeBase64(certInfo.getCertificate()
					.getEncoded());
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
			throw new KeyToolsException("Export de la clé publique impossible:"
					+ certInfo.getAlias(), e);
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

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * <pre>
	 * <b>Algorithme : </b>
	 * DEBUT
	 *    
	 * FIN
	 * </pre>
	 * 
	 * @param certInfo
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public X509Certificate[] genererX509(CertificateInfo certInfo,
			String aliasEmetteur, boolean isAC) throws Exception {

		KeyStore ks = null;
		if (!StringUtils.isBlank(aliasEmetteur)) {
			char[] password = InternalKeystores.password.toCharArray();
			ks = loadKeyStore(InternalKeystores.getACPath(), StoreFormat.JKS,
					InternalKeystores.password.toCharArray());
			CertificateInfo infoEmetteur = new CertificateInfo();
			fillCertInfo(ks, infoEmetteur, aliasEmetteur);
			infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(aliasEmetteur,
					password));
			return genererX509(certInfo, infoEmetteur, isAC);
		} else {
			return genererX509(certInfo, certInfo, isAC);
		}
	}

	public CertificateInfo getCertificateACByAlias(String aliasEmetteur)
			throws Exception {

		KeyStore ks = null;

		char[] password = InternalKeystores.password.toCharArray();
		ks = loadKeyStore(InternalKeystores.getACPath(), StoreFormat.JKS,
				InternalKeystores.password.toCharArray());
		CertificateInfo infoEmetteur = new CertificateInfo();
		fillCertInfo(ks, infoEmetteur, aliasEmetteur);
		infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(aliasEmetteur,
				password));
		return infoEmetteur;

	}

	public CRLDistPoint getDistributionPoints(X509Certificate certX509) {

		X509CertificateObject certificateImpl = (X509CertificateObject) certX509;

		byte[] extension = certificateImpl
				.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

		if (extension == null) {
			if (log.isWarnEnabled()) {
				log.warn("Pas de CRLDistributionPoint pour: "
						+ certificateImpl.getSubjectDN());//
			}
			return null;
		}

		CRLDistPoint distPoints = null;

		try {
			distPoints = CRLDistPoint.getInstance(X509ExtensionUtil
					.fromExtensionValue(extension));
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Extension de CRLDistributionPoint non reconnue pour: "
						+ certificateImpl.getSubjectDN());//
			}
			if (log.isDebugEnabled()) {
				log.debug(e);
			}

		}
		return distPoints;

	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * <pre>
	 * <b>Algorithme : </b>
	 * DEBUT
	 *    
	 * FIN
	 * </pre>
	 * 
	 * @param path
	 * @param storeFormat
	 * @param password
	 * @return
	 * @throws KeyToolsException
	 */
	public KeyStore loadKeyStore(String path, StoreFormat storeFormat,
			char[] password) throws KeyToolsException {
		// TODO Auto-generated method stub
		return loadKeyStore(path, StoreFormat.getValue(storeFormat), password);
	}

	public KeyStore importStore(String path, StoreFormat storeFormat,
			char[] password) throws KeyToolsException,
			UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		switch (storeFormat) {
		case JKS:
		case PKCS12:
			return loadKeyStore(path, StoreFormat.getValue(storeFormat),
					password);

		default:
			loadX509Certs(path);
			return null;

		}
	}

	public X509CRL generateCrl(X509Certificate certSign, CrlInfo crlInfo,
			Key privateKey) throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException, InvalidKeyException, CRLException,
			IllegalStateException, SignatureException {

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
		crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(
				crlInfo.getNumber()));

		X509CRL crl = crlGen.generate((PrivateKey) privateKey, "BC");
		// OutputStream os = new FileOutputStream(new
		// File("./certificats/crlrevoke.crl"));
		// os.write(crl.getEncoded());
		return crl;

	}

	public void revokeCert(X509Certificate cert, X509CRL crl)
			throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException, InvalidKeyException, CRLException,
			IllegalStateException, SignatureException, KeyToolsException {

		// crl.

	}

	public void generateCrl2() throws KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException,
			InvalidKeyException, CRLException, IllegalStateException,
			SignatureException, KeyToolsException {

		String kst = "C:/Documents and Settings/n096015/.myKeys/mykeysAc.jks";

		char[] password = "mKeys983178".toCharArray();
		KeyStore ks = loadKeyStore(kst, StoreFormat.JKS, password);
		CertificateInfo cinfo = new CertificateInfo();
		fillCertInfo(ks, cinfo, "MK DEV AC Intermediaire");
		Calendar nextupdate = Calendar.getInstance();
		CrlInfo crlInfo = new CrlInfo();
		nextupdate.add(Calendar.DAY_OF_YEAR, 30);
		crlInfo.setNextUpdate(nextupdate.getTime());
		Key key = ks.getKey("mk dev root ca", password);
		X509CRL crl = generateCrl(cinfo.getCertificate(), crlInfo, key);
		OutputStream os = new FileOutputStream("c:/dev/crl2.crl");
		os.write(crl.getEncoded());
		os.close();

	}

	// public void timeStamp(KeyStoreInfo ksInfo, CertificateInfo certInfo){
	// TimeStampTokenGenerator ts = new TimeStampTokenGenerator(
	// }

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * <pre>
	 * <b>Algorithme : </b>
	 * DEBUT
	 *    
	 * FIN
	 * </pre>
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
			throws CertificateParsingException, InvalidKeyException,
			CRLException, IllegalStateException, NoSuchProviderException,
			NoSuchAlgorithmException, SignatureException {

		X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
		// crlGen.setIssuerDN((X500Principal) certSign.getIssuerDN());
		crlGen.setIssuerDN(certSign.getCertificate().getSubjectX500Principal());
		String signAlgo = "SHA1WITHRSAENCRYPTION";
		crlGen.setThisUpdate(crlInfo.getThisUpdate());
		crlGen.setNextUpdate(crlInfo.getNextUpdate());
		crlGen.setSignatureAlgorithm(signAlgo);

		crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
				new AuthorityKeyIdentifierStructure(certSign.getCertificate()));
		crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(
				crlInfo.getNumber()));

		X509CRL crl = crlGen.generate((PrivateKey) certSign.getPrivateKey(),
				"BC");
		return crl;
	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * <pre>
	 * <b>Algorithme : </b>
	 * DEBUT
	 *    
	 * FIN
	 * </pre>
	 * 
	 * @param xCerts
	 * @throws IOException
	 * @throws CRLException
	 */
	public void saveCRL(X509CRL crl, File crlFile) throws CRLException,
			IOException {
		OutputStream output = new FileOutputStream(crlFile);
		IOUtils.write(crl.getEncoded(), output);

	}


}
