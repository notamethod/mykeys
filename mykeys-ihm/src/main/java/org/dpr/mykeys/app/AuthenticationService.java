package org.dpr.mykeys.app;

import org.bouncycastle.operator.OperatorCreationException;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.windows.CertificateHelperNew;
import org.dpr.mykeys.ihm.windows.certificate.AuthenticationException;
import org.dpr.mykeys.keystore.CertificateType;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class AuthenticationService {

    public void createUser(String id, char[] pwd) throws ServiceException {
        CertificateHelperNew ch = new CertificateHelperNew();
        KeyStoreHelper kh = new KeyStoreHelper();
        CertificateValue cer;
        KeyStoreValue ki;
        try {
            // check if not exists
            CertificateValue cerCheck = kh.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, null);
            if (cerCheck != null) {
                throw new ServiceException(Messages.getString(Messages.getString("certificate.error.create.exists"), id));
            }
            cer = ch.createCertificate(CertificateType.AUTH, id, pwd);
            cer.setPassword(pwd);
            ki = KSConfig.getInternalKeystores().getUserDB();
        } catch (GeneralSecurityException | OperatorCreationException | IOException e) {
            throw new ServiceException(Messages.getString("certificate.error.create") + id, e); //$NON-NLS-1$
        }

        kh.addCertToKeyStore(ki, cer);
        MkSession.password = cer.getPassword();
        MkSession.user = id;
    }

    public CertificateValue loadUser(String id, char[] pwd) {
        KeyStoreHelper ch = new KeyStoreHelper();
        CertificateValue cer = null;

        try {
            cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        cer.setPassword(pwd);
        return cer;
    }

    public CertificateValue AuthenticateUSer(String id, char[] pwd) throws AuthenticationException {
        KeyStoreHelper ch = new KeyStoreHelper();
        CertificateValue cer;

        try {
            cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
        } catch (Exception e) {
            throw new AuthenticationException("authentication failed", e);
        }

        cer.setPassword(pwd);
        return cer;
    }

    public List<CertificateValue> listUsers() throws ServiceException {
        KeyStoreHelper ch = new KeyStoreHelper();
        List<CertificateValue> cer;

        try {
            cer = ch.getCertificates(KSConfig.getInternalKeystores().getUserDB());
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return cer;
    }

    public void deleteUser(String id) throws ServiceException {
        KeyStoreHelper kh = new KeyStoreHelper();
        CertificateValue cer;
        char[] pwd = null;
        KeyStoreValue ki;
        try {
            cer = kh.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
            ki = KSConfig.getInternalKeystores().getUserDB();
            kh.removeCertificate(ki, cer);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
