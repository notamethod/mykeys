package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.app.KeyToolsException;

public class TamperedWithException extends Exception {

	public TamperedWithException(KeyToolsException e) {
		super (e);
	}

}
