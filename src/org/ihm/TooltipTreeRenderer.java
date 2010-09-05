package org.ihm;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.app.CertificateInfo;
import org.app.KeyStoreInfo;
import org.app.KeyStoreInfo.StoreType;

public class TooltipTreeRenderer extends DefaultTreeCellRenderer implements
	TreeCellRenderer {

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
		if (kInfo.isOpen()) {
		    ImageIcon icon = null;
		    // setTextNonSelectionColor( Color.green);
		    switch (kInfo.getStoreType()) {
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
		else{
		    ImageIcon icon = null;
		    // setTextNonSelectionColor( Color.green);
		    switch (kInfo.getStoreType()) {
		    case CERTSTORE:
			icon = createImageIcon("images/keystoreblue.png");
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
		// ImageIcon icon = createImageIcon("images/keystore.png");
		// if (icon != null) {
		//					
		// setIcon(icon);
		//					
		// }
	    }
	    if (node.getUserObject() instanceof CertificateInfo) {
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
	    }
	}
	if (tooltip != null) {
	    this.setToolTipText(tooltip);
	}
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

}
