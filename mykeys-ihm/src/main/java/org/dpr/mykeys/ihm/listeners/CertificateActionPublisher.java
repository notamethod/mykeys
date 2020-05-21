package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.certificate.CertificateValue;

public interface CertificateActionPublisher extends KeystoreActionPublisher{

    void notifyImportCertificate(String what);

    void notifyExportCertificate(String what);

    void notifyCertificateDeletion(String what);

    void notifyCreateCrl(String what);


}
