package org.dpr.mykeys.app.keystore;

public enum StoreType {
	INTERNAL, EXTERNAL;
	public static StoreType fromValue(String v) {
		return valueOf(v);
	}

	public static String getValue(StoreType type) {
		return type.toString();
	}
}