package org.dpr.mykeys.ihm.components.treekeystore;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.components.CertificatesView;
import org.dpr.mykeys.ihm.components.IModelFactory;
import org.dpr.mykeys.ihm.components.TreeCertManager;

import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class TreeCertificatesView implements CertificatesView {

    final String KS_AC_NAME = "store.ac.name";
    private TreeCertManager treeks;
    private IModelFactory model;

    public TreeCertificatesView() {
        this.treeks = new TreeCertManager();

        model = new IModelFactory() {
            @Override
            public void removeAllElements() {
                treeks.clear();
                //     treeks.getNodes().clear();

            }

            @Override
            public void addElement(ChildInfo ci) {
                treeks.fillNodes(KS_AC_NAME,
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

    }

    @Override
    public void clear() {
        treeks.clear();

    }

    @Override
    public IModelFactory getModel() {
        return model;
    }

    @Override
    public void makeVisible(boolean b) {

    }

    @Override
    public CertificateValue getSelected() {
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) treeks.getTree().getLastSelectedPathComponent();
        if (tNode != null) {
            Object object = tNode.getUserObject();
            if (object != null)
                return (CertificateValue) object;
        }
        return null;

    }

    @Override
    public List getSelectedList() {
        List ar = new ArrayList<CertificateValue>();
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) treeks.getTree().getLastSelectedPathComponent();
        if (tNode != null) {
            Object object = tNode.getUserObject();
            if (object != null) {
                ar.add(object);

            }
        }
        return ar;
    }
}
