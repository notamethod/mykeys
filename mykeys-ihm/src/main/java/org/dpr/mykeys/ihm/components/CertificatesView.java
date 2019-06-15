package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.certificate.CertificateValue;

import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.EventListener;
import java.util.List;

public interface CertificatesView {
    Component getListCerts();

    void addListener(EventListener listListener);

    void clear();

    IModelFactory getModel();

    void makeVisible(boolean b);

    CertificateValue getSelected();

    List getSelectedList();

    void sort();
}
