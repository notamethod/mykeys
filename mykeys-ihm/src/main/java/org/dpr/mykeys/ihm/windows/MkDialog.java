package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.Messages;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.Map;

public class MkDialog extends JDialog{

    public MkDialog(JFrame owner, boolean modal) {
        super(owner, modal);
    }

    protected void initLookAndFeel() {

        setDefaultLookAndFeelDecorated(true);
        try {
            // UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
            UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
            UIManager.put("ToolTip.foreground", new ColorUIResource(Color.ORANGE));
            return;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // handle exception
        }

    }

    protected static boolean checkFields(Component c, Map<String, Object> elements, String... fields) {
        for (String field : fields) {
            String value = (String) elements.get(field);
            if (value == null || value.isEmpty()) {
                MykeysFrame.showError(c, Messages.getFullString("mandatory", "label." + field));
                return false;
            }
        }

        return true;
    }
}
