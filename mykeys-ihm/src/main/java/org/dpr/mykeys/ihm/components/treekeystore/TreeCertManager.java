package org.dpr.mykeys.ihm.components.treekeystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.actions.TreePopupMenu;
import org.dpr.mykeys.ihm.actions.TreePopupMenuCertificate;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKsManager;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.EventListener;

public class TreeCertManager extends TreeKsManager {

    private final static Log log = LogFactory.getLog(TreeCertManager.class);

    public TreePopupMenu getPopup() {
        return new TreePopupMenuCertificate("Popup name", this);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.isPopupTrigger()) {
            System.out.println("cert2");
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
        System.out.println(selRow);
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

        listeners.add((EventCompListener) listener);

    }

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
}
