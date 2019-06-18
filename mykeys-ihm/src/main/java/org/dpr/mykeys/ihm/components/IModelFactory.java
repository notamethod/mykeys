package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.ChildInfo;

public interface IModelFactory {

    void removeAllElements();

    void addElement(ChildInfo ci);

    void refresh();
}
