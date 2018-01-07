package org.dpr.mykeys.app.keystore;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class KeystoreBuilder extends KeyTools {


    public static final Log log = LogFactory.getLog(KeyStoreHelper.class);
    private KeyStore keystore;

    public KeystoreBuilder(KeyStore keystore) {
        super();
        this.keystore = keystore;
    }

    public KeystoreBuilder(StoreFormat format) throws KeyStoreException {
        super();
        this.keystore = KeyStore.getInstance(format.toString());
    }

    /**
     * Create a keystore of type 'ksType' with filename 'name'
     *
     * @param name
     * @param password
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws Exception
     */
    public KeystoreBuilder create(String name, char[] password) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
        Path path = Paths.get(name);
        if (Files.exists(path)) {
            throw new IOException("File already exists " + path.toString());
        }

        keystore.load(null, password);
        OutputStream fos = new FileOutputStream(new File(name));
        keystore.store(fos, password);
        fos.close();


        return this;

    }

    public KeyStore get() {
        return keystore;
    }

    public void addCertToKeyStoreNew(X509Certificate cert, KeyStoreValue ksInfo, CertificateValue certInfo)
            throws KeyToolsException {

        saveCertChain(keystore, cert, certInfo);
        saveKeyStore(keystore, ksInfo);
    }

    public void addCert(X509Certificate cert, KeyStoreValue ksInfo, CertificateValue certInfo) throws KeyToolsException {
        saveCertChain(keystore, cert, certInfo);
        saveKeyStore(keystore, ksInfo);
    }

    @Deprecated
    public KeystoreBuilder addCert(X509Certificate[] xCerts, KeyStoreValue ksInfo, CertificateValue certInfo, char[] password)
            throws KeyToolsException {
        // FIXME
        if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
            certInfo.setPassword(password);
        }

        saveCertChain(keystore, xCerts[0], certInfo);
        saveKeyStore(keystore, ksInfo);
        return this;
    }

    public KeystoreBuilder addCert(KeyStoreValue ksInfo, CertificateValue certInfo) throws KeyToolsException {

        if (null == certInfo.getPassword() || certInfo.getPassword().length == 0) {
            throw new IllegalArgumentException("password can't be null empty");
        }

        saveCertChain(keystore, certInfo);
        try {
            System.out.println(keystore.size());
            System.out.println(new String(certInfo.getPassword()));
            System.out.println(new String(ksInfo.getPassword()));

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        saveKeyStore(keystore, ksInfo);
        return this;
    }

    private String saveCertChain(KeyStore keystore, CertificateValue certInfo) throws KeyToolsException {

        if (StringUtils.isBlank(certInfo.getAlias())) {
            BigInteger bi = KeyTools.RandomBI(30);
            certInfo.setAlias(bi.toString(16));
        }
        try {
            // pas bonne chaine
            // X509Certificate x509Cert = (X509Certificate) cert;

            if (certInfo.getPrivateKey() == null) {
                keystore.setCertificateEntry(certInfo.getAlias(), certInfo.getCertificate());
            } else {
                Certificate[] chaine = certInfo.getCertificateChain();
                if (chaine == null)
                    chaine = new Certificate[]{certInfo.getCertificate()};
                keystore.setKeyEntry(certInfo.getAlias(), certInfo.getPrivateKey(), certInfo.getPassword(), chaine);
            }

        } catch (KeyStoreException e) {
            throw new KeyToolsException("Sauvegarde du certificat impossible:" + certInfo.getAlias(), e);

        }
        return certInfo.getAlias();

    }

    public KeystoreBuilder removeCert(CertificateValue certificateInfo) throws KeyStoreException {

        keystore.deleteEntry(certificateInfo.getAlias());

        return this;
    }


    public void save(KeyStoreValue ksInfo) throws KeyToolsException {

        try {
            OutputStream fos = new FileOutputStream(new File(ksInfo.getPath()));
            keystore.store(fos, ksInfo.getPassword());
            fos.close();
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
        }
    }


    public KeyStore loadKeyStore2(String ksName, String type, char[] pwd) throws KeyToolsException {
        // KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        KeyStore ks = null;
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
        } catch (FileNotFoundException e) {
            throw new KeyToolsException("Fichier non trouvé:" + ksName, e);
        } catch (KeyStoreException | CertificateException | IOException e) {
            throw new KeyToolsException("Echec du chargement de:" + ksName, e);

        } catch (NoSuchAlgorithmException e) {
            throw new KeyToolsException("Format inconnu:" + ksName, e);
        }
        return ks;
    }

    private void saveCertChain(KeyStore kstore, X509Certificate cert, CertificateValue certInfo)
            throws KeyToolsException {
        try {
            // pas bonne chaine
            // X509Certificate x509Cert = (X509Certificate) cert;

            if (certInfo.getPrivateKey() == null) {
                kstore.setCertificateEntry(certInfo.getAlias(), cert);
            } else {
                Certificate[] chaine = null;
                if (certInfo.getCertificateChain() != null) {
                    chaine = certInfo.getCertificateChain();
                } else {
                    chaine = new Certificate[]{cert};
                }
                kstore.setKeyEntry(certInfo.getAlias(), certInfo.getPrivateKey(), certInfo.getPassword(), chaine);
            }

        } catch (KeyStoreException e) {
            throw new KeyToolsException("Sauvegarde du certificat impossible:" + certInfo.getAlias(), e);
        }
    }


}
