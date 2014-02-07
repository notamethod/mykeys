package org.dpr.mykeys.ihm.components;

import static org.dpr.swingutils.ImageUtils.createImageIcon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.CertificatManager;
import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyStoreInfo.StoreFormat;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.PkiTools;
import org.dpr.mykeys.app.PkiTools.TypeObject;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.windows.CreateCertificatDialog;
import org.dpr.mykeys.ihm.windows.ExportCertificateDialog;
import org.dpr.mykeys.ihm.windows.ImportCertificateDialog;
import org.dpr.mykeys.ihm.windows.ListCertRenderer;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.utils.MessageUtils;
import org.dpr.swingutils.LabelValuePanel;

@SuppressWarnings("serial")
public class ListPanel extends JPanel implements DropTargetListener {
	public static final Log log = LogFactory.getLog(ListPanel.class);

	public class ListTransferHandler extends TransferHandler {
		DataFlavor certFlavor;

		public ListTransferHandler() {
			try {
				String certType = DataFlavor.javaJVMLocalObjectMimeType
						+ ";class=\""
						+ org.dpr.mykeys.app.CertificateInfo.class.getName()
						+ "\"";
				certFlavor = new DataFlavor(certType);
			} catch (ClassNotFoundException e) {
				log.trace("ClassNotFound: " + e.getMessage());
			}
		}
	}

	private DetailPanel detailPanel;
	KeysAction actions;

	/**
	 * @author Buck
	 * 
	 */
	public class CertListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {

				log.trace(e.getSource().getClass());
				if (e.getSource() instanceof JList) {
					if (((JList) e.getSource()).getSelectedValue() instanceof CertificateInfo) {
						displayCertDetail((CertificateInfo) ((JList) e
								.getSource()).getSelectedValue());
						if (ksInfo.isOpen()) {
							exportButton.setEnabled(true);
							deleteButton.setEnabled(true);
						}
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
	DropTarget dropTarget;

	public ListPanel() {
		super(new BorderLayout());

		init();

	}

	private void init() {
		// Create the DropTarget and register
		// it with the JPanel.
		dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
				this, true, null);
		// FIXME: check flavor
		dAction = new ActionPanel();
		// setBackground(new Color(125,0,0));
		// BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		// this.setLayout(bl);

		// titre = new GradientLabel("Gestion des certificats");
		// add(titre);
		jp = new JPanel(new BorderLayout());
		final ImageIcon icon = createImageIcon("Locked.png");

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
		ToolTipManager.sharedInstance().registerComponent(listCerts);
		// listCerts.setTransferHandler(new ListTransferHandler());
		addCertButton = new JButton(createImageIcon("add-cert.png"));
		unlockButton = new JToggleButton(createImageIcon("Locked.png"));
		unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
		// unlockButton.setIcon(createImageIcon("Locked.png"));
		unlockButton.setDisabledIcon(createImageIcon("Unlocked.png"));
		addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
		importButton = new JButton("Import");
		importButton.setActionCommand(TypeAction.IMPORT_CERT.getValue());
		exportButton = new JButton("Export");
		exportButton.setActionCommand(TypeAction.EXPORT_CERT.getValue());
		// FIXME libelles
		deleteButton = new JButton("Supprimer");
		deleteButton.setActionCommand(TypeAction.DELETE_CERT.getValue());
		deleteButton.setEnabled(false);
		exportButton.setEnabled(false);
		importButton.setEnabled(false);
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
		//FIXME: ajout bouron entete liste pour fermer liste 
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
		if (ksInfo.isOpen()) {
			unlockButton.setSelected(false);
			// unlockButton.setIcon(createImageIcon("Unlocked.png"));
			unlockButton.setEnabled(false);
			// unlockButton.setDisabledIcon(createImageIcon("Unlocked.png"));
			addCertButton.setEnabled(true);
			importButton.setEnabled(true);

			addCertButton.addActionListener(actions);
			listCerts.setShowImage(false);

		} else {

			importButton.setEnabled(false);
			exportButton.setEnabled(false);
			deleteButton.setEnabled(false);
			addCertButton.setEnabled(false);
			unlockButton.setSelected(false);
			// unlockButton.setIcon(createImageIcon("Locked.png"));
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

		List<CertificateInfo> certs;
		try {
			certs = CertificatManager.getCertificates(ksInfo2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			certs = new ArrayList<CertificateInfo>();
		}
		return certs;

	}

	public class ActionPanel extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("CHECK_OCSP")) {
				log.trace("OCSP");

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
					if (MykeysFrame.askConfirmDialog(null,
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
			MykeysFrame.showError(this, e1.getMessage());
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
			char[] password = MykeysFrame.showPasswordDialog(this);

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
			ksInfo.setOpen(true);

		} catch (Exception e1) {
			MykeysFrame.showError(this, e1.getMessage());
			e1.printStackTrace();
			return false;
		}

		return true;

	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		System.out.println("drop");
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			System.out.println("dropxx");
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();

			try {
				boolean result = false;
				List fileList = (List) transferable
						.getTransferData(DataFlavor.javaFileListFlavor);
				File transferFile = (File) fileList.get(0);
				TypeObject typeObject = PkiTools.getTypeObject(transferFile);
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) && typeObject!=TypeObject.UNKNOWN && typeObject!=null) {
					
					result = dropFile(transferable);
				} else {
					result = false;
				}

				dtde.dropComplete(result);

			} catch (Exception e) {
				System.out.println("Exception while handling drop " + e);
				dtde.rejectDrop();
			}
		} else {
			System.out.println("Drop target rejected drop");
			dtde.dropComplete(false);
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	// This method handles a drop for a list of files
	protected boolean dropFile(Transferable transferable) throws IOException,
			UnsupportedFlavorException, MalformedURLException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyToolsException {
		List fileList = (List) transferable
				.getTransferData(DataFlavor.javaFileListFlavor);
		File transferFile = (File) fileList.get(0);

		final String transferURL = transferFile.getAbsolutePath();
		System.out.println("File URL is " + transferURL);
		TypeObject typeObject = PkiTools.getTypeObject(transferFile);
		if (listModel.isEmpty()) {
			System.out.println("liste vide");
			if (this.getKsInfo() != null) {
				// TODO/create temp mag
			}
		}
		KeyTools kt = new KeyTools();
		StoreFormat format = StoreFormat.fromValue(typeObject);
		char[] truck=null;
		boolean isOpen=false;
		boolean createTempKS=false;
		switch (format) {
		case PKCS12:
			truck=MykeysFrame.showPasswordDialog(null);
			break;
		case DER:
		case PEM:
			isOpen=true;
			break;
		default:
			break;
		}

		if (this.getKsInfo() != null) {
//			FIXME check compatibility
			int retour = JOptionPane.showConfirmDialog(null,
					MessageUtils.getStringMessage("dialog.import_merge"));

			switch (retour) {
			case JOptionPane.YES_OPTION:
				// TODO: merge
				break;
			case JOptionPane.NO_OPTION:
				// TODO: creer nouveau magasin
				//kt.importStore(transferFile, format, "".toCharArray());
				createTempKS=true;

				break;
			default:
				break;
			}

		} else {
			createTempKS=true;


		}
		if (createTempKS){
			KeyStoreInfo newKsinfo = new KeyStoreInfo(transferFile, format, truck); //TODO:ksinfo.settemp
			newKsinfo.setOpen(isOpen);
				setKsInfo(newKsinfo);
				//kt.importStore(transferFile.getPath(), ksinfo.getStoreFormat(), ksinfo.getPassword());
				updateInfo(newKsinfo);
		}

		// if(!TypeObject.UNKNOWN.equals(typeObject)){
		// importFile(transferFile, typeObject, true);
		// }

		return !TypeObject.UNKNOWN.equals(typeObject);
	}

}
