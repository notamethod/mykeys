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
import org.dpr.mykeys.app.keystore.PEMType;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
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
    public void save(KeyStoreValue ksValue, KeyStoreHelper.SAVE_OPTION option) throws RepositoryException {
        File f = new File(ksValue.getPath());
        if (f.exists() && option.equals(KeyStoreHelper.SAVE_OPTION.NONE)) {
            throw new EntityAlreadyExistsException("File already exists " + f.getAbsolutePath());
        }
        /* save the public key in a file */
        try (FileOutputStream fout = new FileOutputStream(f)){
            for (CertificateValue certInfo : ksValue.getCertificates()) {
                byte[] b = certInfo.getCertificate().getEncoded();
                saveXxx(b, fout, PEMType.CERTIFICATE);
            }

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
            saveXxx(privKey, f, PEMType.PRIVATE_KEY);

        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }

    public void saveCSR(byte[] b, File f, KeyStoreHelper.SAVE_OPTION option) throws ServiceException {

        try(FileOutputStream fout = new FileOutputStream(f)) {
            saveXxx(b, fout, PEMType.REQUEST);
        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }

    public void saveXxx(byte[] encoded, OutputStream os, PEMType pemType) throws IOException {

            byte[] base64Encoded = Base64.encodeBase64(encoded);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            List<String> lines = new ArrayList<>();
            lines.add(pemType.Begin());

            String tmpString = new String(base64Encoded);
            String[] datas = tmpString.split("(?<=\\G.{64})");
            Collections.addAll(lines, datas);

            lines.add(pemType.End());
            for (String line: lines){
                osw.write(line);
            }

    }
    @Override
    public void saveCertificates(KeyStoreValue ksValue, List<CertificateValue> certInfos) {

    }
}
