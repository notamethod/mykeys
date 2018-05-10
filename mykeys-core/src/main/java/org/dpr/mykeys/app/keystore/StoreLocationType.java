package org.dpr.mykeys.app.keystore;

/**
 * Keystore location type: inside the App or external.
 * @author christophe
 *
 */
public enum StoreLocationType {
    INTERNAL, EXTERNAL, INTERNAL_TEMP;
	public static StoreLocationType fromValue(String v) {
		return valueOf(v);
	}

	public static String getValue(StoreLocationType type) {
		return type.toString();
	}
}