package org.dpr.mykeys.ihm.certificate;

import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.CancelCreationException;
import org.dpr.mykeys.app.CertificateType;

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

    public static SuperCreate getCreateDialog(JFrame owner, KeyStoreValue info, Certificate issuer, CertificateType certificateType) throws CancelCreationException {
        switch (certificateType) {
            case STANDARD:
                return new CreateCertificatDialog(owner, info, issuer,
                        true);

            case AC:
                return new CACreate(owner, info, issuer,
                        true);

            case SERVER:
                break;
            case CODE_SIGNING:
                break;
            case AUTH:
                break;
        }
        return new CreateCertificatDialog(owner, info, issuer,
                true, certificateType);
    }
}
