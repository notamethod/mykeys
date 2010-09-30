package org.ihm.panel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;

import org.app.CertificateInfo;
import org.app.InternalKeystores;
import org.app.KeyStoreInfo;
import org.app.KeyTools;
import org.app.ProviderUtil;
import org.app.X509Constants;
import org.app.KeyStoreInfo.StoreModel;
import org.ihm.JSpinnerDate;
import org.ihm.KeyStoreUI;
import org.ihm.MyKeys;
import org.ihm.TreeKeyStore;
import org.ihm.tools.JFieldsPanel;
import org.ihm.tools.LabelValuePanel;

public class CreateCertificatDialog extends JDialog implements ItemListener {

    // JTextField x509PrincipalC;
    // JTextField x509PrincipalO;
    // JTextField x509PrincipalL;
    // JTextField x509PrincipalST;
    // JTextField x509PrincipalE;
    // JTextField x509PrincipalCN;
    LabelValuePanel infosPanel;

    KeyStoreInfo ksInfo;

    CertificateInfo certInfo = new CertificateInfo();

    boolean isAC = false;

    public CreateCertificatDialog(JFrame owner, KeyStoreInfo ksInfo,
	    boolean modal) {

	super(owner, modal);
	this.ksInfo = ksInfo;
	if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
	    isAC = true;
	}
	init();
	this.pack();

    }

    private void init() {

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
	for (String algo : ProviderUtil.KeyPairGeneratorList) {
	    mapAlgoKey.put(algo, algo);
	}
	// fill with provider's available algorithms
	Map<String, String> mapAlgoSig = new LinkedHashMap<String, String>();
	for (String algo : ProviderUtil.SignatureList) {
	    mapAlgoSig.put(algo, algo);
	}

	getInfoPanel(isAC, mapKeyLength, mapAlgoKey, mapAlgoSig);
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

	JButton jbOK = new JButton("Valider");
	jbOK.addActionListener(dAction);
	jbOK.setActionCommand("OK");
	JButton jbCancel = new JButton("Annuler");
	jbCancel.addActionListener(dAction);
	jbCancel.setActionCommand("CANCEL");
	JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

	jp.add(panelInfo);
	jp.add(checkPanel);
	jp.add(jf4);

    }

    /**
     * .
     * 
     *<BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param mapKeyLength
     * @param mapAlgoKey
     * @param mapAlgoSig
     * 
     * @param isAC2
     * @return
     */
    private void getInfoPanel(boolean isAC, Map<String, String> mapKeyLength,
	    Map<String, String> mapAlgoKey, Map<String, String> mapAlgoSig) {
	infosPanel = new LabelValuePanel();
	Map<String, String> mapAC = null;
	try {
	    mapAC = TreeKeyStore.getListCerts(InternalKeystores.getACPath(),
		    "JKS", InternalKeystores.password);
	} catch (Exception e) {
	    // 
	}
	if (mapAC == null) {
	    mapAC = new HashMap<String, String>();
	}
	mapAC.put(" ", " ");
	infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");

	if (isAC) {
	    infosPanel.put("Alias (nom du certificat)", "alias",
		    "MyKeys Root CA");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Taille clé publique", JComboBox.class, "keyLength",
		    mapKeyLength, "2048 bits");
	    infosPanel.put("Algorithme clé publique", JComboBox.class,
		    "algoPubKey", mapAlgoKey, "RSA");
	    infosPanel.put("Algorithme de signature", JComboBox.class,
		    "algoSig", mapAlgoSig, "SHA256WithRSAEncryption");
	    // subject
	    infosPanel.putEmptyLine();
	    Calendar calendar = Calendar.getInstance();

	    infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"),
		    JSpinnerDate.class, "notBefore", calendar.getTime()
			    .toLocaleString(), true);
	    calendar.add(Calendar.YEAR, 5);
	    infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"),
		    JSpinnerDate.class, "notAfter", calendar.getTime()
			    .toLocaleString(), true);
	    infosPanel.putEmptyLine();
	    infosPanel.put("Nom (CN)", "CN", "MyKeys Root CA");
	    infosPanel.put("Pays (C)", "C", "FR");
	    infosPanel.put("Organisation (O)", "O", "MyKeys");
	    infosPanel.put("Section (OU)", "OU", "");
	    infosPanel.put("Localité (L)", "L", "");
	    infosPanel.put("Rue (ST)", "SR", "");
	    infosPanel.put("Email (E)", "E", "");

	    infosPanel.putEmptyLine();
	    infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib",
		    "");
	    infosPanel.put("Policy notice", "PolicyNotice", "");
	    infosPanel.put("Policy CPS", "PolicyCPS", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Mot de passe clé privée", JPasswordField.class,
		    "pwd1", InternalKeystores.password, false);
	    infosPanel.put("Confirmer le mot de passe", JPasswordField.class,
		    "pwd2", InternalKeystores.password, false);
	} else {
	    infosPanel.put("Alias (nom du certificat)", "alias", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Taille clé publique", JComboBox.class, "keyLength",
		    mapKeyLength, "2048 bits");
	    infosPanel.put("Algorithme clé publique", JComboBox.class,
		    "algoPubKey", mapAlgoKey, "RSA");
	    infosPanel.put("Algorithme de signature", JComboBox.class,
		    "algoSig", mapAlgoSig, "SHA256WithRSAEncryption");
	    // subject
	    infosPanel.putEmptyLine();
	    Calendar calendar = Calendar.getInstance();

	    infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"),
		    JSpinnerDate.class, "notBefore", calendar.getTime(), true);
	    calendar.add(Calendar.DAY_OF_YEAR, 60);
	    infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"),
		    JSpinnerDate.class, "notAfter", calendar.getTime(), true);
	    infosPanel.putEmptyLine();
	    infosPanel.put("Nom (CN)", "CN", "Nom");
	    infosPanel.put("Pays (C)", "C", "FR");
	    infosPanel.put("Organisation (O)", "O", "Orga");
	    infosPanel.put("Section (OU)", "OU", "Développement");
	    infosPanel.put("Localité (L)", "L", "Saint-Etienne");
	    infosPanel.put("Rue (ST)", "SR", "");
	    infosPanel.put("Email (E)", "E", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib",
		    "");
	    infosPanel.put("Policy notice", "PolicyNotice", "");
	    infosPanel.put("Policy CPS", "PolicyCPS", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Mot de passe clé privée", JPasswordField.class,
		    "pwd1", "", true);
	    infosPanel.put("Confirmer le mot de passe", JPasswordField.class,
		    "pwd2", "", true);
	}

    }

    public class DialogAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent event) {
	    String command = event.getActionCommand();
	    if (command.equals("CHOOSE_IN")) {

	    } else if (command.equals("OK")) {
		Map<String, Object> elements = infosPanel.getElements();
		Set<String> keys = elements.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
		    String key = it.next();
		}
		if (elements.get("alias") == null
			|| elements.get("pwd1") == null) {
		    KeyStoreUI.showError(CreateCertificatDialog.this,
			    "Champs obligatoires");
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
		KeyTools ktools = new KeyTools();
		char[] pkPassword = ((String) elements.get("pwd1"))
			.toCharArray();

		certInfo.setSubjectMap(elements);
		certInfo.setPassword(pkPassword);
		X509Certificate[] xCerts = null;

		try {
		    certInfo.setCrlDistributionURL(((String) elements
			    .get("CrlDistrib")));
		    certInfo.setPolicyNotice(((String) elements
			    .get("PolicyNotice")));
		    certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

		    xCerts = ktools.genererX509(certInfo, (String) elements
			    .get("emetteur"), isAC);

		    //		    
		    // else {
		    // if
		    // (StringUtils.isBlank((String)elements.get("emetteur"))){
		    // X509Certificate cert = ktools.genererX509(certInfo,
		    // false);
		    // xCerts = new X509Certificate[] { cert };
		    // }else{
		    // xCerts = ktools.genererX509(certInfo,
		    // (String)elements.get("emetteur"));
		    // }
		    // // X509Certificate xCert = ktools.genererX509(certInfo,
		    // // false);
		    // // FIXME
		    // // ktools.addCertToKeyStore(xCert, ksInfo, certInfo);
		    //
		    // }
		    ktools.addCertToKeyStoreNew(xCerts, ksInfo, certInfo);
		    CreateCertificatDialog.this.setVisible(false);

		} catch (Exception e) {
		    KeyStoreUI.showError(CreateCertificatDialog.this, e
			    .getMessage());
		    e.printStackTrace();
		}
	    } else if (command.equals("CANCEL")) {
		CreateCertificatDialog.this.setVisible(false);
	    }

	}

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

}
