package org.dpr.mykeys.ihm.certificate;

import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.CancelCreationException;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.event.ItemListener;
import java.util.Map;

public class CACreate extends SuperCreate implements ItemListener {

    @Override
    protected CertificateType getCertificateType() {
        return CertificateType.AC;
    }

    public CACreate(JFrame owner, KeyStoreValue ksInfo,
                    boolean modal) throws CancelCreationException {
        super(owner, modal);
        this.ksInfo = ksInfo;
        init();
        initSpec();
        this.pack();
    }

    public CACreate(JFrame owner, KeyStoreValue ksInfo, Certificate issuer,
                    boolean modal) throws CancelCreationException {
        super(owner, modal);
        this.ksInfo = ksInfo;
        init(issuer);
        initSpec();
        this.pack();
    }


    private void initSpec() {
        setTitle(Messages.getString("ac.creation.title"));

    }

    /**
     * .
     *
     * @param mapKeyLength
     * @param mapAlgoKey
     * @param mapAlgoSig
     * @return
     */
    @Override
    protected LabelValuePanel getExtendedPanel(Map<String, String> mapKeyLength,
                                              Map<String, String> mapAlgoKey, Map<String, String> mapAlgoSig) {

        if (infosPanel == null) {
            infosPanel = new LabelValuePanel();
            infosPanel.put(Messages.getString("x509.alias"), "alias", "");

            infosPanel.putEmptyLine();

            // subject
            infosPanel.putEmptyLine();

            infosPanel.putEmptyLine();
            infosPanel.put(Messages.getString("x509.cdp"), "crlDistrib", "");
            infosPanel.put("Policy notice", "policyNotice", "");
            infosPanel.put("Policy CPS", "policyCPS", "");

            infosPanel.putEmptyLine();
            if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", KSConfig.getInternalKeystores().getPassword(),
                        false);
                infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2",
                        KSConfig.getInternalKeystores().getPassword(), false);
            }
        }
        return infosPanel;
    }

    protected void predefineKeyUsage() {
        for (int i = 0; i < keyUsageCheckBoxes.size(); i++) {
            JCheckBox item = keyUsageCheckBoxes.get(i);
            if ((i == 5) || (i == 6)) {
                item.setSelected(true);
            }

        }
    }
}
