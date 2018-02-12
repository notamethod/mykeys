package org.dpr.mykeys.app;

import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.keystore.ServiceException;

public class TamperedWithException extends Exception {

	public TamperedWithException(KeyToolsException e) {
		super (e);
	}

    public TamperedWithException(ServiceException e) {
        super(e);
    }
}
