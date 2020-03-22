package org.dpr.mykeys.ihm.components.treekeystore;

import org.dpr.mykeys.app.ServiceException;

import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;
import java.util.EventListener;

public interface TreeKeyStoreActions {


    void registerListener(EventListener listener);

    void exporterCertificate(DefaultMutableTreeNode node, boolean b);

    boolean closeStore(DefaultMutableTreeNode node, boolean collapse);

    boolean openStore(DefaultMutableTreeNode node,
                      boolean useInternalPwd, boolean expand);


    void addCertificate(DefaultMutableTreeNode node, boolean b) throws ServiceException;

    void importCertificate(DefaultMutableTreeNode node, boolean b);

    void addCertificateAC(DefaultMutableTreeNode node, boolean b);

    void removeNode(DefaultMutableTreeNode node);

    void changePassword(DefaultMutableTreeNode node, boolean b);

    Component getComponent();

}
