package org.dpr.mykeys.ihm.windows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.AuthenticationService;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.MkSession;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.windows.certificate.AuthenticationException;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.LabelValuePanel;
import org.dpr.swingutils.PanelBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectUserDialog extends MkDialog {
    private static final Log log = LogFactory.getLog(SelectUserDialog.class);
    private final int CPTMAX = 4;

    // CertificateInfo certInfo = new CertificateInfo();
    public JFileChooser jfc;
    private LabelValuePanel infosPanel;
    private int cpt = 0;

    private DefaultComboBoxModel modelCombo;

    public SelectUserDialog(JFrame owner, boolean modal) throws IhmException {


        super(owner, modal);
        initLookAndFeel();
        init();
        this.pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        log.info("user creation dialog...construct");

    }

    public static void main(String[] args) throws IhmException {
        SelectUserDialog di = new SelectUserDialog(null, true);
    }

    private void init() throws IhmException {

        DialogAction dAction = new DialogAction();
        // FIXME:

        setTitle(Messages.getString("title.user.select"));

        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelInfo.setMinimumSize(new Dimension(400, 100));

        infosPanel = getInfoPanel();
        panelInfo.add(infosPanel);

        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

        JButton jbAdd = new JButton("+");
        jbAdd.addActionListener(dAction);
        jbAdd.setActionCommand("ADD");
        JPanel jfAdd = new JPanel();
        jfAdd.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jfAdd.add(jbAdd);
        jp.add(jfAdd);
        jp.add(panelInfo);

        jp.add(jf4);

        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        // add a window listener
        this.addWindowListener(new WindowAdapter() {


            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }

    /**
     * Return an infoPanel .
     *
     * @return
     * @throws ServiceException
     */
    private LabelValuePanel getInfoPanel() throws IhmException {
        infosPanel = new LabelValuePanel();
        final Map<String, String> users = new HashMap<>();
        PanelBuilder pb = new PanelBuilder();

        JComboBox cb = new JComboBox();

        modelCombo = (DefaultComboBoxModel) cb.getModel();

        update();
        //  cb.setModel(modelCombo);
        infosPanel.put(Messages.getString("label.name"), cb, true);
//
        infosPanel.put(Messages.getString("label.password"), ComponentType.PASSWORD.getValue(), "password", "", true);
        //pb.addComponent(Messages.getString("label.password"), "password", ComponentType.PASSWORD);


        pb.addEmptyLine();
        return infosPanel;

    }

    private void update() throws IhmException {
        AuthenticationService auth = new AuthenticationService();

        List<String> userList2 = new ArrayList<>();

        userList2.add("");
        try {
            auth.listUsers().forEach(item -> userList2.add(item.getAlias()));
        } catch (ServiceException e) {
            throw new IhmException(e);
        }

        modelCombo.removeAllElements();
        userList2.forEach(item2 -> modelCombo.addElement(item2));


    }

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("OK")) {

                Map<String, Object> elements = infosPanel.getElements();

                String name = (String) modelCombo.getSelectedItem();
                String pwd = (String) elements.get("password");

                if (name == null || name.isEmpty()) {
                    MykeysFrame.showError(SelectUserDialog.this, Messages.getFullString("mandatory", "label." + name));
                    return;
                }
                if (!checkFields(SelectUserDialog.this, elements, "password")) {
                    return;
                }

                char[] pwdChar = pwd.toCharArray();
                CertificateHelperNew ch = new CertificateHelperNew();
                AuthenticationService auth = new AuthenticationService();
                try {
                    auth.AuthenticateUSer(name, pwdChar);
                    SelectUserDialog.this.setVisible(false);
                    MkSession.user = name;
                    MkSession.password = pwdChar;
                    loginOK();
                    //stay alive until cpt max
                } catch (AuthenticationException e) {
                    log.error("authentication failure", e);
                    MykeysFrame.showError(SelectUserDialog.this, Messages.getString("error.authentication"));
                    if (cpt++ > CPTMAX)
                        System.exit(1);
                    return;
                }


            } else if (command.equals("CANCEL")) {
                SelectUserDialog.this.setVisible(false);
                System.exit(0);
            } else if (command.equals("ADD")) {
                SelectUserDialog.this.setVisible(false);
                log.info("hide select");
                log.info("add new user.");
                SwingUtilities.invokeLater(() -> {
                    CreateUserDialog cs = new CreateUserDialog(null, true, SelectUserDialog.class);
                    log.info("show. new user");
                    //cs.setLocationRelativeTo(SelectUserDialog.this);
                    cs.setVisible(true);
                    try {
                        log.info("update");
                        update();
                        SelectUserDialog.this.setVisible(true);
                        log.info("show again...");
                    } catch (IhmException e) {
                        e.printStackTrace();
                    }
                });


            }

            // System.exit(0);


        }

        private void loginOK() {
            KSConfig.getInternalKeystores().init();
            SwingUtilities.invokeLater(() -> {
                //MykeysFrame frame = new MykeysFrame();
                try {
                    new MykeysFrame();
                } catch (KeyStoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // frame.addComponents();

            });
        }
    }

}
