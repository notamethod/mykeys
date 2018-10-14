package org.dpr.mykeys.ihm.actions;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public interface TreePopupMenu {
    DefaultMutableTreeNode getNode();

    void setNode(DefaultMutableTreeNode node);

    void show(Component invoker, int x, int y);

}
