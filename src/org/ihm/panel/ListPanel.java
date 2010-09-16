package org.ihm.panel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.app.CertificateInfo;
import org.app.KeyStoreInfo;
import org.app.KeyTools;
import org.ihm.KeyStoreUI;
import org.ihm.TreeKeyStore;
import org.ihm.tools.LabelValuePanel;

public class ListPanel extends JPanel {
    /**
     *<pre>
     *<b></b>.
     * 
     *<b>Description :</b>
     *    
     * 
     *</pre>
     * @author C. Roger<BR>
     *  <BR>
     * Créé le 16 sept. 2010 <BR>
     *  <BR>
     *  <BR>
     * <i>Copyright : Tessi Informatique </i><BR>
     */
    public class CertListListener implements ListSelectionListener {


	@Override
	public void valueChanged(ListSelectionEvent e) {
	   
	   System.out.println( e.getSource().getClass());

	}

    }

    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;

    KeyStoreInfo ksInfo;

    ActionPanel dAction;

    JPanel jp;

    JLabel titre = new JLabel();

    public ListPanel() {
	super();
	init();

    }

    private void init() {
	dAction = new ActionPanel();
	// setBackground(new Color(125,0,0));
	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	this.setLayout(bl);

	titre = new JLabel("Gestion des certificats");
	// add(titre);
	jp = new JPanel();

	jp.setLayout(new FlowLayout(FlowLayout.LEADING));

	add(jp);
	// jp.add();
	// jp.add(new JLabel("Contenu du certificat"));
	jp.setVisible(true);
    }

    public void updateInfo(KeyStoreInfo info) {
	jp.removeAll();
	jp.revalidate();
	if (info == null) {
	    return;
	}
	ksInfo = info;

	infosPanel = new LabelValuePanel();
	infosPanel.put("Alias (nom du certificat)", JLabel.class, "", ksInfo
		.getName(), false);
	infosPanel.putEmptyLine();
	if (ksInfo.isOpen()) {
	    infosPanel.put("Etat ks", JLabel.class, "", "ouvert", false);
	} else {
	    infosPanel.put("Etat ks", JLabel.class, "", "fermé", false);
	}
	jp.add(infosPanel);

	CertListModel listModel = new CertListModel();
	listModel.setData(getCertificates(ksInfo));
	JList listCerts = new JList(listModel);
	ListSelectionListener listListener = new CertListListener();
	listCerts.addListSelectionListener(listListener);
	ListCertRenderer renderer = new ListCertRenderer();
	listCerts.setCellRenderer(renderer);
	listCerts
		.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	// if (longValue != null) {
	// list.setPrototypeCellValue(longValue); //get extra space
	// }
	listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	listCerts.setVisibleRowCount(-1);
	listCerts.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    // setButton.doClick(); //emulate button click
		}
	    }
	});
	JScrollPane listScroller = new JScrollPane(listCerts);
	listScroller.setPreferredSize(new Dimension(450, 80));
	listScroller.setAlignmentX(LEFT_ALIGNMENT);

	jp.add(listScroller);

	jp.setVisible(true);
	jp.revalidate();
    }

    /**
     * .
     * 
     *<BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param ksInfo2
     * @return
     */
    private List<CertificateInfo> getCertificates(KeyStoreInfo ksInfo2) {
	List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
	KeyTools kt = new KeyTools();
	KeyStore ks = null;
	try {
	    ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		    ksInfo.getPassword());

	} catch (Exception e1) {
	    // KeyStoreUI.showError(TreeKeyStore.this, e1.getMessage());

	}

	System.out.println("addcerts");
	Enumeration<String> enumKs;
	try {
	    enumKs = ks.aliases();
		if (enumKs != null && enumKs.hasMoreElements()) {

		    while (enumKs.hasMoreElements()) {
			String alias = enumKs.nextElement();

			CertificateInfo certInfo = new CertificateInfo(alias);
			// kt.fillCertInfo(ks, certInfo, alias);
			certs.add(certInfo);
		    }
		}	    
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return certs;


    }

    public class ActionPanel extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent event) {
	    String command = event.getActionCommand();
	    if (command.equals("CHECK_OCSP")) {
		System.out.println("OCSP");

	    } else if (command.equals("OK")) {

		KeyTools kt = new KeyTools();

	    } else if (command.equals("CANCEL")) {
		// ExportCertificateDialog.this.setVisible(false);
	    }

	}

    }

}
