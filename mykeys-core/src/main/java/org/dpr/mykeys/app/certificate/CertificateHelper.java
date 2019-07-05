package org.dpr.mykeys.app.certificate;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.CertificateType;

public class CertificateHelper {

    private final Log log = LogFactory.getLog(CertificateHelper.class);
    private static final int AUTH_VALIDITY = 999;

    public CertificateHelper() {
        super();
    }

    public CertificateValue createCertificate(CertificateType type, String id, char[] charArray) throws ServiceException {
        CertificateGeneratorStandard certGen = new CertificateGeneratorStandard();
        switch (type) {
            case AUTH_MK:
                return certGen.createCertificateAuth(id, charArray);

            default:
                break;
        }
        return null;

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
        if (certGen == null)
            return null;
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
