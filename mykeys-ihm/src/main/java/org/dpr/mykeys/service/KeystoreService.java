package org.dpr.mykeys.service;

import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.repository.MkKeystore;
import org.dpr.mykeys.app.keystore.repository.RepositoryException;

import java.util.List;

public class KeystoreService {

    public void removeCertificates(KeyStoreValue ksValue, List<CertificateValue> certificatesInfo) throws ServiceException {
        MkKeystore mks = MkKeystore.getInstance(ksValue.getStoreFormat());

        try {
            mks.removeCertificates(ksValue, certificatesInfo);
        } catch (RepositoryException e) {
           throw new ServiceException("deletion failed", e);
        }

    }
}
