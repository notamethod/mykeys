package org.dpr.mykeys.app;

public enum StoreType {
	INTERNAL, EXTERNAL;
	public static StoreType fromValue(String v) {
		return valueOf(v);
	}

	public static String getValue(StoreType type) {
		return type.toString();
	}
}