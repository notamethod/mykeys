package org.dpr.mykeys.ihm.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import org.dpr.mykeys.ihm.windows.certificate.AuthenticationException;
import org.dpr.mykeys.ihm.windows.certificate.FillUtils;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.swingutils.FrameUtils;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;
import org.dpr.swingutils.PanelBuilder;

public class SelectUserDialog extends JDialog {
	public static final Log log = LogFactory.getLog(SelectUserDialog.class);

	LabelValuePanel infosPanel;

	// CertificateInfo certInfo = new CertificateInfo();

	boolean isAC = false;
	public JFileChooser jfc;

	public SelectUserDialog(JFrame owner, boolean modal) throws IhmException {

		super(owner, modal);

		init();
		this.pack();

	}

	public static void main(String[] args) throws IhmException {
		SelectUserDialog di = new SelectUserDialog(null, true);
	}

	private void init() throws IhmException {

		DialogAction dAction = new DialogAction();
		// FIXME:

		setTitle("Sélection utilisateur");

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
	 * Return an infoPanel .
	 * @return
	 * @throws ServiceException 
	 */
	private LabelValuePanel getInfoPanel() throws IhmException {
		infosPanel = new LabelValuePanel();
		final Map<String, String> users =  new HashMap<String, String>();

		AuthenticationService auth = new AuthenticationService();
		
	
		users.put(" ", " ");
		try {
			auth.listUsers().forEach(item->users.put(item.getAlias(), item.getAlias()));
		} catch (ServiceException e) {
			throw new IhmException(e);
		}
	//	infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");
		PanelBuilder pb = new PanelBuilder();
		pb.addComponent("User", "user", users, ComponentType.COMBOBOX);
		

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

				String nom=(String) elements.get("user");
				String pwd=(String) elements.get("password");
				if (nom == null|| nom.isEmpty()) {
					MykeysFrame.showError(SelectUserDialog.this, "nom obligatoire");
					return;
				}
				if (pwd == null|| pwd.isEmpty()) {
					MykeysFrame.showError(SelectUserDialog.this, "password obligatoire");
					return;
				}
				
			
				char[] pwdChar =pwd.toCharArray();
				CertificateHelperNew ch = new CertificateHelperNew();
				AuthenticationService auth = new AuthenticationService();
				try {
					auth.AuthenticateUSer(nom, pwdChar);

				} catch (AuthenticationException e) {
					log.error("authentication failure", e);
					MykeysFrame.showError(SelectUserDialog.this, "Echec d'authentification");
					System.exit(1);
				}


			} else if (command.equals("CANCEL")) {
				SelectUserDialog.this.setVisible(false);
			}

		}
	}

}
