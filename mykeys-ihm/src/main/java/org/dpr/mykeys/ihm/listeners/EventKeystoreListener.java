package org.dpr.mykeys.ihm.listeners;

import java.util.EventListener;
import java.util.List;

public interface EventKeystoreListener extends EventListener {

    void KeystoreAdded(List<String> keystores);


}
