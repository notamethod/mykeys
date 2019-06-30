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
import java.security.PrivateKey;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportCertificateDialog extends JDialog implements ItemListener {

    private static final Log log = LogFactory
            .getLog(ExportCertificateDialog.class);
    private JTextField tfDirectory;
    private LabelValuePanel infosPanel;

    @NotNull
    private List<CertificateValue> certInfos;

    private boolean isMultiple;

    private KeyStoreValue ksInfo;

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

        //FIXME:enum
        mapType.put("pem", StoreFormat.PEM.toString());
        mapType.put("der", StoreFormat.DER.toString());
        mapType.put("jks", StoreFormat.JKS.toString());
        if (!isMultiple) {

            mapType.put("pkcs12", StoreFormat.PKCS12.toString());
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

        fileName = alias + StoreFormat.valueOf(format).getExtension();

        return new File(pathSrc, fileName);

    }



    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        JCheckBox jc = (JCheckBox) source;
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


                // saisie mot de passe
                char[] pd = null;
                char[] privKeyPd = null;
                Object o = infosPanel.getElements().get(
                        "isExportKey");
                boolean isExportCle = o == null ? false : (Boolean) o;

                KeyStoreHelper kServ = new KeyStoreHelper(ksInfo);
                String format = (String) infosPanel.getElements().get(
                        "formatCert");
                StoreFormat storeFormat = StoreFormat.valueOf(format);
                String path = tfDirectory.getText().endsWith(storeFormat.getExtension()) ? tfDirectory.getText() : tfDirectory.getText() + storeFormat.getExtension();

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
//                kServ.getPrivateKey()
//                certInfo.setPrivateKey((PrivateKey) ks.getKey(alias, value.getPassword()));
//                if (charArray != null)
//                    certInfo.setPassword(charArray);


                switch (storeFormat) {
                    case PKCS12:
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
                        break;
                    case JKS:
                        pd = DialogUtil.showPasswordDialog(null, "mot de passe d'exportation");

                        try {
                            if (isExportCle) {
                                for (CertificateValue cert : certInfos) {
                                    PrivateKey pk = kServ.getPrivateKey(ksInfo, cert.getAlias(), privKeyPd);
                                    cert.setPrivateKey(pk);
                                    cert.setPassword(privKeyPd);
                                }
                                kServ.export(certInfos, path, storeFormat, pd);
                            }
                        } catch (Exception e) {
                            log.error(e.getLocalizedMessage(), e);

                            DialogUtil.showError(ExportCertificateDialog.this,
                                    e.getLocalizedMessage());

                        }
                        break;
                    case DER:
                    case PEM:

                        try {
                            kServ.export(certInfos, path, storeFormat, pd);
                            if (isExportCle) {
                                kServ.exportPrivateKey(certInfos.get(0), ksInfo, privKeyPd, null,
                                        tfDirectory.getText(), storeFormat);
                            }

                        } catch (Exception e) {
                            log.error(e.getLocalizedMessage(), e);

                            DialogUtil.showError(ExportCertificateDialog.this,
                                    e.getLocalizedMessage());

                        }
                        break;
                    default:
                        break;
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
