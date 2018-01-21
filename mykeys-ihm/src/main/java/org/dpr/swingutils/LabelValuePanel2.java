package org.dpr.swingutils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
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

import org.dpr.mykeys.Messages;

public class LabelValuePanel2 extends JPanel implements DocumentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2550655426888407968L;

	private int nbRows;

	private int nbCols;

	int tfSize;

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	private final static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm";
	private String dateFormat = DEFAULT_DATE_FORMAT;

	private Map<String, Object> elements;

	private Map<String, Object> components;

	public LabelValuePanel2() {
		super();
		this.setLayout(new SpringLayout());
		this.elements = new HashMap<>();
		this.components = new HashMap<>();

	}

	public LabelValuePanel2(Map<String, String> elements2, int cols) {
		this.nbCols = cols;
	}

	public void put(String label, String id, String defaultValue) {
		put(label, JTextField.class, id, defaultValue, true);
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
		List<JComponent> components = null;
		if (class1.getName().equals(JComboBox.class.getName())) {
			component = putCombo(keyValue, values, defaultValue);
		} else if (class1.getName().equals(ButtonGroup.class.getName())) {
			components = putRadios(keyValue, values, defaultValue);
		}

		if (component != null) {
			this.add(jl);
			this.add(component);
			nbRows++;
		} else {
			for (int i = 0; i < components.size(); i++) {
				this.add(i == 0 ? new JLabel(label) : new JLabel(""));
				this.add(components.get(i));

				nbRows++;
			}
		}

		SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);

	}

	private JComponent putCombo(String keyValue, Map<String, String> values,
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

	private List<JComponent> putRadios(String keyValue,
									   Map<String, String> values, String defaultValue) {

		final String globalKey = keyValue;
		final Map<String, String> map = values;
		// JComboBox combo = new JComboBox();
		ButtonGroup bg = new ButtonGroup();

		List<JComponent> radios = new ArrayList<>();
		Set<String> keys = values.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			JRadioButton jradio = new JRadioButton(key);
			radios.add(jradio);
			if (elements.get(globalKey) == null) {
				elements.put(globalKey, map.get(key));
				jradio.setSelected(true);
			}
			jradio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JRadioButton radio = (JRadioButton) e.getSource();
					if (radio.isSelected()) {
						String key = radio.getText();
						elements.put(globalKey, map.get(key));
					}
				}
			});
			bg.add(jradio);
		}

		// combo.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// JComboBox combo = (JComboBox) e.getSource();
		// String key = (String) combo.getSelectedItem();
		// elements.put(globalKey, map.get(key));
		// }
		// });
		// combo.setSelectedItem(defaultValue);
		return radios;
	}

	private void put(String label, Class<?> class1, String keyValue,
					 Object value, boolean isEditable) {
		JLabel jl = new JLabel(label);
		String strValue = null;
		if (value != null) {
			if (value instanceof String) {
				strValue = (String) value;
			} else {
				strValue = value.toString();
			}
		}
		final String globalKey = keyValue;
		JComponent component = null;
		if (class1.getName().equals(JLabel.class.getName())) {
			JLabel labelValue = new JLabel();
			labelValue.setText(strValue);

			component = labelValue;
			this.add(component, globalKey);
			// JtextField
		} else if (class1.getName().equals(JTextField.class.getName())) {

			JTextField field = new JTextField(20);
			field.setText(strValue);
			field.setName(keyValue);

			elements.put(globalKey, strValue);

			field.getDocument().addDocumentListener(new DocumentListener() {
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

			field.setEditable(isEditable);
			if (!isEditable) {
				field.setBorder(null);
			}

			component = field;
			this.add(component, globalKey);

		} else if (class1.getName().equals(JPasswordField.class.getName())) {
			JPasswordField pwdField = new JPasswordField(strValue);
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
			this.add(component, globalKey);
		} else if (class1.getName().equals(JTextArea.class.getName())) {
			JTextArea textArea = new JTextArea(5, 20);
			textArea.setText(strValue);
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
			this.add(textArea, globalKey);
			component = scrollPane;
		} else if (class1.getName().equals(JSpinnerDate.class.getName())) {
			// DateFormat df = DateFormat.getDateInstance();

			Date date = (Date) value;

			if (!isEditable) {
				DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
				put(label, JTextField.class, keyValue, dateFormat.format(date),
						false);
				return;
			}

			JSpinnerDate spinner = new JSpinnerDate(this.dateFormat, date);

			SpinnerModel dateModel = spinner.getModel();
			if (dateModel instanceof SpinnerDateModel) {
				elements.put(globalKey,
						((SpinnerDateModel) dateModel).getDate());
			}

			spinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JSpinnerDate dateSpinner = (JSpinnerDate) e.getSource();
					SpinnerModel dateModel = dateSpinner.getModel();
					if (dateModel instanceof SpinnerDateModel) {
						elements.put(globalKey,
								((SpinnerDateModel) dateModel).getDate());

					}

				}
			});

			component = spinner;
		} else if (class1.getName().equals(JCheckBox.class.getName())) {

			boolean valCheck = Boolean.parseBoolean(strValue);
			JCheckBox checkbox = new JCheckBox("", valCheck);

			elements.put(globalKey, checkbox.isSelected());

			checkbox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBox checkbox = (JCheckBox) e.getSource();

					elements.put(globalKey, checkbox.isSelected());
				}
			});

			component = checkbox;
		}

		// component.setName(globalKey);
		this.add(jl);
		this.add(component);
		nbRows++;
		SpringUtilities.makeCompactGrid(this, nbRows, 2, 3, 3, 3, 3);

	}

	public void set(String keyValue, Object value) {

		String strValue = null;
		if (value != null) {
			if (value instanceof String) {
				strValue = (String) value;
			} else if (value instanceof Date) {
				DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
				strValue = dateFormat.format(value);
			} else {
				strValue = value.toString();
			}

		}
		final String globalKey = keyValue;

		JComponent component = (JComponent) components.get(globalKey);
		if (component == null)
			return;

		if (component instanceof JLabel) {

			JLabel field = (JLabel) component;
			field.setText(strValue);
		} else if (component instanceof JTextField) {

			JTextField field = (JTextField) component;
			field.setText(strValue);
			// field.setEditable(isEditable);
			// if (!isEditable) {
			// field.setBorder(null);
			// }

		}

	}

	// public void set(String keyValue,
	// Object value) {
	// //boolean isEditable=true;
	// Component[] comps = this.getComponents();
	// JComponent component;
	// for (Component c : comps) {
	// if (c.getName().equals(keyValue)) {
	// // if (c instanceof JTextField) {
	// // ((JTextField) c).setText((String) obj);
	// // }
	// component = (JComponent)c;
	// break;
	// }
	// }
	// if (component==null){
	// return;
	// }
	//
	// String strValue = null;
	// if (value != null) {
	// if (value instanceof String) {
	// strValue = (String) value;
	// } else {
	// strValue = value.toString();
	// }
	// }
	//
	// final String globalKey = keyValue;
	//
	// if (component.getClass().getName().equals(JLabel.class.getName())) {
	// JLabel labelValue = new JLabel();
	// ((JLabel)component).setText(strValue);
	//
	// } else if (component.getClass().equals(JTextField.class.getName())) {
	//
	//
	// ((JTextField)component).setText(strValue);
	//
	//
	// elements.put(globalKey, strValue);
	//
	// } else if (component.getName().equals(JPasswordField.class.getName())) {
	//
	// elements.put(globalKey, value);
	// //todo
	// } else if (component.getName().equals(JTextArea.class.getName())) {
	//
	// ((JTextArea)component).setText(strValue);
	//
	// } else if (component.getName().equals(JSpinnerDate.class.getName())) {
	// // DateFormat df = DateFormat.getDateInstance();
	//
	// Date date = (Date) value;
	//
	//
	// JSpinnerDate spinner = new JSpinnerDate(this.dateFormat, date);
	//
	// SpinnerModel dateModel = spinner.getModel();
	// if (dateModel instanceof SpinnerDateModel) {
	// elements.put(globalKey, ((SpinnerDateModel) dateModel)
	// .getDate());
	// }
	// spinner.addChangeListener(new ChangeListener() {
	//
	// @Override
	// public void stateChanged(ChangeEvent e) {
	// JSpinnerDate dateSpinner = (JSpinnerDate) e.getSource();
	// SpinnerModel dateModel = dateSpinner.getModel();
	// if (dateModel instanceof SpinnerDateModel) {
	// elements.put(globalKey, ((SpinnerDateModel) dateModel)
	// .getDate());
	//
	// }
	//
	// }
	// });
	//
	// component = spinner;
	// }
	//
	//
	//
	// }

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

	public void setValue(String strName, Object obj) {
		Component[] comps = this.getComponents();
		for (Component c : comps) {
			if (c.getName().equals(strName)) {
				if (c instanceof JTextField) {
					((JTextField) c).setText((String) obj);
				}
			}
		}
	}

	/**
	 * .
	 * 
	 * 
	 * @param comp
	 * @param constraints
	 * 
	 */

	private void add(Component comp, String key) {
		// TODO Auto-generated method stub
		comp.setName(key);
		// super.add(comp);
		if (components.get(key) == null) {
			components.put(key, comp);
		}
	}

	public static String getString(String string) {
		try {
			string = Messages.getString(string);
		} catch (Exception e) {
			//
		}
		return string;
	}

}
