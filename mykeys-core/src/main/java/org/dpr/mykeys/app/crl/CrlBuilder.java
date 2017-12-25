package org.dpr.mykeys.app.crl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;

import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;

public class CrlBuilder extends KeyTools {

public static void main(String[] args) {

		CrlBuilder test = new CrlBuilder();
				
//		try {
//			test.generateCrl2();
//		} catch (UnrecoverableKeyException | InvalidKeyException | KeyStoreException | NoSuchProviderException
//				| NoSuchAlgorithmException | CertificateException | CRLException | IllegalStateException
//				| SignatureException | IOException | KeyToolsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	  
}

	public void generateCrl2ToFix() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException, CRLException,
			IllegalStateException, SignatureException, KeyToolsException {

//		String kst = "C:/Documents and Settings/n096015/.myKeys/mykeysAc.jks";
//
//		char[] password = "mKeys983178".toCharArray();
//		KeyStore ks = loadKeyStore(kst, StoreFormat.JKS, password);
//		CertificateInfo cinfo = new CertificateInfo();
//		fillCertInfo(ks, cinfo, "MK DEV AC Intermediaire");
//		Calendar nextupdate = Calendar.getInstance();
//		CrlValue crlInfo = new CrlValue();
//		nextupdate.add(Calendar.DAY_OF_YEAR, 30);
//		crlInfo.setNextUpdate(nextupdate.getTime());
//		Key key = ks.getKey("mk dev root ca", password);
//		X509CRL crl = generateCrl(cinfo.getCertificate(), crlInfo, key);
//		OutputStream os = new FileOutputStream("c:/dev/crl2.crl");
//		os.write(crl.getEncoded());
//		os.close();

	}
    // public void timeStamp(KeyStoreValue ksInfo, CertificateInfo certInfo){
	// TimeStampTokenGenerator ts = new TimeStampTokenGenerator(
	// }

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 * @param certSign
	 * @param crlInfo
	 * @return
	 * @throws CertificateParsingException
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws IllegalStateException
	 * @throws CRLException
	 * @throws InvalidKeyException
	 */
//	public X509CRL generateCrl(CertificateInfo certSign, CrlValue crlInfo)
//			throws CertificateParsingException, InvalidKeyException, CRLException, IllegalStateException,
//			NoSuchProviderException, NoSuchAlgorithmException, SignatureException {
//
//		X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
//		// crlGen.setIssuerDN((X500Principal) certSign.getIssuerDN());
//		crlGen.setIssuerDN(certSign.getCertificate().getSubjectX500Principal());
//		String signAlgo = "SHA1WITHRSAENCRYPTION";
//		crlGen.setThisUpdate(crlInfo.getThisUpdate());
//		crlGen.setNextUpdate(crlInfo.getNextUpdate());
//		crlGen.setSignatureAlgorithm(signAlgo);
//
//		crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
//				new AuthorityKeyIdentifierStructure(certSign.getCertificate()));
//		crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(crlInfo.getNumber()));
//
//		X509CRL crl = crlGen.generate((PrivateKey) certSign.getPrivateKey(), "BC");
//		return crl;
//	}


//	public X509CRL generateCrl(X509Certificate certSign, CrlValue crlInfo, Key privateKey) throws KeyStoreException,
//	NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException,
//	UnrecoverableKeyException, InvalidKeyException, CRLException, IllegalStateException, SignatureException {
//
//Calendar calendar = Calendar.getInstance();
//
//X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
//
//Date now = new Date();
//Date nextUpdate = calendar.getTime();
//
//// crlGen.setIssuerDN((X500Principal) certSign.getIssuerDN());
//crlGen.setIssuerDN(certSign.getSubjectX500Principal());
//String signAlgo = "SHA1WITHRSAENCRYPTION";
//crlGen.setThisUpdate(crlInfo.getThisUpdate());
//crlGen.setNextUpdate(crlInfo.getNextUpdate());
//crlGen.setSignatureAlgorithm(signAlgo);
//// BigInteger bi = new BigInteger("816384897");
//// crlGen.addCRLEntry(BigInteger.ONE, now,
//// CRLReason.privilegeWithdrawn);
//BigInteger bi = new BigInteger("155461028");
//crlGen.addCRLEntry(bi, new Date(), CRLReason.privilegeWithdrawn);
//
//crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
//		new AuthorityKeyIdentifierStructure(certSign));
//crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(crlInfo.getNumber()));
//
//X509CRL crl = crlGen.generate((PrivateKey) privateKey, "BC");
//// OutputStream os = new FileOutputStream(new
//// File("./certificats/crlrevoke.crl"));
//// os.write(crl.getEncoded());
//return crl;
//
//}
}
