package org.ihm.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.app.CertificateInfo;
import org.app.KeyStoreInfo;
import org.app.KeyTools;
import org.app.KeyToolsException;
import org.ihm.ImageUtils;
import org.ihm.TypeAction;
import org.ihm.tools.LabelValuePanel;

public class ListPanel extends JPanel {
    private DetailPanel detailPanel;
    KeysAction actions;

    /**
     *<pre>
     * b&gt;&lt;/b&gt;.
     * 
     * b&gt;Description :&lt;/b&gt;
     * 
     * 
     *</pre>
     * 
     * @author C. Roger<BR>
     * <BR>
     *         Créé le 16 sept. 2010 <BR>
     * <BR>
     * <BR>
     *         <i>Copyright : Tessi Informatique </i><BR>
     */
    public class CertListListener implements ListSelectionListener {

	@Override
	public void valueChanged(ListSelectionEvent e) {
	    if (!e.getValueIsAdjusting()) {

		System.out.println(e.getSource().getClass());
		if (e.getSource() instanceof JList) {
		    if (((JList) e.getSource()).getSelectedValue() instanceof CertificateInfo) {
			displayCertDetail((CertificateInfo) ((JList) e
				.getSource()).getSelectedValue());
		    }

		}
	    }

	}

    }

    // Map<String, String> elements = new HashMap<String, String>();
    LabelValuePanel infosPanel;

    KeyStoreInfo ksInfo;

    ActionPanel dAction;

    JPanel jp;
    
    JLabel titre = new JLabel();
    
    JButton addCertButton;

    CertListModel listModel;
    JList listCerts;

    public ListPanel() {
	super(new BorderLayout());

	init();

    }

    private void init() {
	dAction = new ActionPanel();
	// setBackground(new Color(125,0,0));
	// BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	// this.setLayout(bl);

	// titre = new GradientLabel("Gestion des certificats");
	// add(titre);
	jp = new JPanel(new BorderLayout());
	//
	// jp.setLayout(new FlowLayout(FlowLayout.LEADING));
	// jp.add(titre);
	JToolBar toolBar = new JToolBar("Still draggable");
	toolBar.setFloatable(false);
	listModel = new CertListModel(new ArrayList<CertificateInfo>());
	ListSelectionListener listListener = new CertListListener();

	listCerts = new JList(listModel);
	listCerts.addListSelectionListener(listListener);
	ListCertRenderer renderer = new ListCertRenderer();
	listCerts.setCellRenderer(renderer);
	listCerts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
	 listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	 listCerts.setVisibleRowCount(-1);
	 addCertButton = new JButton(ImageUtils.createImageIcon("images/List-add.png"));

	 
	 addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
	toolBar.add(addCertButton);
	toolBar.add(titre);
	JScrollPane listScroller = new JScrollPane(listCerts);
	// listScroller.setPreferredSize(new Dimension(450, 80));
	listScroller.setAlignmentX(LEFT_ALIGNMENT);
	jp.add(toolBar, BorderLayout.PAGE_START);
	jp.add(listScroller, BorderLayout.CENTER);

	// jp.add(listScroller);
	add(jp);
	// jp.add();
	// jp.add(new JLabel("Contenu du certificat"));
	// jp.setVisible(true);
	jp.setVisible(false);
    }

    public void updateInfo(KeyStoreInfo info) {
	jp.setVisible(false);
	// jp.removeAll();
	// jp.revalidate();
	// if (info == null) {
	// return;
	// }
	ksInfo = info;
	listCerts.clearSelection();	
	try {
	    listModel.setData(getCertificates(ksInfo));
	} catch (KeyToolsException e1) {
	    // FIXME
	    e1.printStackTrace();
	}
	addCertButton.removeActionListener(actions);
	actions = new KeysAction(this, ksInfo);
	 addCertButton.addActionListener(actions);
	titre.setText(ksInfo.getName());
	
	 jp.revalidate();
	 jp.setVisible(true);
    }

    public void updateInfo2(KeyStoreInfo info) {
	jp.removeAll();
	jp.revalidate();
	if (info == null) {
	    return;
	}
	ksInfo = info;
	// CertListModel listModel = new CertListModel();
	try {
	    listModel.setData(getCertificates(ksInfo));
	} catch (KeyToolsException e1) {
	    // FIXME
	    e1.printStackTrace();
	}
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

	JList listCerts = new JList(listModel);
	ListSelectionListener listListener = new CertListListener();
	listCerts.addListSelectionListener(listListener);
	ListCertRenderer renderer = new ListCertRenderer();
	listCerts.setCellRenderer(renderer);
	listCerts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
     * &lt;b&gt;Algorithme : &lt;/b&gt;
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param ksInfo2
     * @return
     * @throws KeyToolsException
     */
    private List<CertificateInfo> getCertificates(KeyStoreInfo ksInfo2)
	    throws KeyToolsException {
	List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
	KeyTools kt = new KeyTools();
	KeyStore ks = null;

	ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(), ksInfo
		.getPassword());

	System.out.println("addcerts");
	Enumeration<String> enumKs;
	try {
	    enumKs = ks.aliases();
	    if (enumKs != null && enumKs.hasMoreElements()) {

		while (enumKs.hasMoreElements()) {
		    String alias = enumKs.nextElement();

		    CertificateInfo certInfo = new CertificateInfo(alias);
		    kt.fillCertInfo(ks, certInfo, alias);
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

    private void displayCertDetail(CertificateInfo info) {
	detailPanel.updateInfo(info);

    }

    public void setDetailPanel(DetailPanel detailPanel) {
	this.detailPanel = detailPanel;
    }

    /**
     * @return the ksInfo
     */
    public KeyStoreInfo getKsInfo() {
        return ksInfo;
    }

    /**
     * @param ksInfo the ksInfo to set
     */
    public void setKsInfo(KeyStoreInfo ksInfo) {
        this.ksInfo = ksInfo;
    }
    
    public class KeysAction implements ActionListener {

	    public KeysAction(JComponent frameSource, KeyStoreInfo ksInfo) {
		super();
		this.frameSource = frameSource;
		this.ksInfo = ksInfo;
	    }



	    private JComponent frameSource;
	    
	    private KeyStoreInfo ksInfo;
	    
		@Override
		public void actionPerformed(ActionEvent e) {
		    final String action = e.getActionCommand();
		    final Object composant = e.getSource();


		    TypeAction typeAction = TypeAction.getTypeAction(action);
		    JDialog cs;
		    JFrame frame = null;
		    switch (typeAction) {
//		    case ADD_STORE:
//			frame = (JFrame) tree.getTopLevelAncestor();
//			cs = new CreateStoreDialog(frame, true);
//			cs.setLocationRelativeTo(frame);
//			cs.setVisible(true);
//			break;
	//
//		    case IMPORT_STORE:
//			frame = (JFrame) tree.getTopLevelAncestor();
//			cs = new ImportStoreDialog(frame, true);
//			cs.setLocationRelativeTo(frame);
//			cs.setVisible(true);
//			break;

//		    case EXPORT_CERT:
//			treeKeyStoreParent.exporterCertificate(node, false);
//			break;
	//
//		    case OPEN_STORE:
//			treeKeyStoreParent.openStore(node, false, true);
//			break;
	//
//		    case CLOSE_STORE:
//			treeKeyStoreParent.closeStore(node, true);
//			break;

		    case ADD_CERT:
			    addCertificate(ksInfo, false);
			break;


		    default:
			break;
		    }

		}
		


	}   
    
    public void addCertificate(KeyStoreInfo ksInfo, boolean b) {
	JFrame frame = (JFrame) this.getTopLevelAncestor();

	CreateCertificatDialog cs = new CreateCertificatDialog(frame, ksInfo,
		true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);
	updateInfo(ksInfo);

	return;

    }	    

}
