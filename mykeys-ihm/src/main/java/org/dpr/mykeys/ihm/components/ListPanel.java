package org.dpr.mykeys.ihm.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.windows.CreateCrlDialog;
import org.dpr.mykeys.ihm.windows.IhmException;
import org.dpr.mykeys.ihm.windows.ListCertRenderer;
import org.dpr.mykeys.ihm.windows.certificate.CreateCertificatDialog;
import org.dpr.mykeys.ihm.windows.certificate.ExportCertificateDialog;
import org.dpr.mykeys.ihm.windows.certificate.ImportCertificateDialog;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.template.CreateTemplateDialog;
import org.dpr.mykeys.template.SelectTemplateDialog;
import org.dpr.mykeys.utils.ActionStatus;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.LabelValuePanel;

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
import java.security.GeneralSecurityException;
import java.util.List;

import static org.dpr.mykeys.utils.MessageUtils.getMessage;
import static org.dpr.swingtools.ImageUtils.createImageIcon;

@SuppressWarnings("serial")
public class ListPanel extends JPanel implements DropTargetListener {
    private static final Log log = LogFactory.getLog(ListPanel.class);
    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;
    NodeInfo ksInfo;
    JImgList listCerts;
    private ToolBarManager toolBarManager = new ToolBarManager();
    private DetailPanel detailPanel;
    private KeysAction actions;
    private ActionPanel dAction;
    private JPanel jp;
    private DefaultListModel listModel;
    private DropTarget dropTarget;

    public ListPanel() {
        super(new BorderLayout());

        init();

    }

    private void init() {
        // Create the DropTarget and register
        // it with the JPanel.
        dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
        dAction = new ActionPanel();

        jp = new JPanel(new BorderLayout());
        final ImageIcon icon = createImageIcon("/images/Locked.png");

        actions = new KeysAction(this, this);

        toolBarManager.init("", actions, this);
        listModel = new DefaultListModel();
        ListSelectionListener listListener = new CertListListener();

        listCerts = new JImgList(listModel);

        listCerts.addListSelectionListener(listListener);
        ListCertRenderer renderer = new ListCertRenderer();
        listCerts.setCellRenderer(renderer);
        listCerts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        listCerts.setVisibleRowCount(-1);
        listCerts.setDragEnabled(true);
        // listCerts.setTransferHandler(new ListTransferHandler());

        JScrollPane listScroller = new JScrollPane(listCerts);
        // listScroller.setPreferredSize(new Dimension(450, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        jp.add(listScroller, BorderLayout.CENTER);

        add(jp);

        jp.setVisible(false);
    }

    public void updateInfo(NodeInfo info) throws ServiceException {
        jp.setVisible(false);

        jp.remove(toolBarManager.getInvInstance(info));
        jp.add(toolBarManager.getInstance(info), BorderLayout.PAGE_START);
        // jp.revalidate();
        if (info == null) {
            return;
        }
        ksInfo = info;
        listCerts.clearSelection();
        listModel.removeAllElements();
        // FIXME


        if (ksInfo instanceof ProfilStoreInfo) {

            for (ChildInfo ci : ProfileServices.getProfils(KSConfig.getProfilsPath())) {
                listModel.addElement(ci);
            }
        } else {
            KeyStoreHelper ks = new KeyStoreHelper((KeyStoreValue) ksInfo);
            for (ChildInfo ci : ks.getChildList()) {
                listModel.addElement(ci);
            }
        }
        toolBarManager.removeListeners(info);
        // addCertProfButton.removeActionListener(actions);
        if (ksInfo.isOpen()) {

            toolBarManager.enableGenericActions(info, true);
            toolBarManager.enableListeners(info);

            // addCertProfButton.addActionListener(actions);
            listCerts.setShowImage(false);

        } else {

            toolBarManager.disableActions(info);
            listCerts.setShowImage(true);
        }
        toolBarManager.setTitle(ksInfo.getName());
        // toolBarManager.show(info);

        jp.revalidate();
        jp.setVisible(true);
    }

    private void displayDetail(ChildInfo info) {
        detailPanel.updateInfo(info);

    }

    public void setDetailPanel(DetailPanel detailPanel) {
        this.detailPanel = detailPanel;
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

    /*
     * (non-Javadoc)
     *
     * @see org.dpr.mykeys.ihm.components.IListPanel#setDetailPanel(org.dpr.mykeys.
     * ihm.components.DetailPanel)
     */

    public void addElement(NodeInfo info, boolean b) throws ServiceException {

        JFrame frame = (JFrame) this.getTopLevelAncestor();
        SuperCreate cs = null;
        if (info instanceof KeyStoreValue) {
            cs = new CreateCertificatDialog(frame, (KeyStoreValue) info, true);
        } else {
            cs = new CreateTemplateDialog(frame, true);
        }
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
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

    public void createCrl(NodeInfo info, CertificateValue certificateInfo, boolean b) throws ServiceException {

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
     * <p>
     * <BR>
     *
     * @param info
     * @param certificateInfo
     * @throws ServiceException
     */
    public void deleteCertificate(NodeInfo info, CertificateValue certificateInfo) throws ServiceException {
        KeyStoreValue kinfo = (KeyStoreValue) info;
        KeyStoreHelper ksv = new KeyStoreHelper(kinfo);
        try {
            ksv.removeCertificate(kinfo, certificateInfo);

        } catch (Exception e1) {
            DialogUtil.showError(this, e1.getMessage());
            e1.printStackTrace();
        }
        updateInfo(ksInfo);

    }

    public void exporterCertificate(NodeInfo info, CertificateValue certificateInfo, boolean b) {
        KeyStoreValue kinfo = (KeyStoreValue) info;
        JFrame frame = (JFrame) this.getTopLevelAncestor();

        ExportCertificateDialog cs = new ExportCertificateDialog(frame, kinfo, certificateInfo, true);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);

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
            if (resetPassword == true)
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
            System.out.println("Drop target rejected drop");
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


        ActionStatus act = null;
        try {
            act = service.importCertificates(ksin);
            if (act != null && act.equals(ActionStatus.ASK_PASSWORD)) {
                char[] password = DialogUtil.showPasswordDialog(this);
                ksin.setPassword(password);

                service.importCertificates(ksin);
            }
            updateInfo(ksInfo);
        } catch (KeyToolsException | GeneralSecurityException e) {
            log.error(e);
            DialogUtil.showError(this, e.getLocalizedMessage());

        }

        // System.out.println("File URL is " + transferURL);

        return true;
    }

    public void addCertFromCSR(NodeInfo info, boolean b) throws ServiceException {
        JFrame frame = (JFrame) this.getTopLevelAncestor();
        SuperCreate cs = null;
        if (info instanceof KeyStoreValue) {
            cs = new CreateCertificatFromCSRDialog(frame, (KeyStoreValue) info, true);
        }
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        updateInfo(info);

        return;

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
    class CertListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            System.out.println(e.toString());
            if (!e.getValueIsAdjusting()) {

                log.trace(e.getSource().getClass());
                if (e.getSource() instanceof JList) {
                    if (((JList) e.getSource()).getSelectedValue() instanceof ChildInfo) {
                        displayDetail((ChildInfo) ((JList) e.getSource()).getSelectedValue());
                        if (ksInfo.isOpen()) {
                            toolBarManager.enableElementActions(ksInfo, true);

                        }
                    }

                }
            }

        }

    }

    class ActionPanel extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("CHECK_OCSP")) {
                log.trace("OCSP");

            } else if (command.equals("OK")) {

                KeyTools kt = new KeyTools();

            } else if (command.equals("CANCEL")) {
                // ExportCertificateDialog.this.setVisible(false);
            }

        }

    }

}
