package org.dpr.mykeys.ihm.components;

import java.awt.Component;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Hex;
import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.ChildType;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.service.Profil;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class DetailPanel extends JPanel {
	
	public static final Log log = LogFactory.getLog(DetailPanel.class);
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

	public void updateInfoGen(ChildInfo info) {
		jp.removeAll();
		jtab.revalidate();
		jp.add( getDetailInstance(info));
		//certificatInfo = info;
		
		titre.setText(MyKeys.getMessage().getString("detail.cert.title"));
	 

		//jp.add(infosPanel);

	}

	private Component getDetailInstance(ChildInfo info) {
		if(info instanceof CertificateInfo){
			return new CertDetailPanel((CertificateInfo)info);
		}else{
			return new ProfilDetailPanel((Profil)info);
		}
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
				log.trace("OCSP");

			} else if (command.equals("OK")) {

				KeyTools kt = new KeyTools();

			} else if (command.equals("CANCEL")) {
				// ExportCertificateDialog.this.setVisible(false);
			}

		}

	}

	public void updateInfo(ChildInfo info) {
		// FIXME: repaint component ?
		if (info == null) {
			jtab.setVisible(false);
			return;
		}
		updateInfoGen(info);
		if (info.getChildType().equals(ChildType.CERTIFICATE)){
		updateInfoExt((CertificateInfo) info);
		}
		jtab.setVisible(true);
		jtab.revalidate();

	}

}
