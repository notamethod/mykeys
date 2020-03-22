package org.dpr.mykeys.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.utils.ProviderUtil;
import org.dpr.mykeys.ihm.Messages;

import java.util.LinkedHashMap;
import java.util.Map;

public class X509AttributesUtils {

    final static Log log = LogFactory.getLog(X509AttributesUtils.class);
    private static Map<String, String> mapKeyLength = null;
    private static Map<String, String> mapSignatureAlgorithms;
    private static Map<String, String> mapKeyPairAlgoList;

    /**
     * @return the mapNames
     */
    public static Map<String, String> getMapKeyLength() {
        if (mapKeyLength == null) {
            mapKeyLength = new LinkedHashMap<>();
            for (String kl : getStandardKeyLength()) {
                mapKeyLength.put(Messages.getString("keysize.value", kl), kl);
            }
        }
        return mapKeyLength;
    }

    /**
     * @return the mapNames
     */
    public static Map<String, String> getMapSignatureAlgorithms() {
        if (mapSignatureAlgorithms == null) {
            mapSignatureAlgorithms = new LinkedHashMap<>();
            for (String algo : ProviderUtil.SignatureList) {
                mapSignatureAlgorithms.put(algo, algo);
            }
        }
        return mapSignatureAlgorithms;
    }

    public static Map<String, String> getMapKeyPairAlgorithms() {
        if (mapKeyPairAlgoList == null) {
            mapKeyPairAlgoList = new LinkedHashMap<>();
            for (String algo : ProviderUtil.getKeyPairGeneratorList()) {
                mapKeyPairAlgoList.put(algo, algo);
            }
        }
        return mapKeyPairAlgoList;
    }


    private static String[] getStandardKeyLength() {
        return new String[]{"512", "1024", "2048", "4096", "8192"};
    }
}
