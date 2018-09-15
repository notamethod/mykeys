package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.ChildInfo;

import javax.swing.*;

public class ModelFactory {
    private DefaultListModel listModel;

    public ListModel getListModel() {
        if (listModel == null)
            listModel = new DefaultListModel();
        return listModel;

    }

    public void removeAllElements() {
        listModel.removeAllElements();
    }

    public void addElement(ChildInfo ci) {
        listModel.addElement(ci);
    }
}
