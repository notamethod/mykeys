package org.ihm;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.app.CertificateInfo;
import org.app.KeyStoreInfo;
import org.app.KeyTools;
import org.app.KeyStoreInfo.StoreFormat;
import org.app.KeyStoreInfo.StoreModel;

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
	loadKS();
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
	String path = "mag1.jks";
	KeyStoreInfo ksInfo = new KeyStoreInfo("aa", path, StoreModel.CERTSTORE, StoreFormat.JKS);
	

	KeyTools kt = new KeyTools();
	KeyStore ks = null;

	ksInfo.setPassword("111".toCharArray());
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
}
