package org.dpr.mykeys.ihm.components;

import static org.dpr.swingtools.ImageUtils.createImageIcon;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListModel;

class JImgList extends JList {

	private final ImageIcon icon = createImageIcon("/images/cad1.png");
    private final Image grayImage = GrayFilter.createDisabledImage(icon.getImage());
	private boolean showImage = false;

	/**
	 * @return the showImage
	 */
	public boolean isShowImage() {
		return showImage;
	}

	/**
	 * @param showImage
	 *            the showImage to set
	 */
	public void setShowImage(boolean showImage) {
		this.showImage = showImage;
	}

	public JImgList(ListModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (showImage) {
			Dimension d = getSize();
			int iconH = icon.getIconHeight();
			int height = icon.getIconHeight();
			int ratiohw = icon.getIconHeight() / icon.getIconWidth();
			int ratiowh = icon.getIconWidth() / icon.getIconHeight();
			int center = d.width - icon.getIconWidth();
			// if (d.height>icon.getIconHeight())
			height = d.height;

			g.drawImage(grayImage, center / 2, 0, height * ratiowh, height,
					null);

			// Approach 3: Fix the image position in the scroll pane
			// Point p = scrollPane.getViewport().getViewPosition();
			// g.drawImage(icon.getImage(), p.x, p.y, null);

			setOpaque(false);
		}
		super.paintComponent(g);
	}

}
