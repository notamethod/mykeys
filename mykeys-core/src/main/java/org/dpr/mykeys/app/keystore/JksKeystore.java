package org.dpr.mykeys.app.keystore;

import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;

public class JksKeystore implements MkKeystore {

    public JksKeystore(KeyStoreValue ksValue) {
    }

    @Override
    public void removeCertificate(KeyStoreValue ksValue, CertificateValue certificateInfo) throws ServiceException {

        try {

            if (null == ksValue.getKeystore()) {

                ksValue.setKeystore(loadKeyStore(ksValue.getPath(), ksValue.getStoreFormat(), ksValue.getPassword()));
            }
            ksValue.getKeystore().deleteEntry(certificateInfo.getAlias());
            saveKeyStore(ksValue.getKeystore(), ksValue.getPath(), ksValue.getPassword());

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void savePrivateKey(PrivateKey privateKey, String fName) throws ServiceException {

    }

    public void saveKeyStore(KeyStore ks, String path, char[] password) throws KeyToolsException {

        try {
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


}
