package org.dpr.mykeys.ihm.certificate;

import org.dpr.mykeys.app.certificate.profile.ProfileServices;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.certificate.CertificateListPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeysProfileAction implements ActionListener {

	/**
	 * 
	 */
	private final CertificateListPanel listPanel;

    private ProfileServices profileService = new ProfileServices(KSConfig.getProfilsPath());


	public KeysProfileAction(CertificateListPanel listPanel, JComponent frameSource) {
		super();
		this.listPanel = listPanel;
        // this.ksInfo = ksInfo;
	}

    // private KeyStoreValue ksInfo;

	@Override
	public void actionPerformed(ActionEvent e) {
		final String action = e.getActionCommand();
		final Object composant = e.getSource();

		TypeAction typeAction = TypeAction.getTypeAction(action);
		JDialog cs;
		JFrame frame = null; 
		switch (typeAction) {
		
		case OPEN_STORE:
			if (this.listPanel.openStore(false, true)) {
			}
			try {
				this.listPanel.updateInfo(this.listPanel.ksInfo);
			} catch (ServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;

		case ADD_CERT:
			try {
				this.listPanel.addElement(this.listPanel.ksInfo, false, null);
			} catch (ServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			// addCertificate(ksInfo, false);
			break;
		
		case IMPORT_CERT:
			try {
				this.listPanel.importCertificate(this.listPanel.ksInfo, false);
			} catch (ServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;


		case DELETE_CERT:
			//BROKEN !
//			if (this.listPanel.listCerts != null && this.listPanel.listCerts.getSelected() != null
//                    && this.listPanel.listCerts.getSelected() instanceof CertificateTemplate) {
//                CertificateTemplate info = (CertificateTemplate) this.listPanel.listCerts.getSelected();
//                if (DialogUtil.askConfirmDialog(null, "Suppression du profil " + info.getName())) {
//					try {
//						profileService.delete( info);
//						this.listPanel.updateInfo(this.listPanel.ksInfo);
//					} catch (IOException | ServiceException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
			break;

		default:
			break;
		}
	}
}