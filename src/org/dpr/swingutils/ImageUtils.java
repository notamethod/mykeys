package org.dpr.swingutils;

import javax.swing.ImageIcon;

import org.dpr.mykeys.ihm.images.ImgRes;

public class ImageUtils {

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = ImgRes.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }

 
}
