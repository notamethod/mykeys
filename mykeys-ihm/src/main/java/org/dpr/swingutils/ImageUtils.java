package org.dpr.swingutils;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ImageUtils {

    private static final Log log = LogFactory.getLog(ImageUtils.class);
	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		log.trace(path + " in");
		java.net.URL imgURL = ImageUtils.class.getResource(path);
		log.trace(path + " out " + imgURL);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Image getImage(String path) {
		log.trace(path + " in");
		Image img = Toolkit.getDefaultToolkit().getImage(
				ImageUtils.class.getResource(path));

		log.trace(path + " out " + img);
		if (img != null) {
			return img;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
