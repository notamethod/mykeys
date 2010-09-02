package org.ihm.menuaction;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.ihm.KeyStoreUI;
import org.ihm.panel.CreateStoreDialog;
import org.ihm.panel.ImportStoreDialog;

public class MenuAction extends AbstractAction {

    private KeyStoreUI keyStoreUI;

    ResourceBundle messages = ResourceBundle.getBundle("org.config.Messages",
	    Locale.getDefault());

    public MenuAction(Object keyStoreUI, String string) {
	super(string);
	this.keyStoreUI = (KeyStoreUI) keyStoreUI;
    }

    public MenuAction(Object keyStoreUI, String nomImg, ImageIcon ic) {
	super(nomImg, ic);
	this.keyStoreUI = (KeyStoreUI) keyStoreUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	final String action = e.getActionCommand();
	final Object composant = e.getSource();
	new Thread(new Runnable() {
	    public void run() {
		if (action.equals("newStore")) {
		    CreateStoreDialog cs = new CreateStoreDialog(keyStoreUI,
			    true);
		    cs.setLocationRelativeTo(keyStoreUI);

		    cs.setVisible(true);
		} else if (action.equals("loadStore")) {
		    ImportStoreDialog cs = new ImportStoreDialog(keyStoreUI,
			    true);
		    cs.setLocationRelativeTo(keyStoreUI);

		    cs.setVisible(true);

		}

	    }
	}).start();

    }

}
