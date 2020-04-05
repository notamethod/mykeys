package org.dpr.mykeys.app.keystore;

public enum PEMType {
    CERTIFICATE("-----BEGIN CERTIFICATE-----","-----END CERTIFICATE-----"),
    REQUEST("-----BEGIN CERTIFICATE REQUEST-----", "-----END CERTIFICATE REQUEST-----"),
    PRIVATE_KEY("-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----");

    private String begin;
    private String end;

    PEMType(String begin, String end) {
        this.begin=begin;
        this.end=end;
    }

    public String Begin(){
        return begin;
    }

    public String End(){
        return end;
    }
}
