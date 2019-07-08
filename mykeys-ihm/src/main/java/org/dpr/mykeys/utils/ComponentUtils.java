
package org.dpr.mykeys.utils;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.utils.DialogUtil;

import java.awt.*;
import java.util.Map;

public class ComponentUtils {

    public static void checkNotNull(String string) {
        // TODO Auto-generated method stub

    }

    public static String skin0 = "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel";
    public static String skin1 = "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel";
    public static String skin2 = "org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel";
    public static String skin3 = "org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel";
    public static String skin4 = "org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel";
    public static String skin5 = "org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel";
    public static String skin6 = "org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel";
    public static String skin = skin0;

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
