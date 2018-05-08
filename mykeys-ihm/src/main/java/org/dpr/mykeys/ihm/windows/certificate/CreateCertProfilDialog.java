package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.MkSession;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.app.profile.ProfilException;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.swingtools.FrameModel;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.cert.X509Certificate;
import java.util.*;

public class CreateCertProfilDialog extends SuperCreate implements ItemListener, ActionListener {

    protected LabelValuePanel infosPanel;
    private LabelValuePanel panelInfoVisible;

    private CertificateValue certInfo = new CertificateValue();

    private Properties profile = null;
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

    public void setStrProf(String strProf) {
        this.strProf = strProf;
    }

    public void init() {

        String a = null;
        final LabelValuePanel panel = new LabelValuePanel();


        DialogAction dAction = new DialogAction();
        setTitle(Messages.getString("frame.create.certificateFromTemplate", strProf));
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        FrameModel model = new FrameModel();
        panelInfoVisible = new LabelValuePanel(model);

        //iterate over subject keys
        for (String attribute : SubjectUtil.getStandardList()) {
            String key = SubjectUtil.getLabels().get(attribute);
            panelInfoVisible.put(Messages.getString(key), JTextField.class, attribute, "", true);
        }

        panelInfoVisible.putEmptyLine();
        //iterate over other certificate keys
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
                DialogUtil.showError(this, e1.getMessage());
            }
        }

        this.pack();

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComboBox) {
            String strProf = (String) ((JComboBox) e.getSource()).getSelectedItem();
            System.out.println(strProf);
            ProfileServices pman = new ProfileServices(KSConfig.getProfilsPath());

            if (strProf != null && profile == null) {
                try {
                    profile = pman.loadProfile(strProf);
                    fillCert(profile);

                } catch (ProfilException e1) {
                    DialogUtil.showError(this, e1.getMessage());
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
        String keyUsage = myProfile.getProperty("&keyUsage");
        if (keyUsage != null) {
            panelInfoVisible.set("keyUsage", CertificateUtils.keyUsageToString(Integer.valueOf(keyUsage)));

            certInfo.setKeyUsage(CertificateUtils.keyUsageFromInt(Integer.valueOf(keyUsage)));
        }

        // System.out.println(CertificateUtils.keyUsageToString(certInfo.getKeyUsage()));
    }

    class DialogAction extends AbstractAction {

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
                    CertificateValue issuer = null;
                    if (null != certInfo.getIssuer() && !certInfo.getIssuer().trim().isEmpty())
                        issuer = kserv.findCertificateAndPrivateKeyByAlias(KSConfig.getInternalKeystores().getStoreAC(), certInfo.getIssuer());

                    CertificateValue newCertificate = cm.createCertificate(isAC, issuer);
                    if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL))
                        newCertificate.setPassword(MkSession.password);

                    kserv.addCertToKeyStore(ksInfo, newCertificate);
                    CreateCertProfilDialog.this.setVisible(false);

                } catch (Exception e) {

                    DialogUtil.showError(CreateCertProfilDialog.this, e.getMessage());
                    e.printStackTrace();
                }

            } else if (command.equals("CANCEL")) {
                CreateCertProfilDialog.this.setVisible(false);
            }

        }

        void fillCertInfo() {
            Map<String, Object> elements = panelInfoVisible.getElements();
            Set<String> keys = elements.keySet();
            for (String key : keys) {
            }

            // certInfo.setX509PrincipalMap(elements);
            HashMap<String, String> subjectMap = new HashMap<>();

            FillUtils.fillCertInfo(elements, certInfo);
            certInfo.setDuration(Integer.valueOf((String) elements.get("duration")));

            certInfo.setSubjectMap(elements);

            certInfo.setCrlDistributionURL(((String) elements.get("crlDistrib")));
            certInfo.setPolicyNotice(((String) elements.get("policyNotice")));
            certInfo.setPolicyCPS(((String) elements.get("policyCPS")));
            certInfo.setPolicyID(((String) elements.get("policyID")));
            certInfo.setIssuer(((String) elements.get("issuer")));

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

}
