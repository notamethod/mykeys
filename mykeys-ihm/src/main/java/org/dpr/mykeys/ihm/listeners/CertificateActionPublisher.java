package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.certificate.CertificateValue;

public interface CertificateActionPublisher {

    void notifyopenStore(String what);

    void notifyInsertCertificate(CertificateValue what);

    void notifyInsertCertificateFromProfile(String what);

    void notifyInsertCertificateFromCSR(String what);

    void notifyImportCertificate(String what);

    void notifyExportCertificate(String what);

    void notifyCertificateDeletion(String what);

    void notifyCreateCrl(String what);

    void registerListener(CertificateActionListener listener);

}
