/**
 * 
 */
package org.dpr.mykeys.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyStoreInfo.StoreModel;
import org.dpr.mykeys.app.KeyStoreInfo.StoreType;
import org.dpr.mykeys.ihm.MyKeys;

public class InternalKeystores {

    public static final Log log = LogFactory.getLog(InternalKeystores.class);
    public static String password = "mKeys983178";
    private static String pathAC;
    private static String pathCert;

    public static String getACPath() {

	if (pathAC == null) {

	    pathAC = KSConfig.getCfgPath() + File.separator + "mykeysAc.jks";
	}
	return pathAC;
    }

    public static String getCertPath() {
	if (pathCert == null) {

	    pathCert = KSConfig.getCfgPath() + File.separator
		    + "mykeysCert.jks";
	}
	return pathCert;
    }

    public static KeyStoreInfo getACKeystore() {
	String path = InternalKeystores.getACPath();
	KeyTools kt = new KeyTools();
	String pwd = InternalKeystores.password;
	KeyStoreInfo kinfo = null;
	File f = new File(path);
	if (!f.exists()) {

	    try {

		InputStream is = (InternalKeystores.class
			.getResourceAsStream("/org.dpr.mykeys/config/mykeysAc.jks"));
		copyFile(is, f);
		// InternalKeystores.class.getResource("/org.dpr.mykeys/config/myKeysAc.jks").getFile()getChannel();

	    } catch (Exception e) {
		log.error(e);
	    }

	    // try {
	    // InputStream is =
	    // InternalKeystores.class.getResourceAsStream("/org.dpr.mykeys/config/myKeysAc.jks");
	    //
	    // kt.createKeyStore(StoreFormat.JKS,
	    // InternalKeystores.getACPath(), pwd.toCharArray());
	    // } catch (Exception e) {
	    // // TODO Auto-generated catch block
	    // e.printStackTrace();
	    // }
	}
	kinfo = new KeyStoreInfo(MyKeys.getMessage().getString(
		"magasin.interne"), InternalKeystores.getACPath(),
		StoreModel.CASTORE, StoreFormat.JKS, StoreType.INTERNAL);
	kinfo.setPassword(InternalKeystores.password.toCharArray());
	kinfo.setOpen(true);
	return kinfo;
    }

    public static KeyStoreInfo getCertKeystore() {
	String path = InternalKeystores.getCertPath();
	KeyTools kt = new KeyTools();
	String pwd = InternalKeystores.password;
	KeyStoreInfo kinfo = null;
	File f = new File(path);
	if (!f.exists()) {
	    try {
		kt.createKeyStore(StoreFormat.JKS,
			InternalKeystores.getCertPath(), pwd.toCharArray());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	kinfo = new KeyStoreInfo(MyKeys.getMessage().getString(
		"magasin.interne"), InternalKeystores.getCertPath(),
		StoreModel.CERTSTORE, StoreFormat.JKS, StoreType.INTERNAL);
	kinfo.setPassword(InternalKeystores.password.toCharArray());
	kinfo.setOpen(true);
	return kinfo;
    }

    public static void copyFile(InputStream is, File out) throws Exception {
	// InputStream fis = new FileInputStream(in);
	FileOutputStream fos = new FileOutputStream(out);
	try {
	    byte[] buf = new byte[1024];
	    int i = 0;
	    while ((i = is.read(buf)) != -1) {
		fos.write(buf, 0, i);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    if (is != null)
		is.close();
	    if (fos != null)
		fos.close();
	}
    }

}
