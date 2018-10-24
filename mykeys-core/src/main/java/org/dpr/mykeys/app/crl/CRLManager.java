package org.dpr.mykeys.app.crl;

import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.X509Util;
import org.dpr.mykeys.app.certificate.CertificateValue;


/**
 * @author Buck
 *
 */
public class CRLManager {

	private static final Log log = LogFactory.getLog(CRLManager.class);

    public static final String CRL_EXTENSION = ".crl";

	private String provider;

	/**
	 * . <BR>
	 * 
	 * 
	 * @param securityProvider
	 */
    private CRLManager(String securityProvider) {
		provider = securityProvider;
	}

    public CRLManager() {
        provider = "BC";
    }

	/**
	 * Récupération des points de distribution des CRL.
	 * 
	 * <BR>
	 * 
	 * 
	 * @param certX509
     * @param distPointSet
	 * @throws CRLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void getDistributionPoints(X509Certificate certX509,
			Set<String> distPointSet) {

		X509CertificateObject certificateImpl = (X509CertificateObject) certX509;
		if (log.isInfoEnabled()) {
			log.info("Recherche CRLDistributionPoint pour: "
					+ certificateImpl.getSubjectDN());//
		}
		byte[] extension = certificateImpl
				.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

		if (extension == null) {
			if (log.isWarnEnabled()) {
				log.warn("Pas de CRLDistributionPoint pour: "
						+ certificateImpl.getSubjectDN());//
			}
			return;
		}

		CRLDistPoint distPoints;
        try {
			distPoints = CRLDistPoint.getInstance(X509ExtensionUtil
					.fromExtensionValue(extension));
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Extension de CRLDistributionPoint non reconnue pour: "
						+ certificateImpl.getSubjectDN());//
			}
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
			return;
		}

		DistributionPoint[] pointsDistrib;
		try {
			pointsDistrib = distPoints.getDistributionPoints();
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Extension de CRLDistributionPoint non reconnue pour: "
						+ certificateImpl.getSubjectDN());//
			}
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
			return;
		}
		for (DistributionPoint distributionPoint : pointsDistrib) {
			DistributionPointName name = distributionPoint
					.getDistributionPoint();

			GeneralName[] gns = ((GeneralNames) name.getName()).getNames();

            for (GeneralName gn : gns) {

                if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {

                    //FIXME to test
                    String distPointName = (gn.getName())
                            .toString();

                    distPointSet.add(distPointName);

                    if (log.isDebugEnabled()) {
                        log.debug("récupération url: " + distPointName);
                    }

                }

            }
		}

	}

	/**
	 * .
	 * 
	 * <BR>
	 * 
	 *
	 * @param inStream
	 *            : fichier de CRL
	 * @param cert
	 * @return
	 * @throws CertificateException
	 * @throws NoSuchProviderException
	 * @throws CRLException
	 */
	public EtatRevocation validateCertificate(InputStream inStream,
			X509Certificate cert) throws GeneralSecurityException {
		X509CRL crl;
		crl = getCrl(inStream);

		if (crl.isRevoked(cert)) {
			return EtatRevocation.REVOKED;
		} else {
			return EtatRevocation.NOT_REVOKED;
		}

	}

	/**
	 * Contrôle la validité d'une liste de révocation.
	 * 
	 * <BR>
	 * 
	 * @param date
	 *            : date sur laquelle tester la validité
	 * @param crlFile
	 *            : fichier de crl
	 * @return
	 * @throws CRLException
	 * @throws NoSuchProviderException
	 * @throws FileNotFoundException
	 */
	public EtatCrl validateCRL(Date date, File crlFile)
			throws GeneralSecurityException, IOException {
		// EtatCrl etatCrl = EtatCrl.UNKNOWN;

		InputStream inStream;
		inStream = new FileInputStream(crlFile);
		IOUtils.closeQuietly(inStream);

		return validateCRL(date, inStream);
	}

	/**
	 * Contrôle la validité d'une liste de révocation.
	 * 
	 * <BR>
	 * 
	 * @param date
	 *            : date sur laquelle tester la validité
	 * @param crlFile
	 *            : fichier de crl
	 * @return
	 * @throws CRLException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 */
    private EtatCrl validateCRL(Date date, InputStream inStream)
			throws GeneralSecurityException {
		EtatCrl etatCrl;
		X509CRL crl;

		crl = getCrl(inStream);
		if (date.after(crl.getNextUpdate())) {
			etatCrl = EtatCrl.TO_UPDATE;
		} else {
			etatCrl = EtatCrl.UP_TO_DATE;
		}
		return etatCrl;

	}

	/**
	 * Retourne un objet X509CRL à partir d'un fichier.
	 * 
	 * <BR>
	 *
	 * @param inStream
	 * @throws FileNotFoundException
	 * @throws NoSuchProviderException
	 * @throws CRLException
	 * @throws CertificateException
	 */
    public X509CRL getCrl(InputStream inStream) throws CRLException,
			NoSuchProviderException, CertificateException {

		CertificateFactory cf;

		cf = CertificateFactory.getInstance("X.509", provider);

		X509CRL crl = (X509CRL) cf.generateCRL(inStream);
		if (log.isDebugEnabled()) {
			log.debug("CRL issuer     -->" + crl.getIssuerDN()
					+ "Effective From -->" + crl.getThisUpdate()
					+ "Next update    -->" + crl.getNextUpdate());
		}
		return crl;
	}

	public X509CRL generateCrl(CertificateValue certSign, Date thisDate, Date nextDate, Collection filter) throws CRLException, OperatorCreationException {
		X509Certificate certificate = certSign.getCertificate();
		PrivateKey privateKey = (certSign.getPrivateKey());

		X500Name crlIssuer = X500Name.getInstance(certificate.getSubjectX500Principal().getEncoded());
		X500Name caName = X500Name.getInstance(certificate.getIssuerX500Principal().getEncoded());
		X509v2CRLBuilder builder = new X509v2CRLBuilder(crlIssuer,
				thisDate
		);

		builder.setNextUpdate(nextDate);

		for (Object value : filter) {
			CRLEntry entry = (CRLEntry) value;

			builder.addCRLEntry(entry.getSerialNumber(), new Date(), entry.getReason());
		}
//		builder.addExtension(org.bouncycastle.asn1.x509.Extension.issuingDistributionPoint, true, new IssuingDistributionPoint(null, true, false));

//		ExtensionsGenerator extGen = new ExtensionsGenerator();
//
//		extGen.addExtension(org.bouncycastle.asn1.x509.Extension.reasonCode, false, org.bouncycastle.asn1.x509.CRLReason.lookup(org.bouncycastle.asn1.x509.CRLReason.cACompromise));
//		extGen.addExtension(org.bouncycastle.asn1.x509.Extension.certificateIssuer, true, new GeneralNames(new GeneralName(caName)));

		JcaContentSignerBuilder contentSignerBuilder =
				new JcaContentSignerBuilder("SHA256WithRSAEncryption");

		contentSignerBuilder.setProvider(provider);

		X509CRLHolder crlHolder = builder.build(contentSignerBuilder.build(privateKey));

		JcaX509CRLConverter converter = new JcaX509CRLConverter();

		converter.setProvider(provider);

		return converter.getCRL(crlHolder);

	}

	public enum EtatCrl {

        UNKNOWN, NOT_YET_VALID, UP_TO_DATE, TO_UPDATE

    }

	// http://www.ca-certificat.com/doc/LatestCRL.crl
	public enum EtatRevocation {

        UNKNOWN, REVOKED, NOT_REVOKED

    }

	/**
	 * Construction chemin du fichier de crl.
	 * 
	 * <BR>
	 * chemin de CRL=[pki.crl.directory]/[valeur du champ 'O' de l'AC]/[valeur
	 * du champCN de l'AC].crl
	 * 
	 * 
	 * @param certificate
	 * @return
	 */
    @Deprecated
    public File getCrlFile(String pathName, X509Certificate certificate,
                           boolean isAC) {

		Map<ASN1ObjectIdentifier, String> map;
		// si c'est un certificat d'AC, on récupére les identifiant du sujet,
		// sinon les identifiants de l'emetteur
		if (isAC) {
			map = X509Util.getSubjectMap(certificate);
		} else {
			map = X509Util.getIssuerMap(certificate);
		}
		String fname = map.get(X509Name.CN);
		fname = fname + CRL_EXTENSION;
		String orga = map.get(X509Name.O);
		File fpath = new File(pathName, orga);
		File crlFile = new File(fpath, fname);
		return crlFile;
	}

	/**
	 * Construction chemin du fichier de crl.
	 * 
	 * <BR>
	 * chemin de CRL=[pki.crl.directory]/[valeur du champ 'O' de l'AC]/[valeur
	 * du champCN de l'AC].crl
	 * 
	 * 
	 * @param certificate
	 * @return
	 */
	@Deprecated
	public static String getCrlFileName(X509Certificate certificate,
										boolean isAC) {

		Map<ASN1ObjectIdentifier, String> map;
		// si c'est un certificat d'AC, on récupére les identifiant du sujet,
		// sinon les identifiants de l'emetteur
		if (isAC) {
			map = X509Util.getSubjectMap(certificate);
		} else {
			map = X509Util.getIssuerMap(certificate);
		}
		String fname = map.get(X509Name.CN);
		fname = fname + CRL_EXTENSION;
		String orga = map.get(X509Name.O);
		return orga + File.separator + fname;
	}

	/**
	 * Construction chemin du fichier de crl.
	 * 
	 * <BR>
	 * chemin de CRL=[pki.crl.directory]/[valeur du champ 'O' de l'AC]/[valeur
	 * du champCN de l'AC].crl
	 * 
	 * 
	 * @param certificate
	 * @return
	 */
	@Deprecated
	public static String getCrlFileName(String pathName, X509CRL crl) {
		Map<ASN1ObjectIdentifier, String> map = X509Util.getInfosMap(crl
				.getIssuerX500Principal());
		String fname = map.get(X509Name.CN);
		fname = fname + CRL_EXTENSION;
		String orga = map.get(X509Name.O);
		return orga + File.separator + fname;
	}

	/**
	 * Construction chemin du fichier de crl.
	 * 
	 * <BR>
	 * chemin de CRL=[pki.crl.directory]/[valeur du champ 'O' de l'AC]/[valeur
	 * du champCN de l'AC].crl
	 * 
	 * 
	 * @param certificate
	 * @return
	 */
    @Deprecated
    public File getCrlFile(String pathName, X509CRL crl) {
		Map<ASN1ObjectIdentifier, String> map = X509Util.getInfosMap(crl
				.getIssuerX500Principal());
        //TODO (change with BCStyle.CN). With unit test
		String fname = map.get(X509Name.CN);
		fname = fname + CRL_EXTENSION;
		String orga = map.get(X509Name.O);
		File fpath = new File(pathName, orga);
		File crlFile = new File(fpath, fname);
		return crlFile;
	}

    /**
     * .
     *
     * <BR>
     *
     * @param certSign
     * @param crlValue
     * @return
     * @throws IllegalStateException
     * @throws CRLException
     */
    public X509CRL generateCrl(CertificateValue certSign, CrlValue crlValue, List<String> serialList)
            throws
            CRLException, IllegalStateException,
            OperatorCreationException, IOException {


        X509Certificate certificate = certSign.getCertificate();
        PrivateKey privateKey = (certSign.getPrivateKey());

        X500Name crlIssuer = X500Name.getInstance(certificate.getSubjectX500Principal().getEncoded());
        X500Name caName = X500Name.getInstance(certificate.getIssuerX500Principal().getEncoded());
        X509v2CRLBuilder builder = new X509v2CRLBuilder(crlIssuer,
                crlValue.getThisUpdate()
        );

        builder.setNextUpdate(crlValue.getNextUpdate());

        for (String serial : serialList) {
            BigInteger bigInt = new BigInteger(serial, 16);
            builder.addCRLEntry(bigInt, new Date(), org.bouncycastle.asn1.x509.CRLReason.privilegeWithdrawn);
        }
//		builder.addExtension(org.bouncycastle.asn1.x509.Extension.issuingDistributionPoint, true, new IssuingDistributionPoint(null, true, false));

//		ExtensionsGenerator extGen = new ExtensionsGenerator();
//
//		extGen.addExtension(org.bouncycastle.asn1.x509.Extension.reasonCode, false, org.bouncycastle.asn1.x509.CRLReason.lookup(org.bouncycastle.asn1.x509.CRLReason.cACompromise));
//		extGen.addExtension(org.bouncycastle.asn1.x509.Extension.certificateIssuer, true, new GeneralNames(new GeneralName(caName)));

        JcaContentSignerBuilder contentSignerBuilder =
                new JcaContentSignerBuilder("SHA256WithRSAEncryption");

        contentSignerBuilder.setProvider(provider);

        X509CRLHolder crlHolder = builder.build(contentSignerBuilder.build(privateKey));

        JcaX509CRLConverter converter = new JcaX509CRLConverter();

        converter.setProvider(provider);

        return converter.getCRL(crlHolder);


    }

    /**
     * .
     *
     * <BR>
     *
     * @param crl
     * @param crlFile
     * @throws IOException
     * @throws CRLException
     */
    public void saveCRL(X509CRL crl, String crlFile)
            throws CRLException, IOException {
        File f = new File(crlFile);
        if (f.getParentFile() != null && !f.getParentFile().exists())
            f.getParentFile().mkdirs();
        OutputStream output = new FileOutputStream(crlFile);
        IOUtils.write(crl.getEncoded(), output);
    }


}
