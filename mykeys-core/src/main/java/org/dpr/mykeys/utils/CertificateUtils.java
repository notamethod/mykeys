package org.dpr.mykeys.utils;

import org.dpr.mykeys.app.KeyUsageEnum;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class CertificateUtils {

    Map<Integer, String> mapKeyUSage = new HashMap<>();

    public static String keyUsageToString(boolean[] keyUsage) {
        StringBuilder value = new StringBuilder();
        boolean isKeyUsage = false;
        if (keyUsage == null) {
            return "null";
        }
        for (int i = 0; i < keyUsage.length; i++) {
            if (keyUsage[i]) {
                isKeyUsage = true;
                value.append(", ").append(X509Constants.keyUsageLabel[i]);
            }
        }
        if (isKeyUsage) {
            return value.substring(1);
        } else {
            return null;
        }
    }

    public static List<String> keyUsageToList(boolean[] keyUsage) {
        List<String> kuList = new ArrayList<>();
        String value = "";
        boolean isKeyUsage = false;
        if (keyUsage == null) {
            return kuList;
        }
        for (int i = 0; i < keyUsage.length; i++) {
            if (keyUsage[i]) {
                isKeyUsage = true;
                kuList.add(X509Constants.keyUsageLabel[i]);
            }
        }
        return kuList;
    }

    public static String keyUsageToString(int keyUsage) {
        StringBuilder value = new StringBuilder();
        boolean isKeyUsage = false;

        for (KeyUsageEnum usage : KeyUsageEnum.values()) {
            if ((keyUsage & usage.getIntValue()) == usage.getIntValue())
                value.append(", ").append(usage.getLabel());
        }

        if (value.length() > 0) {
            return value.substring(1);
        } else {
            return null;
        }
    }

    public static boolean[] keyUsageFromInt(int keyUsage) {
        String value = "";
        boolean[] booloKu = new boolean[]{false, false, false, false, false, false, false, false, false};
        boolean isKeyUsage = false;

        for (KeyUsageEnum usage : KeyUsageEnum.values()) {
            if ((keyUsage & usage.getIntValue()) == usage.getIntValue()) {
                for (int i = 0; i < X509Constants.keyUsageInt.length; i++) {
                    if (X509Constants.keyUsageInt[i] == usage.getIntValue()) {
                        booloKu[i] = true;
                    }
                }
            }
        }
        return booloKu;

    }

    public static boolean isKeyUsage(boolean[] keyUsage, int i) {
        return i < keyUsage.length && keyUsage[i];

    }


    public static Set<X509Certificate> loadX509Certs(InputStream aCertStream) throws GeneralSecurityException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

        // chargement du certificat
        Collection<X509Certificate> certs = (Collection<X509Certificate>) cf.generateCertificates(aCertStream);
        return new HashSet<>(certs);
    }

    public static List<CertificateValue> loadX509Certs(String fileName) {

        // NodeInfo nInfo = new KeyStoreValue(new File(fileName));
        List<CertificateValue> certsRetour = new ArrayList<>();


        try (InputStream is = new FileInputStream(new File(fileName))) {

            Set<X509Certificate> certs = loadX509Certs(is);

            for (X509Certificate cert : certs) {
                CertificateValue certInfo = new CertificateValue(null, cert);

                certsRetour.add(certInfo);
            }

        } catch (GeneralSecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return certsRetour;

    }


    /**
     * get a random BigInteger
     *
     * @param numBits
     * @return
     */
    public static BigInteger randomBigInteger(int numBits) {
        SecureRandom random = new SecureRandom();
        return new BigInteger(numBits, random);

    }
}
