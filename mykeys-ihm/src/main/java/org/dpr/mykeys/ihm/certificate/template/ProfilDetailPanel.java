package org.dpr.mykeys.ihm.certificate.template;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.utils.CertificateUtils;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingtools.components.LabelValuePanel;

import java.util.Enumeration;

public class ProfilDetailPanel extends LabelValuePanel {

    private final CertificateTemplate info;

    public ProfilDetailPanel(CertificateTemplate info) {
        this.info = info;
        getPanel();
    }

    private void getPanel() {
        //infosPanel = new LabelValuePanel();
        this.put(Messages.getString("label.name"),
                JTextField.class, "", info.getName(), false);

        this.putEmptyLine();


        for (String attribute : SubjectUtil.getStandardList()) {
            String key = SubjectUtil.getLabels().get(attribute);
            this.put(Messages.getString(key), JTextField.class, attribute, info.getValue((attribute)), false);
        }
        this.putEmptyLine();


        for (Enumeration<String> e = info.getValues(); e.hasMoreElements(); ) {
            String attribute = e.nextElement();
            String key = SubjectUtil.getTemplateLabels().get(attribute);
            if (key != null && !key.startsWith("&"))
                this.put(Messages.getString(key), JTextField.class, attribute, info.getValue((attribute)), false);
        }

        this.put(Messages.getString("x509.subject.organisationUnit"),
                JTextArea.class, "algoPubKey", CertificateUtils.keyUsageToString(info.getIntValue("&keyUsage")), false);


    }
}
