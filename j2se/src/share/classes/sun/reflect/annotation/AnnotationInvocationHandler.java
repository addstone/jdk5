/*
 * @(#)AnnotationInvocationHandler.java	1.7 04/05/03
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.reflect.annotation;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.io.Serializable;
import java.util.*;
import java.lang.annotation.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * InvocationHandler for dynamic proxy implementation of Annotation.
 *
 * @author  Josh Bloch
 * @since   1.5
 */
class AnnotationInvocationHandler implements InvocationHandler, Serializable {
    private final Class type;
    private final Map<String, Object> memberValues;

    AnnotationInvocationHandler(Class type, Map<String, Object> memberValues) {
        this.type = type;
        this.memberValues = memberValues;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        String member = method.getName();
        Class[] paramTypes = method.getParameterTypes();

        // Handle Object and Annotation methods
        if (member.equals("equals") && paramTypes.length == 1 &&
            paramTypes[0] == Object.class)
            return equalsImpl(args[0]);
        assert paramTypes.length == 0;
        if (member.equals("toString"))
            return toStringImpl();
        if (member.equals("hashCode"))
            return hashCodeImpl();
        if (member.equals("annotationType"))
            return type;

        // Handle annotation member accessors
        Object result = memberValues.get(member);

        if (result == null)
            throw new IncompleteAnnotationException(type, member);

        if (result instanceof ExceptionProxy)
            throw ((ExceptionProxy) result).generateException();

        if (result.getClass().isArray() && Array.getLength(result) != 0)
            result = cloneArray(result);

        return result;
    }

    /**
     * This method, which clones its array argument, would not be necessary
     * if Cloneable had a public clone method.
     */
    private Object cloneArray(Object array) {
        Class type = array.getClass();

        if (type == byte[].class) {
            byte[] byteArray = (byte[])array;
            return byteArray.clone();
        }
        if (type == char[].class) {
            char[] charArray = (char[])array;
            return charArray.clone();
        }
        if (type == double[].class) {
            double[] doubleArray = (double[])array;
            return doubleArray.clone();
        }
        if (type == float[].class) {
            float[] floatArray = (float[])array;
            return floatArray.clone();
        }
        if (type == int[].class) {
            int[] intArray = (int[])array;
            return intArray.clone();
        }
        if (type == long[].class) {
            long[] longArray = (long[])array;
            return longArray.clone();
        }
        if (type == short[].class) {
            short[] shortArray = (short[])array;
            return shortArray.clone();
        }
        if (type == boolean[].class) {
            boolean[] booleanArray = (boolean[])array;
            return booleanArray.clone();
        }
        
        Object[] objectArray = (Object[])array;
        return objectArray.clone();
    }


    /**
     * Implementation of dynamicProxy.toString()
     */
    private String toStringImpl() {
        StringBuffer result = new StringBuffer(128);
        result.append('@');
        result.append(type.getName());
        result.append('(');
        boolean firstMember = true;
        for (Map.Entry<String, Object> e : memberValues.entrySet()) {
            if (firstMember)
                firstMember = false;
            else
                result.append(", ");

            result.append(e.getKey());
            result.append('=');
            result.append(memberValueToString(e.getValue()));
        }
        result.append(')');
        return result.toString();
    }

    /**
     * Translates a member value (in "dynamic proxy return form") into a string
     */
    private static String memberValueToString(Object value) {
        Class type = value.getClass();
        if (!type.isArray())    // primitive, string, class, enum const,
                                // or annotation
            return value.toString();

        if (type == byte[].class)
            return Arrays.toString((byte[]) value);
        if (type == char[].class)
            return Arrays.toString((char[]) value);
        if (type == double[].class)
            return Arrays.toString((double[]) value);
        if (type == float[].class)
            return Arrays.toString((float[]) value);
        if (type == int[].class)
            return Arrays.toString((int[]) value);
        if (type == long[].class)
            return Arrays.toString((long[]) value);
        if (type == short[].class)
            return Arrays.toString((short[]) value);
        if (type == boolean[].class)
            return Arrays.toString((boolean[]) value);
        return Arrays.toString((Object[]) value);
    }

    /**
     * Implementation of dynamicProxy.equals(Object o)
     */
    private Boolean equalsImpl(Object o) {
        if (o == this)
            return true;

        if (!type.isInstance(o))
            return false;
        for (Method memberMethod : getMemberMethods()) {
            String member = memberMethod.getName();
            Object ourValue = memberValues.get(member);
            Object hisValue = null;
            AnnotationInvocationHandler hisHandler = asOneOfUs(o);
            if (hisHandler != null) {
                hisValue = hisHandler.memberValues.get(member);
            } else {
                try {
                    hisValue = memberMethod.invoke(o);
                } catch (InvocationTargetException e) {
                    return false;
                } catch (IllegalAccessException e) {
                    throw new AssertionError(e);
                }
            }
            if (!memberValueEquals(ourValue, hisValue))
                return false;
        }
        return true;
    }

    /**
     * Returns an object's invocation handler if that object is a dynamic
     * proxy with a handler of type AnnotationInvocationHandler.
     * Returns null otherwise.
     */
    private AnnotationInvocationHandler asOneOfUs(Object o) {
        if (Proxy.isProxyClass(o.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(o);
            if (handler instanceof AnnotationInvocationHandler)
                return (AnnotationInvocationHandler) handler;
        }
        return null;
    }

    /**
     * Returns true iff the two member values in "dynamic proxy return form"
     * are equal using the appropriate equality function depending on the
     * member type.  The two values will be of the same type unless one of
     * the containing annotations is ill-formed.  If one of the containing
     * annotations is ill-formed, this method will return false unless the
     * two members are identical object references.
     */
    private static boolean memberValueEquals(Object v1, Object v2) {
        Class type = v1.getClass();

        // Check for primitive, string, class, enum const, annotation,
        // or ExceptionProxy
        if (!type.isArray())
            return v1.equals(v2);

        // Check for array of string, class, enum const, annotation,
        // or ExceptionProxy
        if (v1 instanceof Object[] && v2 instanceof Object[])
            return Arrays.equals((Object[]) v1, (Object[]) v2);

        // Check for ill formed annotation(s)
        if (v2.getClass() != type)
            return false;

        // Deal with array of primitives
        if (type == byte[].class)
            return Arrays.equals((byte[]) v1, (byte[]) v2);
        if (type == char[].class)
            return Arrays.equals((char[]) v1, (char[]) v2);
        if (type == double[].class)
            return Arrays.equals((double[]) v1, (double[]) v2);
        if (type == float[].class)
            return Arrays.equals((float[]) v1, (float[]) v2);
        if (type == int[].class)
            return Arrays.equals((int[]) v1, (int[]) v2);
        if (type == long[].class)
            return Arrays.equals((long[]) v1, (long[]) v2);
        if (type == short[].class)
            return Arrays.equals((short[]) v1, (short[]) v2);
        assert type == boolean[].class;
        return Arrays.equals((boolean[]) v1, (boolean[]) v2);
    }

    /**
     * Returns the member methods for our annotation type.  These are
     * obtained lazily and cached, as they're expensive to obtain
     * and we only need them if our equals method is invoked (which should
     * be rare).
     */
    private Method[] getMemberMethods() {
        if (memberMethods == null) {
            final Method[] mm = type.getDeclaredMethods();
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    AccessibleObject.setAccessible(mm, true);
                    return null;
                }
            });
            memberMethods = mm;
        }
        return memberMethods;
    }
    private transient volatile Method[] memberMethods = null;

    /**
     * Implementation of dynamicProxy.hashCode()
     */
    private int hashCodeImpl() {
        int result = 0;
        for (Map.Entry<String, Object> e : memberValues.entrySet()) {
            result += (127 * e.getKey().hashCode()) ^
                memberValueHashCode(e.getValue());
        }
        return result;
    }

    /**
     * Computes hashCode of a member value (in "dynamic proxy return form")
     */
    private static int memberValueHashCode(Object value) {
        Class type = value.getClass();
        if (!type.isArray())    // primitive, string, class, enum const,
                                // or annotation
            return value.hashCode();

        if (type == byte[].class)
            return Arrays.hashCode((byte[]) value);
        if (type == char[].class)
            return Arrays.hashCode((char[]) value);
        if (type == double[].class)
            return Arrays.hashCode((double[]) value);
        if (type == float[].class)
            return Arrays.hashCode((float[]) value);
        if (type == int[].class)
            return Arrays.hashCode((int[]) value);
        if (type == long[].class)
            return Arrays.hashCode((long[]) value);
        if (type == short[].class)
            return Arrays.hashCode((short[]) value);
        if (type == boolean[].class)
            return Arrays.hashCode((boolean[]) value);
        return Arrays.hashCode((Object[]) value);
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
	s.defaultReadObject();


        // Check to make sure that types have not evolved incompatibly

        AnnotationType annotationType = null;
        try {
            annotationType = AnnotationType.getInstance(type);
        } catch(IllegalArgumentException e) {
            // Class is no longer an annotation type; all bets are off
            return;
        }

        Map<String, Class> memberTypes = annotationType.memberTypes();

        for (Map.Entry<String, Object> memberValue : memberValues.entrySet()) {
            String name = memberValue.getKey();
            Class memberType = memberTypes.get(name);
            if (memberType != null) {  // i.e. member still exists
                Object value = memberValue.getValue();
                if (!(memberType.isInstance(value) ||
                      value instanceof ExceptionProxy)) {
                    memberValue.setValue(
                        new AnnotationTypeMismatchExceptionProxy(
                            value.getClass() + "[" + value + "]").setMember(
                                annotationType.members().get(name)));
                }                                
            }
        }
    }
}
