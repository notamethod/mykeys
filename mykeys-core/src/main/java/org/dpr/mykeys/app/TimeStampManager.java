/**
 * 
 */
package org.dpr.mykeys.app;

import org.bouncycastle.tsp.TimeStampToken;

/*
 * http://threebit.net/mail-archive/itext-questions/msg04989.html
 * http://www.berthou.com/fr/2008/01/25/un-timestamp-pour-signer-un-pdf/
 * http://www.karlverger.com/developpement/signature-electronique-horodatage-bouncycastle-2-54
 */
public class TimeStampManager {

	public static TimeStampToken getTimeStampToken(int i) {
		// TODO Auto-generated method stub
		return null;
	}

//	private static Logger log = Logger.getLogger(TimeStampManager.class);
//
//	private static TimeStampManager instance = null;
//
//	public static TimeStampManager getIstance() {
//		if (instance == null)
//			instance = new TimeStampManager();
//		return instance;
//	}
//
//	KeyStoreValue ksInfo;
//
//	private TimeStampManager() {
//
//	}
//
//	/**
//	 * Modyfy PKCS#7 data by adding timestamp
//	 * 
//	 * (at) param signedData (at) throws Exception
//	 */
//	private static CMSSignedData addTimestamp(CMSSignedData signedData)
//			throws Exception {
//
//		Collection ss = signedData.getSignerInfos().getSigners();
//		SignerInformation si = (SignerInformation) ss.iterator().next();
//		TimeStampToken tok = getTimeStampToken(1);
//
//		ASN1InputStream asn1InputStream = new ASN1InputStream(tok.getEncoded());
//		ASN1Primitive tstDER = asn1InputStream.readObject();
//		DERSet ds = new DERSet(tstDER);
//		Attribute a = new Attribute(new DERObjectIdentifier("r"), ds);
//		ASN1EncodableVector dv = new ASN1EncodableVector();
//		dv.add(a);
//		AttributeTable at = new AttributeTable(dv);
//		si = SignerInformation.replaceUnsignedAttributes(si, at);
//		ss.clear();
//		ss.add(si);
//		SignerInformationStore sis = new SignerInformationStore(ss);
//		signedData = CMSSignedData.replaceSigners(signedData, sis);
//		return signedData;
//	}
//
//	public static TimeStampToken getTimeStampToken(int TSA) throws Exception {
//
//		PostMethod post = null;
//		switch (TSA) {
//		case 1:
//			post = new PostMethod("http://www.edelweb.fr/cgi-bin/service-tsp");
//			break;
//		case 2:
//			post = new PostMethod("http://tsp.iaik.at/tsp/TspRequest");
//			break;
//		case 3:
//			post = new PostMethod("http://ns.szikszi.hu:8080/tsa");
//			break;
//		case 4:
//			post = new PostMethod("http://time.certum.pl/");
//			break;
//		}
//
//		TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
//		// request TSA to return certificate
//		reqGen.setCertReq(false);
//		// make a TSP request this is a dummy sha1 hash (20 zero bytes) and
//		// nonce=100
//		TimeStampRequest request = reqGen.generate(TSPAlgorithms.SHA1,
//				new byte[20], BigInteger.valueOf(100));
//
//		byte[] enc_req = request.getEncoded();
//		ByteArrayInputStream bais = new ByteArrayInputStream(enc_req);
//		post.setRequestBody(bais);
//		post.setRequestContentLength(enc_req.length);
//		post.setRequestHeader("Content-type", "application/timestamp-query");
//
//		HttpClient http_client = new HttpClient();
//		http_client.executeMethod(post);
//		InputStream in = post.getResponseBodyAsStream();
//
//		// read TSP response
//		TimeStampResponse resp = new TimeStampResponse(in);
//		resp.validate(request);
//		log.info("Timestamp validated");
//
//		TimeStampToken tsToken = resp.getTimeStampToken();
//		SignerId signer_id = tsToken.getSID();
//
//		BigInteger cert_serial_number = signer_id.getSerialNumber();
//
//		log.info("Signer ID serial " + signer_id.getSerialNumber());
//		log.info("Signer ID issuer " + signer_id.getIssuer().toString());
//
//		//oldCertStore cs = tsToken.getCertificatesAndCRLs("Collection", "BC");
//		CertStore cs = (CertStore) tsToken.getCertificates();
//
//		Collection certs = cs.getCertificates(null);
//
//		Iterator iter = certs.iterator();
//		X509Certificate certificate = null;
//		while (iter.hasNext()) {
//			X509Certificate cert = (X509Certificate) iter.next();
//
//			if (cert_serial_number != null) {
//				if (cert.getSerialNumber().equals(cert_serial_number)) {
//					log.info("using certificate with serial: "
//							+ cert.getSerialNumber());
//					certificate = cert;
//				}
//			} else {
//				if (certificate == null) {
//					certificate = cert;
//				}
//			}
//			log.info("Certificate subject dn " + cert.getSubjectDN());
//			log.info("Certificate serial " + cert.getSerialNumber());
//		}
//		tsToken.validate(certificate, "BC");
//		log.info("TS info " + tsToken.getTimeStampInfo().getGenTime());
//		log.info("TS info " + tsToken.getTimeStampInfo());
//		log.info("TS info " + tsToken.getTimeStampInfo().getAccuracy());
//		log.info("TS info " + tsToken.getTimeStampInfo().getNonce());
//		return tsToken;
//	}
//
//	private final static String KEYSTORE_PASSWORD = "poipoi";
//	private final static String CERTIFICAT_NAME = "keypairTest";
//	private final static String CERTIFICAT_PASSWORD = "poipoi";
//	private final static String TSA_POLICY_ID = "0.0";
//
//	/**
//	 * CrÃ©ation d'un jeton d'horodatage via la date du systeme.
//	 * prï¿½fï¿½rer la rï¿½cup du  temps par un serveur NTP
//	 * 
//	 * @param empreinte
//	 * @param idJeton
//	 * @return
//	 * @throws java.lang.Exception
//	 */
//	public static byte[] getTimestamp(byte[] empreinte, String idJeton,
//			KeyStoreValue ksInfo) throws Exception {
//		try {
//			KeyTools kt = new KeyTools();
//			KeystoreBuilder ksBuilder = new KeystoreBuilder();
//			KeyStore ks = ksBuilder.loadKeyStore(ksInfo.getPath(),
//					ksInfo.getStoreFormat(), ksInfo.getPassword()).get();
//
//			// recup keystore, certifs et clefs
//			X509Certificate cert = (X509Certificate) ks
//					.getCertificate(CERTIFICAT_NAME);
//			PrivateKey pk = (PrivateKey) ks.getKey(CERTIFICAT_NAME,
//					CERTIFICAT_PASSWORD.toCharArray());
//			ArrayList certList = new ArrayList();
//			certList.add(cert);
//			CertStore certs = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(certList), "BC");
//
//			String algorithme;
//			algorithme = TSPAlgorithms.SHA1;
//			TimeStampTokenGenerator tokenGen = new TimeStampTokenGenerator(pk,
//					cert, algorithme, TSA_POLICY_ID);
//			tokenGen.setCertificatesAndCRLs(certs);
//
//			TimeStampRequestGenerator gen = new TimeStampRequestGenerator();
//			gen.setCertReq(true);
//
//			TimeStampRequest req = gen.generate(algorithme, empreinte,
//					new BigInteger(80, SecureRandom.getInstance("SHA1PRNG")));
//			TimeStampToken token = tokenGen.generate(req, new BigInteger(
//					idJeton), new Date(), "BC");
//			return token.getEncoded();
//		} catch (Exception e) {
//			throw new Exception("Internal error while generating timestamp", e);
//		}
//	}
}
