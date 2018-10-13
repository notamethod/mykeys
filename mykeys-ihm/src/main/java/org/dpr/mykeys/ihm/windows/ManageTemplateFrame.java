package org.dpr.mykeys.ihm.windows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.components.CertificateListPanel;
import org.dpr.mykeys.ihm.model.ProfileModel;
import org.dpr.mykeys.template.CreateTemplateDialog;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

public class ManageTemplateFrame extends JFrame {
    public static final Log log = LogFactory.getLog(ManageTemplateFrame.class);

    private JTable table;
    private ProfileModel modele;

    public ManageTemplateFrame() {
        setTitle(Messages.getString("template.title"));
        setPreferredSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modele = new ProfileModel((List<CertificateTemplate>) ProfileServices.getProfils(KSConfig.getProfilsPath()));

        table = new JTable(modele);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    final CertificateTemplate p = modele.getValueAt(row);

                    SwingUtilities.invokeLater(() -> {
                        CreateTemplateDialog cs = new CreateTemplateDialog(ManageTemplateFrame.this, true, p);
                        //cs.setLocationRelativeTo(this);
                        cs.setVisible(true);
                        modele.setProfiles(((List<CertificateTemplate>) ProfileServices.getProfils(KSConfig.getProfilsPath())));
                        modele.fireTableDataChanged();
                    });
                }
            }
        });

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

    public ManageTemplateFrame(JFrame owner) {

        super();


    }

    public static void main(String[] args) {
        KSConfig.init(KSConfig.MKPATH);
        ManageTemplateFrame notesJFrame = new ManageTemplateFrame();
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
                    CreateTemplateDialog cs = new CreateTemplateDialog(ManageTemplateFrame.this, true);
                    //cs.setLocationRelativeTo(this);
                    cs.setVisible(true);
                    modele.setProfiles(((List<CertificateTemplate>) ProfileServices.getProfils(KSConfig.getProfilsPath())));
                    modele.fireTableDataChanged();
                });

            } else if (command.equals("delete")) {
                ProfileServices ps = new ProfileServices(KSConfig.getProfilsPath());
                int row = table.getSelectedRow();
                if (row != -1) {
                    CertificateTemplate p = modele.getValueAt(row);

                    if (DialogUtil.askConfirmDialog(null, Messages.getString("template.delete.ask", p.getName()))) {
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
    }
}

