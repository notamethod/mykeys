package org.dpr.mykeys.ihm.windows.certificate;

import java.awt.*;
import java.util.Iterator;

import javax.swing.*;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.util.encoders.Hex;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.VerticalLayout;

public class CertificateDetailPanel extends JPanel {

    // LabelValuePanel infosPanel;
    CertificateValue info;

    public CertificateDetailPanel(CertificateValue info) {
        this.info = info;
        setLayout(new VerticalLayout());
        getPanel();

    }

    public void getPanel() {

        LabelValuePanel infosPanel = new LabelValuePanel();
        LabelValuePanel otherInfosPanel = new LabelValuePanel();
        infosPanel.addTitle(Messages.getString("x509.subject"));
        if (info.getSubjectMap() != null) {
            Iterator<String> iter = info.getSubjectMap().keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                String name;
                try {
                    name = Messages.getString(X509Util.getMapNames().get(key));
                } catch (Exception e) {
                    name = key;
                }
                String value = info.getSubjectMap().get(key);
                if (value.startsWith("#")) {
                    value = new String(Hex.decode(value.substring(1, value.length())));
                }
                infosPanel.put(name, JTextField.class, "", value, false);
            }
        }

        infosPanel.putEmptyLine();
        infosPanel.addTitle(Messages.getString("x509.validity"));
        infosPanel.put(Messages.getString("x509.startdate"), JSpinnerDate.class, "notBefore", info.getNotBefore(),
                false);
        infosPanel.put(Messages.getString("x509.enddate"), JSpinnerDate.class, "notAfter", info.getNotAfter(),
                false);

        infosPanel.putEmptyLine();
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
        String keyUsage = info.keyUsageToString();
        if (keyUsage != null) {
            infosPanel.put("Utilisation (key usage)", JLabel.class, "keyUsage", keyUsage, false);
        }
        infosPanel.putEmptyLine();
        infosPanel.put(Messages.getString("x509.alias"), JTextField.class, "", info.getAlias(), false);
        infosPanel.putEmptyLine();

        infosPanel.put("Digest SHA1", JLabel.class, "signature", X509Util.toHexString(info.getDigestSHA1(), " ", true),
                false);

        otherInfosPanel.put("Digest SHA256", JTextArea.class, "signature", X509Util.toHexString(info.getDigestSHA256(), " ", true),
                false);


        otherInfosPanel.put("Chaine de certificats", JTextArea.class, "xCertChain", info.getChaineStringValue(), false);
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

}
