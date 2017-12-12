package org.dpr.mykeys.ihm.windows.certificate;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
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
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.keystore.InternalKeystores;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class SuperCreate extends JDialog implements ItemListener {

	public static final Log log = LogFactory.getLog(SuperCreate.class);
	protected LabelValuePanel infosPanel;

	protected CertificateType typeCer;
	protected LabelValuePanel durationPanel;
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

	public SuperCreate(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	protected void init() {

		String a = null;
		final JPanel panel = new JPanel();
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changEvent) {
				JRadioButton aButton = (JRadioButton) changEvent.getSource();

				if (aButton.isSelected()) {
					typeCer = CertificateType.valueOf(aButton.getName());

				}

			}
		};

		BoxLayout bls = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bls);
		final JRadioButton button1 = new JRadioButton("Client");
		button1.setSelected(true);
		button1.setName(CertificateType.STANDARD.toString());
		button1.addChangeListener(changeListener);
		final JRadioButton button2 = new JRadioButton("Serveur");
		button2.setName(CertificateType.SERVER.toString());
		final JRadioButton button3 = new JRadioButton("Signature de code");
		button3.setName(CertificateType.CODE_SIGNING.toString());
		button2.addChangeListener(changeListener);
		button3.addChangeListener(changeListener);
		ButtonGroup vanillaOrMod = new ButtonGroup();
		vanillaOrMod.add(button1);
		vanillaOrMod.add(button2);
		vanillaOrMod.add(button3);
		panel.add(button1);
		// panel.add(button2);
		// panel.add(button3);

		JOptionPane.showMessageDialog(this.getParent(), panel, "Type de certificat", 1, null);
		DialogAction dAction = new DialogAction();

		System.out.println(typeCer);
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
				mapAC = TreeKeyStorePanel.getListCerts(InternalKeystores.getACPath(), "JKS",
						InternalKeystores.password);
			} catch (Exception e) {
				//
			}
			if (mapAC == null) {
				mapAC = new HashMap<String, String>();
			}
			mapAC.put(" ", " ");
			infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");
			PanelUtils putil = new PanelUtils();
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

				infosPanel.put(KSConfig.getMessage().getString("certinfo.notBefore"), JSpinnerDate.class, "notBefore",
						calendar.getTime(), true);
				calendar.add(Calendar.YEAR, 5);
				infosPanel.put(KSConfig.getMessage().getString("certinfo.notAfter"), JSpinnerDate.class, "notAfter",
						calendar.getTime(), true);
				//infosPanel.put("aaa", JTextField.class, "notAfter", calendar.getTime(), true);
				infosPanel.put(KSConfig.getMessage().getString("certinfo.duration"), "duration", "3");
				infosPanel.putEmptyLine();
				putil.addSubjectToPanel(CertificateType.AC, infosPanel);

				infosPanel.putEmptyLine();
				infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib", "");
				infosPanel.put("Policy notice", "PolicyNotice", "");
				infosPanel.put("Policy CPS", "PolicyCPS", "");
				infosPanel.putEmptyLine();
				if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
					infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", InternalKeystores.password,
							false);
					infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2",
							InternalKeystores.password, false);
				}

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

				infosPanel.put(KSConfig.getMessage().getString("certinfo.duration"), "duration",
						getDefaultDuration(CertificateType.STANDARD));
				JCheckBox cbDuration = new JCheckBox(KSConfig.getMessage().getString("extended_mode"));

				cbDuration.setName("extendDuration");
				cbDuration.addItemListener(this);

				infosPanel.put("", cbDuration);
				infosPanel.put(getDurationPanel(3));
				infosPanel.putEmptyLine();
				putil.addSubjectToPanel(CertificateType.STANDARD, infosPanel);

				infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib", "");
				infosPanel.put("Policy notice", "PolicyNotice", "");
				infosPanel.put("Policy CPS", "PolicyCPS", "");
				infosPanel.putEmptyLine();
				if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
					infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", InternalKeystores.password,
							true);
					infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2",
							InternalKeystores.password, true);
				}
			}
		}
		return infosPanel;

	}

	private String getDefaultDuration(CertificateType standard) {
		// TODO Auto-generated method stub
		return "3";
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		JCheckBox jc = (JCheckBox) source;
		String name = jc.getName();
		if (name != null && name.equals("extendDuration")) {
			durationPanel.setVisible(jc.isSelected());
			// this.pack();

		}
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
					KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);

					certInfo.setIssuer((String) infosPanel.getElements().get("emetteur"));
					CertificateHelper certServ = new CertificateHelper(certInfo);

					xCerts = certServ.generateX509(isAC);

					kserv.addCertToKeyStore(xCerts, certInfo);
					SuperCreate.this.setVisible(false);

				} catch (Exception e) {

					log.error("certificate generation error",e);
					MykeysFrame.showError(SuperCreate.this, e.getMessage());

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
			if (elements.get("alias") == null
					|| (elements.get("pwd1") == null && !ksInfo.getStoreType().equals(StoreLocationType.INTERNAL))) {
				MykeysFrame.showError(SuperCreate.this, "Champs obligatoires");
				return;
			}

			// certInfo.setX509PrincipalMap(elements);
			HashMap<String, String> subjectMap = new HashMap<String, String>();
			FillUtils.fillCertInfo(elements, certInfo);
			certInfo.setAlias((String) elements.get("alias"));
			certInfo.setNotBefore((Date) elements.get("notBefore"));
			certInfo.setNotAfter((Date) elements.get("notAfter"));
			if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
				char[] pkPassword = ((String) elements.get("pwd1")).toCharArray();
				certInfo.setPassword(pkPassword);
			}
			certInfo.setSubjectMap(elements);

			certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
			certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
			certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

		}

	}

	public LabelValuePanel getDurationPanel(int duration) {
		Calendar calendar = Calendar.getInstance();
		if (durationPanel == null) {
			durationPanel = new LabelValuePanel();
			durationPanel.put(KSConfig.getMessage().getString("certinfo.notBefore"), JSpinnerDate.class, "notBefore",
					calendar.getTime(), true);
			calendar.add(Calendar.YEAR, duration);
			durationPanel.put(KSConfig.getMessage().getString("certinfo.notAfter"), JSpinnerDate.class, "notAfter",
					calendar.getTime(), true);
			durationPanel.setVisible(false);

		}
		return durationPanel;

	}

}