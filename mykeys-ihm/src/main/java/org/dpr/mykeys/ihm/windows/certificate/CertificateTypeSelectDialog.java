package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.CertificateType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class CertificateTypeSelectDialog extends JDialog {
    private JPanel contentPane0;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panel1;
    private JPanel panel0;
    private CertificateType typeCer;
    private CertificateType typeCerToReturn = null;


    public CertificateTypeSelectDialog(boolean ACAccepted) {
        $$$setupUI$$$();
        setContentPane(contentPane0);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

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
        init(ACAccepted);
        pack();
    }

    private void onOK() {
        typeCerToReturn = typeCer;
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        typeCer = null;
        dispose();
    }

    public static void main(String[] args) {
        CertificateTypeSelectDialog dialog = new CertificateTypeSelectDialog(true);
        dialog.init(true);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        panel1 = new JPanel();
        BoxLayout bls = new BoxLayout(panel1, BoxLayout.Y_AXIS);
        panel1.setLayout(bls);
    }

    private void init(boolean acAccepted) {

        ChangeListener changeListener = changEvent -> {
            JRadioButton aButton = (JRadioButton) changEvent.getSource();

            if (aButton.isSelected()) {
                typeCer = CertificateType.valueOf(aButton.getName());
            }
        };

        final JRadioButton button1 = new JRadioButton("Client");
        button1.setSelected(true);

        button1.setName(CertificateType.STANDARD.toString());
        button1.addChangeListener(changeListener);
        final JRadioButton button2 = new JRadioButton("AC");

        button2.setSelected(false);
        button2.setName(CertificateType.AC.toString());
        button2.addChangeListener(changeListener);
        final JRadioButton button3 = new JRadioButton("Code Signing");
        button3.setSelected(false);
        button3.setName(CertificateType.CODE_SIGNING.toString());
        button3.addChangeListener(changeListener);
        ButtonGroup vanillaOrMod = new ButtonGroup();
        vanillaOrMod.add(button1);
        vanillaOrMod.add(button2);
        vanillaOrMod.add(button3);
        //  vanillaOrMod.add(button3);
        panel1.add(button1);
        if (acAccepted)
            panel1.add(button2);
//        contentPane.add(panel1);
        panel1.add(button3);
        if (panel0 != null)
            panel0.add(panel1);

        setTitle(Messages.getString("type.certificat.select"));
    }

    public CertificateType showDialog() {
        setVisible(true);
        return typeCerToReturn;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane0 = new JPanel();
        contentPane0.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane0.setPreferredSize(new Dimension(350, 120));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane0.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel3.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel3.add(buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel0 = new JPanel();
        panel0.setLayout(new BorderLayout(0, 0));
        contentPane0.add(panel0, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel0.add(panel1, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane0;
    }
}
