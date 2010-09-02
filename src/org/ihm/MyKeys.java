/**
 * Copyright (C) 2009 crja
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ihm;

import java.io.File;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.app.ACKeystore;
import org.app.KSConfig;
import org.app.KeyTools;
import org.app.ProviderUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ihm.panel.CreateStoreDialog;

/**
 * @author Christophe Roger
 * @date 18 mai 2009 TODO: mode autonome: pas de stockage Mode sur: stockage des
 *       clés avec un mot de passe maitre
 */
public class MyKeys {

    private static final String ACFileName = "ACKS.jks";
    // messages
    private static ResourceBundle messages;

    /**
     * @param args
     */
    public static void main(String[] args) {
	MyKeys mk = new MyKeys();
	mk.init();
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		KeyStoreUI frame = new KeyStoreUI();
		// frame.addComponents();

	    }
	});

    }

    private void init() {

	Locale currentLocale = Locale.getDefault();
	try {
	    messages = ResourceBundle.getBundle("org.config.Messages",
		    currentLocale);
	} catch (Exception e) {
	    messages = ResourceBundle.getBundle("org.config.Messages",
		    Locale.ENGLISH);
	}
	try {
	    KSConfig.load();
	    checkConfig();
	} catch (Exception e) {
	    KeyStoreUI.showError(null, messages.getString("error.config"));
	    throw new RuntimeException("Fatal Error");
	}
	Security.addProvider(new BouncyCastleProvider());
	ProviderUtil.init("BC");
	// buildComponents();
	// updateKeyStoreList();

    }

    public static ResourceBundle getMessage() {
	if (messages == null) {
	    Locale currentLocale = Locale.getDefault();
	    messages = ResourceBundle.getBundle("org.config.Messages",
		    currentLocale);
	}
	return messages;
    }

    private void checkConfig() {

	Iterator iter = KSConfig.getUserCfg().getKeys("magasin");
	boolean update = false;
	Map<String, HashMap> typesKS = new HashMap<String, HashMap>();
	while (iter.hasNext()) {
	    String key = (String) iter.next();

	    List list = KSConfig.getUserCfg().getList(key);
	    typesKS.put(key, new HashMap<String, String>());
	    for (Object o : list) {
		String dirName = (String) o;
		File f = new File(dirName);
		if (f.exists()) {
		    typesKS.get(key).put(dirName, dirName);
		} else {
		    String type = key.split("\\.")[1];
		    if (type.equals("AC")){
			String acName = createACKeystore();
			if (acName != null){
			    typesKS.get(key).put(acName, acName);
			}
		    }
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

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     */
    private String createACKeystore() {
	String path = System.getProperty("user.home") + File.separator + KSConfig.cfgPathName + File.separator + ACFileName;
	KeyTools kt = new KeyTools();
	String pwd = ACKeystore.password;
	
	try {
	    kt.createKeyStore("JKS",
		    path, pwd.toCharArray());
	    ACKeystore.path=path;
	    return path;

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;
	}

	
    }

}
