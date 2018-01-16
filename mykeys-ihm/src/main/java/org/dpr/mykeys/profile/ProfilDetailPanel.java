package org.dpr.mykeys.profile;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingutils.LabelValuePanel;

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
        for (Object o : info.getValues()) {
            String attribute = (String) o;
            String key = SubjectUtil.getCertificateLabels().get(attribute);
            if (key != null && !key.startsWith("&"))
                this.put(Messages.getString(key), JTextField.class, attribute, info.getValue((attribute)), false);
        }


        KeyUsage ku = new KeyUsage(info.getIntValue("&keyUSage"));
        int ku2 = info.getIntValue("&keyUSage");
	if ((ku2 & KeyUsage.digitalSignature) == KeyUsage.digitalSignature)
		System.out.println("xx");
	if ((ku2 & KeyUsage.decipherOnly) == KeyUsage.decipherOnly)
		System.out.println("yy");
	if ((ku2 & KeyUsage.dataEncipherment) == KeyUsage.dataEncipherment)
		System.out.println("zz");


        this.put(Messages.getString("x509.subject.organisationUnit"),
                JTextArea.class, "algoPubKey", CertificateUtils.keyUsageToString(info.getIntValue("&keyUSage")), false);


	}
}
