package org.dpr.mykeys.ihm.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.crl.CRLEditorDialog;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class ApplicationHelp {
    private final static Log log = LogFactory.getLog(ApplicationHelp.class);

    private static ApplicationHelp INSTANCE = new ApplicationHelp();
    private JEditorPane targetComponent;

    public static ApplicationHelp getInstance() {
        return INSTANCE;
    }

    private ApplicationHelp() {
    }

    public void setTarget(JEditorPane component) {

        targetComponent = component;
        HTMLEditorKit kit = new HTMLEditorKit();
        targetComponent.setEditorKit(kit);
        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color: #DCDCDC; font-family:Segoe UI; margin: 2px;font-size: 10px }");
        styleSheet.addRule("h1 { margin:2em;padding:0;line-height: 0.7em;font-weight: bold;font-size: 12px;color: white;}");
        styleSheet.addRule(" p {margin-bottom:2em;margin-top:2em}");

    }

    public void requestHelp(String key){
        String language =  Locale.getDefault().getLanguage();

        URL url = this.getClass().getClassLoader().getResource("help/"+language+"/"+key+".html");
        if (url == null)
            url = this.getClass().getClassLoader().getResource("help/"+key+".html");
        if (targetComponent instanceof JEditorPane){
            try {
                ((JEditorPane) targetComponent).setPage(url);
            } catch (IOException e) {
                log.debug("help not found for "+key);
            }
        }
    }

    public void hideHelp() {
        URL url = this.getClass().getClassLoader().getResource("help/welcome.html");
        try {
            targetComponent.setPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
