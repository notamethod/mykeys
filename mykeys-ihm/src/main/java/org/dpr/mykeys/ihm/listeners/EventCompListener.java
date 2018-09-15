package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;

import java.util.EventListener;

public interface EventCompListener extends EventListener {

    void showingCertListRequested(NodeInfo info);

    void showingCertDetailRequested(ChildInfo info);
}
