package org.dpr.mykeys.app.certificate;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.dpr.mykeys.utils.ProviderUtil;
import org.dpr.mykeys.app.CertificateType;

public class CertificateHelper {

    private final Log log = LogFactory.getLog(CertificateHelper.class);
    private static final int AUTH_VALIDITY = 999;

    public CertificateHelper() {
        super();
    }

    public CertificateValue createCertificate(CertificateType type, String id, char[] charArray) throws GeneralSecurityException, OperatorCreationException {
        switch (type) {
            case AUTH:
                return createCertificateAuth(id, charArray);

            default:
                break;
        }
        return null;

    }

    private CertificateValue createCertificateAuth(String id, char[] charArray) throws OperatorCreationException, GeneralSecurityException {

        int validity = AUTH_VALIDITY;
        // X500Name owner = new X500Name("CN=" + fqdn);
        X500Name subject = new X500Name("CN=" + id);
        BigInteger serial = new BigInteger(32, new SecureRandom());
        Date from = new Date();
        Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));
        KeyPair keypair = generateKeyPair("RSA", 2048);
        // Prepare the information required for generating an X.509 certificate.
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(subject, serial, from, to, subject,
                keypair.getPublic());

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keypair.getPrivate());
        X509CertificateHolder certHolder = builder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider(ProviderUtil.provider).getCertificate(certHolder);

        cert.verify(keypair.getPublic());
        CertificateValue value = new CertificateValue(id, cert);
        value.setPrivateKey(keypair.getPrivate());
        return value;


    }

    /**
     * Key pair generation
     *
     * @param algo
     * @param keyLength
     */
    private KeyPair generateKeyPair(String algo, int keyLength) {
        KeyPair keypair = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("generating keypair: " + algo + " keypair: " + keyLength);
            }

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo, "BC");
            keyGen.initialize(keyLength);

            keypair = keyGen.genKeyPair();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return keypair;
    }


    public CertificateValue generate(CertificateValue certInfo, CertificateValue inIssuer, CertificateType usage) throws Exception {
        CertificateGeneratorStandard certGen = null;
        switch (usage) {
            case STANDARD:
                certGen = new CertificateGeneratorStandard();

                break;
            case AC:
                certGen = new CertificateGeneratorStandard() {
                    @Override
                    public void addExtensions(X509v3CertificateBuilder certGen, Map<String, String> parameters) throws CertIOException {
                        int maxLength = -1;
                        if (parameters != null) {
                            maxLength = parameters.get("maxLength") == null ? -1 : Integer.parseInt(parameters.get("maxLength"));
                        }
                        if (maxLength >= 0) {
                            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(maxLength));
                        } else {
                            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
                        }
                    }
                };

                break;
            case CODE_SIGNING:
                certGen = new CertificateGeneratorStandard() {
                    @Override
                    public void addExtensions(X509v3CertificateBuilder certGen, Map<String, String> map) throws CertIOException {
                        certGen.addExtension(Extension.extendedKeyUsage, false,
                                new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning));
                    }
                };

                break;
            case SERVER:
                certGen = new CertificateGeneratorStandard() {
                    @Override
                    public void addExtensions(X509v3CertificateBuilder certGen, Map<String, String> map) throws CertIOException {
                        certGen.addExtension(Extension.extendedKeyUsage, true,
                                new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
                    }
                };
                break;
            case AUTH:
                certGen = new CertificateGeneratorStandard() {
                    @Override
                    public void addExtensions(X509v3CertificateBuilder certGen, Map<String, String> map) throws CertIOException {
                        certGen.addExtension(Extension.extendedKeyUsage, true,
                                new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
                    }
                };
                break;
            default:
                break;
        }
        return certGen.generate(certInfo, inIssuer);
    }
//
//	private PolicyInformation getPolicyInformation(String policyOID, String cps, String unotice) {
//
//		ASN1EncodableVector qualifiers = new ASN1EncodableVector();
//
//		if (!StringUtils.isEmpty(unotice)) {
//			UserNotice un = new UserNotice(null, new DisplayText(DisplayText.CONTENT_TYPE_BMPSTRING, unotice));
//			PolicyQualifierInfo pqiUNOTICE = new PolicyQualifierInfo(PolicyQualifierId.id_qt_unotice, un);
//			qualifiers.add(pqiUNOTICE);
//		}
//		if (!StringUtils.isEmpty(cps)) {
//			PolicyQualifierInfo pqiCPS = new PolicyQualifierInfo(cps);
//			qualifiers.add(pqiCPS);
//		}
//
//		PolicyInformation policyInformation = new PolicyInformation(new ASN1ObjectIdentifier(policyOID),
//				new DERSequence(qualifiers));
//
//		return policyInformation;
//
//	}
//
//	/**
//	 * @param certModel
//	 * @param certIssuer
//	 */
//	CertificateBuilder addCrlDistributionPoint(CertificateValue certModel, CertificateValue certIssuer
//	) { // point
//		log.info("adding crl distribution point");
//		if (certModel.getCrlDistributionURL() != null) {
//			DistributionPoint[] dp = new DistributionPoint[1];
//			DEROctetString oct = new DEROctetString(certModel.getCrlDistributionURL().getBytes());
//			DistributionPointName dpn = new DistributionPointName(
//					new GeneralNames(new GeneralName(GeneralName.dNSName, certModel.getCrlDistributionURL())));
//			dp[0] = new DistributionPoint(dpn, null, null);
//			certGen.addExtension(X509Extension.cRLDistributionPoints, false, new CRLDistPoint(dp));
//		} else {
//			if (certIssuer.getCertificate() != null) {
//				CRLDistPoint dpoint = getDistributionPoints(certIssuer.getCertificate());
//				if (dpoint != null) {
//					certGen.addExtension(X509Extension.cRLDistributionPoints, false, dpoint);
//				}
//			}
//		}
//		return this;
//	}
}
