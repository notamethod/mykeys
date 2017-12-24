package org.dpr.mykeys.app;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyTools {
    public static final String BEGIN_PEM = "-----BEGIN CERTIFICATE-----";
    public static final String END_PEM = "-----END CERTIFICATE-----";
    public static final String BEGIN_KEY = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String END_KEY = "-----END RSA PRIVATE KEY-----";
    private static final int NUM_ALLOWED_INTERMEDIATE_CAS = 0;
    public static String EXT_P12 = ".p12";
    public static String EXT_PEM = ".pem";
    public static String EXT_DER = ".der";
    // FIXME:en création de magasin si l'extension est saisie ne pas la mettre 2
    // fois.
    // FIXME: ne pas autoriser la saisie de la clé privée dans les magasins
    // internes
    final Log log = LogFactory.getLog(KeyTools.class);

    public static void main(String[] args) {
        KeyTools test = new KeyTools();
        // test.KeyPairGen("RSA", 512, new CertificateInfo());
        // KeyStore ks= test.loadKeyStore("keystorePub.p12", TYPE_P12);
        // X509Certificate cert = test.genererX509();
        // test.saveCert("cert001", ks, cert);
        // test.saveKeyStore(ks, "password".toCharArray());
        // test.createKeyStore("JKS");
        // test.createKeyStore(TYPE_P12, "keystorePub.p12");
        Security.addProvider(new BouncyCastleProvider());
//		try {
//			test.generateCrl2();
//		} catch (UnrecoverableKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CRLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SignatureException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyToolsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        // Set aa = Security.getProvider("BC").getServices();
        // Object o = Security.getProvider("BC").get("Signature");
        //
        // Set bb = Security.getProvider("BC").keySet();
        // Set cc = Security.getProvider("BC").getServices();

    }

    /**
     * get a random BigInteger
     *
     * @param numBits
     * @return
     */
    public static BigInteger RandomBI(int numBits) {
        SecureRandom random = new SecureRandom();
        // byte bytes[] = new byte[20];
        // random.nextBytes(bytes);
        BigInteger bi = new BigInteger(numBits, random);
        return bi;

    }

    /**
     * Key pair generation
     *
     * @param algo
     * @param keyLength
     * @param certModel
     * @deprecated replace with own code
     */
    @Deprecated
    public void keyPairGen(String algo, int keyLength, CertificateValue certModel) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("generating keypair: " + algo + " keypair: " + keyLength);
            }

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo, "BC");
            keyGen.initialize(keyLength);

            KeyPair keypair = keyGen.genKeyPair();
            certModel.setPrivateKey(keypair.getPrivate());
            certModel.setPublicKey(keypair.getPublic());

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

    }

    public void saveKeyStore(KeyStore ks, KeyStoreInfo ksInfo) throws KeyToolsException {

        try {
            OutputStream fos = new FileOutputStream(new File(ksInfo.getPath()));
            ks.store(fos, ksInfo.getPassword());
            fos.close();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
        }
    }

    @Deprecated
    public void saveCert(KeyStore kstore, X509Certificate cert, CertificateValue certInfo) throws KeyToolsException {
        try {
            // X509Certificate x509Cert = (X509Certificate) cert;
            Certificate[] chaine = new Certificate[]{cert};
            if (certInfo.getPrivateKey() == null) {
                kstore.setCertificateEntry(certInfo.getAlias(), cert);
            } else {

                kstore.setKeyEntry(certInfo.getAlias(), certInfo.getPrivateKey(), certInfo.getPassword(), chaine);
            }

            // ks.setCertificateEntry(alias, cer);
        } catch (KeyStoreException e) {
            throw new KeyToolsException("Sauvegarde du certificat impossible:" + certInfo.getAlias(), e);
        }
    }

    public void exportDer(CertificateValue certInfo, String fName) throws KeyToolsException {
        /* save the public key in a file */
        try {

            FileOutputStream keyfos = new FileOutputStream(new File(fName + ".der"));
            keyfos.write(certInfo.getCertificate().getEncoded());
            keyfos.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            throw new KeyToolsException("Export de la clé publique impossible:" + certInfo.getAlias(), e);
        }
    }

    public void exportPem(CertificateValue certInfo, String fName) throws KeyToolsException {
        /* save the public key in a file */
        try {
            List<String> lines = new ArrayList<String>();
            lines.add(BEGIN_PEM);
            // FileUtils.writeLines(file, lines)
            File f = new File(fName + ".pem");
            // FileOutputStream keyfos = new FileOutputStream(new File(fName
            // + ".pem"));
            byte[] b = Base64.encodeBase64(certInfo.getCertificate().getEncoded());
            String tmpString = new String(b);
            String[] datas = tmpString.split("(?<=\\G.{64})");
            Collections.addAll(lines, datas);

            lines.add(END_PEM);
            FileUtils.writeLines(f, lines);
            // keyfos.write(certInfo.getCertificate().getEncoded());
            // keyfos.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            throw new KeyToolsException("Export de la clé publique impossible:" + certInfo.getAlias(), e);
        }
    }

    public CRLDistPoint getDistributionPoints(X509Certificate certX509) {

        X509CertificateObject certificateImpl = (X509CertificateObject) certX509;

        byte[] extension = certificateImpl.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

        if (extension == null) {
            if (log.isWarnEnabled()) {
                log.warn("Pas de CRLDistributionPoint pour: " + certificateImpl.getSubjectDN());//
            }
            return null;
        }

        CRLDistPoint distPoints = null;

        try {
            distPoints = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(extension));
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Extension de CRLDistributionPoint non reconnue pour: " + certificateImpl.getSubjectDN());//
            }
            if (log.isDebugEnabled()) {
                log.debug(e);
            }

        }
        return distPoints;

    }


    /**
     * .
     * <p>
     * <BR>
     *
     * @param xCerts
     * @throws IOException
     * @throws CRLException
     */
    public void saveCRL(X509CRL crl, File crlFile) throws CRLException, IOException {
        OutputStream output = new FileOutputStream(crlFile);
        IOUtils.write(crl.getEncoded(), output);

    }


}
