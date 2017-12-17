package org.dpr.mykeys.app;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.bouncycastle.operator.OperatorCreationException;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.windows.CertificateHelperNew;
import org.dpr.mykeys.keystore.CertificateType;

public class AuthenticationService {

	public void createUser(String id, char[] pwd) throws ServiceException {
		CertificateHelperNew ch = new CertificateHelperNew();
		CertificateValue cer = null;
		try {
			cer = ch.createCertificate(CertificateType.AUTH, id, pwd);
		} catch (InvalidKeyException | OperatorCreationException | CertificateException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException e) {
			throw new ServiceException(Messages.getString("certificate.error.create") + id, e); //$NON-NLS-1$
		}
		cer.setPassword(pwd);
		KeyStoreInfo ki = KSConfig.getInternalKeystores().getUserDB();
		KeyStoreHelper kh = new KeyStoreHelper();

		kh.addCertToKeyStore(ki, cer, null);
	}

	public CertificateValue loadUser(String id, char[] pwd) throws ServiceException {
		KeyStoreHelper ch = new KeyStoreHelper();
		CertificateValue cer = null;

		try {
			cer = ch.findACByAlias(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cer.setPassword(pwd);
		return cer;

	}

}
