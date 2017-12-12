package org.dpr.mykeys.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenerateCertificateTest {

	final static Log log = LogFactory.getLog(GenerateCertificateTest.class);

	private static final String AC_NAME = "mykeys root ca 2";

	private KeyTools ktools = new KeyTools();

	@BeforeClass
	public static void init() {

		KSConfig.initResourceBundle();

		KSConfig.load();

		Security.addProvider(new BouncyCastleProvider());

		ProviderUtil.initBC();
	}

	@Test
	public void self_signed_create_ok() {
		boolean isAC = false;
		CertificateInfo certModel = new CertificateInfo("aliastest");
		certModel.setAlgoPubKey("RSA");
		certModel.setAlgoSig("SHA1WithRSAEncryption");

		certModel.setKeyLength(1024);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		certModel.setNotBefore(new Date());
		certModel.setNotAfter(cal.getTime());
		CertificateInfo certIssuer = new CertificateInfo();

		CertificateHelper certServ = new CertificateHelper(certModel);

		try {
			certServ.genererX509(certModel, certModel, isAC);
		} catch (Exception e) {
			log.error(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void create_from_csr_ok() throws ServiceException {

		boolean isAC = false;
		CertificateInfo certModel = new CertificateInfo("aliastest");
		certModel.setAlgoPubKey("RSA");
		certModel.setAlgoSig("SHA1WithRSAEncryption");

		certModel.setKeyLength(1024);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		certModel.setNotBefore(new Date());
		certModel.setNotAfter(cal.getTime());
		CertificateInfo certIssuer = new CertificateInfo();

		CertificateHelper certServ = new CertificateHelper(certModel);
		try {
			certServ.generateFromCSR(new File("src/test/resources/data/cert1.csr").getAbsolutePath(), AC_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

	}
}
