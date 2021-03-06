package org.dpr.mykeys.utils;

import org.dpr.mykeys.ihm.Messages;

import javax.swing.*;
import java.awt.*;

public class DialogUtil {


    /**
     * Show dialog error box
     *
     * @param c
     * @param string
     */
    public static void showError(Component c, String string) {

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(c, string, Messages.getString("error.title"), JOptionPane.ERROR_MESSAGE));
    }

    /**
     * Show dialog box with a password field
     *
     * @param parent
     * @return
     */
    public static char[] showPasswordDialog(Component parent) {
        return showPasswordDialog(parent, "Mot de passe:");

    }

    public static char[] showPasswordDialog(Component parent, String titre) {
        final JPasswordField jpf = new JPasswordField();
        JOptionPane jop = new JOptionPane(jpf, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            @Override
            public void selectInitialValue() {
                jpf.requestFocusInWindow();
            }
        };
        // FIXME: label
        JDialog dialog = jop.createDialog(parent, titre);

        dialog.setVisible(true);
        if (jop.getValue() == null) {
            return null;
        }
        int result = (Integer) jop.getValue();
        dialog.dispose();
        char[] password = null;
        if (result == JOptionPane.OK_OPTION) {
            password = jpf.getPassword();
        } else {
            return null;
        }
        return password;
    }

    /**
     * Show dialog error box
     *
     * @param c
     * @param string
     */
    public static void showInfo(Component c, String string) {
        JOptionPane.showMessageDialog(c, string, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show dialog box with a password field
     *
     * @param parent
     * @return
     */
    public static boolean askConfirmDialog(Component parent, String message) {

        boolean retour = false;
        int result = JOptionPane.showConfirmDialog(parent, message, Messages.getString("title.action.confirm"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            retour = true;
        }
        return retour;
    }
}
