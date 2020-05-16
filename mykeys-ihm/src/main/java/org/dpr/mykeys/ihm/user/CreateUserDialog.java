package org.dpr.mykeys.ihm.user;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.authentication.AuthenticationService;
import org.dpr.mykeys.app.certificate.CertificateManager;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.ihm.windows.MkDialog;
import org.dpr.mykeys.utils.ComponentUtils;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.*;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;

public class CreateUserDialog extends MkDialog {
	private static final Log log = LogFactory.getLog(CreateUserDialog.class);

	private LabelValuePanel infosPanel;

    public CreateUserDialog(JFrame owner, boolean modal) {

		super(owner, modal);

		init();
		this.pack();
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);

	}

	/**
	 * called by selectUSerDialog
	 * @param owner
	 * @param modal
	 * @param dialogClass
	 */
    public CreateUserDialog(JFrame owner, boolean modal, Class<? extends Component> dialogClass) {
        super(owner, modal);
        init();
        log.error("create user constructor");
        this.pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    }

    public static void main(String[] args) {
		CreateUserDialog di = new CreateUserDialog(null, true);
	}

	private void init() {

		initLookAndFeel();
        DialogAction dAction = new DialogAction();
		// FIXME:

        setTitle(Messages.getString("title.user.new"));

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

		jp.add(panelInfo);

		jp.add(jf4);

	}


	private LabelValuePanel getInfoPanel() {
		infosPanel = new LabelValuePanel();
		Map<String, String> mapAC = null;

		PanelBuilder pb = new PanelBuilder();
        pb.addComponent(Messages.getString("label.name"), "name", System.getProperty("user.name"));
        //do we need an email ?
        //pb.addComponent("Email", "email");
        pb.addComponent(Messages.getString("label.password"), "password", ComponentType.PASSWORD);

		pb.addEmptyLine();
		return pb.toPanel();

	}

	class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("OK")) {
				log.error("a1");
				Map<String, Object> elements = infosPanel.getElements();

                String nom = (String) elements.get("name");
				String pwd=(String) elements.get("password");

                if (!ComponentUtils.checkFields(CreateUserDialog.this, elements, "name", "password")) {
                    return;
                }
				log.error("a2");
				char[] pwdChar =pwd.toCharArray();
				CertificateManager ch = new CertificateManager();
				AuthenticationService auth = new AuthenticationService();
				try {
					auth.createUser(nom, pwdChar);
				} catch (ServiceException e) {
					log.error(e);
                    DialogUtil.showError(CreateUserDialog.this, e.getLocalizedMessage());
				}
				log.error("a3");
				CreateUserDialog.this.setVisible(false);

	
			} else if (command.equals("CANCEL")) {
				CreateUserDialog.this.setVisible(false);
//                if (null != previousWindow && previousWindow.getName().equals(SelectUserDialog.class.getName())) {
//                    SwingUtilities.invokeLater(() -> {
//                        SelectUserDialog cs = null;
//                        try {
//                            cs = new SelectUserDialog(
//                                    null, true);
//                        } catch (IhmException e) {
//                            e.printStackTrace();
//                        }
//
//                        cs.setVisible(true);
//                    });
//                }
			}

		}
	}

}
