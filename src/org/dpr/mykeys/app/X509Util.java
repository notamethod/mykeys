package org.dpr.mykeys.app;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

public class X509Util {

	static Map<String, String> mapNames = null;
	final static Log log = LogFactory.getLog(X509Util.class);

	/**
	 * @return the mapNames
	 */
	public static Map<String, String> getMapNames() {
		if (mapNames == null) {
			mapNames = new HashMap<String, String>();
			mapNames.put("C", "x509.subject.country");
			mapNames.put("O", "x509.subject.organisation");
			mapNames.put("OU", "x509.subject.organisationUnit");
			mapNames.put("L", "x509.subject.location");
			mapNames.put("ST", "x509.subject.street");
			mapNames.put("E", "x509.subject.email");
			mapNames.put("CN", "x509.subject.name");

		}
		return mapNames;
	}

	public static String toHexString(byte[] b, String separator,
			boolean upperCase) {
		String retour = "";
		char[] car = Hex.encodeHex(b);
		for (int i = 0; i < car.length; i = i + 2) {
			retour += String.valueOf(car[i]);
			retour += String.valueOf(car[i + 1]);
			retour += separator;
		}
		if (upperCase) {
			return retour.toUpperCase();
		} else {
			return retour.toLowerCase();
		}
	}

	public static void getExtensions(X509Certificate certificate) {
		// byte[] b =
		// certificate.getExtensionValue(X509Extensions.AuditIdentity.getId());
		// ASN1Object obj = X509ExtensionUtil.fromExtensionValue(b);
		byte[] b = certificate.getExtensionValue(X509Extensions.KeyUsage
				.getId());
		ASN1Object obj = null;
		try {
			obj = X509ExtensionUtil.fromExtensionValue(b);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}

		}

		if (log.isDebugEnabled()) {
			log.debug(obj);
		}
		b = certificate.getExtensionValue(X509Extensions.SubjectKeyIdentifier
				.getId());
		obj = null;

		// try {
		// obj = X509ExtensionUtil.fromExtensionValue(b);
		// SubjectKeyIdentifier sk = new
		// SubjectKeyIdentifier(obj.getDEREncoded());
		// SubjectKeyIdentifier ski =
		// SubjectKeyIdentifier.getInstance(extIn.readObject());
		// String skiString = Utils.toHexString(ski.getKeyIdentifier(), " ",
		// true);
		// this.setSubjectKeyIdentifier(skiString);
		// } catch (Exception e) {
		// System.out.println("exc");
		//
		// }
		// System.out.println(obj);
		// AuditIdentity
		// AuthorityInfoAccess
		// AuthorityKeyIdentifier
		// BasicConstraints
		// BiometricInfo
		// CertificateIssuer
		// CertificatePolicies
		// CRLDistributionPoints
		// CRLNumber
		// DeltaCRLIndicator
		// ExtendedKeyUsage
		// FreshestCRL
		// InhibitAnyPolicy
		// InstructionCode
		// InvalidityDate
		// IssuerAlternativeName
		// IssuingDistributionPoint
		// KeyUsage
		// LogoType
		// NameConstraints
		// NoRevAvail
		// PolicyConstraints
		// PolicyMappings
		// PrivateKeyUsagePeriod
		// QCStatements
		// ReasonCode
		// SubjectAlternativeName
		// SubjectDirectoryAttributes
		// SubjectInfoAccess
		// SubjectKeyIdentifier
		// TargetInformation
	}

	// public static String BiToHex(BigInteger bi) {
	//
	// System.out.println("max long   = " + Long.toHexString(Long.MAX_VALUE));
	// System.out.println("bi as long = " + Long.toHexString(bi.longValue()));
	// System.out.println("bi as hex  = " + bi.toString(16));
	// }

	public static Map<DERObjectIdentifier, String> getSubjectMap(
			X509Certificate x509Certificate) {
		X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
		return getInfosMap(x500Principal);
	}

	public static Map<DERObjectIdentifier, String> getIssuerMap(
			X509Certificate x509Certificate) {
		X500Principal x500Principal = x509Certificate.getIssuerX500Principal();
		return getInfosMap(x500Principal);
	}

	public static Map<DERObjectIdentifier, String> getInfosMap(
			X500Principal x500Principal) {
		Map<DERObjectIdentifier, String> subjectMap = new HashMap<DERObjectIdentifier, String>();
		if (x500Principal == null) {
			return subjectMap;
		}
		String principalName = x500Principal.getName();
		if (StringUtils.isBlank(principalName)) {
			return subjectMap;
		}
		X509Name x509Name = new X509Name(principalName);
		Vector<DERObjectIdentifier> v = x509Name.getOIDs();
		for (int i = 0; i < v.size(); i++) {
			DERObjectIdentifier oid = v.get(i);
			String val = (String) x509Name.getValues().get(i);
			subjectMap.put(oid, val);
		}
		return subjectMap;
	}

}
