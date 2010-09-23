/**
 * 
 */
package org.ihm.panel;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.app.CertificateInfo;
import org.ihm.TreeKeyStore;

/**
 *<pre>
 *<b></b>.
 * 
 *<b>Description :</b>
 *    
 * 
 *</pre>
 * @author C. Roger<BR>
 *  <BR>
 * Créé le 16 sept. 2010 <BR>
 *  <BR>
 *  <BR>
 * <i>Copyright : Tessi Informatique </i><BR>
 */
public class ListCertRenderer extends DefaultListCellRenderer {

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @param arg4
     * @return
     * 
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
	
	Component retValue  = 	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	// TODO Auto-generated method stub

	    if (value instanceof CertificateInfo) {
		CertificateInfo cert = ((CertificateInfo)value);
		ImageIcon icon = null;
		if (cert
			.isContainsPrivateKey()) {
		    icon = createImageIcon("images/certificatekey.png");
		} else {
		    icon = createImageIcon("images/certificate2.png");
		}
		if (icon != null) {

		    setIcon(icon);

		}
		setText(cert.getName());
		
	    }	
	//return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	return retValue;
    }
    
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
