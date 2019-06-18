package org.dpr.mykeys.app.certificate;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.dpr.mykeys.utils.ProviderUtil;
import org.dpr.mykeys.app.CertificateType;

public class CertificateHelperNew {

    private final Log log = LogFactory.getLog(CertificateHelperNew.class);
	private static final int AUTH_VALIDITY = 999;

	public CertificateHelperNew() {
		super();
	}

    public CertificateValue createCertificate(CertificateType type, String id, char[] charArray) throws GeneralSecurityException, OperatorCreationException {
		switch (type) {
		case AUTH:
			return  createCertificateAuth(id,  charArray);
		

		default:
			break;
		}
		return null;
		
	}

    private CertificateValue createCertificateAuth(String id, char[] charArray) throws OperatorCreationException, GeneralSecurityException {
		
		int validity = AUTH_VALIDITY;
		// X500Name owner = new X500Name("CN=" + fqdn);
		X500Name subject = new X500Name("CN=" + id);
		BigInteger serial = new BigInteger(32, new SecureRandom());
		Date from = new Date();
		Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));
		KeyPair keypair = generateKeyPair("RSA", 2048);
		 // Prepare the information required for generating an X.509 certificate.
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(subject, serial, from, to, subject,
				keypair.getPublic());


	    ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keypair.getPrivate());
	    X509CertificateHolder certHolder = builder.build(signer);
	    X509Certificate cert = new JcaX509CertificateConverter().setProvider(ProviderUtil.provider).getCertificate(certHolder);
	   
	    cert.verify(keypair.getPublic());
		CertificateValue value = new CertificateValue(id, cert);
		value.setPrivateKey(keypair.getPrivate());
		return value;
	    
		
	}
	
	/**
	 * Key pair generation
	 * 
	 * @param algo
	 * @param keyLength
	 */
    private KeyPair generateKeyPair(String algo, int keyLength) {
		KeyPair keypair =null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("generating keypair: " + algo + " keypair: " + keyLength);
			}

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo, "BC");
			keyGen.initialize(keyLength);

			 keypair = keyGen.genKeyPair();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return keypair;
	}
	
	

}
