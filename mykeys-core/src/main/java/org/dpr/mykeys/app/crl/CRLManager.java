package org.dpr.mykeys.app.crl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.X509Util;


/**
 * @author Buck
 *
 */
public class CRLManager {

	private static final Log log = LogFactory.getLog(CRLManager.class);

	private static final String CRL_EXTENSION = ".crl";

	private String provider = null;

	/**
	 * . <BR>
	 * 
	 * 
	 * @param securityProvider
	 */
    private CRLManager(String securityProvider) {
		provider = securityProvider;
	}

	/**
	 * Récupération des points de distribution des CRL.
	 * 
	 * <BR>
	 * 
	 * 
	 * @param certX509
	 * @param uriMap
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

		CRLDistPoint distPoints = null;
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

		DistributionPoint[] pointsDistrib = null;
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
	 * @param crlFile
	 *            : fichier de CRL
	 * @param cert
	 * @return
	 * @throws CertificateException
	 * @throws NoSuchProviderException
	 * @throws CRLException
	 */
	public EtatRevocation validateCertificate(InputStream inStream,
			X509Certificate cert) throws GeneralSecurityException {
		X509CRL crl = null;
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

		InputStream inStream = null;
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
		EtatCrl etatCrl = EtatCrl.UNKNOWN;
		X509CRL crl = null;

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
	 * @param x509Certificate
	 * @throws FileNotFoundException
	 * @throws NoSuchProviderException
	 * @throws CRLException
	 * @throws CertificateException
	 */
    private X509CRL getCrl(InputStream inStream) throws CRLException,
			NoSuchProviderException, CertificateException {

		CertificateFactory cf = null;

		cf = CertificateFactory.getInstance("X.509", provider);

		X509CRL crl = (X509CRL) cf.generateCRL(inStream);
		if (log.isDebugEnabled()) {
			log.debug("CRL issuer     -->" + crl.getIssuerDN()
					+ "Effective From -->" + crl.getThisUpdate()
					+ "Next update    -->" + crl.getNextUpdate());
		}
		return crl;
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
	public File getCrlFile(String pathName, X509Certificate certificate,
			boolean isAC) {

		Map<ASN1ObjectIdentifier, String> map = null;
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
	public static String getCrlFileName(X509Certificate certificate,
			boolean isAC) {

		Map<ASN1ObjectIdentifier, String> map = null;
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
	public File getCrlFile(String pathName, X509CRL crl) {
		Map<ASN1ObjectIdentifier, String> map = X509Util.getInfosMap(crl
				.getIssuerX500Principal());
		String fname = map.get(X509Name.CN);
		fname = fname + CRL_EXTENSION;
		String orga = map.get(X509Name.O);
		File fpath = new File(pathName, orga);
		File crlFile = new File(fpath, fname);
		return crlFile;
	}
}
