package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;

public interface MkKeystore {

    public static MkKeystore getInstance(KeyStoreValue ksValue) {
        switch (ksValue.getStoreFormat()) {
            case PEM:
                return new PemKeystore(ksValue);
            default:
                return new JksKeystore(ksValue);
        }
    }

    public void removeCertificate(KeyStoreValue ksValue, CertificateValue certificateInfo) throws
            ServiceException;

    public void savePrivateKey(PrivateKey privateKey, String fName)
            throws ServiceException;
}
