package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.CertificateType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class CertificateTypeSelectDialog extends JDialog {
    private JPanel contentPane1;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panel1;
    private JPanel panel0;
    private CertificateType typeCer;
    private CertificateType typeCerToReturn = null;


    public CertificateTypeSelectDialog(boolean ACAccepted) {
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

        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changEvent) {
                JRadioButton aButton = (JRadioButton) changEvent.getSource();

                if (aButton.isSelected()) {
                    typeCer = CertificateType.valueOf(aButton.getName());
                }
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
}
