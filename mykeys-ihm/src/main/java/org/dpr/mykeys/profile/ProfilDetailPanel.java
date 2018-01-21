package org.dpr.mykeys.profile;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingutils.LabelValuePanel;

import java.util.Enumeration;

public class ProfilDetailPanel extends LabelValuePanel {

    private CertificateTemplate info;

    public ProfilDetailPanel(CertificateTemplate info) {
		this.info=info;
		getPanel();
	}
  
	public void getPanel(){
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
            String key = SubjectUtil.getCertificateLabels().get(attribute);
            if (key != null && !key.startsWith("&"))
                this.put(Messages.getString(key), JTextField.class, attribute, info.getValue((attribute)), false);
        }

        this.put(Messages.getString("x509.subject.organisationUnit"),
                JTextArea.class, "algoPubKey", CertificateUtils.keyUsageToString(info.getIntValue("&keyUsage")), false);


	}
}
