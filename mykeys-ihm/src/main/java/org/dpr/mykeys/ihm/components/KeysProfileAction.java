package org.dpr.mykeys.ihm.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.utils.DialogUtil;

public class KeysProfileAction implements ActionListener {

	/**
	 * 
	 */
	private final ListPanel listPanel;

    private ProfileServices profileService = new ProfileServices(KSConfig.getProfilsPath());
	

	public KeysProfileAction(ListPanel listPanel, JComponent frameSource) {
		super();
		this.listPanel = listPanel;
		this.frameSource = frameSource;
		// this.ksInfo = ksInfo;
	}

	private JComponent frameSource;

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
				this.listPanel.addElement(this.listPanel.ksInfo, false);
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
			if (this.listPanel.listCerts != null && this.listPanel.listCerts.getSelectedValue() != null
                    && this.listPanel.listCerts.getSelectedValue() instanceof CertificateTemplate) {
                CertificateTemplate info = (CertificateTemplate) this.listPanel.listCerts.getSelectedValue();
                if (DialogUtil.askConfirmDialog(null, "Suppression du profil " + info.getName())) {
					try {
						profileService.delete( info);
						this.listPanel.updateInfo(this.listPanel.ksInfo);
					} catch (IOException | ServiceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			break;

		default:
			break;
		}
	}
}