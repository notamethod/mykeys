package org.dpr.mykeys.ihm.windows;

import static org.dpr.swingutils.ImageUtils.getImage;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.actions.MenuAction;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.utils.DialogUtil;

/**
 * 
 */
public class MykeysFrame extends JFrame implements WindowListener {

	private static final Log log = LogFactory.getLog(MykeysFrame.class);
	private JPanel p;// panel principal qui contient les images

	// répertoire des images
	String magRep = ".\\";

	String nomImage = "";

	// messages

	private HashMap<String, KeyStoreValue> ksList = new HashMap<>();

	private TreeKeyStorePanel mainPanel;

	/**
	 * Constructeur
	 * @throws KeyStoreException 
	 */
	public MykeysFrame() throws KeyStoreException {
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

	private void init() throws KeyStoreException {
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

	private static List<? extends Image> createIConAppli() {
		List<Image> images = new ArrayList<>();
		// URL imgURL = null;
		images.add(getImage("/images/mkico24.png"));
		images.add(getImage("/images/mkico32.png"));
		images.add(getImage("/images/mkico48.png"));

		return images;
	}

	private void initLookAndFeel() {

		setDefaultLookAndFeelDecorated(true);
		try {

            UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
			 UIManager.put("ToolTip.foreground", new ColorUIResource(Color.ORANGE));

            return;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// handle exception
		}

	}

	public static void removeKeyStore(String path) {

		Iterator iter = KSConfig.getUserCfg().getKeys(KSConfig.STORE_PREFIX);
		boolean update = false;
		Map<String, HashMap> typesKS = new HashMap<>();
		while (iter.hasNext()) {
			String key = (String) iter.next();

			List list = KSConfig.getUserCfg().getList(key);
			typesKS.put(key, new HashMap<String, String>());
			for (Object o : list) {
				String dirName = (String) o;

				if (path.equals(dirName)) {
					update = true;

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

			}
			KSConfig.save();
		}

	}

	private void buildMenu() {

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
		this.setJMenuBar(menuBar);
		// JToolBar toolBar = new JToolBar();
		// this.getContentPane().add(toolBar, BorderLayout.NORTH);

	}

	private void buildComponents() {
		p = new JPanel();
		p.setLayout(new GridLayout(1, 0));
		this.setLayout(new GridLayout(1, 0));
		// menu
		buildMenu();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel = new TreeKeyStorePanel(this.getPreferredSize());
		this.getContentPane().add(p);
		this.getContentPane().getMaximumSize();
		p.add(mainPanel);
		log.trace(this.getMaximizedBounds());

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new MykeysFrame();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

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
					// if (ki.getStoreModel().equals(StoreModel.CASTORE)){
					// InternalKeystores.setPath(dirName);
					// }
					ksList.put(dirName, ki);
				}

			}

		}
		mainPanel.updateKSList(ksList);

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
	/**
     * not sure to continue on this...
	 */
    private void checkUpgrade() {
        String path = System.getProperty("user.home") + File.separator + KSConfig.MK1PATH;
        boolean migrate = false;
        if (new File(path).exists()) {
			DialogUtil.showInfo(this, Messages.getString("update.info", path));
        }

	}
}
