package org.dpr.mykeys.ihm.listeners;

import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.certificate.MkCertificate;

import java.util.EventListener;

public interface EventCompListener extends EventListener {

    void certificateListChanged(NodeInfo info);

    void certificateSelected(MkCertificate info);
}
