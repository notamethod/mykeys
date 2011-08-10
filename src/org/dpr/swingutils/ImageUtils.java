package org.dpr.swingutils;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.images.ImgRes;

public class ImageUtils {

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		System.out.println(path + " in");
		java.net.URL imgURL = ImgRes.class.getResource(path);
		System.out.println(path + " out " + imgURL);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Image createImage(String path) {
		System.out.println(path + " in");
		Image img = Toolkit.getDefaultToolkit().getImage(
				ImgRes.class.getResource(path));

		System.out.println(path + " out " + img);
		if (img != null) {
			return img;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
