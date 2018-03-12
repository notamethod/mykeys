package org.dpr.mykeys.ihm.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.windows.IhmException;
import org.dpr.mykeys.utils.DialogUtil;

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

    // private KeyStoreValue ksInfo;

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

			try {
				this.listPanel.updateInfo(this.listPanel.ksInfo);
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
            } catch (IhmException e1) {
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
					&& this.listPanel.listCerts.getSelectedValue() instanceof CertificateValue) {
				this.listPanel.exporterCertificate(this.listPanel.ksInfo, (CertificateValue) this.listPanel.listCerts.getSelectedValue(), false);
			}
			break;
		case DELETE_CERT:
			if (this.listPanel.listCerts != null && this.listPanel.listCerts.getSelectedValue() != null
					&& this.listPanel.listCerts.getSelectedValue() instanceof CertificateValue) {
				CertificateValue certInfo = (CertificateValue) this.listPanel.listCerts.getSelectedValue();
                if (DialogUtil.askConfirmDialog(null, Messages.getString("delete.certificat.confirm", certInfo.getName()))) {
					try {
						this.listPanel.deleteCertificate(this.listPanel.ksInfo, certInfo);
					} catch (ServiceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			break;
            case CREATE_CRL:
                if (this.listPanel.listCerts != null && this.listPanel.listCerts.getSelectedValue() != null
                        && this.listPanel.listCerts.getSelectedValue() instanceof CertificateValue) {
                    CertificateValue certInfo = (CertificateValue) this.listPanel.listCerts.getSelectedValue();
                    try {
                        this.listPanel.createCrl(this.listPanel.ksInfo, (CertificateValue) this.listPanel.listCerts.getSelectedValue(), false);
                    } catch (ServiceException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
		default:
			break;
		}
	}


}