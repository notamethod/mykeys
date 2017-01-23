package org.dpr.mykeys.ihm.components;

import static org.dpr.swingutils.ImageUtils.createImageIcon;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.dpr.mykeys.ihm.actions.TypeAction;

public class CertificateToolBar extends ObjToolBar  {

	JButton addCertButton;
	JButton addCertProfButton;
	JButton importButton;
	JButton exportButton;
	JButton deleteButton;
	JToggleButton unlockButton;
	
	String title;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	KeysAction actions;
	
	public CertificateToolBar(String name, KeysAction actions) {
		super(name);
		setFloatable(false);
		this.actions=actions;
		init();
	}

	private void init() {
		JLabel titre = new JLabel();
		addCertButton = new JButton(createImageIcon("add-cert.png"));
		addCertProfButton = new JButton(createImageIcon("add-cert-pro.png"));
		unlockButton = new JToggleButton(createImageIcon("Locked.png"));
		unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
		// unlockButton.setIcon(createImageIcon("Locked.png"));
		unlockButton.setDisabledIcon(createImageIcon("Unlocked.png"));
		addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
		addCertProfButton.setActionCommand(TypeAction.ADD_CERT_PROF.getValue());
		addCertProfButton.setToolTipText("create cert from profile");
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
		
		exportButton.addActionListener(actions);
		importButton.addActionListener(actions);
		unlockButton.addActionListener(actions);
		deleteButton.addActionListener(actions);
		addCertProfButton.addActionListener(actions);
		titre.setText(title);
		add(titre);
		add(unlockButton);
		add(addCertButton);
		add(addCertProfButton);
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
		
	}

	public void disableActions() {
		importButton.setEnabled(false);
		exportButton.setEnabled(false);
		deleteButton.setEnabled(false);
		addCertButton.setEnabled(false);
		addCertProfButton.setEnabled(false);
		unlockButton.setSelected(false);

		unlockButton.setEnabled(true);
		
	}

	public void enableListeners() {
		addCertButton.addActionListener(actions);
		
	}

	public void removeListeners() {
		addCertButton.removeActionListener(actions);
		
	}

}
