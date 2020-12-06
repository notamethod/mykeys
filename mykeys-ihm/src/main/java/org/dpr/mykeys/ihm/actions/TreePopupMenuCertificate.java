package org.dpr.mykeys.ihm.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.KeyUsages;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.utils.CertificateUtils;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKeyStoreActions;
import org.dpr.mykeys.ihm.listeners.CertificateActionListener;
import org.dpr.mykeys.ihm.listeners.CertificateActionPublisher;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class TreePopupMenuCertificate extends JPopupMenu implements CertificateActionPublisher, TreePopupMenu {
    public static final Log log = LogFactory.getLog(TreePopupMenuCertificate.class);
    private JMenuItem add;

    private JMenuItem addAC;

    private JMenuItem delete;

    private JMenuItem exportCert;

    //  private JMenuItem menuChangePwd;

    private TreePath path;

    private DefaultMutableTreeNode node;

    private final List<CertificateActionListener> listeners = new ArrayList<>();

    private Certificate certificate;

    public TreePopupMenuCertificate(String string, TreeKeyStoreActions treeKeyStore) {
        super(string);
        init();
    }

    private void init() {
        log.debug("init TreePopupMenuCertificate" + this.getClass() + "  " + this);
        add = new JMenuItem(Messages.getString("certificate.new"));
        add.addActionListener(e -> notifyInsertCertificate(certificate));
//        addAC.addActionListener(e -> notifyInsertCertificateAC(certificate));
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

        } else if (node.getUserObject() instanceof Certificate) {
            Certificate certInfo = (Certificate) node.getUserObject();
            certificate = certInfo;
            add.setVisible(certInfo.isContainsPrivateKey() && (KeyUsages.isKeyUsage(certInfo.getKeyUsage(), X509Constants.USAGE_CERTSIGN)));
//            addAC.setVisible(certInfo.isContainsPrivateKey() && (CertificateUtils.isKeyUsage(certInfo.getKeyUsage(),X509Constants.USAGE_CERTSIGN)));
            delete.setVisible(true);
            exportCert.setVisible(true);

        }
    }

    @Override
    public void notifyopenStore(String what) {

    }

    @Override
    public void notifyInsertCertificate(Certificate what) {
        log.debug("notify " + what.getName() + " " + this.getClass());
        for (CertificateActionListener listener : listeners) {
            listener.insertCertificateRequested(what);
        }
    }

    private void notifyInsertCertificateAC(Certificate certificate) {
        for (CertificateActionListener listener : listeners) {
            listener.insertCertificateACRequested("what !");
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
        log.debug(this + " tcer registerListener " + listener);
        listeners.add(listener);

    }


}
