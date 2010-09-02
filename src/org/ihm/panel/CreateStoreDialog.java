package org.ihm.panel;

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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.app.KSConfig;
import org.app.KeyTools;
import org.ihm.KeyStoreUI;
import org.ihm.tools.JFieldsPanel;
import org.ihm.tools.LabelValuePanel;

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
	infosPanel.put("Confirmer le Mot de passe", JPasswordField.class,
		"pwd2", "", true);

	infosPanel.putEmptyLine();

	// JLabel jl = new JLabel("Type de magasin");
	// ksType = new JComboBox(new String[] { "JKS", "PKCS12" });
	// JFieldsPanel jf0 = new JFieldsPanel(jl, ksType);
	// JLabel jl1 = new JLabel("Mot de passe");
	// pwd1 = new JPasswordField(12);
	// JFieldsPanel jf1 = new JFieldsPanel(jl1, pwd1);
	// JLabel jl2 = new JLabel("Confirmer le Mot de passe");
	// pwd2 = new JPasswordField(12);
	// JFieldsPanel jf2 = new JFieldsPanel(jl2, pwd2);

	JLabel jl4 = new JLabel("Emplacement");
	tfDirectory = new JTextField(20);
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
	jp.add(infosPanel);
	jp.add(jpDirectory);
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
		if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
		    String path = jfc.getSelectedFile().getAbsolutePath();
		    String typeKS = (String) infosPanel.getElements().get(
			    "typeKS");
		    if (!path.toUpperCase().endsWith("JKS")
			    && typeKS.equals("JKS")) {
			path = path + ".jks";
		    }
		    if (!path.toUpperCase().endsWith("p12")
			    && typeKS.equals("PKCS12")) {
			path = path + ".p12";
		    }

		    tfDirectory.setText(path);

		}

	    } else if (command.equals("OK")) {
		if (tfDirectory.getText().equals("")
			|| elements.get("pwd1") == null) {
		    KeyStoreUI.showError(CreateStoreDialog.this,
			    "Champs invalides");
		    return;
		}
		if (!elements.get("pwd1").equals(elements.get("pwd2"))) {
		    KeyStoreUI.showError(CreateStoreDialog.this,
			    "Mot de passe incorrect");
		    return;
		}
		KeyTools kt = new KeyTools();
		try {
		    kt.createKeyStore((String) elements.get("typeKS"),
			    tfDirectory.getText(), ((String) elements
				    .get("pwd1")).toCharArray());
		    KSConfig.getUserCfg().addProperty(
			    "magasin." + elements.get("typeKS"),
			    tfDirectory.getText());
		    ((KeyStoreUI) CreateStoreDialog.this.getParent())
			    .updateKeyStoreList();
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
}
