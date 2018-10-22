package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.app.certificate.CertificateValue;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CertificateSelectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private CertificateValue result;
    private CertificateValue selectedCertificate;

    public CertificateSelectDialog(List<CertificateValue> children) {
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
        List<String> lists = new ArrayList<>();
        for (CertificateValue value : children) {
            lists.add(value.getName());
        }
        comboBox1.setModel(new DefaultComboBoxModel(lists.toArray()));
    }

    private void onOK() {
        result = selectedCertificate;
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CertificateSelectDialog dialog = new CertificateSelectDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public CertificateValue showDialog() {

        setVisible(true);
        return result;
    }

}
