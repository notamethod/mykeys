package org.dpr.mykeys.ihm.windows.certificate;

import java.util.Map;

import org.dpr.mykeys.app.certificate.CertificateValue;

public class FillUtils {

	public static void fillCertInfo(Map<String, Object> elements, CertificateValue certInfo) {

		certInfo.setAlgoPubKey((String) elements.get("algoPubKey"));
		certInfo.setAlgoSig((String) elements.get("algoSig"));
		certInfo.setKeyLength((String) elements.get("keyLength"));

		certInfo.setDuration(Integer.valueOf((String) elements.get("duration")));
		//
		// certInfo.setSubjectMap(elements);
		//
		// certInfo.setCrlDistributionURL(((String)
		// elements.get("CrlDistrib")));
		// certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
		// certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));
	}

}
