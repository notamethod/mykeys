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

	private MykeysFrame MykeysFrame;


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
				} else if (action.equals("options")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Preferences cs = new Preferences();
							cs.setLocationRelativeTo(MykeysFrame);
							cs.setVisible(true);
						}
					});
				} else if (action.equals("profil")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							ManageTemplateFrame cs = new ManageTemplateFrame();
							cs.setLocationRelativeTo(MykeysFrame);
							cs.setVisible(true);
						}
					});

				} else if (action.equals("users")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							ManageUserDialog cs = null;
							try {
								cs = new ManageUserDialog();
							} catch (IhmException e1) {
								e1.printStackTrace();
							} catch (ServiceException e1) {
								e1.printStackTrace();
							}
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