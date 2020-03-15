package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.ihm.windows.CRLEditorDialog;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;

public class CertificateCADetailPanel extends CertificateDetailPanel {
    public CertificateCADetailPanel(CertificateValue info) {
        super(info);
    }

    @Override
    protected void addCrlPanel(LabelValuePanel infosPanel) {

        JButton jbCreate = new JButton(Messages.getString("edit"));
        jbCreate.addActionListener(e -> {

            CRLEditorDialog cs = new CRLEditorDialog(info);

            cs.pack();
            cs.setVisible(true);

        });

        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // jpDirectory.add(jl4);
//        jpDirectory.add(new JLabel("xxxxxxxx"));
        jpDirectory.add(jbCreate);


        infosPanel.put(Messages.getString("store.crl.name"),
                jpDirectory, true);
    }
}
