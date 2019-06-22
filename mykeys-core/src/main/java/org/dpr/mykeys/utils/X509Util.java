package org.dpr.mykeys.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class X509Util {

    private final static Log log = LogFactory.getLog(X509Util.class);
    private static Map<String, String> mapNames = null;

    private X509Util() {
        super();
    }

    /**
     * @return the mapNames
     */
    public static Map<String, String> getMapNames() {
        if (mapNames == null) {
            mapNames = new LinkedHashMap<>();
            mapNames.put("CN", "x509.subject.name");
            mapNames.put("O", "x509.subject.organisation");
            mapNames.put("OU", "x509.subject.organisationUnit");
            mapNames.put("E", "x509.subject.email");
            mapNames.put("C", "x509.subject.country");
            mapNames.put("L", "x509.subject.location");
            mapNames.put("ST", "x509.subject.street");
        }
        return mapNames;
    }

    public static String toHexString(byte[] b, String separator, boolean upperCase) {
        StringBuilder retour = new StringBuilder();
        char[] car = Hex.encodeHex(b);
        for (int i = 0; i < car.length; i = i + 2) {
            retour.append(String.valueOf(car[i]));
            retour.append(String.valueOf(car[i + 1]));
            retour.append(separator);
        }
        if (upperCase) {
            return retour.toString().toUpperCase();
        } else {
            return retour.toString().toLowerCase();
        }
    }

    public static String toHexString(BigInteger bi, String separator, boolean upperCase) {
        String retour = "";
        String converted = bi.toString(16);
        char[] charArray = converted.toCharArray();
        for (int i = 0; i < charArray.length - 1; i = i + 2) {
            retour += String.valueOf(charArray[i]);
            retour += String.valueOf(charArray[i + 1]);
            retour += separator;
        }
        if (upperCase) {
            return retour.toUpperCase();
        } else {
            return retour.toLowerCase();
        }
    }

    public static Map<String, String> getExtensions(X509Certificate certificate) {

        Map<String, String> returnPolicies = new LinkedHashMap<>();
        //Policies
        byte[] policyBytes = certificate.getExtensionValue(Extension.certificatePolicies.toString());
        if (policyBytes != null) {
            CertificatePolicies policies = null;
            try {
                policies = CertificatePolicies.getInstance(X509ExtensionUtil.fromExtensionValue(policyBytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int k = 1;
            if (policies != null) {
                PolicyInformation[] policyInformation = policies.getPolicyInformation();
                for (PolicyInformation pInfo : policyInformation) {
                    //ASN1Sequence policyQualifiers = (ASN1Sequence) pInfo.getPolicyQualifiers().getObjectAt(0);
                    ASN1Sequence policyQualifiers = pInfo.getPolicyQualifiers();
                    if (policyQualifiers != null) {
                        policyQualifiers.forEach(name -> log.debug("policyQualifier: " + name));
                        for (int i = 0; i < policyQualifiers.size(); i++) {
                            ASN1Sequence pol = (ASN1Sequence) policyQualifiers.getObjectAt(i);
                            for (int j = 0; j < pol.size(); j++) {
                                returnPolicies.put(pol.toString(), pol.getObjectAt(j).toString());
                                log.debug("pol: " + pol + " " + pol.getObjectAt(j));
                            }
                        }
                    }

                    ASN1ObjectIdentifier policyId = pInfo.getPolicyIdentifier();
                    returnPolicies.put("policyid" + k, policyId.toString());
                    k++;
                    log.debug("Polycy ID: " + policyId.toString());
                    // return returnPolicies;
                }
            }
        }
        // try {
        // obj = X509ExtensionUtil.fromExtensionValue(b);
        // SubjectKeyIdentifier sk = new
        // SubjectKeyIdentifier(obj.getDEREncoded());
        // SubjectKeyIdentifier ski =
        // SubjectKeyIdentifier.getInstance(extIn.readObject());
        // String skiString = Utils.toHexString(ski.getKeyIdentifier(), " ",
        // true);
        // this.setSubjectKeyIdentifier(skiString);
        // } catch (Exception e) {
        // log.trace("exc");
        //
        // }
        // log.trace(obj);
        // AuditIdentity
        // AuthorityInfoAccess
        // AuthorityKeyIdentifier
        // BasicConstraints
        // BiometricInfo
        // CertificateIssuer
        // CertificatePolicies
        // CRLDistributionPoints
        // CRLNumber
        // DeltaCRLIndicator
        // ExtendedKeyUsage
        // FreshestCRL
        // InhibitAnyPolicy
        // InstructionCode
        // InvalidityDate
        // IssuerAlternativeName
        // IssuingDistributionPoint
        // KeyUsage
        // LogoType
        // NameConstraints
        // NoRevAvail
        // PolicyConstraints
        // PolicyMappings
        // PrivateKeyUsagePeriod
        // QCStatements
        // ReasonCode
        // SubjectAlternativeName
        // SubjectDirectoryAttributes
        // SubjectInfoAccess
        // SubjectKeyIdentifier
        // TargetInformation
        return returnPolicies;
    }



    public static Map<ASN1ObjectIdentifier, String> getSubjectMap(X509Certificate x509Certificate) {
        X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
        return getInfosMap(x500Principal);
    }

    public static Map<ASN1ObjectIdentifier, String> getIssuerMap(X509Certificate x509Certificate) {
        X500Principal x500Principal = x509Certificate.getIssuerX500Principal();
        return getInfosMap(x500Principal);
    }

    public static Map<ASN1ObjectIdentifier, String> getInfosMap(X500Principal x500Principal) {
        Map<ASN1ObjectIdentifier, String> subjectMap = new HashMap<>();
        if (x500Principal == null) {
            return subjectMap;
        }
        String principalName = x500Principal.getName();
        if (StringUtils.isBlank(principalName)) {
            return subjectMap;
        }
        X500Name x509Name = new X500Name(principalName);
        ASN1ObjectIdentifier[] v = x509Name.getAttributeTypes();

        for (RDN rdn : x509Name.getRDNs()) {
            AttributeTypeAndValue[] atrs = rdn.getTypesAndValues();
            for (AttributeTypeAndValue atr : atrs) {

                String val = atr.getValue().toString();
                ASN1ObjectIdentifier type = atr.getType();
                if (log.isDebugEnabled()) {
                    log.debug(type + ":" + val);
                }
                subjectMap.put(type, val);
            }

        }
        return subjectMap;
    }

}
//1.3.6.1.5.5.7.2.1 - id-qt-cps: OID for CPS qualifier
//1.3.6.1.5.5.7.2.2 - id-qt-unotice: OID for user notice qualifier