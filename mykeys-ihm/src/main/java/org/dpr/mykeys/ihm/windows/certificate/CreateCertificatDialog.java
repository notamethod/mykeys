package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.certificate.Usage;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.*;

public class CreateCertificatDialog extends SuperCreate implements ItemListener {

    protected CreateCertificatDialog(JFrame owner, KeyStoreValue ksInfo,
                                     boolean modal) {

        super(owner, modal);
        this.ksInfo = ksInfo;

        init();
        this.pack();
        //this.setVisible(true);

    }

    public static void main(String[] args) {
        JFrame f = null;
        CreateCertificatDialog cr = new CreateCertificatDialog(f, null,
                false);
    }
}
