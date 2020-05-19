package org.dpr.mykeys.ihm.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpMouseListener extends MouseAdapter implements HelpMouseListenerInterface {

    private String key;

    public HelpMouseListener(String key) {
        super();
        this.key = key;
    }
    public HelpMouseListener() {
        super();
    }
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        ApplicationHelp.getInstance().hideHelp();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        if (key!=null)
            ApplicationHelp.getInstance().requestHelp(key);
        else
        ApplicationHelp.getInstance().requestHelp(getKey());
    }
}
