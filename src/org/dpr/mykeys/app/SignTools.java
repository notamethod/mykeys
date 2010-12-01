/**
 * 
 */
package org.dpr.mykeys.app;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import java.security.cert.Certificate;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;

/**
 *<pre>
 * <b></b>.
 * 
 * <b>Description :</b>
 * 
 * 
 *</pre>
 * 
 * @author C. Roger<BR>
 * <BR>
 *         Créé le 1 déc. 2010 <BR>
 * <BR>
 * <BR>
 *         <i>Copyright : Tessi Informatique </i><BR>
 */
public class SignTools {

    public void SignData(KeyStoreInfo ksInfo, CertificateInfo certInfo,
	    String file, boolean addFile) {
	//TODO:
	//ajouter le timestamp
	try {
	    KeyTools kt = new KeyTools();
	    X509Certificate cert = certInfo.getCertificate();
	    PrivateKey privatekey = certInfo.getPrivateKey();
	    // Chargement du fichier qui va être signé

	    File file_to_sign = new File(file);
	    byte[] buffer = new byte[(int) file_to_sign.length()];
	    DataInputStream in = new DataInputStream(new FileInputStream(
		    file_to_sign));
	    in.readFully(buffer);
	    in.close();

	    // Chargement des certificats qui seront stockés dans le fichier .p7
	    // Soit le certificat perso seul, soit la chaine de certification.
	    ArrayList certList = new ArrayList();
	    Certificate[] certsTmp = certInfo.getCertificateChain();
	    for (Certificate cerTmp : certsTmp) {
		certList.add(cerTmp);
	    }
	    CertStore certs = CertStore.getInstance("Collection",
		    new CollectionCertStoreParameters(certList), "BC");

	    CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();

	    // privatekey correspond à notre clé privée récupérée du fichier
	    // PKCS#12
	    // cert correspond au certificat publique personnal_nyal.cer
	    // Le dernier argument est l'algorithme de hachage qui sera utilisé

	    // Ajout des CRLS ??
	    // a voir

	    signGen.addSigner(privatekey, cert,
		    CMSSignedDataGenerator.DIGEST_SHA256);
	    signGen.addCertificatesAndCRLs(certs);
	    CMSProcessable content = new CMSProcessableByteArray(buffer);

	    // Generation du fichier CMS/PKCS#7
	    // L'argument deux permet de signifier si le document doit être
	    // attaché avec la signature
	    // Valeur true: le fichier est attaché (c'est le cas ici)
	    // Valeur false: le fichier est détaché

	    CMSSignedData signedData = signGen.generate(content, addFile, "BC");
	    byte[] signeddata = signedData.getEncoded();

	    // Ecriture du buffer dans un fichier.

	    FileOutputStream envfos = new FileOutputStream(file_to_sign
		    .getName()
		    + ".pk7");
	    envfos.write(signeddata);
	    envfos.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }

    public void verify(String signatureFile) {
	try {
	    // Chargement du fichier signé
	    File f = new File(signatureFile);
	    byte[] buffer = new byte[(int) f.length()];
	    DataInputStream in = new DataInputStream(new FileInputStream(f));
	    in.readFully(buffer);
	    in.close();

	    CMSSignedData signature = new CMSSignedData(buffer);
	    SignerInformation signer = (SignerInformation) signature
		    .getSignerInfos().getSigners().iterator().next();
	    CertStore cs = signature.getCertificatesAndCRLs("Collection", "BC");
	    Iterator iter = cs.getCertificates(signer.getSID()).iterator();
	    X509Certificate certificate = (X509Certificate) iter.next();
	    CMSProcessable sc = signature.getSignedContent();

	    byte[] data = (byte[]) sc.getContent();

	    // Verifie la signature
	    System.out.println(signer.verify(certificate, "BC"));
	
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }
    
    public void verify2(String signatureFile, String fileOri) throws Exception{
	File file_to_sign = new File(fileOri);
	int taille = (int)file_to_sign.length();
	byte[] buffer = new byte[taille];
	DataInputStream in = new DataInputStream(new FileInputStream(file_to_sign));
	try {
	in.readFully(buffer);
	in.close();
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}

	    // Chargement du fichier signature
	    File f = new File(signatureFile);
	    //byte[] buffer2 = new byte[(int) f.length()];
	    FileInputStream isSig = new FileInputStream(f);
//	    DataInputStream in2 = new DataInputStream(new FileInputStream(f));
//	    in.readFully(buffer);
//	    in.close();	
	//validation
	//CMSProcessableByteArray sigArray = new CMSProcessableByteArray(buffer);
	CMSProcessableByteArray content = new CMSProcessableByteArray(buffer);
	//CMSSignedData(CMSProcessable signedContent, java.io.InputStream sigData) 
	CMSSignedData sig = new CMSSignedData( content, isSig);
	SignerInformation signer = (SignerInformation)sig
	.getSignerInfos().getSigners().iterator().next();
	
	//CMSAttributes
	CertStore cs = sig
	.getCertificatesAndCRLs("Collection", "BC");
	Iterator iter = cs.getCertificates(signer.getSID()).iterator();
	X509Certificate certificate = (X509Certificate) iter.next();

	boolean v=false;
	try {
	    v = signer.verify(certificate, "BC");
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println(v);
	//return v; 	
	
    }

}
