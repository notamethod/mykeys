package org.dpr.swingutils;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JFieldsPanel extends JPanel {

    public JFieldsPanel(JComponent jc1, JComponent jc2) {
	setLayout(new FlowLayout(FlowLayout.LEADING));
	add(jc1);
	add(jc2);
    }

    public JFieldsPanel(JComponent jc1, JComponent jc2, int position) {
	setLayout(new FlowLayout(position));
	add(jc1);
	add(jc2);
    }

}
