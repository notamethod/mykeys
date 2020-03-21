package org.dpr.mykeys.app.keystore.repository;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;

import java.util.ArrayList;
import java.util.List;

public abstract class KeystoreRepository implements MkKeystore {

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


    public void save(KeyStoreValue ksValue) throws RepositoryException {
        save(ksValue, KeyStoreHelper.SAVE_OPTION.NONE);
    }

}
