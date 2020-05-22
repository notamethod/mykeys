package org.dpr.mykeys.test.dialog;

import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.ihm.certificate.CertificateTypeSelectDialog;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CertificateTest {

    @Test
    public void openCertificateTypeSelectDialog(){
        CertificateTypeSelectDialog dl = new CertificateTypeSelectDialog(true);
        Component c = getOk(dl);
         new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((JButton) c).doClick();
        }).start();

        CertificateType certType = dl.showDialog();
        assertEquals(CertificateType.STANDARD,certType);

        System.out.println(certType);
    }

    private Component getOk(JDialog dl) {
        List<Component> components = getAllComponents(dl.getContentPane());
        for (Component c : components){
            System.out.println(c.getClass().getName());
            if (c instanceof JButton ) {
                String text = ((JButton) c).getText();
                if ("OK".equals(text));
                return c;
            }

        }
        return null;
    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }
}
