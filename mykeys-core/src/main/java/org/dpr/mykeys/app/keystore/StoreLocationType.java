package org.dpr.mykeys.app.keystore;

/**
 * Keystore location type: inside the App or external.
 * @author christophe
 *
 */
public enum StoreLocationType {
    INTERNAL, EXTERNAL;

	public static String getValue(StoreLocationType type) {
		return type.toString();
	}
}