package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.ihm.actions.TreePopupMenu;
import org.dpr.mykeys.ihm.actions.TreePopupMenuCertificate;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKsManager;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import java.util.EventListener;

public class TreeCertManager extends TreeKsManager {
    public TreePopupMenu getPopup() {
        return new TreePopupMenuCertificate("Popup name", this);
    }

    @Override
    public void registerListener(EventListener listener) {
        // I think i have to suppression th euse other CertListListener !

        //listeners.add((EventCompListener) listener);

    }
}
