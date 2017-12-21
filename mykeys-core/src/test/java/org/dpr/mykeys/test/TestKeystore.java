package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

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
        KeyStoreInfo ksInfo = new KeyStoreInfo("aaz", fileName,
                StoreModel.CERTSTORE, StoreFormat.JKS);
        ksInfo.setPassword("aaa".toCharArray());
       try {
            service.load(ksInfo);
           fail();
        } catch (KeyToolsException e) {
           e.printStackTrace();

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
        KeyStoreInfo ksInfo = new KeyStoreInfo("aa", fileName,
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
        KeyStoreInfo ksInfo = new KeyStoreInfo("aaz", fileName,
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
        String filename="target/test-classes/data/empty.jks";
        KeyStoreHelper service = new KeyStoreHelper();



        try {
           KeyStore ks= service.loadKeyStore(filename, StoreFormat.JKS, "111".toCharArray());
            service.saveKeyStore(ks, filename, "111".toCharArray());
        } catch (KeyToolsException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void add_cert() {
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

        CertificateHelper certServ = new CertificateHelper(certModel);

        try {
            certServ.genererX509(certModel, certModel, isAC);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            fail(e.getMessage());
        }
    }

    @Test
    public void create_ks() {

        KeyStoreHelper service = new KeyStoreHelper();

        String filename="target/test-classes/data/test-create01.jks";
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
}
