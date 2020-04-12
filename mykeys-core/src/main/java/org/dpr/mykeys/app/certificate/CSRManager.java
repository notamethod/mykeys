package org.dpr.mykeys.app.certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.repository.MkKeystore;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

public class CSRManager {


    private static final int CSR_VALIDITY = 365;
    private static final String CSR_SIGN_ALGORITHM = "SHA256withRSA";
    private static final Log log = LogFactory.getLog(CSRManager.class);



    /**
     * Generate a X509 certificate from CSR file
     *
     * @param fic
     * @return
     * @throws ServiceException
     * @throws IOException
     */
    public CertificateValue generateCertificate(InputStream fic, CertificateValue issuer) throws ServiceException, IOException {

        X509Certificate[] certificates = null;
        CertificateValue cert = null;
        try {

            Object pemcsr;
            BufferedReader buf = new BufferedReader(new InputStreamReader(fic));

            //xCert = builder.generateFromCSR(buf, issuer).get();
            PemReader reader = new PemReader(buf);
            PKCS10CertificationRequest csr = convertPemToPKCS10CertificationRequest(reader);
            if (csr == null)
                return cert;

            X500Name x500Name = csr.getSubject();
            log.info("x500Name is: " + x500Name + "\n");
            log.info("Signature algorithm is: " + csr.getSignatureAlgorithm() + "\n");

            byte[] certencoded = sign(csr, issuer.getPrivateKey(), issuer.getCertificate());
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            InputStream in = new ByteArrayInputStream(certencoded);
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(in);
            certificates = new X509Certificate[]{certificate};
            if (certificate != null) {
                log.info("certificate " + certificate.getSubjectDN().getName() + " created !");
            }
            cert = new CertificateValue(certificates);
        } catch (GeneralSecurityException | OperatorCreationException e) {
            throw new ServiceException("error on certificate generation fro csr file " + fic, e);
        }


        return cert;

    }

    private PKCS10CertificationRequest convertPemToPKCS10CertificationRequest(Reader pemReader) {

        PKCS10CertificationRequest csr = null;
        ByteArrayInputStream pemStream = null;

        PEMParser pemParser = new PEMParser(pemReader);

        try {
            Object parsedObj = pemParser.readObject();

            if (parsedObj instanceof PKCS10CertificationRequest) {
                csr = (PKCS10CertificationRequest) parsedObj;

            }
        } catch (IOException ex) {
            log.error("IOException, convertPemToPublicKey", ex);
        }
        return csr;
    }

    private byte[] sign(PKCS10CertificationRequest inputCSR, PrivateKey caPrivate, X509Certificate caCert)
            throws NoSuchAlgorithmException,
            IOException, OperatorCreationException {

        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(CSR_SIGN_ALGORITHM);
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

        X500Name issuer = new X500Name(caCert.getSubjectX500Principal().getName());
        BigInteger serial = new BigInteger(32, new SecureRandom());
        Date from = new Date();
        Date to = new Date(System.currentTimeMillis() + (CSR_VALIDITY * 86400000L));

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        X509v3CertificateBuilder certgen = new X509v3CertificateBuilder(issuer, serial, from, to, inputCSR.getSubject(),
                inputCSR.getSubjectPublicKeyInfo());
        certgen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
        certgen.addExtension(Extension.subjectKeyIdentifier, false,
                extUtils.createSubjectKeyIdentifier(inputCSR.getSubjectPublicKeyInfo()));
        certgen.addExtension(Extension.authorityKeyIdentifier, false,
                new AuthorityKeyIdentifier(
                        new GeneralNames(new GeneralName(new X509Name(caCert.getSubjectX500Principal().getName()))),
                        caCert.getSerialNumber()));

        ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
                .build(PrivateKeyFactory.createKey(caPrivate.getEncoded()));
        X509CertificateHolder holder = certgen.build(signer);
        return holder.toASN1Structure().getEncoded();
    }

    public byte[] generateCSR(X500Principal principal, PrivateKey privateKey , PublicKey publicKey, String algorithm) throws OperatorCreationException, IOException {
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                principal, publicKey);
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(algorithm);
        ContentSigner signer = csBuilder.build(privateKey);
        PKCS10CertificationRequest csr = p10Builder.build(signer);
        return csr.getEncoded();
    }


    public void toFile(byte[] csr, File f) throws ServiceException {
        MkKeystore mks = MkKeystore.getInstance(StoreFormat.PEM);
        mks.saveCSR(csr, f, MkKeystore.SAVE_OPTION.NONE);
    }

    public String toString(byte[] csr) throws ServiceException {
        MkKeystore mks = MkKeystore.getInstance(StoreFormat.PEM);
        ByteArrayOutputStream baos= new ByteArrayOutputStream();

        mks.saveCSR(csr, baos, MkKeystore.SAVE_OPTION.NONE);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public byte[] tobyteArray(byte[] csr) throws ServiceException {
        MkKeystore mks = MkKeystore.getInstance(StoreFormat.PEM);
        ByteArrayOutputStream baos= new ByteArrayOutputStream();

        mks.saveCSR(csr, baos, MkKeystore.SAVE_OPTION.NONE);
        return baos.toByteArray();
    }
}
