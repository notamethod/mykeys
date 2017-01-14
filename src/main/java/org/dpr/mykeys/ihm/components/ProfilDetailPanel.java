package org.dpr.mykeys.ihm.components;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.bouncycastle.util.encoders.Hex;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.service.Profil;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class ProfilDetailPanel extends LabelValuePanel {

	private Profil info;

	public ProfilDetailPanel(Profil info) {
		this.info=info;
		getPanel();
	}
  
	public void getPanel(){
		//infosPanel = new LabelValuePanel();
		this.put(MyKeys.getMessage().getString("label.name"),
				JTextField.class, "", info.getName(), false);
		
		this.putEmptyLine();
	this.put(MyKeys.getMessage().getString("x509.pubkeysize"),
				JTextField.class, "keyLength",
				info.getValue("keyLength"), false);
	
	this.put(MyKeys.getMessage().getString("x509.pubkeyalgo"),
	JTextField.class, "algoPubKey", info.getValue("algoPubKey"), false);
	this.put(MyKeys.getMessage().getString("x509.sigalgo"),
	JTextField.class, "algoPubKey", info.getValue("algoSig"), false);
	this.putEmptyLine();
	this.put(MyKeys.getMessage().getString("certinfo.duration"),
	JTextField.class, "algoPubKey", info.getValue("Duration"), false);
	this.putEmptyLine();
	this.put(MyKeys.getMessage().getString("x509.subject.organisation"),
	JTextField.class, "algoPubKey", info.getValue("O"), false);
	this.put(MyKeys.getMessage().getString("x509.subject.location"),
	JTextField.class, "algoPubKey", info.getValue("L"), false);
	this.put(MyKeys.getMessage().getString("x509.subject.organisationUnit"),
	JTextField.class, "algoPubKey", info.getValue("OU"), false);

	
//	name=gg
//			C=FR
//			=3
//			PolicyCPS=
//			CrlDistrib=
//			PolicyNotice=
//			SR=
//		
//			algoSig=SHA256WithRSAEncryption
//			=RSA
//			emetteur=\ 
//			keyLength=2048
	

//		// this.put("Clé publique", JTextArea.class, "pubKey",
//		// X509Util.toHexString(info.getPublicKey().getEncoded()," ",
//		// false),false);
	
//		this.put(MyKeys.getMessage().getString("x509.sigalgo"),
//				JTextField.class, "algoSig", info.getAlgoSig(), false);
//		this.put(MyKeys.getMessage().getString("x509.startdate"),
//				JSpinnerDate.class, "notBefore", info.getNotBefore(), false);
//		this.put(MyKeys.getMessage().getString("x509.enddate"),
//				JSpinnerDate.class, "notAfter", info.getNotAfter(), false);
//		this.putEmptyLine();
//		this.put(MyKeys.getMessage().getString("x509.serial"),
//				JTextField.class, "numser", info.getCertificate()
//						.getSerialNumber().toString(), false);
//		this.put(MyKeys.getMessage().getString("x509.issuer"),
//				JTextField.class, "emetteur", info.getCertificate()
//						.getIssuerX500Principal().toString(), false);
//		if (info.getSubjectMap() != null) {
//			Iterator<String> iter = info.getSubjectMap().keySet().iterator();
//			while (iter.hasNext()) {
//				String key = iter.next();
//				String name;
//				try {
//					name = MyKeys.getMessage().getString(
//							X509Util.getMapNames().get(key));
//				} catch (Exception e) {
//					name = key;
//				}
//				String value = info.getSubjectMap().get(key);
//				if (value.startsWith("#")) {
//					value = new String(Hex.decode(value.substring(1,
//							value.length())));
//				}
//				this.put(name, JTextField.class, "", value, false);
//			}
//		}
//
//		String keyUsage = info.keyUsageToString();
//		if (keyUsage != null) {
//			this.put("Utilisation (key usage)", JLabel.class, "keyUsage",
//					keyUsage, false);
//		}
	}
}
