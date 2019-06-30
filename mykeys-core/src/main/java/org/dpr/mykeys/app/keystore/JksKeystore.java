package org.dpr.mykeys.app.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class JksKeystore implements MkKeystore {

    private static final Log log = LogFactory.getLog(JksKeystore.class);

    public JksKeystore() {
    }

    @Override
    public void removeCertificates(KeyStoreValue ksValue, List<CertificateValue> certificates) throws ServiceException {

        try {

            if (null == ksValue.getKeystore()) {

                ksValue.setKeystore(loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword()));
            }
            List<CertificateValue> certs = getCertificates(ksValue);

            for (CertificateValue certificateInfo : certificates) {
                if (certificateInfo != null)
                    certs.remove(certificateInfo);
                ksValue.getKeystore().deleteEntry(certificateInfo.getAlias());
            }
            saveKeyStore(ksValue.getKeystore(), ksValue.getPath(), ksValue.getPassword());

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void savePrivateKey(PrivateKey privateKey, String fName, char[] pass) throws ServiceException {

    }

    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) {

    }

    @Override
    public void save(KeyStoreValue ksValue) throws ServiceException {
        File file = new File(ksValue.getPath());
        KeyStore keystore = null;
        if (!file.exists()) {
            try {
                keystore = create(ksValue.getPath(), ksValue.getPassword());
            } catch (Exception e) {
                throw new ServiceException("creating fail", e);
            }
        }
        addCerts(ksValue);

    }

    private KeyStore create(String name, char[] password) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
        KeyStore keystore = KeyStore.getInstance(StoreFormat.JKS.toString());
        Path path = Paths.get(name);
        if (Files.exists(path)) {
            throw new IOException("File already exists " + path.toString());
        }
        keystore.load(null, password);
        OutputStream fos = new FileOutputStream(new File(name));
        keystore.store(fos, password);
        fos.close();
        return keystore;




    }

    @Override
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

    @Override
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

    private void addCerts(KeyStoreValue ksValue) throws ServiceException {
        KeyStore ks = loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword());
        KeystoreBuilder ksb = new KeystoreBuilder(ks);
        try {
            ksb.addCerts(ksValue, ksValue.getCertificates());
        } catch (KeyToolsException e) {
            throw new ServiceException("addCerts fail", e);
        }

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

    /**
     * @param ksName
     * @param format
     * @param pwd
     * @return
     * @throws KeyToolsException
     */
    public KeyStore loadKeyStore(String ksName, StoreFormat format, char[] pwd) throws ServiceException {
        // KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
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
            throw new ServiceException("Echec du chargement de:" + ksName, e);

        } catch (FileNotFoundException e) {
            throw new ServiceException("Fichier non trouvé:" + ksName + ", " + e.getCause(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Format inconnu:" + ksName + ", " + e.getCause(), e);
        } catch (CertificateException | IOException e) {
            throw new ServiceException("Echec du chargement de:" + ksName + ", " + e.getCause(), e);
        }

        return ks;
    }

    /**
     * Must be deleted because of CertificatValue constructor
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
                String message = "chaine de certification nulle pour " + alias + " (" + certInfo.getName() + ")";
                if (certInfo.isContainsPrivateKey())
                    log.error(message);
                else
                    log.debug(message);
                // return null;
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

    private KeyStore getKeyStore(KeyStoreValue ksValue) throws ServiceException {
        if (null == ksValue.getKeystore()) {
            ksValue.setKeystore(loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword()));
        }
        return ksValue.getKeystore();
    }
}
