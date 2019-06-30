package org.dpr.mykeys.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.keystore.KeystoreBuilder;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.mykeys.utils.ComponentUtils;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.dpr.mykeys.utils.KeystoreUtils.KSTYPE_EXT_JKS;
import static org.dpr.mykeys.utils.KeystoreUtils.KSTYPE_EXT_P12;

public class CreateStoreDialog extends JDialog {


    private static final Log log = LogFactory.getLog(CreateStoreDialog.class);
    // JComboBox ksType;
    // JPasswordField password;
    // JPasswordField pwd2;
    private LabelValuePanel infosPanel;
    private JTextField tfDirectory;
    private boolean result = false;

    // Map<String, String> elements = new HashMap<String, String>();

    public CreateStoreDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
        this.pack();
    }

    public boolean showDialog() {
        this.setVisible(true);
        return result;
    }

    private void init() {
        DialogAction dAction = new DialogAction();
        setTitle(Messages.getString("keystore.create.title"));
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        Map<String, String> mapType = new HashMap<>();
        mapType.put("Java Key store", "JKS");
        mapType.put("PKCS12", "PKCS12");

        infosPanel = new LabelValuePanel();

        infosPanel.put(Messages.getString("keystore.type.label"), JComboBox.class, "typeKS", mapType);
        infosPanel.putEmptyLine();
        infosPanel.put(Messages.getString("label.password"), JPasswordField.class, "password", "", true);
        infosPanel.put(Messages.getString("confirm.password"), JPasswordField.class, "pwd2", "", true);

        infosPanel.putEmptyLine();

        JLabel jl4 = new JLabel(Messages.getString("label.filename"));
        tfDirectory = new JTextField(30);

        JButton jbChoose = new JButton("...");
        jbChoose.addActionListener(dAction);
        jbChoose.setActionCommand("CHOOSE_IN");

        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        jpDirectory.add(jl4);
        jpDirectory.add(tfDirectory);
        jpDirectory.add(jbChoose);
        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

        // jp.add(jf0);
        // jp.add(jf1);
        // jp.add(jf2);
        jp.add(jpDirectory);
        jp.add(infosPanel);

        jp.add(jf4);

    }

    public void updateKeyStoreList() {
        // TODO Auto-generated method stub

    }

    private String correctExtension(String name, String typeKS) {
        if (!name.toLowerCase().endsWith(KSTYPE_EXT_JKS) && typeKS.equals("JKS")) {
            name = name + "." + KSTYPE_EXT_JKS;
        }
        if (!name.toLowerCase().endsWith(KSTYPE_EXT_P12) && typeKS.equals("PKCS12")) {
            name = name + "." + KSTYPE_EXT_P12;
        }
        return name;
    }

    private void createKeyStore(StoreFormat format, String text, char[] charArray) {
        // TODO Auto-generated method stub

    }

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            Map<String, Object> elements = infosPanel.getElements();
            String command = event.getActionCommand();
            if (command.equals("CHOOSE_IN")) {

                JFileChooser jfc = new JFileChooser(KSConfig.getDataDir());
                // jfc.addChoosableFileFilter(new KeyStoreFileFilter());

                // jPanel1.add(jfc);
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String path = jfc.getSelectedFile().getAbsolutePath();
                    String typeKS = (String) infosPanel.getElements().get("typeKS");

                    tfDirectory.setText(path);

                }

            } else if (command.equals("OK")) {
                //FIXME: do not work with empty password
                if (tfDirectory.getText().equals("")) {
                    DialogUtil.showError(CreateStoreDialog.this, "Champs invalides");
                    return;
                }

                if (!ComponentUtils.checkFields(CreateStoreDialog.this, elements, "password")) {
                    return;
                }

                if (!elements.get("password").equals(elements.get("pwd2"))) {
                    DialogUtil.showError(CreateStoreDialog.this, "Mot de passe incorrect");
                    return;
                }

                String typeKS = (String) infosPanel.getElements().get("typeKS");
                String dir = correctExtension(tfDirectory.getText(), typeKS);
                Path p = Paths.get(dir);

                if (!p.isAbsolute()) {

                    dir = KSConfig.getDataDir() + File.separator + correctExtension(dir, typeKS);
                }


                try {
                    StoreFormat format = StoreFormat.valueOf((String) elements.get("typeKS"));
                    KeystoreBuilder ksBuilder = new KeystoreBuilder(format);

                    ksBuilder.create(dir, ((String) elements.get("password")).toCharArray());
                    KSConfig.getUserCfg().addProperty("store." + StoreModel.CERTSTORE + "." + format.toString(), dir);

                    result = true;
                    CreateStoreDialog.this.setVisible(false);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    DialogUtil.showError(CreateStoreDialog.this, e.getMessage());
                }

            } else if (command.equals("CANCEL")) {
                CreateStoreDialog.this.setVisible(false);
            }

        }

    }

    /**
     * @author Christophe Roger
     * @date 8 mai 2009
     */
    private class KeyStoreFileFilter extends FileFilter {

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(File arg0) {
            // TODO Auto-generated method stub
            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
