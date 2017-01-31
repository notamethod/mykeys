package org.dpr.mykeys.certificate.windows;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dpr.mykeys.certificate.CertificateInfo;

public class FillUtils {

	public static void fillCertInfo(Map<String, Object> elements, CertificateInfo certInfo) {

		certInfo.setAlgoPubKey((String) elements.get("algoPubKey"));
		certInfo.setAlgoSig((String) elements.get("algoSig"));
		certInfo.setKeyLength((String) elements.get("keyLength"));

		// certInfo.setDuration((Integer) elements.get("duration"));
		//
		// certInfo.setSubjectMap(elements);
		//
		// certInfo.setCrlDistributionURL(((String)
		// elements.get("CrlDistrib")));
		// certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
		// certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));
	}

}
