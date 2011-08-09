package org.dpr.mykeys.ihm.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bouncycastle.util.encoders.Hex;
import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class DetailPanel extends JPanel {
    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;

    CertificateInfo certificatInfo;

    ActionPanel dAction;

    JPanel jp;
    JPanel jpExt;
    JTabbedPane jtab;

    JLabel titre = new JLabel();

    public DetailPanel() {
	super();
	init();

    }

    private void init() {
	dAction = new ActionPanel();

	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	this.setLayout(bl);

	titre = new JLabel("");
	jtab = new JTabbedPane();
	jp = new JPanel();
	jpExt = new JPanel();

	jp.setLayout(new FlowLayout(FlowLayout.LEADING));
	jpExt.setLayout(new FlowLayout(FlowLayout.LEADING));

	add(jtab);
	jtab.add(jp, "Informations principales");
	jtab.add(jpExt, "Informations étendues");
	// jp.add();
	// jp.add(new JLabel("Contenu du certificat"));
	jtab.setVisible(false);
    }

    public void updateInfoGen(CertificateInfo info) {
	jp.removeAll();
	jtab.revalidate();

	certificatInfo = info;
	titre.setText(MyKeys.getMessage().getString("detail.cert.title"));
	infosPanel = new LabelValuePanel();
	infosPanel.put(MyKeys.getMessage().getString("x509.alias"),
		JTextField.class, "", info.getAlias(), false);
	infosPanel.putEmptyLine();
	infosPanel.put(MyKeys.getMessage().getString("x509.pubkeysize"),
		JTextField.class, "keyLength",
		String.valueOf(info.getKeyLength()), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.pubkeyalgo"),
		JTextField.class, "algoPubKey", info.getAlgoPubKey(), false);
	// infosPanel.put("Clé publique", JTextArea.class, "pubKey",
	// X509Util.toHexString(info.getPublicKey().getEncoded()," ",
	// false),false);
	infosPanel.put(MyKeys.getMessage().getString("x509.sigalgo"),
		JTextField.class, "algoSig", info.getAlgoSig(), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.startdate"),
		JSpinnerDate.class, "notBefore", info.getNotBefore(), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.enddate"),
		JSpinnerDate.class, "notAfter", info.getNotAfter(), false);
	infosPanel.putEmptyLine();
	infosPanel.put(MyKeys.getMessage().getString("x509.serial"),
		JTextField.class, "numser", info.getCertificate()
			.getSerialNumber().toString(), false);
	infosPanel.put(MyKeys.getMessage().getString("x509.issuer"),
		JTextField.class, "emetteur", info.getCertificate()
			.getIssuerX500Principal().toString(), false);
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
		    value = new String(Hex.decode(value.substring(1,
			    value.length())));
		}
		infosPanel.put(name, JTextField.class, "", value, false);
	    }
	}

	String keyUsage = info.keyUsageToString();
	if (keyUsage != null) {
	    infosPanel.put("Utilisation (key usage)", JLabel.class, "keyUsage",
		    keyUsage, false);
	}

	jp.add(infosPanel);

    }

    public void updateInfoExt(CertificateInfo info) {
	jpExt.removeAll();
	jtab.revalidate();

	certificatInfo = info;
	titre.setText(MyKeys.getMessage().getString("detail.cert.title"));
	infosPanel = new LabelValuePanel();

	infosPanel.put(MyKeys.getMessage().getString("x509.alias"),
		JLabel.class, "", info.getAlias(), false);
	infosPanel.putEmptyLine();
	infosPanel.put("Chaine de certificats", JTextArea.class, "xCertChain",
		info.getCertChain(), false);
	infosPanel.putEmptyLine();

	infosPanel.put("Digest SHA1", JLabel.class, "signature",
		X509Util.toHexString(info.getDigestSHA1(), " ", false), false);
	infosPanel
		.put("Digest SHA256", JLabel.class, "signature", X509Util
			.toHexString(info.getDigestSHA256(), " ", false), false);
	infosPanel.putEmptyLine();
	infosPanel.put("Signature", JTextArea.class, "signature",
		X509Util.toHexString(info.getSignature(), " ", false), false);
	jpExt.add(infosPanel);

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

    public void updateInfo(CertificateInfo info) {
	// FIXME: repaint component ?
	if (info == null) {
	    jtab.setVisible(false);
	    return;
	}
	updateInfoGen(info);
	updateInfoExt(info);
	jtab.setVisible(true);
	jtab.revalidate();

    }

}
