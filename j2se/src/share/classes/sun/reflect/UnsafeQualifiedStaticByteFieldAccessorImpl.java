/*
 * @(#)UnsafeQualifiedStaticByteFieldAccessorImpl.java	1.1 04/05/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedStaticByteFieldAccessorImpl
    extends UnsafeQualifiedStaticFieldAccessorImpl
{
    UnsafeQualifiedStaticByteFieldAccessorImpl(Field field, boolean isReadOnly) {
        super(field, isReadOnly);
    }

    public Object get(Object obj) throws IllegalArgumentException {
        return new Byte(getByte(obj));
    }

    public boolean getBoolean(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public byte getByte(Object obj) throws IllegalArgumentException {
        return unsafe.getByteVolatile(base, fieldOffset);
    }

    public char getChar(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public short getShort(Object obj) throws IllegalArgumentException {
        return getByte(obj);
    }

    public int getInt(Object obj) throws IllegalArgumentException {
        return getByte(obj);
    }

    public long getLong(Object obj) throws IllegalArgumentException {
        return getByte(obj);
    }

    public float getFloat(Object obj) throws IllegalArgumentException {
        return getByte(obj);
    }

    public double getDouble(Object obj) throws IllegalArgumentException {
        return getByte(obj);
    }

    public void set(Object obj, Object value)
        throws IllegalArgumentException, IllegalAccessException
    {
        if (isReadOnly) {
            throw new IllegalAccessException("Field is final");
        }
        if (value == null) {
            throw new IllegalArgumentException();
        }
        if (value instanceof Byte) {
            unsafe.putByteVolatile(base, fieldOffset, ((Byte) value).byteValue());
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setBoolean(Object obj, boolean z)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setByte(Object obj, byte b)
        throws IllegalArgumentException, IllegalAccessException
    {
        if (isReadOnly) {
            throw new IllegalAccessException("Field is final");
        }
        unsafe.putByteVolatile(base, fieldOffset, b);
    }

    public void setChar(Object obj, char c)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setShort(Object obj, short s)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setInt(Object obj, int i)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setLong(Object obj, long l)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setFloat(Object obj, float f)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setDouble(Object obj, double d)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }
}
