/**
 * 
 */
package org.app;

import java.io.File;

import org.app.KeyStoreInfo.StoreFormat;
import org.app.KeyStoreInfo.StoreModel;
import org.app.KeyStoreInfo.StoreType;
import org.ihm.MyKeys;


public class InternalKeystores {
    public static String password="mKeys983178";
    private static String pathAC;
    private static String pathCert;

    


    public static String getACPath() {

	if (pathAC == null){
	   
	    pathAC = KSConfig.getCfgPath()+File.separator+"mykeysAc.jks";
	}
	return pathAC;
    }    
    
    public static String getCertPath() {
//	String key = "store."+StoreModel.CASTORE.toString()+"."+StoreFormat.JKS.toString();
//	String pathTmp = KSConfig.getUserCfg().getString(key);
	if (pathCert == null){

	    pathCert = KSConfig.getCfgPath()+File.separator+"mykeysCert.jks";
	}
	return pathCert;
    }      
    
    public static KeyStoreInfo getACKeystore() {
	String path = InternalKeystores.getACPath();
	KeyTools kt = new KeyTools();
	String pwd = InternalKeystores.password;
	KeyStoreInfo kinfo = null;
	File f= new File(path);
	if (!f.exists()){
	    try {
		kt.createKeyStore(StoreFormat.JKS,
		InternalKeystores.getACPath(), pwd.toCharArray());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} 
	kinfo =  new KeyStoreInfo(MyKeys.getMessage().getString("magasin.interne"), InternalKeystores.getACPath(), StoreModel.CASTORE, StoreFormat.JKS, StoreType.INTERNAL);
	kinfo.setPassword(InternalKeystores.password.toCharArray());
	return kinfo;   
    }
    
    public static KeyStoreInfo getCertKeystore() {
	String path = InternalKeystores.getCertPath();
	KeyTools kt = new KeyTools();
	String pwd = InternalKeystores.password;
	KeyStoreInfo kinfo = null;
	File f= new File(path);
	if (!f.exists()){
	    try {
		kt.createKeyStore(StoreFormat.JKS,
		InternalKeystores.getCertPath(), pwd.toCharArray());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	kinfo =  new KeyStoreInfo(MyKeys.getMessage().getString("magasin.interne"), InternalKeystores.getCertPath(), StoreModel.CERTSTORE, StoreFormat.JKS, StoreType.INTERNAL);
	kinfo.setPassword(InternalKeystores.password.toCharArray());
	return kinfo;   
    }    

}
