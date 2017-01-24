package org.dpr.mykeys.ihm.windows;

import java.security.KeyStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.test.ListAlgorithms;

public class KeyStoreService {
	public static final Log log = LogFactory.getLog(KeyStoreService.class);
	KeyStoreInfo ksInfo;

	public KeyStoreService(KeyStoreInfo ksInfo) {
		this.ksInfo = ksInfo;
	}

	public void open() {
		// TODO Auto-generated method stub
		
	}

	public void changePassword(char[] newPwd) throws TamperedWithException, KeyToolsException {
		KeyTools kt =new KeyTools();
		KeyStore ks = null;
		try {
			 ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
					ksInfo.getPassword());
		} catch (KeyToolsException e) {
			throw new TamperedWithException(e);
		}
		ksInfo.setPassword(newPwd);
		//TODO:l create save file
		kt.saveKeyStore(ks, ksInfo);
	}

}
