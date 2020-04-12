package org.dpr.mykeys.app.keystore.repository;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.keystore.PEMType;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

class PemKeystoreRepository extends KeystoreRepository {


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
    public void save(KeyStoreValue ksValue, SAVE_OPTION option) throws RepositoryException {
        File f = new File(ksValue.getPath());
        if (f.exists() && option.equals(SAVE_OPTION.NONE)) {
            throw new EntityAlreadyExistsException("File already exists " + f.getAbsolutePath());
        }
        /* save the public key in a file */
        try (FileOutputStream fout = new FileOutputStream(f)){
            List<byte[]> encodedList = new ArrayList<>();
            for (CertificateValue certInfo : ksValue.getCertificates()) {
                encodedList.add(certInfo.getCertificate().getEncoded());
            }
            saveBytes(encodedList, fout, PEMType.CERTIFICATE);
        } catch (Exception e) {

            throw new RepositoryException("Export de la clé publique impossible:", e);
        }
    }

    public void savePrivateKey(PrivateKey privateKey, String fName, char[] pass)
            throws ServiceException {
         savePrivateKey(privateKey, fName);
    }
    public void savePrivateKey(PrivateKey privateKey, String fName)
            throws ServiceException {
        try(FileOutputStream f = new FileOutputStream(fName + ".pem.key")) {
            byte[] privKey = privateKey.getEncoded();
            saveBytes(privKey, f, PEMType.PRIVATE_KEY);

        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }

    public void exportPrivateKey(PrivateKey privateKey, OutputStream os, char[] pass)
            throws ServiceException {
        try{
            byte[] privKey = privateKey.getEncoded();
            saveBytes(privKey, os, PEMType.PRIVATE_KEY);

        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }

    public void saveCSR(byte[] b, File f, SAVE_OPTION option) throws ServiceException {

        try(FileOutputStream fout = new FileOutputStream(f)) {
            saveBytes(b, fout, PEMType.REQUEST);
        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }
    public void saveCSR(byte[] b, OutputStream os, SAVE_OPTION option) throws ServiceException {

        try {
            saveBytes(b, os, PEMType.REQUEST);
        } catch (IOException e) {
            throw new ServiceException("Fail to export csr", e);
        }

    }
    public void saveBytes(byte[] encoded, OutputStream os, PEMType pemType) throws IOException {
        List<byte[]> encodedList = new ArrayList<>();
        encodedList.add(encoded);
        saveBytes(encodedList, os, pemType);
    }


    public void saveBytes(List<byte[]> encodedObjects, OutputStream os, PEMType pemType) throws IOException {

        PrintWriter  osw = new PrintWriter (os);
            for (byte[] encoded : encodedObjects){
                byte[] base64Encoded = Base64.encodeBase64(encoded);
                osw.println(pemType.Begin());
                String[] datas = new String(base64Encoded).split("(?<=\\G.{64})");
                for (String line: datas){
                    osw.println(line);
                }
                osw.print(pemType.End());
            }
        osw.close();
    }
    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) {

    }
}
