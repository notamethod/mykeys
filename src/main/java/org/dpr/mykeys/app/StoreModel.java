package org.dpr.mykeys.app;

public enum StoreModel {
	CASTORE, CERTSTORE, KEYSTORE, P12STORE, PROFILSTORE;

	public static StoreModel fromValue(String v) {
		return valueOf(v);
	}

	public static String getValue(StoreModel type) {
		return type.toString();
	}
}