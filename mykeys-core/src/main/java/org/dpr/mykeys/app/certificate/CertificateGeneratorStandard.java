package org.dpr.mykeys.app.certificate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.dpr.mykeys.utils.ProviderUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import static org.dpr.mykeys.app.KeyTools.RandomBI;

public class CertificateGeneratorStandard implements CertificateGeneratorExtensions {

    private final Log log = LogFactory.getLog(CertificateGeneratorStandard.class);
    private static final int AUTH_VALIDITY = 999;

    public CertificateGeneratorStandard() {
        super();
    }


    /**
     * Key pair generation
     *
     * @param algo
     * @param keyLength
     */
    private KeyPair generateKeyPair(String algo, int keyLength) {
        KeyPair keypair = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("generating keypair: " + algo + " keypair: " + keyLength);
            }

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo, "BC");
            keyGen.initialize(keyLength);

            keypair = keyGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return keypair;
    }

    public void addExtensions(X509v3CertificateBuilder certGen, Map<String, String> parameters) throws IOException {


        Extension en = new Extension(Extension.basicConstraints,
                false,
                new BasicConstraints(false).getEncoded());
        certGen.addExtension(en);

    }

    public CertificateValue generate(CertificateValue certModel)
            throws Exception {
        return generate(certModel, certModel);
    }

    public CertificateValue generate(CertificateValue certModel, CertificateValue certIssuer)
            throws Exception {


        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        KeyPair keypair = generateKeyPair(certModel.getAlgoPubKey(), certModel.getKeyLength());
        // SerialNumber
        BigInteger serial = RandomBI(30);
        if (StringUtils.isBlank(certModel.getAlias())) {
            certModel.setAlias(serial.toString(16));
        }

        X500Name subject = certModel.getFreeSubject() == null ? certModel.subjectMapToX500Name() : certModel.freeSubjectToX500Name();

        //issuer
        X500Name issuerDN;
        if (certIssuer != null && certIssuer.getCertificate() != null) {
            log.info("certificate generated from issuer..." + certIssuer.getName());

            issuerDN = X500Name.getInstance(certIssuer.getCertificate().getSubjectX500Principal().getEncoded());
        } else {
            issuerDN = subject;
        }

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerDN, serial, certModel.getFrom(), certModel.getTo(), subject,
                keypair.getPublic());


        addExtensions(certGen, null);
        certGen.addExtension(Extension.keyUsage, true, new KeyUsage(certModel.getIntKeyUsage()));
        certGen.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(keypair.getPublic()));

        // FIXME: à vérifier en cas de auto signé
        if (certIssuer != null && certIssuer.getCertificate() != null) {
            certGen.addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(certIssuer.getCertificate()));
        } else {
            certGen.addExtension(Extension.authorityKeyIdentifier, false,
                    extUtils.createAuthorityKeyIdentifier(keypair.getPublic()));
        }


        if (StringUtils.isNotBlank(certModel.getPolicyCPS())) {
            PolicyQualifierInfo policyQualifierInfo = new PolicyQualifierInfo(certModel.getPolicyCPS());
            PolicyInformation policyInformation = new PolicyInformation(PolicyQualifierId.id_qt_cps,
                    new DERSequence(policyQualifierInfo));
            ASN1EncodableVector certificatePolicies = new ASN1EncodableVector();
            final UserNotice un = new UserNotice(null, new DisplayText(DisplayText.CONTENT_TYPE_UTF8STRING, certModel.getPolicyNotice()));
            PolicyQualifierInfo not = new PolicyQualifierInfo(PolicyQualifierId.id_qt_unotice, un);
            certificatePolicies.add(policyInformation);
            certificatePolicies.add(not);
            if (!certModel.getPolicyID().isEmpty()) {
                PolicyInformation extraPolicyInfo = new PolicyInformation(new ASN1ObjectIdentifier(certModel.getPolicyID()),
                        new DERSequence(new ASN1ObjectIdentifier("")));
                certificatePolicies.add(extraPolicyInfo);
            }

            certGen.addExtension(Extension.certificatePolicies, false, new DERSequence(certificatePolicies));
        }

        // gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
        // new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

        // self signed ?

        PrivateKey pk;
        PublicKey pubKey;
        if (certIssuer == null || certModel.getSubjectString().equalsIgnoreCase(certIssuer.getSubjectString())) {
            pk = keypair.getPrivate();
            pubKey = keypair.getPublic();
        } else {
            pk = certIssuer.getPrivateKey();
            pubKey = certIssuer.getPublicKey();
        }
        ContentSigner signer = new JcaContentSignerBuilder(certModel.getAlgoSig()).build(pk);

        X509CertificateHolder certHolder = certGen.build(signer);

        X509Certificate cert = new JcaX509CertificateConverter().setProvider(ProviderUtil.provider).getCertificate(certHolder);
        // TODO: let generate expired certificate for test purpose ?
        try {
            cert.checkValidity(new Date());
        } catch (Exception e) {
            log.warn("invalid certificate", e);
        }

        cert.verify(pubKey);

        X509Certificate[] certChain = null;
        // FIXME: gérer la chaine de l'émetteur
        if (certIssuer != null && certIssuer.getCertificateChain() != null) {
            log.info("adding issuer " + certIssuer.getName() + "'s certicate chain to certificate");
            certChain = new X509Certificate[certIssuer.getCertificateChain().length + 1];
            System.arraycopy(certIssuer.getCertificateChain(), 0, certChain, 1,
                    certIssuer.getCertificateChain().length);
            certChain[0] = cert;
            // certChain[1] = certIssuer.getCertificate();
        } else if (certIssuer != null && certIssuer.getCertificate() != null) {
            log.error("FIXME");
            certChain = new X509Certificate[2];
            certChain[0] = cert;
            certChain[1] = certIssuer.getCertificate();
        } else {
            certChain = new X509Certificate[]{cert};
        }
        CertificateValue certReturn = new CertificateValue(certChain);
        certReturn.setPrivateKey(keypair.getPrivate());
        certReturn.setPublicKey(keypair.getPublic());
        certReturn.setPassword(certModel.getPassword());

        return certReturn;

    }


}
