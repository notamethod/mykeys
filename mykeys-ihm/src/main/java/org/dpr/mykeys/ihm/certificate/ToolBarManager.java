package org.dpr.mykeys.ihm.certificate;

import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.certificate.MkCertificate;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.ihm.components.ObjToolBar;
import org.dpr.mykeys.ihm.certificate.template.ProfileToolBar;

import java.awt.*;

class ToolBarManager {

    private CertificateToolBar certToolbar;
    private ProfileToolBar profToolBar;

    public <T extends ObjToolBar> T getInstance(NodeInfo info) {
        if (info instanceof KeyStoreValue) {
            return (T) certToolbar;

        } else {
            return (T) profToolBar;
        }

    }

    public <T extends ObjToolBar> T getInvInstance(NodeInfo info) {
        if (info instanceof KeyStoreValue) {
            return (T) profToolBar;

        } else {
            return (T) certToolbar;

        }
    }

    public void init(String string, CertificateListPanel listPanel) {
        certToolbar = new CertificateToolBar("");
        profToolBar = new ProfileToolBar("", new KeysProfileAction(listPanel, listPanel));

    }

    public void removeListeners(NodeInfo info) {
        getInstance(info).removeListeners();

    }

    public void enableActions(NodeInfo info) {
        getInstance(info).enableActions();

    }

    public void enableGenericActions(NodeInfo info, boolean b) {
        getInstance(info).enableGenericActions(info, b);

    }

    public void enableElementActions(NodeInfo info, MkCertificate ci, boolean b) {
        getInstance(info).enableElementActions(info, ci, b);

    }

    public void enableListeners(NodeInfo info) {
        getInstance(info).enableListeners();

    }

    public void disableActions(NodeInfo info) {
        getInstance(info).disableActions(info);

    }

    public void setTitle(String name) {
        certToolbar.setTitle(name);
        profToolBar.setTitle(name);

    }

    public Component getInstance() {
        // TODO Auto-generated method stub
        return certToolbar;
    }

    public void show(NodeInfo info) {
        getInstance(info).setVisible(true);
        getInvInstance(info).setVisible(false);

    }

}
