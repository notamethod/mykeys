package org.dpr.mykeys.ihm.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
 
public class KeysAction implements ActionListener {

	/**
	 * 
	 */
	private final ListPanel listPanel;

	public KeysAction(ListPanel listPanel, JComponent frameSource) {
		super();
		this.listPanel = listPanel;
		this.frameSource = frameSource;
		// this.ksInfo = ksInfo;
	}

	private JComponent frameSource;

	// private KeyStoreInfo ksInfo;

	@Override
	public void actionPerformed(ActionEvent e) {
		final String action = e.getActionCommand();
		final Object composant = e.getSource();

		TypeAction typeAction = TypeAction.getTypeAction(action);
		JDialog cs;
		JFrame frame = null;
		switch (typeAction) {
		// case ADD_STORE:
		// frame = (JFrame) tree.getTopLevelAncestor();
		// cs = new CreateStoreDialog(frame, true);
		// cs.setLocationRelativeTo(frame);
		// cs.setVisible(true);
		// break;
		//
		// case IMPORT_STORE:
		// frame = (JFrame) tree.getTopLevelAncestor();
		// cs = new ImportStoreDialog(frame, true);
		// cs.setLocationRelativeTo(frame);
		// cs.setVisible(true);
		// break;

		// case EXPORT_CERT:
		// treeKeyStoreParent.exporterCertificate(node, false);
		// break;
		//
		case OPEN_STORE:
			if (this.listPanel.openStore(false, true)) {
			}
			try {
				this.listPanel.updateInfo(this.listPanel.ksInfo);
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		//
		// case CLOSE_STORE:
		// treeKeyStoreParent.closeStore(node, true);
		// break;

		case ADD_CERT:
			try {
				this.listPanel.addElement(this.listPanel.ksInfo, false);
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// addCertificate(ksInfo, false);
			break;
		case ADD_CERT_PROF: 
			try {
				this.listPanel.addCertFromPRofile(this.listPanel.ksInfo, false);
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// addCertificate(ksInfo, false);
			break;
		case ADD_CERT_FROMCSR: 
			try {
				this.listPanel.addCertFromCSR(this.listPanel.ksInfo, false);
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// addCertificate(ksInfo, false);
			break;
		case IMPORT_CERT:
			try {
				this.listPanel.importCertificate(this.listPanel.ksInfo, false);
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case EXPORT_CERT:
			if (this.listPanel.listCerts != null && this.listPanel.listCerts.getSelectedValue() != null
					&& this.listPanel.listCerts.getSelectedValue() instanceof CertificateInfo) {
				this.listPanel.exporterCertificate(this.listPanel.ksInfo, (CertificateInfo) this.listPanel.listCerts.getSelectedValue(), false);
			}
			break;
		case DELETE_CERT:
			if (this.listPanel.listCerts != null && this.listPanel.listCerts.getSelectedValue() != null
					&& this.listPanel.listCerts.getSelectedValue() instanceof CertificateInfo) {
				CertificateInfo certInfo = (CertificateInfo) this.listPanel.listCerts.getSelectedValue();
				if (MykeysFrame.askConfirmDialog(null, "Suppression du certificat " + certInfo.getName())) {
					try {
						this.listPanel.deleteCertificate(this.listPanel.ksInfo, certInfo);
					} catch (ServiceException e1) {
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