package org.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
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
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.app.KeyStoreInfo.StoreFormat;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
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
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

public class KeyTools {
    final Log log = LogFactory.getLog(KeyTools.class);

    static String PUBKEYSTORE = "keystorePub.p12";

    static String TYPE_JKS = "JKS";

    static String TYPE_P12 = "PKCS12";

    static String EXT_JKS = ".jks";

    static String EXT_P12 = ".p12";

    static String X509_TYPE = "X.509";

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
	Set aa = Security.getProvider("BC").getServices();
	Object o = Security.getProvider("BC").get("Signature");

	Set bb = Security.getProvider("BC").keySet();
	Set cc = Security.getProvider("BC").getServices();

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
     * @param ksType
     * @param name
     * @param password
     * @throws Exception
     */
    public void createKeyStore(String ksType, String name, char[] password)
	    throws Exception {
	try {
	    KeyStore ks = KeyStore.getInstance(ksType);

	    ks.load(null, password);
	    OutputStream fos = new FileOutputStream(new File(name));
	    ks.store(fos, password);
	    fos.close();
	} catch (Exception e) {
	    throw new Exception(e);
	}

    }

    public KeyStore loadKeyStore(String ksName, String type, char[] pwd)
	    throws KeyToolsException {
	// KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

	KeyStore ks = null;
	try {
	    try {
		ks = KeyStore.getInstance(type);
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

	certGen
		.setSubjectDN(new X509Principal(certModel
			.subjectMapToX509Name()));
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

	certGen.setSerialNumber(RandomBI(30));
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

	certGen
		.setSubjectDN(new X509Principal(certModel
			.subjectMapToX509Name()));
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
	    certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
		    new AuthorityKeyIdentifierStructure(certIssuer
			    .getCertificate()));
	} else {
	    certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
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
	KeyStore kstore = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		ksInfo.getPassword());
	saveCert(kstore, cert, certInfo);
	saveKeyStore(kstore, ksInfo);
    }

    public void addCertToKeyStoreNew(X509Certificate cert, KeyStoreInfo ksInfo,
	    CertificateInfo certInfo) throws KeyToolsException {
	KeyStore kstore = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		ksInfo.getPassword());
	saveCertChain(kstore, cert, certInfo);
	saveKeyStore(kstore, ksInfo);
    }

    public void addCertToKeyStoreNew(X509Certificate[] xCerts,
	    KeyStoreInfo ksInfo, CertificateInfo certInfo)
	    throws KeyToolsException {
	KeyStore kstore = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		ksInfo.getPassword());
	saveCertChain(kstore, xCerts, certInfo);
	saveKeyStore(kstore, ksInfo);
    }

    /**
     * .
     * 
     *<BR>
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

		kstore.setKeyEntry(certInfo.getAlias(), certInfo
			.getPrivateKey(), certInfo.getPassword(), xCerts);
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

		kstore.setKeyEntry(certInfo.getAlias(), certInfo
			.getPrivateKey(), certInfo.getPassword(), chaine);
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

		kstore.setKeyEntry(certInfo.getAlias(), certInfo
			.getPrivateKey(), certInfo.getPassword(), chaine);
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

    /**
     * Chargement certificat X509 à partir d'un flux.
     * 
     *<BR>
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
	    KeyStore ks = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		    ksInfo.getPassword());
	    PrivateKey privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(),
		    password);
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
     *<BR>
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
	    String alias, boolean isAC) throws Exception {

	KeyStore ks = null;
	if (!StringUtils.isBlank(alias)) {
	    char[] password = ACKeystore.password.toCharArray();
	    ks = loadKeyStore(ACKeystore.path, "AC", ACKeystore.password
		    .toCharArray());
	    CertificateInfo infoEmetteur = new CertificateInfo();
	    fillCertInfo(ks, infoEmetteur, alias);
	    infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(alias, password));
	    return genererX509(certInfo, infoEmetteur, isAC);
	} else {
	    return genererX509(certInfo, certInfo, isAC);
	}

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
		log
			.warn("Extension de CRLDistributionPoint non reconnue pour: "
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
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
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

    // algorithms.put("MD2WITHRSAENCRYPTION", new
    // DERObjectIdentifier("1.2.840.113549.1.1.2"));
    // algorithms.put("MD2WITHRSA", new
    // DERObjectIdentifier("1.2.840.113549.1.1.2"));
    // algorithms.put("MD5WITHRSAENCRYPTION", new
    // DERObjectIdentifier("1.2.840.113549.1.1.4"));
    // algorithms.put("MD5WITHRSA", new
    // DERObjectIdentifier("1.2.840.113549.1.1.4"));
    // algorithms.put("RSAWITHMD5", new
    // DERObjectIdentifier("1.2.840.113549.1.1.4"));
    // algorithms.put("SHA1WITHRSAENCRYPTION", new
    // DERObjectIdentifier("1.2.840.113549.1.1.5"));
    // algorithms.put("SHA1WITHRSA", new
    // DERObjectIdentifier("1.2.840.113549.1.1.5"));
    // algorithms.put("SHA224WITHRSAENCRYPTION",
    // PKCSObjectIdentifiers.sha224WithRSAEncryption);
    // algorithms.put("SHA224WITHRSA",
    // PKCSObjectIdentifiers.sha224WithRSAEncryption);
    // algorithms.put("SHA256WITHRSAENCRYPTION",
    // PKCSObjectIdentifiers.sha256WithRSAEncryption);
    // algorithms.put("SHA256WITHRSA",
    // PKCSObjectIdentifiers.sha256WithRSAEncryption);
    // algorithms.put("SHA384WITHRSAENCRYPTION",
    // PKCSObjectIdentifiers.sha384WithRSAEncryption);
    // algorithms.put("SHA384WITHRSA",
    // PKCSObjectIdentifiers.sha384WithRSAEncryption);
    // algorithms.put("SHA512WITHRSAENCRYPTION",
    // PKCSObjectIdentifiers.sha512WithRSAEncryption);
    // algorithms.put("SHA512WITHRSA",
    // PKCSObjectIdentifiers.sha512WithRSAEncryption);
    // algorithms.put("SHA1WITHRSAANDMGF1",
    // PKCSObjectIdentifiers.id_RSASSA_PSS);
    // algorithms.put("SHA224WITHRSAANDMGF1",
    // PKCSObjectIdentifiers.id_RSASSA_PSS);
    // algorithms.put("SHA256WITHRSAANDMGF1",
    // PKCSObjectIdentifiers.id_RSASSA_PSS);
    // algorithms.put("SHA384WITHRSAANDMGF1",
    // PKCSObjectIdentifiers.id_RSASSA_PSS);
    // algorithms.put("SHA512WITHRSAANDMGF1",
    // PKCSObjectIdentifiers.id_RSASSA_PSS);
    // algorithms.put("RSAWITHSHA1", new
    // DERObjectIdentifier("1.2.840.113549.1.1.5"));
    // algorithms.put("RIPEMD160WITHRSAENCRYPTION", new
    // DERObjectIdentifier("1.3.36.3.3.1.2"));
    // algorithms.put("RIPEMD160WITHRSA", new
    // DERObjectIdentifier("1.3.36.3.3.1.2"));
    // algorithms.put("SHA1WITHDSA", new
    // DERObjectIdentifier("1.2.840.10040.4.3"));
    // algorithms.put("DSAWITHSHA1", new
    // DERObjectIdentifier("1.2.840.10040.4.3"));
    // algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
    // algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
    // algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
    // algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
    // algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
    // algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
    // algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
    // algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
    // algorithms.put("GOST3411WITHGOST3410",
    // CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    // algorithms.put("GOST3410WITHGOST3411",
    // CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    // algorithms.put("GOST3411WITHECGOST3410",
    // CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    // algorithms.put("GOST3411WITHECGOST3410-2001",
    // CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    // algorithms.put("GOST3411WITHGOST3410-2001",
    // CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);

    // 2.16.840.1.114412.1.3.0.27

    // [5]: ObjectId: 2.5.29.32 Criticality=false
    // CertificatePolicies [
    // [CertificatePolicyId: [2.16.840.1.114412.1.3.0.1]
    // [PolicyQualifierInfo: [
    // qualifierID: 1.3.6.1.5.5.7.2.1 //cps
    // qualifier: 0000: 16 2E 68 74 74 70 3A 2F 2F 77 77 77 2E 64 69 67
    // ..http://www.dig
    // 0010: 69 63 65 72 74 2E 63 6F 6D 2F 73 73 6C 2D 63 70 icert.com/ssl-cp
    // 0020: 73 2D 72 65 70 6F 73 69 74 6F 72 79 2E 68 74 6D s-repository.htm
    //
    // ], PolicyQualifierInfo: [
    // qualifierID: 1.3.6.1.5.5.7.2.2
    // qualifier: 0000: 30 82 01 56 1E 82 01 52 00 41 00 6E 00 79 00 20
    // 0..V...R.A.n.y.
    // 0010: 00 75 00 73 00 65 00 20 00 6F 00 66 00 20 00 74 .u.s.e. .o.f. .t
    // 0020: 00 68 00 69 00 73 00 20 00 43 00 65 00 72 00 74 .h.i.s. .C.e.r.t
    // 0030: 00 69 00 66 00 69 00 63 00 61 00 74 00 65 00 20 .i.f.i.c.a.t.e.
    // 0040: 00 63 00 6F 00 6E 00 73 00 74 00 69 00 74 00 75 .c.o.n.s.t.i.t.u
    // 0050: 00 74 00 65 00 73 00 20 00 61 00 63 00 63 00 65 .t.e.s. .a.c.c.e
    // 0060: 00 70 00 74 00 61 00 6E 00 63 00 65 00 20 00 6F .p.t.a.n.c.e. .o
    // 0070: 00 66 00 20 00 74 00 68 00 65 00 20 00 44 00 69 .f. .t.h.e. .D.i
    // 0080: 00 67 00 69 00 43 00 65 00 72 00 74 00 20 00 43 .g.i.C.e.r.t. .C
    // 0090: 00 50 00 2F 00 43 00 50 00 53 00 20 00 61 00 6E .P./.C.P.S. .a.n
    // 00A0: 00 64 00 20 00 74 00 68 00 65 00 20 00 52 00 65 .d. .t.h.e. .R.e
    // 00B0: 00 6C 00 79 00 69 00 6E 00 67 00 20 00 50 00 61 .l.y.i.n.g. .P.a
    // 00C0: 00 72 00 74 00 79 00 20 00 41 00 67 00 72 00 65 .r.t.y. .A.g.r.e
    // 00D0: 00 65 00 6D 00 65 00 6E 00 74 00 20 00 77 00 68 .e.m.e.n.t. .w.h
    // 00E0: 00 69 00 63 00 68 00 20 00 6C 00 69 00 6D 00 69 .i.c.h. .l.i.m.i
    // 00F0: 00 74 00 20 00 6C 00 69 00 61 00 62 00 69 00 6C .t. .l.i.a.b.i.l
    // 0100: 00 69 00 74 00 79 00 20 00 61 00 6E 00 64 00 20 .i.t.y. .a.n.d.
    // 0110: 00 61 00 72 00 65 00 20 00 69 00 6E 00 63 00 6F .a.r.e. .i.n.c.o
    // 0120: 00 72 00 70 00 6F 00 72 00 61 00 74 00 65 00 64 .r.p.o.r.a.t.e.d
    // 0130: 00 20 00 68 00 65 00 72 00 65 00 69 00 6E 00 20 . .h.e.r.e.i.n.
    // 0140: 00 62 00 79 00 20 00 72 00 65 00 66 00 65 00 72 .b.y. .r.e.f.e.r
    // 0150: 00 65 00 6E 00 63 00 65 00 2E .e.n.c.e..
    //
    // ]] ]
    // ]

}
