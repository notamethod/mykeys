package org.dpr.mykeys.ihm.certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.configuration.MkSession;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.CancelCreationException;
import org.dpr.mykeys.ihm.CertificatesView;
import org.dpr.mykeys.ihm.IhmException;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.ihm.components.ListImgCertificatesView;
import org.dpr.mykeys.ihm.components.treekeystore.TreeCertificatesView;
import org.dpr.mykeys.ihm.keystore.ImportStoreDialog;
import org.dpr.mykeys.ihm.listeners.CertificateActionListener;
import org.dpr.mykeys.ihm.listeners.EventCompListener;
import org.dpr.mykeys.ihm.crl.CreateCrlDialog;
import org.dpr.mykeys.ihm.certificate.template.CreateTemplateDialog;
import org.dpr.mykeys.ihm.certificate.template.SelectTemplateDialog;
import org.dpr.mykeys.app.utils.ActionStatus;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.service.KeystoreService;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.dpr.mykeys.utils.MessageUtils.getMessage;
import static org.dpr.swingtools.ImageUtils.createImageIcon;

@SuppressWarnings("serial")
public class CertificateListPanel extends JPanel implements DropTargetListener, CertificateActionListener {
    private static final Log log = LogFactory.getLog(CertificateListPanel.class);

    NodeInfo ksInfo;
    CertificatesView listCerts;
    private final List<EventCompListener> listeners = new ArrayList<>();
    private final ToolBarManager toolBarManager = new ToolBarManager();

    private JPanel jp;

    public CertificateListPanel(String viewType) {
        super(new BorderLayout());

        init(viewType);
    }

    private void init(String viewType) {
        // Create the DropTarget and register
        // it with the JPanel.
        //private DefaultListModel listModel;
        DropTarget dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
        ActionPanel dAction = new ActionPanel();

        jp = new JPanel(new BorderLayout());
        final ImageIcon icon = createImageIcon("/images/Locked.png");


        toolBarManager.init("", this);
        //for some times...

        if (toolBarManager.getInstance() instanceof CertificateToolBar) {
            ((CertificateToolBar) toolBarManager.getInstance()).registerListener(this);
        }


        ListSelectionListener listListener = new CertListListener();

        String viewTypePref = KSConfig.getUserCfg().getString("certificate.list.style", "flat");
        if (viewType == null)
            viewType = viewTypePref;
        if (viewType.equalsIgnoreCase("tree")) {
            listCerts = new TreeCertificatesView();
            ((TreeCertificatesView) listCerts).addCertListener(this);
        } else
            listCerts = new ListImgCertificatesView();


        listCerts.addListener(listListener);
        // listCerts.addListener(this);

        // listCerts.setTransferHandler(new ListTransferHandler());

        JScrollPane listScroller = new JScrollPane(listCerts.getListCerts());
        // listScroller.setPreferredSize(new Dimension(450, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        jp.add(listScroller, BorderLayout.CENTER);

        add(jp);

        jp.setVisible(false);
    }


    public void updateInfo(NodeInfo info) throws ServiceException {
        log.debug("Update Info");
        jp.setVisible(false);

        jp.remove(toolBarManager.getInvInstance(info));
        jp.add(toolBarManager.getInstance(info), BorderLayout.PAGE_START);
        // jp.revalidate();
        if (info == null) {
            return;
        }
        ksInfo = info;
        listCerts.clear();

        if (ksInfo instanceof ProfilStoreInfo) {

            for (ChildInfo ci : ProfileServices.getProfils(KSConfig.getProfilsPath())) {
                listCerts.getModel().addElement(ci);
            }
        } else {
            KeyStoreHelper ks = new KeyStoreHelper((KeyStoreValue) ksInfo);
            if (ks.getChildList() != null) {


                log.debug("childlist:" + ks.getChildList().size());
                for (ChildInfo ci : ks.getChildList()) {
                    listCerts.getModel().addElement(ci);
                }
            }
            //TODO add parameter to refresh method (type ks: storemodel an dstortype)
            listCerts.sort();
            listCerts.getModel().refresh();
        }
        toolBarManager.removeListeners(info);
        // addCertProfButton.removeActionListener(actions);
        if (ksInfo.isOpen()) {

            toolBarManager.enableGenericActions(info, true);
            toolBarManager.enableListeners(info);

            // addCertProfButton.addActionListener(actions);
            listCerts.makeVisible(false);

        } else {

            toolBarManager.disableActions(info);
            listCerts.makeVisible(true);
        }
        toolBarManager.setTitle(ksInfo.getName());
        // toolBarManager.show(info);

        jp.revalidate();
        jp.setVisible(true);
    }

    private void displayDetail(ChildInfo info) {
        notifyCertDetailToUpdate(info);


    }

    private void notifyCertDetailToUpdate(ChildInfo info) {
        for (EventCompListener listener : listeners) {
            listener.showingCertDetailRequested(info);
        }
    }


    public void registerListener(EventCompListener listener) {
        listeners.add(listener);

    }


    /**
     * @return the ksInfo
     */
    public NodeInfo getKsInfo() {
        return ksInfo;
    }

    /**
     * @param ksInfo the ksInfo to set
     */
    public void setKsInfo(KeyStoreValue ksInfo) {
        this.ksInfo = ksInfo;
    }


    public void addElement(NodeInfo info, boolean b, CertificateValue issuer) throws ServiceException {

        JFrame frame = (JFrame) this.getTopLevelAncestor();
        SuperCreate cs = null;
        CertificateType certType = null;
        if (info instanceof KeyStoreValue) {
            KeyStoreValue ksInfo = (KeyStoreValue) info;
            //selection dialog only for pki store
            if (ksInfo.getStoreModel().equals(StoreModel.PKISTORE)) {
                CertificateTypeSelectDialog dl = new CertificateTypeSelectDialog(true);
                certType = dl.showDialog();
                if (certType == null)
                    return;
            } else {
                if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
                    certType = CertificateType.AC;
                } else {
                    certType = CertificateType.STANDARD;
                }
            }
            if (certType != null) {
                try {
                    cs = CertificateCreateFactory.getCreateDialog(frame, (KeyStoreValue) info, issuer, certType);
                } catch (CancelCreationException e) {
                    //creation cancelled
                    return;
                }
            }

        } else {
            cs = new CreateTemplateDialog(frame, true);
        }
        if (cs == null)
            return;
        cs.setLocationRelativeTo(frame);
        cs.setResizable(true);
        cs.setVisible(true);
        updateInfo(info);
        return;
    }

    public void addCertFromPRofile(NodeInfo info, boolean b) throws ServiceException, IhmException {

        JFrame frame = (JFrame) this.getTopLevelAncestor();
        SelectTemplateDialog cs = new SelectTemplateDialog(frame, (KeyStoreValue) info);

        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        updateInfo(info);

        return;
    }

    public void showCreateCrlFrame(NodeInfo info, CertificateValue certificateInfo, boolean b) throws ServiceException {

        JFrame frame = (JFrame) this.getTopLevelAncestor();
        CreateCrlDialog cs = new CreateCrlDialog(frame, certificateInfo);

        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        updateInfo(info);

        return;
    }

    /**
     * .
     *
     * <BR>
     *
     * @param info
     * @param certificateInfo
     * @throws ServiceException
     */
    public void showDeleteCertificateFrame(NodeInfo info, List<CertificateValue> certificateInfo) throws ServiceException {
        KeyStoreValue kinfo = (KeyStoreValue) info;
        KeystoreService ksv = new KeystoreService();
        try {
            ksv.removeCertificates(kinfo, certificateInfo);

        } catch (Exception e1) {
            DialogUtil.showError(this, e1.getMessage());
            log.error("error deleting cetificate", e1);
        }
        updateInfo(ksInfo);

    }

    public void showExportCertificatesFrame(NodeInfo info, List selectedValuesList) {
        KeyStoreValue kinfo = (KeyStoreValue) info;
        JFrame frame = (JFrame) this.getTopLevelAncestor();
        List<CertificateValue> certificates;
        certificates = new ArrayList<>(selectedValuesList);
        ExportCertificateDialog cs = new ExportCertificateDialog(frame, kinfo, certificates, true);
        cs.setLocationRelativeTo(frame);
        List<String> files = cs.showDialog();
        if (files!=null) {
            for (String fileName : files) {
                StoreFormat format = KeystoreUtils.findKeystoreType(fileName);

//            kserv.importStore(tfDirectory.getText(), format,
//                    pdin.length == 0 ? null : pdin);
//            // KSConfig.getUserCfg().addProperty("magasin." + typeKS,
//            // tfDirectory.getText());
//            KSConfig.getUserCfg().addProperty(
//                    "store." + StoreModel.CERTSTORE + "."
//                            + format.toString(), tfDirectory.getText());
//            ((MykeysFrame) ImportStoreDialog.this.getParent())
//                    .updateKeyStoreList();
            }
        }
    }

    public void importCertificate(NodeInfo info, boolean b) throws ServiceException {
        KeyStoreValue kinfo = (KeyStoreValue) info;
        JFrame frame = (JFrame) this.getTopLevelAncestor();

        ImportCertificateDialog cs = new ImportCertificateDialog(frame, kinfo, true);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        updateInfo(kinfo);

    }

    public boolean openStore(boolean useInternalPwd, boolean expand) {

        boolean resetPassword = false;
        if (ksInfo.isProtected()) {

            KeyStoreValue kstInfo = (KeyStoreValue) ksInfo;
            if (kstInfo.getPassword() == null) {
                char[] password = DialogUtil.showPasswordDialog(this);

                if (password == null || password.length == 0) {
                    return false;
                }
                resetPassword = true;
                kstInfo.setPassword(password);
            }
        }

        try {
            KeyStoreHelper kserv = new KeyStoreHelper((KeyStoreValue) ksInfo);
            kserv.open();
            ksInfo.setOpen(true);
        } catch (Exception e1) {
            DialogUtil.showError(this, e1.getMessage());
            log.error("error opening keystore", e1);
            //reset password to try next time
            if (resetPassword)
                ((KeyStoreValue) ksInfo).setPassword(null);
            return false;
        }

        return true;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.ihm.components.IListPanel#dragEnter(java.awt.dnd.
     * DropTargetDragEvent)
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.ihm.components.IListPanel#dragExit(java.awt.dnd.
     * DropTargetEvent)
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.ihm.components.IListPanel#dragOver(java.awt.dnd.
     * DropTargetDragEvent)
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.ihm.components.IListPanel#drop(java.awt.dnd.
     * DropTargetDropEvent)
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
            // Accept the drop and get the transfer data
            dtde.acceptDrop(dtde.getDropAction());
            Transferable transferable = dtde.getTransferable();

            try {
                boolean result = false;

                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    result = dropFile(transferable);
                } else {
                    result = false;
                }

                dtde.dropComplete(result);

            } catch (Exception e) {
                log.error("Exception while handling drop ", e);
                DialogUtil.showError(this, getMessage("error.dnd"));
                dtde.rejectDrop();
            }
        } else {
            log.error("Drop target rejected drop");
            dtde.dropComplete(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.ihm.components.IListPanel#dropActionChanged(java.awt.dnd.
     * DropTargetDragEvent)
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    // This method handles a drop for a list of files
    boolean dropFile(Transferable transferable)
            throws IOException, UnsupportedFlavorException, ServiceException {

        List fileList = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
        File transferFile = (File) fileList.get(0);
        KeyStoreHelper kh = new KeyStoreHelper();

        KeyStoreValue ksin = kh.createKeyStoreValue(transferFile);
        KeyStoreHelper service = new KeyStoreHelper((KeyStoreValue) ksInfo);

        char[] newPwd = new char[0];

        if (((KeyStoreValue) ksInfo).getStoreType().equals(StoreLocationType.INTERNAL))
            newPwd = MkSession.password;

        ActionStatus act = null;
        try {
            act = service.importCertificates(ksin, newPwd);
            if (act != null && act.equals(ActionStatus.ASK_PASSWORD)) {
                char[] password = DialogUtil.showPasswordDialog(this);
                ksin.setPassword(password);

                service.importCertificates(ksin, newPwd);
            }
            updateInfo(ksInfo);
        } catch (Exception e) {
            log.error(e);
            DialogUtil.showError(this, e.getLocalizedMessage());

        }

        return true;
    }

    public void addCertFromCSR(NodeInfo info, boolean b) throws ServiceException {
        JFrame frame = (JFrame) this.getTopLevelAncestor();
        SuperCreate cs = null;
        if (info instanceof KeyStoreValue) {
            cs = new CreateCertificatFromCSRDialog(frame, (KeyStoreValue) info, true);
        }
        if (cs != null) {
            cs.setLocationRelativeTo(frame);
            cs.setResizable(false);
            cs.setVisible(true);
            updateInfo(info);
        }

        return;

    }

    @Override
    public void openStoreRequested(String what) {
        if (openStore(false, true)) {

            try {
                updateInfo(ksInfo);
            } catch (ServiceException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void insertCertificateRequested(CertificateValue certificate) {
        if (certificate == null) {
            if (listCerts != null && listCerts.getSelected() != null) {
                List<CertificateValue> certInfo = listCerts.getSelectedList();
                if (certInfo.get(0).getType().equals(CertificateType.AC))
                    certificate = certInfo.get(0);
            }
        }
        try {

            addElement(ksInfo, false, certificate);
        } catch (ServiceException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void insertCertificateFromProfileRequested(String what) {
        try {
            addCertFromPRofile(ksInfo, false);
        } catch (ServiceException | IhmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    @Override
    public void insertCertificateFromCSRRequested(String what) {
        try {
            addCertFromCSR(ksInfo, false);
        } catch (ServiceException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    @Override
    public void importCertificateRequested(String what) {
        try {
            importCertificate(ksInfo, false);
        } catch (ServiceException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void exportCertificateRequested(String what) {
        if (listCerts != null && listCerts.getSelected() != null) {
            showExportCertificatesFrame(ksInfo, listCerts.getSelectedList());
        }
    }

    @Override
    public void deleteCertificateRequested(String what) {
        if (listCerts != null && listCerts.getSelected() != null) {
            List<CertificateValue> certInfo = listCerts.getSelectedList();
            if (DialogUtil.askConfirmDialog(null, Messages.getString("delete.certificat.confirm", certInfo.toString()))) {
                try {
                    showDeleteCertificateFrame(ksInfo, certInfo);
                } catch (ServiceException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void createCrlRequested(String what) {
        if (listCerts != null && listCerts.getSelected() != null) {
            CertificateValue certInfo = listCerts.getSelected();
            try {
                showCreateCrlFrame(ksInfo, certInfo, false);
            } catch (ServiceException e1) {
                e1.printStackTrace();
            }
        }

    }

    @Override
    public void insertCertificateACRequested(String s) {

    }

    class ListTransferHandler extends TransferHandler {
        DataFlavor certFlavor;

        public ListTransferHandler() {
            try {
                String certType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                        + org.dpr.mykeys.app.certificate.CertificateValue.class.getName() + "\"";
                certFlavor = new DataFlavor(certType);
            } catch (ClassNotFoundException e) {
                log.trace("ClassNotFound: " + e.getMessage());
            }
        }
    }

    /**
     * @author Buck
     */
    class CertListListener implements ListSelectionListener, EventCompListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {

                log.trace(e.getSource().getClass());
                if (e.getSource() instanceof JList) {
                    if (((JList) e.getSource()).getSelectedValue() instanceof ChildInfo) {
                        ChildInfo ci = (ChildInfo) ((JList) e.getSource()).getSelectedValue();
                        displayDetail(ci);
                        if (ksInfo.isOpen()) {
                            toolBarManager.enableElementActions(ksInfo, ci, true);

                        }
                    }

                }
            }
        }

        @Override
        public void showingCertListRequested(NodeInfo info) {

        }

        @Override
        public void showingCertDetailRequested(ChildInfo info) {
            displayDetail(info);
            if (ksInfo.isOpen()) {
                toolBarManager.enableElementActions(ksInfo, info, true);

            }
        }
    }

    class ActionPanel extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            switch (command) {
                case "CHECK_OCSP":
                    log.trace("OCSP");

                    break;
                case "OK":

                    break;
                case "CANCEL":
                    // ExportCertificateDialog.this.setVisible(false);
                    break;
            }
        }

    }

}