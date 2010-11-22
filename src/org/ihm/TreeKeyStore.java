package org.ihm;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of Sun Microsystems nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.app.CertificateInfo;
import org.app.InternalKeystores;
import org.app.KeyStoreInfo;
import org.app.KeyStoreInfo.StoreModel;
import org.app.KeyStoreInfo.StoreType;
import org.app.KeyTools;
import org.app.KeyToolsException;
import org.ihm.components.GradientTree;
import org.ihm.components.GradientTreeRenderer;
import org.ihm.menuaction.TreePopupMenu;
import org.ihm.windows.CreateCertificatDialog;
import org.ihm.windows.DetailPanel;
import org.ihm.windows.ExportCertificateDialog;
import org.ihm.windows.ImportCertificateDialog;
import org.ihm.windows.ListPanel;

public class TreeKeyStore extends JPanel implements MouseListener,
	TreeExpansionListener, TreeWillExpandListener {

    public class TreeTransferHandler extends TransferHandler {
	     DataFlavor nodesFlavor;
	     DataFlavor[] flavors = new DataFlavor[1];
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport support) {

	         if(!support.isDrop()) {
	             return false;
	         }
	         support.setShowDropLocation(true);
	         System.out.println(nodesFlavor.getHumanPresentableName());
	         if(!support.isDataFlavorSupported(nodesFlavor)) {
	             return false;
	         }
	         return true;
	}

    }

    final static Log log = LogFactory.getLog(TreeKeyStore.class);
    private DetailPanel detailPanel;
    
    private ListPanel listePanel;

    private GradientTree tree;

    DefaultMutableTreeNode rootNode;

    DefaultMutableTreeNode cliNode;

    DefaultMutableTreeNode acNode;

    private TreeModel treeModel;

    TreePopupMenu popup;

    public TreeKeyStore(Dimension dim) {
	this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

	// Create the nodes.
	rootNode = new DefaultMutableTreeNode("Magasins");
	acNode = new DefaultMutableTreeNode(MyKeys.getMessage().getString(
		"store.ac.name"));
	cliNode = new DefaultMutableTreeNode(MyKeys.getMessage().getString(
		"store.cert.name"));

	treeModel = new TreeModel(rootNode);
	treeModel.addTreeModelListener(new TreeKeyStoreModelListener());

	tree = new GradientTree(treeModel);
	System.out.println(tree.getUI());
	
	GradientTreeRenderer renderer = new GradientTreeRenderer();

	tree.setCellRenderer(renderer);
	renderer.jtree1 = tree;
	ToolTipManager.sharedInstance().registerComponent(tree);
	// javax.swing.ToolTipManager.ToolTipManager.sharedInstance().registerComponent(tree);
	tree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);

	popup = new TreePopupMenu("Popup name", this);

	treeModel.insertNodeInto(acNode, rootNode, rootNode.getChildCount());
	treeModel.insertNodeInto(cliNode, rootNode, rootNode.getChildCount());

	tree.setRootVisible(false);

	tree.addMouseListener(this);
	tree.addTreeWillExpandListener(this);
	tree.addTreeExpansionListener(this);
	//drop enabled
        tree.setDropMode(DropMode.ON);
        tree.setTransferHandler(new TreeTransferHandler());
	// Create the scroll pane and add the tree to it.
	JScrollPane treeView = new JScrollPane(tree);
	JPanel leftPanel = new JPanel();
	listePanel = new ListPanel();
	JSplitPane splitLeftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	// Create the viewing pane.
	detailPanel = new DetailPanel();
	listePanel.setDetailPanel(detailPanel);
	JScrollPane scrollDetail = new JScrollPane(detailPanel);
	splitLeftPanel.setBottomComponent(scrollDetail);
	splitLeftPanel.setTopComponent(listePanel);
	splitLeftPanel.setDividerLocation(150);
	// Add the scroll panes to a split pane.
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	splitPane.setTopComponent(treeView);
	splitPane.setBottomComponent(splitLeftPanel);
	splitPane.setDividerLocation(210);
	
	
	// Add the split pane to this panel.
	add(splitPane);

    }

    private void displayCertDetail(CertificateInfo info) {
	detailPanel.updateInfo(info);

    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @param ksiInfo
     */
    private void displayKeystoreList(KeyStoreInfo info) {
	listePanel.updateInfo(info);
	
    }    
    /**
     * Update nodes with keystores list
     * 
     * @param ksList
     */
    public void updateKSList(HashMap<String, KeyStoreInfo> ksList) {
	clear();
	// Set<String> dirs = ksList.keySet();
	SortedSet<String> dirs = new TreeSet<String>(
		String.CASE_INSENSITIVE_ORDER);
	dirs.addAll(ksList.keySet());
	addInternalKS();

	Iterator<String> iter = dirs.iterator();
	while (iter.hasNext()) {
	    String dir = iter.next();
	    KeyStoreInfo ksinfo = ksList.get(dir);
	    DefaultMutableTreeNode node = null;
	    if (ksinfo.getStoreModel().equals(StoreModel.CASTORE)) {
		node = addObject(acNode, ksinfo, true);
	    } else {
		node = addObject(cliNode, ksinfo, true);

	    }
	   // addObject(node, "[Vide]", false);
	}
	// tree.repaint();

    }

    private void addInternalKS() {
	DefaultMutableTreeNode nodei = addObject(acNode, InternalKeystores
		.getACKeystore(), true);
	
	//addObject(nodei, "[Vide]", false);
	nodei = addObject(cliNode, InternalKeystores.getCertKeystore(), true);
	//addObject(nodei, "[Vide]", false);

    }

    /** Remove all nodes except the root node. */
    public void clear() {
	acNode.removeAllChildren();
	cliNode.removeAllChildren();
	treeModel.reload();
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
	TreePath currentSelection = tree.getSelectionPath();
	if (currentSelection != null) {
	    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
		    .getLastPathComponent());
	    MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
	    if (parent != null) {
		treeModel.removeNodeFromParent(currentNode);
		return;
	    }
	}

    }

    public void removeNode(DefaultMutableTreeNode node) {

	MutableTreeNode parent = (MutableTreeNode) (node.getParent());
	if (parent != null) {
	    treeModel.removeNodeFromParent(node);
	    return;
	}

    }

    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
	    Object child, boolean shouldBeVisible) {
	DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
	if (parent == null) {
	    parent = rootNode;
	}

	// It is key to invoke this on the TreeModel, and NOT
	// DefaultMutableTreeNode
	treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

	// Make sure the user can see the lovely new node.
	if (shouldBeVisible) {
	    tree.scrollPathToVisible(new TreePath(childNode.getPath()));
	}
	
	return childNode;
    }

    private void removeChildrenObjects(DefaultMutableTreeNode parent) {

	while (treeModel.getChildCount(parent) != 0) {
	    treeModel.removeNodeFromParent((DefaultMutableTreeNode) treeModel
		    .getChild(parent, 0));
	}

    }

    public boolean closeStore(DefaultMutableTreeNode node, boolean collapse) {
	KeyStoreInfo ksInfo = ((KeyStoreInfo) node.getUserObject());
	removeChildrenObjects(node);
	addObject(node, "[Vide]", false);
	if (collapse) {
	    ksInfo.setOpen(false);
	    tree.collapsePath(new TreePath(node.getPath()));
	}
	return true;

    }

    public boolean openStore(DefaultMutableTreeNode node,
	    boolean useInternalPwd, boolean expand) {
	KeyStoreInfo ksInfo = ((KeyStoreInfo) node.getUserObject());
	if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) { // equals(StoreModel.CASTORE))
								// {
	    useInternalPwd = true;
	}
	// ask for password
	if (!useInternalPwd) {
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
	    KeyStoreUI.showError(TreeKeyStore.this, e1.getMessage());
	    e1.printStackTrace();
	    return false;
	}
//	try {
//	    Enumeration<String> enumKs = ks.aliases();
//	    if (enumKs != null && enumKs.hasMoreElements()) {
//		removeChildrenObjects(node);
//		node.removeAllChildren();
//		while (enumKs.hasMoreElements()) {
//		    String alias = enumKs.nextElement();
//		    if (log.isDebugEnabled()) {
//			log.debug("alias:" + alias);
//		    }
//		    
//		    CertificateInfo certInfo = new CertificateInfo(alias);
//		    kt.fillCertInfo(ks, certInfo, alias);
//		    addObject(node, certInfo, false);
//		}
//	    }
//	} catch (KeyStoreException e) {
//	    // TODO Auto-generated catch block
//	    return false;
//	}
//	if (expand) {
//	    ksInfo.setOpen(true);
//	    tree.expandPath(new TreePath(node.getPath()));
//	}
	return true;

    }

    public void showPopupMenu(MouseEvent e) {
	DefaultMutableTreeNode tNode = null;
	int selRow = tree.getRowForLocation(e.getX(), e.getY());
	TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	if (selPath != null) {
	    tNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
	}

	popup.setNode(tNode);
	popup.show(tree, e.getX(), e.getY());

    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
	// Create and set up the window.
	JFrame frame = new JFrame("TreeIconDemo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// Create and set up the content pane.
	TreeKeyStore newContentPane = new TreeKeyStore(null);
	newContentPane.setOpaque(true); // content panes must be opaque
	frame.setContentPane(newContentPane);

	// Display the window.
	frame.pack();
	frame.setVisible(true);
    }

    public static void main(String[] args) {
	// Schedule a job for the event-dispatching thread:
	// creating and showing this application's GUI.
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		createAndShowGUI();
	    }
	});
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
	// System.out.println("collaps");

    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
	// System.out.println("expand");

    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event)
	    throws ExpandVetoException {
	// System.out.println("collapse1");

    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event)
	    throws ExpandVetoException {
	// System.out.println("ask expand");
	DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) event.getPath()
		.getLastPathComponent();
	if (tNode.getParent() != null) {
	    Object object = tNode.getUserObject();
	    if (object instanceof KeyStoreInfo) {
		if (((KeyStoreInfo) object).isOpen()) {
		    return;
		} else {

		    if (openStore(tNode, false, true)) {
			return;
		    }

		}
	    } else if (object instanceof String) {
		return;
	    }
	    throw new ExpandVetoException(event);

	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
	if (e.isPopupTrigger()) {
	    int selRow = tree.getRowForLocation(e.getX(), e.getY());
	    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	    if (selRow != -1) {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection == null
			|| !currentSelection.equals(selPath)) {
		    tree.setSelectionPath(selPath);
		}

	    }
	    showPopupMenu(e);

	}
	int selRow = tree.getRowForLocation(e.getX(), e.getY());
	TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	if (selRow != -1) {
	    if (e.getClickCount() == 1) {
		DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) selPath
			.getLastPathComponent();
		Object object = tNode.getUserObject();
		if (object instanceof CertificateInfo) {
		    CertificateInfo certInfo = ((CertificateInfo) object);
		    displayCertDetail(certInfo);
		    
		} else {
		    displayCertDetail(null);
			if (object instanceof KeyStoreInfo) {
			    KeyStoreInfo ksiInfo = ((KeyStoreInfo) object);
			    if (ksiInfo != null)
			    displayKeystoreList(ksiInfo);
			    
			}else{
			    displayKeystoreList(null);
			}
		}

		System.out.println(selPath);
	    } else if (e.getClickCount() == 2) {
		// DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)
		// selPath
		// .getLastPathComponent();
		// Object object = tNode.getUserObject();
		// if (object instanceof KeyStoreInfo) {
		// KeyStoreInfo ksInfo = ((KeyStoreInfo) object);
		//
		// if (!ksInfo.isOpen()) {
		// openStore(tNode, false, true);
		//
		// }
		// }
	    }
	}
    }



    @Override
    public void mouseReleased(MouseEvent e) {
	if (e.isPopupTrigger()) {
	    if (e.isPopupTrigger()) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {

		    TreePath currentSelection = tree.getSelectionPath();
		    if (currentSelection == null
			    || !currentSelection.equals(selPath)) {
			tree.setSelectionPath(selPath);
		    }

		}
		showPopupMenu(e);

	    }
	    showPopupMenu(e);
	}

    }

    class PopupHandler implements ActionListener {
	JTree tree;

	JPopupMenu popup;

	Point loc;

	public PopupHandler(JTree tree, JPopupMenu popup) {
	    this.tree = tree;
	    this.popup = popup;
	    // tree.addMouseListener(ma);
	}

	public void actionPerformed(ActionEvent e) {
	    System.out.println("popuprr");
	    String ac = e.getActionCommand();
	    TreePath path = tree.getPathForLocation(loc.x, loc.y);
	    // //System.out.println("path = " + path);
	    // //System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
	    // if(ac.equals("ADD CHILD"))
	    // System.out.println("popuprr");
	    // if(ac.equals("ADD SIBLING"))
	    // addSibling(path);
	}
    }

    public void addCertificate(DefaultMutableTreeNode node, boolean b) {
	JFrame frame = (JFrame) tree.getTopLevelAncestor();
	KeyStoreInfo ksInfo = null;
	Object object = node.getUserObject();
	if (object instanceof KeyStoreInfo) {
	    ksInfo = ((KeyStoreInfo) object);
	}
	CreateCertificatDialog cs = new CreateCertificatDialog(frame, ksInfo,
		true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);
	openStore(node, true, true);

	return;

    }

    public void importCertificate(DefaultMutableTreeNode node, boolean b) {
	JFrame frame = (JFrame) tree.getTopLevelAncestor();
	KeyStoreInfo ksInfo = null;
	Object object = node.getUserObject();
	if (object instanceof KeyStoreInfo) {
	    ksInfo = ((KeyStoreInfo) object);
	}
	ImportCertificateDialog cs = new ImportCertificateDialog(frame, ksInfo,
		true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);
	openStore(node, true, true);

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
     * @param node
     * @param b
     */
    public void exporterCertificate(DefaultMutableTreeNode node, boolean b) {
	JFrame frame = (JFrame) tree.getTopLevelAncestor();
	// KeyStoreInfo ksInfo = null;
	CertificateInfo certInfo = null;
	Object object = node.getUserObject();
	if (object instanceof CertificateInfo) {
	    certInfo = ((CertificateInfo) object);
	}
	KeyStoreInfo ksInfo = null;
	DefaultMutableTreeNode objectKs = (DefaultMutableTreeNode) node
		.getParent();// .getUserObject();
	if (objectKs.getUserObject() instanceof KeyStoreInfo) {
	    ksInfo = ((KeyStoreInfo) objectKs.getUserObject());
	}
	ExportCertificateDialog cs = new ExportCertificateDialog(frame, ksInfo,
		certInfo, true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);

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
     * @param node
     * @param b
     */
    public void addCertificateAC(DefaultMutableTreeNode node, boolean b) {
	JFrame frame = (JFrame) tree.getTopLevelAncestor();
	KeyStoreInfo ksInfo = null;
	Object object = node.getUserObject();
	if (object instanceof KeyStoreInfo) {
	    ksInfo = ((KeyStoreInfo) object);
	}
	CreateCertificatDialog cs = new CreateCertificatDialog(frame, ksInfo,
		true);
	cs.setLocationRelativeTo(frame);
	cs.setResizable(false);
	cs.setVisible(true);
	openStore(node, true, true);

	return;
    }

    public static Map<String, String> getListCerts(String path, String type,
	    String password) throws KeyToolsException, KeyStoreException {
	KeyTools kt = new KeyTools();
	KeyStore ks = null;

	ks = kt.loadKeyStore(path, type, password.toCharArray());
	Map<String, String> certsAC = new HashMap<String, String>();
	Enumeration<String> enumKs = ks.aliases();
	while (enumKs.hasMoreElements()) {
	    String alias = enumKs.nextElement();
	    Certificate cert = ks.getCertificate(alias);

	    CertificateInfo certInfo = new CertificateInfo(alias);
	    kt.fillCertInfo(ks, certInfo, alias);

	    certsAC.put(alias, alias);

	}

	return certsAC;

    }

}
