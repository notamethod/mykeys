package org.dpr.mykeys.profile;

import static org.dpr.mykeys.utils.MessageUtils.getMessage;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.Border;

import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.ihm.windows.certificate.CreateCertificatDialog;
import org.dpr.mykeys.ihm.windows.certificate.FillUtils;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.utils.X509AttributesUtils;
import org.dpr.swingutils.LabelValuePanel;

public class CreateProfilDialog extends SuperCreate implements ItemListener {

	protected LabelValuePanel infosPanel;

	protected CertificateValue certInfo = new CertificateValue();

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
		setTitle(getMessage("frame.create.certificateTemplate"));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

        Map<String, String> mapKeyLength = X509AttributesUtils.getMapKeyLength();
		// fill with provider's available algorithms
        Map<String, String> mapAlgoKey = X509AttributesUtils.getMapKeyPairAlgorithms();

		// fill with provider's available algorithms
        Map<String, String> mapAlgoSig = X509AttributesUtils.getMapSignatureAlgorithms();


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
				mapAC = TreeKeyStorePanel.getListCerts(KSConfig.getInternalKeystores().getACPath(), "JKS",
						KSConfig.getInternalKeystores().getPassword());
			} catch (Exception e) {
				//
			}
			if (mapAC == null) {
				mapAC = new HashMap<String, String>();
			}
			mapAC.put(" ", " ");

            infosPanel.put(getMessage("template.name"), "name", "");

			infosPanel.put(getMessage("label.description"), JTextArea.class, "description", "", true);
			infosPanel.putEmptyLine();
			infosPanel.put(getMessage("x509.issuer"), JComboBox.class, "emetteur", mapAC, "");
			infosPanel.put(getMessage("x509.pubkeysize"), JComboBox.class, "keyLength", mapKeyLength,
					"2048 bits");
			infosPanel.put(getMessage("x509.pubkeyalgo"), JComboBox.class, "algoPubKey", mapAlgoKey,
					"RSA");
			infosPanel.put(getMessage("x509.sigalgo"), JComboBox.class, "algoSig", mapAlgoSig,
                    "SHA256WITHRSA");
			// subject
			infosPanel.putEmptyLine();
			Calendar calendar = Calendar.getInstance();

			infosPanel.put(getMessage("certinfo.duration"), "duration", "3");
			infosPanel.putEmptyLine();

			infosPanel.put(getMessage("x509.subject.country"), "C", "FR");
			infosPanel.put(getMessage("x509.subject.organisation"), "O", "Orga");
			infosPanel.put(getMessage("x509.subject.organisationUnit"), "OU", "Développement");
			infosPanel.put(getMessage("x509.subject.location"), "L", "Saint-Etienne");
			infosPanel.put(getMessage("x509.subject.street"), "SR", "");

			infosPanel.putEmptyLine();
		//	infosPanel.put(getMessage("x509.cdp"), JCheckBox.class, "CrlDistribObli", false, false); 
			infosPanel.put(getMessage("x509.cdp"), "CrlDistrib", "");
			infosPanel.put(getMessage("x509.policynotice"), "PolicyNotice", "");
			infosPanel.put(getMessage("x509.policycps"), "PolicyCPS", "");

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
					ProfileServices pman = new ProfileServices(KSConfig.getProfilsPath());
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
			
			FillUtils.fillCertInfo(elements, certInfo);
			//certInfo.setDuration((Integer) elements.get("duration"));

			certInfo.setSubjectMap(elements);

			certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
			certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
			certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));
		}

	}



}