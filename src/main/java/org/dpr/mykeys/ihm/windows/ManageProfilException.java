package org.dpr.mykeys.ihm.windows;

public class ManageProfilException extends Exception {

	public ManageProfilException(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}

    public ManageProfilException(String string, Exception e)
    {
       super(string, e);
    }

}
