package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.windows.CRLEditorDialog;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CertificateCADetailPanel extends CertificateDetailPanel {
    public CertificateCADetailPanel(CertificateValue info) {
        super(info);
    }

    @Override
    protected void addCrlPanel(LabelValuePanel infosPanel) {
        System.out.println("CRL");

        JButton jbCreate = new JButton(Messages.getString("edit"));
        JButton jbChoose2 = new JButton("...");
        //jbChoose.addActionListener(dAction);
        jbCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                CRLEditorDialog cs = new CRLEditorDialog(info);

                cs.pack();
                cs.setVisible(true);

            }
        });

        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // jpDirectory.add(jl4);
//        jpDirectory.add(new JLabel("xxxxxxxx"));
        jpDirectory.add(jbCreate);


        infosPanel.put(Messages.getString("store.crl.name"),
                jpDirectory, true);
    }
}
