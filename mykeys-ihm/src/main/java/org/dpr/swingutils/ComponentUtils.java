
package org.dpr.swingutils;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.utils.DialogUtil;

import java.awt.*;
import java.util.Map;

public class ComponentUtils {

	public static void checkNotNull(String string) {
		// TODO Auto-generated method stub
		
	}


    public static boolean checkFields(Component c, Map<String, Object> elements, String... fields) {
        for (String field : fields) {
            String value = (String) elements.get(field);
            if (value == null || value.isEmpty()) {
                DialogUtil.showError(c, Messages.getFullString("mandatory", "label." + field));
                return false;
            }
        }

        return true;
    }
}
