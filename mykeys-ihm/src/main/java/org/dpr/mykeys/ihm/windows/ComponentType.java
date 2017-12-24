package org.dpr.mykeys.ihm.windows;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;

public enum ComponentType {

	PASSWORD(JPasswordField.class), COMBOBOX(JComboBox.class);


	private final Class<?> value;

	ComponentType(Class<?> value) {
		this.value = value;
	}

	/** La méthode accesseur qui renvoit la valeur de l'enum */
	public Class<?> getValue() {
		return this.value;
	}

	// /**
	// * Permet de recuperer un <code>EtatSignature</code> à partir de sa valeur.
	// *
	// * <BR>
	// *
	// * @param value
	// * valeur de l'objet EtatSignature
	// *
	// * @return EtatTransaction
	// */
	// public static ComponentType getTypeAction(String value) {
	// TypeAction[] typesAction = TypeAction.values();
	// for (int i = 0; i < typesAction.length; i++) {
	// TypeAction action = typesAction[i];
	// if (StringUtils.equals(action.getValue(), value)) {
	// return action;
	// }
	// }
	// return null;
	// }
}
