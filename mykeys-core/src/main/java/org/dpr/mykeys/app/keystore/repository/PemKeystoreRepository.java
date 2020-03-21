package org.dpr.mykeys.app.keystore.repository;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PemKeystoreRepository extends KeystoreRepository {

    public static final String BEGIN_PEM = "-----BEGIN CERTIFICATE-----";
    public static final String END_PEM = "-----END CERTIFICATE-----";
    public static final String BEGIN_KEY = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String END_KEY = "-----END RSA PRIVATE KEY-----";
    private static final Log log = LogFactory.getLog(PemKeystoreRepository.class);


    public PemKeystoreRepository() {
    }


    public List<CertificateValue> getCertificates(KeyStoreValue ksValue)
            throws ServiceException {
        if (ksValue.getCertificates() != null && !ksValue.getCertificates().isEmpty())
            return ksValue.getChildList();
        else {
            List<CertificateValue> certs = new ArrayList<>();
            try (BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(ksValue.getPath())))) {

                PemReader reader = new PemReader(buf);

                PemObject obj;

                while ((obj = reader.readPemObject()) != null) {
                    X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC")
                            .getCertificate(new X509CertificateHolder(obj.getContent()));
                    CertificateValue certificate = new CertificateValue(null, cert);
                    certificate.setAlias(certificate.getName());
                    certs.add(certificate);
                }
            } catch (GeneralSecurityException | IOException e) {
                throw new ServiceException(e);
            }
            ksValue.setCertificates(certs);
            return certs;
        }
    }

    @Override
    public void addCert(KeyStoreValue ki, CertificateValue certificate) {

    }

    @Override
    public void save(KeyStoreValue ksValue, KeyStoreHelper.SAVE_OPTION option) throws RepositoryException {
        File f = new File(ksValue.getPath());
        if (f.exists() && option.equals(KeyStoreHelper.SAVE_OPTION.NONE)) {
            throw new EntityAlreadyExistsException("File already exists " + f.getAbsolutePath());
        }
        /* save the public key in a file */
        try {
            List<String> lines = new ArrayList<>();
            for (CertificateValue certInfo : ksValue.getCertificates()) {
                lines.add(BEGIN_PEM);
                // FileUtils.writeLines(file, lines)

                byte[] b = Base64.encodeBase64(certInfo.getCertificate().getEncoded());
                String tmpString = new String(b);
                String[] datas = tmpString.split("(?<=\\G.{64})");
                Collections.addAll(lines, datas);

                lines.add(END_PEM);
                FileUtils.writeLines(f, lines);
            }

        } catch (Exception e) {

            throw new RepositoryException("Export de la clé publique impossible:", e);
        }
    }

    public void savePrivateKey(PrivateKey privateKey, String fName, char[] pass)
            throws ServiceException {

        try {

            byte[] privKey = privateKey.getEncoded();

            List<String> lines = new ArrayList<>();
            lines.add(BEGIN_KEY);
            File f = new File(fName + ".pem.key");

            byte[] b = Base64.encodeBase64(privKey);
            String tmpString = new String(b);
            String[] datas = tmpString.split("(?<=\\G.{64})");
            Collections.addAll(lines, datas);

            lines.add(END_KEY);
            FileUtils.writeLines(f, lines);
// binary ?
//            try (FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"));) {
//                keyfos.write(privKey);
//            }

        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }

    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) {

    }
}
