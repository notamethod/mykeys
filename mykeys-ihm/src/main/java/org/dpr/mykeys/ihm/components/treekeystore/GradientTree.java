package org.dpr.mykeys.ihm.components.treekeystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

class GradientTree extends JTree implements DropTargetListener {

    private final static Log log = LogFactory.getLog(GradientTree.class);
	public GradientTree(TreeModel newModel) {
		super(newModel);
		setUI(new GradientTreeUI());

	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
        log.debug("drag GTree");
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
        log.debug("drop");
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

}
