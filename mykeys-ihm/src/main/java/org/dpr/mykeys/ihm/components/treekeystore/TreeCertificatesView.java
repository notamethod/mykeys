package org.dpr.mykeys.ihm.components.treekeystore;

import org.dpr.mykeys.app.certificate.MkCertificate;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.ihm.actions.TreePopupMenuCertificate;
import org.dpr.mykeys.ihm.CertificatesView;
import org.dpr.mykeys.ihm.IModelFactory;
import org.dpr.mykeys.ihm.listeners.CertificateActionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.List;

public class TreeCertificatesView extends SecurityElementView implements CertificatesView  {

    final String KS_AC_NAME = "store.ac.name";
    private final TreeCertManager treeks;


    public TreeCertManager getTreeManager() {
        return treeks;
    }

    public TreeCertificatesView() {
        this.treeks = new TreeCertManager();

        model = new IModelFactory() {
            @Override
            public void removeAllElements() {
                treeks.clear();
                //     treeks.getNodes().clear();

            }

            @Override
            public void addElement(MkCertificate ci) {
                treeks.fillNodes(
                        ci);
            }

            @Override
            public void refresh() {

                treeks.organize();
            }
        };

        DefaultMutableTreeNode acNode = new DefaultMutableTreeNode(Messages.getString(KS_AC_NAME));

        treeks.addNode(KS_AC_NAME, acNode, true);
    }

    @Override
    public Component getListCerts() {
        return treeks.getTree();
    }

    @Override
    public void addListener(EventListener listListener) {
        //    this.listListener = listListener;
        treeks.registerListener(listListener);
        //  this.getTreeManager().getPopup().registerListener(listListener);


    }

    public void addCertListener(CertificateActionListener listener) {
        //    this.listListener = listListener;
        TreePopupMenuCertificate popup = (TreePopupMenuCertificate) this.getTreeManager().getPopup();
        popup.registerListener(listener);


    }
    @Override
    public void clear() {
        treeks.clear();

    }


    @Override
    public void makeVisible(boolean b) {

    }

    @Override
    public Certificate getSelected() {
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) treeks.getTree().getLastSelectedPathComponent();
        if (tNode != null) {
            Object object = tNode.getUserObject();
            List<Certificate> certs = new ArrayList<>();

            Enumeration<TreeNode> children = tNode.children();
            if (children != null) {
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
                    Object childObj = child.getUserObject();
                    if (childObj instanceof Certificate)
                        certs.add((Certificate) childObj);
                }
            }
            if (object != null && object instanceof Certificate) {
                ((Certificate) object).setChildren(certs);

                return (Certificate) object;
            }
        }
        return null;

    }

    @Override
    public List getSelectedList() {
        List ar = new ArrayList<Certificate>();
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) treeks.getTree().getLastSelectedPathComponent();
        if (tNode != null) {
            Object object = tNode.getUserObject();
            if (object != null) {
                ar.add(object);

            }
        }
        return ar;
    }

    @Override
    public void sort() {

    }


}
