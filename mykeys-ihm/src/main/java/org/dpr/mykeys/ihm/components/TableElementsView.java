package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.certificate.MkCertificate;
import org.dpr.mykeys.ihm.CertificatesView;
import org.dpr.mykeys.ihm.IModelFactory;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
//        TableColumnModel columnModel = table.getColumnModel();
//        columnModel.getColumn(0).setWidth(50);
//        columnModel.getColumn(0).setPreferredWidth(50);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                tableModel.getRow(table.getSelectedRow()).ifPresent(certificate -> {
                            for (EventCompListener listener : listeners) {
                                listener.certificateSelected(certificate);
                            }
                        }
                );
            }
        });
        resizeColumns();
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
        table.clearSelection();
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
        return tableModel.getRow(table.getSelectedRow()).orElse(null);

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


    private void init() {

    }

    private void resizeColumns() {
        float[] columnWidthPercentage = {0.05f, 0.55f, 0.1f, 0.05f, 0.05f};

        // Use TableColumnModel.getTotalColumnWidth() if your table is included in a JScrollPane
        TableColumnModel columnModel = table.getColumnModel();
        int tW = columnModel.getTotalColumnWidth();
        TableColumn column;

        int cantCols = columnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++) {
            column = columnModel.getColumn(i);
            int pWidth = Math.round(columnWidthPercentage[i] * tW);
            column.setPreferredWidth(pWidth);
        }
    }
}
