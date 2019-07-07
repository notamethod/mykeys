package org.dpr.mykeys.ihm.windows.certificate;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.util.encoders.Hex;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.utils.X509Util;
import org.dpr.mykeys.utils.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.swingtools.components.JSpinnerDate;
import org.dpr.swingtools.components.LabelValuePanel;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.List;
import java.util.Map;

public class CertificateDetailPanel extends JPanel {

    // LabelValuePanel infosPanel;
    protected final CertificateValue info;

    public CertificateDetailPanel(CertificateValue info) {
        this.info = info;
        setLayout(new VerticalLayout());
        init();
    }

    private void init() {

        LabelValuePanel infosPanel = new LabelValuePanel();
        LabelValuePanel otherInfosPanel = new LabelValuePanel();
        addSubjectPanel(infosPanel);
        //for later use
//        LabelValuePanel subjectPanel = new LabelValuePanel();
//        PanelUtils.addSubjectToPanel(CertificateType.STANDARD, subjectPanel);
//        this.add(subjectPanel);
        //duration

        addDurationPanel(infosPanel);

        infosPanel.put(Messages.getString("x509.pubkeysize"), JTextField.class, "keyLength",
                String.valueOf(info.getKeyLength()), false);
        infosPanel.put(Messages.getString("x509.pubkeyalgo"), JTextField.class, "algoPubKey", info.getAlgoPubKey(),
                false);
        // infosPanel.put("Clé publique", JTextArea.class, "pubKey",
        // X509Util.toHexString(info.getPublicKey().getEncoded()," ",
        // false),false);
        infosPanel.put(Messages.getString("x509.sigalgo"), JTextField.class, "algoSig", info.getAlgoSig(), false);

        infosPanel.putEmptyLine();
        infosPanel.put(Messages.getString("x509.serial"), JTextField.class, "numser",
                X509Util.toHexString(info.getCertificate().getSerialNumber(), " ", true), false);
        infosPanel.putEmptyLine();

        infosPanel.addTitle(Messages.getString("x509.issuer"));
        addIssuerPanel(infosPanel);
        addCrlPanel(infosPanel);
        addUsagePanel(infosPanel);
        infosPanel.put(Messages.getString("x509.alias"), JTextField.class, "", info.getAlias(), false);
        infosPanel.putEmptyLine();

        infosPanel.put("Digest SHA1", JLabel.class, "signature", X509Util.toHexString(info.getDigestSHA1(), " ", true),
                false);


        info.getOtherParams().forEach((k, v) -> otherInfosPanel.put(Messages.getDefaultString(k), JTextField.class, "", v, false));
        otherInfosPanel.put(Messages.getString("policies.title"), JTextArea.class, "signature", inlineFormat(info.getOtherParams()).toString(),
                false);
        otherInfosPanel.put("Digest SHA256", JTextArea.class, "signature", X509Util.toHexString(info.getDigestSHA256(), " ", true),
                false);


        otherInfosPanel.put(Messages.getString("certificatchain.label"), JTextArea.class, "xCertChain", info.getChaineStringValue(), false);
        otherInfosPanel.putEmptyLine();
        otherInfosPanel.put("Signature", JTextArea.class, "signature", X509Util.toHexString(info.getSignature(), " ", false),
                false);


        JXCollapsiblePane cp = new JXCollapsiblePane();
        cp.setCollapsed(true);
        cp.setLayout(new BorderLayout());

        cp.add(otherInfosPanel);
        // Show/hide the "Controls"
        JButton toggle = new JButton(cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        toggle.setText(Messages.getString("show.extended.info"));
        this.add(infosPanel);
        this.add(toggle);
        this.add(cp);
    }

    private StringBuilder inlineFormat(Map<String, String> otherParams) {
        StringBuilder sb = new StringBuilder();
        info.getOtherParams().forEach((k, v) -> {
            if (v == null)
                sb.append(Messages.getDefaultString(k)).append("\n");
            else
                sb.append(Messages.getDefaultString(k)).append(": ").append(v).append("\n");
            ;
        });
        return sb;
    }
    protected void addCrlPanel(LabelValuePanel infosPanel) {
    }

    private void addUsagePanel(LabelValuePanel infosPanel) {
        infosPanel.addTitle(Messages.getString("usage.title"));
        List<String> keyUsageList = CertificateUtils.keyUsageToList(info.getKeyUsage());
        if (keyUsageList != null) {
            for (String keyUsage : keyUsageList)
                infosPanel.put("", JLabel.class, "keyUsage", keyUsage, false);
        }
        infosPanel.putEmptyLine();
    }

    private void addIssuerPanel(LabelValuePanel infosPanel) {
        //TODO; sort elements !
        final X500Name name2 = X500Name.getInstance(info.getCertificate().getIssuerX500Principal().getEncoded());
        for (org.bouncycastle.asn1.x500.RDN rdn : name2.getRDNs()) {

            for (AttributeTypeAndValue tv : rdn.getTypesAndValues()) {
                String oidName;
                String type = BCStyle.INSTANCE.oidToDisplayName(tv.getType());
                try {
                    oidName = Messages.getString(X509Util.getMapNames().get(type));
                } catch (Exception e) {
                    oidName = type;
                }
                infosPanel.put(oidName, JTextField.class, "", tv.getValue(), false);
            }
        }
        infosPanel.putEmptyLine();
    }

    private void addDurationPanel(LabelValuePanel infosPanel) {
        infosPanel.addTitle(Messages.getString("x509.validity"), getValidityColor(info));
        infosPanel.put(Messages.getString("x509.startdate"), JSpinnerDate.class, "notBefore", info.getNotBefore(),
                false);
        infosPanel.put(Messages.getString("x509.enddate"), JSpinnerDate.class, "notAfter", info.getNotAfter(),
                false);

        infosPanel.putEmptyLine();
    }

    private void addSubjectPanel(LabelValuePanel infosPanel) {
        infosPanel.addTitle(Messages.getString("x509.subject"));
        if (info.getSubjectMap() != null) {
            for (String key : info.getSubjectMap().keySet()) {
                String name;
                try {
                    name = Messages.getString(X509Util.getMapNames().get(key));
                } catch (Exception e) {
                    name = key;
                }
                String value = info.getSubjectMap().get(key);
                if (value.startsWith("#")) {
                    value = new String(Hex.decode(value.substring(1)));
                }
                infosPanel.put(name, JTextField.class, "", value, false);
            }
        }
        infosPanel.putEmptyLine();
    }

    private Color getValidityColor(CertificateValue info) {
        if (info.getCertificate() != null) {
            try {
                info.getCertificate().checkValidity();
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                return Color.RED;
            }
            return Color.GREEN;

        }
        return Color.orange;
    }

}
