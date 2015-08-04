package org.dpr.mykeys.ihm.windows;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;

import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.CreateCertificatDialog.DialogAction;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class SuperCreate extends JDialog implements ItemListener {

	protected LabelValuePanel infosPanel;
	protected KeyStoreInfo ksInfo;
	protected CertificateInfo certInfo = new CertificateInfo();
	protected boolean isAC = false;

	public SuperCreate() {
		super();
	}

	public SuperCreate(Frame owner) {
		super(owner);
	}

	public SuperCreate(Dialog owner) {
		super(owner);
	}

	public SuperCreate(Window owner) {
		super(owner);
	}

	public SuperCreate(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public SuperCreate(Frame owner, String title) {
		super(owner, title);
	}

	public SuperCreate(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	public SuperCreate(Dialog owner, String title) {
		super(owner, title);
	}

	public SuperCreate(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	public SuperCreate(Window owner, String title) {
		super(owner, title);
	}

	public SuperCreate(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public SuperCreate(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public SuperCreate(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
	}

	public SuperCreate(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public SuperCreate(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public SuperCreate(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
	}

	protected void init() {

		DialogAction dAction = new DialogAction();
		if (isAC) {
			setTitle("Création d'une autorité de certification");
		} else {
			setTitle("Création de Certificat");
		}
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

		Map<String, String> mapKeyLength = new HashMap<String, String>();
		mapKeyLength.put("512 bits", "512");
		mapKeyLength.put("1024 bits", "1024");
		mapKeyLength.put("2048 bits", "2048");
		mapKeyLength.put("4096 bits", "4096");
		// fill with provider's available algorithms
		Map<String, String> mapAlgoKey = new LinkedHashMap<String, String>();
		for (String algo : ProviderUtil.getKeyPairGeneratorList()) {
			mapAlgoKey.put(algo, algo);
		}
		// fill with provider's available algorithms
		Map<String, String> mapAlgoSig = new LinkedHashMap<String, String>();
		for (String algo : ProviderUtil.SignatureList) {
			mapAlgoSig.put(algo, algo);
		}

		createInfoPanel(isAC, mapKeyLength, mapAlgoKey, mapAlgoSig);
		panelInfo.add(infosPanel);

		// JPanel panelInfo2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel checkPanel = new JPanel(new GridLayout(0, 3));

		Border border = BorderFactory.createTitledBorder("Key usage");
		checkPanel.setBorder(border);
		for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
			JCheckBox item = new JCheckBox(X509Constants.keyUsageLabel[i]);
			item.addItemListener(this);
			if ((isAC && i == 5) || (isAC && i == 6)) {
				item.setSelected(true);
			}
			checkPanel.add(item);
		}

		jp.add(panelInfo);
		jp.add(checkPanel);
		jp.add(new OkCancelPanel(dAction, FlowLayout.RIGHT));

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
	private LabelValuePanel createInfoPanel(boolean isAC, Map<String, String> mapKeyLength,
			Map<String, String> mapAlgoKey, Map<String, String> mapAlgoSig) {
		if (infosPanel == null) {
			infosPanel = new LabelValuePanel();
			Map<String, String> mapAC = null;
			try {
				mapAC = TreeKeyStorePanel
						.getListCerts(InternalKeystores.getACPath(), "JKS", InternalKeystores.password);
			} catch (Exception e) {
				//
			}
			if (mapAC == null) {
				mapAC = new HashMap<String, String>();
			}
			mapAC.put(" ", " ");
			infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");

			if (isAC) {
				infosPanel.put("Alias (nom du certificat)", "alias", "MyKeys Root CA");
				infosPanel.putEmptyLine();
				infosPanel.put("Taille clé publique", JComboBox.class, "keyLength", mapKeyLength, "2048 bits");
				infosPanel.put("Algorithme clé publique", JComboBox.class, "algoPubKey", mapAlgoKey, "RSA");
				infosPanel.put("Algorithme de signature", JComboBox.class, "algoSig", mapAlgoSig,
						"SHA256WithRSAEncryption");
				// subject
				infosPanel.putEmptyLine();
				Calendar calendar = Calendar.getInstance();

				infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"), JSpinnerDate.class, "notBefore",
						calendar.getTime(), true);
				calendar.add(Calendar.YEAR, 5);
				infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"), JSpinnerDate.class, "notAfter",
						calendar.getTime(), true);
				infosPanel.putEmptyLine();
				infosPanel.put("Nom (CN)", "CN", "MyKeys Root CA");
				infosPanel.put("Pays (C)", "C", "FR");
				infosPanel.put("Organisation (O)", "O", "MyKeys");
				infosPanel.put("Section (OU)", "OU", "");
				infosPanel.put("Localité (L)", "L", "");
				infosPanel.put("Rue (ST)", "SR", "");
				infosPanel.put("Email (E)", "E", "");

				infosPanel.putEmptyLine();
				infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib", "");
				infosPanel.put("Policy notice", "PolicyNotice", "");
				infosPanel.put("Policy CPS", "PolicyCPS", "");
				infosPanel.putEmptyLine();
				infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", InternalKeystores.password,
						false);
				infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2", InternalKeystores.password,
						false);
			} else {
				infosPanel.put("Alias (nom du certificat)", "alias", "");
				infosPanel.putEmptyLine();
				infosPanel.put("Taille clé publique", JComboBox.class, "keyLength", mapKeyLength, "2048 bits");
				infosPanel.put("Algorithme clé publique", JComboBox.class, "algoPubKey", mapAlgoKey, "RSA");
				infosPanel.put("Algorithme de signature", JComboBox.class, "algoSig", mapAlgoSig,
						"SHA256WithRSAEncryption");
				// subject
				infosPanel.putEmptyLine();
				Calendar calendar = Calendar.getInstance();

				infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"), JSpinnerDate.class, "notBefore",
						calendar.getTime(), true);
				calendar.add(Calendar.DAY_OF_YEAR, 60);
				infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"), JSpinnerDate.class, "notAfter",
						calendar.getTime(), true);
				infosPanel.putEmptyLine();
				infosPanel.put("Nom (CN)", "CN", "Nom");
				infosPanel.put("Pays (C)", "C", "FR");
				infosPanel.put("Organisation (O)", "O", "Orga");
				infosPanel.put("Section (OU)", "OU", "Développement");
				infosPanel.put("Localité (L)", "L", "Saint-Etienne");
				infosPanel.put("Rue (ST)", "SR", "");
				infosPanel.put("Email (E)", "E", "");
				infosPanel.putEmptyLine();
				infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib", "");
				infosPanel.put("Policy notice", "PolicyNotice", "");
				infosPanel.put("Policy CPS", "PolicyCPS", "");
				infosPanel.putEmptyLine();
				infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", "", true);
				infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2", "", true);
			}
		}
		return infosPanel;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		JCheckBox jc = (JCheckBox) source;
		String val = jc.getText();
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
				try {
					fillCertInfo();
					X509Certificate[] xCerts = null;
					KeyTools ktools = new KeyTools();
					xCerts = ktools.genererX509(certInfo, (String) infosPanel.getElements().get("emetteur"), isAC);

					ktools.addCertToKeyStoreNew(xCerts, ksInfo, certInfo);
					SuperCreate.this.setVisible(false);

				} catch (Exception e) {

					MykeysFrame.showError(SuperCreate.this, e.getMessage());
					e.printStackTrace();
				}

			} else if (command.equals("CANCEL")) {
				SuperCreate.this.setVisible(false);
			}

		}

		public void fillCertInfo() {
			Map<String, Object> elements = infosPanel.getElements();
			Set<String> keys = elements.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
			}
			if (elements.get("alias") == null || elements.get("pwd1") == null) {
				MykeysFrame.showError(SuperCreate.this, "Champs obligatoires");
				return;
			}

			// certInfo.setX509PrincipalMap(elements);
			HashMap<String, String> subjectMap = new HashMap<String, String>();
			certInfo.setAlgoPubKey((String) elements.get("algoPubKey"));
			certInfo.setAlgoSig((String) elements.get("algoSig"));
			certInfo.setKeyLength((String) elements.get("keyLength"));
			certInfo.setAlias((String) elements.get("alias"));
			certInfo.setNotBefore((Date) elements.get("notBefore"));
			certInfo.setNotAfter((Date) elements.get("notAfter"));

			char[] pkPassword = ((String) elements.get("pwd1")).toCharArray();

			certInfo.setSubjectMap(elements);
			certInfo.setPassword(pkPassword);

			certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
			certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
			certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

		}

	}

}