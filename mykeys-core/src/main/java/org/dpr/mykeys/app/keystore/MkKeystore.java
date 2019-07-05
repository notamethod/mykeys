package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.repository.keystore.Pkcs12KeystoreRepository;
import org.dpr.mykeys.app.repository.RepositoryException;
import org.dpr.mykeys.app.repository.keystore.DerKeystoreRepository;
import org.dpr.mykeys.app.repository.keystore.JksKeystoreRepository;
import org.dpr.mykeys.app.repository.keystore.KeystoreRepository;
import org.dpr.mykeys.app.repository.keystore.PemKeystoreRepository;

import java.security.PrivateKey;
import java.util.List;

public interface MkKeystore {


    static MkKeystore getInstance(StoreFormat format) {
        switch (format) {
            case PEM:
                return new PemKeystoreRepository();
            case DER:
                return new DerKeystoreRepository();
            case PKCS12:
                return new Pkcs12KeystoreRepository();
            default:
                return new JksKeystoreRepository();
        }
    }

    void removeCertificates(KeyStoreValue ksValue, List<CertificateValue> certificatesInfo) throws
            ServiceException;

    void savePrivateKey(PrivateKey privateKey, String fName, char[] pass)
            throws ServiceException;


    void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos);

    void save(KeyStoreValue ksValue) throws RepositoryException;

    //load ?
    List<CertificateValue> getCertificates(KeyStoreValue ksValue)
            throws ServiceException;

    void addCert(KeyStoreValue ki, CertificateValue certificate) throws ServiceException;

    void save(KeyStoreValue ksValue, KeystoreRepository.SAVE_OPTION option) throws RepositoryException;

}
