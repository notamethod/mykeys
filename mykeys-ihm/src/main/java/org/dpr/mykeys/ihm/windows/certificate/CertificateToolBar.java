package org.dpr.mykeys.ihm.windows.certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.components.CertificateListPanel;
import org.dpr.mykeys.ihm.components.ObjToolBar;
import org.dpr.mykeys.ihm.listeners.CertificateActionListener;
import org.dpr.mykeys.ihm.listeners.CertificateActionPublisher;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import static org.dpr.swingtools.ImageUtils.createImageIcon;

public class CertificateToolBar extends ObjToolBar implements CertificateActionPublisher {

    private static final Log log = LogFactory.getLog(CertificateToolBar.class);
	private JButton addCertButton;
	private JButton addCertProfButton;
	private JButton addCertFromCSRButton;
	private JButton importButton;
	private JButton exportButton;
	private JButton deleteButton;
	private JButton CrlManagerButton;
	private JToggleButton unlockButton;

	private String title;
    private List<CertificateActionListener> listeners = new ArrayList<>();
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    CertificateListPanel.KeysAction actions;

    public CertificateToolBar(String name, CertificateListPanel.KeysAction actions) {
		super(name);
		setFloatable(false);
		this.actions=actions;
		init();
	}

	private void init() {
		JLabel titre = new JLabel();
		addCertButton = new JButton(createImageIcon("/images/add-cert.png"));
		addCertProfButton = new JButton(createImageIcon("/images/add-cert-pro.png"));
		addCertFromCSRButton = new JButton(createImageIcon("/images/add-csr2.png"));
		unlockButton = new JToggleButton(createImageIcon("/images/Locked.png"));
		unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
		unlockButton.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
			      if(ev.getStateChange()==ItemEvent.SELECTED){
			      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
			      }
			   }
			});

        unlockButton.addActionListener(e -> log.debug("Handled by Lambda listener"));
		// unlockButton.setIcon(createImageIcon("/images/Locked.png"));

		unlockButton.setDisabledIcon(createImageIcon("/images/Unlocked.png"));
		addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
		addCertFromCSRButton.setActionCommand(TypeAction.ADD_CERT_FROMCSR.getValue());
		addCertProfButton.setActionCommand(TypeAction.ADD_CERT_PROF.getValue());
		addCertButton.setToolTipText("create a new certificate");
        addCertProfButton.setToolTipText("<html>create certificate from a template template</html>");
		addCertFromCSRButton.setToolTipText("create certificate from a CSR request");
		importButton = new JButton(createImageIcon("/images/import.png")); 
		importButton.setActionCommand(TypeAction.IMPORT_CERT.getValue());
        importButton.setToolTipText(Messages.getString("import_button.tooltip"));
		exportButton = new JButton(createImageIcon("/images/export.png"));
		exportButton.setActionCommand(TypeAction.EXPORT_CERT.getValue());
        exportButton.setToolTipText(Messages.getString("export_button.tooltip"));


		CrlManagerButton = new JButton(createImageIcon("/images/revok.png"));
        CrlManagerButton.setActionCommand(TypeAction.CREATE_CRL.getValue());
        CrlManagerButton.setToolTipText(Messages.getString(Messages.getString("crl.create.tooltip")));
        CrlManagerButton.setEnabled(false);
        CrlManagerButton.addActionListener(e -> notifyCreateCrl("what ?"));

		// FIXME libelles
		deleteButton = new JButton(createImageIcon("/images/trash_can.png"));
        //deleteButton.setActionCommand(TypeAction.DELETE_CERT.getValue());
        deleteButton.setToolTipText(Messages.getString("delete_certificate.tooltip"));

		deleteButton.setEnabled(false);
		exportButton.setEnabled(false);
		importButton.setEnabled(false);


        exportButton.addActionListener(e -> notifyExportCertificate("what ?"));
        importButton.addActionListener(e -> notifyImportCertificate("what ?"));
        unlockButton.addActionListener(e -> notifyopenStore("what ?"));
        deleteButton.addActionListener(e -> notifyCertificateDeletion("what ?"));


        addCertProfButton.addActionListener(e -> notifyInsertCertificateFromProfile("what ?"));
        addCertFromCSRButton.addActionListener(e -> notifyInsertCertificateFromCSR("what ?"));
		titre.setText(title);
		add(titre);
		add(unlockButton);
		add(addCertButton);
		add(addCertProfButton);
		add(addCertFromCSRButton);
        add(CrlManagerButton);
		add(importButton);
		add(exportButton);
		add(deleteButton);
		addSeparator();
	}

	public void enableActions() {
		unlockButton.setSelected(false);
		unlockButton.setEnabled(false);
		exportButton.setEnabled(true);
		deleteButton.setEnabled(true);
		importButton.setEnabled(true);
		addCertButton.setEnabled(true);
		addCertFromCSRButton.setEnabled(true);
		addCertProfButton.setEnabled(true);
	}

	void enableCertActions() {
		
		exportButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}

    public void disableActions(NodeInfo info) {
		importButton.setEnabled(false);
		exportButton.setEnabled(false);
		deleteButton.setEnabled(false);
		addCertButton.setEnabled(false);
		addCertProfButton.setEnabled(false);
        addCertFromCSRButton.setVisible((info instanceof KeyStoreValue) & ((KeyStoreValue) info).getStoreModel().equals(StoreModel.CASTORE));
		addCertFromCSRButton.setEnabled(false);
		unlockButton.setSelected(false);
		unlockButton.setEnabled(true);
	}


	public void enableListeners() {
        addCertButton.addActionListener(e -> notifyInsertCertificate(null));
		
	}

	public void removeListeners() {
        for (ActionListener al : addCertButton.getActionListeners()) {
            addCertButton.removeActionListener(al);
        }
	}

    @Override
    public void enableGenericActions(NodeInfo info, boolean b) {
        unlockButton.setSelected(false);
        unlockButton.setEnabled(false);
        exportButton.setEnabled(false);
        deleteButton.setEnabled(false);
        CrlManagerButton.setVisible((info instanceof KeyStoreValue) & (((KeyStoreValue) info).isCAStore()));
        CrlManagerButton.setEnabled(false);
        importButton.setEnabled(true);
        addCertButton.setEnabled(true);
        addCertFromCSRButton.setVisible(isCertStore(info));
        addCertFromCSRButton.setEnabled(true);
        addCertProfButton.setEnabled(true);
    }

    private boolean isCertStore(NodeInfo info) {
        return (info instanceof KeyStoreValue) & ((KeyStoreValue) info).isCertStore();
    }
    @Override
    public void enableElementActions(NodeInfo info, ChildInfo ci, boolean b) {

        exportButton.setEnabled(b);
        deleteButton.setEnabled(b);
        CrlManagerButton.setEnabled(b ? (ci instanceof CertificateValue) && (((CertificateValue) ci).isAcceptChildAC()) : false);

    }

    @Override
    public void notifyopenStore(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.openStoreRequested("what !");
        }

    }

    @Override
    public void notifyInsertCertificate(CertificateValue what) {
        for (CertificateActionListener listener : listeners) {
            listener.insertCertificateRequested(what);
        }

    }

    @Override
    public void notifyInsertCertificateFromProfile(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.insertCertificateFromProfileRequested("what !");
        }

    }

    @Override
    public void notifyInsertCertificateFromCSR(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.insertCertificateFromCSRRequested("what !");
        }
    }

    @Override
    public void notifyImportCertificate(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.importCertificateRequested("what !");
        }
    }

    @Override
    public void notifyExportCertificate(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.exportCertificateRequested("what !");
        }

    }

    @Override
    public void notifyCertificateDeletion(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.deleteCertificateRequested("what !");
        }
    }

    @Override
    public void notifyCreateCrl(String what) {
        for (CertificateActionListener listener : listeners) {
            listener.createCrlRequested("what !");
        }

    }

    @Override
    public void registerListener(CertificateActionListener listener) {
        System.out.println("register " + listener);
        listeners.add(listener);

    }
}
