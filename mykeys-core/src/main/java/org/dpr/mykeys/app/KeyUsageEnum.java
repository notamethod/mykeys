package org.dpr.mykeys.app;

import org.bouncycastle.asn1.x509.KeyUsage;

public enum KeyUsageEnum {
	cRLSign(KeyUsage.cRLSign, "cRLSign"), digitalSignature(KeyUsage.digitalSignature,
			"digitalSignature"), nonRepudiation(KeyUsage.nonRepudiation, "nonRepudiation"), keyEncipherment(
					KeyUsage.keyEncipherment, "keyEncipherment"), dataEncipherment(KeyUsage.dataEncipherment,
							"dataEncipherment"), keyAgreement(KeyUsage.keyAgreement, "keyAgreement"), keyCertSign(
									KeyUsage.keyCertSign, "keyCertSign"), encipherOnly(KeyUsage.encipherOnly,
											"encipherOnly"), decipherOnly(KeyUsage.decipherOnly, "decipherOnly");

	private int intValue;
	private String label;

	KeyUsageEnum(int value, String strValue) {
		intValue = value;
		label = strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public String getLabel() {
		return label;
	}

}

// public static final int = (1 << 7);
// public static final int = (1 << 6);
// public static final int = (1 << 5);
// public static final int = (1 << 4);
// public static final int = (1 << 3);
// public static final int = (1 << 2);
// public static final int cRLSign = (1 << 1);
// public static final int = (1 << 0);
// public static final int = (1 << 15);