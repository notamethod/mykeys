package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.keystore.TamperedWithException;
import org.dpr.mykeys.app.certificate.CertificateManager;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.keystore.repository.MkKeystore;
import org.dpr.mykeys.app.utils.ProviderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.*;


public class TestKeystore {

    private final static Log log = LogFactory.getLog(TestKeystore.class);

    private static final String AC_NAME = "mykeys root ca 2";

    @BeforeAll
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
        } catch (ServiceException e) {
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
        } catch (ServiceException e) {
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
        } catch (TamperedWithException | KeyToolsException | ServiceException e) {
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
        } catch (ServiceException | KeyToolsException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void add_cert() throws ServiceException {

        char[] pwd = "111".toCharArray();
        String filename = "target/test-classes/data/add_cert.jks";
        Path target = Paths.get(filename);

        delete(target);

        KeyStoreValue ki = null;
        KeystoreBuilder ksBuilder;
        KeyStoreHelper service = new KeyStoreHelper();

        try {
            ksBuilder = new KeystoreBuilder(StoreFormat.JKS);

            ksBuilder.create(filename, "111".toCharArray());
            ki = service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray());
        } catch (Exception e) {

            e.printStackTrace();
            fail();
        }


        CertificateValue val = createCert();
        val.setPassword(pwd);
        //ki.setPassword(pwd);

        service.addCertToKeyStore(ki, val, null, null);
    }

    private void delete(Path target) {
        try {
            Files.delete(target);
        } catch (IOException e) {
            //silent e.printStackTrace();
        }
    }

    @Test
    public void create_ks_jks() {

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
    public void create_ks_p12() {

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
        CertificateManager certServ = new CertificateManager();

        CertificateValue retValue = null;
        try {
            retValue = certServ.generate(certModel, certModel, CertificateType.STANDARD);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            fail(e.getMessage());
        }
        return retValue;

    }

    @Test
    public void testCreateCert() {
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
        certModel.setPolicyCPS("CPO000");
        CertificateValue certIssuer = new CertificateValue();
        CertificateManager certServ = new CertificateManager();

        CertificateValue retValue = null;
        try {
            retValue = certServ.generate(certModel, certModel, CertificateType.STANDARD);
            System.out.println(retValue.getPolicyCPS());

            CertificateValue cv = new CertificateValue("xx", retValue.getCertificate());
            System.out.println(cv.getOtherParams());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            fail(e.getMessage());
        }


    }

    @Test
    public void testExport() {

        Path resourceDirectory = Paths.get("target/test-classes/data/test1.pem");
        CertificateValue cv = createCert();
        List<CertificateValue> listCert = new ArrayList<>();
        listCert.add(cv);
        String fileName = resourceDirectory.toAbsolutePath().toString();
        KeyStoreHelper service = new KeyStoreHelper();
        try {
            service.export(listCert, fileName, StoreFormat.PEM, null, KeyStoreHelper.SAVE_OPTION.REPLACE);
        } catch (KeyToolsException e) {
            fail();
        }
        KeyStoreValue ksv = new KeyStoreValue(fileName, StoreFormat.PEM);
        service = new KeyStoreHelper(ksv);
        boolean found = false;
        try {
            List<CertificateValue> certs = service.getCertificates();
            for (CertificateValue cert : certs) {
                if (cert.getPublicKey().equals(cv.getPublicKey()))
                    found = true;
            }
            assertTrue(found);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importPem() throws ServiceException, KeyStoreException {

        char[] pwd = "111".toCharArray();
        String filename = "target/test-classes/data/my.jks";
        String filenamePem = "target/test-classes/data/pem/3cdeb3d0.pem";
        KeyStoreValue ki = null;

        MkKeystore mks = MkKeystore.getInstance(StoreFormat.PEM);
        KeyStoreValue ksv = new KeyStoreValue(filenamePem, StoreFormat.PEM);

        try {
            ki = emptyKeystore(filename, pwd);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        KeyStoreHelper service = new KeyStoreHelper(ki);
        service.importX509CertToJks(null, ksv, null);
        ki = service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray());
        Enumeration<String> enumKs = ki.getKeystore().aliases();
        int size = Collections.list(enumKs).size();
        assertTrue(size == 1);
    }

    @Test
    public void getCertsPem() throws ServiceException {


        String filenamePem = "target/test-classes/data/pem/3cdeb3d0.pem";
        KeyStoreValue ki = null;

        MkKeystore mks = MkKeystore.getInstance(StoreFormat.PEM);
        KeyStoreValue ksv = new KeyStoreValue(filenamePem, StoreFormat.PEM);

        assertEquals(1, mks.getCertificates(ksv).size());

    }

    @Test
    public void getCertsDer() throws ServiceException {


        String filename = "target/test-classes/data/der/3cdeb3d0x.der";
        KeyStoreValue ki = null;

        MkKeystore mks = MkKeystore.getInstance(StoreFormat.DER);
        KeyStoreValue ksv = new KeyStoreValue(filename, StoreFormat.DER);

        assertEquals( 1, mks.getCertificates(ksv).size(),"Error assert");

    }

    private KeyStoreValue emptyKeystore(String filename, char[] pwd) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, ServiceException {
        Path target = Paths.get(filename);

        delete(target);

        KeyStoreValue ki = null;
        KeystoreBuilder ksBuilder;
        KeyStoreHelper service = new KeyStoreHelper();

        ksBuilder = new KeystoreBuilder(StoreFormat.JKS);

        ksBuilder.create(filename, "111".toCharArray());
        return service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray());

    }
}
