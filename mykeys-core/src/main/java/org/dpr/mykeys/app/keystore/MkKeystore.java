package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.List;

public interface MkKeystore {

    public static MkKeystore getInstance(StoreFormat format) {
        switch (format) {
            case PEM:
                return new PemKeystore();
            case DER:
                return new DerKeystore();
            default:
                return new JksKeystore();
        }
    }

    public void removeCertificate(KeyStoreValue ksValue, CertificateValue certificateInfo) throws
            ServiceException;

    public void savePrivateKey(PrivateKey privateKey, String fName)
            throws ServiceException;

    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) throws
            ServiceException;

    public void save(KeyStoreValue ksValue) throws ServiceException;

    //load ?
    public List<CertificateValue> getCertificates(KeyStoreValue ksValue)
            throws ServiceException;
}
