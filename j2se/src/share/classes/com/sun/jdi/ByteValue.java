/*
 * @(#)ByteValue.java	1.14 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jdi;

/**
 * Provides access to a primitive <code>byte</code> value in the target VM.
 *
 * @author Robert Field
 * @author Gordon Hirsch
 * @author James McIlree
 * @since  1.3
 */
public interface ByteValue extends PrimitiveValue, Comparable<ByteValue>
{
    /**
     * Returns this ByteValue as a <code>byte</code>.
     * 
     * @return the <code>byte</code> mirrored by this object.
     */
    byte value();

    /**
     * Compares the specified Object with this ByteValue for equality.
     *
     * @return true if the Object is a ByteValue and if applying "=="
     * to the two mirrored primitives would evaluate to true; false
     * otherwise.
     */
    boolean equals(Object obj);

    /**
     * Returns the hash code value for this BooleanValue.
     *
     * @return the integer hash code
     */
    int hashCode();
}

