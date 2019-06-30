package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.certificate.CertificateValue;

import java.util.ArrayList;
import java.util.List;

public abstract class ClassicKeystore implements MkKeystore {

    public void removeCertificates(KeyStoreValue ksValue, List<CertificateValue> certificates) throws ServiceException {
        List<CertificateValue> certs = getCertificates(ksValue);
        List<CertificateValue> certsToRemove = new ArrayList<>();
        for (CertificateValue cert : certs) {
            for (CertificateValue certificateInfo : certificates) {
                if (certificateInfo.getName().equals(cert.getName())) {
                    certsToRemove.add(cert);
                }
            }
        }
        certs.removeAll(certsToRemove);
        saveCertificates(ksValue, certs);
    }
}
