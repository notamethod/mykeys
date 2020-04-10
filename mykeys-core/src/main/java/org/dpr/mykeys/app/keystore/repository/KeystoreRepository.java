package org.dpr.mykeys.app.keystore.repository;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;

import java.io.File;
import java.io.OutputStream;
import java.security.PrivateKey;
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

    @Override
    public void saveCSR(byte[] b, File f, KeyStoreHelper.SAVE_OPTION option) throws ServiceException {
        throw new ServiceException("not implemented");
    }
    @Override
    public void exportPrivateKey(PrivateKey privateKey, OutputStream os, char[] pass)
            throws ServiceException {
        throw new ServiceException("not implemented");
    }


}
