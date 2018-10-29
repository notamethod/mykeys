package org.dpr.mykeys.ihm.windows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CRLEntry;
import org.dpr.mykeys.app.crl.CRLManager;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.model.CRLEntryModel;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Calendar;
import java.util.Date;

public class CRLEditorDialog extends JDialog {
    private final static Log log = LogFactory.getLog(CRLEditorDialog.class);
    private final CRLEntryModel model;
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
        model = new CRLEntryModel();
        table1.setModel(model);
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
        CRLEntry value = dialog.showDialog();
        if (value != null) {
            ((CRLEntryModel) table1.getModel()).addRow(value);
        }
    }

    private void onOK() {
        Date nextUpdate;
        if (service.getCRL() != null && service.getCRL().getNextUpdate() != null)
            nextUpdate = service.getCRL().getNextUpdate();
        else
            nextUpdate = new Date();
        //TODO: manage next update
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 7);
        nextUpdate = cal.getTime();
        try {
            service.saveCRL(nextUpdate, ((CRLEntryModel) table1.getModel()).getValues());
        } catch (CRLException | ServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            X509CRL crl = service.loadCRL(f);
            setTitle(Messages.getString("crl.edit.subtitle"));
            if (CRLManager.EtatCrl.UP_TO_DATE.equals(service.getValidity())) {
                subTitle.setText("CRL OK");
            } else {
                subTitle.setText("CRL TOO OLD");
            }
            if (crl.getRevokedCertificates() != null) {
                for (X509CRLEntry entry : crl.getRevokedCertificates()) {
                    String subject = "";
                    for (CertificateValue child : certificate.getChildren()) {
                        if (child.getCertificate().getSerialNumber().equals(entry.getSerialNumber()))
                            subject = child.getSubjectString();
                    }
                    model.addRow(new CRLEntry(entry, subject));
                }
            }
            validityPeriodLabel.setText(Messages.getString("crl.validity.period", crl.getThisUpdate(), crl.getNextUpdate()));
        } catch (FileNotFoundException e) {
            log.info("crl does not exist yet !");
            state = CRLState.NEW;
            setTitle(Messages.getString("crl.create.subtitle"));
            subTitle.setText("");
        }
    }
}
