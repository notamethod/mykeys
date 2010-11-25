/**
 * 
 */
package org.dpr.mykeys.app;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyStoreInfo.StoreModel;

/**
 *<pre>
 * <b></b>.
 * 
 * <b>Description :</b>
 * 
 * 
 *</pre>
 * 
 * @author C. Roger<BR>
 * <BR>
 *         Créé le 16 nov. 2010 <BR>
 * <BR>
 * <BR>
 *         <i>Copyright : Tessi Informatique </i><BR>
 */
public class CommonsActions {

    public void exportCert(StoreFormat storeFormat, String path,
	    char[] password, CertificateInfo certInfo) {
	StoreModel storeModel = StoreModel.P12STORE;
	KeyStoreInfo ksInfo = new KeyStoreInfo("store", path, storeModel,
		storeFormat);
	ksInfo.setPassword(password);
	KeyTools kt = new KeyTools();

	try {
	    KeyStore ks = kt.createKeyStore(storeFormat, path, password);
	    kt
		    .addCertToKeyStoreNew(certInfo.getCertificate(), ksInfo,
			    certInfo);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

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
     * @param pwd
     * @param string
     * @param format2
     * @return
     * @throws Exception
     * 
     */
    public KeyStore createStore(StoreFormat format, String dir, char[] pwd)
	    throws Exception {

	KeyTools kt = new KeyTools();

	KeyStore ks = kt.createKeyStore(format, dir, pwd);
	KSConfig.getUserCfg().addProperty(
		"store." + StoreModel.CERTSTORE + "." + format.toString(), dir);
	return ks;

    }

    // public KeyStore addCert(CertificateInfo ci, KeyStore ks, char[] pwd)
    // throws Exception {
    // KeyStoreInfo ksinfo=new Keys
    // ksinfo.s
    //
    // KeyStore kstore = loadKeyStore(ksInfo.getPath(), ksInfo
    // .getStoreFormat(), ksInfo.getPassword());
    // saveCertChain(kstore, cert, certInfo);
    // saveKeyStore(kstore, ksInfo);
    //      
    //
    // }
}
