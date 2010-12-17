package org.dpr.mykeys.app.test;

import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.TimeStampManager;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyStoreInfo.StoreModel;

public class Test {

    final static Log log = LogFactory.getLog(Test.class);
    /**
     * .
     * 
     *<BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param args
     */
    public static void main(String[] args) {
	TimeStamp();
	// ImportCert();

    }

    private static void ImportCert() {
	KeyTools kt = new KeyTools();
	try {
	    String typeCert = null;

	    String alias = "aaa";
	    String path = "c:/dev/empty.jks";
	    String pathCert = "c:/dev/cpi.cer";
	    KeyStoreInfo ksInfo = new KeyStoreInfo("aa", path, StoreModel.CERTSTORE, StoreFormat.JKS);
	    kt.importX509Cert(alias, ksInfo, pathCert, typeCert, "111"
		    .toCharArray());

	} catch (Exception e) {

	    // e.printStackTrace();

	}

    }

    private static void loadKS() {
	//String path = "data/test01.jks";
	//KeyStoreInfo ksInfo = new KeyStoreInfo("aa", path, StoreModel.CERTSTORE, StoreFormat.JKS);
	String path = System.getProperty("user.dir");
	
	URL url = Test.class.getResource("data/test01.jks");
	
	try {
	    System.out.println(url.toURI().getPath());
	} catch (URISyntaxException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}
	
	//System.out.println(String. Test.class.getPackage().getName());
//	String name = Test.class.getPackage().getName().replace('.', File.separatorChar);
//	File f = new File(path, name+File.separator+"data");
//	f = new File(f.getAbsolutePath(), "test01.jks");
//	System.out.println(f.getAbsolutePath());
	KeyTools kt = new KeyTools();
	KeyStore ks = null;
	String fileName=null;
	try {
	    fileName=url.toURI(). getPath().substring(1);
	} catch (URISyntaxException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}
	fileName="C:/Documents and Settings/n096015/workspace_V2008/MyKeys0/bin/org/dpr/mykeys/app/test/data/test01.jks";
	KeyStoreInfo ksInfo = new KeyStoreInfo("aa", fileName, StoreModel.CERTSTORE, StoreFormat.JKS);
	ksInfo.setPassword("1234".toCharArray());
	try {
	    ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo
		    .getPassword());

	} catch (Exception e1) {

	    e1.printStackTrace();

	}

	Enumeration<String> enumKs = null;
	try {
	    enumKs = ks.aliases();
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (enumKs != null && enumKs.hasMoreElements()) {

	    while (enumKs.hasMoreElements()) {
		String alias = enumKs.nextElement();
		if (log.isDebugEnabled()) {
		    log.debug(alias);
		}
		//                 
		CertificateInfo certInfo = new CertificateInfo(alias);
		kt.fillCertInfo(ks, certInfo, alias);

	    }
	}

    }
    
    private static void TimeStamp() {
	Security.addProvider(new BouncyCastleProvider());	
	
	KeyTools kt = new KeyTools();
	KeyStore ks = null;
	String fileName=null;
	
	fileName="C:/Documents and Settings/n096015/workspace_V2008/MyKeys0/bin/org/dpr/mykeys/app/test/data/test01.jks";
	KeyStoreInfo ksInfo = new KeyStoreInfo("aa", fileName, StoreModel.CERTSTORE, StoreFormat.JKS);
	ksInfo.setPassword("1234".toCharArray());
	try {
	    ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo
		    .getPassword());

	} catch (Exception e1) {

	    e1.printStackTrace();

	}
	CertificateInfo certInfo = null;
	Enumeration<String> enumKs = null;
	try {
	    enumKs = ks.aliases();
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (enumKs != null && enumKs.hasMoreElements()) {

	    while (enumKs.hasMoreElements()) {
		String alias = enumKs.nextElement();
		if (log.isDebugEnabled()) {
		    log.debug(alias);
		}
		//                 
		certInfo = new CertificateInfo(alias);
		kt.fillCertInfo(ks, certInfo, alias);

	    }
	}
	try {
	   TimeStampToken tsp =  TimeStampManager.getTimeStampToken(4);
	   System.out.println(tsp);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }    
}
