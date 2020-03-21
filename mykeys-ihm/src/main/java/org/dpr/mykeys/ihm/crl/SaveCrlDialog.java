package org.dpr.mykeys.ihm.crl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CrlValue;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.JSpinnerDate;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SaveCrlDialog extends JDialog {
    private static final Log log = LogFactory.getLog(SaveCrlDialog.class);

    private LabelValuePanel infosPanel;
    private CrlValue result;

    public SaveCrlDialog(JFrame owner) {

        super(owner, true);

        init();
        this.pack();

    }

    public SaveCrlDialog(JFrame owner, CertificateValue certificateValue) {

        super(owner, true);
        // CertificateInfo certInfo = new CertificateInfo();
        init();
        this.pack();

    }

    public static void main(String[] args) {
        SaveCrlDialog dial = new SaveCrlDialog(null);
        dial.pack();
        dial.setVisible(true);
    }

    private void init() {

        DialogAction dAction = new DialogAction();
        // FIXME:

        setTitle("Création d'une liste de révocation");

        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelInfo.setMinimumSize(new Dimension(400, 100));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(formatter);


        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);
        getInfoPanel(null);
        jp.add(infosPanel);

        jp.add(jf4);

    }

    /**
     * .
     *
     * @param mapAlgoSig
     * @return
     */
    private void getInfoPanel(Map<String, String> mapAlgoSig) {
        infosPanel = new LabelValuePanel();


        infosPanel.putEmptyLine();
        Calendar calendar = Calendar.getInstance();

        infosPanel.put(Messages.getString("x509.startdate"),
                JSpinnerDate.class, "notBefore", calendar.getTime(), true);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        infosPanel.put(Messages.getString("x509.enddate"),
                JSpinnerDate.class, "notAfter", calendar.getTime(), true);
        infosPanel.putEmptyLine();

    }

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("OK")) {

                Map<String, Object> elements = infosPanel.getElements();
                CrlValue crlValue = new CrlValue();

                crlValue.setName("name");
                crlValue.setThisUpdate((Date) elements.get("notBefore"));
                crlValue.setNextUpdate((Date) elements.get("notAfter"));
                result = crlValue;

            }
            dispose();
        }

    }

    public CrlValue showDialog() {

        setVisible(true);
        return result;
    }
}

