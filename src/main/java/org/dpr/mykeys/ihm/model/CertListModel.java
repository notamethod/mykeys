/**
 * 
 */
package org.dpr.mykeys.ihm.model;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.dpr.mykeys.app.CertificateInfo;


/**
 * @author Buck
 *
 */
public class CertListModel implements ListModel {

	public CertListModel(List<CertificateInfo> data) {
		super();
		this.data = data;
	}

	private List<CertificateInfo> data;

	/**
	 * .
	 * 
	 * 
	 * @param arg0
	 * 
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void addListDataListener(ListDataListener arg0) {
		// super.a

	}

	/**
	 * .
	 * 
	 * 
	 * @param arg0
	 * @return
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return data.get(index);
	}

	/**
	 * .
	 * 
	 * 
	 * @return
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return data.size();
	}

	/**
	 * .
	 * 
	 * 
	 * @param arg0
	 * 
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Retourne le data.
	 * 
	 * @return List<CertificateInfo> - le data.
	 */
	public List<CertificateInfo> getData() {
		return data;
	}

	/**
	 * Affecte le data.
	 * 
	 * @param data
	 *            le data à affecter.
	 */
	public void setData(List<CertificateInfo> data) {
		this.data = data;

	}

}
