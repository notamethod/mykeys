package org.dpr.mykeys.ihm.windows;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyStoreInfo.StoreModel;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.ihm.MyKeys;

import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.LabelValuePanel;

public class CreateStoreDialog extends JDialog {

	private JTextField tfDirectory;

	// JComboBox ksType;
	// JPasswordField pwd1;
	// JPasswordField pwd2;
	LabelValuePanel infosPanel;

	// Map<String, String> elements = new HashMap<String, String>();

	public CreateStoreDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
		this.pack();
	}

	public void init() {
		DialogAction dAction = new DialogAction();
		setTitle("Création de magasin");
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		Map<String, String> mapType = new HashMap<String, String>();
		mapType.put("Java Key store", "JKS");
		mapType.put("PKCS12", "PKCS12");

		infosPanel = new LabelValuePanel();

		infosPanel.put("Type de magasin", JComboBox.class, "typeKS", mapType);
		infosPanel.putEmptyLine();
		infosPanel.put("Mot de passe", JPasswordField.class, "pwd1", "", true);
		infosPanel.put("Confirmer le Mot de passe", JPasswordField.class, "pwd2", "", true);

		infosPanel.putEmptyLine();

		JLabel jl4 = new JLabel(MyKeys.getMessage().getString("label.filename"));
		tfDirectory = new JTextField(30);

		JButton jbChoose = new JButton("...");
		jbChoose.addActionListener(dAction);
		jbChoose.setActionCommand("CHOOSE_IN");

		JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
		jpDirectory.add(jl4);
		jpDirectory.add(tfDirectory);
		jpDirectory.add(jbChoose);
		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		// jp.add(jf0);
		// jp.add(jf1);
		// jp.add(jf2);
		jp.add(jpDirectory);
		jp.add(infosPanel);

		jp.add(jf4);

	}

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			Map<String, Object> elements = infosPanel.getElements();
			String command = event.getActionCommand();
			if (command.equals("CHOOSE_IN")) {

				JFileChooser jfc = new JFileChooser(getDataDir());
				// jfc.addChoosableFileFilter(new KeyStoreFileFilter());

				// jPanel1.add(jfc);
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = jfc.getSelectedFile().getAbsolutePath();
					String typeKS = (String) infosPanel.getElements().get("typeKS");

					tfDirectory.setText(path);

				}

			} else if (command.equals("OK")) {
				if (tfDirectory.getText().equals("") || elements.get("pwd1") == null) {
					MykeysFrame.showError(CreateStoreDialog.this, "Champs invalides");
					return;
				}
				if (!elements.get("pwd1").equals(elements.get("pwd2"))) {
					MykeysFrame.showError(CreateStoreDialog.this, "Mot de passe incorrect");
					return;
				}

				String typeKS = (String) infosPanel.getElements().get("typeKS");
				String dir = correctExtension(tfDirectory.getText(), typeKS);
				Path p = Paths.get(dir);
				
				if (!p.isAbsolute()) {
					
					dir = getDataDir() + File.separator + correctExtension(dir, typeKS);;
				}
				KeyTools kt = new KeyTools();
				try {
					StoreFormat format = StoreFormat.valueOf((String) elements.get("typeKS"));
					createKeyStore(format, dir, ((String) elements.get("pwd1")).toCharArray());
					kt.createKeyStore(format, dir, ((String) elements.get("pwd1")).toCharArray());
					KSConfig.getUserCfg().addProperty("store." + StoreModel.CERTSTORE + "." + format.toString(), dir);
					((MykeysFrame) CreateStoreDialog.this.getParent()).updateKeyStoreList();
					CreateStoreDialog.this.setVisible(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (command.equals("CANCEL")) {
				CreateStoreDialog.this.setVisible(false);
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

	public String correctExtension(String name, String typeKS) {
		if (!name.toUpperCase().endsWith("JKS") && typeKS.equals("JKS")) {
			name = name + ".jks";
		}
		if (!name.toUpperCase().endsWith("p12") && typeKS.equals("PKCS12")) {
			name = name + ".p12";
		}
		return name;
	}

	public String getDataDir() {

		String dir = KSConfig.getUserCfg().getString("data.dir");
		if (dir == null) {
			// if (OSInfo.getOs().equals(OS.UNIX)) {
			// dir = KSConfig.getCfgPath();
			// } else {
			// document dir in windows
			File f = FileSystemView.getFileSystemView().getDefaultDirectory();
			File data = new File(f, MyKeys.getMessage().getString("default.datadir"));
			data.mkdirs();
			dir = data.getAbsolutePath();
			// }
		}
		return dir;
	}

	public void createKeyStore(StoreFormat format, String text, char[] charArray) {
		// TODO Auto-generated method stub

	}
}
