package org.app;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectIdentifier {

    public static final String COUNTRY = "2.5.4.6";

    public static final String ORGANISATION = "2.5.4.10";

    public static final String LOCATION = "2.5.4.7";

    public static final String STREET = "2.5.4.8";

    public static final String EMAIL = "1.2.840.113549.1.9.1";

    public static final String COMMON_NAME = "2.5.4.3";

    public static void getNameToOID() {
	Map<String, String> map = new HashMap<String, String>();
	Field[] fields = ObjectIdentifier.class.getFields();
	for (Field f : fields) {
	    try {
		map.put(f.getName(), (String) f.get(null));
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    public void getOIDToName() {
	Map<String, String> map = new HashMap<String, String>();
	Field[] fields = ObjectIdentifier.class.getFields();
	for (Field f : fields) {
	    try {
		map.put((String) f.get(null), f.getName());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
