package org.dpr.mykeys.keystore;

public enum CertificateType {
	STANDARD, AC, SERVER, CODE_SIGNING;
	public static CertificateType fromValue(String v) {
		return valueOf(v);
	}

	public static String getValue(CertificateType type) {
		return type.toString();
	}
}