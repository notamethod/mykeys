package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CRLManager;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

public class CRLService {
    CRLManager manager;
    X509CRL crl;

    public CRLService(CertificateValue certificate) {
        manager = new CRLManager();
    }

    public X509CRL loadCRL(File f) {

        try (InputStream is = new FileInputStream(f)) {
            crl = manager.getCrl(is);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | CRLException | NoSuchProviderException | CertificateException e) {
            e.printStackTrace();
        }
        return crl;

    }

    public CRLManager.EtatCrl getValidity() {
        Date now = new Date();
        CRLManager.EtatCrl etatCrl;
        if (now.after(crl.getNextUpdate())) {
            etatCrl = CRLManager.EtatCrl.TO_UPDATE;
        } else {
            etatCrl = CRLManager.EtatCrl.UP_TO_DATE;
        }
        return etatCrl;
    }

    public List<X509Certificate> getChildren(CertificateValue certificate) {
        return null;
    }
}
