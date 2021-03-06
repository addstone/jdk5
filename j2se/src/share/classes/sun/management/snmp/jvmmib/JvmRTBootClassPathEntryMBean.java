/*
 * @(#)JvmRTBootClassPathEntryMBean.java	1.4 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management.snmp.jvmmib;

//
// Generated by mibgen version 5.0 (06/02/03) when compiling JVM-MANAGEMENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.jmx.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "JvmRTBootClassPathEntry" MBean.
 */
public interface JvmRTBootClassPathEntryMBean {

    /**
     * Getter for the "JvmRTBootClassPathItem" variable.
     */
    public String getJvmRTBootClassPathItem() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTBootClassPathIndex" variable.
     */
    public Integer getJvmRTBootClassPathIndex() throws SnmpStatusException;

}
