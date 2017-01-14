package org.dpr.mykeys.ihm.windows;

import java.awt.event.ItemListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.StoreModel;

public class CreateCertificatDialogServer  extends CreateCertificatDialog implements ItemListener{
	public CreateCertificatDialogServer(JFrame owner, KeyStoreInfo ksInfo) {

		super(owner, ksInfo, true);
		this.ksInfo = ksInfo;
		if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}
		init();
		this.pack();

	}
}
