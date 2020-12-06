package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.certificate.Certificate;

import java.util.EventListener;

public interface CertificateActionListener extends EventListener {

    void openStoreRequested(String what);

    void insertCertificateRequested(Certificate what);

    void insertCertificateFromProfileRequested(String what);

    void insertCertificateFromCSRRequested(String what);

    void importCertificateRequested(String what);

    void exportCertificateRequested(String what);

    void deleteCertificateRequested(String what);

    void createCrlRequested(String what);

    void insertCertificateACRequested(String s);
}
