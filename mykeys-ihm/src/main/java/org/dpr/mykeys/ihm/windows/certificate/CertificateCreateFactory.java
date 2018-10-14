package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.CancelCreationException;

import javax.swing.*;

public class CertificateCreateFactory {

    public static SuperCreate getCreateDialog(JFrame owner, KeyStoreValue ksInfo,
                                              boolean modal) throws CancelCreationException {


        if (ksInfo == null) {
            return new CreateCertificatDialog(owner, ksInfo,
                    modal);
        } else if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
            return new CACreate(owner, ksInfo,
                    modal);
        } else {
            return new CreateCertificatDialog(owner, ksInfo,
                    modal);
        }
        //return null;
    }

    public static SuperCreate getCreateDialog(JFrame owner, KeyStoreValue info, CertificateValue issuer, boolean modal) throws CancelCreationException {
        return new CreateCertificatDialog(owner, info, issuer,
                modal);
    }
}
