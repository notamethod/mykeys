package org.dpr.mykeys.certificate.windows;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.keystore.InternalKeystores;
import org.dpr.mykeys.keystore.KeyStoreInfo;
import org.dpr.mykeys.keystore.StoreModel;
import org.dpr.mykeys.keystore.StoreType;

public class CreateCertificatDialog extends SuperCreate implements ItemListener {

	public CreateCertificatDialog(JFrame owner, KeyStoreInfo ksInfo,
			boolean modal) {

		super(owner, modal);
		this.ksInfo = ksInfo;
		if (ksInfo==null){
			isAC = false;
		}else
		if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
			isAC = true;
		}
		init();
		this.pack();
		//this.setVisible(true);

	}
	
public static void main(String[] args) {
	JFrame f=null;
	CreateCertificatDialog cr = new CreateCertificatDialog(f, null,
			false);
}

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("CHOOSE_IN")) {

			} else if (command.equals("OK")) {
				Map<String, Object> elements = infosPanel.getElements();
				Set<String> keys = elements.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
				}
				if (elements.get("alias") == null
						|| elements.get("pwd1") == null) {
					MykeysFrame.showError(CreateCertificatDialog.this,
							"Champs obligatoires");
					return;
				}

				// certInfo.setX509PrincipalMap(elements);
				HashMap<String, String> subjectMap = new HashMap<String, String>();
				FillUtils.fillCertInfo(elements, certInfo);
				certInfo.setAlias((String) elements.get("alias"));
				certInfo.setNotBefore((Date) elements.get("notBefore"));
				certInfo.setNotAfter((Date) elements.get("notAfter"));
				KeyTools ktools = new KeyTools();
				char[] pkPassword = ((String) elements.get("pwd1"))
						.toCharArray();

				certInfo.setSubjectMap(elements);
				
			    if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) {
			        certInfo.setPassword(InternalKeystores.password.toCharArray());
			    }else{
			        certInfo.setPassword(pkPassword);
			    }


	
				X509Certificate[] xCerts = null;

				try {
					certInfo.setCrlDistributionURL(((String) elements
							.get("CrlDistrib")));
					certInfo.setPolicyNotice(((String) elements
							.get("PolicyNotice")));
					certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

					xCerts = ktools.genererX509(certInfo,
							(String) elements.get("emetteur"), isAC);

					ktools.addCertToKeyStoreNew(xCerts, ksInfo, certInfo);
					CreateCertificatDialog.this.setVisible(false);

				} catch (Exception e) {
					MykeysFrame.showError(CreateCertificatDialog.this,
							e.getMessage());
					e.printStackTrace();
				}
			} else if (command.equals("CANCEL")) {
				CreateCertificatDialog.this.setVisible(false);
			}

		}

	}

}
