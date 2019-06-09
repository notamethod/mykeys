package org.dpr.mykeys.ihm.windows.certificate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportCertificateDialog extends JDialog implements ItemListener {

    public static final String PEM_KEY_EXT = ".key";
    public static final String PEM_CERT_EXT = ".cer";
    private static final Log log = LogFactory
            .getLog(ExportCertificateDialog.class);
    private JTextField tfDirectory;
    private LabelValuePanel infosPanel;

    @NotNull
    private List<CertificateValue> certInfos;

    private boolean isMultiple;

    private KeyStoreValue ksInfo;

    private boolean isExportCle = false;


    public ExportCertificateDialog(Frame owner, KeyStoreValue ksInfo, @NotNull
            List<CertificateValue> certInfos, boolean modal) {
        super(owner, modal);
        this.certInfos = certInfos;
        isMultiple = certInfos.size() > 1;
        this.ksInfo = ksInfo;
        init();
        this.pack();
    }

    private void init() {
        DialogAction dAction = new DialogAction();
        setTitle(Messages.getString("dialog.export.title"));
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        Map<String, String> mapType = new LinkedHashMap<>();

        mapType.put("pem", "pem");
        if (!isMultiple) {
            mapType.put("der", "der");
            mapType.put("pkcs12", "pkcs12");
        }

        infosPanel = new LabelValuePanel();

        infosPanel.put("Format", ButtonGroup.class, "formatCert", mapType, "");
        if (isContainsPrivateKey(certInfos)) {

            infosPanel.put(Messages.getString("export.private.key"), JCheckBox.class,
                    "isExportKey", String.valueOf(!isMultiple), !isMultiple);

        }

        infosPanel.putEmptyLine();

        tfDirectory = new JTextField(35);

        File outputFile = getTargetFile(null);
        tfDirectory.setText(outputFile.getAbsolutePath());

        JButton jbChoose = new JButton("...");
        jbChoose.addActionListener(dAction);
        jbChoose.setActionCommand("CHOOSE_IN");

        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        jpDirectory.add(tfDirectory);
        jpDirectory.add(jbChoose);
        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

        infosPanel.put(Messages.getString("dialog.generic.fileout"),
                jpDirectory, true);
        jp.add(infosPanel);
        jp.add(jf4);

    }

    private boolean isContainsPrivateKey(@NotNull List<CertificateValue> certInfos) {
        for (CertificateValue cert : certInfos) {
            if (cert.isContainsPrivateKey())
                return true;
        }
        return false;
    }

    public void updateKeyStoreList() {
        // TODO Auto-generated method stub

    }

    private File getTargetFile(String format) {
        File pathSrc = new File(KSConfig.getDataDir());
        if (pathSrc != null && !pathSrc.isDirectory()) {
            pathSrc = new File(pathSrc.getParent());
        }

        //get alias
        String retAlias = certInfos.get(0).getAlias();
        if (certInfos.size() > 1)
            retAlias += "_multi";
        if (retAlias == null)
            retAlias = certInfos.get(0).getName();

        final String alias = retAlias;
        String fileName = null;
        if (null == format) {
            return new File(pathSrc, alias);
        }
        if (format.equalsIgnoreCase("pkcs12")) {
            fileName = alias + KeyTools.EXT_P12;
        } else if (format.equalsIgnoreCase("der")) {
            fileName = alias + KeyTools.EXT_DER;
        } else if (format.equalsIgnoreCase("pem")) {
            fileName = alias + KeyTools.EXT_PEM;
        } else {
            fileName = alias;
        }
        return new File(pathSrc, fileName);


    }

    private String getAlias() {

        String retAlias = certInfos.get(0).getAlias();
        if (certInfos.size() > 1)
            retAlias += "_multi";
        return retAlias;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        JCheckBox jc = (JCheckBox) source;
        isExportCle = jc.isSelected();


    }

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            Map<String, Object> elements = infosPanel.getElements();
            String command = event.getActionCommand();
            if (command.equals("CHOOSE_IN")) {

                String format = (String) infosPanel.getElements().get(
                        "formatCert");
                File outputFile = getTargetFile(format);

                JFileChooser jfc = new JFileChooser(outputFile);
                // the first, only, and selected filter is 'All Files'
                jfc.removeChoosableFileFilter(jfc.getFileFilter());
                //add filters
                jfc.addChoosableFileFilter(new KeyStoreFileFilter("der", "fichiers der (*.der)"));
                jfc.addChoosableFileFilter(new KeyStoreFileFilter("der", "fichiers PKCS12 (*.p12)"));
                jfc.addChoosableFileFilter(new KeyStoreFileFilter("der", "fichiers pem (*.pem)"));
                jfc.setSelectedFile(outputFile);
                // jPanel1.add(jfc);
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                    //TODO: remove or not ?
                    KSConfig.getUserCfg().setProperty("output.path",
                            jfc.getSelectedFile().getParent());
                    tfDirectory
                            .setText(jfc.getSelectedFile().getAbsolutePath());

                }

            } else if (command.equals("OK")) {
                if (tfDirectory.getText().equals("")) {
                    DialogUtil.showError(ExportCertificateDialog.this,
                            "Champs invalides");
                    return;
                }

                String path = tfDirectory.getText();
                // saisie mot de passe
                char[] pd = null;
                char[] privKeyPd = null;
                Object o = infosPanel.getElements().get(
                        "isExportKey");
                boolean isExportCle = o == null ? false : (Boolean) o;

                KeyTools kt = new KeyTools();
                KeyStoreHelper kServ = new KeyStoreHelper(ksInfo);
                String format = (String) infosPanel.getElements().get(
                        "formatCert");
                if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                    if (isExportCle) {
                        privKeyPd = DialogUtil.showPasswordDialog(null, "mot de passe de la cl� priv�e");
                    }
                } else if (!ksInfo.getName().startsWith("previous")) {
                    privKeyPd = MkSession.password;
                } else {
                    privKeyPd = InternalKeystores.MK1_PD.toCharArray();
                }
                // TODO check it
                setPassword(pd, certInfos);
                if (format.equalsIgnoreCase("pkcs12")) {
                    pd = DialogUtil.showPasswordDialog(null, "mot de passe d'exportation");


                    CommonServices cact = new CommonServices();

                    try {
                        cact.exportCert(ksInfo, StoreFormat.PKCS12, path,
                                pd, certInfos.get(0), isExportCle, privKeyPd);
                    } catch (Exception e) {
                        log.error(e);
                        DialogUtil.showError(ExportCertificateDialog.this,
                                e.getLocalizedMessage());
                    }
                } else if (format.equals("der")) {
                    try {
                        kServ.exportDers(certInfos, path);
                        if (isExportCle) {
                            kServ.exportPrivateKey(certInfos.get(0), privKeyPd,
                                    tfDirectory.getText());
                        }

                    } catch (Exception e) {

                        DialogUtil.showError(ExportCertificateDialog.this,
                                e.getLocalizedMessage());

                    }

                } else {
                    try {
                        kServ.exportPems(certInfos, path);
                        if (isExportCle) {
                            kServ.exportPrivateKeyPEM(certInfos.get(0), ksInfo, privKeyPd,
                                    tfDirectory.getText());
                        }

                    } catch (Exception e) {

                        DialogUtil.showError(ExportCertificateDialog.this,
                                e.getLocalizedMessage());

                    }
                }
                ExportCertificateDialog.this.setVisible(false);
                DialogUtil.showInfo(ExportCertificateDialog.this,
                        "Exportation terminée");
            } else if (command.equals("CANCEL")) {
                ExportCertificateDialog.this.setVisible(false);
            }

        }

        private void setPassword(char[] password, List<CertificateValue> certInfos) {
            certInfos.forEach(cert -> cert.setPassword(password));
        }

    }

    /**
     * @author Christophe Roger
     * @date 8 mai 2009
     */
    class KeyStoreFileFilter extends FileFilter {

        private String filterExtension;
        private String filterDescription;

        private KeyStoreFileFilter(String extension, String descrip) {
            this.filterExtension = extension;
            this.filterDescription = descrip;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FilenameUtils.getExtension(f.getName());
            return extension != null && extension.equalsIgnoreCase(filterExtension);

        }


        /*
         * (non-Javadoc)
         *
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return filterDescription;
        }

    }
}
