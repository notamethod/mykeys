package org.dpr.mykeys.ihm.components.treekeystore;

import org.dpr.mykeys.ihm.CertificatesView;
import org.dpr.mykeys.ihm.IModelFactory;

public abstract class SecurityElementView implements CertificatesView {

    protected IModelFactory model;

    @Override
    public IModelFactory getModel() {
        return model;
    }
}
