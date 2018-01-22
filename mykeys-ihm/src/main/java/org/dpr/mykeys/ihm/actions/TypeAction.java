package org.dpr.mykeys.ihm.actions;

import org.apache.commons.lang.StringUtils;

public enum TypeAction {
	ADD_STORE("AddStore"), EXPORT_CERT("ExportCert"), IMPORT_STORE(
			"ImportStore"), OPEN_STORE("OpenStore"), ADD_CERT("AddCert"), IMPORT_CERT(
			"ImportCert"), CLOSE_STORE("CloseStore"), REMOVE_STORE(
			"RemoveStore"), DELETE_STORE("DeleteStore"), ADD_CERT_AC(
            "AddCertAC"), DELETE_CERT("DeleteCert"), CREATE_CRL("createCrl"), ADD_CERT_PROF("AddCertProf"), CHANGE_PWD("changePWd"), ADD_CERT_FROMCSR("addCertFromCSR");

	/** L'attribut qui contient la valeur associé à l'enum */
	private final String value;

	/** Le constructeur qui associe une valeur à l'enum */
    TypeAction(String value) {
		this.value = value;
	}

	/** La méthode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/**
	 * Permet de recuperer un <code>EtatSignature</code> à partir de sa valeur.
	 * 
	 * <BR>
	 * 
	 * @param value
	 *            valeur de l'objet EtatSignature
	 * 
	 * @return EtatTransaction
	 */
	public static TypeAction getTypeAction(String value) {
		TypeAction[] typesAction = TypeAction.values();
		for (TypeAction action : typesAction) {
			if (StringUtils.equals(action.getValue(), value)) {
				return action;
			}
		}
		return null;
	}

}
