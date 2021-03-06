/*
 * @(#)CRLReasonCodeExtension.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import sun.security.util.*;

/**
 * The reasonCode is a non-critical CRL entry extension that identifies
 * the reason for the certificate revocation. CAs are strongly
 * encouraged to include reason codes in CRL entries; however, the
 * reason code CRL entry extension should be absent instead of using the
 * unspecified (0) reasonCode value.
 * <p>The ASN.1 syntax for this is:
 * <pre>
 *  id-ce-cRLReason OBJECT IDENTIFIER ::= { id-ce 21 }
 *
 *  -- reasonCode ::= { CRLReason }
 *
 * CRLReason ::= ENUMERATED {
 *    unspecified             (0),
 *    keyCompromise           (1),
 *    cACompromise            (2),
 *    affiliationChanged      (3),
 *    superseded              (4),
 *    cessationOfOperation    (5),
 *    certificateHold         (6),
 *    removeFromCRL           (8),
 *    privilegeWithdrawn      (9),
 *    aACompromise           (10) }
 * </pre>
 * @author Hemma Prafullchandra
 * @version 1.15
 * @see Extension
 * @see CertAttrSet
 */
public class CRLReasonCodeExtension extends Extension implements CertAttrSet {

    /**
     * Attribute name and Reason codes
     */
    public static final String NAME = "CRLReasonCode";
    public static final String REASON = "reason";

    public static final int UNSPECIFIED = 0;
    public static final int KEY_COMPROMISE = 1;
    public static final int CA_COMPROMISE = 2;
    public static final int AFFLIATION_CHANGED = 3;
    public static final int SUPERSEDED = 4;
    public static final int CESSATION_OF_OPERATION = 5;
    public static final int CERTIFICATE_HOLD = 6;
    // note 7 missing in syntax
    public static final int REMOVE_FROM_CRL = 8;
    public static final int PRIVILEGE_WITHDRAWN = 9;
    public static final int AA_COMPROMISE = 10;

    private int reasonCode = 0;

    private void encodeThis() throws IOException {
        if (reasonCode == 0) {
            this.extensionValue = null;
            return;
        }
        DerOutputStream dos = new DerOutputStream();
        dos.putEnumerated(reasonCode);
        this.extensionValue = dos.toByteArray();
    }

    /**
     * Create a CRLReasonCodeExtension with the passed in reason.
     * Criticality automatically set to false.
     *
     * @param reason the enumerated value for the reason code.
     */
    public CRLReasonCodeExtension(int reason) throws IOException {
        reasonCode = reason;
        this.extensionId = PKIXExtensions.ReasonCode_Id;
        this.critical = false;
        encodeThis();
    }

    /**
     * Create a CRLReasonCodeExtension with the passed in reason.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param reason the enumerated value for the reason code.
     */
    public CRLReasonCodeExtension(boolean critical, int reason)
    throws IOException {
        this.extensionId = PKIXExtensions.ReasonCode_Id;
        this.critical = critical;
        this.reasonCode = reason;
        encodeThis();
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value an array of DER encoded bytes of the actual value.
     * @exception ClassCastException if value is not an arry of bytes
     * @exception IOException on error.
     */
    public CRLReasonCodeExtension(Boolean critical, Object value)
    throws IOException {
	    this.extensionId = PKIXExtensions.ReasonCode_Id;
	    this.critical = critical.booleanValue();
	    this.extensionValue = (byte[]) value;
	    DerValue val = new DerValue(this.extensionValue);
	    this.reasonCode = val.getEnumerated();
    }

    /**
     * Set the attribute value.
     */
    public void set(String name, Object obj) throws IOException {
        if (!(obj instanceof Integer)) {
	    throw new IOException("Attribute must be of type Integer.");
	}
	if (name.equalsIgnoreCase(REASON)) {
            reasonCode = ((Integer)obj).intValue();
	} else {
	    throw new IOException
		("Name not supported by CRLReasonCodeExtension");
	}
        encodeThis();
    }

    /**
     * Get the attribute value.
     */
    public Object get(String name) throws IOException {
	if (name.equalsIgnoreCase(REASON)) {
	    return new Integer(reasonCode);
	} else {
	    throw new IOException
		("Name not supported by CRLReasonCodeExtension");
	}
    }

    /**
     * Delete the attribute value.
     */
    public void delete(String name) throws IOException {
	if (name.equalsIgnoreCase(REASON)) {
	    reasonCode = 0;
	} else {
	    throw new IOException
		("Name not supported by CRLReasonCodeExtension");
	}
        encodeThis();
    }

    /**
     * Returns a printable representation of the Reason code.
     */
    public String toString() {
        String s = super.toString() + "    Reason Code: ";

        switch (reasonCode) {
        case UNSPECIFIED: s += "Unspecified"; break;
        case KEY_COMPROMISE: s += "Key Compromise"; break;
        case CA_COMPROMISE: s += "CA Compromise"; break;
        case AFFLIATION_CHANGED: s += "Affiliation Changed"; break;
        case SUPERSEDED: s += "Superseded"; break;
        case CESSATION_OF_OPERATION: s += "Cessation Of Operation"; break;
        case CERTIFICATE_HOLD: s += "Certificate Hold"; break;
        case REMOVE_FROM_CRL: s += "Remove from CRL"; break;
        case PRIVILEGE_WITHDRAWN: s += "Privilege Withdrawn"; break;
        case AA_COMPROMISE: s += "AA Compromise"; break;
        default: s += "Unrecognized reason code (" + reasonCode + ")";
        }
        return (s);
    }

    /**
     * Write the extension to the DerOutputStream.
     *
     * @param out the DerOutputStream to write the extension to.
     * @exception IOException on encoding errors.
     */
    public void encode(OutputStream out) throws IOException {
	DerOutputStream  tmp = new DerOutputStream();

	if (this.extensionValue == null) {
	    this.extensionId = PKIXExtensions.ReasonCode_Id;
	    this.critical = false;
	    encodeThis();
	}
	super.encode(tmp);
	out.write(tmp.toByteArray());
    }

    /**
     * Return an enumeration of names of attributes existing within this
     * attribute.
     */
    public Enumeration getElements() {
        AttributeNameEnumeration elements = new AttributeNameEnumeration();
        elements.addElement(REASON);

	return (elements.elements());
    }

    /**
     * Return the name of this attribute.
     */
    public String getName() {
        return (NAME);
    }
}
