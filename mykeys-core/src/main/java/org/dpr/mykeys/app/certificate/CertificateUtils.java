package org.dpr.mykeys.app.certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dpr.mykeys.app.KeyUsageEnum;
import org.dpr.mykeys.app.X509Constants;

public class CertificateUtils {

	Map<Integer, String> mapKeyUSage = new HashMap<Integer, String>();

	public static String keyUsageToString(boolean[] keyUsage) {
		String value = "";
		boolean isKeyUsage = false;
		if (keyUsage == null) {
			return "null";
		}
		for (int i = 0; i < keyUsage.length; i++) {
			if (keyUsage[i]) {
				isKeyUsage = true;
				value = value + ", " + X509Constants.keyUsageLabel[i];
			}
		}
		if (isKeyUsage) {
			return value.substring(1, value.length());
		} else {
			return null;
		}
	}

	public static String keyUsageToString(int keyUsage) {
		String value = "";
		boolean isKeyUsage = false;

		for (KeyUsageEnum usage : KeyUsageEnum.values()) {
			if ((keyUsage & usage.getIntValue()) == usage.getIntValue())
				value = value + ", " + usage.getLabel();
		}

		if (!value.isEmpty()) {
			return value.substring(1, value.length());
		} else {
			return null;
		}

	}

	public static boolean[] keyUsageFromInt(int keyUsage) {
		String value = "";
		boolean[] booloKu = new boolean[] { false, false, false, false, false, false, false, false, false };
		boolean isKeyUsage = false;

		for (KeyUsageEnum usage : KeyUsageEnum.values()) {
			if ((keyUsage & usage.getIntValue()) == usage.getIntValue()) {
				for (int i = 0; i < X509Constants.keyUsageInt.length; i++) {
					if (X509Constants.keyUsageInt[i] == usage.getIntValue()) {
						booloKu[i] = true;
					}
				}
			}
		}
		return booloKu;

	}

	/**
	 * Chargement certificat X509 à partir d'un flux.
	 * 
	 * <BR>
	 * 
	 * 
	 * @param aCertStream
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static X509Certificate loadX509CertOld(InputStream aCertStream)
            throws GeneralSecurityException {
		// création d'une fabrique de certificat X509
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		// chargement du certificat
		X509Certificate cert = (X509Certificate) cf.generateCertificate(aCertStream);
		return cert;
	}

    private static Set<X509Certificate> loadX509Certs(InputStream aCertStream) throws GeneralSecurityException {

		CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

		// chargement du certificat
		Collection<X509Certificate> certs = (Collection<X509Certificate>) cf.generateCertificates(aCertStream);
		Set<X509Certificate> certificates = new HashSet<X509Certificate>(certs);
		return certificates;
	}

    public static List<CertificateValue> loadX509Certs(String fileName) {

        // NodeInfo nInfo = new KeyStoreValue(new File(fileName));
		List<CertificateValue> certsRetour = new ArrayList<CertificateValue>();

		InputStream is = null;
		try {
			is = new FileInputStream(new File(fileName));
			Set<X509Certificate> certs = loadX509Certs(is);

			for (X509Certificate cert : certs) {
				CertificateValue certInfo = new CertificateValue(null, cert);

				certsRetour.add(certInfo);
			}

		} catch (FileNotFoundException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return certsRetour;

	}
	

}
