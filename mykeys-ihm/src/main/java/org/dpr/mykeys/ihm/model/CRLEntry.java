package org.dpr.mykeys.ihm.model;

import org.dpr.mykeys.app.certificate.CertificateValue;

import java.math.BigInteger;
import java.util.Date;

public class CRLEntry {
    private BigInteger serialNumber;
    private String subject;
    private Date revocationDate;
    private Object revocationReason;

    public CRLEntry(CertificateValue value) {
        serialNumber = value.getCertificate().getSerialNumber();
        subject = value.getSubjectString();
        revocationDate = new Date();
        revocationReason = 1;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Object getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Object getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public Object getRevocationReason() {

        return revocationReason;
    }

    public void setRevocationReason(Object revocationReason) {
        this.revocationReason = revocationReason;
    }
}
