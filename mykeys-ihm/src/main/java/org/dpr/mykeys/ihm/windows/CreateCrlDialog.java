package org.dpr.mykeys.ihm.windows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.MkSession;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.crl.CRLManager;
import org.dpr.mykeys.app.crl.CrlValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.ihm.components.CertificateListPanel;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKeyStorePanel;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.JSpinnerDate;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.security.cert.X509CRL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static org.dpr.mykeys.utils.MessageUtils.getMessage;

public class CreateCrlDialog extends JDialog {
    private static final Log log = LogFactory.getLog(CertificateListPanel.class);
    // JTextField x509PrincipalC;
    // JTextField x509PrincipalO;
    // JTextField x509PrincipalL;
    // JTextField x509PrincipalST;
    // JTextField x509PrincipalE;
    // JTextField x509PrincipalCN;
    public JFileChooser jfc;
    boolean isAC = false;
    private JTextField tfDirectoryOut;
    private LabelValuePanel infosPanel;
    // CertificateInfo certInfo = new CertificateInfo();
    private CertificateValue certificateValue;

    public CreateCrlDialog(JFrame owner, boolean modal) {

        super(owner, modal);

        init();
        this.pack();

    }

    public CreateCrlDialog(JFrame owner, CertificateValue certificateValue) {

        super(owner, true);
        this.certificateValue = certificateValue;
        init();
        this.pack();

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

        // fill with provider's available algorithms
        Map<String, String> mapAlgoSig = new LinkedHashMap<>();
        for (String algo : ProviderUtil.SignatureList) {
            mapAlgoSig.put(algo, algo);
        }

        getInfoPanel(mapAlgoSig);
        panelInfo.add(infosPanel);

        FileSystemView fsv = FileSystemView.getFileSystemView();
        File f = fsv.getDefaultDirectory();

        JLabel jl5 = new JLabel(Messages.getString("file.output"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(formatter);
        tfDirectoryOut = new JTextField(40);
        tfDirectoryOut.setText(f.getAbsolutePath() + File.separator + certificateValue.getName() + "." + formattedDateTime + CRLManager.CRL_EXTENSION);
        JButton jbChoose2 = new JButton("...");
        jbChoose2.addActionListener(dAction);
        jbChoose2.setActionCommand("CHOOSE_OUT");

        JPanel jpDirectory2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
        jpDirectory2.add(jl5);
        jpDirectory2.add(tfDirectoryOut);
        jpDirectory2.add(jbChoose2);

        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

        jp.add(panelInfo);
        jp.add(jpDirectory2);
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
        if (certificateValue == null) {
            Map<String, String> mapAC = null;
            try {
                mapAC = TreeKeyStorePanel.getListCerts(
                        KSConfig.getInternalKeystores().getACPath(), "JKS",
                        KSConfig.getInternalKeystores().getPassword());
            } catch (Exception e) {
                //
            }
            if (mapAC == null) {
                mapAC = new HashMap<>();
            }
            mapAC.put(" ", " ");
            infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");


            infosPanel.putEmptyLine();
        }
        //   infosPanel.put("Alias (nom du certificat)", "alias", "");
//        infosPanel.put("Algorithme de signature", JComboBox.class, "algoSig",
//                mapAlgoSig, "SHA256WithRSAEncryption");
        // subject
        infosPanel.putEmptyLine();
        Calendar calendar = Calendar.getInstance();

        infosPanel.put(Messages.getString("x509.startdate"),
                JSpinnerDate.class, "notBefore", calendar.getTime(), true);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        infosPanel.put(Messages.getString("x509.enddate"),
                JSpinnerDate.class, "notAfter", calendar.getTime(), true);
        infosPanel.putEmptyLine();
        infosPanel.put(getMessage("crl.serials"), JTextArea.class, "serials", "", true);
        infosPanel.putEmptyLine();
        infosPanel.putEmptyLine();

    }

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("OK")) {

                Map<String, Object> elements = infosPanel.getElements();
                CrlValue crlValue = new CrlValue();
                String serials = (String) elements.get("serials");
                List<String> list = new ArrayList<>();
                if (serials != null && !serials.isEmpty())
                    list = new ArrayList<String>(Arrays.asList(serials.split(",")));
                // certInfo.setX509PrincipalMap(elements);
                HashMap<String, String> subjectMap = new HashMap<>();
                crlValue.setName("name");
                crlValue.setThisUpdate((Date) elements.get("notBefore"));
                crlValue.setNextUpdate((Date) elements.get("notAfter"));
                crlValue.setPath(tfDirectoryOut.getText());


                try {
                    CRLManager crlMan = new CRLManager();
                    KeyStoreHelper ktools = new KeyStoreHelper();
                    String aliasIssuer = (String) elements.get("emetteur");
                    CertificateValue certSign = certificateValue;
                    if (certSign == null) {
                        certSign = ktools.findCertificateAndPrivateKeyByAlias(null, aliasIssuer);
                    } else if (certSign.getPrivateKey() == null) {
                        certSign = ktools.findCertificateByAlias(KSConfig.getInternalKeystores().getStoreAC(), certSign.getAlias(), MkSession.password);
                    }
                    X509CRL xCRL = crlMan.generateCrl(certSign, crlValue, list);
                    crlMan.saveCRL(xCRL, crlValue.getPath());
                    // FIXME: add crl to tree
                    // ktools.generateCrl(certSign, crlValue, privateKey);
                    CreateCrlDialog.this.setVisible(false);

                } catch (Exception e) {
                    DialogUtil.showError(CreateCrlDialog.this, e.getMessage());
                    e.printStackTrace();
                }
            } else if (command.equals("CANCEL")) {
                CreateCrlDialog.this.setVisible(false);
            }

        }
    }

}

