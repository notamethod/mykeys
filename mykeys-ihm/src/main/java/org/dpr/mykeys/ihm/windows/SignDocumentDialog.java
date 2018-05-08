package org.dpr.mykeys.ihm.windows;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.CommonServices;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;

class SignDocumentDialog extends JDialog {

	private JTextField tfDirectoryIn;
	private JTextField tfDirectoryOut;

	// JComboBox ksType;
	// JPasswordField pwd1;
	// JPasswordField pwd2;
	private LabelValuePanel infosPanel;

	// Map<String, String> elements = new HashMap<String, String>();

	public SignDocumentDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
		this.pack();
	}

	private void init() {
		DialogAction dAction = new DialogAction();
		setTitle("Signature de document");
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		Map<String, String> mapType = new HashMap<>();
		mapType.put("CMS/PKCS#7", "CMS");

		Map<String, String> mapCerts = null;
		try {
			mapCerts = TreeKeyStorePanel.getListCerts(
					KSConfig.getInternalKeystores().getCertPath(), "JKS",
					KSConfig.getInternalKeystores().getPassword());
		} catch (Exception e) {
			//
		}
		if (mapCerts == null) {
			mapCerts = new HashMap<>();
		}
		mapCerts.put(" ", " ");

		infosPanel = new LabelValuePanel();

		infosPanel.put("Format de signature", JComboBox.class, "typeSig",
				mapType);
		infosPanel.putEmptyLine();
		infosPanel.put("Certificat signataire", JComboBox.class, "certificat",
				mapCerts, "");
		infosPanel.putEmptyLine();

		JLabel jl4 = new JLabel("Fichier à signer");
		JLabel jl5 = new JLabel("Fichier en sortie");

		FileSystemView fsv = FileSystemView.getFileSystemView();
		File f = fsv.getDefaultDirectory();
		tfDirectoryIn = new JTextField(40);
		tfDirectoryIn.setText(f.getAbsolutePath());
		JButton jbChoose = new JButton("...");
		jbChoose.addActionListener(dAction);
		jbChoose.setActionCommand("CHOOSE_IN");

		tfDirectoryOut = new JTextField(40);
		tfDirectoryOut.setText(f.getAbsolutePath());
		JButton jbChoose2 = new JButton("...");
		jbChoose2.addActionListener(dAction);
		jbChoose2.setActionCommand("CHOOSE_OUT");

		JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
		jpDirectory.add(jl4);
		jpDirectory.add(tfDirectoryIn);
		jpDirectory.add(jbChoose);

		JPanel jpDirectory2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		jpDirectory2.add(jl5);
		jpDirectory2.add(tfDirectoryOut);
		jpDirectory2.add(jbChoose2);

		JButton jbOK = new JButton(Messages.getString("button.confirm"));
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton(Messages.getString("button.cancel"));
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		// jp.add(jf0);
		// jp.add(jf1);
		// jp.add(jf2);
		jp.add(infosPanel);
		jp.add(jpDirectory);
		jp.add(jpDirectory2);
		jp.add(jf4);

	}

	class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			Map<String, Object> elements = infosPanel.getElements();
			String command = event.getActionCommand();
			JFileChooser jfc = null;
			if (command.equals("CHOOSE_IN")) {
				jfc = new JFileChooser();

				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					tfDirectoryIn.setText(jfc.getSelectedFile()
							.getAbsolutePath());

				}
			} else if (command.equals("CHOOSE_OUT")) {
				jfc = new JFileChooser();
				// jfc.addChoosableFileFilter(new KeyStoreFileFilter());

				// jPanel1.add(jfc);
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = jfc.getSelectedFile().getAbsolutePath();
					String typeSig = (String) infosPanel.getElements().get(
							"typeSig");
					if (!path.toUpperCase().endsWith("PK7")
							&& typeSig.equals("CMS")) {
						path = path + ".pk7";
					}

					tfDirectoryOut.setText(path);

				}

			} else if (command.equals("OK")) {
				if (tfDirectoryIn.getText().equals("")
						|| elements.get("pwd1") == null) {
                    DialogUtil.showError(SignDocumentDialog.this,
							"Champs invalides");
					return;
				}
				if (!elements.get("pwd1").equals(elements.get("pwd2"))) {
                    DialogUtil.showError(SignDocumentDialog.this,
							"Mot de passe incorrect");
					return;
				}

                CommonServices cact = new CommonServices();
				// cact.signData(ksInfo, password, certInfo, false);
				// FIXME
				// cact.exportCert(StoreFormat.PKCS12, path, password,
				// certInfo);

			} else if (command.equals("CANCEL")) {
				SignDocumentDialog.this.setVisible(false);
			}
		}

	}

	/**
	 * @author Christophe Roger
	 * @date 8 mai 2009
	 */
	private class KeyStoreFileFilter extends FileFilter {

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

	public void createKeyStore(StoreFormat format, String text, char[] charArray) {
		// TODO Auto-generated method stub

	}
}
