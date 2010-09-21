package org.ihm;

import javax.swing.ImageIcon;

public class ImageUtils {

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = ImageUtils.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }    
}
