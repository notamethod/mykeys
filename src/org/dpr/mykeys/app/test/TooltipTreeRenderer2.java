package org.dpr.mykeys.app.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.KeyStoreInfo;

public class TooltipTreeRenderer2 extends DefaultTreeCellRenderer implements
	TreeCellRenderer {

    private JLabel label = new JLabel( );

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
	    boolean sel, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {
	final Component rc = super.getTreeCellRendererComponent(tree, value,
		sel, expanded, leaf, row, hasFocus);
	String tooltip = null;
	
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
			icon = createImageIcon("images/keystoreblueo.png");
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

	    }
	    else if (node.getUserObject() instanceof CertificateInfo) {
		tooltip = ((CertificateInfo) node.getUserObject())
			.getSubjectString();
		ImageIcon icon = null;
		if (((CertificateInfo) node.getUserObject())
			.isContainsPrivateKey()) {
		    icon = createImageIcon("images/certificatekey.png");
		} else {
		    icon = createImageIcon("images/certificate2.png");
		}
		if (icon != null) {

		    setIcon(icon);

		}
	    }else{
//		    setBackgroundSelectionColor(new Color(10,20,30));
//		    setBackground(new Color(10,20,30));
	    }
	}
	if (tooltip != null) {
	    this.setToolTipText(tooltip);
	}
	setOpaque(false);
	return rc;

    }
    
 // @Override
  public Component getTreeCellRendererComponent1(JTree tree, Object value,
	    boolean isSelected, boolean isExpanded, boolean isLeaf, int row,
	    boolean hasFocus) {
      JLabel defaut = (JLabel)super.getTreeCellRendererComponent(
	            tree, value, isSelected, isExpanded,
	            isLeaf, row, hasFocus );
	 
	        label.setIcon( defaut.getIcon( ) );
	        label.setText( defaut.getText( ) );
	        label.setIconTextGap( defaut.getIconTextGap( ) );
	        label.setFont( defaut.getFont( ) );
	        label.setBackground( isSelected ?
	            getBackgroundSelectionColor( ) :
	            getBackgroundNonSelectionColor( ) );
	        label.setForeground( isSelected ?
	            getTextSelectionColor( ) :
	            getTextNonSelectionColor( ) );
	        label.setOpaque( isSelected );
	        return label;


  }    

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
//	java.net.URL imgURL = TreeKeyStore.class.getResource(path);
//	if (imgURL != null) {
//	    return new ImageIcon(imgURL);
//	} else {
//	    System.err.println("Couldn't find file: " + path);
	    return null;
	//}
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
	// TODO Auto-generated method stub
	
//  w = tree.getRowBounds(0).getWidth();
	Graphics2D g2d = (Graphics2D)g;
	int w = getWidth( );
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

}
