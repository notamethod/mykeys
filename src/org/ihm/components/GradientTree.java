package org.ihm.components;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;


public class GradientTree extends JTree {

    public GradientTree(TreeModel newModel) {
	super(newModel);
	setUI(new GradientTreeUI());
	 
    }



  
}
