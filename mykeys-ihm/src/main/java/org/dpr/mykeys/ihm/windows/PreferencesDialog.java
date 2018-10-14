package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.app.KSConfig;

import javax.swing.*;
import java.awt.event.*;

public class PreferencesDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton flatRadioButton;
    private JRadioButton treeRadioButton;

    public PreferencesDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        String viewType = KSConfig.getUserCfg().getString("certificate.list.style", "flat");
        if (viewType.equalsIgnoreCase("tree"))
            treeRadioButton.setSelected(true);
        else
            flatRadioButton.setSelected(true);

    }

    private void onOK() {
        if (treeRadioButton.isSelected()) {
            KSConfig.getUserCfg().setProperty("certificate.list.style", "tree");
        } else {
            KSConfig.getUserCfg().setProperty("certificate.list.style", "flat");
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        KSConfig.initResourceBundle();
        KSConfig.init();
        PreferencesDialog dialog = new PreferencesDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
