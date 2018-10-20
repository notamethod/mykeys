package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.windows.CreateCrlDialog;
import org.dpr.swingtools.components.JFieldsPanel;
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

        JButton jbCreate = new JButton("...");
        JButton jbChoose2 = new JButton("...");
        //jbChoose.addActionListener(dAction);
        jbCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                CreateCrlDialog cs = new CreateCrlDialog(null, info);

                cs.setLocationRelativeTo(null);
                cs.setResizable(false);
                cs.setVisible(true);

            }
        });
        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // jpDirectory.add(jl4);
        jpDirectory.add(new JLabel("xxxxxxxx"));
        jpDirectory.add(jbCreate);


        infosPanel.put(Messages.getString("dialog.generic.fileout"),
                jpDirectory, true);
    }
}
