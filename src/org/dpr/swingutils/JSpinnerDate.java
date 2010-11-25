package org.dpr.swingutils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class JSpinnerDate extends JSpinner {

    SpinnerDateModel model;

    public JSpinnerDate(String format, Date initDate) {
	super();
	init(format, initDate);

    }

    private void init(String format, Date initDate) {

	Calendar calendar = new GregorianCalendar();
	if (initDate == null) {
	    initDate = calendar.getTime();
	}
	calendar.add(Calendar.YEAR, -100);
	Date earliestDate = calendar.getTime();
	calendar.add(Calendar.YEAR, 200);
	Date latestDate = calendar.getTime();
	model = new SpinnerDateModel(initDate, earliestDate, latestDate,
		Calendar.YEAR);
	this.setModel(model);
	this.setEditor(new JSpinner.DateEditor(this, format));

    }

}
