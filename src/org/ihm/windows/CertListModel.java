/**
 * 
 */
package org.ihm.windows;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.app.CertificateInfo;

/**
 *<pre>
 *<b></b>.
 * 
 *<b>Description :</b>
 *    
 * 
 *</pre>
 * @author C. Roger<BR>
 *  <BR>
 * Créé le 16 sept. 2010 <BR>
 *  <BR>
 *  <BR>
 * <i>Copyright : Tessi Informatique </i><BR>
 */
public class CertListModel implements ListModel{

    public CertListModel(List<CertificateInfo> data) {
	super();
	this.data = data;
    }

    private List<CertificateInfo> data;
    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @param arg0
     * 
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void addListDataListener(ListDataListener arg0) {
	//super.a
	
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
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
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
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
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
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
     * @return List<CertificateInfo> - le data.
     */
    public List<CertificateInfo> getData() {
        return data;
    }

    /**
     * Affecte le data.
     * @param data le data à affecter.
     */
    public void setData(List<CertificateInfo> data) {
        this.data = data;
        
    }

}
