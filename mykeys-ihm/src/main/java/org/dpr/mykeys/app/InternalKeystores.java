/**
 * 
 */
package org.dpr.mykeys.app;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.MkUtils;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;

public class InternalKeystores {

	public static final Log log = LogFactory.getLog(InternalKeystores.class);
	public  String password = "mKeys983178";
	public String getPassword() {
		return password;
	}

	private  String pathAC;
	private  String pathCert;
	private  String pathProfils;
	
	public InternalKeystores(String pathAC, String pathCert, String pathProfils) {
		this.pathAC=pathAC;
		this.pathCert = pathCert;
		this.pathProfils = pathProfils;
	}

	public String getACPath() {

		return pathAC;
	}

	public  String getCertPath() {

		return pathCert;
	}

	public  String getProfilsPath() {

		return pathProfils;
	}

	public  KeyStoreInfo getStoreAC() {
	
		KeyTools kt = new KeyTools();
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathAC);
		if (!f.exists()) {

			try {

				InputStream is = (InternalKeystores.class.getResourceAsStream("/install/mykeysAc.jks"));
				MkUtils.copyFile(is, f);
				// InternalKeystores.class.getResource("/org.dpr.mykeys/config/myKeysAc.jks").getFile()getChannel();

			} catch (Exception e) {
				log.error(e);
			}

		}
		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathAC,
				StoreModel.CASTORE, StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public  KeyStoreInfo getStoreCertificate() {
		KeystoreBuilder ksBuilder = new KeystoreBuilder();
		String pwd = password;
		KeyStoreInfo kinfo = null;
		File f = new File(pathCert);
		//create keystore
		if (!f.exists()) {
			try {
				ksBuilder.create(StoreFormat.JKS, pathCert, pwd.toCharArray());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		kinfo = new KeyStoreInfo(KSConfig.getMessage().getString("magasin.interne"), pathCert,
				StoreModel.CERTSTORE, StoreFormat.JKS, StoreLocationType.INTERNAL);
		kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public ProfilStoreInfo getStoreProfils() {
		

		ProfilStoreInfo kinfo = null;
		File f = new File(pathProfils);
		if (!f.exists()) {
			f.mkdirs();

		}
		kinfo = new ProfilStoreInfo(KSConfig.getMessage().getString("profil.name"), pathProfils, StoreFormat.PROPERTIES);
		kinfo.setPassword("null".toCharArray());
		kinfo.setOpen(true);
		return kinfo;
	}

	public KeyStoreInfo getKeystoreInfo() {
		return new KeyStoreInfo(new File(pathAC), StoreFormat.JKS, KSConfig.getInternalKeystores().getPassword().toCharArray());
	}

}
