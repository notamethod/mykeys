package org.dpr.mykeys.ihm.windows.certificate;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.profile.ProfilException;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.model.FrameModel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingutils.LabelValuePanel;
import org.dpr.swingutils.SWComponent;

public class CreateCertProfilDialog extends SuperCreate implements ItemListener, ActionListener {

	protected LabelValuePanel infosPanel;
	protected LabelValuePanel panelInfoVisible;

	protected CertificateValue certInfo = new CertificateValue();

	private Properties profile = null;

    public void setStrProf(String strProf) {
        this.strProf = strProf;
    }

    private String strProf = null;

    public CreateCertProfilDialog(Frame owner, KeyStoreValue ksInfo, boolean modal) {

		super(owner, true);
		this.ksInfo = ksInfo;
		if (ksInfo == null) {
			isAC = false;
		} else if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}

	}

	public CreateCertProfilDialog() {
		super();
	}

	public CreateCertProfilDialog(Frame owner) {
		super(owner);
	}

	public static void main(String[] args) {
		JFrame f = null;
		CreateCertificatDialog cr = new CreateCertificatDialog(f, null, false);
	}

    public void init() {

        String a = null;
        final LabelValuePanel panel = new LabelValuePanel();




		DialogAction dAction = new DialogAction();
        setTitle(Messages.getString("frame.create.certificateTemplate", strProf));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		FrameModel model = new FrameModel();
		panelInfoVisible = new LabelValuePanel(model);

        for (String attribute : SubjectUtil.getStandardList()) {
            String key = SubjectUtil.getLabels().get(attribute);
            panelInfoVisible.put(Messages.getString(key), JTextField.class, attribute, "", true);
		}

        panelInfoVisible.putEmptyLine();

        //TODO: check v.startsWith("&")
        SubjectUtil.getCertificateLabels().forEach((k, v) ->
                panelInfoVisible.put(Messages.getString(v), JTextField.class, k, "", true));

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

		Border border = BorderFactory.createTitledBorder("Key usage");

		JPanel checkPanelExt = new JPanel(new GridLayout(0, 3));

		border = BorderFactory.createTitledBorder("Extended Key usage");
		checkPanelExt.setBorder(border);
		for (int i = 0; i < X509Constants.ExtendedkeyUsageLabel.length; i++) {
			JCheckBox item = new JCheckBox(X509Constants.ExtendedkeyUsageLabel[i]);
			item.addItemListener(this);

			checkPanelExt.add(item);
		}
		// jp.add(jpProfil);
		jp.add(panelInfoVisible);
		jp.add(panelInfo);

		jp.add(checkPanelExt);

		jp.add(new OkCancelPanel(dAction, FlowLayout.RIGHT));


        ProfileServices pman = new ProfileServices(KSConfig.getProfilsPath());
        if (strProf != null && profile == null) {
            try {
                profile = pman.loadProfile(strProf);
                fillCert(profile);

            } catch (ProfilException e1) {
                MykeysFrame.showError(this, e1.getMessage());
            }
        }

        this.pack();

	}

	/**
	 * .
	 * 
	 * 
	 * @param mapKeyLength
	 * @param mapAlgoKey
	 * @param mapAlgoSig
	 *
	 * @return
	 */
	private LabelValuePanel createInfoPanel(Map<String, String> mapKeyLength, Map<String, String> mapAlgoKey,
			Map<String, String> mapAlgoSig) {
		if (infosPanel == null) {
			infosPanel = new LabelValuePanel();

			infosPanel.putEmptyLine();
			infosPanel.putDisabled(Messages.getString("x509.issuer"), "emetteur", "");
			infosPanel.put(Messages.getString("x509.pubkeysize"), "keyLength", "");
			infosPanel.put(Messages.getString("x509.pubkeyalgo"), "algoPubKey", "");

			infosPanel.put(Messages.getString("x509.sigalgo"), "algoSig", "");
			// subject
			infosPanel.putEmptyLine();
			Calendar calendar = Calendar.getInstance();

			infosPanel.putDisabled(Messages.getString("certinfo.Duration"), "duration", "");
			infosPanel.putEmptyLine();

			infosPanel.putEmptyLine();
			// infosPanel.put(Messages.getString("x509.cdp"),
			// JCheckBox.class, "CrlDistribObli", false, false);
			infosPanel.put(Messages.getString("x509.cdp"), "CrlDistrib", "");
			infosPanel.put(Messages.getString("x509.policynotice"), "PolicyNotice", "");
			infosPanel.put(Messages.getString("x509.policycps"), "PolicyCPS", "");
			infosPanel.setVisible(false);

		}

		return infosPanel;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		JCheckBox jc = (JCheckBox) source;
		String val = jc.getText();
		System.out.println("changed 1!");
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

					CertificateHelper cm = new CertificateHelper(certInfo);
					KeyTools ktools = new KeyTools();
					KeyStoreHelper kserv = new KeyStoreHelper(ksInfo);
					//FIXME
					 CertificateValue newCertificate = cm.createCertificate(null);
					// TODO manage ksinfo
                    kserv.addCertToKeyStore(ksInfo, newCertificate);
					CreateCertProfilDialog.this.setVisible(false);

				} catch (Exception e) {

					MykeysFrame.showError(CreateCertProfilDialog.this, e.getMessage());
					e.printStackTrace();
				}

			} else if (command.equals("CANCEL")) {
				CreateCertProfilDialog.this.setVisible(false);
			}

		}

		void fillCertInfo() {
			Map<String, Object> elements = panelInfoVisible.getElements();
			Set<String> keys = elements.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
			}

			// certInfo.setX509PrincipalMap(elements);
			HashMap<String, String> subjectMap = new HashMap<String, String>();

			FillUtils.fillCertInfo(elements, certInfo);
			certInfo.setDuration(Integer.valueOf((String) elements.get("duration")));

			certInfo.setSubjectMap(elements);

			certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
			certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
			certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));
			// certInfo.setKeyUsage(keyUsage);
			boolean[] booloKu = new boolean[9];

			// KeyUsage ::= BIT STRING {
			// digitalSignature (0),
			// nonRepudiation (1),
			// keyEncipherment (2),
			// dataEncipherment (3),
			// keyAgreement (4),
			// keyCertSign (5),
			// cRLSign (6),
			// encipherOnly (7),
			// decipherOnly (8) }
			// certInfo.setKeyUsage(keyUsage);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			System.out.println("houlala");
			String strProf = (String) ((JComboBox) e.getSource()).getSelectedItem();
            System.out.println(strProf);
			ProfileServices pman = new ProfileServices(KSConfig.getProfilsPath());

			if (strProf != null && profile == null) {
				try {
					profile = pman.loadProfile(strProf);
					fillCert(profile);

				} catch (ProfilException e1) {
					MykeysFrame.showError(this, e1.getMessage());
				}
			}
		}

	}

	private void fillCert(Properties myProfile) {
		Enumeration<?> e = myProfile.propertyNames();
        System.out.println(e);
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			System.out.println(key + " -- " + myProfile.getProperty(key));
			panelInfoVisible.set(key, myProfile.getProperty(key));
			// infosPanel.set( key, myProfile.getProperty(key));
		}
		certInfo.setKeyUsage(CertificateUtils.keyUsageFromInt(Integer.valueOf(myProfile.getProperty("keyUSage"))));
		// System.out.println(CertificateUtils.keyUsageToString(certInfo.getKeyUsage()));
	}

}
