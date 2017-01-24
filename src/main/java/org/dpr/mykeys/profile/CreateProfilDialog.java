package org.dpr.mykeys.profile;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.certificate.windows.CreateCertificatDialog;
import org.dpr.mykeys.certificate.windows.CreateCertificatDialog.DialogAction;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.ihm.windows.SuperCreate;
import org.dpr.mykeys.keystore.InternalKeystores;
import org.dpr.mykeys.keystore.KeyStoreInfo;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class CreateProfilDialog extends SuperCreate implements ItemListener {

	protected LabelValuePanel infosPanel;

	protected CertificateInfo certInfo = new CertificateInfo();

	public CreateProfilDialog(Frame owner, boolean modal) {

		super(owner, true);

		init();
		this.pack();

	}

	public CreateProfilDialog() {
		super();
	}

	public CreateProfilDialog(Frame owner) {
		super(owner);
	}

	public static void main(String[] args) {
		JFrame f = null;
		CreateCertificatDialog cr = new CreateCertificatDialog(f, null, false);
	}

	protected void init() {

		DialogAction dAction = new DialogAction();
		setTitle(MyKeys.getMessage().getString("frame.create.profil"));
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

		createInfoPanel(mapKeyLength, mapAlgoKey, mapAlgoSig);
		panelInfo.add(infosPanel);

		// JPanel panelInfo2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel checkPanel = new JPanel(new GridLayout(0, 3));

		Border border = BorderFactory.createTitledBorder("Key usage");
		checkPanel.setBorder(border);
		for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
			JCheckBox item = new JCheckBox(X509Constants.keyUsageLabel[i]);
			item.addItemListener(this);

			checkPanel.add(item);
		}

		  JPanel checkPanelExt = new JPanel(new GridLayout(0, 3));

	         border = BorderFactory.createTitledBorder("Extended Key usage");
	         checkPanelExt.setBorder(border);
	        for (int i = 0; i < X509Constants.ExtendedkeyUsageLabel.length; i++) {
	            JCheckBox item = new JCheckBox(X509Constants.ExtendedkeyUsageLabel[i]);
	            item.addItemListener(this);

	            checkPanelExt.add(item);
	        }
	        
		jp.add(panelInfo);
		jp.add(checkPanel);
		jp.add(checkPanelExt);
	
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
	private LabelValuePanel createInfoPanel(Map<String, String> mapKeyLength, Map<String, String> mapAlgoKey,
			Map<String, String> mapAlgoSig) {
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

			infosPanel.put(MyKeys.getMessage().getString("label.name"), "name", "");

			infosPanel.putEmptyLine();
			infosPanel.put(MyKeys.getMessage().getString("x509.issuer"), JComboBox.class, "emetteur", mapAC, "");
			infosPanel.put(MyKeys.getMessage().getString("x509.pubkeysize"), JComboBox.class, "keyLength", mapKeyLength,
					"2048 bits");
			infosPanel.put(MyKeys.getMessage().getString("x509.pubkeyalgo"), JComboBox.class, "algoPubKey", mapAlgoKey,
					"RSA");
			infosPanel.put(MyKeys.getMessage().getString("x509.sigalgo"), JComboBox.class, "algoSig", mapAlgoSig,
					"SHA256WithRSAEncryption");
			// subject
			infosPanel.putEmptyLine();
			Calendar calendar = Calendar.getInstance();

			infosPanel.put(MyKeys.getMessage().getString("certinfo.duration"), "Duration", "3");
			infosPanel.putEmptyLine();

			infosPanel.put(MyKeys.getMessage().getString("x509.subject.country"), "C", "FR");
			infosPanel.put(MyKeys.getMessage().getString("x509.subject.organisation"), "O", "Orga");
			infosPanel.put(MyKeys.getMessage().getString("x509.subject.organisationUnit"), "OU", "Développement");
			infosPanel.put(MyKeys.getMessage().getString("x509.subject.location"), "L", "Saint-Etienne");
			infosPanel.put(MyKeys.getMessage().getString("x509.subject.street"), "SR", "");

			infosPanel.putEmptyLine();
		//	infosPanel.put(MyKeys.getMessage().getString("x509.cdp"), JCheckBox.class, "CrlDistribObli", false, false); 
			infosPanel.put(MyKeys.getMessage().getString("x509.cdp"), "CrlDistrib", "");
			infosPanel.put(MyKeys.getMessage().getString("x509.policynotice"), "PolicyNotice", "");
			infosPanel.put(MyKeys.getMessage().getString("x509.policycps"), "PolicyCPS", "");

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
					ProfileManager pman = new ProfileManager();
					pman.saveToFile(infosPanel.getElements(), (String) infosPanel.getElements().get("name"), certInfo);
			
					CreateProfilDialog.this.setVisible(false);

				} catch (Exception e) {

					MykeysFrame.showError(CreateProfilDialog.this, e.getMessage());
					e.printStackTrace();
				}

			} else if (command.equals("CANCEL")) {
				CreateProfilDialog.this.setVisible(false);
			}

		}

		void fillCertInfo() {
			Map<String, Object> elements = infosPanel.getElements();
			Set<String> keys = elements.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
			}

			// certInfo.setX509PrincipalMap(elements);
			HashMap<String, String> subjectMap = new HashMap<String, String>();
			
			certInfo.setAlgoPubKey((String) elements.get("algoPubKey"));
			certInfo.setAlgoSig((String) elements.get("algoSig"));
			certInfo.setKeyLength((String) elements.get("keyLength"));
			certInfo.setDuration((Integer) elements.get("duration"));

			certInfo.setSubjectMap(elements);

			certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
			certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
			certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));
		}

	}



}