package org.dpr.mykeys.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageUtils {

	// messages
	private static ResourceBundle messages=null;
	private static final Log log = LogFactory.getLog(MessageUtils.class);
	
	private static void init() {

		// Locale.setDefault(Locale.ENGLISH);
		log.debug("loading configuration...");
		
		Locale currentLocale = Locale.getDefault();
		try {
			messages = ResourceBundle.getBundle(
					"Messages", currentLocale);
		} catch (Exception e) {
			messages = ResourceBundle.getBundle(
					"Messages", Locale.ENGLISH);
		}
	}	
	
	public static ResourceBundle getBundleMessage() {

		if (messages==null){
			init();
		}
		return messages;
	}	
	
	public static String getStringMessage(String key) {
		if (messages==null){
			init();
		}
		return getBundleMessage().getString(key);
	}	
	public static String getMessage(String key) {
		if (messages==null){
			init();
		}
		return getBundleMessage().getString(key);
	}	
}
