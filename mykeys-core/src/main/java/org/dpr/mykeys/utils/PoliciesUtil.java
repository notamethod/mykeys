package org.dpr.mykeys.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PoliciesUtil {

    protected static final Log log = LogFactory.getLog(PoliciesUtil.class);

    String id_qt = "1.3.6.1.5.5.7.2";
    String id_qt_cps = "1.3.6.1.5.5.7.2.1";
    String id_qt_unotice = "1.3.6.1.5.5.7.2.2";
    String id_dv = "2.23.140.1.2.1";
    private static final Map<ASN1ObjectIdentifier, String> qualifiers = new HashMap() {{
        put(PolicyQualifierId.id_dv, "oid.policy.id_dv");
        put(PolicyQualifierId.id_qt_cps, "oid.policy.id_cps");
        put(PolicyQualifierId.id_qt_unotice, "oid.policy.id_qt_unotice");
        put(PolicyQualifierId.id_ev, "oid.policy.id_ev");
        put(PolicyQualifierId.id_evssl_globalsign, "oid.policy.id_evssl_globalsign");
        put(PolicyQualifierId.id_ov, "oid.policy.id_ov");
    }};


    public static Map<String, String> getExtensionPolicies(X509Certificate certificate) throws PoliciesException {

        Map<String, String> returnPolicies = new LinkedHashMap<>();
        //Policies
        byte[] policyBytes = certificate.getExtensionValue(Extension.certificatePolicies.toString());
        if (policyBytes != null) {
            CertificatePolicies policies = null;
            try {
                policies = CertificatePolicies.getInstance(X509ExtensionUtil.fromExtensionValue(policyBytes));
            } catch (IOException e) {
                throw new PoliciesException("invalid policy informations", e);
            }
            int k = 1;
            if (policies != null) {
                PolicyInformation[] policyInformation = policies.getPolicyInformation();
                for (PolicyInformation pInfo : policyInformation) {
                    ASN1Sequence policyQualifiers = pInfo.getPolicyQualifiers();
                    if (policyQualifiers != null) {
                        policyQualifiers.forEach(name -> log.debug("policyQualifier: " + name));
                        for (int i = 0; i < policyQualifiers.size(); i++) {
                            ASN1Sequence pol = (ASN1Sequence) policyQualifiers.getObjectAt(i);
                            for (int j = 0; j < pol.size(); j++) {
                                returnPolicies.put(getName(pol), pol.getObjectAt(j).toString());
                                log.debug("pol: " + getName(pol) + " " + pol.getObjectAt(j));
                            }
                        }
                    }

                    ASN1ObjectIdentifier policyId = pInfo.getPolicyIdentifier();
                    String name = getName(policyId);
                    returnPolicies.put(name, null);
                    k++;
                    log.debug("Polycy ID: " + name);
                }
            }
        }

        return returnPolicies;
    }

    private static String getName(ASN1ObjectIdentifier policyId) {

        if (qualifiers.get(policyId) != null) {
            //for resource bundle
            return qualifiers.get(policyId);
        } else {
            //oid
            return policyId.toString();
        }

    }

    private static String getName(ASN1Sequence policyId) {

        String s = qualifiers.get(policyId.getObjectAt(0));
        if (s != null) {
            //for resource bundle
            return s;
        } else {
            //oid
            return policyId.toString();
        }

    }
}
