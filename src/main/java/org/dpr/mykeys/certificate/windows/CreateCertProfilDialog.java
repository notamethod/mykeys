package org.dpr.mykeys.certificate.windows;

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
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Enumeration;
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
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import javax.swing.border.Border;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.certificate.CertificateManager;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.model.FrameModel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.ihm.windows.SuperCreate;
import org.dpr.mykeys.keystore.InternalKeystores;
import org.dpr.mykeys.keystore.KeyStoreInfo;
import org.dpr.mykeys.keystore.StoreModel;
import org.dpr.mykeys.profile.ProfilException;
import org.dpr.mykeys.profile.ProfileManager;
import org.dpr.swingutils.LabelValuePanel;
import org.dpr.swingutils.SWComponent;

public class CreateCertProfilDialog  extends SuperCreate implements ItemListener, ActionListener
{

    protected LabelValuePanel infosPanel;
    protected LabelValuePanel panelInfoVisible;

    protected CertificateInfo certInfo = new CertificateInfo();
    
    private Properties profile = null;

    public CreateCertProfilDialog(Frame owner, KeyStoreInfo ksInfo, boolean modal)
    {

        super(owner, true);
		this.ksInfo = ksInfo;
		if (ksInfo==null){
			isAC = false;
		}else
		if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}

        init();
        this.pack();

    }

    public CreateCertProfilDialog()
    {
        super();
    }

    public CreateCertProfilDialog(Frame owner)
    {
        super(owner);
    }

    public static void main(String[] args)
    {
        JFrame f = null;
        CreateCertificatDialog cr = new CreateCertificatDialog(f, null, false);
    }

    protected void init()
    {

        DialogAction dAction = new DialogAction();
        setTitle(MyKeys.getMessage().getString("frame.create.profil"));
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        FrameModel model = new FrameModel();
         panelInfoVisible = new LabelValuePanel(model);
        Map<String, String> mapProfiles = new HashMap<String, String>();
        mapProfiles.put("", "");
    	ProfileManager pman = new ProfileManager();
	
        for (String profile : pman.getProfiles())
        {
            if (profile != null)
            {
                profile = profile.substring(0, profile.indexOf("."));
            }
            mapProfiles.put(profile, profile);
        }
        //		ComboBoxModel aModel = new DefaultComboBoxModel();
        //        JComboBox comboProf = new JComboBox(getProfiles());
        //        JPanel jpProfil = new JPanel();
        //        jpProfil.setLayout(new FlowLayout(FlowLayout.LEADING));
        //        jpProfil.add(new JLabel("Profil: "));
        //        jpProfil.add(comboProf);
        SWComponent swc = new SWComponent("Profil", JComboBox.class, "profil", mapProfiles, null, this);
        panelInfoVisible.put(swc);
        panelInfoVisible.put(MyKeys.getMessage().getString("label.name"), "name", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.country"), "C", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.organisation"), "O", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.organisationUnit"), "OU", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.location"), "L", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "E", "");

        panelInfoVisible.putEmptyLine();
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "algoPubKey", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "algoSig", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "keyLength", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "Duration", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "E", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "CrlDistrib", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "PolicyNotice", "");
        panelInfoVisible.putDisabled(MyKeys.getMessage().getString("x509.subject.email"), "PolicyCPS", "");

       

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelInfo.setMinimumSize(new Dimension(400, 100));

       
       

      

        // JPanel panelInfo2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel checkPanel = new JPanel(new GridLayout(0, 3));

        Border border = BorderFactory.createTitledBorder("Key usage");
        checkPanel.setBorder(border);
        for (int i = 0; i < X509Constants.keyUsageLabel.length; i++)
        {
            JCheckBox item = new JCheckBox(X509Constants.keyUsageLabel[i]);
            item.addItemListener(this);

            checkPanel.add(item);
        }

        JPanel checkPanelExt = new JPanel(new GridLayout(0, 3));

        border = BorderFactory.createTitledBorder("Extended Key usage");
        checkPanelExt.setBorder(border);
        for (int i = 0; i < X509Constants.ExtendedkeyUsageLabel.length; i++)
        {
            JCheckBox item = new JCheckBox(X509Constants.ExtendedkeyUsageLabel[i]);
            item.addItemListener(this);

            checkPanelExt.add(item);
        }
        //     jp.add(jpProfil);
        jp.add(panelInfoVisible);
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
            Map<String, String> mapAlgoSig)
    {
        if (infosPanel == null)
        {
            infosPanel = new LabelValuePanel();


            infosPanel.putEmptyLine();
            infosPanel.putDisabled(MyKeys.getMessage().getString("x509.issuer"), "emetteur", "");
          infosPanel.put(MyKeys.getMessage().getString("x509.pubkeysize"), "keyLength", 
                  "");
            infosPanel.put(MyKeys.getMessage().getString("x509.pubkeyalgo"),  "algoPubKey", "");
      
            infosPanel.put(MyKeys.getMessage().getString("x509.sigalgo"), "algoSig",
                    "");
            // subject
            infosPanel.putEmptyLine();
            Calendar calendar = Calendar.getInstance();

            infosPanel.putDisabled(MyKeys.getMessage().getString("certinfo.Duration"), "Duration", "");
            infosPanel.putEmptyLine();


            infosPanel.putEmptyLine();
            //	infosPanel.put(MyKeys.getMessage().getString("x509.cdp"), JCheckBox.class, "CrlDistribObli", false, false); 
            infosPanel.put(MyKeys.getMessage().getString("x509.cdp"), "CrlDistrib", "");
            infosPanel.put(MyKeys.getMessage().getString("x509.policynotice"), "PolicyNotice", "");
            infosPanel.put(MyKeys.getMessage().getString("x509.policycps"), "PolicyCPS", "");
            infosPanel.setVisible(false);

        }

        return infosPanel;

    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        Object source = e.getItemSelectable();
        JCheckBox jc = (JCheckBox) source;
        String val = jc.getText();
        System.out.println("changed 1!");
        for (int i = 0; i < X509Constants.keyUsageLabel.length; i++)
        {
            if (val.equals(X509Constants.keyUsageLabel[i]))
            {
                certInfo.getKeyUsage()[i] = jc.isSelected();
                return;
            }
        }

    }

    public class DialogAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent event)
        {
            String command = event.getActionCommand();
            if (command.equals("CHOOSE_IN"))
            {

            }
            else if (command.equals("OK"))
            {
                try
                {
                    fillCertInfo();
                    X509Certificate[] xCerts = null;

    			
    					CertificateManager cm = new CertificateManager(certInfo);
    					KeyTools ktools = new KeyTools();
    					xCerts = cm.generateX509();
    					//TODO manage ksinfo
    					ktools.addCertToKeyStoreNew(xCerts, ksInfo, certInfo);
                    CreateCertProfilDialog.this.setVisible(false);

                }
                catch (Exception e)
                {

                    MykeysFrame.showError(CreateCertProfilDialog.this, e.getMessage());
                    e.printStackTrace();
                }

            }
            else if (command.equals("CANCEL"))
            {
                CreateCertProfilDialog.this.setVisible(false);
            }

        }

        void fillCertInfo()
        {
            Map<String, Object> elements = panelInfoVisible.getElements();
            Set<String> keys = elements.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext())
            {
                String key = it.next();
            }

            // certInfo.setX509PrincipalMap(elements);
            HashMap<String, String> subjectMap = new HashMap<String, String>();

            FillUtils.fillCertInfo(elements, certInfo);
            certInfo.setDuration(Integer.valueOf( (String) elements.get("Duration")));

            certInfo.setSubjectMap(elements);

            certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
            certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
            certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));
        }

    }




    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JComboBox)
        {
            System.out.println("houlala");
            String strProf = (String) ((JComboBox) e.getSource()).getSelectedItem();

            ProfileManager pman = new ProfileManager();

            if (strProf != null && profile==null)
            {
                try
                {
                    profile=pman.loadProfile(strProf);
                    fillCert(profile);
                 
                }
                catch (ProfilException e1)
                {
                    MykeysFrame.showError(this, e1.getMessage());
                }
            }
        }

    }

    private void fillCert(Properties myProfile)
    {
    	 Enumeration<?> e = myProfile.propertyNames();

    	    while (e.hasMoreElements()) {
    	      String key = (String) e.nextElement();
    	      System.out.println(key + " -- " + myProfile.getProperty(key));
    	      panelInfoVisible.set( key, myProfile.getProperty(key));
    	    //  infosPanel.set( key, myProfile.getProperty(key));
    	    }
    	   
        
    }


}
