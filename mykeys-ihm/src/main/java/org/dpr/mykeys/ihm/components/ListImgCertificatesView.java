package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.windows.ListCertRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.EventListener;
import java.util.List;

public class ListImgCertificatesView extends Component implements CertificatesView {

    private JImgList listCerts;

    @Override
    public Component getListCerts() {
        return listCerts;
    }

    private IModelFactory model;
    private DefaultListModel listModel;
    //private ListSelectionListener listListener;

    public ListImgCertificatesView() {
        listModel = new DefaultListModel();

        model = new IModelFactory() {
            @Override
            public void removeAllElements() {
                listModel.removeAllElements();
            }

            @Override
            public void addElement(ChildInfo ci) {
                listModel.addElement(ci);
            }

            @Override
            public void refresh() {

            }
        };

        listCerts = new JImgList(listModel);
        init();

    }

    private void init() {


        ListCertRenderer renderer = new ListCertRenderer();
        listCerts.setCellRenderer(renderer);
        listCerts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        listCerts.setVisibleRowCount(-1);
        listCerts.setDragEnabled(true);
    }

    @Override
    public void addListener(EventListener listListener) {
        // this.listListener = listListener;
        if (listListener instanceof ListSelectionListener)
            listCerts.addListSelectionListener((ListSelectionListener) listListener);
    }

    @Override
    public void clear() {
        listCerts.clearSelection();
        model.removeAllElements();
    }

    @Override
    public IModelFactory getModel() {
        return model;
    }

    @Override
    public void makeVisible(boolean b) {
        listCerts.setShowImage(b);
    }

    @Override
    public CertificateValue getSelected() {
        return (CertificateValue) listCerts.getSelectedValue();
    }

    @Override
    public List getSelectedList() {
        return listCerts.getSelectedValuesList();
    }
}
