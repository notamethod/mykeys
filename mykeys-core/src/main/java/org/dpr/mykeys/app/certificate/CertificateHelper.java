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
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;

import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateHelper {


    private static final int CSR_VALIDITY = 365;
    private static final String CSR_SIGN_ALGORITHM = "SHA256withRSA";
    private static final Log log = LogFactory.getLog(CertificateHelper.class);
    KeyTools ktool;
    private CertificateValue certInfo;

    public CertificateHelper(CertificateValue certInfo) {
        super();
        this.certInfo = certInfo;
    }

    private CertificateHelper() {
        super();
    }

    public CertificateValue createCertificate(CertificateValue issuer) throws CertificateException {

        return createCertificate(false, issuer);
    }

    /**
     * @param isAC   true is is an CA
     * @param issuer certificate issuer
     * @return
     * @throws CertificateException
     */
    public CertificateValue createCertificate(boolean isAC, CertificateValue issuer) throws CertificateException {

        CertificateBuilder builder = new CertificateBuilder();
        try {

            if (issuer == null) {
                issuer = certInfo;
            }
            return builder.generate(certInfo, issuer, isAC).getValue();
        } catch (Exception e) {
            log.error(e);
            throw new CertificateException(e);
        }
    }

    public CertificateValue createCertificate(CertificateValue certModel, CertificateValue issuer) throws CertificateException {

        CertificateBuilder builder = new CertificateBuilder();
        try {

            if (issuer == null) {
                issuer = certInfo;
            }
            return builder.generate(certModel, issuer, false).getValue();
        } catch (Exception e) {
            log.error(e);
            throw new CertificateException(e);
        }
    }

    public CertificateValue createCertificate(CertificateValue certModel, CertificateValue issuer, Usage usage) throws CertificateException {

        CertificateBuilder builder = new CertificateBuilder();
        try {

            if (issuer == null) {
                issuer = certInfo;
            }
            return builder.generate(certModel, issuer, false, usage).getValue();
        } catch (Exception e) {
            log.error(e);
            throw new CertificateException(e);
        }
    }


    public X509Certificate[] generateCrlToFix() {
        // if (ktool == null) {
        // ktool = new KeyTools();
        // }
        // X509Certificate[] xCerts;
        // try {
        // xCerts = ktool.genererX509(certInfo, certInfo.getIssuer(), false);
        // } catch (Exception e) {
        // throw new CertificateException(e);
        // }
        // return xCerts;
        return null;
    }

    public String keyUsageToString() {
        String value = "";
        boolean[] keyUsage = certInfo.getKeyUsage();
        boolean isKeyUsage = false;
        if (keyUsage == null) {
            return "null";
        }
        for (int i = 0; i < keyUsage.length; i++) {
            if (keyUsage[i]) {
                isKeyUsage = true;
                value = value + ", " + X509Constants.keyUsageLabel[i];
            }
        }
        if (isKeyUsage) {
            return value.substring(1);
        } else {
            return null;
        }

    }
//TODEL
//	public X509Certificate[] genererX509(CertificateValue infoEmetteur, CertificateValue certInfo,
//			String aliasEmetteur, boolean isAC) throws Exception {
//		CertificateBuilder builder = new CertificateBuilder();
//		Usage usage = null;
//		KeyStoreValue ksInfo = null;
//		if (!StringUtils.isBlank(aliasEmetteur)) {
//			char[] password = InternalKSTmp.getPassword();
//			ksInfo = InternalKSTmp.getStoreAC();
//			infoEmetteur.setPrivateKey((PrivateKey) ks.getKey(aliasEmetteur, password));
//			usage = Usage.CODESIGNING;
//		}
//		return builder.generate(certInfo, infoEmetteur, isAC, usage).getCertificates();
//	}


    /**
     * Generate a X509 certificate from CSR file
     *
     * @param fic
     * @return
     * @throws ServiceException
     * @throws IOException
     */
    public CertificateValue generateFromCSR(InputStream fic, CertificateValue issuer) throws ServiceException, IOException {

        CertificateBuilder builder = new CertificateBuilder();

        X509Certificate[] certificates = null;
        CertificateValue cert = null;
        try {

            Object pemcsr;
            BufferedReader buf = new BufferedReader(new InputStreamReader(fic));

            //xCert = builder.generateFromCSR(buf, issuer).get();
            PemReader reader = new PemReader(buf);
            PKCS10CertificationRequest csr = convertPemToPKCS10CertificationRequest(reader);

            X500Name x500Name = csr.getSubject();
            log.info("x500Name is: " + x500Name + "\n");
            log.info("x500Name is: " + csr.getSignatureAlgorithm() + "\n");

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

        int validity = CSR_VALIDITY;
        X500Name issuer = new X500Name(caCert.getSubjectX500Principal().getName());
        BigInteger serial = new BigInteger(32, new SecureRandom());
        Date from = new Date();
        Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));

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
        byte[] certencoded = holder.toASN1Structure().getEncoded();
        return certencoded;

    }


}
