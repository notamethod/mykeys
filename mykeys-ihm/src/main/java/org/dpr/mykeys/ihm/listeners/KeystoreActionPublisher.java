package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.certificate.CertificateValue;

public interface KeystoreActionPublisher {
    void notifyopenStore(String what);

    void notifyInsertCertificate(CertificateValue what);

    void notifyInsertCertificateFromProfile(String what);

    void notifyInsertCertificateFromCSR(String what);

    void registerListener(CertificateActionListener listener);
}
