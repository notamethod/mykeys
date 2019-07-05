package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;

import java.io.File;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class Test {

    private final static Log log = LogFactory.getLog(Test.class);

    /**
     * .
     *
     * @param args
     * @throws ServiceException
     */
    public static void main(String[] args) throws ServiceException {
        loadKS();
        // ImportCert();

    }

    private static void ImportCert() {
        try {
            String alias = "aaa";
            String path = "c:/dev/empty.jks";
            String pathCert = "c:/dev/cpi.cer";
            KeyStoreValue ksInfo = new KeyStoreValue("aa", path,
                    StoreModel.CERTSTORE, StoreFormat.JKS);
            KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);

            KeyStoreValue ksIn = new KeyStoreValue(new File(pathCert),
                    StoreFormat.UNKNOWN, "111".toCharArray());

            kserv.importX509CertToJks(alias, ksIn, "111".toCharArray());


        } catch (Exception e) {

            // e.printStackTrace();

        }

    }

    private static void loadKS() throws ServiceException {
        String path = "mag1.jks";
        KeyStoreValue ksInfo = new KeyStoreValue("aa", path,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        KeyStore ks = null;
        KeyStoreHelper ksBuilder = new KeyStoreHelper(ksInfo);
        ksInfo.setPassword("111".toCharArray());
        try {
            ks = ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
                    ksInfo.getPassword()).getKeystore();

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        Enumeration<String> enumKs = null;
        try {
            enumKs = ks.aliases();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (enumKs != null && enumKs.hasMoreElements()) {

            while (enumKs.hasMoreElements()) {
                String alias = enumKs.nextElement();
                if (log.isDebugEnabled()) {
                    log.debug(alias);
                }
                //
                CertificateValue certInfo = fillCertInfo(ksInfo, ks, alias);

            }
        }

    }

    private static CertificateValue fillCertInfo(KeyStoreValue ksInfo, KeyStore ks, String alias) throws ServiceException {

        CertificateValue certInfo;
        try {
            Certificate certificate = ks.getCertificate(alias);
            Certificate[] certs = ks.getCertificateChain(alias);

            certInfo = new CertificateValue(alias, (X509Certificate) certificate);
            if (ks.isKeyEntry(alias)) {
                certInfo.setContainsPrivateKey(true);

            }
            StringBuilder bf = new StringBuilder();
            if (certs == null) {
                String message = "chaine de certification nulle pour " + alias + " (" + certInfo.getName() + ")";
                if (certInfo.isContainsPrivateKey())
                    log.error(message);
                else
                    log.debug(message);
                // return null;
            } else {
                for (Certificate chainCert : certs) {
                    bf.append(chainCert.toString());
                }
                certInfo.setChaineStringValue(bf.toString());
                certInfo.setCertificateChain(certs);
            }

        } catch (GeneralSecurityException e) {
            throw new ServiceException("filling certificate Info impossible", e);
        }
        return certInfo;


    }
}
