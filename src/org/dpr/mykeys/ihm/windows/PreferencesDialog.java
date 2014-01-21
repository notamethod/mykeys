package org.dpr.mykeys.ihm.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

public class PreferencesDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PreferencesDialog dialog = new PreferencesDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PreferencesDialog() {
		setBounds(100, 100, 619, 407);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 603, 336);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Conserever les mots de passe en session");
		chckbxNewCheckBox.setBounds(6, 74, 223, 23);
		contentPanel.add(chckbxNewCheckBox);
		
		JCheckBox chckbxEnregistrerLesMots = new JCheckBox("Enregistrer les mots de passe");
		chckbxEnregistrerLesMots.setBounds(6, 100, 223, 23);
		contentPanel.add(chckbxEnregistrerLesMots);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 336, 603, 33);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
