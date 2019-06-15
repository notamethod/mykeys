package org.dpr.mykeys.ihm.windows;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.actions.MenuAction;
import org.dpr.mykeys.ihm.components.MainPKIPanel;
import org.dpr.mykeys.ihm.components.MainPanel;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKeyStorePanel;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
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
public class MykeysFrame extends JFrame implements WindowListener {

    private static final Log log = LogFactory.getLog(MykeysFrame.class);
    // répertoire des images
    String magRep = ".\\";
    String nomImage = "";
    // private JPanel p;// panel principal qui contient les images

    JToolBar toolbar;
    // messages
    private HashMap<String, KeyStoreValue> ksList = new HashMap<>();
    //keystores
    private MainPanel mainStandardPanel;
    //pki
    private MainPKIPanel mainPKIPanel;

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

        // Get size
        // Dimension dimension = toolkit.getScreenSize();

        // this.setPreferredSize(dimension);
        this.setPreferredSize(new Dimension(745, 850));
        // this.setResizable(false);

        // setNbCols(4);
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

    public static void removeKeyStore(String path) {

        Iterator iter = KSConfig.getUserCfg().getKeys(KSConfig.STORE_PREFIX);
        boolean update = false;
        List<String> dirNameList = new ArrayList<>();
        Map<String, HashMap> typesKS = new HashMap<>();
        while (iter.hasNext()) {
            String key = (String) iter.next();

            List list = KSConfig.getUserCfg().getList(key);
            typesKS.put(key, new HashMap<String, String>());
            for (Object o : list) {
                String dirName = (String) o;

                if (path.equals(dirName)) {
                    update = true;
                    dirNameList.add(dirName);
                } else {
                    typesKS.get(key).put(dirName, dirName);
                }
            }
        }
        if (update) {
            Set ks1 = typesKS.keySet();
            for (String key1 : (Iterable<String>) ks1) {
                KSConfig.getUserCfg().clearProperty(key1);
                Set ks2 = typesKS.get(key1).keySet();
                for (String key2 : (Iterable<String>) ks2) {
                    KSConfig.getUserCfg().addProperty(key1, key2);

                }
                //remove other properties
                for (String dname : dirNameList) {
                    byte[] encoded = Base64.getEncoder().encode(dname.getBytes());
                    String hexString = Hex.encodeHexString(encoded);
                    KSConfig.getUserCfg().clearProperty("intpwd." + hexString);
                }
            }
            KSConfig.save();
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MykeysFrame();
                } catch (Exception e) {
                    log.error("init error", e);
                }

            }
        });

    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TreeKeyStorePanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void init() throws KeyStoreException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        initLookAndFeel();
        this.addWindowListener(this);
        buildComponents();
        this.setSize(this.getPreferredSize());
        this.setIconImages(createIConAppli());
        updateKeyStoreList();
        // this.setBackground(new Color(125, 0, 0));
        this.pack();
        this.setVisible(true);

    }

    private void initLookAndFeel() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        setDefaultLookAndFeelDecorated(true);
        try {

            UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
            UIManager.put("ToolTip.foreground", new ColorUIResource(Color.ORANGE));

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
        menu1.add(itemStart1);
        JMenuItem itemLoad = new JMenuItem(new MenuAction(this, Messages.getString("magasin.load")));
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
        mainPKIPanel = new MainPKIPanel(this.getPreferredSize());
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

    public void updateKeyStoreList() throws KeyStoreException {
        KSConfig.getInternalKeystores().getACPath();
        Iterator iter = KSConfig.getUserCfg().getKeys(KSConfig.STORE_PREFIX);
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String[] typeTmp = key.split("\\.");
            if (typeTmp != null && typeTmp.length > 2) {
                List list = KSConfig.getUserCfg().getList(key);
                for (Object o : list) {
                    String dirName = (String) o;
                    String fileName = dirName.substring(dirName.lastIndexOf("\\") + 1, dirName.length());
                    KeyStoreValue ki = new KeyStoreValue(fileName, dirName, StoreModel.fromValue(typeTmp[1]),
                            StoreFormat.valueOf(typeTmp[2]));
                    byte[] encoded = Base64.getEncoder().encode(dirName.getBytes());
                    String hexString = Hex.encodeHexString(encoded);

                    if (KSConfig.getUserCfg().getBoolean("intpwd." + hexString, false)) {
                        ki.setStoreType(StoreLocationType.INTERNAL);
                        ki.setOpen(true);
                    }

                    // if (ki.getStoreModel().equals(StoreModel.CASTORE)){
                    // InternalKeystores.setPath(dirName);
                    // }
                    ksList.put(dirName, ki);
                }
            }
        }
        mainStandardPanel.updateKSList(ksList);
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
            File fileDest = new File(KSConfig.getDataDir(), "previous_" + mk1StoreCert);
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
                updateKeyStoreList();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

        }
    }
}
