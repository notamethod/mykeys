package org.dpr.mykeys.ihm.components.treekeystore;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of Sun Microsystems nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.PkiTools.TypeObject;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import org.dpr.mykeys.ihm.CancelCreationException;
import org.dpr.mykeys.ihm.actions.TreePopupMenu;
import org.dpr.mykeys.ihm.actions.TreePopupMenuKS;
import org.dpr.mykeys.ihm.listeners.CertificateActionListener;
import org.dpr.mykeys.ihm.listeners.EventCompListener;
import org.dpr.mykeys.ihm.listeners.EventKeystoreListener;
import org.dpr.mykeys.ihm.model.TreeKeyStoreModelListener;
import org.dpr.mykeys.ihm.model.TreeModel;
import org.dpr.mykeys.ihm.certificate.CertificateCreateFactory;
import org.dpr.mykeys.ihm.certificate.ImportCertificateDialog;
import org.dpr.mykeys.ihm.certificate.SuperCreate;
import org.dpr.mykeys.ihm.keystore.ChangePasswordDialog;
import org.dpr.mykeys.service.KeystoreService;
import org.dpr.mykeys.app.utils.X509Util;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;

public class TreeKsManager implements MouseListener,
        TreeExpansionListener, TreeWillExpandListener, DropTargetListener, TreeKeyStoreActions {

    private final static Log log = LogFactory.getLog(TreeKsManager.class);

    protected final List<EventCompListener> listeners = new ArrayList<>();
    private final List<CertificateActionListener> keystoreListeners = new ArrayList<>();
    protected final GradientTree tree;
    protected final Map<String, DefaultMutableTreeNode> nodes;
    protected final Map<String, DefaultMutableTreeNode> fixedNodes;
    protected DefaultMutableTreeNode rootNode;
    protected TreePopupMenu popupMenu;
    protected final String KS_AC_NAME = "store.ac.name";

    public TreePopupMenu getPopup() {
        if (popupMenu == null) {
            popupMenu = new TreePopupMenuKS("Popup name", this);
        }
        return popupMenu;
    }

    protected final TreeModel treeModel;

    private final TreePopupMenu popup;

    public TreeKsManager() {

        nodes = new LinkedHashMap<>();
        fixedNodes = new HashMap<>();
        // Create the nodes.
        rootNode = new DefaultMutableTreeNode("Magasins");

        treeModel = new TreeModel(rootNode);
        treeModel.addTreeModelListener(new TreeKeyStoreModelListener());

        tree = new GradientTree(treeModel);
        log.trace(tree.getUI());

        GradientTreeRenderer renderer = new GradientTreeRenderer();

        tree.setCellRenderer(renderer);
        renderer.jtree1 = tree;
        ToolTipManager.sharedInstance().registerComponent(tree);
        // javax.swing.ToolTipManager.ToolTipManager.sharedInstance().registerComponent(tree);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        popup = getPopup();

        tree.setRootVisible(false);

        tree.addMouseListener(this);
        tree.addTreeWillExpandListener(this);
        tree.addTreeExpansionListener(this);
        // drop enabled
        tree.setDropMode(DropMode.ON);
        tree.setTransferHandler(new TreeTransferHandler());

        // Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);
        JPanel leftPanel = new JPanel();
    }

    protected void displayCertDetail(CertificateValue info) {
        notifyCertDetailToUpdate(info);

    }

    protected void notifyCertDetailToUpdate(CertificateValue info) {
        for (EventCompListener listener : listeners) {
            listener.certificateSelected(info);
        }
    }

    /**
     * .
     * <p>
     * <BR>
     *
     * @param info
     */
    private void displayKeystoreList(NodeInfo info) {
        fireCertificateListChanged(info);
    }

    void fireCertificateListChanged(NodeInfo info) {
        for (EventCompListener listener : listeners) {
            listener.certificateListChanged(info);
        }
    }

    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        //	getLevel();
        for (Map.Entry<String, DefaultMutableTreeNode> entry : fixedNodes.entrySet()) {
            entry.getValue().removeAllChildren();
        }
        treeModel.reload();
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
                    .getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }
    }

    public void removeNode(DefaultMutableTreeNode node) {

        MutableTreeNode parent = (MutableTreeNode) (node.getParent());
        if (parent != null) {
            treeModel.removeNodeFromParent(node);
            return;
        }
    }

    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                             Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        if (parent == null) {
            parent = rootNode;
        }

        // It is key to invoke this on the TreeModel, and NOT
        // DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }

    public DefaultMutableTreeNode addObject(String parentName,
                                            Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode parent = nodes.get(parentName);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        if (parent == null) {
            parent = rootNode;
        }

        // It is key to invoke this on the TreeModel, and NOT
        // DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }


    /**
     * add nodes and reorganize
     *
     * @param tmpMap
     * @param child
     * @return
     */
    protected DefaultMutableTreeNode reorganizeNode(Map<String, DefaultMutableTreeNode> tmpMap,
                                                    Object child) {
        //empty tree modele ?
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) child;
        // DefaultMutableTreeNode parent = rootNode;


        if (node.getUserObject() instanceof CertificateValue) {

            CertificateValue value = ((CertificateValue) node.getUserObject());
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(value);
            String key = X509Util.toHexString(value.getDigestSHA256(), "", false);
            String issuerParent = getIssuerParent(value);
            DefaultMutableTreeNode parent = nodes.get(issuerParent);
            TreeNode tn = rootNode.getFirstChild();

            if (parent != null && tmpMap.get(issuerParent) == null) {
                reorganizeNode(tmpMap, parent);
            }
            if ((parent == null || key.equals(issuerParent)) && tmpMap.get(key) == null) {

                treeModel.insertNodeInto(newNode, (MutableTreeNode) tn, tn.getChildCount());
                tmpMap.put(key, newNode);
                tree.scrollPathToVisible(new TreePath(node.getPath()));
            } else if (parent != null && tmpMap.get(issuerParent) != null && tmpMap.get(key) == null) {
                treeModel.insertNodeInto(newNode, tmpMap.get(issuerParent), tmpMap.get(issuerParent).getChildCount());
                tmpMap.put(key, newNode);
                tree.scrollPathToVisible(new TreePath(node.getPath()));
            }

            // tree.scrollPathToVisible(new TreePath(node.getPath()));
            // tree.expandPath(new TreePath(rootNode.getPath()));
        } else {
            if (rootNode.getChildCount() < 1)
                treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
            tmpMap.put(KS_AC_NAME, node);
        }
        //else ?
        return node;
    }

    private String getIssuerParent(CertificateValue value) {
        if (value.getCertificateChain() != null && value.getCertificateChain().length > 1) {
            try {
                CertificateValue cv = new CertificateValue("xx", (X509Certificate) value.getCertificateChain()[1]);

                return X509Util.toHexString(cv.getDigestSHA256(), "", false);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private void removeChildrenObjects(DefaultMutableTreeNode parent) {

        while (treeModel.getChildCount(parent) != 0) {
            treeModel.removeNodeFromParent((DefaultMutableTreeNode) treeModel
                    .getChild(parent, 0));
        }

    }

    public boolean closeStore(DefaultMutableTreeNode node, boolean collapse) {
        KeyStoreValue ksInfo = ((KeyStoreValue) node.getUserObject());
        removeChildrenObjects(node);
        addObject(node, "[Vide]", false);
        if (collapse) {
            ksInfo.setOpen(false);
            tree.collapsePath(new TreePath(node.getPath()));
        }
        return true;

    }

    public boolean openStore(DefaultMutableTreeNode node,
                             boolean useInternalPwd, boolean expand) {
        KeyStoreValue ksInfo = ((KeyStoreValue) node.getUserObject());
        if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) { // equals(StoreModel.CASTORE))
            useInternalPwd = true;
            log.info("internal pass");
        }

        KeystoreService ksv = new KeystoreService();
        boolean isOpen= ksv.openStore(ksInfo);
        fireCertificateListChanged(ksInfo);
        return isOpen;

    }

    protected void showPopupMenu(MouseEvent e) {
        DefaultMutableTreeNode tNode = null;
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selPath != null) {
            tNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        }

        popup.setNode(tNode);
        popup.show(tree, e.getX(), e.getY());

    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        log.trace("collaps");

    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        log.trace("expand");

    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) {
        log.trace("collapse1");

    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event)
            throws ExpandVetoException {
        log.trace("ask expand");
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) event.getPath()
                .getLastPathComponent();
        if (tNode.getParent() != null) {
            Object object = tNode.getUserObject();
            if (object instanceof KeyStoreValue) {
                if (((KeyStoreValue) object).isOpen()) {
                    return;
                } else {

                    if (openStore(tNode, false, true)) {
                        return;
                    }

                }
            } else if (object instanceof String) {
                return;
            }
            //spent 1 full day to add this condition ! :-(
            else if (object instanceof CertificateValue) {
                return;
            }
            throw new ExpandVetoException(event);

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int selRow = tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                TreePath currentSelection = tree.getSelectionPath();
                if (currentSelection == null
                        || !currentSelection.equals(selPath)) {
                    tree.setSelectionPath(selPath);
                }

            }
            showPopupMenu(e);

        }
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        log.debug(selRow);
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {
            if (e.getClickCount() == 1) {
                DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) selPath
                        .getLastPathComponent();
                Object object = tNode.getUserObject();
                if (object instanceof CertificateValue) {
                    CertificateValue certInfo = ((CertificateValue) object);
                    displayCertDetail(certInfo);

                } else {
                    displayCertDetail(null);
                    if (object instanceof KeyStoreValue) {
                        KeyStoreValue ksiInfo = ((KeyStoreValue) object);
                        if (ksiInfo != null)

                            displayKeystoreList(ksiInfo);


                    } else if (object instanceof ProfilStoreInfo) {
                        ProfilStoreInfo ksiInfo = ((ProfilStoreInfo) object);
                        if (ksiInfo != null)

                            displayKeystoreList(ksiInfo);


                    } else {

                        displayKeystoreList(null);

                    }
                }

                log.trace(selPath);
            } else if (e.getClickCount() == 2) {
                //nothing
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (e.isPopupTrigger()) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {

                    TreePath currentSelection = tree.getSelectionPath();
                    if (currentSelection == null
                            || !currentSelection.equals(selPath)) {
                        tree.setSelectionPath(selPath);
                    }

                }
                showPopupMenu(e);

            }
            showPopupMenu(e);
        }

    }

    public void addCertificate(DefaultMutableTreeNode node, boolean b) throws ServiceException {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        SuperCreate cs = null;
        try {
            cs = CertificateCreateFactory.getCreateDialog(frame, ksInfo,
                    true);
        } catch (CancelCreationException e) {
            //creation cancelled
            return;
        }
        cs.setLocationRelativeTo(frame);
        cs.setResizable(true);
        cs.setVisible(true);
        openStore(node, true, true);
        displayKeystoreList(ksInfo);
        return;

    }

    public void importCertificate(DefaultMutableTreeNode node, boolean b) {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        ImportCertificateDialog cs = new ImportCertificateDialog(frame, ksInfo,
                true);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        openStore(node, true, true);

    }

    public void changePassword(DefaultMutableTreeNode node, boolean b) {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        ChangePasswordDialog cs = new ChangePasswordDialog(frame, ksInfo);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);


    }

    @Override
    public Component getComponent() {
        return tree.getTopLevelAncestor();
    }

    @Override
    public void registerListener(EventListener listener) {
        // I think i have to suppression th euse other CertListListener !

        listeners.add((EventCompListener) listener);

    }

    public void dispatchListener(EventKeystoreListener listener) {
        ((TreePopupMenuKS)getPopup()).addKeystoreListener(listener);
    }
//xxx

    /**
     * .
     * <p>
     * <BR>
     *
     * @param node
     * @param b
     */
    public void exporterCertificate(DefaultMutableTreeNode node, boolean b) {
        log.error("Method removed !");
//        JFrame frame = (JFrame) tree.getTopLevelAncestor();
//        // KeyStoreValue ksInfo = null;
//        CertificateValue certInfo = null;
//        Object object = node.getUserObject();
//        if (object instanceof CertificateValue) {
//            certInfo = ((CertificateValue) object);
//        }
//        KeyStoreValue ksInfo = null;
//        DefaultMutableTreeNode objectKs = (DefaultMutableTreeNode) node
//                .getParent();// .getUserObject();
//        if (objectKs.getUserObject() instanceof KeyStoreValue) {
//            ksInfo = ((KeyStoreValue) objectKs.getUserObject());
//        }
//        ExportCertificateDialog cs = new ExportCertificateDialog(frame, ksInfo,
//                certInfo, true);
//        cs.setLocationRelativeTo(frame);
//        cs.setResizable(false);
//        cs.setVisible(true);

    }

    /**
     * .
     * <p>
     * <BR>
     *
     * @param node
     * @param b
     */
    public void addCertificateAC(DefaultMutableTreeNode node, boolean b) {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        SuperCreate cs = null;
        try {
            cs = CertificateCreateFactory.getCreateDialog(frame, ksInfo,
                    true);
        } catch (CancelCreationException e) {
            //creation cancelled
            return;
        }
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        openStore(node, true, true);

        return;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        boolean isActionCopy = false;
        log.debug("drop");
        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
            if ((dtde.getDropAction() & DnDConstants.ACTION_COPY) != 0) {
                isActionCopy = true;
            }
            // Accept the drop and get the transfer data
            dtde.acceptDrop(dtde.getDropAction());
            Transferable transferable = dtde.getTransferable();

            try {
                boolean result = false;
                List fileList = (List) transferable
                        .getTransferData(DataFlavor.javaFileListFlavor);
                File transferFile = (File) fileList.get(0);
                TypeObject typeObject = PkiTools.getTypeObject(transferFile);
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) && typeObject != TypeObject.UNKNOWN && typeObject != null) {

                    result = dropFile(transferable, isActionCopy);
                } else {
                    result = false;
                }

                dtde.dropComplete(result);

            } catch (Exception e) {
                log.error("Exception while handling drop ", e);
                dtde.rejectDrop();
            }
        } else {
            log.info("Drop target rejected drop");
            dtde.dropComplete(false);
        }
    }

    private boolean dropFile(Transferable transferable, boolean isActionCopy) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        log.debug("dropevent");

    }

    public JTree getTree() {
        return tree;
    }

    public void addNode(String s, DefaultMutableTreeNode node, boolean insertInModel) {
        nodes.put(s, node);
        //FIXME: not good at all
        fixedNodes.put(s, node);

        if (insertInModel)
            treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
    }

    public void fillNodes(ChildInfo ci) {
        if (ci instanceof CertificateValue) {
            CertificateValue value = (CertificateValue) ci;
            log.debug(value.getCertificateChain());
            String key = X509Util.toHexString(value.getDigestSHA256(), "", false);
            nodes.put(key, new DefaultMutableTreeNode(value));
        }
    }

    public void organize() {
        log.info("Organize");
        DefaultMutableTreeNode node = null;
        Map<String, DefaultMutableTreeNode> tmpMap = new HashMap<>();
        for (Map.Entry<String, DefaultMutableTreeNode> entry : nodes.entrySet()) {
            //entry.getValue().removeAllChildren();
            node = reorganizeNode(tmpMap,
                    entry.getValue());
        }
        treeModel.reload();

        if (node != null)
            tree.scrollPathToVisible(new TreePath(node.getPath()));
    }



    class TreeTransferHandler extends TransferHandler {
        DataFlavor nodesFlavor;
        DataFlavor[] flavors = new DataFlavor[1];

        /**
         * @param support
         * @return
         * @see TransferHandler#importData(TransferSupport)
         */
        @Override
        public boolean importData(TransferSupport support) {
            log.debug("drop object");
            if (!canImport(support)) {
                return false;
            }
            DataFlavor df = null;
            try {
                df = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
                        ";class=org.dpr.mykeys.app.certificate.CertificateValue");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            JTree.DropLocation dropLocation =
                    (JTree.DropLocation) support.getDropLocation();
            TreePath path = dropLocation.getPath();
            Transferable transferable = support.getTransferable();
            List<CertificateValue> transferData;
            try {
                transferData = (List<CertificateValue>) transferable.getTransferData(
                        df);
            } catch (IOException | UnsupportedFlavorException e) {
                e.printStackTrace();
                return false;
            }

            DefaultMutableTreeNode parentNode =
                    (DefaultMutableTreeNode) path.getLastPathComponent();
            Object object = parentNode.getUserObject();
            KeyStoreValue ksInfo = null;
            if (object instanceof KeyStoreValue) {
                ksInfo = ((KeyStoreValue) object);
            }
            if (ksInfo == null)
                return false;
            if ((ksInfo.isProtected() || ksInfo.getStoreFormat().equals(StoreFormat.JKS)) && !ksInfo.isOpen()){
                log.error("Keystore must be opened first !");
                return false;
            }
            KeystoreService ksv = new KeystoreService();
            try {
                ksv.addCertificates(ksInfo, transferData);
            } catch (ServiceException e) {
                log.error("drop failed", e);
                return false;
            }
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
            fireCertificateListChanged(ksInfo);
            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.swing.TransferHandler#canImport(javax.swing.TransferHandler
         * .TransferSupport)
         */
        @Override
        public boolean canImport(TransferSupport support) {

            if (!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            //log.trace(nodesFlavor.getHumanPresentableName());
            if (!support.isDataFlavorSupported(nodesFlavor)) {
                //why ?
                //return false;

            }
            JTree.DropLocation dropLocation =
                    (JTree.DropLocation) support.getDropLocation();
            return dropLocation.getPath() != null;

        }

    }

}
