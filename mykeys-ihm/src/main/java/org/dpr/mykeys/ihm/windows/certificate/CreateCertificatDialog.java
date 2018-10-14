package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;

import javax.swing.*;
import java.awt.event.ItemListener;

public class CreateCertificatDialog extends SuperCreate implements ItemListener {

    protected CreateCertificatDialog(JFrame owner, KeyStoreValue ksInfo, CertificateValue issuer,
                                     boolean modal) {

        super(owner, modal);
        this.ksInfo = ksInfo;

        init(issuer);
        this.pack();
        //this.setVisible(true);

    }
    protected CreateCertificatDialog(JFrame owner, KeyStoreValue ksInfo,
                                     boolean modal) {

        super(owner, modal);
        this.ksInfo = ksInfo;

        this.pack();
        //this.setVisible(true);

    }
    public static void main(String[] args) {
        JFrame f = null;
        CreateCertificatDialog cr = new CreateCertificatDialog(f, null,
                null, false);
    }
}
