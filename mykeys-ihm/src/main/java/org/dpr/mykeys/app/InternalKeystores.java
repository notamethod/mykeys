
package org.dpr.mykeys.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import org.dpr.mykeys.utils.MkUtils;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

/**
 * Helpers for internal keystores.
 * Since this version, keystores are "protected" by the same pad as previous version
 * but private keys are protected by user pad.
 */
public class InternalKeystores {

    private static final Log log = LogFactory.getLog(InternalKeystores.class);
    private static final String pad = "mKeys983178";
    public static final String MK1_PD = "mKeys983178";
    private static final String USERDB = "userDB.jks";
    private static String cfgPath;
    public static final String MK1_STORE_AC = "mykeysAc.jks";
    public static final String MK1_STORE_CERT = "mykeysCert.jks";

    private KeyStoreValue storeAC;


    public InternalKeystores(String cfgPath, String profilsPath) {

        InternalKeystores.cfgPath = cfgPath;
        this.pathProfils = profilsPath;
        this.pathUDB = cfgPath + USERDB;
    }

    String generateName(StoreModel storeModel, boolean withExtension) {
        if (MkSession.user == null)
            throw new IllegalArgumentException("session is empty");
        String hdigest = new DigestUtils(SHA_256).digestAsHex((MkSession.user + storeModel.toString()).getBytes());
        if (withExtension)
            hdigest += ".jks";
        return hdigest;

    }

    public String getPassword() {
        return pad;
    }

    private String pathAC;
    private String pathCert;
    private final String pathProfils;
    private final String pathUDB;
    private String pathPKI;


    public String getACPath() {

        return pathAC;
    }

    public String getCertPath() {

        return pathCert;
    }

    public String getPKIPath() {

        return pathPKI;
    }

    public String getProfilsPath() {

        return pathProfils;
    }

    public KeyStoreValue getStoreAC() {

        KeyStoreValue kinfo;
        File f = new File(pathAC);
        if (!f.exists()) {

            try {

                InputStream is = (InternalKeystores.class.getResourceAsStream("/install/mykeysAc.jks"));
                MkUtils.copyFile(is, f);

            } catch (Exception e) {
                log.error(e);
            }

        }
        kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathAC, StoreModel.CASTORE,
                StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }

    public KeyStoreValue getStorePKI() {

        KeyStoreValue kinfo;
        File f = new File(pathPKI);
        if (!f.exists()) {

            try {

                InputStream is = (InternalKeystores.class.getResourceAsStream("/install/mykeysAc.jks"));
                MkUtils.copyFile(is, f);
                // InternalKeystores.class.getResource("/org.dpr.mykeys/config/myKeysAc.jks").getFile()getChannel();

            } catch (Exception e) {
                log.error(e);
            }
        }
        kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathPKI, StoreModel.PKISTORE,
                StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }

    public boolean existsUserDatabase() {

        File f = new File(pathUDB);
        return f.exists();
    }

    public boolean existsCertDatabase() {

        File f = new File(pathCert);
        return f.exists();
    }

    public boolean existsACDatabase() {

        File f = new File(pathAC);
        return f.exists();
    }

    public boolean existsPKIDatabase() {

        File f = new File(pathPKI);
        return f.exists();
    }

    public boolean existsProfilDatabase() {

        File f = new File(pathProfils);
        return f.exists();
    }

    public KeyStoreValue getUserDB() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);

        KeyStoreValue kinfo;
        File f = new File(pathUDB);
        if (!existsUserDatabase()) {

            ksBuilder.create(pathUDB, pad.toCharArray());

        }
        kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathUDB, StoreModel.CERTSTORE,
                StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }

    public void createUserDB() throws Exception {
        KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
        KeyStoreValue kinfo;
        new File(pathUDB);
        if (!existsUserDatabase()) {
            try {
                ksBuilder.create(pathUDB, pad.toCharArray());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathUDB, StoreModel.CERTSTORE,
                StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
        kinfo.setOpen(true);

    }

    public KeyStoreValue getStoreCertificate() throws KeyStoreException {
        KeystoreBuilder ksBuilder = new KeystoreBuilder(StoreFormat.JKS);
        KeyStoreValue kinfo;
        File f = new File(pathCert);
        // create keystore
        if (!f.exists()) {
            try {
                ksBuilder.create(pathCert, pad.toCharArray());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        kinfo = new KeyStoreValue(Messages.getString("magasin.interne"), pathCert, StoreModel.CERTSTORE,
                StoreFormat.JKS, StoreLocationType.INTERNAL);
        kinfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }

    public ProfilStoreInfo getStoreProfils() {

        ProfilStoreInfo kinfo;
        File f = new File(pathProfils);
        if (!f.exists()) {
            f.mkdirs();

        }
        kinfo = new ProfilStoreInfo(Messages.getString("certificateTemplate.name"), pathProfils,
                StoreFormat.PROPERTIES);
        kinfo.setPassword("null".toCharArray());
        kinfo.setOpen(true);
        return kinfo;
    }

    public KeyStoreValue getKeystoreInfo() {
        return new KeyStoreValue(new File(pathAC), StoreFormat.JKS,
                KSConfig.getInternalKeystores().getPassword().toCharArray());
    }

    public void init() {
        if (null == pathAC) {
            this.pathAC = cfgPath + generateName(StoreModel.CASTORE, true);
        }
        if (null == pathCert) {
            this.pathCert = cfgPath + generateName(StoreModel.CERTSTORE, true);
        }
        if (null == pathPKI) {
            this.pathPKI = cfgPath + generateName(StoreModel.PKISTORE, true);
        }

    }
}
