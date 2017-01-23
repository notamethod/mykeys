package org.dpr.mykeys.ihm.components;

import static org.dpr.swingutils.ImageUtils.createImageIcon;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.dpr.mykeys.ihm.actions.TypeAction;

public abstract class ObjToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6922678740807004941L;
	private String title;

	public ObjToolBar(String name) {
		super(name);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	KeysAction actions;

	public abstract void enableActions();

	public abstract void disableActions();

	public abstract void enableListeners();

	public abstract void removeListeners();

	public  void setVisible() {
		setVisible(!isVisible());
		
	}

}
