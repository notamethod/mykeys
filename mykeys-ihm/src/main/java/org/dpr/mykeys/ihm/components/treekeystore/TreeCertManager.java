package org.dpr.mykeys.ihm.components.treekeystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.actions.TreePopupMenu;
import org.dpr.mykeys.ihm.actions.TreePopupMenuCertificate;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class TreeCertManager extends TreeKsManager {

    private final static Log log = LogFactory.getLog(TreeCertManager.class);

    @Override
    public TreePopupMenu getPopup() {
        if (popupMenu == null)
            popupMenu = new TreePopupMenuCertificate("Popup name", this);
        return popupMenu;
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.isPopupTrigger()) {
            log.debug("cert2");
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

                }

                log.trace(selPath);
            }
        }

    }

    @Override
    public void registerListener(EventListener listener) {
        // I think i have to suppression th euse other CertListListener !

//        if (listener instanceof  CertificateActionListener)
//            getPopup().registerListener(listener);
//        else
        //show detail cetificate
        listeners.add((EventCompListener) listener);

    }
//    @Override
//    public void registerListener(CertificateActionListener listener) {
//        // I think i have to suppression th euse other CertListListener !
//        popupMenu.registerListener(listener);
//
//
//    }


    @Override
    protected void displayCertDetail(CertificateValue info) {
        notifyCertDetailToUpdate(info);

    }

    @Override
    protected void notifyCertDetailToUpdate(CertificateValue info) {
        for (EventCompListener listener : listeners) {
            listener.showingCertDetailRequested(info);
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
        System.out.println(treeModel.getChildCount(rootNode.getFirstChild()));
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        if (node != null)
            tree.scrollPathToVisible(new TreePath(node.getPath()));
    }

    /**
     * Remove all nodes except the root node.
     */
    @Override
    public void clear() {
        treeModel.setRoot(null);
        // ((DefaultTreeModel)getModel()).setRoot(null);
        //super.clear();
        fixedNodes.clear();
        nodes.clear();
        rootNode = new DefaultMutableTreeNode("Magasins");

        treeModel.setRoot(rootNode);
        System.out.println("CLEAR");
        //  DefaultMutableTreeNode root2 = (DefaultMutableTreeNode) rootNode;
        //root2.removeAllChildren();
        DefaultMutableTreeNode acNode = new DefaultMutableTreeNode(Messages.getString(KS_AC_NAME));

        addNode(KS_AC_NAME, acNode, true);
        treeModel.reload();

    }


}
