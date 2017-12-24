package org.dpr.mykeys.ihm.windows;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.AuthenticationService;
import org.dpr.mykeys.app.CommonsActions;
import org.dpr.mykeys.app.CrlInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.certificate.FillUtils;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.swingutils.FrameUtils;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;
import org.dpr.swingutils.PanelBuilder;

public class CreateUserDialog extends MkDialog {
	public static final Log log = LogFactory.getLog(CreateUserDialog.class);

	LabelValuePanel infosPanel;


	public CreateUserDialog(JFrame owner, boolean modal) {

		super(owner, modal);

		init();
		this.pack();
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);

	}

	public static void main(String[] args) {
		CreateUserDialog di = new CreateUserDialog(null, true);
	}

	private void init() {

		initLookAndFeel();
        DialogAction dAction = new DialogAction();
		// FIXME:

		setTitle("Nouvel utilisateur");

		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

		infosPanel = getInfoPanel();
		panelInfo.add(infosPanel);

		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		jp.add(panelInfo);

		jp.add(jf4);

	}

	/**
	 * .
	 * 
	 * 
	 * @param mapKeyLength
	 * @param mapAlgoKey
	 * @param mapAlgoSig
	 * 
	 * @param isAC2
	 * @return
	 */
	private LabelValuePanel getInfoPanel() {
		infosPanel = new LabelValuePanel();
		Map<String, String> mapAC = null;

		PanelBuilder pb = new PanelBuilder();
		pb.addComponent("Nom", "nom", System.getProperty("user.name"));
		pb.addComponent("Email", "email");
		pb.addComponent("Mot de passe", "password", ComponentType.PASSWORD);

		pb.addEmptyLine();
		return pb.toPanel();

	}

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("OK")) {

				Map<String, Object> elements = infosPanel.getElements();

				String nom=(String) elements.get("nom");
				String pwd=(String) elements.get("password");
				if (nom == null|| nom.isEmpty()) {
					MykeysFrame.showError(CreateUserDialog.this, "nom obligatoire");
					return;
				}
				if (pwd == null|| pwd.isEmpty()) {
					MykeysFrame.showError(CreateUserDialog.this, "password obligatoire");
					return;
				}
				
			
				char[] pwdChar =pwd.toCharArray();
				CertificateHelperNew ch = new CertificateHelperNew();
				AuthenticationService auth = new AuthenticationService();
				try {
					auth.createUser(nom, pwdChar);
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


	
			} else if (command.equals("CANCEL")) {
				CreateUserDialog.this.setVisible(false);
			}

		}
	}

}
