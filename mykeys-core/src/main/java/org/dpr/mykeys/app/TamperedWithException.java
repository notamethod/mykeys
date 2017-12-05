package org.dpr.mykeys.app;

import org.dpr.mykeys.app.KeyToolsException;

public class TamperedWithException extends Exception {

	public TamperedWithException(KeyToolsException e) {
		super (e);
	}

}
