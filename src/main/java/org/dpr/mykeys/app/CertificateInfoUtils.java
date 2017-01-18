package org.dpr.mykeys.app;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bouncycastle.asn1.x509.KeyUsage;

public class CertificateInfoUtils {
	
	
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
		
		for (KeyUsageEnum usage : KeyUsageEnum.values()){
			if ((keyUsage & usage.getIntValue()) == usage.getIntValue())
				value = value + ", " + usage.getLabel();
		  }

		if (!value.isEmpty()) {
			return value.substring(1, value.length());
		} else {
			return null;
		}

	}
	

}
