package org.dpr.mykeys.ihm.windows.certificate;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.swingutils.JDropText;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.LabelValuePanel;

public class ImportCertificateDialog extends JDialog {

	private JDropText tfDirectory;

	public static final String CERTTYPE_KEY_DER = "der";

	public static final String CERTTYPE_EXT_DER = ".der";

	public static final String CERTTYPE_KEY_P12 = "PKCS12";

	public static final String CERTTYPE_EXT_P12 = ".p12";

	private LabelValuePanel infosPanel;

	private KeyStoreValue ksInfo;

	// Map<String, String> elements = new HashMap<String, String>();

    public ImportCertificateDialog(Frame owner, KeyStoreValue ksInfo,
                                   boolean modal) {
		super(owner, modal);
		this.ksInfo = ksInfo;
		init();
		this.pack();
	}

	private void init() {
		DialogAction dAction = new DialogAction();
		setTitle(Messages.getString("x509.import.title"));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		Map<String, String> mapType = new LinkedHashMap<>();
		mapType.put("auto", "auto");
		mapType.put("der", "der");
		mapType.put("PKCS12", "PKCS12");

		infosPanel = new LabelValuePanel();

		infosPanel.put(Messages.getString("type.certificat"), JComboBox.class, "typeCert",
				mapType);
		infosPanel.putEmptyLine();
		infosPanel.put(Messages.getString("alias.to.assign"), "alias", "");

		infosPanel.put(Messages.getString("label.password"), JPasswordField.class, "pwd1", "", true);

		infosPanel.putEmptyLine();

		JLabel jl4 = new JLabel(Messages.getString("file.location"));
		tfDirectory = new JDropText();


		JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// jpDirectory.add(jl4);
		jpDirectory.add(tfDirectory);

        JButton jbOK = new JButton(Messages.getString("button.confirm"));
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		infosPanel.put(Messages.getString("file.location"), jpDirectory, true);
		// jp.add(jf0);
		// jp.add(jf1);
		// jp.add(jf2);
		jp.add(infosPanel);
		// jp.add(jpDirectory);
		jp.add(jf4);

	}

	class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			Map<String, Object> elements = infosPanel.getElements();
			String command = event.getActionCommand();
			if (command.equals("CHOOSE_IN")) {


			} else if (command.equals("OK")) {
				if (tfDirectory.getText().equals("")
						|| elements.get("pwd1") == null) {
					MykeysFrame.showError(ImportCertificateDialog.this,
							"Champs invalides");
					return;
				}

				KeyTools kt = new KeyTools();
				try {
					String typeCert = (String) elements.get("typeCert");
					if (elements.get("typeCert").equals("auto")) {
						typeCert = null;// findTypeKS(tfDirectory.getText());
					}
					String alias = (String) elements.get("alias");
					if (alias == null) {
						MykeysFrame.showError(ImportCertificateDialog.this,
								"Renseignez un alias pour ce certificat");
					}
					KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);
					//FIXME;CRR 
					System.out.println(typeCert);
					kserv.importX509Cert(alias, tfDirectory.getText(),
							StoreFormat.fromValue(typeCert),
							((String) elements.get("pwd1")).toCharArray());

					// ((MykeysFrame)ImportCertificateDialog.this.getParent()).updateKeyStoreList();
					ImportCertificateDialog.this.setVisible(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					MykeysFrame.showError(ImportCertificateDialog.this,
							e.getLocalizedMessage());
					// e.printStackTrace();

				}

			} else if (command.equals("CANCEL")) {
				ImportCertificateDialog.this.setVisible(false);
			}

		}

		private String findTypeCert(String filename) {
			// try {
			// String ext = filename.substring(filename.lastIndexOf('.')+1,
			// filename.length());
			// if (ext.equalsIgnoreCase(KSTYPE_EXT_JKS)){
			// return KSTYPE_KEY_JKS;
			// }
			// if (ext.equalsIgnoreCase(KSTYPE_EXT_PKCS12)){
			// return KSTYPE_KEY_PKCS12;
			// }
			// return null;
			// } catch (IndexOutOfBoundsException e) {
			// return null;
			// }
			return null;

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

	private class TextListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			updateFields();

		}

		public void insertUpdate(DocumentEvent e) {

			updateFields();

		}

		public void removeUpdate(DocumentEvent e) {
			updateFields();

		}

	}

	private void updateFields() {
		// TODO Auto-generated method stub

	}
}
