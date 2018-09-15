package org.dpr.mykeys.ihm.components;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.PkiTools.TypeObject;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.ihm.actions.TreePopupMenu;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKsManager;
import org.dpr.mykeys.ihm.listeners.EventCompListener;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.*;
import java.util.List;

public class MainPanel extends JPanel implements
        DropTargetListener, EventCompListener {

    private final static Log log = LogFactory.getLog(MainPanel.class);
    private DetailPanel detailPanel;
    private CertificateListPanel listePanel;
    private TreeKsManager treeksKeystoreMngr;

    final String KS_AC_NAME = "store.ac.name";
    final String KS_CLI_NAME = "store.cert.name";
    final String KS_MRU_NAME = "mru.name";
    //
    // DefaultMutableTreeNode crlNode;
    //
    // DefaultMutableTreeNode sandBoxNode;
    private TreePopupMenu popup;

    public MainPanel(Dimension dim) {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


        treeksKeystoreMngr = new TreeKsManager();

        treeksKeystoreMngr.registerListener(this);


        treeksKeystoreMngr.addNode(KS_AC_NAME, new DefaultMutableTreeNode(Messages.getString(
                KS_AC_NAME)), true);
        treeksKeystoreMngr.addNode(KS_CLI_NAME, new DefaultMutableTreeNode(Messages.getString(
                KS_CLI_NAME)), true);
        treeksKeystoreMngr.addNode(KS_MRU_NAME, new DefaultMutableTreeNode(Messages.getString(
                KS_MRU_NAME)), true);
        // Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(treeksKeystoreMngr.getTree());
        JPanel leftPanel = new JPanel();
        listePanel = new CertificateListPanel();
        listePanel.registerListener(this);
        JSplitPane splitLeftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // Create the viewing pane.
        detailPanel = new DetailPanel();
        JScrollPane scrollDetail = new JScrollPane(detailPanel);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
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

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("TreeIconDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        MainPanel newContentPane = new MainPanel(null);
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static Map<String, String> getListCerts(String path, String type,
                                                   String password) throws KeyToolsException, KeyStoreException, ServiceException {
        KeyTools kt = new KeyTools();
        KeyStore ks = null;
        KeyStoreHelper ksv = new KeyStoreHelper(null);
        ks = ksv.loadKeyStore(path, StoreFormat.fromValue(type), password.toCharArray()).getKeystore();
        Map<String, String> certsAC = new HashMap<>();
        Enumeration<String> enumKs = ks.aliases();
        while (enumKs.hasMoreElements()) {
            String alias = enumKs.nextElement();
            Certificate cert = ks.getCertificate(alias);
            CertificateValue certInfo = ksv.fillCertInfo(ks, alias);
            certsAC.put(alias, alias);

        }

        return certsAC;

    }



    /**
     * Update nodes with keystores list
     *
     * @param ksList
     * @throws KeyStoreException
     */
    public void updateKSList(HashMap<String, KeyStoreValue> ksList) throws KeyStoreException {
        treeksKeystoreMngr.clear();
        // Set<String> dirs = ksList.keySet();
        SortedSet<String> dirs = new TreeSet<>(
                String.CASE_INSENSITIVE_ORDER);
        dirs.addAll(ksList.keySet());
        addInternalKS();

        for (String dir : dirs) {
            KeyStoreValue ksinfo = ksList.get(dir);
            DefaultMutableTreeNode node = null;
            if (ksinfo.getStoreModel().equals(StoreModel.CASTORE)) {
                node = treeksKeystoreMngr.addObject(KS_AC_NAME, ksinfo, true);
            } else {

                if (ksinfo.getStoreType().equals(StoreLocationType.INTERNAL))
                    node = treeksKeystoreMngr.addObject(KS_CLI_NAME, ksinfo, true);

                else

                    node = treeksKeystoreMngr.addObject(KS_MRU_NAME, ksinfo, true);

            }
        }
        //end loop




    }

    private void addInternalKS() throws KeyStoreException {
        DefaultMutableTreeNode nodei = treeksKeystoreMngr.addObject(KS_AC_NAME,
                KSConfig.getInternalKeystores().getStoreAC(), true);

        nodei = treeksKeystoreMngr.addObject(KS_CLI_NAME, KSConfig.getInternalKeystores().getStoreCertificate(), true);
        //nodei = addObject(adminNode, KSConfig.getInternalKeystores().getStoreProfils(), true);
    }

    public void addCertificate(DefaultMutableTreeNode node, boolean b) throws ServiceException {
        treeksKeystoreMngr.addCertificate(node, b);

    }

    public void importCertificate(DefaultMutableTreeNode node, boolean b) {
        treeksKeystoreMngr.importCertificate(node, b);
    }

    public void changePassword(DefaultMutableTreeNode node, boolean b) {
        treeksKeystoreMngr.changePassword(node, b);
    }


    /**
     * .
     * <p>
     * <BR>
     *
     * @param node
     * @param b
     */
    public void addCertificateAC(DefaultMutableTreeNode node, boolean b) {
        treeksKeystoreMngr.addCertificateAC(node, b);
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

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        boolean isActionCopy = false;
        System.out.println("drop");
        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
            if ((dtde.getDropAction() & DnDConstants.ACTION_COPY) != 0) {
                isActionCopy = true;
            }
            // Accept the drop and get the transfer data
            dtde.acceptDrop(dtde.getDropAction());
            Transferable transferable = dtde.getTransferable();

            try {
                boolean result = false;
                List fileList = (List) transferable
                        .getTransferData(DataFlavor.javaFileListFlavor);
                File transferFile = (File) fileList.get(0);
                TypeObject typeObject = PkiTools.getTypeObject(transferFile);
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) && typeObject != TypeObject.UNKNOWN && typeObject != null) {

                    result = dropFile(transferable, isActionCopy);
                } else {
                    result = false;
                }

                dtde.dropComplete(result);

            } catch (Exception e) {
                log.error("Exception while handling drop ", e);
                dtde.rejectDrop();
            }
        } else {
            log.info("Drop target rejected drop");
            dtde.dropComplete(false);
        }
    }

    private boolean dropFile(Transferable transferable, boolean isActionCopy) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        System.out.println("dropevent");

    }

    @Override
    public void showingCertListRequested(NodeInfo info) {
        try {
            listePanel.updateInfo(info);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showingCertDetailRequested(ChildInfo info) {

        detailPanel.updateInfo(info);

    }


    class TreeTransferHandler extends TransferHandler {
        DataFlavor nodesFlavor;
        DataFlavor[] flavors = new DataFlavor[1];

        /**
         * .
         * <p>
         * <BR><pre>
         * <b>Algorithme : </b>
         * DEBUT
         * <p>
         * FIN</pre>
         *
         * @param arg0
         * @return
         * @see TransferHandler#importData(TransferSupport)
         */
        @Override
        public boolean importData(TransferSupport arg0) {
            // TODO Auto-generated method stub
            return super.importData(arg0);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.swing.TransferHandler#canImport(javax.swing.TransferHandler
         * .TransferSupport)
         */
        @Override
        public boolean canImport(TransferSupport support) {

            if (!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            //log.trace(nodesFlavor.getHumanPresentableName());
            if (!support.isDataFlavorSupported(nodesFlavor)) {
                //return false;
            }
            return true;
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
            log.trace("popuprr");
            String ac = e.getActionCommand();
            TreePath path = tree.getPathForLocation(loc.x, loc.y);
            // //log.trace("path = " + path);
            // //System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
            // if(ac.equals("ADD CHILD"))
            // log.trace("popuprr");
            // if(ac.equals("ADD SIBLING"))
            // addSibling(path);
        }
    }

}