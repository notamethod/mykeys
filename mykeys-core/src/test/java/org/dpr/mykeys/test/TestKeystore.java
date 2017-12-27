package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.util.Calendar;
import java.util.Date;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.fail;


public class TestKeystore {

    final static Log log = LogFactory.getLog(TestKeystore.class);

    private static final String AC_NAME = "mykeys root ca 2";

    private KeyTools ktools = new KeyTools();

    @BeforeClass
    public static void init() {

        KSConfigTestTmp.initResourceBundle();

        KSConfigTestTmp.init(".myKeys");

        Security.addProvider(new BouncyCastleProvider());

        ProviderUtil.initBC();
    }

    @Test
    public void load_keystore__wrong_password() {
        boolean isAC = false;
        Path resourceDirectory = Paths.get("target/test-classes/data/test01.jks");
        KeyStoreHelper service = new KeyStoreHelper();

        String fileName = resourceDirectory.toAbsolutePath().toString();
        KeyStoreValue ksInfo = new KeyStoreValue("aaz", fileName,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        ksInfo.setPassword("aaa".toCharArray());
        try {
            service.load(ksInfo);
            fail();
        } catch (KeyToolsException e) {
            //ok;

        }
    }

    @Test
    public void load_keystore_good_password() throws IOException {
        boolean isAC = false;
        Path source = Paths.get("target/test-classes/data/empty.jks");
        Path target = Paths.get("target/test-classes/data/empty_work.jks");
        Files.copy(source, target, REPLACE_EXISTING);
        KeyStoreHelper service = new KeyStoreHelper();

        String fileName = target.toAbsolutePath().toString();
        KeyStoreValue ksInfo = new KeyStoreValue("aa", fileName,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        ksInfo.setPassword("111".toCharArray());
        try {
            service.load(ksInfo);
        } catch (KeyToolsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void change_password_works_ok() throws IOException {
        boolean isAC = false;
        Path source = Paths.get("target/test-classes/data/empty.jks");
        Path target = Paths.get("target/test-classes/data/empty_work.jks");
        Files.copy(source, target, REPLACE_EXISTING);
        KeyStoreHelper service = new KeyStoreHelper();

        String fileName = target.toAbsolutePath().toString();
        KeyStoreValue ksInfo = new KeyStoreValue("aaz", fileName,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        ksInfo.setPassword("111".toCharArray());
        try {
            service.changePassword(ksInfo, "bbb".toCharArray());
        } catch (TamperedWithException | KeyToolsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void save_ok() {
        String filename = "target/test-classes/data/empty.jks";
        KeyStoreHelper service = new KeyStoreHelper();


        try {
            KeyStore ks = service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray()).getKeystore();
            service.saveKeyStore(ks, filename, "111".toCharArray());
        } catch (KeyToolsException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void add_cert() throws IOException, ServiceException {

        String filename = "target/test-classes/data/add_cert.jks";
        Path target = Paths.get(filename);

        delete(target);

        KeyStoreValue ki = null;
        KeystoreBuilder ksBuilder = null;
        KeyStoreHelper service = new KeyStoreHelper();

        try {
            ksBuilder = new KeystoreBuilder(StoreFormat.JKS);

            ksBuilder.create(filename, "111".toCharArray());
            ki=service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray());
        } catch (Exception e) {

            e.printStackTrace();
            fail();
        }


        CertificateValue val = createCert();


        service.addCertToKeyStore(ki, val,"111".toCharArray());
    }

    private void delete(Path target) {
        try {
            Files.delete(target);
        } catch (IOException e) {
            //silent e.printStackTrace();
        }
    }

    @Test
    public void create_ks_jks() throws IOException {

        KeyStoreHelper service = new KeyStoreHelper();

        String filename = "target/test-classes/data/test-create_create_ks.jks";
        Path target = Paths.get(filename);
        delete(target);
        KeystoreBuilder ksBuilder = null;

        try {
            ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            fail();
        }

        try {
            ksBuilder.create(filename, "111".toCharArray());
            service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray());
        } catch (Exception e) {

            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void create_ks_p12() throws IOException {

        KeyStoreHelper service = new KeyStoreHelper();

        String filename = "target/test-classes/data/test-create_create_ks.p12";
        Path target = Paths.get(filename);
        delete(target);
        KeystoreBuilder ksBuilder = null;

        try {
            ksBuilder = new KeystoreBuilder(StoreFormat.PKCS12);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            fail();
        }

        try {
            ksBuilder.create(filename, "111".toCharArray());
            service.loadKeyStore(filename, StoreFormat.PKCS12, "111".toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private CertificateValue createCert() {
        boolean isAC = false;
        CertificateValue certModel = new CertificateValue("aliastest");
        certModel.setAlgoPubKey("RSA");
        certModel.setAlgoSig("SHA1WithRSAEncryption");

        certModel.setKeyLength(1024);
        certModel.setSubjectMap("CN=toto");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        certModel.setNotBefore(new Date());
        certModel.setNotAfter(cal.getTime());
        CertificateValue certIssuer = new CertificateValue();
        CertificateHelper certServ = new CertificateHelper(certModel);

        CertificateValue retValue = null;
        try {
            retValue = certServ.createCertificate(certModel, certModel);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            fail(e.getMessage());
        }
        return retValue;

    }


}