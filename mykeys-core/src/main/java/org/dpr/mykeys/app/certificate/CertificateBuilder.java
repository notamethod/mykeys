package org.dpr.mykeys.app.certificate;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
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
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.dpr.mykeys.app.KeyTools;

public class CertificateBuilder extends KeyTools {

	private final Log log = LogFactory.getLog(CertificateBuilder.class);


	private CertificateValue certificateValue;

	private X509V3CertificateGenerator certGen;

	public CertificateBuilder() {
		super();
		certGen = new X509V3CertificateGenerator();
	}

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
		return certificateValue.getCertificate();
	}

    public CertificateValue getValue() {
        return certificateValue;
    }

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * 
	 * @param certModel
	 * @param certIssuer
	 * @return
	 * @throws Exception
	 */
	// public X509Certificate[] genererX509(CertificateInfo certInfo, String
	// aliasEmetteur, boolean isAC)
	// throws Exception {
	//
	// KeyStoreValue ksInfo = null;
	// if (!StringUtils.isBlank(aliasEmetteur)) {
	// char[] password = KSConfig.getInternalKeystores().getPassword().toCharArray();
	// ksInfo = KSConfig.getInternalKeystores().getACKeystore();
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
	private CertificateValue genererX509(CertificateValue certModel, CertificateValue certIssuer, boolean isAC)
			throws Exception {


		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(certModel.getAlgoPubKey(), "BC");
		keyGen.initialize(certModel.getKeyLength());
		KeyPair keypair = keyGen.genKeyPair();

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

		certGen.setPublicKey(keypair.getPublic());
		setDuration(certModel);

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
				extUtils.createSubjectKeyIdentifier(keypair.getPublic()));

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
					new AuthorityKeyIdentifierStructure(keypair.getPublic()));
		}

		if (StringUtils.isNotBlank(certModel.getPolicyCPS())) {
			PolicyInformation pi = getPolicyInformation(certModel.getPolicyID(), certModel.getPolicyCPS(),
					certModel.getPolicyNotice());

			DERSequence seq = new DERSequence(pi);
			certGen.addExtension(X509Extensions.CertificatePolicies.getId(), false, seq);
		}
		// gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
		// new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
        X509Certificate cert = null;
		if (certModel.getSubjectString().equalsIgnoreCase(certIssuer.getSubjectString())) {
             cert = certGen.generate(keypair.getPrivate());
        } else {
             cert = certGen.generate(certIssuer.getPrivateKey());
        }
		// TODO: let generate expired certificate for test purpose ?
		try {
			cert.checkValidity(new Date());
		} catch (Exception e) {
			log.warn("invalid certificate", e);
		}
        if (certModel.getSubjectString().equalsIgnoreCase(certIssuer.getSubjectString())) {
            cert.verify(keypair.getPublic());
        }else{
            cert.verify(certIssuer.getPublicKey());
        }
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
		CertificateValue certReturn = new CertificateValue(certChain);
		certReturn.setPrivateKey(keypair.getPrivate());
		certReturn.setPublicKey(keypair.getPublic());
		return certReturn;

	}

	@SuppressWarnings("deprecation")
	private X509Certificate[] genererX509CodeSigning(CertificateValue certModel, CertificateValue certIssuer, boolean isAC)
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

		setDuration(certModel);
		certGen.setPublicKey(certModel.getPublicKey());

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

	private void setDuration(CertificateValue certModel) {
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

	private PolicyInformation getPolicyInformation(String policyOID, String cps, String unotice) {

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
	 */
	CertificateBuilder addCrlDistributionPoint(CertificateValue certModel, CertificateValue certIssuer
			) { // point
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
		return this;
	}

	public CertificateBuilder generate(CertificateValue certInfo, CertificateValue infoEmetteur, boolean isAC,
			Usage usage) throws Exception {
		switch (usage) {
		case CODESIGNING:
		    //FIXME change signature
			genererX509CodeSigning(certInfo, infoEmetteur, isAC);
			break;

		default:
			certificateValue =  genererX509(certInfo, infoEmetteur, isAC);
			//FIXME: crl dispitribuion point?
			break;
		}
		return this;

	}

	public CertificateBuilder generate(CertificateValue certInfo, CertificateValue infoEmetteur, boolean isAC)
			throws Exception {
		return generate(certInfo, infoEmetteur, isAC, Usage.DEFAULT);

	}

	public X509Certificate[] getCertificates() {
		return (X509Certificate[]) certificateValue.getCertificateChain();
	}





	

}
