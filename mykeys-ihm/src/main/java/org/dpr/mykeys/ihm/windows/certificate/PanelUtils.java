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

	//suffix key for default values in resource bundle
	public static final String DEFAULT_STD = ".default";
	public static final String DEFAULT_AC = ".acdefault";

	public static void addSubjectToPanel(CertificateType type, LabelValuePanel infosPanel) {

		infosPanel.addTitle(Messages.getString("x509.subject"));
		String defaultKey = CertificateType.STANDARD.equals(type) ? DEFAULT_STD : DEFAULT_AC;
//		switch (type) {
//
//		case STANDARD:
//
//			break;
//		case AC:
//		for (String attribute : SubjectUtil.getStandardList()) {
//				String key = SubjectUtil.getLabels().get(attribute);
//				infosPanel.put(Messages.getString(key), attribute, Messages.getString(key + ".acdefault"));
//			}
//			break;
//		default:
//			break;
//		}
			for (String attribute : SubjectUtil.getStandardList()) {
				String key = SubjectUtil.getLabels().get(attribute);
				infosPanel.put(Messages.getString(key), attribute, Messages.getString(key + defaultKey));
			}
		infosPanel.putEmptyLine();

	}

}
