package org.dpr.mykeys.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.Messages;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class SubjectUtil {

    static Map<String, String> mapSubjectLabels = null;
    static Map<String, String> mapCertificateLabels = null;
    final static Log log = LogFactory.getLog(SubjectUtil.class);

    /**
     * @return the mapNames
     */
    public static Map<String, String> getLabels() {
        if (mapSubjectLabels == null) {
            mapSubjectLabels = new HashMap<String, String>();
            mapSubjectLabels.put("C", "x509.subject.country");
            mapSubjectLabels.put("O", "x509.subject.organisation");
            mapSubjectLabels.put("OU", "x509.subject.organisationUnit");
            mapSubjectLabels.put("L", "x509.subject.location");
            mapSubjectLabels.put("ST", "x509.subject.street");
            mapSubjectLabels.put("E", "x509.subject.email");
            mapSubjectLabels.put("CN", "x509.subject.name");
        }
        return mapSubjectLabels;
    }

    /**
     * @return the mapNames
     */
    public static Map<String, String> getCertificateLabels() {
        if (mapCertificateLabels == null) {
            mapCertificateLabels = new HashMap<String, String>();
            mapCertificateLabels.put("duration", "certinfo.duration");
            mapCertificateLabels.put("policyCPS", "x509.policycps");
            mapCertificateLabels.put("crlDistrib", "x509.cdp");
            mapCertificateLabels.put("keyUSage2", "certinfo.keyUsage");
            mapCertificateLabels.put("keyUSage", "certinfo.keyUsage");
            mapCertificateLabels.put("algoSig", "x509.sigalgo");
            mapCertificateLabels.put("algoPubKey", "x509.pubkeyalgo");
            mapCertificateLabels.put("issuer", "x509.issuer");
            mapCertificateLabels.put("keyLength", "x509.pubkeysize");
            mapCertificateLabels.put("description", "label.description");
        }
        return mapCertificateLabels;
    }
    //x509.policynotice

    public static String toHexString(byte[] b, String separator, boolean upperCase) {
        String retour = "";
        char[] car = Hex.encodeHex(b);
        for (int i = 0; i < car.length; i = i + 2) {
            retour += String.valueOf(car[i]);
            retour += String.valueOf(car[i + 1]);
            retour += separator;
        }
        if (upperCase) {
            return retour.toUpperCase();
        } else {
            return retour.toLowerCase();
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


    public static String[] getStandardList() {
        return new String[]{"CN", "O", "C", "L"};
    }
}
