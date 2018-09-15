package org.dpr.mykeys.ihm.windows;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.dpr.mykeys.ihm.components.DetailPanel;
import org.dpr.mykeys.ihm.components.CertificateListPanel;

public class ListFrame extends JFrame {

	private DetailPanel detailPanel;
	private CertificateListPanel listePanel;

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ListFrame lf = new ListFrame();
				lf.init();
			}
		});
    }

    private void init() {
		JSplitPane splitLeftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		listePanel = new CertificateListPanel();
		
		// Create the viewing pane.
		detailPanel = new DetailPanel();

		JScrollPane scrollDetail = new JScrollPane(detailPanel);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
		splitLeftPanel.setBottomComponent(scrollDetail);
		splitLeftPanel.setTopComponent(listePanel);
		splitLeftPanel.setDividerLocation(150);		
		this.add(splitLeftPanel);
		this.pack();
		this.setVisible(true);
	}
	
}
