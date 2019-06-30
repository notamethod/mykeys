package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.PkiTools.TypeObject;

public enum StoreFormat {
    JKS(".jks"), PKCS12(".p12"), PEM(".pem"), DER(".der"), UNKNOWN(""), PROPERTIES("");

    private final String extension;


    private StoreFormat(String extension) {
        this.extension = extension;

    }

    public String getExtension() {
        return extension;
    }


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