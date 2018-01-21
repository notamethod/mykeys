package org.dpr.mykeys.utils;

import java.util.MissingResourceException;

class ResourceBundle {
    private static final String BUNDLE_NAME = "Messages"; //$NON-NLS-1$

    private static final java.util.ResourceBundle RESOURCE_BUNDLE = java.util.ResourceBundle.getBundle(BUNDLE_NAME);

    private ResourceBundle() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
