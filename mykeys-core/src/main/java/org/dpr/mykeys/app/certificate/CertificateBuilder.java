package org.dpr.mykeys.app.certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.PolicyQualifierId;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.UserNotice;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.dpr.mykeys.app.KeyTools;

public class CertificateBuilder extends KeyTools {

	final Log log = LogFactory.getLog(CertificateBuilder.class);
	X509Certificate certificate;
	X509Certificate[] certificates;

	public CertificateBuilder load(InputStream is) throws CertificateException {

		{
			// création d'une fabrique de certificat X509
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			// chargement du certificat
			X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
			return this;
		}

	}

	public X509Certificate get() {
		return certificate;
	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * 
	 * @param certInfo
	 * @param string
	 * @return
	 * @throws Exception
	 */
	// public X509Certificate[] genererX509(CertificateInfo certInfo, String
	// aliasEmetteur, boolean isAC)
	// throws Exception {
	//
	// KeyStoreInfo ksInfo = null;
	// if (!StringUtils.isBlank(aliasEmetteur)) {
	// char[] password = InternalKeystores.password.toCharArray();
	// ksInfo = InternalKeystores.getACKeystore();
	//
	//
	//
	// infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(aliasEmetteur, password));
	// return genererX509CodeSigning(certInfo, infoEmetteur, isAC);
	// } else {
	// return genererX509(certInfo, certInfo, isAC);
	// }
	// }

	@SuppressWarnings("deprecation")
	private X509Certificate[] genererX509(CertificateInfo certModel, CertificateInfo certIssuer, boolean isAC)
			throws Exception {

		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		keyPairGen(certModel.getAlgoPubKey(), certModel.getKeyLength(), certModel);

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		// SerialNumber
		BigInteger bi = RandomBI(30);
		certGen.setSerialNumber(bi);
		if (StringUtils.isBlank(certModel.getAlias())) {
			certModel.setAlias(bi.toString(16));
		}
		if (certIssuer.getCertificate() != null) {
			log.info("certificate generated from issuer..." + certIssuer.getName());
			certGen.setIssuerDN(certIssuer.getCertificate().getSubjectX500Principal());
		} else {
			certGen.setIssuerDN(new X509Principal(new X509Principal(certModel.subjectMapToX509Name())));
		}

		certGen.setPublicKey(certModel.getPublicKey());
		setDuration(certModel, certGen);

		certGen.setSubjectDN(new X509Principal(certModel.subjectMapToX509Name()));
		certGen.setSignatureAlgorithm(certModel.getAlgoSig());
		int maxLength = -1;
		if (isAC) {
			// TODO: pass as parameter for server certificate
			if (maxLength >= 0) {
				certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(maxLength));
			} else {
				certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(true));
			}
		} else {
			certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		}
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(certModel.getIntKeyUsage()));
		// certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
		// new AuthorityKeyIdentifierStructure( caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
				extUtils.createSubjectKeyIdentifier(certModel.getPublicKey()));

		// X509Extensions.ExtendedKeyUsage.

		// FIXME: extensions to fix
		// KeyPurposeId kpid[]=new KeyPurposeId[2];
		//
		// kpid[0]=KeyPurposeId.id_kp_clientAuth;
		// kpid[1]=KeyPurposeId.id_kp_serverAuth;
		// Vector v = new Vector<KeyPurposeId>();
		// v.add(kpid[0]);
		// v.add(kpid[1]);
		//
		// certGen.addExtension(X509Extensions.ExtendedKeyUsage, false,
		// new ExtendedKeyUsage(v));

		// FIXME: à vérifier en cas de auto signé
		if (certIssuer.getCertificate() != null) {
			certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
					new AuthorityKeyIdentifierStructure(certIssuer.getCertificate()));
		} else {
			certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
					new AuthorityKeyIdentifierStructure(certModel.getPublicKey()));
		}

		if (StringUtils.isNotBlank(certModel.getPolicyCPS())) {
			PolicyInformation pi = getPolicyInformation(certModel.getPolicyID(), certModel.getPolicyCPS(),
					certModel.getPolicyNotice());

			DERSequence seq = new DERSequence(pi);
			certGen.addExtension(X509Extensions.CertificatePolicies.getId(), false, seq);
		}
		// gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
		// new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

		// point de distribution des CRL
		addCrlDistributionPoint(certModel, certIssuer, certGen);

		X509Certificate cert = certGen.generate(certIssuer.getPrivateKey());
		// TODO: let generate expired certificate for test purpose ?
		try {
			cert.checkValidity(new Date());
		} catch (Exception e) {
			log.warn("invalid certificate", e);
		}
		cert.verify(certIssuer.getPublicKey());
		X509Certificate[] certChain = null;
		// FIXME: gérer la chaine de l'émetteur
		if (certIssuer.getCertificateChain() != null) {
			log.info("adding issuer " + certIssuer.getName() + "'s certicate chain to certificate");
			certChain = new X509Certificate[certIssuer.getCertificateChain().length + 1];
			System.arraycopy(certIssuer.getCertificateChain(), 0, certChain, 1,
					certIssuer.getCertificateChain().length);
			certChain[0] = cert;
			// certChain[1] = certIssuer.getCertificate();
		} else if (certIssuer.getCertificate() != null) {
			log.error("FIXME");
			certChain = new X509Certificate[2];
			certChain[0] = cert;
			certChain[1] = certIssuer.getCertificate();
		} else {
			certChain = new X509Certificate[] { cert };
		}

		return certChain;

	}

	@SuppressWarnings("deprecation")
	public X509Certificate[] genererX509CodeSigning(CertificateInfo certModel, CertificateInfo certIssuer, boolean isAC)
			throws Exception {

		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		keyPairGen(certModel.getAlgoPubKey(), certModel.getKeyLength(), certModel);

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		BigInteger bi = RandomBI(30);
		certGen.setSerialNumber(bi);
		if (StringUtils.isBlank(certModel.getAlias())) {
			certModel.setAlias(bi.toString(16));
		}
		if (certIssuer.getCertificate() != null) {
			certGen.setIssuerDN(certIssuer.getCertificate().getSubjectX500Principal());
		} else {
			certGen.setIssuerDN(new X509Principal(new X509Principal(certModel.subjectMapToX509Name())));
		}

		setDuration(certModel, certGen);
		certGen.setPublicKey(certModel.getPublicKey());
		setDuration(certModel, certGen);
		certGen.setSubjectDN(new X509Principal(certModel.subjectMapToX509Name()));
		certGen.setSignatureAlgorithm(certModel.getAlgoSig());

		certGen.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(false));

		certGen.addExtension(X509Extension.keyUsage, true, new KeyUsage(certModel.getIntKeyUsage()));
		// certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
		// new AuthorityKeyIdentifierStructure( caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
				extUtils.createSubjectKeyIdentifier(certModel.getPublicKey()));

		// FIXME: � v�rifier en cas de auto sign�
		if (certIssuer.getCertificate() != null) {
			certGen.addExtension(X509Extension.authorityKeyIdentifier, false,
					new AuthorityKeyIdentifierStructure(certIssuer.getCertificate()));
		} else {
			certGen.addExtension(X509Extension.authorityKeyIdentifier, false,
					new AuthorityKeyIdentifierStructure(certModel.getPublicKey()));
		}

		if (certModel.getPolicyCPS() != null) {
			PolicyInformation pi = getPolicyInformation(certModel.getPolicyID(), certModel.getPolicyCPS(),
					certModel.getPolicyNotice());

			DERSequence seq = new DERSequence(pi);
			certGen.addExtension(X509Extension.certificatePolicies.getId(), false, seq);
		}
		// gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
		// new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

		certGen.addExtension(X509Extensions.ExtendedKeyUsage, false,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning));

		X509Certificate cert = certGen.generate(certIssuer.getPrivateKey());
		cert.checkValidity(new Date());

		cert.verify(certIssuer.getPublicKey());
		X509Certificate[] certChain = null;
		// FIXME: g�rer la chaine de l'�metteur
		if (certIssuer.getCertificate() != null) {
			certChain = new X509Certificate[2];
			certChain[0] = cert;
			certChain[1] = certIssuer.getCertificate();
		} else {
			certChain = new X509Certificate[] { cert };
		}

		return certChain;

	}

	private void setDuration(CertificateInfo certModel, X509V3CertificateGenerator certGen) {
		if (certModel.getDuration() > 0) {
			certModel.setNotBefore(new Date());
			certGen.setNotBefore(certModel.getNotBefore());
			LocalDateTime ldt = LocalDateTime.ofInstant(certModel.getNotBefore().toInstant(), ZoneId.systemDefault());

			ZonedDateTime zdt = ldt.plusYears(certModel.getDuration()).atZone(ZoneId.systemDefault());
			certModel.setNotAfter(Date.from(zdt.toInstant()));
			certGen.setNotAfter((certModel.getNotAfter()));

		} else {
			if (null == certModel.getNotBefore()) {
				certModel.setNotBefore(new Date());
			}
			certGen.setNotBefore(certModel.getNotBefore());

			certGen.setNotAfter((certModel.getNotAfter()));
		}
	}

	PolicyInformation getPolicyInformation(String policyOID, String cps, String unotice) {

		ASN1EncodableVector qualifiers = new ASN1EncodableVector();

		if (!StringUtils.isEmpty(unotice)) {
			UserNotice un = new UserNotice(null, new DisplayText(DisplayText.CONTENT_TYPE_BMPSTRING, unotice));
			PolicyQualifierInfo pqiUNOTICE = new PolicyQualifierInfo(PolicyQualifierId.id_qt_unotice, un);
			qualifiers.add(pqiUNOTICE);
		}
		if (!StringUtils.isEmpty(cps)) {
			PolicyQualifierInfo pqiCPS = new PolicyQualifierInfo(cps);
			qualifiers.add(pqiCPS);
		}

		PolicyInformation policyInformation = new PolicyInformation(new ASN1ObjectIdentifier(policyOID),
				new DERSequence(qualifiers));

		return policyInformation;

	}

	/**
	 * @param certModel
	 * @param certIssuer
	 * @param certGen
	 */
	void addCrlDistributionPoint(CertificateInfo certModel, CertificateInfo certIssuer,
			X509V3CertificateGenerator certGen) { // point
		// CRL
		if (certModel.getCrlDistributionURL() != null) {
			DistributionPoint[] dp = new DistributionPoint[1];
			DEROctetString oct = new DEROctetString(certModel.getCrlDistributionURL().getBytes());
			DistributionPointName dpn = new DistributionPointName(
					new GeneralNames(new GeneralName(GeneralName.dNSName, certModel.getCrlDistributionURL())));
			dp[0] = new DistributionPoint(dpn, null, null);
			certGen.addExtension(X509Extension.cRLDistributionPoints, false, new CRLDistPoint(dp));
		} else {
			if (certIssuer.getCertificate() != null) {
				CRLDistPoint dpoint = getDistributionPoints(certIssuer.getCertificate());
				if (dpoint != null) {
					certGen.addExtension(X509Extension.cRLDistributionPoints, false, dpoint);
				}
			}
		}
	}

	public CertificateBuilder generate(CertificateInfo certInfo, CertificateInfo infoEmetteur, boolean isAC,
			Usage usage) throws Exception {
		switch (usage) {
		case CODESIGNING:
			certificates = genererX509CodeSigning(certInfo, infoEmetteur, isAC);
			break;

		default:
			certificates = genererX509(certInfo, infoEmetteur, isAC);
			break;
		}
		return this;

	}

	public CertificateBuilder generate(CertificateInfo certInfo, CertificateInfo infoEmetteur, boolean isAC)
			throws Exception {
		return generate(certInfo, infoEmetteur, isAC, Usage.DEFAULT);

	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public X509Certificate[] getCertificates() {
		return certificates;
	}

	public CertificateBuilder fromRequest(Reader buf, CertificateInfo issuer)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException, OperatorCreationException, CertificateException {
		PemReader reader = new PemReader(buf);
		PKCS10CertificationRequest csr = convertPemToPKCS10CertificationRequest(reader);

		X500Name x500Name = csr.getSubject();
		System.out.println("x500Name is: " + x500Name + "\n");
		System.out.println("x500Name is: " + csr.getSignatureAlgorithm() + "\n");

		csr.getSubject();

		byte[] certencoded = sign(csr, issuer.getPrivateKey(), issuer.getCertificate());
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

		InputStream in = new ByteArrayInputStream(certencoded);
		certificate = (X509Certificate) certFactory.generateCertificate(in);
		if (certificate != null) {
			log.info("certificate " + certificate.getSubjectDN().getName() + " created !");
		}
		return this;
	}

	private PKCS10CertificationRequest convertPemToPKCS10CertificationRequest(Reader pemReader) {

		PKCS10CertificationRequest csr = null;
		ByteArrayInputStream pemStream = null;

		PEMParser pemParser = new PEMParser(pemReader);

		try {
			Object parsedObj = pemParser.readObject();

			System.out.println("PemParser returned: " + parsedObj);

			if (parsedObj instanceof PKCS10CertificationRequest) {
				csr = (PKCS10CertificationRequest) parsedObj;

			}
		} catch (IOException ex) {
			log.error("IOException, convertPemToPublicKey", ex);
		}

		return csr;
	}

	public byte[] sign(PKCS10CertificationRequest inputCSR, PrivateKey caPrivate, X509Certificate caCert)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException,
			IOException, OperatorCreationException, CertificateException {

		AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
		AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

		int validity = 365;
		X500Name issuer = new X500Name(caCert.getSubjectX500Principal().getName());
		BigInteger serial = new BigInteger(32, new SecureRandom());
		Date from = new Date();
		Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));

		// PKCS10CertificationRequestHolder pk10Holder = new
		// PKCS10CertificationRequestHolder(inputCSR);
		// in newer version of BC such as 1.51, this is
		// PKCS10CertificationRequest pk10Holder = new
		// PKCS10CertificationRequest(inputCSR);
		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		X509v3CertificateBuilder certgen = new X509v3CertificateBuilder(issuer, serial, from, to, inputCSR.getSubject(),
				inputCSR.getSubjectPublicKeyInfo());
		certgen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
		certgen.addExtension(Extension.subjectKeyIdentifier, false,
				extUtils.createSubjectKeyIdentifier(inputCSR.getSubjectPublicKeyInfo()));
		certgen.addExtension(Extension.authorityKeyIdentifier, false,
				new AuthorityKeyIdentifier(
						new GeneralNames(new GeneralName(new X509Name(caCert.getSubjectX500Principal().getName()))),
						caCert.getSerialNumber()));

		ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
				.build(PrivateKeyFactory.createKey(caPrivate.getEncoded()));
		X509CertificateHolder holder = certgen.build(signer);
		byte[] certencoded = holder.toASN1Structure().getEncoded();
		return certencoded;

	}

}
