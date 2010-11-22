package org.ihm.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.app.CertificateInfo;
import org.app.KeyStoreInfo;
import org.app.KeyStoreInfo.StoreFormat;
import org.app.KeyTools;
import org.app.KeyToolsException;
import org.ihm.ImageUtils;
import org.ihm.KeyStoreUI;
import org.ihm.TypeAction;
import org.ihm.components.JImgList;
import org.ihm.tools.LabelValuePanel;

public class ListPanel extends JPanel {
    public class ListTransferHandler extends TransferHandler {
	DataFlavor certFlavor;

	public ListTransferHandler() {
	    try {
		String certType = DataFlavor.javaJVMLocalObjectMimeType
			+ ";class=\"" + org.app.CertificateInfo.class.getName()
			+ "\"";
		certFlavor = new DataFlavor(certType);
	    } catch (ClassNotFoundException e) {
		System.out.println("ClassNotFound: " + e.getMessage());
	    }
	}
    }

    private DetailPanel detailPanel;
    KeysAction actions;

    /**
     * <pre>
     * b&gt;&lt;/b&gt;.
     * 
     * b&gt;Description :&lt;/b&gt;
     * 
     * 
     * </pre>
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
    JButton importButton;
    JButton exportButton;
    JButton deleteButton;
    JToggleButton unlockButton;

    DefaultListModel listModel;
    JImgList listCerts;

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
	final ImageIcon icon= ImageUtils.createImageIcon("images/Locked.png");

//	 Jpanel jp2 = new JPanel(new BorderLayout())
//	{
//		public void paintComponent(Graphics g)
//		{
//			//  Approach 1: Dispaly image at at full size
//			g.drawImage(icon.getImage(), 0, 0, null);
//
//			//  Approach 2: Scale image to size of component
//			// Dimension d = getSize();
//			// g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
//
//			//  Approach 3: Fix the image position in the scroll pane
//			// Point p = scrollPane.getViewport().getViewPosition();
//			// g.drawImage(icon.getImage(), p.x, p.y, null);
//
//			setOpaque( false );
//			super.paintComponent(g);
//		}
//	};
	
	//
	// jp.setLayout(new FlowLayout(FlowLayout.LEADING));
	// jp.add(titre);
	JToolBar toolBar = new JToolBar("Still draggable");
	toolBar.setFloatable(false);
	listModel = new DefaultListModel();
	ListSelectionListener listListener = new CertListListener();

	listCerts = new JImgList(listModel);

	listCerts.addListSelectionListener(listListener);
	ListCertRenderer renderer = new ListCertRenderer();
	listCerts.setCellRenderer(renderer);
	listCerts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	listCerts.setVisibleRowCount(-1);
	listCerts.setDragEnabled(true);
	listCerts.setTransferHandler(new ListTransferHandler());
	addCertButton = new JButton(
		ImageUtils.createImageIcon("images/add-cert.png"));
	unlockButton = new JToggleButton(
		ImageUtils.createImageIcon("images/Locked.png"));
	unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
	// unlockButton.setIcon(ImageUtils.createImageIcon("images/Locked.png"));
	unlockButton.setDisabledIcon(ImageUtils
		.createImageIcon("images/Unlocked.png"));
	addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
	importButton = new JButton("Import");
	importButton.setActionCommand(TypeAction.IMPORT_CERT.getValue());
	exportButton = new JButton("Export");
	exportButton.setActionCommand(TypeAction.EXPORT_CERT.getValue());
	// FIXME libelles
	deleteButton = new JButton("Supprimer");
	deleteButton.setActionCommand(TypeAction.DELETE_CERT.getValue());
	deleteButton.setEnabled(false);
	actions = new KeysAction(this);
	exportButton.addActionListener(actions);
	importButton.addActionListener(actions);
	unlockButton.addActionListener(actions);
	deleteButton.addActionListener(actions);
	toolBar.add(titre);
	toolBar.add(unlockButton);
	toolBar.add(addCertButton);
	toolBar.add(importButton);
	toolBar.add(exportButton);
	toolBar.add(deleteButton);
	toolBar.addSeparator();

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
	if (info == null) {
	    return;
	}
	ksInfo = info;
	listCerts.clearSelection();
	listModel.removeAllElements();
	try {
	    for (CertificateInfo ci : getCertificates(ksInfo)) {
		listModel.addElement(ci);
	    }

	} catch (KeyToolsException e1) {
	    // FIXME
	    e1.printStackTrace();
	}

	addCertButton.removeActionListener(actions);
	if (ksInfo.getPassword() != null) {
	    unlockButton.setSelected(false);
	    // unlockButton.setIcon(ImageUtils.createImageIcon("images/Unlocked.png"));
	    unlockButton.setEnabled(false);
	    // unlockButton.setDisabledIcon(ImageUtils.createImageIcon("images/Unlocked.png"));
	    addCertButton.setEnabled(true);
	    importButton.setEnabled(true);
	    exportButton.setEnabled(true);
	    deleteButton.setEnabled(true);
	    addCertButton.addActionListener(actions);
	    listCerts.setShowImage(false);

	} else {

	    importButton.setEnabled(false);
	    exportButton.setEnabled(false);
	    deleteButton.setEnabled(false);
	    addCertButton.setEnabled(false);
	    unlockButton.setSelected(false);
	    // unlockButton.setIcon(ImageUtils.createImageIcon("images/Locked.png"));
	    unlockButton.setEnabled(true);
	    listCerts.setShowImage(true);
	}
	titre.setText(ksInfo.getName());

	jp.revalidate();
	jp.setVisible(true);
    }

    /**
     * .
     * 
     * <BR>
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
	if (ksInfo.getPassword() == null
		&& ksInfo.getStoreFormat().equals(StoreFormat.PKCS12)) {
	    return certs;
	}

	ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		ksInfo.getPassword());

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
     * @param ksInfo
     *            the ksInfo to set
     */
    public void setKsInfo(KeyStoreInfo ksInfo) {
	this.ksInfo = ksInfo;
    }

    public class KeysAction implements ActionListener {

	public KeysAction(JComponent frameSource) {
	    super();
	    this.frameSource = frameSource;
	    // this.ksInfo = ksInfo;
	}

	private JComponent frameSource;

	// private KeyStoreInfo ksInfo;

	@Override
	public void actionPerformed(ActionEvent e) {
	    final String action = e.getActionCommand();
	    final Object composant = e.getSource();

	    TypeAction typeAction = TypeAction.getTypeAction(action);
	    JDialog cs;
	    JFrame frame = null;
	    switch (typeAction) {
	    // case ADD_STORE:
	    // frame = (JFrame) tree.getTopLevelAncestor();
	    // cs = new CreateStoreDialog(frame, true);
	    // cs.setLocationRelativeTo(frame);
	    // cs.setVisible(true);
	    // break;
	    //
	    // case IMPORT_STORE:
	    // frame = (JFrame) tree.getTopLevelAncestor();
	    // cs = new ImportStoreDialog(frame, true);
	    // cs.setLocationRelativeTo(frame);
	    // cs.setVisible(true);
	    // break;

	    // case EXPORT_CERT:
	    // treeKeyStoreParent.exporterCertificate(node, false);
	    // break;
	    //
	    case OPEN_STORE:
		if (openStore(false, true)) {
		}
		updateInfo(ListPanel.this.ksInfo);
		break;
	    //
	    // case CLOSE_STORE:
	    // treeKeyStoreParent.closeStore(node, true);
	    // break;

	    case ADD_CERT:
		addCertificate(ksInfo, false);
		break;
	    case IMPORT_CERT:
		importCertificate(ksInfo, false);
		break;

	    case EXPORT_CERT:
		if (listCerts != null
			&& listCerts.getSelectedValue() != null
			&& listCerts.getSelectedValue() instanceof CertificateInfo) {
		    exporterCertificate(ksInfo,
			    (CertificateInfo) listCerts.getSelectedValue(),
			    false);
		}
		break;
	    case DELETE_CERT:
		if (listCerts != null
			&& listCerts.getSelectedValue() != null
			&& listCerts.getSelectedValue() instanceof CertificateInfo) {
		    CertificateInfo certInfo = (CertificateInfo) listCerts
			    .getSelectedValue();
		    if (KeyStoreUI.askConfirmDialog(null,
			    "Suppression du certificat " + certInfo.getName())) {
			deleteCertificate(ksInfo, certInfo);
		    }
		}
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

    /**
     * .
     * 
     * <BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *     
     * FIN
     * </pre>
     * 
     * @param ksInfo2
     * @param certificateInfo
     */
    public void deleteCertificate(KeyStoreInfo ksInfo2,
	    CertificateInfo certificateInfo) {
	KeyTools kt = new KeyTools();
	try {
	    kt.deleteCertificate(ksInfo, certificateInfo);

	} catch (Exception e1) {
	    KeyStoreUI.showError(this, e1.getMessage());
	    e1.printStackTrace();
	}
	updateInfo(ksInfo);

    }

    public void exporterCertificate(KeyStoreInfo ksInfo,
	    CertificateInfo certificateInfo, boolean b) {
	JFrame frame = (JFrame) this.getTopLevelAncestor();

	ExportCertificateDialog cs = new ExportCertificateDialog(frame, ksInfo,
		certificateInfo, true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);

    }

    public void importCertificate(KeyStoreInfo ksInfo2, boolean b) {
	JFrame frame = (JFrame) this.getTopLevelAncestor();

	ImportCertificateDialog cs = new ImportCertificateDialog(frame, ksInfo,
		true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);
	updateInfo(ksInfo);

    }

    public boolean openStore(boolean useInternalPwd, boolean expand) {

	// if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) { //
	// equals(StoreModel.CASTORE))
	// // {
	// useInternalPwd = true;
	// }
	// ask for password
	if (ksInfo.getPassword() == null) {
	    char[] password = KeyStoreUI.showPasswordDialog(this);

	    if (password == null || password.length == 0) {
		return false;
	    }

	    ksInfo.setPassword(password);

	}

	KeyTools kt = new KeyTools();
	KeyStore ks = null;
	try {
	    ks = kt.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
		    ksInfo.getPassword());

	} catch (Exception e1) {
	    KeyStoreUI.showError(this, e1.getMessage());
	    e1.printStackTrace();
	    return false;
	}

	return true;

    }

}
