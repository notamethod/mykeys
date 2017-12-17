package org.dpr.mykeys.ihm.windows;

import javax.swing.JPasswordField;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.ihm.actions.TypeAction;

public enum ComponentType {

	PASSWORD(JPasswordField.class);


	private final Class<?> value;

	private ComponentType(Class<?> value) {
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
