package org.dpr.mykeys.ihm.windows;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.CreateCertificateFromProfile.DialogAction;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CreateCertificateFromProfile extends JDialog {

	protected LabelValuePanel infosPanel;
	protected KeyStoreInfo ksInfo;
	protected CertificateInfo certInfo = new CertificateInfo();
	protected boolean isAC = false;

	public CreateCertificateFromProfile() {
		super();
	}

	

	public CreateCertificateFromProfile(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public CreateCertificateFromProfile(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	

	protected void init() {

		DialogAction dAction = new DialogAction();

			setTitle("Création de Certificat");
		
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

		

	
		panelInfo.add(infosPanel);

	

		jp.add(panelInfo);
		
		jp.add(new OkCancelPanel(dAction, FlowLayout.RIGHT));

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
					CreateCertificateFromProfile.this.setVisible(false);

				} catch (Exception e) {

					MykeysFrame.showError(CreateCertificateFromProfile.this, e.getMessage());
					e.printStackTrace();
				}

			} else if (command.equals("CANCEL")) {
				CreateCertificateFromProfile.this.setVisible(false);
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
				MykeysFrame.showError(CreateCertificateFromProfile.this, "Champs obligatoires");
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
