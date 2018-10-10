package org.dpr.mykeys.ihm.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKeyStoreActions;
import org.dpr.mykeys.ihm.listeners.CertificateActionListener;
import org.dpr.mykeys.ihm.listeners.CertificateActionPublisher;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.keystore.CreateStoreDialog;
import org.dpr.mykeys.keystore.ImportStoreDialog;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;

public class TreePopupMenuCertificate extends JPopupMenu implements CertificateActionPublisher, TreePopupMenu {
    public static final Log log = LogFactory.getLog(TreePopupMenuCertificate.class);
    private JMenuItem add;

    private JMenuItem delete;

    private JMenuItem exportCert;

    //  private JMenuItem menuChangePwd;

    private TreePath path;

    private DefaultMutableTreeNode node;

    private List<CertificateActionListener> listeners = new ArrayList<>();

    public TreePopupMenuCertificate(String string, TreeKeyStoreActions treeKeyStore) {
        super(string);
        init();
    }

    private void init() {
        System.out.println("xxxxxxxxrr");
        add = new JMenuItem(Messages.getString("magasin.new"));
        add.addActionListener(e -> notifyInsertCertificate("what ?"));
        add.setVisible(false);
        this.show();


        exportCert = new JMenuItem(Messages.getString(
                "certificat.export"));
        exportCert.addActionListener(e -> notifyExportCertificate("what ?"));
        exportCert.setVisible(false);


        delete = new JMenuItem("Suppression physique");
        delete.addActionListener(e -> notifyCertificateDeletion("what ?"));
        delete.setVisible(false);

//        menuChangePwd = new JMenuItem(Messages.getString("magasin.change.password"));
//        menuChangePwd.addActionListener(new TreePopupMenu.TreePopupAction());
//        menuChangePwd.setActionCommand(TypeAction.CHANGE_PWD.getValue());
//        menuChangePwd.setVisible(false);

        add(add);

        add(exportCert);

        add(delete);
        // add(menuChangePwd);
    }

    /**
     * @return the node
     */
    public DefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(DefaultMutableTreeNode node) {
        add.setVisible(false);

        exportCert.setVisible(false);

        delete.setVisible(false);
//        menuChangePwd.setVisible(false);
        this.node = node;
        if (node == null || node.getParent() == null) {
            add.setVisible(false);
            delete.setVisible(false);
            exportCert.setVisible(false);

        } else if (node.getUserObject() instanceof KeyStoreValue) {
            KeyStoreValue ksInfo = (KeyStoreValue) node.getUserObject();


        } else if (node.getUserObject() instanceof CertificateValue) {
            CertificateValue certInfo = (CertificateValue) node.getUserObject();
            add.setVisible(true);
            delete.setVisible(true);
            exportCert.setVisible(true);

        }
    }

    @Override
    public void notifyopenStore(String what) {

    }

    @Override
    public void notifyInsertCertificate(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.insertCertificateRequested("what !");
        }
    }

    @Override
    public void notifyInsertCertificateFromProfile(String what) {

    }

    @Override
    public void notifyInsertCertificateFromCSR(String what) {

    }

    @Override
    public void notifyImportCertificate(String what) {

    }

    @Override
    public void notifyExportCertificate(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.exportCertificateRequested("what !");
        }
    }

    @Override
    public void notifyCertificateDeletion(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.deleteCertificateRequested("what !");
        }
    }

    @Override
    public void notifyCreateCrl(String what) {

    }

    @Override
    public void registerListener(CertificateActionListener listener) {

        listeners.add(listener);


    }

}
