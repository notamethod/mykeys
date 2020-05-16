package org.dpr.mykeys.ihm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.ihm.certificate.CertificateCADetailPanel;
import org.dpr.mykeys.ihm.certificate.CertificateDetailPanel;
import org.dpr.mykeys.ihm.certificate.template.ProfilDetailPanel;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DetailPanel extends JPanel {

    private static final Log log = LogFactory.getLog(DetailPanel.class);
    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;

    CertificateValue certificatInfo;

    private JPanel jp;
    private JTabbedPane jtab;

    private JLabel titre = new JLabel();

    public DetailPanel() {
        super();
        init();

    }

    private void init() {
        ActionPanel dAction = new ActionPanel();

        BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(bl);

        titre = new JLabel("");
        jtab = new JTabbedPane();
        jp = new JPanel();
        JPanel jpExt = new JPanel();

        jp.setLayout(new FlowLayout(FlowLayout.LEADING));
        jpExt.setLayout(new FlowLayout(FlowLayout.LEADING));

        add(jtab);
        jtab.add(jp, "Informations");
        //jtab.add(jpExt, "Informations étendues");
        // jp.add();
        // jp.add(new JLabel("Contenu du certificat"));
        jtab.setVisible(false);
    }

    public void updateInfoGen(ChildInfo info) {


        //jp.add(infosPanel);

    }

    private Component getDetailInstance(ChildInfo info) {
        if (info instanceof CertificateValue) {
            CertificateValue value = (CertificateValue) info;
            if (value.getType() != null && value.getType().equals(CertificateType.AC))
                return new CertificateCADetailPanel(value);
            else
                return new CertificateDetailPanel(value);
        } else {
            return new ProfilDetailPanel((CertificateTemplate) info);
        }
    }


    class ActionPanel extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            switch (command) {
                case "CHECK_OCSP":
                    log.trace("OCSP");

                    break;
                case "OK":
                    break;
                case "CANCEL":
                    // ExportCertificateDialog.this.setVisible(false);
                    break;
            }

        }

    }

    public void updateInfo(ChildInfo info) {
        // FIXME: repaint component ?
        if (info == null) {
            jtab.setVisible(false);
            return;
        }
        jp.removeAll();
        //jtab.revalidate();
        jp.add(getDetailInstance(info));
        titre.setText(Messages.getString("detail.cert.title"));
        if (info instanceof CertificateValue) {
            jtab.setTitleAt(0, ((CertificateValue) info).getAlias());
        }

        jtab.setVisible(true);
        jtab.revalidate();

    }

}