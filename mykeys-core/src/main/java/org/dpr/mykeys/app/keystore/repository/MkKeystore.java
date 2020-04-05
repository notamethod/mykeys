package org.dpr.mykeys.app.keystore.repository;

import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreFormat;

import java.io.File;
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
            case JKS:
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

    void save(KeyStoreValue ksValue, KeyStoreHelper.SAVE_OPTION option) throws RepositoryException;

    void saveCSR(byte[] b, File f, KeyStoreHelper.SAVE_OPTION option) throws ServiceException;

}
