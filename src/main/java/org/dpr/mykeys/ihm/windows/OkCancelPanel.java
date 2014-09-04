package org.dpr.mykeys.ihm.windows;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.dpr.swingutils.JFieldsPanel;

public class OkCancelPanel extends JPanel {

	public OkCancelPanel(ActionListener aListener) {
		super();
		setLayout(new FlowLayout(FlowLayout.LEADING));
		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(aListener);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(aListener);
		jbCancel.setActionCommand("CANCEL");

		add(jbOK);
		add(jbCancel);
	}
	public OkCancelPanel(ActionListener aListener, int position) {
		super();
		setLayout(new FlowLayout(position));
		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(aListener);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(aListener);
		jbCancel.setActionCommand("CANCEL");
	
		add(jbOK);
		add(jbCancel);
	}

}
