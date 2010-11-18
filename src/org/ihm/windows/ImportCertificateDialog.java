package org.ihm.windows;

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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.app.KeyStoreInfo;
import org.app.KeyTools;
import org.ihm.KeyStoreUI;
import org.ihm.tools.JFieldsPanel;
import org.ihm.tools.LabelValuePanel;

public class ImportCertificateDialog extends JDialog {

    private JTextField tfDirectory;

    public static final String CERTTYPE_KEY_DER = "der";

    public static final String CERTTYPE_EXT_DER = ".der";

    public static final String CERTTYPE_KEY_P12 = "PKCS12";

    public static final String CERTTYPE_EXT_P12 = ".p12";

    LabelValuePanel infosPanel;

    KeyStoreInfo ksInfo;

    // Map<String, String> elements = new HashMap<String, String>();

    public ImportCertificateDialog(Frame owner, KeyStoreInfo ksInfo,
	    boolean modal) {
	super(owner, modal);
	this.ksInfo = ksInfo;
	init();
	this.pack();
    }

    public void init() {
	DialogAction dAction = new DialogAction();
	setTitle("Importation de certificat");
	JPanel jp = new JPanel();
	BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
	jp.setLayout(bl);
	setContentPane(jp);

	Map<String, String> mapType = new LinkedHashMap<String, String>();
	mapType.put("auto", "auto");
	mapType.put("der", "der");
	mapType.put("PKCS12", "PKCS12");

	infosPanel = new LabelValuePanel();

	infosPanel.put("Type de Certificat", JComboBox.class, "typeCert",
		mapType);
	infosPanel.putEmptyLine();
	infosPanel.put("Alias  à affecter", "alias", "");

	infosPanel.put("Mot de passe", JPasswordField.class, "pwd1", "", true);

	infosPanel.putEmptyLine();

	JLabel jl4 = new JLabel("Emplacement");
	tfDirectory = new JTextField(20);
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
		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    tfDirectory
			    .setText(jfc.getSelectedFile().getAbsolutePath());

		}

	    } else if (command.equals("OK")) {
		if (tfDirectory.getText().equals("")
			|| elements.get("pwd1") == null) {
		    KeyStoreUI.showError(ImportCertificateDialog.this,
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
			KeyStoreUI.showError(ImportCertificateDialog.this,
				"Renseignez un alias pour ce certificat");
		    }
		    kt.importX509Cert(alias, ksInfo, tfDirectory.getText(),
			    typeCert, ((String) elements.get("pwd1"))
				    .toCharArray());

		    // ((KeyStoreUI)ImportCertificateDialog.this.getParent()).updateKeyStoreList();
		    ImportCertificateDialog.this.setVisible(false);
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    KeyStoreUI.showError(ImportCertificateDialog.this, e
			    .getLocalizedMessage());
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
