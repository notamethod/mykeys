package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.certificate.MkCertificate;
import org.dpr.mykeys.ihm.CertificatesView;
import org.dpr.mykeys.ihm.IModelFactory;
import org.dpr.mykeys.ihm.components.treekeystore.SecurityElementView;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.*;
import java.util.List;

public class ListImgCertificatesView extends SecurityElementView implements CertificatesView {

    private final JImgList listCerts;
    private final List<EventCompListener> listeners = new ArrayList<>();
    DataFlavor nodesFlavor;
    final DataFlavor[] flavors = new DataFlavor[1];

    @Override
    public Component getListCerts() {
        return listCerts;
    }

    private final DefaultListModel listModel;
    //private ListSelectionListener listListener;

    public ListImgCertificatesView() {
        listModel = new DefaultListModel();

        model = new IModelFactory() {
            @Override
            public void removeAllElements() {
                listModel.removeAllElements();
            }

            @Override
            public void addElement(MkCertificate ci) {
                listModel.addElement(ci);
            }

            @Override
            public void refresh() {

            }
        };

        listCerts = new JImgList(listModel);

        try {
            nodesFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
                    ";class=org.dpr.mykeys.app.certificate.Certificate");
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        init();

    }

    public void sort() {

        List<MkCertificate> list = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            list.add((MkCertificate) listModel.get(i));
        }
        Collections.sort(list);
        model.removeAllElements();
        for (MkCertificate s : list) {
            model.addElement(s);
        }
    }

    private void init() {


        ListCertRenderer renderer = new ListCertRenderer();
        listCerts.setCellRenderer(renderer);
        listCerts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        listCerts.setVisibleRowCount(-1);
        listCerts.setDragEnabled(true);
        listCerts.setTransferHandler(new TransferHandler() {
            int index;

            @Override
            public int getSourceActions(JComponent comp) {
                return COPY_OR_MOVE;
            }

            @Override
            public Transferable createTransferable(JComponent comp) {
                index = listCerts.getSelectedIndex();
                return new CertificatesTransferable(listCerts.getSelectedValuesList());
            }


            @Override
            public void exportDone(JComponent comp, Transferable trans, int action) {
                if (action == MOVE) {
                    //   transport.remove(index);
                }
            }
        });

        listCerts.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                for (EventCompListener listener:listeners){
                    MkCertificate ci = (MkCertificate) ((JList) event.getSource()).getSelectedValue();
                    listener.certificateSelected(ci);
                }
            }
        });

    }

    @Override
    public void addListener(EventListener listener) {
        listeners.add((EventCompListener) listener);
    }

    @Override
    public void clear() {
        listCerts.clearSelection();
        model.removeAllElements();
    }

    @Override
    public void makeVisible(boolean b) {
        listCerts.setShowImage(b);
    }

    @Override
    public Certificate getSelected() {
        return (Certificate) listCerts.getSelectedValue();
    }

    @Override
    public List getSelectedList() {
        return listCerts.getSelectedValuesList();
    }

    public class CertificatesTransferable implements Transferable {
        final List<Certificate> nodes;

        public CertificatesTransferable(List<Certificate> nodes) {
            this.nodes = nodes;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean issup = nodesFlavor.equals(flavor);
            return nodesFlavor.equals(flavor);
        }
    }
}
