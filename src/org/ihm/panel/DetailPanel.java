package org.ihm.panel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.app.CertificateInfo;
import org.app.KeyTools;
import org.app.X509Util;
import org.bouncycastle.util.encoders.Hex;
import org.ihm.MyKeys;
import org.ihm.tools.LabelValuePanel;

public class DetailPanel extends JPanel {
    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;

    CertificateInfo certificatInfo;

    ActionPanel dAction;

    JPanel jp;

    JLabel titre = new JLabel();

    public DetailPanel() {
	super();
	init();

    }

    private void init() {
	dAction = new ActionPanel();
	//setBackground(new Color(125,0,0));
	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	this.setLayout(bl);
	
	titre = new JLabel("Gestion des certificats");
	// add(titre);
	jp = new JPanel();

	jp.setLayout(new FlowLayout(FlowLayout.LEADING));

	add(jp);
	// jp.add();
	// jp.add(new JLabel("Contenu du certificat"));
	jp.setVisible(true);
    }

    public void updateInfo(CertificateInfo info) {
	jp.removeAll();
	jp.revalidate();
	if (info == null) {
	    return;
	}
	certificatInfo = info;
	titre.setText(MyKeys.getMessage().getString("detail.cert.title"));
	infosPanel = new LabelValuePanel();
	infosPanel.put(MyKeys.getMessage().getString("x509.alias"), JLabel.class, "", info
		.getAlias(), false);
	infosPanel.putEmptyLine();
	infosPanel.put(MyKeys.getMessage().getString("x509.pubkeysize"), JLabel.class, "keyLength", String
		.valueOf(info.getKeyLength()), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.pubkeyalgo"), JLabel.class, "algoPubKey",
		info.getAlgoPubKey(), false);
	// infosPanel.put("Clé publique", JTextArea.class, "pubKey",
	// X509Util.toHexString(info.getPublicKey().getEncoded()," ",
	// false),false);
	infosPanel.put(MyKeys.getMessage().getString("x509.sigalgo"), JLabel.class, "algoSig", info
		.getAlgoSig(), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.startdate"), JLabel.class, "notBefore", info
		.getNotBefore().toString(), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.enddate"), JLabel.class, "notAfter", info
		.getNotAfter().toString(), false);
	infosPanel.putEmptyLine();
	infosPanel.put(MyKeys.getMessage().getString("x509.serial"), JLabel.class, "numser",info.getCertificate().getSerialNumber().toString(), false);	
	infosPanel.put(MyKeys.getMessage().getString("x509.issuer"), JLabel.class, "emetteur",info.getCertificate().getIssuerX500Principal().toString(), false);	
	if (info.getSubjectMap() != null) {
	    Iterator<String> iter = info.getSubjectMap().keySet().iterator();
	    while (iter.hasNext()) {
		String key = iter.next();
		String name;
		try {
		    name = MyKeys.getMessage().getString(
			    X509Util.getMapNames().get(key));
		} catch (Exception e) {
		    name = key;
		}
		String value = info.getSubjectMap().get(key);
		if (value.startsWith("#")) {
		    value = new String(Hex.decode(value.substring(1, value
			    .length())));
		}
		infosPanel.put(name, JLabel.class, "", value, false);
	    }
	}

	String keyUsage = info.keyUsageToString();
	if (keyUsage != null) {
	    infosPanel.put("Utilisation (key usage)", JLabel.class, "keyUsage",
		    keyUsage, false);
	}

	infosPanel.putEmptyLine();
	infosPanel.put("Chaine de certificats", JTextArea.class, "xCertChain",
		info.getCertChain(), false);
	infosPanel.putEmptyLine();

	infosPanel.put("Digest SHA1", JLabel.class, "signature", X509Util
		.toHexString(info.getDigestSHA1(), " ", false), false);
	infosPanel.put("Digest SHA256", JLabel.class, "signature", X509Util
		.toHexString(info.getDigestSHA256(), " ", false), false);
	infosPanel.putEmptyLine();
	infosPanel.put("Signature", JTextArea.class, "signature", X509Util
		.toHexString(info.getSignature(), " ", false), false);
	jp.add(infosPanel);

	//JButton jbCheckOcsp = new JButton("Check OCSP");
//	jbCheckOcsp.addActionListener(dAction);
//	jbCheckOcsp.setActionCommand("CHECK_OCSP");
//	jbCheckOcsp.setVisible(true);
//	jp.add(jbCheckOcsp);
	jp.setVisible(true);
	jp.revalidate();
    }

    public class ActionPanel extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent event) {
	    String command = event.getActionCommand();
	    if (command.equals("CHECK_OCSP")) {
		System.out.println("OCSP");

	    } else if (command.equals("OK")) {

		KeyTools kt = new KeyTools();

	    } else if (command.equals("CANCEL")) {
		// ExportCertificateDialog.this.setVisible(false);
	    }

	}

    }

}
