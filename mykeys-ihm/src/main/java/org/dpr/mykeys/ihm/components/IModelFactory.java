package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.ChildInfo;

import javax.swing.*;

public interface IModelFactory {

    void removeAllElements();

    void addElement(ChildInfo ci);

    void refresh();
}
