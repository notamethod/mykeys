package org.dpr.mykeys.ihm;

import java.awt.Rectangle;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class TreeModel extends DefaultTreeModel {

    private Rectangle rect;
    public TreeModel(TreeNode root) {
	super(root);
	// TODO Auto-generated constructor stub
    }
    /**
     * @return the rect
     */
    public Rectangle getRect() {
        return rect;
    }
    /**
     * @param rect the rect to set
     */
    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

}
