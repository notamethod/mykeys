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
import org.dpr.mykeys.app.certificate.CertificateValue;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PemKeystore implements MkKeystore {

    private static final Log log = LogFactory.getLog(PemKeystore.class);
    private final KeyStoreValue ksValue;

    public PemKeystore(KeyStoreValue ksValue) {
        this.ksValue = ksValue;
    }

    @Override
    public void removeCertificate(KeyStoreValue ksValue, CertificateValue certificateInfo) throws ServiceException {
        try {
            List<CertificateValue> certs = getCertificates();
            CertificateValue certToRemove = null;
            for (CertificateValue cert : certs) {
                if (certificateInfo.getName().equals(cert.getName())) {
                    certToRemove = cert;
                }
            }
            if (certToRemove != null)
                certs.remove(certToRemove);
            saveCertificates(certs);
        } catch (IOException | KeyToolsException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }

    }

    public List<CertificateValue> getCertificates()
            throws ServiceException, IOException, GeneralSecurityException {
        if (ksValue.getChildList() != null)
            return (List<CertificateValue>) ksValue.getChildList();
        else {
            List<CertificateValue> certs = new ArrayList<>();
            try (BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(ksValue.getPath())));) {

                PemReader reader = new PemReader(buf);

                PemObject obj;

                while ((obj = reader.readPemObject()) != null) {
                    X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC")
                            .getCertificate(new X509CertificateHolder(obj.getContent()));
                    CertificateValue certificate = new CertificateValue(null, cert);
                    certificate.setAlias(certificate.getName());
                    certs.add(certificate);
                }
            }
            ksValue.setChildList(certs);
            return certs;
        }
    }

    public void saveCertificates(List<CertificateValue> certInfos) throws KeyToolsException {
        /* save the public key in a file */
        try {
            List<String> lines = new ArrayList<>();
            for (CertificateValue certInfo : certInfos) {
                lines.add(KeyTools.BEGIN_PEM);
                // FileUtils.writeLines(file, lines)
                File f = new File(ksValue.getPath());

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

    public void savePrivateKey(PrivateKey privateKey, String fName)
            throws ServiceException {

        try {

            byte[] privKey = privateKey.getEncoded();

            List<String> lines = new ArrayList<>();
            lines.add(KeyTools.BEGIN_KEY);
            File f = new File(fName + ".pem.key");

            byte[] b = Base64.encodeBase64(privKey);
            String tmpString = new String(b);
            String[] datas = tmpString.split("(?<=\\G.{64})");
            Collections.addAll(lines, datas);

            lines.add(KeyTools.END_KEY);
            FileUtils.writeLines(f, lines);
// binary ?
//            try (FileOutputStream keyfos = new FileOutputStream(new File(fName + ".key"));) {
//                keyfos.write(privKey);
//            }

        } catch (Exception e) {
            throw new ServiceException("Fail to export private key", e);
        }
    }
}
