/**
 * 
 */
package org.dpr.mykeys.ihm.windows;

import static org.dpr.swingutils.ImageUtils.createImageIcon;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.profile.Profil;


/**
 * @author Buck
 *
 */
public class ListCertRenderer extends DefaultListCellRenderer {

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @return
	 * 
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		Component retValue = super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		// TODO Auto-generated method stub

		if (value instanceof CertificateInfo) {
			CertificateInfo cert = ((CertificateInfo) value);
			ImageIcon icon = null;
			if (cert.isContainsPrivateKey()) {
				icon = createImageIcon("/images/certificatekey.png");
			} else {
				icon = createImageIcon("/images/certificate2.png");
			}
			if (icon != null) {

				setIcon(icon);

			}
			setText(cert.getName());
			StringBuilder sb = new StringBuilder();
			sb.append("<html>Certificat ").append(cert.getName());
			if (cert.getAlias()!=null) 
			    sb.append(" (").append(cert.getAlias()).append(")");
			 sb.append("<br>Numéro de série ").append(cert.getCertificate().getSerialNumber());
			 sb.append("<br>Emetteur ").append(cert.getCertificate().getIssuerDN());
			if (cert.isContainsPrivateKey()){
			    sb.append("<br>Clé privée présente");
			}
			sb.append("</html>");
			this.setToolTipText(sb.toString());

		}else if (value instanceof Profil) {
			Profil prof = ((Profil) value);
			setIcon(createImageIcon("/images/profile1.png"));
			setText(prof.getName());
		}
		// return super.getListCellRendererComponent(list, value, index,
		// isSelected, cellHasFocus);
		return retValue;
	}



}
