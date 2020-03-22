package org.dpr.mykeys.ihm.keystore;

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
import javax.swing.filechooser.FileFilter;

import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeystoreUtils;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.JDropText;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;

public class ImportStoreDialog extends JDialog {

	private JDropText tfDirectory;

	private LabelValuePanel infosPanel;

	// Map<String, String> elements = new HashMap<String, String>();

	public ImportStoreDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
		this.pack();
	}

	private void init() {
		DialogAction dAction = new DialogAction();
        setTitle(Messages.getString("keystore.import.title"));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		Map<String, String> mapType = new LinkedHashMap<>();
		mapType.put("auto", "auto");
		mapType.put("Java Key store", "JKS");
		mapType.put("PKCS12", "PKCS12");

		infosPanel = new LabelValuePanel();

        infosPanel.put(Messages.getString("keystore.type.label"), JComboBox.class, "typeKS", mapType);
		infosPanel.putEmptyLine();
        infosPanel.put(Messages.getString("label.password"), JPasswordField.class, "pwd1", "", true);

		infosPanel.putEmptyLine();

		// JLabel jl = new JLabel("Type de magasin");
		// ksType = new JComboBox(new String[] { "JKS", "PKCS12" });
		// JFieldsPanel jf0 = new JFieldsPanel(jl, ksType);
        // JLabel jl1 = new JLabel(Messages.getString("label.password"));
		// pwd1 = new JPasswordField(12);
		// JFieldsPanel jf1 = new JFieldsPanel(jl1, pwd1);
		// JLabel jl2 = new JLabel("Confirmer le Mot de passe");
		// pwd2 = new JPasswordField(12);
		// JFieldsPanel jf2 = new JFieldsPanel(jl2, pwd2);

        JLabel jl4 = new JLabel(Messages.getString("file.location"));
		tfDirectory = new JDropText();
		
//		jbChoose.addActionListener(dAction);
//		jbChoose.setActionCommand("CHOOSE_IN");

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
            switch (command) {
                case "CHOOSE_IN":
//				JFileChooser jfc = new JFileChooser();
//				// jfc.addChoosableFileFilter(new KeyStoreFileFilter());
//
//				// jPanel1.add(jfc);
//				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//					tfDirectory
//							.setText(jfc.getSelectedFile().getAbsolutePath());
//
//				}

                    break;
                case "OK":
                    if (tfDirectory.getText().equals("")
                            || elements.get("pwd1") == null) {
                        DialogUtil.showError(ImportStoreDialog.this,
                                "Champs invalides");
                        return;
                    }

                    KeyStoreHelper kserv = new KeyStoreHelper(null);
                    try {
                        StoreFormat format = KeystoreUtils.findKeystoreType(tfDirectory.getText());
                        char[] pdin = ((String) elements.get("pwd1")).toCharArray();
                        kserv.importStore(tfDirectory.getText(), format,
                                pdin.length == 0 ? null : pdin);
                        // KSConfig.getUserCfg().addProperty("magasin." + typeKS,
                        // tfDirectory.getText());
                        KSConfig.getUserCfg().addProperty(
                                "store." + StoreModel.CERTSTORE + "."
                                        + format.toString(), tfDirectory.getText());
                        ((MykeysFrame) ImportStoreDialog.this.getParent())
                                .updateKeyStoreList();
                        ImportStoreDialog.this.setVisible(false);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        DialogUtil.showError(ImportStoreDialog.this,
                                e.getLocalizedMessage());
                        // e.printStackTrace();

                    }

                    break;
                case "CANCEL":
                    ImportStoreDialog.this.setVisible(false);
                    break;
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
}
