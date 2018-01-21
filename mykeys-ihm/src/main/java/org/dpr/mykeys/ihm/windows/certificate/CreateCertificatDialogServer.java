package org.dpr.mykeys.ihm.windows.certificate;

import java.awt.event.ItemListener;

import javax.swing.JFrame;

import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;

class CreateCertificatDialogServer extends CreateCertificatDialog implements ItemListener {
    public CreateCertificatDialogServer(JFrame owner, KeyStoreValue ksInfo) {

		super(owner, ksInfo, true);
		this.ksInfo = ksInfo;
		if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}
		init();
		this.pack();

	}
}
