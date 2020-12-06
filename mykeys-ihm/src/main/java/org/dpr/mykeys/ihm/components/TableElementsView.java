package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.certificate.MkCertificate;
import org.dpr.mykeys.ihm.CertificatesView;
import org.dpr.mykeys.ihm.IModelFactory;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class TableElementsView extends Component implements CertificatesView {

    private final JTable table;
    private final CryptoElementModel tableModel;
    private final IModelFactory model;
    private final List<EventCompListener> listeners = new ArrayList<>();

    public TableElementsView() {
        tableModel = new CryptoElementModel();


        model = new IModelFactory() {
            @Override
            public void removeAllElements() {
                tableModel.clear();
            }

            @Override
            public void addElement(MkCertificate ci) {
                tableModel.addRow((Certificate) ci);
            }

            @Override
            public void refresh() {

            }
        };

        table = new JTable(tableModel);
        init();
    }

    @Override
    public Component getListCerts() {
        return table;
    }

    @Override
    public void addListener(EventListener listListener) {
        registerListener((EventCompListener) listListener);
    }

    @Override
    public void clear() {
        model.removeAllElements();

    }

    @Override
    public IModelFactory getModel() {
        return model;
    }

    @Override
    public void makeVisible(boolean b) {

    }

    @Override
    public Certificate getSelected() {
        return tableModel.getRow(table.getSelectedRow());
    }

    @Override
    public List getSelectedList() {
        return tableModel.getRows(table.getSelectedRows());

    }

    @Override
    public void sort() {

    }

    public void registerListener(EventCompListener listener) {
        listeners.add(listener);
    }


    private void init(){
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                System.out.println(tableModel.getRow(table.getSelectedRow()).toString());
                for (EventCompListener listener:listeners){
                    listener.certificateSelected(tableModel.getRow(table.getSelectedRow()));
                }
            }
        });
    }
}
