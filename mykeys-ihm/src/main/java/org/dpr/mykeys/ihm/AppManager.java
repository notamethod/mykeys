package org.dpr.mykeys.ihm;

import org.apache.commons.codec.binary.Hex;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.ihm.listeners.EventKeystoreListener;
import org.dpr.mykeys.ihm.windows.MykeysFrame;

import java.security.KeyStoreException;
import java.util.*;

public class AppManager {

    private final HashMap<String, KeyStoreValue> ksList = new HashMap<>();
    private List<EventKeystoreListener> listeners = new ArrayList<>();


    public void start() throws Exception{
        new MykeysFrame();
    }

    /** Constructeur privé */
    private AppManager()
    {}

    /** Instance unique non préinitialisée */
    private static AppManager INSTANCE = null;

    /** Point d'accès pour l'instance unique du singleton */
    public static AppManager getInstance()
    {
        if (INSTANCE == null)
        {   INSTANCE = new AppManager();
        }
        return INSTANCE;
    }

    public static void removeKeyStore(String path) {

        Iterator iter = KSConfig.getUserCfg().getKeys(KSConfig.STORE_PREFIX);
        boolean update = false;
        List<String> dirNameList = new ArrayList<>();
        Map<String, HashMap> typesKS = new HashMap<>();
        while (iter.hasNext()) {
            String key = (String) iter.next();

            List list = KSConfig.getUserCfg().getList(key);
            typesKS.put(key, new HashMap<String, String>());
            for (Object o : list) {
                String dirName = (String) o;

                if (path.equals(dirName)) {
                    update = true;
                    dirNameList.add(dirName);
                } else {
                    typesKS.get(key).put(dirName, dirName);
                }
            }
        }
        if (update) {
            Set ks1 = typesKS.keySet();
            for (String key1 : (Iterable<String>) ks1) {
                KSConfig.getUserCfg().clearProperty(key1);
                Set ks2 = typesKS.get(key1).keySet();
                for (String key2 : (Iterable<String>) ks2) {
                    KSConfig.getUserCfg().addProperty(key1, key2);

                }
                //remove other properties
                for (String dname : dirNameList) {
                    byte[] encoded = Base64.getEncoder().encode(dname.getBytes());
                    String hexString = Hex.encodeHexString(encoded);
                    KSConfig.getUserCfg().clearProperty("intpwd." + hexString);
                }
            }
            KSConfig.save();
        }

    }

    public HashMap<String, KeyStoreValue> updateKeyStoreList() throws KeyStoreException {
        KSConfig.getInternalKeystores().getACPath();
        Iterator iter = KSConfig.getUserCfg().getKeys(KSConfig.STORE_PREFIX);
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String[] typeTmp = key.split("\\.");
            if (typeTmp != null && typeTmp.length > 2) {
                List list = KSConfig.getUserCfg().getList(key);
                for (Object o : list) {
                    String dirName = (String) o;
                    String fileName = dirName.substring(dirName.lastIndexOf("\\") + 1);
                    KeyStoreValue ki = new KeyStoreValue(fileName, dirName, StoreModel.fromValue(typeTmp[1]),
                            StoreFormat.valueOf(typeTmp[2]));
                    byte[] encoded = Base64.getEncoder().encode(dirName.getBytes());
                    String hexString = Hex.encodeHexString(encoded);

                    if (KSConfig.getUserCfg().getBoolean("intpwd." + hexString, false)) {
                        ki.setStoreType(StoreLocationType.INTERNAL);
                        ki.setOpen(true);
                    }

                    // if (ki.getStoreModel().equals(StoreModel.CASTORE)){
                    // InternalKeystores.setPath(dirName);
                    // }
                    ksList.put(dirName, ki);
                }
            }
        }
        return ksList;

    }

    public void fireKeystoreChanged() throws KeyStoreException {
        updateKeyStoreList();
        for (EventKeystoreListener listener: listeners){
            listener.KeystoreAdded(null);
        }
    }

    public void addListener(EventKeystoreListener listener){
        listeners.add(listener);
    }
}
