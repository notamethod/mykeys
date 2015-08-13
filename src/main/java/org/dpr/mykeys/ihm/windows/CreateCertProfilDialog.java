package org.dpr.mykeys.ihm.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.swingutils.LabelValuePanel;
import org.dpr.swingutils.SWComponent;

public class CreateCertProfilDialog extends JDialog implements ItemListener, ActionListener {

	protected LabelValuePanel infosPanel;

	protected CertificateInfo certInfo = new CertificateInfo();

	public CreateCertProfilDialog(Frame owner, boolean modal) {

		super(owner, true);

		init();
		this.pack();

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

	protected void init() {

		DialogAction dAction = new DialogAction();
		setTitle(MyKeys.getMessage().getString("frame.create.profil"));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);
		
		
		LabelValuePanel panelInfo0 = new LabelValuePanel();
		Map<String, String> mapProfiles = new HashMap<String, String>();
		   mapProfiles.put("", "");
		for (String profile:getProfiles()){
		    if (profile!=null){
		        profile=profile.substring (0, profile.indexOf("."));
		    }
		    mapProfiles.put(profile, profile);
		}
//		ComboBoxModel aModel = new DefaultComboBoxModel();
//        JComboBox comboProf = new JComboBox(getProfiles());
//        JPanel jpProfil = new JPanel();
//        jpProfil.setLayout(new FlowLayout(FlowLayout.LEADING));
//        jpProfil.add(new JLabel("Profil: "));
//        jpProfil.add(comboProf);
       SWComponent swc =new SWComponent("Profil", JComboBox.class, "profil", mapProfiles, null, this);
		panelInfo0.put(swc);
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
	   //     jp.add(jpProfil);
	    jp.add(panelInfo0);
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
		System.out.println("changed !");
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
					saveToFile(infosPanel.getElements(), (String) infosPanel.getElements().get("name"));
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

	public void saveToFile(Map<String, Object> elements, String name) throws ManageProfilException, IOException {
		if (StringUtils.isBlank(name)) {
			throw new ManageProfilException("nom obligatoire");
		}
		File f = new File(KSConfig.getCfgPath(), name+".mkprof");
		if (f.exists()) {
			throw new ManageProfilException("Le profil existe déjà");
		}
		Properties p = new Properties();
		for (Map.Entry<String, Object> entry : elements.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			p.setProperty(entry.getKey(), (String)entry.getValue());
		}
		p.store(new FileOutputStream(f), "");

	}
	
	String[] getProfiles(){

	    File dir = new File(KSConfig.getCfgPath());
	    String[] list = dir.list(new FilenameFilter() {
	        @Override
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith(".mkprof");
	        }
	    });
	   return list;
	}

    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("houlala");
        
    }
	
	
	

}