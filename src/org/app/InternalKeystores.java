/**
 * 
 */
package org.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
	    FileChannel inChannel = null;
	    FileChannel outChannel = null;
	        try {
	     inChannel =  new FileInputStream(InternalKeystores.class.getResource("/org/config/myKeysAc.jks").getFile()).getChannel();
            //InternalKeystores.class.getResource("/org/config/myKeysAc.jks").getFile()getChannel();
         outChannel = new
            FileOutputStream(f).getChannel();

            inChannel.transferTo(0, inChannel.size(),
                    outChannel);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
		if (inChannel != null) inChannel.close();
		if (outChannel != null) outChannel.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
        }
	    
//	    try {
//		InputStream is =  InternalKeystores.class.getResourceAsStream("/org/config/myKeysAc.jks");
//		
//		kt.createKeyStore(StoreFormat.JKS,
//		InternalKeystores.getACPath(), pwd.toCharArray());
//	    } catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }
	}
	kinfo =  new KeyStoreInfo(MyKeys.getMessage().getString("magasin.interne"), InternalKeystores.getACPath(), StoreModel.CASTORE, StoreFormat.JKS, StoreType.INTERNAL);
	kinfo.setPassword(InternalKeystores.password.toCharArray());
	kinfo.setOpen(true);
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
	kinfo.setOpen(true);
	return kinfo;   
    }    

}
