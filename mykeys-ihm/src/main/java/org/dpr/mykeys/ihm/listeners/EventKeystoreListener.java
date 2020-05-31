package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;

import java.util.EventListener;
import java.util.List;

public interface EventKeystoreListener extends EventListener {

    void KeystoreAdded(List<String> keystores);


}
