package org.dpr.mykeys.ihm.actions;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.dpr.mykeys.ihm.windows.CreateCrlDialog;
import org.dpr.mykeys.ihm.windows.CreateStoreDialog;
import org.dpr.mykeys.ihm.windows.ImportStoreDialog;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.SignDocumentDialog;
import org.dpr.mykeys.ihm.windows.VerifSigDialog;

public class MenuAction extends AbstractAction {

    private MykeysFrame MykeysFrame;

    ResourceBundle messages = ResourceBundle.getBundle(
	    "org.dpr.mykeys.config.Messages", Locale.getDefault());

    public MenuAction(Object MykeysFrame, String string) {
	super(string);
	this.MykeysFrame = (MykeysFrame) MykeysFrame;
    }

    public MenuAction(Object MykeysFrame, String nomImg, ImageIcon ic) {
	super(nomImg, ic);
	this.MykeysFrame = (MykeysFrame) MykeysFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	final String action = e.getActionCommand();
	final Object composant = e.getSource();
	new Thread(new Runnable() {
	    public void run() {
		if (action.equals("newStore")) {
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    CreateStoreDialog cs = new CreateStoreDialog(
				    MykeysFrame, true);
			    cs.setLocationRelativeTo(MykeysFrame);
			    cs.setVisible(true);
			}
		    });

		} else if (action.equals("loadStore")) {
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ImportStoreDialog cs = new ImportStoreDialog(
				    MykeysFrame, true);
			    cs.setLocationRelativeTo(MykeysFrame);

			    cs.setVisible(true);
			}
		    });

		} else if (action.equals("addCrl")) {
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    CreateCrlDialog cs = new CreateCrlDialog(
				    MykeysFrame, true);
			    cs.setLocationRelativeTo(MykeysFrame);
			    cs.setVisible(true);
			}
		    });

		} else if (action.equals("signFile")) {
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    VerifSigDialog cs = new VerifSigDialog(MykeysFrame,
				    true);
			    cs.setLocationRelativeTo(MykeysFrame);
			    cs.setVisible(true);
			    // SignDocumentDialog cs = new SignDocumentDialog(
			    // MykeysFrame, true);
			    // cs.setLocationRelativeTo(MykeysFrame);
			    // cs.setVisible(true);

			}
		    });

		}

	    }
	}).start();

    }

}
// javax.swing.SwingUtilities.invokeLater(new Runnable() {
// public void run() {
// createAndShowGUI();
// }
// });

// CreateStoreDialog cs = new CreateStoreDialog(MykeysFrame,
// true);
// cs.setLocationRelativeTo(MykeysFrame);
// cs.setVisible(true);