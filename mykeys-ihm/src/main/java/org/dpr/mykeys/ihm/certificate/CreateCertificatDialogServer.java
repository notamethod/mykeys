package org.dpr.mykeys.ihm.certificate;

import java.awt.event.ItemListener;

import javax.swing.JFrame;

import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.CancelCreationException;

class CreateCertificatDialogServer extends CreateCertificatDialog implements ItemListener {
	public CreateCertificatDialogServer(JFrame owner, KeyStoreValue ksInfo) throws CancelCreationException {

		super(owner, ksInfo, true);
		this.ksInfo = ksInfo;
		if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}
		init();
		this.pack();

	}
}
