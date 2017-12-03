package org.dpr.mykeys.app;

public class TamperedWithException extends Exception {

	public TamperedWithException(KeyToolsException e) {
		super (e);
	}

}
