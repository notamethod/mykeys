package org.dpr.mykeys.app.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.utils.KeystoreUtils;
import org.dpr.mykeys.utils.X509Util;
import org.dpr.mykeys.utils.CertificateUtils;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.utils.ActionStatus;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class KeyStoreHelper implements StoreService<KeyStoreValue> {
    private static final Log log = LogFactory.getLog(KeyStoreHelper.class);

    public static final String MK3_SN = "D4 A0 81";
    private KeyStoreValue ksInfo;

    public KeyStoreHelper(KeyStoreValue ksInfo) {
        this.ksInfo = ksInfo;
    }

    public KeyStoreHelper() {
        super();

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
        ksBuilder.addCertToKeyStoreNew(ksInfo, certInfo);
    }

    public ActionStatus importCertificates(KeyStoreValue ksin, char[] newPwd)
            throws ServiceException, GeneralSecurityException, KeyToolsException {
        ksin.setStoreFormat(KeystoreUtils.findKeystoreType(ksin.getPath()));
        if (ksin.getPassword() == null && (StoreFormat.JKS.equals(ksin.getStoreFormat()) || StoreFormat.PKCS12.equals(ksin.getStoreFormat()))) {
            return ActionStatus.ASK_PASSWORD;
        }
        importX509CertToJks(null, ksin, newPwd);
        return null;

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

    public List<CertificateValue> getCertificates() throws ServiceException {
        List<CertificateValue> certs = new ArrayList<>();

        if (ksInfo.getPassword() == null && ksInfo.getStoreFormat().equals(StoreFormat.PKCS12)) {
            return certs;
        }
        if (ksInfo.getStoreFormat().equals(StoreFormat.UNKNOWN))
            ksInfo.setStoreFormat(KeystoreUtils.findKeystoreType(ksInfo.getPath()));

        MkKeystore mks = MkKeystore.getInstance(ksInfo.getStoreFormat());
        // ksInfo.setOpen(true);
        //   certs = mks.getCertificates(ksInfo);
        switch (ksInfo.getStoreFormat()) {
            case DER:
            case PEM:
                certs = mks.getCertificates(ksInfo);
                    ksInfo.setOpen(true);
                return certs;
            case JKS:
                certs = mks.getCertificates(ksInfo);
                return certs;
            default:
                return null;
        }

//        KeyStore ks = getKeystore();
//
//
//        Enumeration<String> enumKs;
//        try {
//            enumKs = ks.aliases();
//            if (enumKs != null && enumKs.hasMoreElements()) {
//
//                while (enumKs.hasMoreElements()) {
//                    String alias = enumKs.nextElement();
//
//                    CertificateValue certInfo = fillCertInfo(ks, alias);
//                    certs.add(certInfo);
//                }
//            }
//        } catch (KeyStoreException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return certs;
    }


    public List<CertificateValue> getCertificatesForUser(KeyStoreValue ki) throws ServiceException {
        List<CertificateValue> certs = new ArrayList<>();

        if (ki.getPassword() == null && ki.getStoreFormat().equals(StoreFormat.PKCS12)) {
            return certs;
        }

        MkKeystore mks = MkKeystore.getInstance(ki.getStoreFormat());
        return mks.getCertificates(ki);
    }


    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.keystore.StoreService#getChildList()
     */
    @Override
    public List<CertificateValue> getChildList() throws ServiceException {
        log.debug("get child list");
        List<CertificateValue> certs;
        certs = getCertificates();
        return certs;
    }

    public void importX509CertToJks(String alias, KeyStoreValue value, char[] charArray)
            throws ServiceException {

        MkKeystore mks = MkKeystore.getInstance(value.getStoreFormat());
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

        } else if (StoreFormat.DER.equals(storeFormat) || StoreFormat.PEM.equals(storeFormat)) {

            List<CertificateValue> certs = mks.getCertificates(value);
                addCertsToKeyStore(ksInfo, certs);

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

        addCertsToKeyStore(ksInfo, certs);

    }



    @Deprecated
    /**
     */
    public void removeCertificates(KeyStoreValue ksValue, List<CertificateValue> certificatesInfo) throws
            KeyToolsException, KeyStoreException {
        MkKeystore mks = MkKeystore.getInstance(ksValue.getStoreFormat());

        try {
            mks.removeCertificates(ksValue, certificatesInfo);
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


    public void exportPrivateKey(CertificateValue certInfo, KeyStoreValue ksInfo, char[] passwordIn, char[] passwordOut, String fName, StoreFormat format)
            throws KeyToolsException {

        try {

            PrivateKey privateKey = getPrivateKey(ksInfo, certInfo.getAlias(), passwordIn);
            KeyStoreValue ksout = new KeyStoreValue(fName, format);
            MkKeystore mks = MkKeystore.getInstance(ksout.getStoreFormat());
            mks.savePrivateKey(privateKey, fName, passwordOut);

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
     * @throws ServiceException
     */
    private void addCertsToKeyStore(KeyStoreValue ki, List<CertificateValue> certificates) throws ServiceException {

        try {
            KeyStore ks = load(ki);
            KeystoreBuilder ksb = new KeystoreBuilder(ks);

            ksb.addCerts(ki, certificates);
        } catch (KeyToolsException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @param certificate The certificate to add in keystore
     * @param password    keystore's password
     * @throws ServiceException
     */
    public void addCertToKeyStore(KeyStoreValue ki, CertificateValue certificate, char[] password,
                                  char[] certificatePassword) throws ServiceException {
            if (password != null)
                ki.setPassword(password);
            if (certificatePassword != null)
                certificate.setPassword(certificatePassword);
        MkKeystore mks = MkKeystore.getInstance(ki.getStoreFormat());
        mks.addCert(ki, certificate);
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
            if (certificate instanceof X509Certificate) {
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


    public PrivateKey getPrivateKey(KeyStoreValue ksInfoIn, String alias, char[] password) throws
            KeyToolsException, GeneralSecurityException, ServiceException {
        KeyStore keyStore = loadKeyStore(ksInfoIn.getPath(), ksInfoIn.getStoreFormat(), ksInfoIn.getPassword()).getKeystore();

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
        if (privateKey != null) {
            return privateKey;
        } else {
            throw new GeneralSecurityException("Clé privée absente ");

        }
    }


    public KeyStoreValue createKeyStoreValue(File ksFile) {
        StoreFormat format = KeystoreUtils.findKeystoreType(ksFile.getAbsolutePath());
        KeyStoreValue ksv = new KeyStoreValue(ksFile, format, null);
        return ksv;
    }


    public Map<String, String> getMapStringCerts(KeyStoreValue ksv) {
        MkKeystore mks = MkKeystore.getInstance(ksv.getStoreFormat());
        Map<String, String> certsAC = new HashMap<>();
        try {
            for (CertificateValue cv : mks.getCertificates(ksv)) {
                certsAC.put(cv.getName(), cv.getAlias());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return certsAC;

    }

    public void export(List<CertificateValue> certInfos, String fName, StoreFormat format, char[] pwd) throws KeyToolsException {
        /* save the public key in a file */

        try {
            KeyStoreValue ksv = new KeyStoreValue(fName, format);
            if (pwd != null)
                ksv.setPassword(pwd);
            ksv.setCertificates(certInfos);
            MkKeystore mks = MkKeystore.getInstance(format);
            mks.save(ksv);


        } catch (Exception e) {

            throw new KeyToolsException("Export de la clé publique impossible:", e);
        }
    }
}
