/*
 * @(#)StubFactoryStaticImpl.java	1.4 04/04/12
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.presentation.rmi;

import java.lang.reflect.InvocationHandler ;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public class StubFactoryStaticImpl extends StubFactoryBase
{
    private Class stubClass ;

    public StubFactoryStaticImpl(Class cls)
    {
	super( null ) ;
	this.stubClass = cls;
    }
    
    public org.omg.CORBA.Object makeStub()
    {
	org.omg.CORBA.Object stub = null;
	try {
	    stub = (org.omg.CORBA.Object) stubClass.newInstance();
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
	return stub ;
    }
}
