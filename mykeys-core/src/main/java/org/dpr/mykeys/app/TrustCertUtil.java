/**
 * 
 */
package org.dpr.mykeys.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Buck
 *
 */
public class TrustCertUtil {

	public static final String FILTRE_CERTIFICAT_X509 = "*.CER";
	public static final String X509_CERTIFICATE_TYPE = "X.509";
	private static final Log log = LogFactory.getLog(TrustCertUtil.class);

	/**
	 * .
	 *
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static X509Certificate[] getTrustedCerts(String repertoireAC,
			String provider) throws GeneralSecurityException {
		String typeCert = X509_CERTIFICATE_TYPE;
		X509Certificate[] trustedCerts = null;
		// Chargement de la liste des certificats de confiance
		try {
			Set<X509Certificate> certs = listerCertificats(repertoireAC,
					typeCert, provider);
			trustedCerts = new X509Certificate[certs.size()];
			int i = 0;
			for (X509Certificate certificat : certs) {
				trustedCerts[i++] = certificat;
			}
		} catch (IOException ioe) {
			throw new GeneralSecurityException(
					"Problème de lecture des certificats de confiance de "
							+ repertoireAC, ioe);
		}

		return trustedCerts;
	}

	/**
	 * Récupération des AC reconnues à partir d'un keystore.
	 *
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static X509Certificate[] getTrustedCerts(KeyStore ks, String provider)
			throws GeneralSecurityException {
		Enumeration<String> en = ks.aliases();
		Set<X509Certificate> lstCerts = new HashSet<X509Certificate>();
		while (en.hasMoreElements()) {
			String alias = en.nextElement();
			lstCerts.add((X509Certificate) ks.getCertificate(alias));
		}
		X509Certificate[] trustedCerts = new X509Certificate[lstCerts.size()];
		int i = 0;
		for (X509Certificate cert : lstCerts) {
			trustedCerts[i++] = cert;
		}

		return trustedCerts;
	}

	/**
	 * Concatene des fichiers .cer dans un fichier unique, en supprimant les
	 * doublons.
	 *
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static void concatCerts(String srcPath, File destFile,
			String provider) throws GeneralSecurityException, IOException {
		String typeCert = X509_CERTIFICATE_TYPE;
		// Chargement de la liste des certificats de confiance

		Set<X509Certificate> certs = listerCertificats(srcPath, typeCert,
				provider);
		try {
			InputStream certStream = new FileInputStream(destFile);
			// remarque: un fichier .cer peut contenir plus d'un certificat
			Collection<X509Certificate> trustedCerts2 = chargerCertificatsX509(
					certStream, typeCert, provider);
			IOUtils.closeQuietly(certStream);
			certs.addAll(trustedCerts2);
		} catch (IOException ioe) {
			// fichier vide
		}

		OutputStream output = new FileOutputStream(destFile);

		for (X509Certificate certificat : certs) {
			output.write(certificat.getEncoded());
		}
		output.close();

	}

	/**
	 * Vérification chaine de certificats.
	 *
	 * 
	 * @param password
	 * @param anchors
	 * @param certs
	 * @param crls
	 * @throws CertPathValidatorException
	 *             si le chemin de certification n'est pas valide
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CertPathBuilderException
	 * @throws NoSuchProviderException
	 */
	protected static void checkTrusted(X509Certificate[] anchors,
			Certificate[] certs, Collection<?> crls, String provider,
			boolean isCheckCrl) throws CertPathValidatorException,
			NoSuchAlgorithmException, CertificateException,
			InvalidAlgorithmParameterException, NoSuchProviderException {

		/* Construct a valid path. */
		List<TrustAnchor> listAnchors = new ArrayList<TrustAnchor>();

		for (X509Certificate cert : anchors) {
			TrustAnchor ta = new TrustAnchor(cert, null);
			listAnchors.add(ta);
		}

		Set anchorSet = new HashSet(listAnchors);
		List<X509Certificate> lstChaine = new ArrayList<X509Certificate>();
		for (Certificate cc0 : certs) {
			lstChaine.add((X509Certificate) cc0);
		}
		CollectionCertStoreParameters params = new CollectionCertStoreParameters(
				lstChaine);
		CertStore store = CertStore.getInstance("Collection", params, provider);

		CertStore crlStore = null;
		if (isCheckCrl) {
			CollectionCertStoreParameters revoked = new CollectionCertStoreParameters(
					crls);
			crlStore = CertStore.getInstance("Collection", revoked, provider);
		}

		// create certificate path
		CertificateFactory factory = CertificateFactory.getInstance("X.509",
				provider);
		List certChain = new ArrayList();

		certChain.add(lstChaine.get(0));
		// certChain.add(interCert);

		CertPath certPath = factory.generateCertPath(certChain);
		Set trust = anchorSet;// Collections.singleton(new TrustAnchor(rootCert,
								// null));
		// perform validation
		CertPathValidator validator = CertPathValidator.getInstance("PKIX",
				provider);
		PKIXParameters param = new PKIXParameters(trust);

		param.addCertStore(store);
		param.setDate(new Date());

		if (isCheckCrl) {
			param.addCertStore(crlStore);
			param.setRevocationEnabled(true);
		} else {
			param.setRevocationEnabled(false);
		}

		// CertPathValidatorResult result = validator.validate(certPath, param);
		validator.validate(certPath, param);
		if (log.isInfoEnabled()) {
			log.info("certificate path validated");
		}
	}

	protected static Set<X509Certificate> listerCertificats(
			String aCertificatesDirectory, String typeCert, String provider,
			boolean recursive) throws IOException, GeneralSecurityException {
		List<X509Certificate> lstCert = new ArrayList<X509Certificate>();
		// Set<X509Certificate> lstCert = new HashSet<X509Certificate>();
		// recherche des certificats dans le répertoire (*.cer ou *.CER)

		IOFileFilter fileFilter = new WildcardFileFilter(
				FILTRE_CERTIFICAT_X509, IOCase.INSENSITIVE);

		IOFileFilter dirFilter = recursive ? TrueFileFilter.INSTANCE : null;
		Collection<File> lstFichiers = FileUtils.listFiles(new File(
				aCertificatesDirectory), fileFilter, dirFilter);

		if (lstFichiers != null) {
			// boucle sur les certificats trouvés
			for (File fichier : lstFichiers) {
				InputStream certStream = new FileInputStream(fichier);
				// remarque: un fichier .cer peut contenir plus d'un certificat
				Collection<X509Certificate> trustedCerts = chargerCertificatsX509(
						certStream, typeCert, provider);
				IOUtils.closeQuietly(certStream);
				lstCert.addAll(trustedCerts);
			}
		}
		Set<X509Certificate> trustedCertificates = new HashSet<X509Certificate>(
				lstCert);
		return trustedCertificates;
	}

	protected static Set<X509Certificate> listerCertificats(
			String aCertificatesDirectory, String typeCert, String provider)
			throws IOException, GeneralSecurityException {
		return listerCertificats(aCertificatesDirectory, typeCert, provider,
				false);
	}

	/**
	 * Récupère une liste de certificats à partir d'un fichier .cer.
	 *
	 * 
	 * @param aCertStream
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static Collection<X509Certificate> chargerCertificatsX509(
			InputStream aCertStream, String typeCert, String provider)
			throws GeneralSecurityException {
		// création d'une fabrique de certificat X509
		CertificateFactory cf = CertificateFactory.getInstance(typeCert,
				provider);

		// chargement du certificat
		Collection<X509Certificate> certs = (Collection<X509Certificate>) cf
				.generateCertificates(aCertStream);
		return certs;
	}

	/**
	 * Vérifie le chemin de certification d'un certificat.

	 * 
	 * @param trusted
	 *            : liste des certificats reconnus
	 * @param certs
	 *            : chaîne de certification du certificat à contrôler
	 * @throws IOException
	 */
	public static void validate(X509Certificate[] trusted, Certificate[] certs,
			String provider) throws CertPathValidatorException,
			GeneralSecurityException {
		checkTrusted(trusted, certs, null, provider, false);
	}

	/**
	 * Récupère les AC reconnues à partir d'un Stream.
	 *
	 * 
	 * @param is
	 *            le inputStream à lire
	 * @param securityProvider
	 * @throws GeneralSecurityException
	 */
	public static X509Certificate[] getTrustedCerts(InputStream certStream,
			String securityProvider) throws GeneralSecurityException {
		String typeCert = X509_CERTIFICATE_TYPE;
		Collection<X509Certificate> trustedCerts = chargerCertificatsX509(
				certStream, typeCert, securityProvider);
		// suppression des doublons
		Set<X509Certificate> trustedCertificates = new HashSet<X509Certificate>(
				trustedCerts);
		X509Certificate[] certsArray = null;
		// Chargement de la liste des certificats de confiance

		certsArray = new X509Certificate[trustedCertificates.size()];
		int i = 0;
		for (X509Certificate certificat : trustedCerts) {
			certsArray[i++] = certificat;
		}
		return certsArray;
	}

	/**
	 * .
	 *
	 * 
	 * @param repertoireAC
	 * @param provider
	 * @return
	 */
	public static X509Certificate[] getAllTrustedCerts(String repertoireAC,
			String provider) throws GeneralSecurityException {
		String typeCert = X509_CERTIFICATE_TYPE;
		X509Certificate[] trustedCerts = null;
		// Chargement de la liste des certificats de confiance
		try {
			Set<X509Certificate> certs = listerCertificats(repertoireAC,
					typeCert, provider, true);
			trustedCerts = new X509Certificate[certs.size()];
			int i = 0;
			for (X509Certificate certificat : certs) {
				trustedCerts[i++] = certificat;
			}
		} catch (IOException ioe) {
			throw new GeneralSecurityException(
					"Problème de lecture des certificats de confiance de "
							+ repertoireAC, ioe);
		}

		return trustedCerts;
	}

}
