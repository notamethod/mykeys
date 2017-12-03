package org.dpr.swingutils;

import org.dpr.mykeys.app.KSConfig;

public class JLabel extends javax.swing.JLabel {

	public JLabel(String text) {
		//

		try {
			text = KSConfig.getMessage().getString(text);
		} catch (Exception e) {
			//
		}
		setText(text);

		setIcon(null);

		setHorizontalAlignment(LEADING);

		updateUI();

		setAlignmentX(LEFT_ALIGNMENT);
	}

}
