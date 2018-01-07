package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.PkiTools.TypeObject;

public enum StoreFormat {
	JKS, PKCS12, PEM, DER, UNKNOWN, PROPERTIES;
	public static StoreFormat fromValue(String v) {
		StoreFormat fmt = null;
		try {
			fmt = valueOf(v);
		} catch (Exception e) {
			fmt = UNKNOWN;
		}
		return fmt;

	}

	public static String getValue(StoreFormat format) {
		return format.toString();
	}

	public static StoreFormat fromValue(TypeObject typeObject) {
		switch (typeObject) {
		case MAGP12:
			return PKCS12;

        case MAGCER:
        	return DER;

		default:
			return UNKNOWN;
		}
	}
}