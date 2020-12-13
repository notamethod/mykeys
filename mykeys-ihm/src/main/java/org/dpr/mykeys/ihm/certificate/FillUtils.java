package org.dpr.mykeys.ihm.certificate;

import java.util.Map;

import org.dpr.mykeys.app.certificate.Certificate;

public class FillUtils {

	public static void fillCertInfo(Map<String, Object> elements, Certificate certInfo) {

		certInfo.setAlgoPubKey((String) elements.get("algoPubKey"));
		certInfo.setAlgoSig((String) elements.get("algoSig"));
		certInfo.setKeyLength((String) elements.get("keyLength"));
	}
}
