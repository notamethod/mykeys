package org.dpr.mykeys.ihm.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.ihm.windows.certificate.CertificateCADetailPanel;
import org.dpr.mykeys.ihm.windows.certificate.CertificateDetailPanel;
import org.dpr.mykeys.template.ProfilDetailPanel;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DetailPanel extends JPanel {

    private static final Log log = LogFactory.getLog(DetailPanel.class);
    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;

    CertificateValue certificatInfo;

    private ActionPanel dAction;

    private JPanel jp;
    private JPanel jpExt;
    private JTabbedPane jtab;

    private JLabel titre = new JLabel();

    public DetailPanel() {
        super();
        init();

    }

    private void init() {
        dAction = new ActionPanel();

        BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(bl);

        titre = new JLabel("");
        jtab = new JTabbedPane();
        jp = new JPanel();
        jpExt = new JPanel();

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
            if (command.equals("CHECK_OCSP")) {
                log.trace("OCSP");

            } else if (command.equals("OK")) {

                KeyTools kt = new KeyTools();

            } else if (command.equals("CANCEL")) {
                // ExportCertificateDialog.this.setVisible(false);
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
            jtab.setTitleAt(0, ((CertificateValue) ((CertificateValue) info)).getAlias());
        }

        jtab.setVisible(true);
        jtab.revalidate();

    }

}
