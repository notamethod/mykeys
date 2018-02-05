package org.dpr.mykeys.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.AuthenticationService;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class AuthenticationTest {

    private final static Log log = LogFactory.getLog(AuthenticationTest.class);

    private static final String AC_NAME = "mykeys root ca 2";

    private KeyTools ktools = new KeyTools();

    @BeforeClass
    public static void init() {

        // Locale.setDefault(Locale.ENGLISH);
        log.debug("loading configuration...");

        KSConfig.initResourceBundle();
        KSConfig.externalPath = "target/test-classes/data/";
        KSConfig.init(".myKeys2");

        ProviderUtil.initBC();
    }

    @Before
    public void setupTests() throws IOException {
        Path source = Paths.get("target/test-classes/data/userDBOri.jks");
        Path target = Paths.get("target/test-classes/data/userDB.jks");
        Files.copy(source, target, REPLACE_EXISTING);
    }

    @Test
    public void list_users() {
        AuthenticationService service = new AuthenticationService();
        System.out.println(KSConfig.getInternalKeystores().existsUserDatabase());
        try {
            List<CertificateValue> users = service.listUsers();
            assertThat(users).hasSize(2);
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void add_users() {
        AuthenticationService service = new AuthenticationService();
        System.out.println(KSConfig.getInternalKeystores().existsUserDatabase());
        try {
            service.createUser("user3", "pwd".toCharArray());

            List<CertificateValue> lst = service.listUsers();
            assertEquals("", 3, lst.size());

            for (CertificateValue val : lst) {
                assertTrue("", val.getAlias().startsWith("user"));
                assertTrue("", val.isContainsPrivateKey());


            }
        } catch (ServiceException e) {

            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void load_users() {
        AuthenticationService service = new AuthenticationService();
        System.out.println(KSConfig.getInternalKeystores().existsUserDatabase());
        try {
            CertificateValue val = service.loadUser("user1", "pwd".toCharArray());

            assertEquals("", val.getAlias(), "user1");

        } catch (Exception e) {

            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void delete_users() {
        AuthenticationService service = new AuthenticationService();
        System.out.println(KSConfig.getInternalKeystores().existsUserDatabase());
        try {
            List<CertificateValue> lst = service.listUsers();
            assertEquals("", lst.size(), 2);
            service.deleteUser("user1");
            lst = service.listUsers();
            assertEquals("", lst.size(), 1);
        } catch (ServiceException e) {

            e.printStackTrace();
            fail("");
        }
    }
}
