package org.dpr.mykeys.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ProviderUtil {
    private final static Log log = LogFactory.getLog(ProviderUtil.class);
    public static List<String> SignatureList;
    public static Provider provider;
    private static List<String> KeyPairGeneratorList;

    public static void main(String[] args) {

        initBC();
    }

    public static void init() {
        initServices();
    }

    public static void init(String p) {
        provider = Security.getProvider(p);
        initServices();
    }

    public static void initBC() {
        Security.addProvider(new BouncyCastleProvider());
        provider = Security.getProvider("BC");
        initServices();
    }

    private static void initServices() {
        initKeyPairGeneratorList();
        initSignaturesList();
    }

    private static void initKeyPairGeneratorList() {
        KeyPairGeneratorList = new ArrayList<>();
        for (Object o : provider.keySet()) {
            String entry = (String) o;
            if (entry.startsWith("KeyPairGenerator.")) {
                KeyPairGeneratorList.add(entry.substring("KeyPairGenerator."
                        .length()));
            }
        }
        Collections.sort(KeyPairGeneratorList);

        printList("setKeyPairGeneratorList", KeyPairGeneratorList);
    }

    private static void initSignaturesList() {
        SignatureList = new ArrayList<>();
        for (Object o : provider.keySet()) {
            String entry = (String) o;
            if (entry.startsWith("Signature.")) {
                SignatureList.add(entry.substring("Signature.".length()));
            }
        }
        Collections.sort(SignatureList);
        printList("setSignatureList", SignatureList);
    }

    private static void printList(String setName, List algorithms) {
        if (log.isDebugEnabled()) {
            log.debug(setName + ":");
            if (algorithms.isEmpty()) {
                log.debug("            None available.");
            } else {
                for (Object algorithm : algorithms) {
                    String name = (String) algorithm;

                    log.debug("            " + name);
                }
            }
        }
    }

    public static List<String> getKeyPairGeneratorList() {
        if (KeyPairGeneratorList == null) {
            Security.addProvider(new BouncyCastleProvider());
            ProviderUtil.init("BC");
        }
        return KeyPairGeneratorList;
    }
}
