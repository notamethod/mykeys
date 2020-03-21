package org.dpr.mykeys.app.utils;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PolicyQualifierId extends ASN1ObjectIdentifier {
    private static final String id_qt = "1.3.6.1.5.5.7.2";
    public static final PolicyQualifierId id_qt_cps = new PolicyQualifierId("1.3.6.1.5.5.7.2.1");
    public static final PolicyQualifierId id_qt_unotice = new PolicyQualifierId("1.3.6.1.5.5.7.2.2");
    public static final PolicyQualifierId id_dv = new PolicyQualifierId("2.23.140.1.2.1");
    public static final PolicyQualifierId id_ev = new PolicyQualifierId("2.23.140.1.1");
    public static final PolicyQualifierId id_evssl_globalsign = new PolicyQualifierId("1.3.6.1.4.1.4146.1.1");
    public static final PolicyQualifierId id_ov = new PolicyQualifierId("2.23.140.1.2.2");


    PolicyQualifierId(String var1) {
        super(var1);
    }
}
