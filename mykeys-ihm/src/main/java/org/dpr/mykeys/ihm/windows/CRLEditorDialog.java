package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CRLManager;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.security.cert.X509CRL;

public class CRLEditorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton exporterButton;
    private JTable table1;
    private JButton ajouterButton;
    private JButton buttonModifyMessagesButton;
    private JLabel subTitle;
    private JLabel validityPeriodLabel;
    private CertificateValue certificate;
    private CRLService service;
    CRLState state;

    public CRLEditorDialog(CertificateValue certificate) {

        this.certificate = certificate;
        service = new CRLService(certificate);
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
        init();

        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCertificate();
            }
        });
    }

    private void addCertificate() {
        ;
        CertificateSelectDialog dialog = new CertificateSelectDialog(certificate.getChildren());
        dialog.pack();
        dialog.showDialog();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CRLEditorDialog dialog = new CRLEditorDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void init() {

        File f = new File(KSConfig.getDefaultCrlPath(certificate));
        if (!f.exists()) {
            state = CRLState.NEW;
            setTitle(Messages.getString("crl.create.subtitle"));
            subTitle.setText("");
        } else {
            setTitle(Messages.getString("crl.edit.subtitle"));
            X509CRL crl = service.loadCRL(f);
            if (CRLManager.EtatCrl.UP_TO_DATE.equals(service.getValidity())) {
                subTitle.setText("CRL OK");
            } else {
                subTitle.setText("CRL TOO OLD");
            }
            validityPeriodLabel.setText(Messages.getString("crl.validity.period", crl.getThisUpdate(), crl.getNextUpdate()));

        }

    }
}
