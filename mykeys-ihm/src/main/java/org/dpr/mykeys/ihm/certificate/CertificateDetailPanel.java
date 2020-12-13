package org.dpr.mykeys.ihm.certificate;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.utils.Pair;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.utils.CertificateUtils;
import org.dpr.mykeys.app.utils.X509Util;
import org.dpr.swingtools.components.JSpinnerDate;
import org.dpr.swingtools.components.LabelValuePanel;
import org.dpr.swingtools.components.SWLabel;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.util.List;
import java.util.*;

public class CertificateDetailPanel extends JPanel {

    // LabelValuePanel infosPanel;
    protected final Certificate info;

    public CertificateDetailPanel(Certificate info) {
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
        try {
            Collection col = X509ExtensionUtil.getSubjectAlternativeNames(info.getCertificate());
            if (!col.isEmpty())
                otherInfosPanel.put(Messages.getString("alternativeNames"), JTextArea.class, "altnames", inlineFormat(col, "altname."),
                        false);
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        }

        if (!info.getOtherParams().isEmpty())
            otherInfosPanel.put(Messages.getString("policies.title"), JTextArea.class, "policies", inlineFormat(info.getOtherParams()).toString(),
                    false);

        Set<String> distPointSet = X509Util.getDistributionPoints(info.getCertificate());
        if (!distPointSet.isEmpty())
            otherInfosPanel.put(Messages.getString("x509.cdp"), JTextField.class, "crldist", inlineFormat(distPointSet, null),
                false);


        try {
            List<String> extendedKU = X509Util.getExtendedKeyUsages(info.getCertificate());
            final boolean[] first = new boolean[]{true};
            extendedKU.forEach(item -> {
                otherInfosPanel.put(first[0] ? Messages.getString("eku.title") : "", JLabel.class, "keyUsage", Messages.getDefaultString(item), false);
                first[0] = false;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        otherInfosPanel.put("Digest SHA256", JTextField.class, "signature", X509Util.toHexString(info.getDigestSHA256(), " ", true),
                false);
//        public static final int otherName = 0;
//        public static final int rfc822Name = 1;
//        public static final int dNSName = 2;
//        public static final int x400Address = 3;
//        public static final int directoryName = 4;
//        public static final int ediPartyName = 5;
//        public static final int uniformResourceIdentifier = 6;
//        public static final int iPAddress = 7;
//        public static final int registeredID = 8;

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

    private StringBuilder inlineFormat(Collection col, String prefix) {
        StringBuilder sb = new StringBuilder();
        for (Object o : col) {
            if (o instanceof ArrayList) {
                List lo = (List) o;
                for (int i = 0; i < lo.size(); i++) {
                    Object obj = lo.get(i);
                    if (i == 0 && prefix != null)
                        sb.append(Messages.getDefaultString(prefix + obj.toString())).append(": ");
                    else
                        sb.append(obj.toString()).append(" ");
                }
                sb.append("\n");
            } else {
                sb.append(o.toString()).append("\n");
            }
        }
        return sb;
    }


    /**
     * Formap map elements to texte area
     *
     * @param otherParams the map
     * @return
     */
    private StringBuilder inlineFormat(Map<String, String> otherParams) {
        StringBuilder sb = new StringBuilder();
        info.getOtherParams().forEach((k, v) -> {
            if (v == null)
                sb.append(Messages.getDefaultString(k)).append("\n");
            else
                sb.append(Messages.getDefaultString(k)).append(": ").append(v).append("\n");
        });
        return sb;
    }

    protected void addCrlPanel(LabelValuePanel infosPanel) {
    }

    private void addUsagePanel(LabelValuePanel infosPanel) {
        // infosPanel.addTitle(Messages.getString("usage.title"), Color.CYAN);
        List<String> keyUsageList = CertificateUtils.keyUsageToList(info.getKeyUsage());
        if (keyUsageList != null) {
            final boolean[] first = new boolean[]{true};

            keyUsageList.forEach(item -> {
                infosPanel.put(new SWLabel(first[0] ? Messages.getString("certinfo.keyUsage") : "", SWLabel.Style.BOLD), JLabel.class, "keyUsage", Messages.getDefaultString(item), false);
                first[0] = false;
            });

//            for (String keyUsage : keyUsageList)
//                infosPanel.put("", JLabel.class, "keyUsage", Messages.getDefaultString(keyUsage), false);
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
        for (Pair pair : info.getSubjectList()){
            String name;
            try {
                name = Messages.getString(X509Util.getMapNames().get(pair.getKey()));
            } catch (Exception e) {
                name = pair.getKey();
            }
            String value = pair.getValue();
            if (value.startsWith("#")) {
                value = new String(Hex.decode(value.substring(1)));
            }
            infosPanel.put(name, JTextField.class, "", value, false);
        }

        infosPanel.putEmptyLine();
    }

    private Color getValidityColor(Certificate info) {
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
