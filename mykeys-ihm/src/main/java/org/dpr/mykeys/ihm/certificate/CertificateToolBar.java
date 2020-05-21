package org.dpr.mykeys.ihm.certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.components.ObjToolBar;
import org.dpr.mykeys.ihm.listeners.*;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private final List<CertificateActionListener> listeners = new ArrayList<>();
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


    public CertificateToolBar(String name) {
		super(name);
		setFloatable(false);
		init();
	}

	private void init() {
		JLabel titre = new JLabel();
		addCertButton = new JButton(createImageIcon("/images/add-cert.png"));
		addCertProfButton = new JButton(createImageIcon("/images/add-cert-pro.png"));
		addCertFromCSRButton = new JButton(createImageIcon("/images/add-csr2.png"));
		unlockButton = new JToggleButton(createImageIcon("/images/Locked.png"));
		unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
        unlockButton.addItemListener(ev -> {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
            } else if (ev.getStateChange() == ItemEvent.DESELECTED) {
            }
        });

        unlockButton.addActionListener(e -> log.debug("Handled by Lambda listener"));
        unlockButton.addMouseListener(new HelpMouseListener("unlock_store"));
		unlockButton.setDisabledIcon(createImageIcon("/images/Unlocked.png"));
		addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
		addCertFromCSRButton.setActionCommand(TypeAction.ADD_CERT_FROMCSR.getValue());
		addCertProfButton.setActionCommand(TypeAction.ADD_CERT_PROF.getValue());
        addCertButton.addMouseListener(new HelpMouseListener("add_cert"));
        addCertProfButton.addMouseListener(new HelpMouseListener("add_cert_fromtemplate"));
        addCertFromCSRButton.addMouseListener(new HelpMouseListener("add_cert_fromcsr"));
		importButton = new JButton(createImageIcon("/images/import.png")); 
		importButton.setActionCommand(TypeAction.IMPORT_CERT.getValue());
        importButton.addMouseListener(new HelpMouseListener("import_cert"));

		exportButton = new JButton(createImageIcon("/images/export.png"));
		exportButton.setActionCommand(TypeAction.EXPORT_CERT.getValue());
        exportButton.addMouseListener(new HelpMouseListener("export_cert"));
//        exportButton.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent mouseEvent) {
//                ApplicationHelp.getInstance().requestHelp("export_cert");
//            }});

		CrlManagerButton = new JButton(createImageIcon("/images/revok.png"));
        CrlManagerButton.setActionCommand(TypeAction.CREATE_CRL.getValue());
        CrlManagerButton.setToolTipText(Messages.getString(Messages.getString("crl.create.tooltip")));
        CrlManagerButton.setEnabled(false);
        CrlManagerButton.addActionListener(e -> notifyCreateCrl("what ?"));

		// FIXME libelles
		deleteButton = new JButton(createImageIcon("/images/trash_can.png"));
        deleteButton.addMouseListener(new HelpMouseListener("delete_cert"));
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
        CrlManagerButton.setEnabled(b && ((ci instanceof CertificateValue) && (((CertificateValue) ci).isAcceptChildAC())));

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
        log.debug("register " + listener);
        listeners.add(listener);

    }
}
