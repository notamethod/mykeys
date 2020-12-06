package org.dpr.mykeys.ihm;


import org.dpr.mykeys.app.certificate.MkCertificate;

public interface IModelFactory {

    void removeAllElements();

    void addElement(MkCertificate ci);

    void refresh();
}
