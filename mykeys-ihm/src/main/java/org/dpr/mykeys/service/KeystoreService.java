package org.dpr.mykeys.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.repository.MkKeystore;
import org.dpr.mykeys.app.keystore.repository.RepositoryException;
import org.dpr.mykeys.utils.DialogUtil;

import java.util.List;

public class KeystoreService {

    private static final Log log = LogFactory.getLog(KeystoreService.class);
    public void removeCertificates(KeyStoreValue ksValue, List<Certificate> certificatesInfo) throws ServiceException {
        MkKeystore mks = MkKeystore.getInstance(ksValue.getStoreFormat());

        try {
            mks.removeCertificates(ksValue, certificatesInfo);
        } catch (RepositoryException e) {
           throw new ServiceException("deletion failed", e);
        }

    }

    public void addCertificates(KeyStoreValue ksValue, List<Certificate> certificatesInfo) throws ServiceException {

        MkKeystore mks = MkKeystore.getInstance(ksValue.getStoreFormat());
        try {
            mks.addCertificates(ksValue, certificatesInfo);
        } catch (RepositoryException e) {
            throw new ServiceException("Can't add certificates", e);
        }

    }

    public boolean openStore(KeyStoreValue ksInfo) {

        boolean resetPassword = false;
        if (ksInfo.isProtected()) {

            if (ksInfo.getPassword() == null) {
                char[] password = DialogUtil.showPasswordDialog(null);

                if (password == null || password.length == 0) {
                    return false;
                }
                resetPassword = true;
                ksInfo.setPassword(password);
            }
        }

        try {
            KeyStoreHelper kserv = new KeyStoreHelper( ksInfo);
            kserv.open();
            ksInfo.setOpen(true);
        } catch (Exception e1) {
            DialogUtil.showError(null, e1.getMessage());
            log.error("error opening keystore", e1);
            //reset password to try next time
            if (resetPassword)
                ((KeyStoreValue) ksInfo).setPassword(null);
            return false;
        }

        return true;

    }
}
