package org.dpr.mykeys.certificate;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.dpr.mykeys.app.KeyTools;

public class CertificateManager {
	KeyTools ktool;
	CertificateInfo certInfo;

	public CertificateManager(CertificateInfo certInfo) {
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

}
