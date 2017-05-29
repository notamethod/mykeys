package org.dpr.mykeys.ihm.windows;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;


public class Preferences extends JDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("Messages"); //$NON-NLS-1$

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Preferences dialog = new Preferences();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Preferences() {
		setBounds(100, 100, 514, 533);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblPreferences = new JLabel(BUNDLE.getString("Preferences.lblPreferences.text")); //$NON-NLS-1$
		lblPreferences.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPreferences.setBounds(12, 13, 117, 16);
		contentPanel.add(lblPreferences);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 49, 592, 16);
		contentPanel.add(separator);
		
		JCheckBox chckbxNebox = new JCheckBox(BUNDLE.getString("Preferences.chckbxNebox.text")); //$NON-NLS-1$
		chckbxNebox.setBounds(0, 74, 224, 25);
		contentPanel.add(chckbxNebox);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
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
