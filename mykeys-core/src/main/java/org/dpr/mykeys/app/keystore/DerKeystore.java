package org.dpr.mykeys.app.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class DerKeystore extends ClassicKeystore implements MkKeystore {

    private static final Log log = LogFactory.getLog(DerKeystore.class);


    public DerKeystore() {
    }


    @Override
    public void savePrivateKey(PrivateKey privateKey, String fName, char[] pass) throws ServiceException {
        try {

            byte[] privKey = privateKey.getEncoded();

// binary ?
            try (FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"))) {
                keyfos.write(privKey);
            }

        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }

    }

    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) throws ServiceException {

    }

    @Override
    public void save(KeyStoreValue ksValue) throws ServiceException {
        try {
            try (FileOutputStream keyfos = new FileOutputStream(new File(ksValue.getPath() + ".der"))) {
                for (CertificateValue certInfo : ksValue.getCertificates()) {
                    keyfos.write(certInfo.getCertificate().getEncoded());
                }
            }
        } catch (Exception e) {
            throw new ServiceException("Export de la clé publique impossible:", e);
        }
    }

    @Override
    public List<CertificateValue> getCertificates(KeyStoreValue ksValue) throws ServiceException {
        if (ksValue.getCertificates() != null && !ksValue.getCertificates().isEmpty())
            return ksValue.getChildList();

        List<CertificateValue> certsRetour = new ArrayList<>();
        //  InputStream is = null;
        try (InputStream is = new FileInputStream(new File(ksValue.getPath()))) {
            //  is = new FileInputStream(new File(ksValue.getPath()));

            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

            // chargement du certificat
            Collection<X509Certificate> certs = (Collection<X509Certificate>) cf.generateCertificates(is);
            Set<X509Certificate> certificates = new HashSet<>(certs);
            for (X509Certificate cert : certs) {
                CertificateValue certInfo = new CertificateValue(null, cert);

                certsRetour.add(certInfo);
            }

        } catch (GeneralSecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ksValue.setCertificates(certsRetour);
        return certsRetour;
    }

    @Override
    public void addCert(KeyStoreValue ki, CertificateValue certificate) throws ServiceException {

    }
}
