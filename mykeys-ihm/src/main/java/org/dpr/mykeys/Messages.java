package org.dpr.mykeys;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "Messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object... params) {
        try {
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getFullString(String key, String... params) {
        try {
            String[] strParams = new String[params.length];
            int i = 0;
            for (String whatever : params) {
                strParams[i++] = Messages.getString(whatever);
            }
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), strParams);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
