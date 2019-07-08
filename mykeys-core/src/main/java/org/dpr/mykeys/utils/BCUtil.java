package org.dpr.mykeys.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.KeyPurposeId;

import java.util.HashMap;
import java.util.Map;

public class BCUtil {

    protected static final Log log = LogFactory.getLog(BCUtil.class);


    public static final Map<ASN1ObjectIdentifier, String> extendedKeyUsages = new HashMap() {{
        put(KeyPurposeId.id_kp_clientAuth, "eku.clientAuth");
        put(KeyPurposeId.id_kp_codeSigning, "eku.codeSigning");
        put(KeyPurposeId.id_kp_serverAuth, "eku.serverAuth");

    }};


}
