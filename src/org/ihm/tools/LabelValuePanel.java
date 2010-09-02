package org.ihm.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.app.KSConfig;
import org.ihm.JSpinnerDate;

public class LabelValuePanel extends JPanel implements DocumentListener {

    int nbRows;

    int nbCols;

    int tfSize;

    Map<String, Object> elements;

    public LabelValuePanel() {
	super();
	this.setLayout(new SpringLayout());
	this.elements = new HashMap<String, Object>();

    }

    public LabelValuePanel(Map<String, String> elements2, int cols) {
	this.nbCols = cols;
    }

    public void put(String label, String id, String defaultValue) {
	final String globalKey = id;
	JLabel jl = new JLabel(label);
	JTextField jt = new JTextField(20);
	jt.setText(defaultValue);
	jt.setName(id);
	if (elements.get(globalKey) == null) {
	    elements.put(globalKey, defaultValue);
	}
	jt.getDocument().addDocumentListener(new DocumentListener() {
	    @Override
	    public void changedUpdate(DocumentEvent e) {
		String value = null;
		try {
		    value = e.getDocument().getText(0,
			    e.getDocument().getLength());
		} catch (BadLocationException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		elements.put(globalKey, value);
	    }

	    @Override
	    public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);

	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	    }
	});
	this.add(jl);
	this.add(jt);
	nbRows++;
	SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);
    }

    public void put(String label, Class<?> class1, String keyValue,
	    Map<String, String> values) {
	JLabel jl = new JLabel(label);

	JComponent component = null;
	if (class1.getName().equals(JComboBox.class.getName())) {
	    component = putCombo(keyValue, values, "");
	}

	this.add(jl);
	this.add(component);
	nbRows++;
	SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);

    }

    public void put(String label, Class<?> class1, String keyValue,
	    Map<String, String> values, String defaultValue) {
	JLabel jl = new JLabel(label);

	JComponent component = null;
	if (class1.getName().equals(JComboBox.class.getName())) {
	    component = putCombo(keyValue, values, defaultValue);
	}

	this.add(jl);
	this.add(component);
	nbRows++;
	SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);

    }

    public JComponent putCombo(String keyValue, Map<String, String> values,
	    String defaultValue) {

	final String globalKey = keyValue;
	final Map<String, String> map = values;
	JComboBox combo = new JComboBox();
	Set<String> keys = values.keySet();
	Iterator<String> it = keys.iterator();
	while (it.hasNext()) {
	    String key = it.next();
	    combo.addItem(key);
	    if (elements.get(globalKey) == null) {
		elements.put(globalKey, map.get(key));
	    }
	}

	combo.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JComboBox combo = (JComboBox) e.getSource();
		String key = (String) combo.getSelectedItem();
		elements.put(globalKey, map.get(key));
	    }
	});
	combo.setSelectedItem(defaultValue);
	return combo;
    }

    public void put(String label, Class<?> class1, String keyValue,
	    String value, boolean isEditable) {
	JLabel jl = new JLabel(label);

	final String globalKey = keyValue;
	JComponent component = null;
	if (class1.getName().equals(JLabel.class.getName())) {
	    JLabel labelValue = new JLabel();
	    labelValue.setText(value);

	    component = labelValue;
	} else if (class1.getName().equals(JPasswordField.class.getName())) {
	    JPasswordField pwdField = new JPasswordField(value);
	    pwdField.setEditable(isEditable);
	    elements.put(globalKey, value);
	    pwdField.getDocument().addDocumentListener(new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
		    String value = null;
		    try {
			value = e.getDocument().getText(0,
				e.getDocument().getLength());
		    } catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		    elements.put(globalKey, value);

		}

		@Override
		public void insertUpdate(DocumentEvent e) {
		    changedUpdate(e);

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		    changedUpdate(e);
		}
	    });

	    component = pwdField;
	} else if (class1.getName().equals(JTextArea.class.getName())) {
	    JTextArea textArea = new JTextArea(5, 20);
	    textArea.setText(value);
	    textArea.setLineWrap(true);
	    textArea.setEditable(isEditable);
	    JScrollPane scrollPane = new JScrollPane(textArea);

	    textArea.getDocument().addDocumentListener(new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
		    String value = null;
		    try {
			value = e.getDocument().getText(0,
				e.getDocument().getLength());
		    } catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		    elements.put(globalKey, value);

		}

		@Override
		public void insertUpdate(DocumentEvent e) {
		    changedUpdate(e);

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		    changedUpdate(e);
		}
	    });

	    component = scrollPane;
	} else if (class1.getName().equals(JSpinnerDate.class.getName())) {
	    DateFormat df = DateFormat.getDateInstance();

	    Date date = null;
	    try {
		date = df.parse(value);
	    } catch (ParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }
	    JSpinnerDate spinner = new JSpinnerDate(KSConfig.getDefaultCfg()
		    .getString("default.dateFormat"), date);
	    SpinnerModel dateModel = spinner.getModel();
	    if (dateModel instanceof SpinnerDateModel) {
		elements.put(globalKey, ((SpinnerDateModel) dateModel)
			.getDate());
	    }
	    spinner.addChangeListener(new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
		    JSpinnerDate dateSpinner = (JSpinnerDate) e.getSource();
		    SpinnerModel dateModel = dateSpinner.getModel();
		    if (dateModel instanceof SpinnerDateModel) {
			elements.put(globalKey, ((SpinnerDateModel) dateModel)
				.getDate());

		    }

		}
	    });

	    component = spinner;
	}

	this.add(jl);
	this.add(component);
	nbRows++;
	SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);

    }

    public void put(String label, JComponent component, boolean isEditable) {
	JLabel jl = new JLabel(label);
	this.add(jl);
	this.add(component);
	nbRows++;
	SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);

    }

    public void putEmptyLine() {
	JLabel jl = new JLabel(" ");
	JLabel jl2 = new JLabel(" ");

	this.add(jl);
	this.add(jl2);
	nbRows++;
	SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);
    }

    public String getStringValue(String name) {
	return (String) elements.get(name);
    }

    public Object getValue(String name) {
	return elements.get(name);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
	// TODO Auto-generated method stub

    }

    @Override
    public void insertUpdate(DocumentEvent e) {
	// TODO Auto-generated method stub

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
	// TODO Auto-generated method stub

    }

    /**
     * @return the elements
     */
    public Map<String, Object> getElements() {
	return elements;
    }

    /**
     * @param elements
     *            the elements to set
     */
    public void setElements(Map<String, Object> elements) {
	this.elements = elements;
    }

}
