package org.dpr.mykeys.certificate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bouncycastle.asn1.x509.KeyUsage;
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
}
