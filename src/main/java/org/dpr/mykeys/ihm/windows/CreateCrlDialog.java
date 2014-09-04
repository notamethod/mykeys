package org.dpr.mykeys.ihm.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.CommonsActions;
import org.dpr.mykeys.app.CrlInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class CreateCrlDialog extends JDialog {
	public static final Log log = LogFactory.getLog(ListPanel.class);
	// JTextField x509PrincipalC;
	// JTextField x509PrincipalO;
	// JTextField x509PrincipalL;
	// JTextField x509PrincipalST;
	// JTextField x509PrincipalE;
	// JTextField x509PrincipalCN;

	JTextField tfDirectoryOut;
	LabelValuePanel infosPanel;

	// CertificateInfo certInfo = new CertificateInfo();

	boolean isAC = false;
	public JFileChooser jfc;

	public CreateCrlDialog(JFrame owner, boolean modal) {

		super(owner, modal);

		init();
		this.pack();

	}

	private void init() {

		DialogAction dAction = new DialogAction();
		// FIXME:

		setTitle("Création d'une liste de révocation");

		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

		// fill with provider's available algorithms
		Map<String, String> mapAlgoSig = new LinkedHashMap<String, String>();
		for (String algo : ProviderUtil.SignatureList) {
			mapAlgoSig.put(algo, algo);
		}

		getInfoPanel(mapAlgoSig);
		panelInfo.add(infosPanel);

		FileSystemView fsv = FileSystemView.getFileSystemView();
		File f = fsv.getDefaultDirectory();

		JLabel jl5 = new JLabel("Fichier en sortie");

		tfDirectoryOut = new JTextField(40);
		tfDirectoryOut.setText(f.getAbsolutePath());
		JButton jbChoose2 = new JButton("...");
		jbChoose2.addActionListener(dAction);
		jbChoose2.setActionCommand("CHOOSE_OUT");

		JPanel jpDirectory2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		jpDirectory2.add(jl5);
		jpDirectory2.add(tfDirectoryOut);
		jpDirectory2.add(jbChoose2);

		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		jp.add(panelInfo);
		jp.add(jpDirectory2);
		jp.add(jf4);

	}

	/**
	 * .
	 * 
	 * 
	 * @param mapKeyLength
	 * @param mapAlgoKey
	 * @param mapAlgoSig
	 * 
	 * @param isAC2
	 * @return
	 */
	private void getInfoPanel(Map<String, String> mapAlgoSig) {
		infosPanel = new LabelValuePanel();
		Map<String, String> mapAC = null;
		try {
			mapAC = TreeKeyStorePanel.getListCerts(
					InternalKeystores.getACPath(), "JKS",
					InternalKeystores.password);
		} catch (Exception e) {
			//
		}
		if (mapAC == null) {
			mapAC = new HashMap<String, String>();
		}
		mapAC.put(" ", " ");
		infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");

		infosPanel.put("Alias (nom du certificat)", "alias", "");
		infosPanel.putEmptyLine();

		infosPanel.put("Algorithme de signature", JComboBox.class, "algoSig",
				mapAlgoSig, "SHA256WithRSAEncryption");
		// subject
		infosPanel.putEmptyLine();
		Calendar calendar = Calendar.getInstance();

		infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"),
				JSpinnerDate.class, "notBefore", calendar.getTime(), true);
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"),
				JSpinnerDate.class, "notAfter", calendar.getTime(), true);
		infosPanel.putEmptyLine();

		infosPanel.putEmptyLine();

	}

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("CHOOSE_OUT")) {
				infosPanel.set("alias", "totototo");
				// jfc = new JFileChooser();
				// if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				// {
				// String path = jfc.getSelectedFile().getAbsolutePath();
				//
				// if (!path.toUpperCase().endsWith("CRL")) {
				//
				// path = path + ".crl";
				// }
				//
				// tfDirectoryOut.setText(path);
				// }

			} else if (command.equals("OK")) {

				Map<String, Object> elements = infosPanel.getElements();
				log.trace(elements.get("alias"));
				Set<String> keys = elements.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
				}
				if (elements.get("alias") == null) {
					MykeysFrame.showError(CreateCrlDialog.this,
							"alias obligatoire");
					return;
				}
				CrlInfo crlInfo = new CrlInfo();
				;

				// certInfo.setX509PrincipalMap(elements);
				HashMap<String, String> subjectMap = new HashMap<String, String>();
				crlInfo.setName((String) elements.get("alias"));
				crlInfo.setThisUpdate((Date) elements.get("notBefore"));
				crlInfo.setNextUpdate((Date) elements.get("notAfter"));
				crlInfo.setPath(tfDirectoryOut.getText());

				KeyTools ktools = new KeyTools();
				// CertificateInfo certSign =
				// ktools.getCertificateACByAlias((String)
				// elements.get("emetteur"));

				CommonsActions cActions = new CommonsActions();

				try {

					cActions.generateCrl((String) elements.get("emetteur"),
							crlInfo);
					// FIXME: add crl to tree
					// ktools.generateCrl(certSign, crlInfo, privateKey);
					CreateCrlDialog.this.setVisible(false);

				} catch (Exception e) {
					MykeysFrame.showError(CreateCrlDialog.this, e.getMessage());
					e.printStackTrace();
				}
			} else if (command.equals("CANCEL")) {
				CreateCrlDialog.this.setVisible(false);
			}

		}
	}

}
