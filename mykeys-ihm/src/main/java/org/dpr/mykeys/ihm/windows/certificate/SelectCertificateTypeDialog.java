package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.keystore.CertificateType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SelectCertificateTypeDialog extends JDialog {

    public static void main(String[] args) {

    }


    private void init() {

        CertificateType typeCer;
//        ChangeListener changeListener = new ChangeListener() {
//            public void stateChanged(ChangeEvent changEvent) {
//                JRadioButton aButton = (JRadioButton) changEvent.getSource();
//
//                if (aButton.isSelected()) {
//                    typeCer = CertificateType.valueOf(aButton.getName());
//                }
//            }
//        };
//
//        final JRadioButton button1 = new JRadioButton("Client");
//        button1.setSelected(true);
//        button1.setName(CertificateType.STANDARD.toString());
//        button1.addChangeListener(changeListener);
//        final JRadioButton button2 = new JRadioButton("Serveur");
//        button2.setName(CertificateType.SERVER.toString());
//        final JRadioButton button3 = new JRadioButton("Signature de code");
//        button3.setName(CertificateType.CODE_SIGNING.toString());
//        button2.addChangeListener(changeListener);
//        button3.addChangeListener(changeListener);
//        ButtonGroup vanillaOrMod = new ButtonGroup();
//        vanillaOrMod.add(button1);
//        vanillaOrMod.add(button2);
//        vanillaOrMod.add(button3);
//        panel.add(button1);
//        this.setContentPane(panel);
//        this.pack();
//        this.setVisible(true);
//        JOptionPane.showMessageDialog(this.getParent(), panel, Messages.getString("type.certificat"), 1, null);


    }
}
