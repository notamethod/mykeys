package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.components.CertificateComboxModel;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificateSelectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JPanel reasonPanel;
    private CertificateValue result;
    private CertificateValue selectedCertificate;
    Map<String, CertificateValue> certificateMap = new HashMap<>();

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
        CertificateValue[] lists = new CertificateValue[children.size()];

        int i = 0;
        for (CertificateValue value : children) {
            certificateMap.put(value.getSubjectString(), value);
            comboBox1.addItem(value.getSubjectString());
        }

    }

    private void onOK() {
        if (comboBox1.getSelectedItem() != null && certificateMap.get(comboBox1.getSelectedItem()) != null)
            selectedCertificate = certificateMap.get(comboBox1.getSelectedItem());
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

    private void createUIComponents() {
        reasonPanel = new JPanel();
    }
}
