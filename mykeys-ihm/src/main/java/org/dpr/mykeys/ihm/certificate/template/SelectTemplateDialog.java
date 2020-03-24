package org.dpr.mykeys.ihm.certificate.template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.authentication.AuthenticationService;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.IhmException;
import org.dpr.mykeys.ihm.windows.MkDialog;
import org.dpr.mykeys.ihm.certificate.CreateCertProfilDialog;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTemplateDialog extends MkDialog {
    private static final Log log = LogFactory.getLog(SelectTemplateDialog.class);
    private final int CPTMAX = 4;

    private LabelValuePanel infosPanel;
    private final KeyStoreValue ksInfo;
    int cpt = 0;

    private DefaultComboBoxModel modelCombo;

    public SelectTemplateDialog(JFrame owner, KeyStoreValue ksInfo) throws IhmException {


        super(owner, true);
        this.ksInfo = ksInfo;
        initLookAndFeel();
        init();
        this.pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

    }

    public static void main(String[] args) throws IhmException {
        SelectTemplateDialog di = new SelectTemplateDialog(null, null);
    }

    private void init() throws IhmException {

        DialogAction dAction = new DialogAction();


        setTitle(Messages.getString("title.template.select"));

        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));

        setPreferredSize(new Dimension(280, 110));
        infosPanel = getInfoPanel();
        panelInfo.add(infosPanel);

        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);


        jp.add(panelInfo);

        jp.add(jf4);


    }

    /**
     * Return an infoPanel .
     *
     * @return
     * @throws ServiceException
     */
    private LabelValuePanel getInfoPanel() {
        infosPanel = new LabelValuePanel();
        final Map<String, String> users = new HashMap<>();


        //   pb.addComponent(Messages.getString("label.name"), "name", users, ComponentType.COMBOBOX);
        JComboBox cb = new JComboBox();
        cb.setPrototypeDisplayValue("name of a template");

        modelCombo = (DefaultComboBoxModel) cb.getModel();

        update();
        //  cb.setModel(modelCombo);
        infosPanel.put(Messages.getString("label.name"), cb, true);

        return infosPanel;

    }

    private void update() {
        AuthenticationService auth = new AuthenticationService();

        List<String> profileList = new ArrayList<>();

        profileList.add("");
        Map<String, String> mapProfiles = new HashMap<>();
        mapProfiles.put("", "");
        ProfileServices pman = new ProfileServices(KSConfig.getProfilsPath());
        log.debug("listing available templates...");
        for (String profile : pman.getProfiles()) {
            if (profile != null) {
                profile = profile.substring(0, profile.indexOf("."));
            }
            profileList.add(profile);
        }

        modelCombo.removeAllElements();
        profileList.forEach(item -> modelCombo.addElement(item));


    }

    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {


            String command = event.getActionCommand();
            if (command.equals("OK")) {

                Map<String, Object> elements = infosPanel.getElements();

                String profile = (String) modelCombo.getSelectedItem();

                if (profile == null || profile.isEmpty()) {
                    DialogUtil.showError(SelectTemplateDialog.this, Messages.getFullString("mandatory", "label." + profile));
                    return;
                }


                SelectTemplateDialog.this.setVisible(false);
                CreateCertProfilDialog cs = cs = new CreateCertProfilDialog(null, ksInfo, true);
                cs.setStrProf(profile);
                cs.init();
                cs.setVisible(true);
                //stay alive until cpt max


            } else if (command.equals("CANCEL")) {
                SelectTemplateDialog.this.setVisible(false);

            }


        }


    }

}
