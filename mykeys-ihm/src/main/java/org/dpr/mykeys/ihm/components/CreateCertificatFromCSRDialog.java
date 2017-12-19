package org.dpr.mykeys.ihm.components;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.certificate.CreateCertificatDialog;
import org.dpr.mykeys.ihm.windows.certificate.PanelUtils;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.swingutils.JDropText;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class CreateCertificatFromCSRDialog extends SuperCreate implements ItemListener, ActionListener {

	protected LabelValuePanel infosPanel;
	protected LabelValuePanel panelInfoVisible;

	private JDropText tfDirectory;

	protected CertificateValue certInfo = new CertificateValue();

	public CreateCertificatFromCSRDialog(Frame owner, KeyStoreInfo ksInfo, boolean modal) {

		super(owner, true);
		this.ksInfo = ksInfo;
		if (ksInfo == null) {
			isAC = false;
		} else if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}

		init();
		this.pack();

	}

	public static void main(String[] args) {
		JFrame f = null;
		CreateCertificatDialog cr = new CreateCertificatDialog(f, null, false);
	}

	protected void init() {

		DialogAction dAction = new DialogAction();
		setTitle(KSConfig.getMessage().getString("frame.create.fromcsr"));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);
		// infosPanel = new LabelValuePanel();

		createInfoPanel();
		JLabel jl4 = new JLabel("Emplacement");
		tfDirectory = new JDropText();

		JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// jpDirectory.add(jl4);
		jpDirectory.add(tfDirectory);

		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		infosPanel.put("Emplacement", jpDirectory, true);
		// jp.add(jf0);
		// jp.add(jf1);
		// jp.add(jf2);
		jp.add(infosPanel);
		// jp.add(jpDirectory);
		jp.add(jf4);

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		JCheckBox jc = (JCheckBox) source;
		String val = jc.getText();
		System.out.println("changed 1!");
		for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
			if (val.equals(X509Constants.keyUsageLabel[i])) {
				certInfo.getKeyUsage()[i] = jc.isSelected();
				return;
			}
		}

	}

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("CHOOSE_IN")) {

			} else if (command.equals("OK")) {

				if (tfDirectory.getText().equals("")) {
					MykeysFrame.showError(CreateCertificatFromCSRDialog.this, "Champs invalides");
					return;
				}

				// certInfo.setIssuer((String) infosPanel.getElements().get("emetteur"));
				CertificateHelper cm = new CertificateHelper(certInfo);
				KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);
				try (InputStream is = new FileInputStream(tfDirectory.getText())) {
					CertificateValue issuer = kserv.findCertificateAndPrivateKeyByAlias(ksInfo, (String) infosPanel.getElements().get("emetteur"));
					CertificateValue xCerts = cm.generateFromCSR(is, issuer);
					KeyTools ktools = new KeyTools();
				
					
					// TODO manage ksinfo
					kserv.addCertToKeyStore(xCerts, KSConfig.getInternalKeystores().getPassword().toCharArray());
					CreateCertificatFromCSRDialog.this.setVisible(false);

				} catch (Exception e) {

					log.error("error generating certificate", e);
					MykeysFrame.showError(CreateCertificatFromCSRDialog.this, e.getMessage());

				}

			} else if (command.equals("CANCEL")) {
				CreateCertificatFromCSRDialog.this.setVisible(false);
			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	private LabelValuePanel createInfoPanel() {
		if (infosPanel == null) {
			infosPanel = new LabelValuePanel();
			Map<String, String> mapAC = null;
			try {
				mapAC = TreeKeyStorePanel.getListCerts(KSConfig.getInternalKeystores().getACPath(), "JKS",
						KSConfig.getInternalKeystores().getPassword());
			} catch (Exception e) {
				//
			}
			if (mapAC == null) {
				mapAC = new HashMap<String, String>();
			}
			mapAC.put(" ", " ");
			infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");

		}
		return infosPanel;

	}

}
