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
package org.dpr.mykeys;

import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.windows.CreateCrlDialog;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.CreateUserDialog;

/**
 * @author Christophe Roger
 * @date 18 mai 2009 TODO: mode autonome: pas de stockage Mode sur: stockage des
 *       clï¿½s avec un mot de passe maitre
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//MykeysFrame frame = new MykeysFrame();
				try {
					new MykeysFrame();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// frame.addComponents();

			}
		});

	}

	private void init() {

		// Locale.setDefault(Locale.ENGLISH);
		log.debug("loading configuration...");
		  
		KSConfig.initResourceBundle();
		log.info("initializing securiy provider...");
		Security.addProvider(new BouncyCastleProvider());
		ProviderUtil.init("BC");
		
		try {
			KSConfig.init(".myKeys2");
			checkUpdate();
			checkConfig();
		} catch (Exception e) {

			MykeysFrame.showError(null, KSConfig.getMessage().getString("error.config"));
			throw new RuntimeException("Fatal Error");
		}
		
		// buildComponents();
		// updateKeyStoreList();
	}

	private void checkUpdate() throws InvocationTargetException, InterruptedException {
		if (!KSConfig.getInternalKeystores().existsUserDatabase()) {
			
			boolean retour = MykeysFrame.askConfirmDialog(null, "Vous devez créer un utilisateur avant de continuer");
			if (!retour) {
				System.exit(0);
			}
			
			
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					CreateUserDialog cs = new CreateUserDialog(
							null, true);
					//cs.setLocationRelativeTo(MykeysFrame);
					cs.setVisible(true);
				}
			});

		}
		if (!KSConfig.getInternalKeystores().existsUserDatabase())
			System.exit(0);

		
	}

	private void checkConfig() {

		Iterator<?> iter = KSConfig.getUserCfg().getKeys("store");
		boolean update = false;
		Map<String, HashMap> typesKS = new HashMap<String, HashMap>();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			log.info("found store info: "+key);
			List list = KSConfig.getUserCfg().getList(key);
			typesKS.put(key, new HashMap<String, String>());
			for (Object o : list) {
				String dirName = (String) o;
				log.info("check file store info: "+dirName);
				File f = new File(dirName);
				if (f.exists()) {
					typesKS.get(key).put(dirName, dirName);
					log.info("exist ok: "+dirName);
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

	/**
	 * .
	 * 
	 */
	private static KeyStoreInfo getACKeystore() {
		String path = KSConfig.getInternalKeystores().getACPath();
		KeyTools kt = new KeyTools();
		String pwd = KSConfig.getInternalKeystores().getPassword();
		KeyStoreInfo kinfo = null;

		kinfo = new KeyStoreInfo("interne", KSConfig.getInternalKeystores().getACPath(),
				StoreModel.CASTORE, StoreFormat.JKS, StoreLocationType.INTERNAL);
		return kinfo;
		// try {
		// kt.loadKeyStore(path, StoreFormat.JKS, pwd.toCharArray());
		// } catch (KeyToolsException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// kt.createKeyStore(StoreFormat.JKS,
		// KSConfig.getInternalKeystores().getACPath(), pwd.toCharArray());
		// //InternalKeystores.path=path;
		// return new KeyStoreInfo("interne", KSConfig.getInternalKeystores().getACPath(),
		// StoreModel.CASTORE, StoreFormat.JKS, StoreType.INTERNAL);

	}

}
