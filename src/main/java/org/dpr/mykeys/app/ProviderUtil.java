package org.dpr.mykeys.app;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderUtil {
	private static List<String> KeyPairGeneratorList;



	final static Log log = LogFactory.getLog(ProviderUtil.class);

	public static List<String> SignatureList;

	public static Provider provider;

	public static void main(String[] args) {

		Hashtable t1 = X509Name.DefaultSymbols;
		Hashtable t2 = X509Name.DefaultLookUp;
		initBC();
	}

	public static void init() {
		initServices();
	}

	public static void init(String p) {
		provider = Security.getProvider(p);
		initServices();
	}

	public static void initBC() {
		Security.addProvider(new BouncyCastleProvider());
		provider = Security.getProvider("BC");
		initServices();
	}

	private static void initServices() {
		initKeyPairGeneratorList();
		initSignaturesList();
	}

	public static void initKeyPairGeneratorList() {
		KeyPairGeneratorList = new ArrayList<String>();
		Iterator<Object> it = provider.keySet().iterator();
		while (it.hasNext()) {
			String entry = (String) it.next();
			if (entry.startsWith("KeyPairGenerator.")) {
				KeyPairGeneratorList.add(entry.substring("KeyPairGenerator."
						.length()));
			}
		}
		Collections.sort(KeyPairGeneratorList);

		printList("setKeyPairGeneratorList", KeyPairGeneratorList);
	}

	public static void initSignaturesList() {
		SignatureList = new ArrayList<String>();
		Iterator<Object> it = provider.keySet().iterator();
		while (it.hasNext()) {
			String entry = (String) it.next();
			if (entry.startsWith("Signature.")) {
				SignatureList.add(entry.substring("Signature.".length()));
			}
		}
		Collections.sort(SignatureList);
		printList("setSignatureList", SignatureList);
	}

	public static void printList(String setName, List algorithms) {
		if (log.isDebugEnabled()) {
			log.debug(setName + ":");
			if (algorithms.isEmpty()) {
				log.debug("            None available.");
			} else {
				Iterator it = algorithms.iterator();
				while (it.hasNext()) {
					String name = (String) it.next();

					log.debug("            " + name);
				}
			}
		}
	}
	
	public static List<String> getKeyPairGeneratorList() {
		if (KeyPairGeneratorList==null){
		Security.addProvider(new BouncyCastleProvider());
		ProviderUtil.init("BC");
		}
				return KeyPairGeneratorList;
	}
}
