package org.dpr.mykeys.ihm.windows;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CRLEntry;
import org.dpr.mykeys.app.crl.CRLManager;
import org.dpr.mykeys.app.crl.CrlValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.model.CRLEntryModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class CRLEditorDialog extends JDialog {
    private final static Log log = LogFactory.getLog(CRLEditorDialog.class);
    private final CRLEntryModel model;
    private JPanel contentPane0;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton exporterButton;
    private JTable table1;
    private JButton ajouterButton;
    private JLabel subTitle;
    private JLabel validityPeriodLabel;
    private final CertificateValue certificate;
    private final CRLService service;
    private File crlFile;
    CRLState state;

    public CRLEditorDialog(CertificateValue certificate) {

        this.certificate = certificate;
        service = new CRLService(certificate);
        setContentPane(contentPane0);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        exporterButton.addActionListener(e -> onExport());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane0.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        model = new CRLEntryModel();
        table1.setModel(model);
        init();

        ajouterButton.addActionListener(e -> addCertificate());
    }

    private void addCertificate() {
        //FIXME: does not work if crl not created;
        CertificateSelectDialog dialog = new CertificateSelectDialog(certificate.getChildren());
        dialog.pack();
        CRLEntry value = dialog.showDialog();
        if (value != null) {
            ((CRLEntryModel) table1.getModel()).addRow(value);
        }
    }

    private void onOK() {
        SaveCrlDialog dialog = new SaveCrlDialog(null);
        CrlValue value = dialog.showDialog();
        if (value != null) {
            //TODO: manage next update
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 7);

            try {
                service.saveCRL(value.getThisUpdate(), value.getNextUpdate(), ((CRLEntryModel) table1.getModel()).getValues());
            } catch (CRLException | ServiceException | IOException e) {
                e.printStackTrace();
            }
        }
        dispose();
    }

    private void onExport() {
        JFileChooser jfc = new JFileChooser();
        // the first, only, and selected filter is 'All Files'

        // jfc.setSelectedFile(outputFile);
        // jPanel1.add(jfc);
        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            String fileExport = jfc.getSelectedFile().getAbsolutePath();
            try {
                FileUtils.copyFile(crlFile, new File(fileExport));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            crlFile = f;
        } catch (FileNotFoundException e) {
            log.info("crl does not exist yet !");
            state = CRLState.NEW;
            setTitle(Messages.getString("crl.create.subtitle"));
            subTitle.setText("");
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane0 = new JPanel();
        contentPane0.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane0.add(panel1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        this.$$$loadButtonText$$$(buttonOK, ResourceBundle.getBundle("Messages").getString("button.save"));
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, ResourceBundle.getBundle("Messages").getString("button.cancel"));
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exporterButton = new JButton();
        exporterButton.setText("Exporter");
        panel1.add(exporterButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane0.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        subTitle = new JLabel();
        subTitle.setText("Validité de la CRL");
        panel3.add(subTitle, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        validityPeriodLabel = new JLabel();
        validityPeriodLabel.setText("");
        panel3.add(validityPeriodLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label1 = new JLabel();
        label1.setText("Certificats révoqués");
        panel3.add(label1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        ajouterButton = new JButton();
        ajouterButton.setText("Ajouter");
        panel3.add(ajouterButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane0.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane0.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane0;
    }

}
