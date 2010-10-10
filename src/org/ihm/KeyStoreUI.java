package org.ihm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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

import org.app.InternalKeystores;
import org.app.KSConfig;
import org.app.KeyStoreInfo;
import org.app.KeyStoreInfo.StoreFormat;
import org.app.KeyStoreInfo.StoreModel;
import org.ihm.menuaction.MenuAction;

/**
 * 
 */
public class KeyStoreUI extends JFrame implements WindowListener {

    JPanel p;// panel principal qui contient les images

    // répertoire des images
    String magRep = ".\\";

    String nomImage = "";

    // messages

    HashMap<String, KeyStoreInfo> ksList = new HashMap<String, KeyStoreInfo>();

    TreeKeyStore mainPanel;

    /**
     * Constructeur
     */
    public KeyStoreUI() {
	super("mykeys");
	 // Get toolkit
	    Toolkit toolkit = Toolkit.getDefaultToolkit();

	    // Get size
	    //Dimension dimension = toolkit.getScreenSize();

	 //this.setPreferredSize(dimension);
	    this.setPreferredSize(new Dimension(960,650));
	// this.setResizable(false);

	// setNbCols(4);
	init();

    }

    private void init() {
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
	List<Image> images = new ArrayList<Image>();
	//URL imgURL = null;
	images.add(Toolkit.getDefaultToolkit().getImage(TreeKeyStore.class.getResource("images/mkico24.png")));
	images.add(Toolkit.getDefaultToolkit().getImage(TreeKeyStore.class.getResource("images/mkico32.png")));
	images.add(Toolkit.getDefaultToolkit().getImage(TreeKeyStore.class.getResource("images/mkico48.png")));

	return images;
    }

    private void initLookAndFeel() {

	try {
	    UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
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
	Map<String, HashMap> typesKS = new HashMap<String, HashMap>();
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
	    Iterator<String> iter1 = ks1.iterator();
	    while (iter1.hasNext()) {
		String key1 = iter1.next();
		KSConfig.getUserCfg().clearProperty(key1);
		Set ks2 = typesKS.get(key1).keySet();
		Iterator<String> iter2 = ks2.iterator();
		while (iter2.hasNext()) {
		    String key2 = iter2.next();
		    KSConfig.getUserCfg().addProperty(key1, key2);

		}

	    }
	    KSConfig.save();
	}

    }

    private void buildMenu() {

	ResourceBundle messages = MyKeys.getMessage();
	// menu
	JMenuBar menuBar = new JMenuBar();
	JMenu menu1 = new JMenu(MyKeys.getMessage().getString("magasin"));
//	ImageIcon icon = createImageIcon("images/keystore.png");
//	menu1.setIcon(icon);
	JMenuItem itemStart1 = new JMenuItem(new MenuAction(this, MyKeys
		.getMessage().getString("magasin.new")));
	itemStart1.setActionCommand("newStore");
	menu1.add(itemStart1);
	JMenuItem itemLoad = new JMenuItem(new MenuAction(this, MyKeys
		.getMessage().getString("magasin.load")));
	itemLoad.setActionCommand("loadStore");
	menu1.add(itemLoad);

	JMenu menu2 = new JMenu(MyKeys.getMessage().getString("options"));

	JMenuItem itemStart = new JMenuItem(new MenuAction(this, messages
		.getString("options")));
	itemStart.setActionCommand("newGame");
	JMenu item = new JMenu(messages.getString("options"));
	menu2.add(itemStart);
	itemStart.add(item);

	// JMenu itemb = new JMenu(messages.getString("options.images"));
	// ButtonGroup group = new ButtonGroup();
	//
	// JRadioButtonMenuItem item1 = new JRadioButtonMenuItem(new MenuAction(
	// this, messages.getString("level.easy")));
	// JRadioButtonMenuItem item2 = new JRadioButtonMenuItem(new MenuAction(
	// this, messages.getString("level.medium")));
	// JRadioButtonMenuItem item3 = new JRadioButtonMenuItem(new MenuAction(
	// this, messages.getString("level.hard")));
	// item1.setActionCommand("lEasy");
	// item2.setActionCommand("lMedium");
	// item3.setActionCommand("lHard");
	// item1.setSelected(true);
	//
	// group.add(item1);
	// group.add(item2);
	// group.add(item3);
	//
	// JCheckBoxMenuItem itemCheck = new JCheckBoxMenuItem(new MenuAction(
	// this, messages.getString("options.fix")));
	// itemCheck.setActionCommand("actCheckBloc");
	// // JMenuItem item1 = new JMenuItem(new MenuAction(this,
	// // messages.getString("level.easy")));
	// // JMenuItem item2 = new JMenuItem(new MenuAction(this,
	// // messages.getString("level.medium")));
	// // JMenuItem item3 = new JMenuItem(new MenuAction(this,
	// // messages.getString("level.hard")));
	//
	// item.add(item1);
	// item.add(item2);
	// item.add(item3);
	menuBar.add(menu1);
	menuBar.add(menu2);
	// menuBar.add(menu2);
	// menu2.add(itemStart);
	// menu2.add(item);
	// menu2.add(itemb);
	this.setJMenuBar(menuBar);
	// String[] fichiersImage = getFichiersImage();
	// buildMenuImages(itemb, fichiersImage);
	// menu2.add(itemCheck);

    }

    private void buildComponents() {
	p = new JPanel();
	p.setLayout(new GridLayout(1,0));
	 this.setLayout(new GridLayout(1,0));
	// menu
	buildMenu();

	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	mainPanel = new TreeKeyStore(this.getPreferredSize());
	this.getContentPane().add(p);
	this.getContentPane().getMaximumSize();
	p.add(mainPanel); 
	System.out.println(this.getMaximizedBounds());


    }

    /**
     * Show dialog error box
     * 
     * @param c
     * @param string
     */
    public static void showError(Component c, String string) {
	JOptionPane.showMessageDialog(c, string, "Erreur",
		JOptionPane.ERROR_MESSAGE);

    }

    /**
     * Show dialog box with a password field
     * 
     * @param parent
     * @return
     */
    public static char[] showPasswordDialog(Component parent) {
	System.out.println("xxx");
	final JPasswordField jpf = new JPasswordField();
	JOptionPane jop = new JOptionPane(jpf, JOptionPane.QUESTION_MESSAGE,
		JOptionPane.OK_CANCEL_OPTION);
	//FIXME: label
	JDialog dialog = jop.createDialog(parent, "Mot de passe:");
	dialog.addComponentListener(new ComponentAdapter() {

	    public void componentShown(ComponentEvent e) {
		jpf.requestFocusInWindow();
	    }
	});
	dialog.setVisible(true);
	if (jop.getValue() == null) {
	    return null;
	}
	int result = (Integer) jop.getValue();
	dialog.dispose();
	char[] password = null;
	if (result == JOptionPane.OK_OPTION) {
	    password = jpf.getPassword();
	}else{
	    return null;
	}
	return password;
    }

    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		new KeyStoreUI();

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

    public void updateKeyStoreList() {
	InternalKeystores.getACPath();
	Iterator iter = KSConfig.getUserCfg().getKeys(KSConfig.STORE_PREFIX);
	while (iter.hasNext()) {
	    String key = (String) iter.next();
	    String[] typeTmp = key.split("\\.");
	    if (typeTmp != null && typeTmp.length > 2) {
		List list = KSConfig.getUserCfg().getList(key);
		for (Object o : list) {
		    String dirName = (String) o;
		    String fileName = dirName.substring(dirName
			    .lastIndexOf("\\") + 1, dirName.length());
		    KeyStoreInfo ki = new KeyStoreInfo(fileName, dirName,
			    StoreModel.fromValue(typeTmp[1]), StoreFormat.valueOf(typeTmp[2]));
//		    if (ki.getStoreModel().equals(StoreModel.CASTORE)){
//			InternalKeystores.setPath(dirName);
//		    }
		    ksList.put(dirName, ki);
		}

	    }

	}
	mainPanel.updateKSList(ksList);

    }

    /**
     * Show dialog error box
     * 
     * @param c
     * @param string
     */
    public static void showInfo(Component c, String string) {
	JOptionPane.showMessageDialog(c, string, "Information",
		JOptionPane.INFORMATION_MESSAGE);

    }

    protected static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = TreeKeyStore.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }
    protected static Image createImage(String path) {
	java.net.URL imgURL = TreeKeyStore.class.getResource(path);
	
	if (imgURL != null) {
	    return Toolkit.getDefaultToolkit().getImage(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }    

}
