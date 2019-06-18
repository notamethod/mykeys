package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.certificate.CertificateValue;

import java.security.PrivateKey;
import java.util.List;

public interface MkKeystore {

    static MkKeystore getInstance(StoreFormat format) {
        switch (format) {
            case PEM:
                return new PemKeystore();
            case DER:
                return new DerKeystore();
            default:
                return new JksKeystore();
        }
    }

    void removeCertificate(KeyStoreValue ksValue, CertificateValue certificateInfo) throws
            ServiceException;

    void savePrivateKey(PrivateKey privateKey, String fName)
            throws ServiceException;

    void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) throws
            ServiceException;

    void save(KeyStoreValue ksValue) throws ServiceException;

    //load ?
    List<CertificateValue> getCertificates(KeyStoreValue ksValue)
            throws ServiceException;

    void addCert(KeyStoreValue ki, CertificateValue certificate) throws ServiceException;
}
