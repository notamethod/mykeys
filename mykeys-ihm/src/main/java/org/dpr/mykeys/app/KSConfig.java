package org.dpr.mykeys.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle; 

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KSConfig {

	private static final Log log = LogFactory.getLog(KSConfig.class);
	static PropertiesConfiguration userConfig;

	static PropertiesConfiguration defaultConfig;

	static String usrFileName = "user.properties";

	static String defaultFileName = "default.properties";

	public static final String STORE_PREFIX = "store";

	static String path;
	
	static InternalKeystores internalKeystores;
	
	private static ResourceBundle messages;

	public static void init(String cfgPathName) {

		path = System.getProperty("user.home") + File.separator + cfgPathName  + File.separator;
		try {
			userConfig = new PropertiesConfiguration(path + File.separator
					+ usrFileName);

		} catch (ConfigurationException e) {
			// create files
			userConfig = new PropertiesConfiguration();
			userConfig.setFile(new File(path, usrFileName));

		}
		try {

			defaultConfig = new PropertiesConfiguration(path + File.separator
					+ defaultFileName);
			defaultConfig.setAutoSave(true);
		} catch (ConfigurationException e) {
			// create files

			defaultConfig = new PropertiesConfiguration();
			defaultConfig.setFile(new File(path, defaultFileName));

			setDefault(defaultConfig);
		}
		userConfig.setAutoSave(true);
		defaultConfig.setAutoSave(true);

	}
	

	public static String getCfgPath() {
		return path;
	}
	
	public static String getProfilsPath() {
		return path + File.separator + "profils";
	}

	private static void setDefault(PropertiesConfiguration cfg) {
		defaultConfig.setAutoSave(true);
		cfg.setProperty("subject.key.CN", "true");
		cfg.setProperty("subject.key.O", "true");
		cfg.setProperty("subject.key.OU", "true");
		cfg.setProperty("subject.key.L", "true");
		cfg.setProperty("subject.key.ST", "true");
		cfg.setProperty("subject.key.E", "true");
		cfg.setProperty("default.dateFormat", "dd/MM/yyyy HH:mm");

	}

	public static List getSubjectKeys() {
		List<String> list = new ArrayList<String>();
		Iterator<String> iter = userConfig.getKeys("subject.key.");
		while (iter.hasNext()) {
			String name = iter.next();
			if (userConfig.getBoolean(name)) {
				list.add(name.substring("subject.key.".length()));
			}
		}
		return list;

	}

	public static void save() {
		// if (userConfig == null) {
		// return;
		// }
		//
		// try {
		// userConfig.save();
		// } catch (ConfigurationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	/**
	 * @return the application
	 */
	public static PropertiesConfiguration getUserCfg() {
		return userConfig;
	}

	/**
	 * @return the application
	 */
	public static PropertiesConfiguration getDefaultCfg() {
		return defaultConfig;
	}
	
	public static void initResourceBundle() {
		Locale currentLocale = Locale.getDefault();
		log.info("Init RessourceBundle for locale "+currentLocale.toString());
		try {
			messages = ResourceBundle.getBundle(
					"Messages", currentLocale);
		} catch (Exception e) {
			messages = ResourceBundle.getBundle(
					"Messages", Locale.ENGLISH);
		}
		
	}
	
	public static ResourceBundle getMessage() {
		if (messages == null) {
			Locale currentLocale = Locale.getDefault();
			messages = ResourceBundle.getBundle(
					"org.dpr.mykeys.config.Messages", currentLocale);
		}
		return messages;
	}
	
	public static InternalKeystores getInternalKeystores() {
		if (internalKeystores == null) {
			internalKeystores = new InternalKeystores(getCfgPath() +  "mykeysAc.jks",getCfgPath() + "mykeysCert.jks",getProfilsPath());
			
		}
		return internalKeystores;
	}
	
	

}
