package org.dpr.mykeys.ihm.windows;

import org.dpr.mykeys.utils.ComponentUtils;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class MkDialog extends JDialog {

    protected MkDialog(JFrame owner, boolean modal) {
        super(owner, modal);
    }

    protected void initLookAndFeel() {

        setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(ComponentUtils.skin);
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

}
