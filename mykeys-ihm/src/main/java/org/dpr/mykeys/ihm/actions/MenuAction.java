package org.dpr.mykeys.ihm.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.windows.*;
import org.dpr.mykeys.keystore.CreateStoreDialog;
import org.dpr.mykeys.keystore.ImportStoreDialog;

public class MenuAction extends AbstractAction {

    private final MykeysFrame MykeysFrame;


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
        new Thread(() -> {
            switch (action) {
                case "newStore":
                    SwingUtilities.invokeLater(() -> {
                        CreateStoreDialog cs = new CreateStoreDialog(
                                MykeysFrame, true);
                        cs.setLocationRelativeTo(MykeysFrame);
                        cs.setVisible(true);
                    });

                    break;
                case "loadStore":
                    SwingUtilities.invokeLater(() -> {
                        ImportStoreDialog cs = new ImportStoreDialog(
                                MykeysFrame, true);
                        cs.setLocationRelativeTo(MykeysFrame);

                        cs.setVisible(true);
                    });

                    break;
                case "addCrl":
                    SwingUtilities.invokeLater(() -> {
                        CreateCrlDialog cs = new CreateCrlDialog(
                                MykeysFrame, true);
                        cs.setLocationRelativeTo(MykeysFrame);
                        cs.setVisible(true);
                    });
                    break;
                case "options":
                    SwingUtilities.invokeLater(() -> {
                        Preferences cs = new Preferences();
                        cs.setLocationRelativeTo(MykeysFrame);
                        cs.setVisible(true);
                    });
                    break;
                case "profil":
                    SwingUtilities.invokeLater(() -> {
                        ManageTemplateFrame cs = new ManageTemplateFrame();
                        cs.setLocationRelativeTo(MykeysFrame);
                        cs.setVisible(true);
                    });

                    break;
                case "users":
                    SwingUtilities.invokeLater(() -> {
                        ManageUserDialog cs = null;
                        try {
                            cs = new ManageUserDialog();
                        } catch (ServiceException e1) {
                            e1.printStackTrace();
                            return;
                        }
                        cs.setLocationRelativeTo(MykeysFrame);
                        cs.setVisible(true);
                    });
                    break;
                case "signFile":
                    SwingUtilities.invokeLater(() -> {
                        VerifSigDialog cs = new VerifSigDialog(MykeysFrame,
                                true);
                        cs.setLocationRelativeTo(MykeysFrame);
                        cs.setVisible(true);
// SignDocumentDialog cs = new SignDocumentDialog(
// MykeysFrame, true);
// cs.setLocationRelativeTo(MykeysFrame);
// cs.setVisible(true);

                    });

                    break;
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