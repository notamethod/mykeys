package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.TimeStampManager;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class TestCerts {

    private final static Log log = LogFactory.getLog(Test.class);

    String emptyKeystore;

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

    @Before
    public void setup() throws IOException {
        Path source = Paths.get("target/test-classes/data/empty.jks");
        Path target = Paths.get("target/test-classes/data/empty_work.jks");
        Files.copy(source, target, REPLACE_EXISTING);
        emptyKeystore = target.toAbsolutePath().toString();
    }

    @Test
    public void ImportX509() {

        try {
            String typeCert = null;

            String alias = "aaa";
            String pathCert = "target/test-classes/data/aaa.p12";
            KeyStoreValue ksInfo = new KeyStoreValue("aa", emptyKeystore,
                    StoreModel.CERTSTORE, StoreFormat.JKS);
            ksInfo.setPassword("111".toCharArray());
            File f = new File(pathCert);
            KeyStoreValue ksIn = new KeyStoreValue(new File(pathCert),
                    StoreFormat.PKCS12, "aaa".toCharArray());
            KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);
            kserv.importX509CertToJks(alias, ksIn, "aaa".toCharArray());

            KeyStoreHelper kservRet = new KeyStoreHelper(ksInfo);
            List<?> lst = kservRet.getChildList();
            assertEquals(1, lst.size());

        } catch (Exception e) {

            e.printStackTrace();
            fail();


        }

    }

    @Test
    public void loadKS() throws ServiceException {
        // String path = "data/test01.jks";
        // KeyStoreValue ksInfo = new KeyStoreValue("aa", path,
        // StoreModel.CERTSTORE, StoreFormat.JKS);
        String path = System.getProperty("user.dir");

        URL url = TestCerts.class.getResource("/data/test01.jks");

        try {
            log.trace(url.toURI().getPath());
        } catch (URISyntaxException e2) {
            // TODO Auto-generated catch block
            log.error(e2);
            fail();
        }

        KeyTools kt = new KeyTools();
        KeyStore ks = null;

        String fileName = null;
        try {
            fileName = url.toURI().getPath().substring(1);
        } catch (URISyntaxException e2) {
            log.error(e2);
            fail();
        }
        Path resourceDirectory = Paths.get("src/test/resources/data/test01.jks");
        fileName = resourceDirectory.toAbsolutePath().toString();
        KeyStoreValue ksInfo = new KeyStoreValue("aa", fileName,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        KeyStoreHelper ksBuilder = new KeyStoreHelper(ksInfo);
        ksInfo.setPassword("1234".toCharArray());
        try {
            ks = ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
                    ksInfo.getPassword()).getKeystore();

        } catch (Exception e1) {

            log.error(e1);
            fail();
        }

        Enumeration<String> enumKs = null;
        try {
            enumKs = ks.aliases();
        } catch (KeyStoreException e) {
            log.error(e);
            fail();
        }
        if (enumKs != null && enumKs.hasMoreElements()) {

            while (enumKs.hasMoreElements()) {
                String alias = enumKs.nextElement();
                if (log.isDebugEnabled()) {
                    log.debug("alias " + alias);
                }
                //
                CertificateValue certInfo = fillCertInfo(ksInfo, ks, alias);

            }
        }

    }

    @Test
    public void TimeStamp() throws ServiceException {
        Security.addProvider(new BouncyCastleProvider());

        KeyTools kt = new KeyTools();

        KeyStore ks = null;
        String fileName = null;
        Path resourceDirectory = Paths.get("src/test/resources/data/test01.jks");
        fileName = resourceDirectory.toAbsolutePath().toString();
        KeyStoreValue ksInfo = new KeyStoreValue("aa", fileName,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        ksInfo.setPassword("1234".toCharArray());
        KeyStoreHelper ksHelper = new KeyStoreHelper(ksInfo);
        try {
            ks = ksHelper.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
                    ksInfo.getPassword()).getKeystore();

        } catch (Exception e1) {

            log.error(e1);
            fail();

        }
        CertificateValue certInfo = null;
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
                certInfo = fillCertInfo(ksInfo, ks, alias);
            }
        }
        try {
            TimeStampToken tsp = TimeStampManager.getTimeStampToken(4);
            log.trace(tsp);
        } catch (Exception e) {
            log.error(e);
            fail();
        }

    }
}
