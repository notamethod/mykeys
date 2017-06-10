package org.dpr.mykeys.certificate;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.X509Constants;

public class CertificateService {
	KeyTools ktool;
	CertificateInfo certInfo;

	public CertificateService(CertificateInfo certInfo) {
		super();
		this.certInfo = certInfo;
	}

	public X509Certificate[] generateX509() throws CertificateException {
		if (ktool == null) {
			ktool = new KeyTools();
		}
		X509Certificate[] xCerts;
		try {
			xCerts = ktool.genererX509(certInfo, certInfo.getIssuer(), false);
		} catch (Exception e) {
			throw new CertificateException(e);
		}
		return xCerts;
	}
	
	public X509Certificate[] generateCrl() throws CertificateException {
		if (ktool == null) {
			ktool = new KeyTools();
		}
		X509Certificate[] xCerts;
		try {
			xCerts = ktool.genererX509(certInfo, certInfo.getIssuer(), false);
		} catch (Exception e) {
			throw new CertificateException(e);
		}
		return xCerts;
	}
	
	public String keyUsageToString() {
		String value = "";
		boolean[] keyUsage= certInfo.getKeyUsage();
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

}
