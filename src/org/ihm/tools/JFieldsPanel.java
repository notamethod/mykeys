package org.ihm.tools;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JFieldsPanel extends JPanel {

    public JFieldsPanel(JComponent jc1, JComponent jc2) {
	// TODO Auto-generated constructor stub
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
