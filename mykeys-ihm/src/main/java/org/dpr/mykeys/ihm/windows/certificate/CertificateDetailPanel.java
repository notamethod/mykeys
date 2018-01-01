package org.dpr.mykeys.ihm.windows.certificate;

import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bouncycastle.util.encoders.Hex;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class CertificateDetailPanel extends LabelValuePanel {

    // LabelValuePanel infosPanel;
    CertificateValue info;

    public CertificateDetailPanel(CertificateValue info) {
        this.info = info;
        getPanel();
    }

    public void getPanel() {
        // infosPanel = new LabelValuePanel();
        this.addTitle(Messages.getString("x509.subject"));
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
                this.put(name, JTextField.class, "", value, false);
            }
        }
        this.putEmptyLine();
        this.addTitle(Messages.getString("x509.validity"));
        this.put(Messages.getString("x509.startdate"), JSpinnerDate.class, "notBefore", info.getNotBefore(),
                false);
        this.put(Messages.getString("x509.enddate"), JSpinnerDate.class, "notAfter", info.getNotAfter(),
                false);

        this.putEmptyLine();
        this.put(Messages.getString("x509.pubkeysize"), JTextField.class, "keyLength",
                String.valueOf(info.getKeyLength()), false);
        this.put(Messages.getString("x509.pubkeyalgo"), JTextField.class, "algoPubKey", info.getAlgoPubKey(),
                false);
        // this.put("Clé publique", JTextArea.class, "pubKey",
        // X509Util.toHexString(info.getPublicKey().getEncoded()," ",
        // false),false);
        this.put(Messages.getString("x509.sigalgo"), JTextField.class, "algoSig", info.getAlgoSig(), false);

        this.putEmptyLine();
        this.put(Messages.getString("x509.serial"), JTextField.class, "numser",
                X509Util.toHexString(info.getCertificate().getSerialNumber(), " ", true), false);

        this.put(Messages.getString("x509.issuer"), JTextArea.class, "emetteur",
                info.getCertificate().getIssuerX500Principal().toString(), false);


        String keyUsage = info.keyUsageToString();
        if (keyUsage != null) {
            this.put("Utilisation (key usage)", JLabel.class, "keyUsage", keyUsage, false);
        }

        this.putEmptyLine();

        this.put("Digest SHA1", JLabel.class, "signature", X509Util.toHexString(info.getDigestSHA1(), " ", false),
                false);

        this.put("Digest SHA256", JLabel.class, "signature", X509Util.toHexString(info.getDigestSHA256(), " ", false),
                false);
//		this.put("Digest SHA256", JLabel.class, "signature", X509Util.toHexString(info.getDigestSHA256(), " ", false),
//				false);
        this.putEmptyLine();
        this.put("Chaine de certificats", JTextArea.class, "xCertChain", info.getChaineStringValue(), false);
        this.putEmptyLine();
        this.put("Signature", JTextArea.class, "signature", X509Util.toHexString(info.getSignature(), " ", false),
                false);
        this.put(Messages.getString("x509.alias"), JTextField.class, "", info.getAlias(), false);
    }

}
