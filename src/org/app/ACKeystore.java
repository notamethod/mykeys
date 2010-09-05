/**
 * 
 */
package org.app;

import java.io.File;

import org.app.KeyStoreInfo.StoreFormat;
import org.app.KeyStoreInfo.StoreType;


public class ACKeystore {
    public static String password="mKeys983178";
    public static String path ;

    
    public static void setPath(String dirName) {
	path=dirName;
	//getKeystore();
	
    }
    
    public static void getKeystore() {
	if (path == null){
	    path=getACPath();
	    
	}
	
    }

    public static String getACPath() {
	String key = "store."+StoreType.CASTORE.toString()+"."+StoreFormat.JKS.toString();
	String pathTmp = KSConfig.getUserCfg().getString(key);
	if (pathTmp == null){
	    KSConfig.getUserCfg().addProperty(key, KSConfig.getCfgPath()+File.separator+"mykeysAc.jks");	
	    pathTmp = KSConfig.getCfgPath()+File.separator+"mykeysAc.jks";
	}
	return pathTmp;
    }    

}
