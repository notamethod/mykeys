package org.dpr.mykeys.ihm.windows;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.AppManager;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.configuration.InternalKeystores;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.actions.MenuAction;
import org.dpr.mykeys.ihm.components.MainPKIPanel;
import org.dpr.mykeys.ihm.components.MainPanel;
import org.dpr.mykeys.ihm.listeners.HelpMouseListener;
import org.dpr.mykeys.utils.ComponentUtils;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyStoreException;
import java.util.*;
import java.util.List;

import static org.dpr.swingtools.ImageUtils.getImage;

/**
 *
 */
public class MykeysFrame extends JFrame implements WindowListener{

    private static final Log log = LogFactory.getLog(MykeysFrame.class);
    // répertoire des images
    String magRep = ".\\";
    String nomImage = "";
    // private JPanel p;// panel principal qui contient les images

    JToolBar toolbar;

    //keystores
    private MainPanel mainStandardPanel;

    private JPanel pnlCards;

    /**
     * Constructeur
     *
     * @throws KeyStoreException
     */
    public MykeysFrame() throws KeyStoreException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        super("mykeys");
        // Get toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        this.setPreferredSize(new Dimension(740, 820));
        init();
        checkUpgrade();

    }

    private static List<? extends Image> createIConAppli() {
        List<Image> images = new ArrayList<>();
        // URL imgURL = null;
        images.add(getImage("/images/mkico24.png"));
        images.add(getImage("/images/mkico32.png"));
        images.add(getImage("/images/mkico48.png"));

        return images;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MykeysFrame();
            } catch (Exception e) {
                log.error("init error", e);
            }

        });

    }

    private void init() throws KeyStoreException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        initLookAndFeel();
        this.addWindowListener(this);
        buildComponents();
        this.setSize(this.getPreferredSize());
        this.setIconImages(createIConAppli());
        this.mainStandardPanel.updateKSList(AppManager.getInstance().updateKeyStoreList(), null);
        // this.setBackground(new Color(125, 0, 0));
        this.pack();
        this.setVisible(true);

    }

    private void initLookAndFeel() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        setFont(new FontUIResource(new Font("Segoe UI", Font.PLAIN, 12)));
        setDefaultLookAndFeelDecorated(true);

        try {

            UIManager.setLookAndFeel(ComponentUtils.skin);
            UIManager.put("ToolTip.foreground", new ColorUIResource(Color.ORANGE));
            UIManager
                    .put("defaultFont", new Font("Arial", Font.BOLD, 14));
            return;
        } catch (Exception e1) {
            log.error("error setting look and feel", e1);
        }

        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

    }

    private String buildMenu() {

        // menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu(Messages.getString("magasin"));
        // ImageIcon icon = createImageIcon("/images/images/keystore.png");
        // menu1.setIcon(icon);
        JMenuItem itemStart1 = new JMenuItem(new MenuAction(this, Messages.getString("magasin.new")));
        itemStart1.setActionCommand("newStore");
        itemStart1.addMouseListener(new HelpMouseListener("new_store"));

        menu1.add(itemStart1);
        JMenuItem itemLoad = new JMenuItem(new MenuAction(this, Messages.getString("magasin.load")));
        itemLoad.addMouseListener(new HelpMouseListener("import_store"));
        itemLoad.setActionCommand("loadStore");
        menu1.add(itemLoad);

        JMenu menuOptions = new JMenu(Messages.getString("options"));

        JMenuItem itemLog = new JMenuItem(new MenuAction(this, Messages.getString("options")));
        itemLog.setActionCommand("options");
        JMenuItem menuCrl = new JMenuItem(new MenuAction(this, Messages.getString("crl.add")));
        menuCrl.setActionCommand("addCrl");
        // menuOptions.add(menuCrl);

        JMenuItem menu3 = new JMenuItem(new MenuAction(this, Messages.getString("certificateTemplate.name")));
        menu3.setActionCommand("profil");
        JMenuItem menu4 = new JMenuItem(new MenuAction(this, Messages.getString("users.title")));
        menu4.setActionCommand("users");
        JMenuItem menu5 = new JMenuItem(new MenuAction(this, Messages.getString("extraction.menu.title")));
        menu5.setActionCommand("extractor");
        // JMenuItem menu4 = new JMenuItem(new MenuAction(this, MyKeys
        // .getMessage().getString("file.sign")));
        // menu4.setActionCommand("signFile");
        menuBar.add(menu1);
        menuBar.add(menuOptions);
        // menuBar.add(menu3);
        // menuBar.add(menu4);
        // Create a toolbar and give it an etched border.
        menuOptions.add(menu3);
        menuOptions.add(menu4);
        menuOptions.add(menu5);
        JToggleButton menuStd = new JToggleButton(Messages.getString("mode.standard"));
        menuStd.addActionListener(e -> switchCard("STD"));
        JToggleButton menuPKI = new JToggleButton(Messages.getString("mode.pki"));
        menuPKI.addActionListener(e -> switchCard("PKI"));
        //TOOD: save preferences
        String viewMode = KSConfig.getUserCfg().getString("view.mode");
        if (viewMode != null) {

            if (viewMode.equals("STD")) {
                menuStd.setSelected(true);
            } else {
                menuPKI.setSelected(true);
            }
        } else
            menuStd.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(menuStd);
        group.add(menuPKI);
        toolbar = new JToolBar();
        toolbar.add(menuStd);
        toolbar.add(menuPKI);
        this.setJMenuBar(menuBar);


        return viewMode;
    }


    private void switchCard(String cardName) {

        //save preference
        KSConfig.getUserCfg().setProperty("view.mode", cardName);

        CardLayout cl = (CardLayout) (pnlCards.getLayout());
        cl.show(pnlCards, cardName);
    }

    private void buildComponents() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1, 0));
        //  this.setLayout(new GridLayout(1, 0));
        // menu
        String viewMode = buildMenu();
        CardLayout cards = new CardLayout();
        pnlCards = new JPanel(cards);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainStandardPanel = new MainPanel(this.getPreferredSize());
        //pki
        MainPKIPanel mainPKIPanel = new MainPKIPanel(this.getPreferredSize());
        pnlCards.add(mainStandardPanel, "STD");
        pnlCards.add(mainPKIPanel, "PKI");

        this.getContentPane().add(toolbar, BorderLayout.NORTH);


        this.getContentPane().add(p, BorderLayout.CENTER);
        //this.getContentPane().add(p);
        // this.getContentPane().getMaximumSize();
        p.add(pnlCards);
        if (viewMode != null) {
            switchCard(viewMode);
        }
        log.trace(this.getMaximizedBounds());

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        KSConfig.save();

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }



    private void checkUpgrade() {
        String path = System.getProperty("user.home") + File.separator + KSConfig.MK1PATH;
        boolean migrate = false;
        //TODO    KSConfig.getUserCfg().addProperty("store." + StoreModel.CERTSTORE + "." + format.toString(), dir);
        if (new File(path).exists()) {

            copyOldKeystores(true, path, InternalKeystores.MK1_STORE_CERT);
            copyOldKeystores(false, path, InternalKeystores.MK1_STORE_AC);
        }
    }

    private void copyOldKeystores(boolean ask, String path, String mk1StoreCert) {
        File certFile = new File(path, mk1StoreCert);
        File touFile = new File(path, "passed.mk");
        if (!touFile.exists()) {

        }

        if (certFile.exists() && !touFile.exists()) {
            if (ask)
                if (!DialogUtil.askConfirmDialog(null, Messages.getString("migrate.keystore", path))) {
                    try {
                        new FileOutputStream(touFile).close();
                    } catch (IOException e) {
                        log.error("keystore copy error", e);
                    }
                    return;
                }
            File fileDest = new File(KSConfig.getDefaultCertificatePath(), "previous_" + mk1StoreCert);
            try {
                Files.copy(certFile.toPath(), fileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.delete(certFile.toPath());
            } catch (IOException e) {
                log.error("keystore copy error", e);
            }

            KSConfig.getUserCfg().addProperty(
                    "store." + StoreModel.CERTSTORE + "."
                            + StoreFormat.JKS.toString(), fileDest.getAbsolutePath());


            byte[] encoded = Base64.getEncoder().encode(fileDest.getAbsolutePath().getBytes());
            String hexString = Hex.encodeHexString(encoded);

            KSConfig.getUserCfg().addProperty(
                    "intpwd." + hexString, "true");
            try {
                this.mainStandardPanel.updateKSList(AppManager.getInstance().updateKeyStoreList(), null);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

        }
    }
    private void setFont(FontUIResource myFont) {
        UIManager.put("CheckBoxMenuItem.acceleratorFont", myFont);
        UIManager.put("Button.font", myFont);
        UIManager.put("ToggleButton.font", myFont);
        UIManager.put("RadioButton.font", myFont);
        UIManager.put("CheckBox.font", myFont);
        UIManager.put("ColorChooser.font", myFont);
        UIManager.put("ComboBox.font", myFont);
        UIManager.put("Label.font", myFont);
        UIManager.put("List.font", myFont);
        UIManager.put("MenuBar.font", myFont);
        UIManager.put("Menu.acceleratorFont", myFont);
        UIManager.put("RadioButtonMenuItem.acceleratorFont", myFont);
        UIManager.put("MenuItem.acceleratorFont", myFont);
        UIManager.put("MenuItem.font", myFont);
        UIManager.put("RadioButtonMenuItem.font", myFont);
        UIManager.put("CheckBoxMenuItem.font", myFont);
        UIManager.put("OptionPane.buttonFont", myFont);
        UIManager.put("OptionPane.messageFont", myFont);
        UIManager.put("Menu.font", myFont);
        UIManager.put("PopupMenu.font", myFont);
        UIManager.put("OptionPane.font", myFont);
        UIManager.put("Panel.font", myFont);
        UIManager.put("ProgressBar.font", myFont);
        UIManager.put("ScrollPane.font", myFont);
        UIManager.put("Viewport.font", myFont);
        UIManager.put("TabbedPane.font", myFont);
        UIManager.put("Slider.font", myFont);
        UIManager.put("Table.font", myFont);
        UIManager.put("TableHeader.font", myFont);
        UIManager.put("TextField.font", myFont);
        UIManager.put("Spinner.font", myFont);
        UIManager.put("PasswordField.font", myFont);
        UIManager.put("TextArea.font", myFont);
        UIManager.put("TextPane.font", myFont);
        UIManager.put("EditorPane.font", myFont);
        UIManager.put("TabbedPane.smallFont", myFont);
        UIManager.put("TitledBorder.font", myFont);
        UIManager.put("ToolBar.font", myFont);
        UIManager.put("ToolTip.font", myFont);
        UIManager.put("Tree.font", myFont);
        UIManager.put("FormattedTextField.font", myFont);
        UIManager.put("IconButton.font", myFont);
        UIManager.put("InternalFrame.optionDialogTitleFont", myFont);
        UIManager.put("InternalFrame.paletteTitleFont", myFont);
        UIManager.put("InternalFrame.titleFont", myFont);
    }


}
