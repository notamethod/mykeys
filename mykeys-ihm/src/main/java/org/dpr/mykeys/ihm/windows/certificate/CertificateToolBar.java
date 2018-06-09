package org.dpr.mykeys.ihm.windows.certificate;

import static org.dpr.swingtools.ImageUtils.createImageIcon;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.actions.TypeAction;

import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.components.ObjToolBar;

public class CertificateToolBar extends ObjToolBar  {

	private JButton addCertButton;
	private JButton addCertProfButton;
	private JButton addCertFromCSRButton;
	private JButton importButton;
	private JButton exportButton;
	private JButton deleteButton;
	private JButton CrlManagerButton;
	private JToggleButton unlockButton;

	private String title;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	ListPanel.KeysAction actions;

	public CertificateToolBar(String name, ListPanel.KeysAction actions) {
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

		unlockButton.addActionListener(e -> System.out.println("Handled by Lambda listener"));
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
        CrlManagerButton.addActionListener(actions);

		// FIXME libelles
		deleteButton = new JButton(createImageIcon("/images/trash_can.png"));
		deleteButton.setActionCommand(TypeAction.DELETE_CERT.getValue());
        deleteButton.setToolTipText(Messages.getString("delete_certificate.tooltip"));

		deleteButton.setEnabled(false);
		exportButton.setEnabled(false);
		importButton.setEnabled(false);


        exportButton.addActionListener(actions);
		importButton.addActionListener(actions);
		unlockButton.addActionListener(actions);
		deleteButton.addActionListener(actions);
		addCertProfButton.addActionListener(actions);
		addCertFromCSRButton.addActionListener(actions);
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
		addCertButton.addActionListener(actions);
		
	}

	public void removeListeners() {
		addCertButton.removeActionListener(actions);
		
	}

    @Override
    public void enableGenericActions(NodeInfo info, boolean b) {
        unlockButton.setSelected(false);
        unlockButton.setEnabled(false);
        exportButton.setEnabled(false);
        deleteButton.setEnabled(false);
        CrlManagerButton.setVisible((info instanceof KeyStoreValue) & ((KeyStoreValue) info).getStoreModel().equals(StoreModel.CASTORE));
        CrlManagerButton.setEnabled(false);
        importButton.setEnabled(true);
        addCertButton.setEnabled(true);
        addCertFromCSRButton.setVisible((info instanceof KeyStoreValue) & ((KeyStoreValue) info).getStoreModel().equals(StoreModel.CERTSTORE));
        addCertFromCSRButton.setEnabled(true);
        addCertProfButton.setEnabled(true);

    }

    @Override
    public void enableElementActions(boolean b) {

        exportButton.setEnabled(b);
        deleteButton.setEnabled(b);
        CrlManagerButton.setEnabled(b);
    }

}
