package org.dpr.mykeys.app;


import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Utilities from dragndrop features
 */
public class PkiTools {

    public static TypeObject getTypeObject(File transferFile) {
        Map<String, String> map = new HashMap<>();
        map.put("p12", "p12");
        map.put("pfx", "p12");
        map.put("cer", "cer");
        map.put("der", "der");
        map.put("pem", "pem");
        String ext = FilenameUtils.getExtension(transferFile.getName());
        if (map.get(ext) != null) {
            return TypeObject.get(map.get(ext));
        }
        return null;
    }


    public enum TypeObject {

        X509("x509"), MAGP12("p12"), MAGCER("cer"), UNKNOWN("unknown");

        private final String value;

        TypeObject(String value) {
            this.value = value;
        }


        String getValue() {
            return this.value;
        }

        static TypeObject get(String value) {
            TypeObject[] types = TypeObject.values();
            for (TypeObject type : types) {
                if (StringUtils.equals(type.getValue(), value)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

    }
}
