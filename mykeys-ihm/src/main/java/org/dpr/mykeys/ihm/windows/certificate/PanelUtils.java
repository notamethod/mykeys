package org.dpr.mykeys.ihm.windows.certificate;

import java.util.Locale;
import java.util.ResourceBundle;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingutils.LabelValuePanel;

public class PanelUtils {
	
	public static final String EMAIL_KEY="x509.subject.email";
	public static final String LOCATION_KEY="x509.subject.email";
	public static final String COUNTRY_KEY="x509.subject.email";
	public static final String ORG_KEY="x509.subject.email";
	public static final String ORG_UNIT_KEY="x509.subject.email";
	public static final String STREET_KEY="x509.subject.email";


	public static void addSubjectToPanel(CertificateType type, LabelValuePanel infosPanel) {

		infosPanel.addTitle(Messages.getString("x509.subject"));
		switch (type) {
		case STANDARD:
			for (String attribute : SubjectUtil.getStandardList()) {
				String key = SubjectUtil.getLabels().get(attribute);
				infosPanel.put(Messages.getString(key), attribute, Messages.getString(key + ".default"));
			}
			break;
		case AC:
			infosPanel.put("Nom (CN)", "CN", "MyKeys Root CA");
			infosPanel.put("Pays (C)", "C", "FR");
			infosPanel.put("Organisation (O)", "O", "MyKeys");
			infosPanel.put("Section (OU)", "OU", "");
			infosPanel.put("Localité (L)", "L", "");
			infosPanel.put("Rue (ST)", "SR", "");
			infosPanel.put("Email (E)", "E", "");
			break;
		default:
			break;
		}

		infosPanel.putEmptyLine();

	}

}
