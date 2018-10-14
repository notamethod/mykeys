package org.dpr.mykeys.app.keystore;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.utils.ActionStatus;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class KeyStoreHelper implements StoreService<KeyStoreValue> {
    private static final Log log = LogFactory.getLog(KeyStoreHelper.class);

    private static final String[] KSTYPE_EXTS_PKCS12 = {"p12", "pfx", "pkcs12"};
    private static final String[] KSTYPE_EXTS_DER = {"der", "cer"};
    private static final String KSTYPE_EXT_PEM = "pem";
    public static final String KSTYPE_EXT_JKS = "jks";
    public static final String KSTYPE_EXT_P12 = "p12";

    public static final String MK3_SN = "D4 A0 81";
    private KeyStoreValue ksInfo;

    public KeyStoreHelper(KeyStoreValue ksInfo) {
        this.ksInfo = ksInfo;
    }

    public KeyStoreHelper() {
        super();

    }

    private static StoreFormat findTypeKStore(@NotNull String filename) {

        log.debug("finding type of file...");
        try {
            String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
            if (ext.equalsIgnoreCase(KSTYPE_EXT_JKS)) {
                return StoreFormat.JKS;
            }
            for (String aliasType : KSTYPE_EXTS_PKCS12) {
                if (ext.equalsIgnoreCase(aliasType)) {
                    return StoreFormat.PKCS12;
                }
            }
            for (String aliasType : KSTYPE_EXTS_DER) {
                if (ext.equalsIgnoreCase(aliasType)) {
                    return StoreFormat.DER;
                }
            }
            if (ext.equalsIgnoreCase(KSTYPE_EXT_PEM)) {
                return StoreFormat.PEM;
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    private static StoreFormat findKeystoreType(String filename) {

        StoreFormat format = findTypeKStore(filename);

        log.info("type of file is " + format);
        return format;

    }

    public void setKsInfo(KeyStoreValue ksInfo) {
        this.ksInfo = ksInfo;
    }

    public void open() throws ServiceException {


        loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword());


    }

    public void changePassword(KeyStoreValue ksInfo, char[] newPwd) throws TamperedWithException, KeyToolsException, ServiceException {

        KeyStore ks;
        try {
            ks = loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).getKeystore();
        } catch (ServiceException e) {
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
                            throw new ServiceException(e);
                        }

                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new ServiceException(e);
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

    private void importX509CertFromP12(String alias, KeyStoreValue ksin, char[] pwd)
            throws KeyToolsException, GeneralSecurityException, ServiceException {
        //TODO: use alias to get only one certificate
        //FIXME: but need to import all certitiftcates
        //TODO; check if alias exists in output ks
        KeyStore ks = load(ksin);

        String aliasOri = null;
        Enumeration<String> enumKs = ks.aliases();
        while (enumKs.hasMoreElements()) {
            aliasOri = enumKs.nextElement();
        }
        if (alias == null) {
            alias = aliasOri;
        }
        Certificate cert = ks.getCertificate(aliasOri);
        CertificateValue certInfo = new CertificateValue(alias, (X509Certificate) cert, ksin.getPassword());

        certInfo.setCertificateChain(ks.getCertificateChain(aliasOri));
        certInfo.setPrivateKey((PrivateKey) ks.getKey(aliasOri, ksin.getPassword()));

        KeystoreBuilder ksBuilder = new KeystoreBuilder(load(ksInfo));
        ksBuilder.addCertToKeyStoreNew((X509Certificate) cert, ksInfo, certInfo);
    }

    private void importX509Cert(String alias, KeyStoreValue ksin)
            throws KeyToolsException, GeneralSecurityException, ServiceException {

        importX509Cert(alias, ksin, null);

    }

    public ActionStatus importCertificates(KeyStoreValue ksin, char[] newPwd)
            throws ServiceException, GeneralSecurityException, KeyToolsException {
        ksin.setStoreFormat(findKeystoreType(ksin.getPath()));
        if (ksin.getPassword() == null && (StoreFormat.JKS.equals(ksin.getStoreFormat()) || StoreFormat.PKCS12.equals(ksin.getStoreFormat()))) {
            return ActionStatus.ASK_PASSWORD;
        }
        importX509Cert(null, ksin, newPwd);
        return null;

    }

    public ActionStatus importCertificates(KeyStoreValue ksin)
            throws ServiceException, GeneralSecurityException, KeyToolsException {
        ksin.setStoreFormat(findKeystoreType(ksin.getPath()));

        return importCertificates(ksin, null);

    }

    /**
     * @param ksName
     * @param format
     * @param pwd
     * @return
     * @throws ServiceException
     */

    private KeyStore getKeystore(String ksName, StoreFormat format, char[] pwd) throws ServiceException {

            return loadKeyStore(ksName, format, pwd).getKeystore();

    }

    public KeyStore getKeystore() throws ServiceException {


            return loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo.getPassword()).getKeystore();

    }

    private List<CertificateValue> getCertificates() throws ServiceException {
        List<CertificateValue> certs = new ArrayList<>();

        if (ksInfo.getPassword() == null && ksInfo.getStoreFormat().equals(StoreFormat.PKCS12)) {
            return certs;
        }

        KeyStore ks = getKeystore();


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


    public List<CertificateValue> getCertificates(KeyStoreValue ki) throws ServiceException {
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
        List<CertificateValue> certs;
        certs = getCertificates();
        return certs;
    }

    public void importX509Cert(String alias, KeyStoreValue value, char[] charArray)
            throws ServiceException {


        StoreFormat storeFormat = value.getStoreFormat();

        if (storeFormat == null || StoreFormat.PKCS12.equals(storeFormat)) {
            try {
                importX509CertFromP12(alias, value, charArray);
            } catch (KeyToolsException | GeneralSecurityException e) {
                // TODO Auto-generated catch block
                throw new ServiceException(e);
            }

        } else if (StoreFormat.JKS.equals(storeFormat)) {
            try {
                importX509CertFromJKS(alias, value, charArray);
            } catch (GeneralSecurityException e) {
                throw new ServiceException(e);
            }

        } else if (StoreFormat.DER.equals(storeFormat)) {
            importX509CertFromDer(alias, value, charArray);

        } else if (StoreFormat.PEM.equals(storeFormat)) {
            try {
                importX509CertFromPem(alias, value, charArray);
            } catch (IOException | GeneralSecurityException e) {
                throw new ServiceException(e);
            }
        }
    }

    public void importX509CertFromJKS(String alias0, KeyStoreValue value, char[] charArray)
            throws ServiceException, GeneralSecurityException {
        List<CertificateValue> certs = new ArrayList<>();

            KeyStore ks = load(value);


        Enumeration<String> enumKs;
        try {
            enumKs = ks.aliases();
            if (enumKs != null) {

                while (enumKs.hasMoreElements()) {
                    String alias = enumKs.nextElement();

                    Certificate cert = ks.getCertificate(alias);
                    CertificateValue certInfo = new CertificateValue(alias, (X509Certificate) cert, value.getPassword());

                    certInfo.setCertificateChain(ks.getCertificateChain(alias));
                    certInfo.setPrivateKey((PrivateKey) ks.getKey(alias, value.getPassword()));
                    if (charArray != null)
                        certInfo.setPassword(charArray);

                    KeystoreBuilder ksBuilder = new KeystoreBuilder(load(ksInfo));
                    ksBuilder.addCert(ksInfo, certInfo);
                    certs.add(certInfo);
                }
            }
        } catch (KeyStoreException | KeyToolsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void importX509CertFromDer(String alias, KeyStoreValue ksv, char[] charArray)
            throws ServiceException {

        List<CertificateValue> certs = CertificateUtils.loadX509Certs(ksv.getPath());

        for (CertificateValue certValue : certs) {
            addCertToKeyStore(ksInfo, certValue);

        }
    }

    public void importX509CertFromPem(String alias, KeyStoreValue ksv, char[] charArray)
            throws ServiceException, IOException, GeneralSecurityException {

        log.info("import x509 from pem file");
        BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(ksv.getPath())));

        //xCert = builder.generateFromCSR(buf, issuer).get();
        PemReader reader = new PemReader(buf);

        PemObject obj;

        while ((obj = reader.readPemObject()) != null) {
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC")
                    .getCertificate(new X509CertificateHolder(obj.getContent()));


            addCertToKeyStore(ksInfo, new CertificateValue(null, cert));
        }
    }


    public void removeCertificate(KeyStoreValue ksValue, CertificateValue certificateInfo) throws
            KeyToolsException, KeyStoreException {

        try {
            KeyStore ks = getKeystore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword());
            ks.deleteEntry(certificateInfo.getAlias());
            saveKeyStore(ks, ksValue.getPath(), ksValue.getPassword());

        } catch (ServiceException e) {
            e.printStackTrace();
        }

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

    public void exportPrivateKey(CertificateValue certInfo, char[] password, String fName) throws KeyToolsException {
        /* save the private key in a file */
        try {
            KeyStore ks = getKeystore();
            PrivateKey privateKey;
            if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                privateKey = (PrivateKey) ks.getKey(certInfo.getAlias(), password);
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
            PrivateKey privateKey;
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

    public KeyStore importStore(String path, StoreFormat storeFormat, char[] password) throws
            ServiceException {
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
     * @throws ServiceException
     */
    public void addCertToKeyStore(KeyStoreValue ki, CertificateValue certificate) throws ServiceException {

        addCertToKeyStore(ki, certificate, null, null);

    }

    /**
     * @param certificate The certificate to add in keystore
     * @param password    keystore's password
     * @throws ServiceException
     */
    private void addCertToKeyStore(KeyStoreValue ki, CertificateValue certificate, char[] password,
                                   char[] certificatePassword) throws ServiceException {

        try {
            KeyStore ks = load(ki);
            KeystoreBuilder ksb = new KeystoreBuilder(ks);
            if (password != null)
                ki.setPassword(password);
            if (certificatePassword != null)
                certificate.setPassword(certificatePassword);
            ksb.addCert(ki, certificate);
        } catch (KeyToolsException e) {
            throw new ServiceException(e);
        }
    }

    public CertificateValue findCertificateAndPrivateKeyByAlias(KeyStoreValue store, String alias) throws
            ServiceException {
        if (null == store || null == alias || alias.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return findCertificateByAlias(store, alias, store.getPassword());
    }

    public CertificateValue findCertificateByAlias(KeyStoreValue store, String alias, char[] password) throws
            ServiceException {
        if (null == store || null == alias || alias.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        CertificateValue certInfo;
        try {
            KeyStore ks = load(store);

            Certificate certificate = ks.getCertificate(alias);
            //mk3 is a special CA: let it different for now
            if (certificate != null && certificate instanceof X509Certificate) {
                String sn0 = X509Util.toHexString(((X509Certificate) certificate).getSerialNumber(), " ", true);
                if (MK3_SN.equals(sn0.trim())) {
                    password = store.getPassword();
                }

            }
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

        } catch (GeneralSecurityException e) {
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
    public KeyStoreValue loadKeyStore(String ksName, StoreFormat format, char[] pwd) throws ServiceException {
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
        KeyStore keystore = ks;
        keystoreValue.setKeystore(ks);
        return keystoreValue;
    }

    public KeyStore load(KeyStoreValue ksin) throws ServiceException {
        KeyStore keystore = loadKeyStore(ksin.getPath(), ksin.getStoreFormat(), ksin.getPassword()).getKeystore();

        return keystore;

    }

    private PrivateKey getPrivateKey(String alias, KeyStore keyStore, char[] motDePasse)
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

    public PrivateKey getPrivateKey(KeyStoreValue ksInfoIn, String alias, char[] password) throws
            KeyToolsException, GeneralSecurityException, ServiceException {
        KeyStore kstore = loadKeyStore(ksInfoIn.getPath(), ksInfoIn.getStoreFormat(), ksInfoIn.getPassword()).getKeystore();
        return getPrivateKey(alias, kstore, password);
    }


    public KeyStoreValue createKeyStoreValue(File ksFile) {
        StoreFormat format = findKeystoreType(ksFile.getAbsolutePath());
        KeyStoreValue ksv = new KeyStoreValue(ksFile, format, null);
        return ksv;
    }

    /**
     * check if keystore is protected by password
     *
     * @param ksv
     * @return
     */
    public boolean isPasswordProtected(KeyStoreValue ksv) {
        ksv.setStoreFormat(findKeystoreType(ksv.getPath()));
        if (StoreFormat.JKS.equals(ksv.getStoreFormat()) || StoreFormat.PKCS12.equals(ksv.getStoreFormat())) {
            return true;
        }
        return false;
    }

    public void exportDers(List<CertificateValue> certInfos, String fName) throws KeyToolsException {
        try {
            try (FileOutputStream keyfos = new FileOutputStream(new File(fName + ".der"))) {
                for (CertificateValue certInfo : certInfos) {
                    keyfos.write(certInfo.getCertificate().getEncoded());
                }
            }
        } catch (Exception e) {
            throw new KeyToolsException("Export de la clé publique impossible:", e);
        }
    }


    public void exportPems(List<CertificateValue> certInfos, String fName) throws KeyToolsException {
        /* save the public key in a file */
        try {
            List<String> lines = new ArrayList<>();
            for (CertificateValue certInfo : certInfos) {
                lines.add(KeyTools.BEGIN_PEM);
                // FileUtils.writeLines(file, lines)
                File f = new File(fName + ".pem");
                // FileOutputStream keyfos = new FileOutputStream(new File(fName
                // + ".pem"));
                byte[] b = Base64.encodeBase64(certInfo.getCertificate().getEncoded());
                String tmpString = new String(b);
                String[] datas = tmpString.split("(?<=\\G.{64})");
                Collections.addAll(lines, datas);

                lines.add(KeyTools.END_PEM);
                FileUtils.writeLines(f, lines);
            }

        } catch (Exception e) {

            throw new KeyToolsException("Export de la clé publique impossible:", e);
        }
    }
}
