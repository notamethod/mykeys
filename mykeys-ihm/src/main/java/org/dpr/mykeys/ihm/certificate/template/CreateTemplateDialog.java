package org.dpr.mykeys.ihm.certificate.template;

import org.dpr.mykeys.app.KeyUsages;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.utils.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.ihm.certificate.FillUtils;
import org.dpr.mykeys.ihm.certificate.SuperCreate;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.mykeys.app.utils.OrderedProperties;
import org.dpr.mykeys.utils.X509AttributesUtils;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

import static org.dpr.mykeys.utils.MessageUtils.getMessage;

public class CreateTemplateDialog extends SuperCreate implements ItemListener {

    private LabelValuePanel infosPanel;
    private final Map<String, JCheckBox> checkBoxes = new HashMap<>();
    private final CertificateValue certInfo = new CertificateValue();
    private boolean isEditing = false;

    public CreateTemplateDialog(Frame owner, boolean modal) {

        super(owner, true);

        init();
        this.pack();

    }

    public CreateTemplateDialog(Frame owner, boolean modal, CertificateTemplate template) {

        super(owner, true);
        isEditing = true;
        init(template);
        this.pack();

    }

    protected void init(CertificateTemplate template) {
        init();
        fillInfos(template);

    }

    private void fillInfos(CertificateTemplate template) {
        OrderedProperties myProfile = template.getProperties();
        Enumeration<?> e = myProfile.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            infosPanel.set(key, myProfile.getProperty(key));
            // infosPanel.set( key, myProfile.getProperty(key));
        }
        ((JTextComponent) infosPanel.getComponent("name")).setEditable(false);
        String keyUsage = myProfile.getProperty("&keyUsage");
        if (keyUsage != null) {
            //  infosPanel.set("keyUsage", CertificateUtils.keyUsageToString(Integer.valueOf(keyUsage)));

            //FIXME certInfo.setKeyUsage(CertificateUtils.keyUsageFromInt(Integer.valueOf(keyUsage)));
            boolean[] kus = KeyUsages.keyUsageFromInt(Integer.valueOf(keyUsage));
            for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
                checkBoxes.get(X509Constants.keyUsageLabel[i]).setSelected(kus[i]);

            }
        }

        // log.debug(CertificateUtils.keyUsageToString(certInfo.getKeyUsage()));
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
            checkBoxes.put(X509Constants.keyUsageLabel[i], item);
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
//        infosPanel.set("algoPubKey",
//                "DSA");
//        infosPanel.set("duration",
//                "5");

    }

    /**
     * .
     *
     * @param mapKeyLength
     * @param mapAlgoKey
     * @param mapAlgoSig
     * @return
     */
    protected LabelValuePanel createInfoPanel(Map<String, String> mapKeyLength, Map<String, String> mapAlgoKey,
                                              Map<String, String> mapAlgoSig) {
        if (infosPanel == null) {
            infosPanel = new LabelValuePanel();
            Map<String, String> mapAC = null;
            KeyStoreValue ksAC = KSConfig.getInternalKeystores().getStoreAC();
            KeyStoreHelper ksh = new KeyStoreHelper();
            try {
                mapAC = ksh.getCAMapAlias(ksAC);
            } catch (Exception e) {
                //
            }
            if (mapAC == null) {
                mapAC = new HashMap<>();
            }
            mapAC.put(" ", " ");

            infosPanel.put(getMessage("template.name"), "name", "");

            infosPanel.put(getMessage("label.description"), JTextArea.class, "description", "", true);
            infosPanel.putEmptyLine();
            infosPanel.put(getMessage("x509.issuer"), JComboBox.class, "issuer", mapAC, "");
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
            infosPanel.put(getMessage("x509.subject.organisation"), "O", "");
            infosPanel.put(getMessage("x509.subject.organisationUnit"), "OU", "Sample");
            infosPanel.put(getMessage("x509.subject.location"), "L", "");
            infosPanel.put(getMessage("x509.subject.street"), "SR", "");

            infosPanel.putEmptyLine();
            //	infosPanel.put(getMessage("x509.cdp"), JCheckBox.class, "CrlDistribObli", false, false);
            infosPanel.put(getMessage("x509.cdp"), "crlDistrib", "");
            infosPanel.put(getMessage("x509.policynotice"), "policyNotice", "");
            infosPanel.put(getMessage("x509.policycps"), "policyCPS", "");
            infosPanel.put(getMessage("x509.policyid"), "policyID", "");
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

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            switch (command) {
                case "CHOOSE_IN":

                    break;
                case "OK":
                    try {
                        fillCertInfo();
                        ProfileServices pman = new ProfileServices(KSConfig.getProfilsPath());
                        pman.saveToFile(infosPanel.getElements(), (String) infosPanel.getElements().get("name"), certInfo, isEditing);

                        CreateTemplateDialog.this.setVisible(false);

                    } catch (Exception e) {

                        DialogUtil.showError(CreateTemplateDialog.this, e.getMessage());
                        e.printStackTrace();
                    }

                    break;
                case "CANCEL":
                    CreateTemplateDialog.this.setVisible(false);
                    break;
            }

        }

        void fillCertInfo() {
            Map<String, Object> elements = infosPanel.getElements();
            Set<String> keys = elements.keySet();
            for (String key : keys) {
            }

            // certInfo.setX509PrincipalMap(elements);
            HashMap<String, String> subjectMap = new HashMap<>();

            FillUtils.fillCertInfo(elements, certInfo);
            //certInfo.setDuration((Integer) elements.get("duration"));

            certInfo.setSubjectMap(elements);

            certInfo.setCrlDistributionURL(((String) elements.get("crlDistrib")));
            certInfo.setPolicyNotice(((String) elements.get("policyNotice")));
            certInfo.setPolicyCPS(((String) elements.get("policyCPS")));
            certInfo.setPolicyID(((String) elements.get("policyID")));
        }

    }


}