package org.dpr.mykeys.ihm;

import javax.swing.*;
import java.awt.*;

public abstract class CustomJDialog extends JDialog {

    protected Object result;

    public CustomJDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
        this.pack();
    }


    protected abstract void init();

    public Object showDialog() {
        this.setVisible(true);
        return result;
    }
}
