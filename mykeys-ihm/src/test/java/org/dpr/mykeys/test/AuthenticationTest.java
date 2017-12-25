package org.dpr.mykeys.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.AuthenticationService;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.junit.BeforeClass;
import org.junit.Test;

public class AuthenticationTest {

	final static Log log = LogFactory.getLog(AuthenticationTest.class);

	private static final String AC_NAME = "mykeys root ca 2";

	private KeyTools ktools = new KeyTools();

	@BeforeClass
	public static void init() {

		// Locale.setDefault(Locale.ENGLISH);
		log.debug("loading configuration...");

		KSConfig.initResourceBundle();
		KSConfig.externalPath = "src/test/resources/data/";
		KSConfig.init(".myKeys2");

		ProviderUtil.initBC();
	}

	@Test
	public void list_users() {
		AuthenticationService service = new AuthenticationService();
		System.out.println(KSConfig.getInternalKeystores().existsUserDatabase());
		try {
			service.listUsers();
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
			service.createUser("user1", "pwd".toCharArray());
			service.createUser("user2", "pwd".toCharArray());
			List<CertificateValue> lst = service.listUsers();
			assertEquals("", lst.size(), 2);
			
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

}
