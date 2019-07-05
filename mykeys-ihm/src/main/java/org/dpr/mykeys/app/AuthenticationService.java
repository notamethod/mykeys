package org.dpr.mykeys.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.ihm.windows.certificate.AuthenticationException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuthenticationService {

    private static final Log log = LogFactory
            .getLog(AuthenticationService.class);
    public void createUser(String id, char[] pwd) throws ServiceException {
        CertificateHelper ch = new CertificateHelper();
        KeyStoreHelper kh = new KeyStoreHelper();
        CertificateValue cer;
        KeyStoreValue ki;
        try {
            // check if not exists
            CertificateValue cerCheck = kh.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, null);
            if (cerCheck != null) {
                throw new ServiceException(Messages.getString(Messages.getString("certificate.error.create.exists"), id));
            }
            cer = ch.createCertificate(CertificateType.AUTH_MK, id, pwd);
            cer.setPassword(pwd);
            ki = KSConfig.getInternalKeystores().getUserDB();
        } catch (GeneralSecurityException | IOException e) {
            throw new ServiceException(Messages.getString("certificate.error.create") + id, e); //$NON-NLS-1$
        }

        kh.addCertToKeyStore(ki, cer, null, null);
        MkSession.updateCreds(id, cer.getPassword());

    }

    public CertificateValue loadUser(String id, char[] pwd) {
        KeyStoreHelper ch = new KeyStoreHelper();
        CertificateValue cer = null;

        try {
            cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
        } catch (Exception e) {
            log.error(e);
        }
        if (cer != null)
            cer.setPassword(pwd);
        return cer;
    }

    public CertificateValue authenticateUSer(String id, char[] pwd) throws AuthenticationException {
        KeyStoreHelper ch = new KeyStoreHelper();
        CertificateValue cer;

        try {
            cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
        } catch (Exception e) {
            throw new AuthenticationException("authentication failed", e);
        }
        if (cer == null)
            throw new AuthenticationException("authentication failed, can't load certificate");
        cer.setPassword(pwd);
        return cer;
    }

    public List<CertificateValue> listUsers() throws ServiceException {
        KeyStoreHelper ch = new KeyStoreHelper();
        List<CertificateValue> cer;

        try {
            cer = ch.getCertificatesForUser(KSConfig.getInternalKeystores().getUserDB());
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
            List<CertificateValue> list = Collections.singletonList(cer);
            kh.removeCertificates(ki, list);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
