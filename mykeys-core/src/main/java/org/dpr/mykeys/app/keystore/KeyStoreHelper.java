package org.dpr.mykeys.app.keystore;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.app.certificate.CertificateBuilder;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.utils.ActionStatus;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class KeyStoreHelper implements StoreService<KeyStoreValue> {
    public static final Log log = LogFactory.getLog(KeyStoreHelper.class);

    public static final String[] KSTYPE_EXT_PKCS12 = {"p12", "pfx", "pkcs12"};
    public static final String KSTYPE_EXT_JKS = "jks";
    KeyStoreValue ksInfo;

    public KeyStoreHelper(KeyStoreValue ksInfo) {
        this.ksInfo = ksInfo;
    }

    public KeyStoreHelper() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static StoreFormat findTypeKS(String filename) {

        try {
            String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
            if (ext.equalsIgnoreCase(KSTYPE_EXT_JKS)) {
                return StoreFormat.JKS;
            }
            for (String aliasType : KSTYPE_EXT_PKCS12) {
                if (ext.equalsIgnoreCase(aliasType)) {
                    return StoreFormat.PKCS12;
                }
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    public void setKsInfo(KeyStoreValue ksInfo) {
        this.ksInfo = ksInfo;
    }

    public void open() throws ServiceException {

        try {
            loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword());
        } catch (KeyToolsException e) {
            throw new ServiceException("can't load keystore " + ksInfo.getPath(), e);
        }

    }

    public void changePassword(KeyStoreValue ksInfo, char[] newPwd) throws TamperedWithException, KeyToolsException {

        KeyStore ks = null;
        try {
            ks = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).getKeystore();
        } catch (KeyToolsException e) {
            throw new TamperedWithException(e);
        }
        Enumeration<String> enumKs;
        try {
            enumKs = ks.aliases();
            if (enumKs != null && enumKs.hasMoreElements()) {

                while (enumKs.hasMoreElements()) {
                    String alias = enumKs.nextElement();
                    if (ks.isKeyEntry(alias)) {
                        try {
                            PrivateKey pk = (PrivateKey) ks.getKey(alias, ksInfo.getPassword());
                            ks.setKeyEntry(alias, pk, newPwd, ks.getCertificateChain(alias));
                        } catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ksInfo.setPassword(newPwd);
        // TODO:l create save file
        saveKeyStore(ks, ksInfo.getPath(), newPwd);
    }

    public void saveKeyStore(KeyStore ks, String path, char[] password) throws KeyToolsException {

        try {
            OutputStream fos = new FileOutputStream(new File(path));
            ks.store(fos, password);
            fos.close();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
        }
    }

    public void importX509Cert(String alias, KeyStoreValue ksin)
            throws KeyToolsException, FileNotFoundException, GeneralSecurityException {

        if (ksin.getStoreFormat().equals(StoreFormat.PKCS12)) {
            KeyStore ks = load(ksin);
            KeystoreBuilder ksBuilder = new KeystoreBuilder(ks);
            String aliasOri = null;
            Enumeration<String> enumKs = ks.aliases();
            while (enumKs.hasMoreElements()) {
                aliasOri = enumKs.nextElement();
            }

            Certificate cert = ks.getCertificate(aliasOri);
            CertificateValue certInfo = new CertificateValue(alias, (X509Certificate) cert, ksin.getPassword());

            certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
            certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, ksin.getPassword()));
            // addCertToKeyStore((X509Certificate)cert, ksInfo, certInfo);
            ksBuilder.addCertToKeyStoreNew((X509Certificate) cert, ksInfo, certInfo);
        } // TODO JKS

    }

    public ActionStatus importCertificates(KeyStoreValue ksin)
            throws FileNotFoundException, KeyToolsException, GeneralSecurityException {
        ksin.setStoreFormat(findTypeKS(ksin.getPath()));
        if (ksin.getPassword() == null && StoreFormat.PKCS12.equals(ksin.getStoreFormat())) {
            return ActionStatus.ASK_PASSWORD;
        }
        importX509Cert(null, ksin);
        return null;

    }

    /**
     * @param ksName
     * @param format
     * @param pwd
     * @return
     * @throws ServiceException
     * @Deprecated use ksinfo with service
     */
    @Deprecated
    public KeyStore getKeystore(String ksName, StoreFormat format, char[] pwd) throws ServiceException {


        try {
            return loadKeyStore(ksName, format, pwd).getKeystore();
        } catch (KeyToolsException e) {
            throw new ServiceException("can't open keystore" + ksName, e);
        }
    }

    public KeyStore getKeystore() throws ServiceException {


        try {
            return loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).getKeystore();
        } catch (KeyToolsException e) {
            throw new ServiceException("can't open keystore" + ksInfo.getPath(), e);
        }
    }

    public List<CertificateValue> getCertificates() throws ServiceException {
        List<CertificateValue> certs = new ArrayList<>();

        if (ksInfo.getPassword() == null && ksInfo.getStoreFormat().equals(StoreFormat.PKCS12)) {
            return certs;
        }

        KeyStore ks = getKeystore();

        log.trace("addcerts");
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
        return certs;

    }

    public List<CertificateValue> getCertificates(KeyStoreValue ki) throws ServiceException, KeyToolsException {
        List<CertificateValue> certs = new ArrayList<>();

        if (ki.getPassword() == null && ki.getStoreFormat().equals(StoreFormat.PKCS12)) {
            return certs;
        }

        KeyStore ks = loadKeyStore(ki.getPath(), ki.getStoreFormat(), null).getKeystore();

        log.trace("addcerts");
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
        return certs;

    }


    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.keystore.StoreService#getChildList()
     */
    @Override
    public List<CertificateValue> getChildList() throws ServiceException {
        // TODO Auto-generated method stub
        List<CertificateValue> certs = null;
        certs = getCertificates();
        return certs;
    }

    public void addCertToKeyStore(KeyStoreValue ksInfo, X509Certificate[] xCerts, CertificateValue certInfo, char[] password) throws ServiceException {


        try {
            KeyStore ks = load(ksInfo);
            new KeystoreBuilder(ks).addCert(xCerts, ksInfo, certInfo, password);
            saveKeyStore(ks, ksInfo.getPath(), password);
        } catch (KeyToolsException e) {
            throw new ServiceException(e);
        }
    }

    public void importX509Cert(String alias, String fileName, StoreFormat storeFormat, char[] charArray)
            throws ServiceException {
        KeystoreBuilder ksBuilder = null;
        if (storeFormat == null || storeFormat.PKCS12.equals(storeFormat)) {

            try {
                KeyStore ks = loadKeyStore(fileName, storeFormat, ksInfo.getPassword()).getKeystore();

                String aliasOri = null;
                Enumeration<String> enumKs = ks.aliases();
                while (enumKs.hasMoreElements()) {
                    aliasOri = enumKs.nextElement();
                }
                Certificate cert = ks.getCertificate(aliasOri);
                CertificateValue certInfo = new CertificateValue(alias, (X509Certificate) cert, charArray);

                certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
                certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, charArray));
                // addCertToKeyStore((X509Certificate)cert, ksInfo, certInfo);
                ksBuilder = new KeystoreBuilder(ks);
                ksBuilder.addCert((X509Certificate) cert, ksInfo, certInfo);
            } catch (KeyToolsException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (StoreFormat.JKS.equals(storeFormat)) {
            ;
            try (InputStream is = new FileInputStream(new File(fileName))) {

                CertificateBuilder cb = new CertificateBuilder();
                X509Certificate cert = cb.load(is).get();
                CertificateValue certInfo = new CertificateValue(alias, cert, charArray);

                ksBuilder.addCert(cert, ksInfo, certInfo);

            } catch (KeyToolsException | CertificateException | IOException e) {
                // TODO Auto-generated catch block
                throw new ServiceException(e);
            }
        }
    }

    public void removeCertificate(CertificateValue certificateInfo) throws KeyToolsException, KeyStoreException {
        new KeystoreBuilder(load(ksInfo)).removeCert(certificateInfo).save(ksInfo);

    }

    /**
     * Must be deleted because of CertificatValue constructor
     *
     * @param ks
     * @param alias
     * @return
     * @throws ServiceException
     */
    public CertificateValue fillCertInfo(KeyStore ks, String alias) throws ServiceException {
        CertificateValue certInfo = null;
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
                    log.info(message);
                // return null;
            } else {
                for (Certificate chainCert : certs) {
                    bf.append(chainCert.toString());
                }
                certInfo.setChaineStringValue(bf.toString());
                certInfo.setCertificateChain(certs);
            }

        } catch (KeyStoreException e) {
            throw new ServiceException("filling certificate Info impossible", e);
        }
        return certInfo;
    }

    public void exportPrivateKey(CertificateValue certInfo, char[] password, String fName) throws KeyToolsException {
        /* save the private key in a file */

        try {
            KeyStore ks = getKeystore();
            PrivateKey privateKey = null;
            if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), ksInfo.getPassword());
            } else {
                privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), password);
            }
            byte[] privKey = privateKey.getEncoded();
            FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"));
            keyfos.write(privKey);
            keyfos.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            throw new KeyToolsException("Export de la clé privée impossible:" + certInfo.getAlias(), e);
        }
    }

    public void exportPrivateKeyPEM(CertificateValue certInfo, KeyStoreValue ksInfo, char[] password, String fName)
            throws KeyToolsException {
        /* save the private key in a file */

        try {
            KeyStore ks = getKeystore();
            PrivateKey privateKey = null;
            if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), ksInfo.getPassword());
            } else {
                privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), password);
            }
            byte[] privKey = privateKey.getEncoded();

            List<String> lines = new ArrayList<>();
            lines.add(KeyTools.BEGIN_KEY);
            // FileUtils.writeLines(file, lines)
            File f = new File(fName + ".pem.key");
            // FileOutputStream keyfos = new FileOutputStream(new File(fName
            // + ".pem"));
            byte[] b = Base64.encodeBase64(privKey);
            String tmpString = new String(b);
            String[] datas = tmpString.split("(?<=\\G.{64})");
            Collections.addAll(lines, datas);

            lines.add(KeyTools.END_KEY);
            FileUtils.writeLines(f, lines);

            FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"));
            keyfos.write(privKey);
            keyfos.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            throw new KeyToolsException("Export de la clé privée impossible:" + certInfo.getAlias(), e);
        }
    }

    public KeyStore importStore(String path, StoreFormat storeFormat, char[] password) throws KeyToolsException,
            UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, ServiceException {
        // TODO Auto-generated method stub
        switch (storeFormat) {
            case JKS:
            case PKCS12:
                return getKeystore(path, storeFormat, password);

            default:
                CertificateUtils.loadX509Certs(path);
                return null;

        }
    }

    /**
     * @param certificate The certificate to add in keystore
     * @param password    keystore's password
     * @throws ServiceException
     */
    public void addCertToKeyStore(CertificateValue certificate, char[] password) throws ServiceException {
        if (StringUtils.isBlank(certificate.getAlias())) {
            BigInteger bi = KeyTools.RandomBI(30);
            certificate.setAlias(bi.toString(16));
        }

        try {
            KeyStore ks = load(ksInfo);
            KeystoreBuilder ksb = new KeystoreBuilder(ks);
            ksb.addCert(ksInfo, certificate, password);
        } catch (KeyToolsException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * @param certificate The certificate to add in keystore
     * @param password    keystore's password
     * @throws ServiceException
     */
    public void addCertToKeyStore(KeyStoreValue ki, CertificateValue certificate, char[] password) throws ServiceException {

        try {
            KeyStore ks = load(ki);
            KeystoreBuilder ksb = new KeystoreBuilder(ks);
            if (password == null)
                password = ki.getPassword();
            ksb.addCert(ki, certificate, password);
        } catch (KeyToolsException e) {
            throw new ServiceException(e);
        }

    }

    public CertificateValue findCertificateAndPrivateKeyByAlias(KeyStoreValue store, String alias) throws ServiceException {
        if (null == store || null == alias || alias.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return findCertificateByAlias(store, alias, store.getPassword());
    }

    public CertificateValue findCertificateByAlias(KeyStoreValue store, String alias, char[] password) throws ServiceException {
        if (null == store || null == alias || alias.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        CertificateValue certInfo = null;
        try {
            KeyStore ks = load(store);

            Certificate certificate = ks.getCertificate(alias);
            Certificate[] certs = ks.getCertificateChain(alias);
            certInfo = new CertificateValue(alias, (X509Certificate) certificate);
            if (ks.isKeyEntry(alias)) {
                certInfo.setContainsPrivateKey(true);
                if (password != null)
                    certInfo.setPrivateKey((PrivateKey) ks.getKey(alias, password));

            }
            StringBuilder bf = new StringBuilder();
            if (certs == null) {
                log.error("chaine de certification nulle pour" + alias + "(" + alias + ")");
                return null;
            }
            for (Certificate chainCert : certs) {
                bf.append(chainCert.toString());
            }
            certInfo.setChaineStringValue(bf.toString());
            certInfo.setCertificateChain(certs);

        } catch (KeyStoreException | KeyToolsException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
            throw new ServiceException(e);
        }
        return certInfo;
    }

    /**
     * @param ksName
     * @param format
     * @param pwd
     * @return
     * @throws KeyToolsException
     */
    public KeyStoreValue loadKeyStore(String ksName, StoreFormat format, char[] pwd) throws KeyToolsException {
        // KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        KeyStoreValue keystoreValue = new KeyStoreValue(new File(ksName), format, pwd);
        String type = StoreFormat.getValue(format);
        keystoreValue.setPassword(pwd);
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
        } catch (KeyStoreException e) {
            throw new KeyToolsException("Echec du chargement de:" + ksName, e);

        } catch (FileNotFoundException e) {
            throw new KeyToolsException("Fichier non trouvé:" + ksName + ", " + e.getCause(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyToolsException("Format inconnu:" + ksName + ", " + e.getCause(), e);
        } catch (CertificateException | IOException e) {
            throw new KeyToolsException("Echec du chargement de:" + ksName + ", " + e.getCause(), e);
        }
        KeyStore keystore = ks;
        keystoreValue.setKeystore(ks);
        return keystoreValue;

    }

    public KeyStore load(KeyStoreValue ksin) throws KeyToolsException {
        KeyStore keystore = loadKeyStore(ksin.getPath(), ksin.getStoreFormat(), ksin.getPassword()).getKeystore();

        return keystore;

    }

    public PrivateKey getPrivateKey(String alias, KeyStore keyStore, char[] motDePasse)
            throws GeneralSecurityException {
        //
        // PrivateKeyEntry pkEntry = (PrivateKeyEntry) keyStore.getEntry(alias,
        // new KeyStore.PasswordProtection(motDePasse));
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, motDePasse);
        if (privateKey != null) {
            return privateKey;
        } else {
            throw new GeneralSecurityException("Clé privée absente ");

        }
    }

    public PrivateKey getPrivateKey(KeyStoreValue ksInfoIn, String alias, char[] password) throws KeyToolsException, GeneralSecurityException {
        KeyStore kstore = loadKeyStore(ksInfoIn.getPath(), ksInfoIn.getStoreFormat(), ksInfoIn.getPassword()).getKeystore();
        return getPrivateKey(alias, kstore, password);
    }


}
