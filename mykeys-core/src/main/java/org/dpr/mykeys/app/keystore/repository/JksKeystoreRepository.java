package org.dpr.mykeys.app.keystore.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.keystore.StoreFormat;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;

class JksKeystoreRepository extends AbstractJavaKeystoreRepository {

    private static final Log log = LogFactory.getLog(JksKeystoreRepository.class);

    protected final StoreFormat format = StoreFormat.JKS;

    public JksKeystoreRepository() {
    }

    @Override
    public void removeCertificates(KeyStoreValue ksValue, List<CertificateValue> certificates) throws ServiceException {

        try {

            if (null == ksValue.getKeystore()) {

                ksValue.setKeystore(loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword()));
            }
            List<CertificateValue> certs = getCertificates(ksValue);

            for (CertificateValue certificateInfo : certificates) {
                if (certificateInfo != null) {
                    certs.remove(certificateInfo);
                    ksValue.getKeystore().deleteEntry(certificateInfo.getAlias());
                }
            }
            saveKeyStore(ksValue.getKeystore(), ksValue.getPath(), ksValue.getPassword());

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void savePrivateKey(PrivateKey privateKey, String fName, char[] pass) {

    }

    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) {

    }



    public void saveKeyStore(KeyStore ks, String path, char[] password) throws KeyToolsException {

        try {
            ks.aliases();
            OutputStream fos = new FileOutputStream(new File(path));
            ks.store(fos, password);
            fos.close();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + path, e);
        }
    }


    private KeyStore getKeyStore(KeyStoreValue ksValue) throws ServiceException {
        if (null == ksValue.getKeystore()) {
            ksValue.setKeystore(loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword()));
        }
        return ksValue.getKeystore();
    }

    @Override
    protected StoreFormat getFormat() {
        return format;
    }
}
