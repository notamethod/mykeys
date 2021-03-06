package org.dpr.mykeys.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.crl.CRLManager;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.utils.X509Util;

import javax.swing.filechooser.FileSystemView;

public class KSConfig {

	private static final Log log = LogFactory.getLog(KSConfig.class);
	private static PropertiesConfiguration userConfig;

    private static PropertiesConfiguration defaultConfig;

    public static final String STORE_PREFIX = "store";


    public static final String MKPATH = ".mykeys2";
	public static final String MK1PATH = ".myKeys";

	private static String path;
	
	public static String externalPath;


    private static InternalKeystores internalKeystores;
	
	private static ResourceBundle messages;

    public static void init() {
        init(MKPATH);
    }
	public static void init(String cfgPathName) {

		if (externalPath==null)
			path = System.getProperty("user.home") + File.separator + cfgPathName  + File.separator;
		else 
			path=externalPath;
        String usrFileName = "user.properties";
        try {
			userConfig = new PropertiesConfiguration(path + File.separator
					+ usrFileName);

		} catch (ConfigurationException e) {
			// create files

            userConfig = new PropertiesConfiguration();
			userConfig.setFile(new File(path, usrFileName));

		}
        String defaultFileName = "default.properties";
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
        File f = new File(path, "mk.log");
        addAppender(f);
		userConfig.setAutoSave(true);
		defaultConfig.setAutoSave(true);
    }

	/**
	 * Manually add appender: in case of first launch, directory not exists
	 * @param f the log file
	 */
	private static void addAppender(File f) {

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		Layout layout = PatternLayout.newBuilder().withPattern(PatternLayout.SIMPLE_CONVERSION_PATTERN)
				.withConfiguration(config)
				.build();

		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy("2MB");
		DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
		.withConfig(config)
		.withFileIndex("5")
		.build();

		RollingFileAppender appender = RollingFileAppender.newBuilder().
				setConfiguration(config)
				.setName("FILE")
				.setLayout(layout)
				.withFilePattern("rolling-%d{MM-dd-yy}.log.gz")
				.withPolicy(policy)
				.withStrategy(strategy)
				.withFileName(f.getAbsolutePath()).build();

		appender.start();
		config.addAppender(appender);
		config.getAppenders();
		LoggerConfig conf = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		conf.addAppender(appender, null,null);
		ctx.updateLoggers();
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

    public static InternalKeystores getInternalKeystores()  {
		if (internalKeystores == null) {
			internalKeystores = new InternalKeystores(getCfgPath() ,getProfilsPath());
		}
		return internalKeystores;
	}

	public static String getDefaultCrlPath(Certificate certificateValue) {

		return getCrlPath() + internalKeystores.generateName(StoreModel.PKISTORE, false) + File.separator + X509Util.toHexString(certificateValue.getDigestSHA256(), "", false) + CRLManager.CRL_EXTENSION;
	}
	/**
	 * Return  directory used for certificates storage.
	 *
	 * @return directorny name
	 */
	public static String getDefaultCertificatePath() {

		String dir = getUserCfg().getString("data.dir");
		if (dir == null) {
			File f = FileSystemView.getFileSystemView().getDefaultDirectory();
			File data = new File(f, Messages.getString("default.datadir"));
			data.mkdirs();
			dir = data.getAbsolutePath();
		}
		return dir;
	}

	public static String getCrlPath() {
        String crlPathName = "crls";
        return path + crlPathName + File.separator;
	}
}
