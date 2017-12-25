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
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;

public class AuthenticationService {

    public void createUser(String id, char[] pwd) throws ServiceException {
        CertificateHelperNew ch = new CertificateHelperNew();
        CertificateValue cer = null;
        KeyStoreValue ki = null;
        try {
            cer = ch.createCertificate(CertificateType.AUTH, id, pwd);
            cer.setPassword(pwd);
            ki = KSConfig.getInternalKeystores().getUserDB();
        } catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException
                | NoSuchProviderException | SignatureException | OperatorCreationException | KeyStoreException | IOException e) {
            throw new ServiceException(Messages.getString("certificate.error.create") + id, e); //$NON-NLS-1$
        }

        KeyStoreHelper kh = new KeyStoreHelper();

        kh.addCertToKeyStore(ki, cer, null);
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
        CertificateValue cer = null;

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
        List<CertificateValue> cer = null;

        try {
            cer = ch.getCertificates(KSConfig.getInternalKeystores().getUserDB());
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return cer;
    }

}
