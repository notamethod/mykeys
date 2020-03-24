package org.dpr.mykeys.utils;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.utils.SubjectUtil;
import org.dpr.swingtools.components.JSpinnerDate;
import org.dpr.swingtools.components.LabelValuePanel;

import java.util.Calendar;
import java.util.Date;

public class PanelUtils {

    public static final String EMAIL_KEY = "x509.subject.email";
    public static final String LOCATION_KEY = "x509.subject.email";
    public static final String COUNTRY_KEY = "x509.subject.email";
    public static final String ORG_KEY = "x509.subject.email";
    public static final String ORG_UNIT_KEY = "x509.subject.email";
    public static final String STREET_KEY = "x509.subject.email";

    //suffix key for default values in resource bundle
    private static final String DEFAULT_STD = ".default";
    private static final String DEFAULT_AC = ".acdefault";

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
            // infosPanel.put(Messages.getString(key), attribute, Messages.getString(key + defaultKey));
            if (key != null && key.equals("x509.subject.country") && StringUtils.isBlank(Messages.getString(key + defaultKey)))
                infosPanel.put(Messages.getString(key), attribute, System.getProperty("user.country"));
            else
                infosPanel.put(Messages.getString(key), attribute, Messages.getString(key + defaultKey));
        }

        infosPanel.putEmptyLine();

    }

    public static LabelValuePanel getDurationPanel(int duration, LabelValuePanel parent, boolean editable) {

        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.YEAR, duration);
        return getDurationPanel(calendar.getTime(), calendar2.getTime(), parent, editable);
    }

    public static LabelValuePanel getDurationPanel(Date NotBefore, Date notAfter, LabelValuePanel parent, boolean editable) {

        Calendar calendar = Calendar.getInstance();
        LabelValuePanel durationPanel = new LabelValuePanel();

        durationPanel.addTitle(Messages.getString("valid.period.title"));
        durationPanel.put(Messages.getString("x509.startdate"), JSpinnerDate.class, "notBefore",
                NotBefore, editable);

        durationPanel.put(Messages.getString("x509.enddate"), JSpinnerDate.class, "notAfter",
                notAfter, editable);
        // durationPanel.setVisible(false);
        durationPanel.putEmptyLine();
        parent.addChild(durationPanel);
        return durationPanel;
    }

}
