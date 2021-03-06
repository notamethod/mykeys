package org.dpr.mykeys.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.CertificateType;
import org.dpr.mykeys.app.keystore.repository.RepositoryException;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.configuration.MkSession;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.certificate.CertificateManager;
import org.dpr.mykeys.service.KeystoreService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class AuthenticationService {

    private static final Log log = LogFactory
            .getLog(AuthenticationService.class);
    public void createUser(String id, char[] pwd) throws ServiceException {
        CertificateManager ch = new CertificateManager();
        KeyStoreHelper kh = new KeyStoreHelper();
        Certificate cer;
        KeyStoreValue ki;
        try {
            // check if not exists
            Certificate cerCheck = kh.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, null);
            if (cerCheck != null) {
                throw new ServiceException(Messages.getString(Messages.getString("certificate.error.create.exists"), id));
            }
            cer = ch.createLoginCertificate(id, pwd);
            cer.setPassword(pwd);
            ki = KSConfig.getInternalKeystores().getUserDB();
        } catch (GeneralSecurityException | IOException | RepositoryException e) {
            throw new ServiceException(Messages.getString("certificate.error.create") + id, e); //$NON-NLS-1$
        }

        kh.addCertToKeyStore(ki, cer, null, null);
        MkSession.updateCreds(id, cer.getPassword());

    }

    public Certificate loadUser(String id, char[] pwd) {
        KeyStoreHelper ch = new KeyStoreHelper();
        Certificate cer = null;

        try {
            cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
        } catch (Exception e) {
            log.error(e);
        }
        if (cer != null)
            cer.setPassword(pwd);
        return cer;
    }

    public Certificate authenticateUSer(String id, char[] pwd) throws AuthenticationException {
        KeyStoreHelper ch = new KeyStoreHelper();
        Certificate cer;

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

    public List<Certificate> listUsers() throws ServiceException {
        KeyStoreHelper ch = new KeyStoreHelper();
        List<Certificate> cer;

        try {
            cer = ch.getCertificatesForUser(KSConfig.getInternalKeystores().getUserDB());
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return cer;
    }

    public void deleteUser(String id) throws ServiceException {
        KeyStoreHelper kh = new KeyStoreHelper();
        KeystoreService ks = new KeystoreService();
        Certificate cer;
        char[] pwd = null;
        KeyStoreValue ki;
        try {
            cer = kh.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
            ki = KSConfig.getInternalKeystores().getUserDB();
            List<Certificate> list = Collections.singletonList(cer);
            ks.removeCertificates(ki, list);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
