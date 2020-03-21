package org.dpr.mykeys.app.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeystoreUtils {
    public static final String KSTYPE_EXT_JKS = "jks";
    public static final String KSTYPE_EXT_P12 = "p12";
    private static final String[] KSTYPE_EXTS_PKCS12 = {"p12", "pfx", "pkcs12"};
    private static final String[] KSTYPE_EXTS_DER = {"der", "cer"};
    private static final String KSTYPE_EXT_PEM = "pem";

    private static final Log log = LogFactory.getLog(KeystoreUtils.class);

    public static StoreFormat findKeystoreType(String filename) {

        log.debug("finding type of file...");
        try {
            String ext = filename.substring(filename.lastIndexOf('.') + 1);
            if (ext.equalsIgnoreCase(KSTYPE_EXT_JKS)) {
                return StoreFormat.JKS;
            }
            for (String aliasType : KSTYPE_EXTS_PKCS12) {
                if (ext.equalsIgnoreCase(aliasType)) {
                    return StoreFormat.PKCS12;
                }
            }
            for (String aliasType : KSTYPE_EXTS_DER) {
                if (ext.equalsIgnoreCase(aliasType)) {
                    return StoreFormat.DER;
                }
            }
            if (ext.equalsIgnoreCase(KSTYPE_EXT_PEM)) {
                return StoreFormat.PEM;
            }
            return StoreFormat.UNKNOWN;
        } catch (IndexOutOfBoundsException e) {
            return StoreFormat.UNKNOWN;
        }
    }
}
