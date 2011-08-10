package org.dpr.mykeys.ihm.windows;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.CommonsActions;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.LabelValuePanel;

public class ExportCertificateDialog extends JDialog implements ItemListener {

	private JTextField tfDirectory;

	public static final String PEM_KEY_EXT = ".key";

	public static final String PEM_CERT_EXT = ".cer";

	LabelValuePanel infosPanel;

	CertificateInfo certInfo;

	KeyStoreInfo ksInfo;

	private boolean isExportCle = false;

	// Map<String, String> elements = new HashMap<String, String>();

	public ExportCertificateDialog(Frame owner, KeyStoreInfo ksInfo,
			CertificateInfo certInfo, boolean modal) {
		super(owner, modal);
		this.certInfo = certInfo;
		this.ksInfo = ksInfo;
		init();
		this.pack();
	}

	public void init() {
		DialogAction dAction = new DialogAction();
		setTitle("Exportation de certificat");
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		Map<String, String> mapType = new LinkedHashMap<String, String>();
		mapType.put("der", "der");
		mapType.put("pem", "pem/key");
		mapType.put("pkcs12", "PKCS12");

		// mapType.put("der", "der");

		infosPanel = new LabelValuePanel();

		infosPanel.put("Format", JComboBox.class, "formatCert", mapType);

		// infosPanel.put("Export de la clé privée", JCheckBox, "");
		if (certInfo.isContainsPrivateKey()) {
			JCheckBox jc = new JCheckBox("Exporter la clé privée");
			jc.addItemListener(this);

			jp.add(jc);
		}

		infosPanel.putEmptyLine();

		JLabel jl4 = new JLabel("Emplacement");
		tfDirectory = new JTextField(40);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File f = fsv.getDefaultDirectory();
		tfDirectory.setText(f.getAbsolutePath());
		JButton jbChoose = new JButton("...");
		jbChoose.addActionListener(dAction);
		jbChoose.setActionCommand("CHOOSE_IN");

		JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// jpDirectory.add(jl4);
		jpDirectory.add(tfDirectory);
		jpDirectory.add(jbChoose);
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

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			Map<String, Object> elements = infosPanel.getElements();
			String command = event.getActionCommand();
			if (command.equals("CHOOSE_IN")) {
				JFileChooser jfc = new JFileChooser();

				// jfc.addChoosableFileFilter(new KeyStoreFileFilter());

				// jPanel1.add(jfc);
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					tfDirectory
							.setText(jfc.getSelectedFile().getAbsolutePath());

				}

			} else if (command.equals("OK")) {
				if (tfDirectory.getText().equals("")) {
					MykeysFrame.showError(ExportCertificateDialog.this,
							"Champs invalides");
					return;
				}

				String path = tfDirectory.getText();
				// saisie mot de passe
				char[] password = null;
				if (isExportCle) {
					password = MykeysFrame.showPasswordDialog(null);
				}

				KeyTools kt = new KeyTools();
				String format = (String) infosPanel.getElements().get(
						"formatCert");

				if (format.equals("PKCS12")) {
					CommonsActions cact = new CommonsActions();
					cact.exportCert(StoreFormat.PKCS12, path, password,
							certInfo);
				} else if (format.equals("der")) {
					try {
						kt.exportDer(certInfo, path);
						if (isExportCle) {
							kt.exportPrivateKey(certInfo, ksInfo, password,
									tfDirectory.getText());
						}

					} catch (Exception e) {

						MykeysFrame.showError(ExportCertificateDialog.this,
								e.getLocalizedMessage());

					}

				} else {
					try {
						kt.exportPem(certInfo, path);
						// if (isExportCle) {
						// kt.exportPrivateKey(certInfo, ksInfo, password,
						// tfDirectory.getText());
						// }

					} catch (Exception e) {

						MykeysFrame.showError(ExportCertificateDialog.this,
								e.getLocalizedMessage());

					}
				}
				ExportCertificateDialog.this.setVisible(false);
				MykeysFrame.showInfo(ExportCertificateDialog.this,
						"Exportation terminée");
			} else if (command.equals("CANCEL")) {
				ExportCertificateDialog.this.setVisible(false);
			}

		}

	}

	/**
	 * @author Christophe Roger
	 * @date 8 mai 2009
	 */
	public class KeyStoreFileFilter extends FileFilter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File arg0) {
			// TODO Auto-generated method stub
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public void updateKeyStoreList() {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		JCheckBox jc = (JCheckBox) source;
		isExportCle = jc.isSelected();
		// for (int i=0; i<X509Constants.keyUsageLabel.length; i++){
		// if (val.equals(X509Constants.keyUsageLabel[i])){
		// certInfo.getKeyUsage()[i]=jc.isSelected();
		// return;
		// }
		// }

	}
}
