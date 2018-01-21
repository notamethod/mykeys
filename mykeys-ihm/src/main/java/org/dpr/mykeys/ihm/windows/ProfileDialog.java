package org.dpr.mykeys.ihm.windows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.model.ProfileModel;
import org.dpr.mykeys.profile.CreateProfilDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

public class ProfileDialog extends JFrame {
    public static final Log log = LogFactory.getLog(ListPanel.class);

    private JTable table;
    private ProfileModel modele;

    public ProfileDialog() {
        setTitle(Messages.getString("template.title"));
        setPreferredSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modele = new ProfileModel((List<CertificateTemplate>) ProfileServices.getProfils(KSConfig.getProfilsPath()));

        table = new JTable(modele);
        table.setRowSelectionAllowed(true);


        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);
        JPanel jp1 = new JPanel();
        jp.add(new JScrollPane(table), CENTER);
        // jp.add(jp1);
        jp.add(getButtonPanel());
        init();
        pack();
    }

    public ProfileDialog(JFrame owner) {

        super();


    }

    public static void main(String[] args) {
        KSConfig.init(".myKeys25");
        ProfileDialog notesJFrame = new ProfileDialog();
        notesJFrame.setVisible(true);
    }

    private JPanel getButtonPanel() {
        DialogAction dAction = new DialogAction();
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout(FlowLayout.LEADING));
        JButton jbAdd = new JButton(Messages.getString("template.create.button"));
        jbAdd.addActionListener(dAction);
        jbAdd.setActionCommand("add");
        JButton JbDelete = new JButton(Messages.getString("template.delete.button"));
        JbDelete.addActionListener(dAction);
        JbDelete.setActionCommand("delete");

        jp.add(jbAdd);
        jp.add(JbDelete);
        return jp;
    }


    private void init() {


    }


    class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("add")) {
                SwingUtilities.invokeLater(() -> {
                    CreateProfilDialog cs = new CreateProfilDialog(ProfileDialog.this, true);
                    //cs.setLocationRelativeTo(this);
                    cs.setVisible(true);
                    modele.setProfiles(((List<CertificateTemplate>) ProfileServices.getProfils(KSConfig.getProfilsPath())));
                    modele.fireTableDataChanged();
                });

            } else if (command.equals("delete")) {
                ProfileServices ps = new ProfileServices(KSConfig.getProfilsPath());
                int row = table.getSelectedRow();
                CertificateTemplate p = modele.getValueAt(row);
                try {
                    ps.delete(p);
                    modele.setProfiles(((List<CertificateTemplate>) ProfileServices.getProfils(KSConfig.getProfilsPath())));
                    modele.fireTableDataChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

