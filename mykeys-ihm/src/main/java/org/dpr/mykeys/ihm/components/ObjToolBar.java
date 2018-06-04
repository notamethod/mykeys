package org.dpr.mykeys.ihm.components;

import org.dpr.mykeys.app.NodeInfo;

import javax.swing.JToolBar;

public abstract class ObjToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6922678740807004941L;
	private String title;

    protected ObjToolBar(String name) {
		super(name);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public abstract void enableActions();

    public abstract void disableActions(NodeInfo info);

	public abstract void enableListeners();

	public abstract void removeListeners();

    public abstract void enableGenericActions(NodeInfo info, boolean b);

    public abstract void enableElementActions(boolean b);

	public  void setVisible() {
		setVisible(!isVisible());
		
	}

}
