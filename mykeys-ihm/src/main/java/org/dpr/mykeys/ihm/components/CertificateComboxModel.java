package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.certificate.CertificateValue;

import javax.swing.*;

public class CertificateComboxModel extends DefaultComboBoxModel implements ComboBoxModel {
    final CertificateValue[] values;
    String selection;


    public CertificateComboxModel(CertificateValue[] values) {
        super();
        this.values = values;
    }

    @Override
    public void setSelectedItem(Object anObject) {
        selection = ((CertificateValue) anObject).getName();

    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }

    public CertificateValue getTypedSelectedItem() {
        return (CertificateValue) super.getSelectedItem();
    }

    @Override
    public Object getElementAt(int index) {
        return values[index];
    }
}
