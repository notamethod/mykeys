package org.ihm.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.app.KeyStoreInfo;
import org.ihm.TreeKeyStore;

public class GradientTreeRenderer extends DefaultTreeCellRenderer implements
	TreeCellRenderer {

    public JTree jtree1;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
	    boolean isSelected, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {
	final JLabel rc = (JLabel)super.getTreeCellRendererComponent(tree, value,
		isSelected, expanded, leaf, row, hasFocus);
	String tooltip = null;
	setOpenIcon(createImageIcon("images/Go-down.png"));
	setClosedIcon(createImageIcon("images/Go-previous.png"));
	
	Color colorb=UIManager.getColor("Tree.textBackground");
	Color colorf=UIManager.getColor("Tree.textForeground");
	rc.setForeground(colorf);
	
	// setTextNonSelectionColor( Color.black);
	if (value instanceof DefaultMutableTreeNode) {

	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	    if (node.getUserObject() instanceof KeyStoreInfo) {
		KeyStoreInfo kInfo = (KeyStoreInfo) node.getUserObject();
		tooltip = kInfo.getPath();
//		if (kInfo.isOpen()) {
		    ImageIcon icon = null;
		    // setTextNonSelectionColor( Color.green);
		    switch (kInfo.getStoreModel()) {
		    case CERTSTORE:
			icon = createImageIcon("images/keystoreblue.png");
			//icon = createImageIcon("images/keystoreblueo.png");
			break;
		    case CASTORE:
			icon = createImageIcon("images/keystorered.png");
			break;
		    default:
			icon = createImageIcon("images/keystoreblue.png");
			break;
		    }

		    if (icon != null) {

			setIcon(icon);

		    }
		    if (isSelected){
			rc.setForeground(colorb);
		    }
	    }

	    
	    else{
//
	    }
	}
	if (tooltip != null) {
	    this.setToolTipText(tooltip);
	}
	setOpaque(false);
	return rc;

    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = TreeKeyStore.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
	if(jtree1.getPathForLocation(getX(), getY()).getPathCount()!=2){
	    super.paintComponent( g );
	    return;
	}
	Graphics2D g2d = (Graphics2D)g;
	System.out.println(jtree1.getBounds().width);
	int w = getWidth();//jtree1.getBounds().width;//getWidth( );
	int h = getHeight( );
	 
	// Paint a gradient from top to bottom
	Color color1 = getBackground( );
	Color color2 = color1.darker( );
	GradientPaint gp = new GradientPaint(
	    0, 0, color1,
	    0, h, color2 );

	g2d.setPaint( gp );
	g2d.fillRect( 0, 0, w, h );	

	    super.paintComponent( g );


    }
    
   

//	@Override
//	public Dimension getPreferredSize() {
//	Dimension size = super.getPreferredSize();
//	size.width = jtree1.getBounds().width;
//	
//	return size;
//	}
//
//	@Override
//	public void setBounds(final int x, final int y, final int width, final int height) {
//	super.setBounds(x, y, Math.min(jtree1.getWidth()- x, width), height);
//
//	}    

}
