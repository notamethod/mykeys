package org.dpr.mykeys.ihm.certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.configuration.MkSession;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateManager;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.CancelCreationException;
import org.dpr.mykeys.ihm.listeners.HelpMouseListener;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.mykeys.app.utils.ProviderUtil;
import org.dpr.mykeys.utils.PanelUtils;
import org.dpr.swingtools.components.LabelValuePanel;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.*;

public class SuperCreate extends JDialog implements ItemListener {

    protected static final Log log = LogFactory.getLog(SuperCreate.class);
    protected KeyStoreValue ksInfo;
    protected boolean isAC = false;

    LabelValuePanel infosPanel;
    final Certificate certInfo = new Certificate();
    protected CertificateType typeCer;
    private LabelValuePanel durationPanel;
    private Certificate issuer;
    protected final List<JCheckBox> keyUsageCheckBoxes = new ArrayList<>();

    protected SuperCreate() {
        super();
    }

    protected SuperCreate(Frame owner) {
        super(owner);
    }

    private SuperCreate(Dialog owner) {
        super(owner);
    }

    private SuperCreate(Window owner) {
        super(owner);
    }

    protected SuperCreate(Frame owner, boolean modal) {
        super(owner, modal);
    }

    private SuperCreate(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    private static String getDefaultDuration(CertificateType standard) {
        return "3";
    }

    protected CertificateType getCertificateType() {
        if (typeCer == null)
            return CertificateType.STANDARD;
        return typeCer;
    }

    protected void init(Certificate issuer) throws CancelCreationException {
        this.issuer = issuer;
        init();
    }

    protected void init() throws CancelCreationException {


        String a = null;


//Server Authentication (1.3.6.1.5.5.7.3.1).
//
//        Other "common" types of X.509 certs are Client Authentication (1.3.6.1.5.5.7.3.2), Code Signing (1.3.6.1.5.5.7.3.3), and a handful of others are used for various encryption and authentication schemes.
        DialogAction dAction = new DialogAction();

        typeCer = getCertificateType();

        if (null == typeCer)
            throw new CancelCreationException();

        setTitle(Messages.getString("certificat.creation.title"));


        JPanel jp = new JPanel();
        //BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(new VerticalLayout());
        //jp.setLayout(bl);
        setContentPane(jp);
        JScrollPane scrollPane = new JScrollPane(jp);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setContentPane(scrollPane);

        LabelValuePanel subjectPanel = new LabelValuePanel();

        PanelUtils.addSubjectToPanel(typeCer, subjectPanel);
        if (KSConfig.getUserCfg().getProperty("freeflight") != null)
            subjectPanel.put("Free subject", "freesubject", "");
        LabelValuePanel extendedPanel = getExtendedPanel(null, null, null);

        JPanel checkPanel = new JPanel(new GridLayout(0, 3));

        Border border = BorderFactory.createTitledBorder("Key usage");
        checkPanel.setBorder(border);
        for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
            JCheckBox item = new JCheckBox(X509Constants.keyUsageLabel[i]);
            keyUsageCheckBoxes.add(item);
            item.addItemListener(this);

            checkPanel.add(item);
        }

        JXCollapsiblePane cp = new JXCollapsiblePane();
        cp.setCollapsed(true);
        cp.setLayout(new BorderLayout());
//
        cp.add(extendedPanel);
        // Show/hide the "Controls"
        JButton toggle = new JButton(cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        toggle.setText(Messages.getString("show.extended.info"));
//        this.add(infosPanel);
//        this.add(toggle);
//        this.add(cp);

        infosPanel = new LabelValuePanel();
        jp.add(subjectPanel);
        jp.add(PanelUtils.getDurationPanel(3, infosPanel, true));
        jp.add(getPubKeyPanel());
        jp.add(getSignaturePanel());
        jp.add(toggle);
        jp.add(cp);
        infosPanel.addChild(subjectPanel);
        infosPanel.addChild(extendedPanel);


        jp.add(cp);
        jp.add(checkPanel);
        jp.add(new OkCancelPanel(dAction, FlowLayout.RIGHT));

        predefineKeyUsage();

    }

    private Component getPubKeyPanel() {
        Map<String, String> mapAlgoKey = new LinkedHashMap<>();
        for (String algo : ProviderUtil.getKeyPairGeneratorList()) {
            mapAlgoKey.put(algo, algo);
        }
        Map<String, String> mapKeyLength = new LinkedHashMap<>();
        mapKeyLength.put("512 bits", "512");
        mapKeyLength.put("1024 bits", "1024");
        mapKeyLength.put("2048 bits", "2048");
        mapKeyLength.put("4096 bits", "4096");
        mapKeyLength.put("8192 bits*", "8192");
        mapKeyLength.put("16384 bits*", "16384");

        LabelValuePanel pubKeyPanel = new LabelValuePanel();
        pubKeyPanel.addTitle(Messages.getString("publickey.info.title"));
        pubKeyPanel.put(Messages.getString("x509.pubkeysize"), JComboBox.class, "keyLength", mapKeyLength, "2048 bits");
        pubKeyPanel.put(Messages.getString("x509.pubkeyalgo"), JComboBox.class, "algoPubKey", mapAlgoKey, "RSA");
        pubKeyPanel.putEmptyLine();
        JXCollapsiblePane cp = new JXCollapsiblePane();

        // JXCollapsiblePane can be used like any other container
        cp.setLayout(new BorderLayout());
        cp.add(pubKeyPanel);
        infosPanel.addChild(pubKeyPanel);
        return cp;
    }

    protected void predefineKeyUsage() {

    }

    private Component getSignaturePanel() {
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

        // fill with provider's available algorithms
        Map<String, String> mapAlgoSig = new LinkedHashMap<>();
        for (String algo : ProviderUtil.SignatureList) {
            mapAlgoSig.put(algo, algo);
        }


        LabelValuePanel sigPanel = new LabelValuePanel();
        sigPanel.addTitle(Messages.getString("signature.info.title"));

        sigPanel.put(Messages.getString("x509.sigalgo"), JComboBox.class, "algoSig", mapAlgoSig,
                "SHA256WITHRSA");
        if (issuer != null) {
            sigPanel.putDisabled(Messages.getString("x509.issuer"), "emetteur", issuer.getName());
        } else
            sigPanel.put(Messages.getString("x509.issuer"), JComboBox.class, "emetteur", mapAC, " ");
        sigPanel.putEmptyLine();
        JXCollapsiblePane cp = new JXCollapsiblePane();

        // JXCollapsiblePane can be used like any other container
        cp.setLayout(new BorderLayout());
        cp.add(sigPanel);
        infosPanel.addChild(sigPanel);
        return cp;
    }

    /**
     * .
     *
     * @param mapKeyLength
     * @param mapAlgoKey
     * @param mapAlgoSig
     * @return
     */
    protected LabelValuePanel getExtendedPanel(Map<String, String> mapKeyLength,
                                               Map<String, String> mapAlgoKey, Map<String, String> mapAlgoSig) {

        LabelValuePanel extendedPanel = null;
        if (extendedPanel == null) {
            extendedPanel = new LabelValuePanel();
            extendedPanel.put(Messages.getString("x509.alias"), "alias", "");

            // subject
            extendedPanel.putEmptyLine();
            extendedPanel.putEmptyLine();

            extendedPanel.put(Messages.getString("x509.cdp"), "crlDistrib", "");
            extendedPanel.put("Policy notice", "policyNotice", "");
            extendedPanel.put("Policy CPS", "policyCPS", "");
            extendedPanel.put(Messages.getString("x509.policyid"), "policyID", "");
            // infosPanel.putList(Messages.getString("x509.policyid"), JStringList.class, "policyID", JStringList.Position.RIGHT);
            extendedPanel.putEmptyLine();

            if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                extendedPanel.addTitle(Messages.getString("privatekey.title"));
                extendedPanel.put(Messages.getString("optional.privatekey.label"), JPasswordField.class, "pwd1", "",
                        true);
                extendedPanel.getComponent("pwd1").addMouseListener(new HelpMouseListener("create_privatekey"));
                extendedPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2",
                        "", true);
            }

        }
        return extendedPanel;

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        JCheckBox jc = (JCheckBox) source;
        String name = jc.getName();
        if (name != null && name.equals("extendDuration")) {
            durationPanel.setVisible(jc.isSelected());
            // this.pack();

        }
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

                        KeyStoreHelper kserv = new KeyStoreHelper();

                        KeyStoreValue ksAC = KSConfig.getInternalKeystores().getStoreAC();
                        certInfo.setIssuer((String) infosPanel.getElements().get("emetteur"));

                        if (ksInfo.getStoreModel().equals(StoreModel.PKISTORE)) {
                            ksAC = ksInfo;
                        }
                        Certificate inIssuer = SuperCreate.this.issuer;
                        if (inIssuer != null) {
                            certInfo.setIssuer(inIssuer.getAlias());
                            inIssuer = kserv.findCertificateByAlias(ksAC, certInfo.getIssuer(), MkSession.password);

                        } else {
                            if (null != certInfo.getIssuer() && !certInfo.getIssuer().trim().isEmpty())
                                inIssuer = kserv.findCertificateByAlias(ksAC, certInfo.getIssuer(), MkSession.password);
                        }
                        CertificateManager chn = new CertificateManager();
                        Certificate newCertificate = chn.generate(certInfo, inIssuer, typeCer);

                        if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL))
                            newCertificate.setPassword(MkSession.password);
                        if (newCertificate.getPassword() == null || newCertificate.getPassword().length <= 0) {
                            log.debug("Using keystore password to protect private key");
                            newCertificate.setPassword(ksInfo.getPassword());
                        }
                        kserv.addCertToKeyStore(ksInfo, newCertificate, null, null);
                        SuperCreate.this.setVisible(false);

                    } catch (Exception e) {

                        log.error("certificate generation error", e);
                        DialogUtil.showError(SuperCreate.this, e.getMessage());

                    }

                    break;
                case "CANCEL":
                    SuperCreate.this.setVisible(false);
                    break;
            }

        }

        void fillCertInfo() {
            Map<String, Object> elements = infosPanel.getElements();
            Set<String> keys = elements.keySet();
            for (String key : keys) {
            }
            if (elements.get("alias") == null
                    || (elements.get("pwd1") == null && !ksInfo.getStoreType().equals(StoreLocationType.INTERNAL))) {
                DialogUtil.showError(SuperCreate.this, "Champs obligatoires");
                return;
            }

            // certInfo.setX509PrincipalMap(elements);
            HashMap<String, String> subjectMap = new HashMap<>();
            FillUtils.fillCertInfo(elements, certInfo);
            certInfo.setAlias((String) elements.get("alias"));
            certInfo.setNotBefore((Date) elements.get("notBefore"));
            certInfo.setNotAfter((Date) elements.get("notAfter"));
            if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                char[] pkPassword = ((String) elements.get("pwd1")).toCharArray();
                certInfo.setPassword(pkPassword);
            }
            certInfo.setSubjectMap(elements);

            certInfo.setCrlDistributionURL(((String) elements.get("crlDistrib")));
            certInfo.setPolicyNotice(((String) elements.get("policyNotice")));
            certInfo.setPolicyID(((String) elements.get("policyID")));
            // certInfo.setPolicyID(infosPanel.getElements().getList("policyID").get(0));
            certInfo.setPolicyCPS(((String) elements.get("policyCPS")));
            certInfo.setFreeSubject((String) elements.get("freesubject"));
        }

    }


}