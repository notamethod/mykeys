/**
 * 
 */
package org.dpr.mykeys.app.signature;


/**
 * @author Buck
 *
 */
class SignToolsToUpgrade {

//	public static final Log log = LogFactory.getLog(SignTools.class);
//	public static void main(String[] args) {
//		Security.addProvider(new BouncyCastleProvider());
//		X509Certificate certs[] = null;
//		try {
//			InputStream is = new FileInputStream(
//					"C:/Documents and Settings/PXXX/trusted.cer");
//			certs = TrustCertUtil.getTrustedCerts(is, "BC");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (GeneralSecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// try {
//		// KeyStore ks = kt.loadKeyStore("fdt.p12", StoreFormat.PKCS12,
//		// "1234".toCharArray());
//		// //{85B8BEBE-A752-4728-8A60-52E946A941B7}
//		// CertificateInfo certInfo = new CertificateInfo();
//		// kt.fillCertInfo(ks, certInfo,
//		// "{85B8BEBE-A752-4728-8A60-52E946A941B7}");
//		// // while (ks.aliases().hasMoreElements()){
//		// // log.trace(ks.aliases().nextElement());
//		// // }
//		// } catch (KeyToolsException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//
//		try {
//			FileInputStream fCrl = new FileInputStream(new File("LATESTCRL"));
//			CRLManager crlMan = new CRLManager("BC");
//			X509CRL x509Crl = crlMan.getCrl(fCrl);
//			SignTools tools = new SignTools();
//			// tools.generateCMS(certs, null, false);
//			String outputName = "test1.p7b";
//			tools.generateEmptyCMS(certs, new X509CRL[] { x509Crl }, outputName);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CRLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		SignTools tools = new SignTools();
//		// tools.generateCMS(certs, null, false);
//		// tools.generateEmptyCMS(certs, false);
//
//		// tools.generateCMS(certifs, file, addFile);
//	}
//
//	public void SignData(KeyStoreValue ksInfo, CertificateInfo certInfo,
//			String file, boolean addFile) {
//		// TODO:
//		// ajouter le timestamp
//		try {
//			KeyTools kt = new KeyTools();
//			X509Certificate cert = certInfo.getCertificate();
//			PrivateKey privatekey = certInfo.getPrivateKey();
//			// Chargement du fichier qui va être signé
//
//			File file_to_sign = new File(file);
//			byte[] buffer = new byte[(int) file_to_sign.length()];
//			DataInputStream in = new DataInputStream(new FileInputStream(
//					file_to_sign));
//			in.readFully(buffer);
//			in.close();
//
//			// Chargement des certificats qui seront stockés dans le fichier .p7
//			// Soit le certificat perso seul, soit la chaine de certification.
//			ArrayList certList = new ArrayList();
//			Certificate[] certsTmp = certInfo.getCertificateChain();
//			for (Certificate cerTmp : certsTmp) {
//				certList.add(cerTmp);
//			}
//			CertStore certs = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(certList), "BC");
//
//			CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
//
//			
//			
//			// privatekey correspond à notre clé privée récupérée du fichier
//			// PKCS#12
//			// cert correspond au certificat publique personnal_nyal.cer
//			// Le dernier argument est l'algorithme de hachage qui sera utilisé
//
//			// Ajout des CRLS ??
//			// a voir
//			
//			ContentSigner sha1Signer = new JcaContentSignerBuilder();
//
//			signGen.addSigner(privatekey, cert,
//					CMSSignedDataGenerator.DIGEST_SHA256);
//			signGen.addCertificatesAndCRLs(certs);
//			CMSProcessable content = new CMSProcessableByteArray(buffer);
//			
//			
//
//			// Generation du fichier CMS/PKCS#7
//			// L'argument deux permet de signifier si le document doit être
//			// attaché avec la signature
//			// Valeur true: le fichier est attaché (c'est le cas ici)
//			// Valeur false: le fichier est détaché
//
//			CMSSignedData signedData = signGen.generate(content, addFile, "BC");
//			byte[] signeddata = signedData.getEncoded();
//
//			// Ecriture du buffer dans un fichier.
//
//			FileOutputStream envfos = new FileOutputStream(
//					file_to_sign.getName() + ".pk7");
//			envfos.write(signeddata);
//			envfos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	public void verify(String signatureFile) {
//		try {
//			// Chargement du fichier signé
//			File f = new File(signatureFile);
//			byte[] buffer = new byte[(int) f.length()];
//			DataInputStream in = new DataInputStream(new FileInputStream(f));
//			in.readFully(buffer);
//			in.close();
//
//			CMSSignedData signature = new CMSSignedData(buffer);
//			SignerInformation signer = (SignerInformation) signature
//					.getSignerInfos().getSigners().iterator().next();
//			CertStore cs = signature.getCertificatesAndCRLs("Collection", "BC");
//			Iterator iter = cs.getCertificates(signer.getSID()).iterator();
//			X509Certificate certificate = (X509Certificate) iter.next();
//			CMSProcessable sc = signature.getSignedContent();
//
//			byte[] data = (byte[]) sc.getContent();
//
//			// Verifie la signature
//			log.trace(signer.verify(certificate, "BC"));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	public void verify2(String signatureFile, String fileOri) throws Exception {
//		File file_to_sign = new File(fileOri);
//		int taille = (int) file_to_sign.length();
//		byte[] buffer = new byte[taille];
//		DataInputStream in = new DataInputStream(new FileInputStream(
//				file_to_sign));
//		try {
//			in.readFully(buffer);
//			in.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// Chargement du fichier signature
//		File fs = new File(signatureFile);
//		// byte[] buffer2 = new byte[(int) f.length()];
//		FileInputStream isSig = new FileInputStream(fs);
//		// DataInputStream in2 = new DataInputStream(new FileInputStream(f));
//		// in.readFully(buffer);
//		// in.close();
//		// validation
//		// CMSProcessableByteArray sigArray = new
//		// CMSProcessableByteArray(buffer);
//		CMSProcessableByteArray content = new CMSProcessableByteArray(buffer);
//		// CMSSignedData(CMSProcessable signedContent, java.io.InputStream
//		// sigData)
//		CMSSignedData sig = new CMSSignedData(content, isSig);
//		SignerInformation signer = (SignerInformation) sig.getSignerInfos()
//				.getSigners().iterator().next();
//
//		// CMSAttributes
//		CertStore cs = sig.getCertificatesAndCRLs("Collection", "BC");
//		Iterator iter = cs.getCertificates(signer.getSID()).iterator();
//		X509Certificate certificate = (X509Certificate) iter.next();
//
//		boolean v = false;
//		try {
//			v = signer.verify(certificate, "BC");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		log.trace(v);
//		// return v;
//
//	}
//
//	public void generateCMS(X509Certificate[] certifs, String file,
//			boolean addFile) {
//		// TODO:
//		// ajouter le timestamp
//		try {
//			file = "c:/dev/testurl.txt";
//			// X509Certificate cert = certInfo.getCertificate();
//			// PrivateKey privatekey = certInfo.getPrivateKey();
//			// Chargement du fichier qui va être signé
//
//			File file_to_sign = new File(file);
//			byte[] buffer = new byte[(int) file_to_sign.length()];
//			DataInputStream in = new DataInputStream(new FileInputStream(
//					file_to_sign));
//			in.readFully(buffer);
//			in.close();
//
//			// Chargement des certificats qui seront stockés dans le fichier .p7
//			// Soit le certificat perso seul, soit la chaine de certification.
//			ArrayList certList = new ArrayList();
//			// Certificate[] certsTmp = certInfo.getCertificateChain();
//			for (Certificate cerTmp : certifs) {
//				certList.add(cerTmp);
//			}
//			CertStore certs = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(certList), "BC");
//
//			CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
//
//			// privatekey correspond à notre clé privée récupérée du fichier
//			// PKCS#12
//			// cert correspond au certificat publique personnal_nyal.cer
//			// Le dernier argument est l'algorithme de hachage qui sera utilisé
//
//			// Ajout des CRLS ??
//			// a voir
//
//			// signGen.addSigner(privatekey, cert,
//			// CMSSignedDataGenerator.DIGEST_SHA256);
//			signGen.addCertificatesAndCRLs(certs);
//			CMSProcessable content = new CMSProcessableByteArray(buffer);
//
//			// Generation du fichier CMS/PKCS#7
//			// L'argument deux permet de signifier si le document doit être
//			// attaché avec la signature
//			// Valeur true: le fichier est attaché (c'est le cas ici)
//			// Valeur false: le fichier est détaché
//
//			CMSSignedData signedData = signGen.generate(content, addFile, "BC");
//			byte[] signeddata = signedData.getEncoded();
//
//			// Ecriture du buffer dans un fichier.
//
//			FileOutputStream envfos = new FileOutputStream(
//					file_to_sign.getName() + ".pk7");
//			envfos.write(signeddata);
//			envfos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	public void generateEmptyCMS(X509Certificate[] certifs, X509CRL[] crls,
//			String outputName) {
//
//		try {
//
//			// Chargement des certificats qui seront stockés dans le fichier .p7
//			// Soit le certificat perso seul, soit la chaine de certification.
//			ArrayList certList = new ArrayList();
//			ArrayList crlList = new ArrayList();
//			// Certificate[] certsTmp = certInfo.getCertificateChain();
//			for (Certificate cerTmp : certifs) {
//				certList.add(cerTmp);
//			}
//			for (X509CRL crlTmp : crls) {
//				crlList.add(crlTmp);
//			}
//			CertStore certs = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(certList), "BC");
//
//			CertStore crlStore = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(crlList), "BC");
//
//			CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
//
//			// privatekey correspond à notre clé privée récupérée du fichier
//			// PKCS#12
//			// cert correspond au certificat publique personnal_nyal.cer
//			// Le dernier argument est l'algorithme de hachage qui sera utilisé
//
//			// Ajout des CRLS
//
//			// signGen.addSigner(privatekey, cert,
//			// CMSSignedDataGenerator.DIGEST_SHA256);
//			signGen.addCertificatesAndCRLs(certs);
//			signGen.addCertificatesAndCRLs(crlStore);
//			// CMSProcessable content = new CMSProcessableByteArray(buffer);
//
//			// Generation du fichier CMS/PKCS#7
//			// L'argument deux permet de signifier si le document doit être
//			// attaché avec la signature
//			// Valeur true: le fichier est attaché (c'est le cas ici)
//			// Valeur false: le fichier est détaché
//
//			CMSSignedData signedData = signGen.generate(null, false, "BC");
//			byte[] signeddata = signedData.getEncoded();
//
//			// Ecriture du buffer dans un fichier.
//
//			FileOutputStream envfos = new FileOutputStream("test1" + ".pk7");
//			envfos.write(signeddata);
//			envfos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	/**
//	 * Génération d'une enveloppe CMS non signée. Pour stockage des AC et des
//	 * CRL
//	 * 
//	 * <BR>
//	 * 
//	 * 
//	 * @param certifs
//	 * @param crls
//	 * @param output
//	 */
//	public void generateUnsignedCMS(X509Certificate[] certifs, X509CRL[] crls,
//			String outputName) {
//
//		try {
//
//			// mise en forme certificats
//			ArrayList certList = new ArrayList();
//
//			for (Certificate cerTmp : certifs) {
//				certList.add(cerTmp);
//			}
//			CertStore certs = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(certList), "BC");
//			// mise en forme crls
//			ArrayList crlList = new ArrayList();
//			for (X509CRL crlTmp : crls) {
//				crlList.add(crlTmp);
//			}
//
//			CertStore crlStore = CertStore.getInstance("Collection",
//					new CollectionCertStoreParameters(crlList), "BC");
//
//			CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
//
//			// privatekey correspond à notre clé privée récupérée du fichier
//			// PKCS#12
//			// cert correspond au certificat publique personnal_nyal.cer
//			// Le dernier argument est l'algorithme de hachage qui sera utilisé
//
//			// Ajout des CRLS
//
//			// signGen.addSigner(privatekey, cert,
//			// CMSSignedDataGenerator.DIGEST_SHA256);
//			signGen.addCertificatesAndCRLs(certs);
//			signGen.addCertificatesAndCRLs(crlStore);
//			// CMSProcessable content = new CMSProcessableByteArray(buffer);
//
//			// Generation du fichier CMS/PKCS#7
//			// L'argument deux permet de signifier si le document doit être
//			// attaché avec la signature
//			// Valeur true: le fichier est attaché (c'est le cas ici)
//			// Valeur false: le fichier est détaché
//
//			CMSSignedData signedData = signGen.generate(null, false, "BC");
//			byte[] signeddata = signedData.getEncoded();
//
//			// Ecriture du buffer dans un fichier.
//
//			FileOutputStream envfos = new FileOutputStream(outputName);
//			envfos.write(signeddata);
//			envfos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}

}
