package org.dpr.swingutils;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.images.ImgRes;

public class ImageUtils {

	public static final Log log = LogFactory.getLog(ImageUtils.class);
	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		log.trace(path + " in");
		java.net.URL imgURL = ImgRes.class.getResource(path);
		log.trace(path + " out " + imgURL);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Image createImage(String path) {
		log.trace(path + " in");
		Image img = Toolkit.getDefaultToolkit().getImage(
				ImgRes.class.getResource(path));

		log.trace(path + " out " + img);
		if (img != null) {
			return img;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
