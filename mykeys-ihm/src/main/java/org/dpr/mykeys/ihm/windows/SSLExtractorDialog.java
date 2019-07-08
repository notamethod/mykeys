package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.SSLCertificateExtractor;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import java.awt.event.*;

public class SSLExtractorDialog extends JDialog {
    private JPanel contentPane1;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JCheckBox fullChainCheckBox;
    private JCheckBox saveToMRUCheckBox;

    public SSLExtractorDialog() {
        setContentPane(contentPane1);
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
        contentPane1.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        init();
        pack();
    }

    private void init() {
    }

    private void onOK() {
        SSLCertificateExtractor extractor = new SSLCertificateExtractor(textField1.getText());
        try {
            String fileName = extractor.run(KSConfig.getDefaultCertificatePath(), fullChainCheckBox.isSelected());
            DialogUtil.showInfo(this, "Certificates saved to " + fileName);
            if (saveToMRUCheckBox.isSelected()) {
                KeyStoreHelper helper = new KeyStoreHelper();
                helper.importStore(fileName, StoreFormat.PEM, null);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SSLExtractorDialog dialog = new SSLExtractorDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
