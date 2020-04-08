package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.certificate.CSRManager;
import org.dpr.mykeys.app.certificate.CertificateManager;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CRLManager;
import org.dpr.mykeys.app.crl.CrlValue;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.utils.ProviderUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.*;

import static org.junit.Assert.*;

public class CertificateTest {

    private final static Log log = LogFactory.getLog(CertificateTest.class);

    private static final String AC_NAME = "mykeys root ca 2";

    @BeforeClass
    public static void init() {

        KSConfigTestTmp.initResourceBundle();

        KSConfigTestTmp.init(".myKeys");

        Security.addProvider(new BouncyCastleProvider());

        ProviderUtil.initBC();
    }

    @Test
    public void self_signed_create_ok() {
        boolean isAC = false;
        CertificateValue certModel = new CertificateValue("aliastest");
        certModel.setAlgoPubKey("RSA");
        certModel.setAlgoSig("SHA1WithRSAEncryption");

        certModel.setKeyLength(1024);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        certModel.setNotBefore(new Date());
        certModel.setNotAfter(cal.getTime());
        CertificateValue certIssuer = new CertificateValue();
        certModel.setSubjectMap("CN=toto");
        CertificateManager certServ = new CertificateManager();

        try {
            certServ.generate(certModel, certModel, CertificateType.STANDARD);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            fail(e.getMessage());
        }
    }

    @Test
    public void create_from_csr_ok() throws ServiceException {

        boolean isAC = false;
        CertificateValue certModel = new CertificateValue("aliastest");
        certModel.setAlgoPubKey("RSA");
        certModel.setAlgoSig("SHA1WithRSAEncryption");

        certModel.setKeyLength(1024);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        certModel.setNotBefore(new Date());
        certModel.setNotAfter(cal.getTime());

        CSRManager certServ = new CSRManager();
        KeyStoreHelper ksh = new KeyStoreHelper();
        CertificateValue certIssuer = ksh.findCertificateAndPrivateKeyByAlias(getStoreAC(), AC_NAME);
        try {
            CertificateValue out=certServ.generateCertificate(new FileInputStream(new File("src/test/resources/data/cert1.csr")), certIssuer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void create_csr() throws ServiceException {

        boolean isAC = false;
//        CertificateValue certModel = new CertificateValue("aliastest");
//        certModel.setAlgoPubKey("RSA");
//        certModel.setAlgoSig("SHA1WithRSAEncryption");
//        certModel.setKeyLength(1024);
//
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MONTH, 1);
//        certModel.setNotBefore(new Date());
//        certModel.setNotAfter(cal.getTime());

        CSRManager certCsr = new CSRManager();
        KeyStoreHelper ksh = new KeyStoreHelper();
        CertificateManager certServ = new CertificateManager();
        KeyPair kp = certServ.generateKeyPair("RSA", 2048);
        X500Principal principal = new X500Principal("CN=Requested Test Certificate");
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        try {
            byte[] csr = certCsr.generateCSR(principal, kp);
            File f = new File("target/test-classes/csr.csr");
            if (f.exists())
                f.delete();
            certCsr.exportToFile(csr, f);
            assertTrue(f.exists());

        } catch (IOException | OperatorCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

    private KeyStoreValue getStoreAC() {


        String pwd = "mKeys983178";
        KeyStoreValue kinfo = null;


        kinfo = new KeyStoreValue("", new File("src/test/resources/data/mykeysAc.jks").getAbsolutePath(),
                StoreModel.CASTORE, StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(pwd.toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }

    @Test
    public void generateCRL() throws ServiceException {

        KeyStoreHelper ksh = new KeyStoreHelper();
        CertificateValue certIssuer = ksh.findCertificateAndPrivateKeyByAlias(getStoreAC(), AC_NAME);
        CRLManager man = new CRLManager();
        CrlValue crlValue = new CrlValue();
        List<String> serials = new ArrayList<>();
        X509CRL crl = null;

        // certInfo.setX509PrincipalMap(elements);
        HashMap<String, String> subjectMap = new HashMap<>();
        crlValue.setName("name");
        crlValue.setThisUpdate(new Date());
        crlValue.setNextUpdate(new Date());
        try {
            crl = man.generateCrl(certIssuer, crlValue, serials);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        String fpath = new File("target/test-classes/crl1.crl").getAbsolutePath();
        try {
            man.saveCRL(crl, fpath);
        } catch (CRLException | IOException e) {
            e.printStackTrace();
        }
        try (FileInputStream fis = new FileInputStream(fpath)) {

            X509CRL returnCRL = man.getCrl(fis);
            assertEquals(returnCRL.getIssuerDN(), crl.getIssuerDN());
            assertEquals(returnCRL.getThisUpdate(), crl.getThisUpdate());
            assertEquals(returnCRL.getNextUpdate(), crl.getNextUpdate());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }
}
