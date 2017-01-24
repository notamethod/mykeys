import java.util.Calendar;
import java.util.Date;

import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenerateCertificateTest {

	private KeyTools ktools = new KeyTools();

	@BeforeClass
	public static void init() {
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
		try {
			ktools.genererX509(certModel, certModel, isAC);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
