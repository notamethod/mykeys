package org.dpr.mykeys.ihm.components;

import static org.dpr.swingutils.ImageUtils.createImageIcon;
import static org.dpr.mykeys.utils.MessageUtils.getMessage;
import java.awt.BorderLayout;
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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.certificate.CertificateInfo;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.windows.ListCertRenderer;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.certificate.CreateCertProfilDialog;
import org.dpr.mykeys.ihm.windows.certificate.CreateCertificatDialog;
import org.dpr.mykeys.ihm.windows.certificate.ExportCertificateDialog;
import org.dpr.mykeys.ihm.windows.certificate.ImportCertificateDialog;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.keystore.ActionStatus;
import org.dpr.mykeys.keystore.KeyStoreInfo;
import org.dpr.mykeys.keystore.KeyStoreService;
import org.dpr.mykeys.keystore.StoreFormat;
import org.dpr.mykeys.profile.CreateProfilDialog;
import org.dpr.mykeys.profile.ProfilStoreInfo;
import org.dpr.swingutils.LabelValuePanel;

@SuppressWarnings("serial")
public class ListPanel extends JPanel implements DropTargetListener {
	public static final Log log = LogFactory.getLog(ListPanel.class);

	ToolBarManager toolBarManager = new ToolBarManager();

	public class ListTransferHandler extends TransferHandler {
		DataFlavor certFlavor;

		public ListTransferHandler() {
			try {
				String certType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
						+ org.dpr.mykeys.app.certificate.CertificateInfo.class.getName() + "\"";
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
					if (((JList) e.getSource()).getSelectedValue() instanceof ChildInfo) {
						displayDetail((ChildInfo) ((JList) e.getSource()).getSelectedValue());
						if (ksInfo.isOpen()) {
							toolBarManager.enableActions(ksInfo);

						}
					}

				}
			}

		}

	}

	// Map<String, String> elements = new HashMap<String, String>();
	LabelValuePanel infosPanel;

	NodeInfo ksInfo;

	ActionPanel dAction;

	JPanel jp;

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
		dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
		dAction = new ActionPanel();

		jp = new JPanel(new BorderLayout());
		final ImageIcon icon = createImageIcon("/images/Locked.png");

		actions = new KeysAction(this, this);

		toolBarManager.init("Still draggable", actions, this);
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
		// listCerts.setTransferHandler(new ListTransferHandler());

		JScrollPane listScroller = new JScrollPane(listCerts);
		// listScroller.setPreferredSize(new Dimension(450, 80));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		jp.add(listScroller, BorderLayout.CENTER);

		add(jp);

		jp.setVisible(false);
	}

	public void updateInfo(NodeInfo info) throws ServiceException {
		jp.setVisible(false);

		jp.remove(toolBarManager.getInvInstance(info));
		jp.add(toolBarManager.getInstance(info), BorderLayout.PAGE_START);
		// jp.revalidate();
		if (info == null) {
			return;
		}
		ksInfo = info;
		listCerts.clearSelection();
		listModel.removeAllElements();
		// FIXME
		if (ksInfo instanceof ProfilStoreInfo) {
			for (ChildInfo ci : ksInfo.getChildList()) {
				listModel.addElement(ci);
			}
		} else {
			KeyStoreService ks = new KeyStoreService((KeyStoreInfo) ksInfo);
			for (ChildInfo ci : ks.getChildList()) {
				listModel.addElement(ci);
			}
		}
		toolBarManager.removeListeners(info);
		// addCertProfButton.removeActionListener(actions);
		if (ksInfo.isOpen()) {

			toolBarManager.enableActions(info);
			toolBarManager.enableListeners(info);

			// addCertProfButton.addActionListener(actions);
			listCerts.setShowImage(false);

		} else {

			toolBarManager.disableActions(info);
			listCerts.setShowImage(true);
		}
		toolBarManager.setTitle(ksInfo.getName());
		// toolBarManager.show(info);

		jp.revalidate();
		jp.setVisible(true);
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

	private void displayDetail(ChildInfo info) {
		detailPanel.updateInfo(info);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#setDetailPanel(org.dpr.mykeys.
	 * ihm.components.DetailPanel)
	 */

	public void setDetailPanel(DetailPanel detailPanel) {
		this.detailPanel = detailPanel;
	}

	/**
	 * @return the ksInfo
	 */
	public NodeInfo getKsInfo() {
		return ksInfo;
	}

	/**
	 * @param ksInfo
	 *            the ksInfo to set
	 */
	public void setKsInfo(KeyStoreInfo ksInfo) {
		this.ksInfo = ksInfo;
	}

	public void addElement(NodeInfo info, boolean b) throws ServiceException {

		JFrame frame = (JFrame) this.getTopLevelAncestor();
		SuperCreate cs = null;
		if (info instanceof KeyStoreInfo) {
			cs = new CreateCertificatDialog(frame, (KeyStoreInfo) info, true);
		} else {
			cs = new CreateProfilDialog(frame, true);
		}
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);
		updateInfo(info);

		return;
	}

	public void addCertFromPRofile(NodeInfo info, boolean b) throws ServiceException {

		JFrame frame = (JFrame) this.getTopLevelAncestor();
		SuperCreate cs = null;
		if (info instanceof KeyStoreInfo) {
			cs = new CreateCertProfilDialog(frame, (KeyStoreInfo) info, true);
		}
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);
		updateInfo(info);

		return;
	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * 
	 * @param ksInfo2
	 * @param certificateInfo
	 * @throws ServiceException
	 */
	public void deleteCertificate(NodeInfo info, CertificateInfo certificateInfo) throws ServiceException {
		KeyStoreInfo kinfo = (KeyStoreInfo) info;
		KeyStoreService ksv = new KeyStoreService(kinfo);
		try {
			ksv.removeCertificate(certificateInfo);

		} catch (Exception e1) {
			MykeysFrame.showError(this, e1.getMessage());
			e1.printStackTrace();
		}
		updateInfo(ksInfo);

	}

	public void exporterCertificate(NodeInfo info, CertificateInfo certificateInfo, boolean b) {
		KeyStoreInfo kinfo = (KeyStoreInfo) info;
		JFrame frame = (JFrame) this.getTopLevelAncestor();

		ExportCertificateDialog cs = new ExportCertificateDialog(frame, kinfo, certificateInfo, true);
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);

	}

	public void importCertificate(NodeInfo info, boolean b) throws ServiceException {
		KeyStoreInfo kinfo = (KeyStoreInfo) info;
		JFrame frame = (JFrame) this.getTopLevelAncestor();

		ImportCertificateDialog cs = new ImportCertificateDialog(frame, kinfo, true);
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);
		updateInfo(kinfo);

	}

	public boolean openStore(boolean useInternalPwd, boolean expand) {

		if (ksInfo.isProtected()) {

			KeyStoreInfo kstInfo = (KeyStoreInfo) ksInfo;
			if (kstInfo.getPassword() == null) {
				char[] password = MykeysFrame.showPasswordDialog(this);

				if (password == null || password.length == 0) {
					return false;
				}

				kstInfo.setPassword(password);
			}

		}

		try {
			KeyStoreService kserv = new KeyStoreService((KeyStoreInfo) ksInfo);
			kserv.open();

			ksInfo.setOpen(true);

		} catch (Exception e1) {
			MykeysFrame.showError(this, e1.getMessage());
			e1.printStackTrace();
			return false;
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dragEnter(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dragExit(java.awt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dragOver(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#drop(java.awt.dnd.
	 * DropTargetDropEvent)
	 */
	@Override
	public void drop(DropTargetDropEvent dtde) {
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();

			try {
				boolean result = false;

				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					result = dropFile(transferable);
				} else {
					result = false;
				}

				dtde.dropComplete(result);

			} catch (Exception e) {
				System.out.println("Exception while handling drop " + e);
				MykeysFrame.showError(this, getMessage("error.dnd"));
				dtde.rejectDrop();
			}
		} else {
			System.out.println("Drop target rejected drop");
			dtde.dropComplete(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dropActionChanged(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	// This method handles a drop for a list of files
	protected boolean dropFile(Transferable transferable)
			throws IOException, UnsupportedFlavorException, MalformedURLException, ServiceException {

		List fileList = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
		File transferFile = (File) fileList.get(0);
		KeyStoreInfo ksin = new KeyStoreInfo(transferFile, null, null);
		KeyStoreService service = new KeyStoreService((KeyStoreInfo) ksInfo);
		final String transferURL = transferFile.getAbsolutePath();

		ActionStatus act = null;
		try {
			act = service.importCertificates(ksin);
			if (act.equals(ActionStatus.ASK_PASSWORD)) {
				char[] password = MykeysFrame.showPasswordDialog(this);
				ksin.setPassword(password);

				service.importCertificates(ksin);
			}
			updateInfo(ksInfo);
		} catch (KeyToolsException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("File URL is " + transferURL);

		return true;
	}

}
