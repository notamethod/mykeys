/**
 * Copyright (C) 2009 crja
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dpr.mykeys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.ihm.windows.CreateUserDialog;
import org.dpr.mykeys.ihm.windows.IhmException;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.SelectUserDialog;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.Security;
import java.util.*;

/**
 * @author Christophe Roger
 */
public class MyKeys {

    public static final Log log = LogFactory.getLog(MyKeys.class);
    //private static final String ACFileName = "ACKS.jks";
    // messages


    /**
     * @param args
     */
    public static void main(String[] args) {
        MyKeys mk = new MyKeys();
        mk.init();

    }

    private void init() {

        Locale.setDefault(Locale.ENGLISH);
        log.debug("loading configuration...");

        KSConfig.initResourceBundle();
        log.info("initializing securiy provider...");
        Security.addProvider(new BouncyCastleProvider());
        ProviderUtil.init("BC");

        try {
            KSConfig.init(".myKeys25");
            boolean justCreated = checkUpdate();
            checkConfig();
            if (justCreated) {
                migrate();
            }else{
                login();
            }

        } catch (Exception e) {

            MykeysFrame.showError(null, Messages.getString("error.config"));
            throw new RuntimeException("Fatal Error",e);
        }

        // buildComponents();
        // updateKeyStoreList();
    }

    /**
     * not sure to continue on this...
     */
    private void migrate() {
        log.info("migration not implemented");
//        char[] password = null;
//        if (KSConfig.getInternalKeystores().existsACDatabase()){
//
//            KeyStoreValue ki = KSConfig.getInternalKeystores().getStoreAC();
//            KeyStoreHelper kh = new KeyStoreHelper();
//             password = MykeysFrame.showPasswordDialog(null, "Veuillez renseigner votre mot de passe pour upgrader les magasins");
//
//        }
    }

    private void login() {
        SwingUtilities.invokeLater(() -> {
            SelectUserDialog cs = null;
            try {
                cs = new SelectUserDialog(
                        null, true);
            } catch (IhmException e) {
                e.printStackTrace();
            }

            cs.setVisible(true);
        });
    }

    private boolean checkUpdate() throws InvocationTargetException, InterruptedException {
        boolean justCreated=false;
        if (!KSConfig.getInternalKeystores().existsUserDatabase()) {

            boolean retour = MykeysFrame.askConfirmDialog(null, Messages.getString("prompt.createUser"));
            if (!retour) {
                System.exit(0);
            }


            SwingUtilities.invokeAndWait(() -> {
                CreateUserDialog cs = new CreateUserDialog(
                        null, true);
                //cs.setLocationRelativeTo(MykeysFrame);
                cs.setVisible(true);
            });
            justCreated=true;

        }
        if (!KSConfig.getInternalKeystores().existsUserDatabase())
            System.exit(0);

        return justCreated;
    }

    private void checkConfig() {

        Iterator<?> iter = KSConfig.getUserCfg().getKeys("store");
        boolean update = false;
        Map<String, HashMap> typesKS = new HashMap<String, HashMap>();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            log.info("found store info: " + key);
            List list = KSConfig.getUserCfg().getList(key);
            typesKS.put(key, new HashMap<String, String>());
            for (Object o : list) {
                String dirName = (String) o;
                log.info("check file store info: " + dirName);
                File f = new File(dirName);
                if (f.exists()) {
                    typesKS.get(key).put(dirName, dirName);
                    log.info("exist ok: " + dirName);
                } else {

                    update = true;

                }
            }
        }
        if (update) {
            Set ks1 = typesKS.keySet();
            Iterator<String> iter1 = ks1.iterator();
            while (iter1.hasNext()) {
                String key1 = iter1.next();
                KSConfig.getUserCfg().clearProperty(key1);
                Set ks2 = typesKS.get(key1).keySet();
                Iterator<String> iter2 = ks2.iterator();
                while (iter2.hasNext()) {
                    String key2 = iter2.next();
                    KSConfig.getUserCfg().addProperty(key1, key2);

                }

            }
            KSConfig.save();
        }

    }

}
