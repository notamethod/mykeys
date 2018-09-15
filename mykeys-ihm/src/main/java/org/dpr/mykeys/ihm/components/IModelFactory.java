package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.ChildInfo;

import javax.swing.*;

public interface IModelFactory {

    public void removeAllElements();

    public void addElement(ChildInfo ci);

    void refresh();
}
