package org.dpr.mykeys.app.repository.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreFormat;

import java.security.PrivateKey;
import java.util.List;

public class Pkcs12KeystoreRepository extends AbstractJavaKeystoreRepository {

    private static final Log log = LogFactory.getLog(JksKeystoreRepository.class);
    protected final StoreFormat format = StoreFormat.PKCS12;

    @Override
    public void savePrivateKey(PrivateKey privateKey, String fName, char[] pass) {

    }

    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) {

    }


    @Override
    protected StoreFormat getFormat() {
        return format;
    }
}
