package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.windows.ListCertRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.*;
import java.util.List;

public class ListImgCertificatesView extends Component implements CertificatesView {

    private JImgList listCerts;

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];

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

        try {
            nodesFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
                    ";class=org.dpr.mykeys.app.certificate.CertificateValue");
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        init();

    }

    public void sort() {

        List<ChildInfo> list = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            list.add((ChildInfo) listModel.get(i));
        }
        Collections.sort(list);
        model.removeAllElements();
        for (ChildInfo s : list) {
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

    public class CertificatesTransferable implements Transferable {
        List<CertificateValue> nodes;

        public CertificatesTransferable(List<CertificateValue> nodes) {
            this.nodes = nodes;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            System.out.println(flavors);
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean issup = nodesFlavor.equals(flavor);
            System.out.println("check " + flavors + issup);
            return nodesFlavor.equals(flavor);
        }
    }
}
