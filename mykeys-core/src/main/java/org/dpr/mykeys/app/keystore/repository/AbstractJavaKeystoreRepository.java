package org.dpr.mykeys.app.keystore.repository;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.keystore.StoreFormat;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class AbstractJavaKeystoreRepository extends KeystoreRepository {

    private static final Log log = LogFactory.getLog(AbstractJavaKeystoreRepository.class);

    private KeyStore create(String name, StoreFormat format, char[] password) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
        KeyStore keystore = KeyStore.getInstance(format.toString());
        Path path = Paths.get(name);
        if (path.toFile().exists()) {
            throw new IOException("File already exists " + path.toString());
        }
        keystore.load(null, password);
        OutputStream fos = new FileOutputStream(new File(name));
        keystore.store(fos, password);
        fos.close();
        return keystore;
    }

    private void addCerts(KeyStoreValue ksValue) throws RepositoryException {

        try {
            KeyStore ks = loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword());
            KeystoreBuilder ksb = new KeystoreBuilder(ks);
            ksb.addCerts(ksValue, ksValue.getCertificates());
        } catch (KeyToolsException | ServiceException e) {
            throw new RepositoryException("addCerts fail", e);
        }
    }

    /**
     * @param ksName
     * @param format
     * @param pwd
     * @return
     * @throws KeyToolsException
     */
    KeyStore loadKeyStore(String ksName, StoreFormat format, char[] pwd) throws ServiceException {
        KeyStoreValue keystoreValue = new KeyStoreValue(new File(ksName), format, pwd);

        String type = StoreFormat.getValue(format);
        keystoreValue.setPassword(pwd);
        KeyStore ks;
        try {
            try {
                ks = KeyStore.getInstance(type, "BC");
            } catch (Exception e) {
                ks = KeyStore.getInstance("JKS");
            }

            // get user password and file input stream

            java.io.FileInputStream fis = new java.io.FileInputStream(ksName);
            ks.load(fis, pwd);
            fis.close();
        } catch (KeyStoreException e) {
            throw new ServiceException("Fail to load:" + ksName, e);

        } catch (FileNotFoundException e) {
            throw new ServiceException("File not found:" + ksName + ", " + e.getCause(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Format unknown:" + ksName + ", " + e.getCause(), e);
        } catch (CertificateException | IOException e) {
            throw new ServiceException("Fail to load:" + ksName + ", " + e.getCause(), e);
        }

        return ks;
    }

    @Override
    public void save(KeyStoreValue ksValue, SAVE_OPTION option) throws RepositoryException {
        File file = new File(ksValue.getPath());
        boolean exists = file.exists();
        try {
            if (exists) {
                switch (option) {
                    case NONE:
                        throw new EntityAlreadyExistsException("File already exists " + file.getAbsolutePath());
                    case REPLACE:
                        FileUtils.deleteQuietly(file);
                        create(ksValue.getPath(), getFormat(), ksValue.getPassword());
                        break;
                    case ADD:
                        loadKeyStore(ksValue.getPath(), getFormat(), ksValue.getPassword());
                        break;
                    default:
                        //nothing
                        break;
                }
            } else {
                create(ksValue.getPath(), getFormat(), ksValue.getPassword());
            }

        } catch (Exception e) {
            throw new RepositoryException("creating fail", e);
        }

        addCerts(ksValue);

    }

    public List<CertificateValue> getCertificates(KeyStoreValue ksValue) throws ServiceException {

        if (ksValue.getCertificates() != null && !ksValue.getCertificates().isEmpty())
            return ksValue.getChildList();
        else {

            if (null == ksValue.getKeystore()) {

                ksValue.setKeystore(loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword()));
            }
            KeyStore ks = ksValue.getKeystore();
            List<CertificateValue> certs = new ArrayList<>();

            Enumeration<String> enumKs;
            try {
                enumKs = ks.aliases();
                if (enumKs != null && enumKs.hasMoreElements()) {

                    while (enumKs.hasMoreElements()) {
                        String alias = enumKs.nextElement();

                        CertificateValue certInfo = fillCertInfo(ks, alias);
                        certs.add(certInfo);
                    }
                }
            } catch (KeyStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ksValue.setCertificates(certs);
            return certs;
        }
    }

    /**
     * Must be deleted because of CertificateValue constructor
     *
     * @param ks
     * @param alias
     * @return
     * @throws ServiceException
     */
    private CertificateValue fillCertInfo(KeyStore ks, String alias) throws ServiceException {
        CertificateValue certInfo;
        try {
            Certificate certificate = ks.getCertificate(alias);
            Certificate[] certs = ks.getCertificateChain(alias);

            certInfo = new CertificateValue(alias, (X509Certificate) certificate);
            if (ks.isKeyEntry(alias)) {
                certInfo.setContainsPrivateKey(true);

            }
            StringBuilder bf = new StringBuilder();
            if (certs == null) {
                String message = "certification chain is null for " + alias + " (" + certInfo.getName() + ")";
                if (certInfo.isContainsPrivateKey())
                    log.error(message);
                else
                    log.debug(message);
            } else {
                for (Certificate chainCert : certs) {
                    bf.append(chainCert.toString());
                }
                certInfo.setChaineStringValue(bf.toString());
                certInfo.setCertificateChain(certs);
            }

        } catch (GeneralSecurityException e) {
            throw new ServiceException("filling certificate Info impossible", e);
        }
        return certInfo;
    }

    public void addCert(KeyStoreValue ksValue, CertificateValue certificate) throws ServiceException {
        KeyStore ks = loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword());
        KeystoreBuilder ksb = new KeystoreBuilder(ks);
        try {
            ksb.addCert(ksValue, certificate);
            ksValue.getCertificates().add(certificate);
        } catch (KeyToolsException e) {
            throw new ServiceException("addCerts fail", e);
        }

    }


    protected abstract StoreFormat getFormat();

}
