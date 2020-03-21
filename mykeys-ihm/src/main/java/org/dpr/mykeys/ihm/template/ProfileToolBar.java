package org.dpr.mykeys.ihm.template;

import static org.dpr.swingtools.ImageUtils.createImageIcon;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.components.KeysProfileAction;
import org.dpr.mykeys.ihm.components.ObjToolBar;

public class ProfileToolBar extends ObjToolBar {

    private JButton addCertButton;

    private JButton importButton;

    private JButton deleteButton;
    private JToggleButton unlockButton;

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private final KeysProfileAction actions;

    public ProfileToolBar(String name, KeysProfileAction actions) {
        super(name);
        setFloatable(false);
        this.actions = actions;
        init();
    }

    private void init() {
        JLabel titre = new JLabel();
        addCertButton = new JButton(createImageIcon("/images/add-cert.png"));
        unlockButton = new JToggleButton(createImageIcon("/images/Locked.png"));
        unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
        // unlockButton.setIcon(createImageIcon("/images/Locked.png"));
        unlockButton.setDisabledIcon(createImageIcon("/images/Unlocked.png"));
        addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());

        importButton = new JButton("Import");
        importButton.setActionCommand(TypeAction.IMPORT_CERT.getValue());

        // FIXME libelles
        deleteButton = new JButton("Supprimer");
        deleteButton.setActionCommand(TypeAction.DELETE_CERT.getValue());
        deleteButton.setEnabled(false);

        importButton.setEnabled(false);

        importButton.addActionListener(actions);
        unlockButton.addActionListener(actions);
        deleteButton.addActionListener(actions);

        titre.setText(title);
        add(titre);
        add(unlockButton);
        add(addCertButton);

        add(importButton);

        add(deleteButton);
        addSeparator();

    }

    public void enableActions() {
        unlockButton.setSelected(false);
        unlockButton.setEnabled(false);

        deleteButton.setEnabled(true);
        importButton.setEnabled(true);
        addCertButton.setEnabled(true);

    }

    public void disableActions(NodeInfo info) {
        importButton.setEnabled(false);

        deleteButton.setEnabled(false);
        addCertButton.setEnabled(false);

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

    }

    @Override
    public void enableElementActions(NodeInfo info, ChildInfo ci, boolean b) {

    }

}
