/*
 * @(#)QueryEval.java	4.24 04/02/10
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;


// java import
import java.io.Serializable;

// RI import
import javax.management.MBeanServer;


/**
 * Allows a query to be performed in the context of a specific MBean server.
 *
 * @since 1.5
 */
public abstract class QueryEval implements Serializable   { 
    
    /* Serial version */
    private static final long serialVersionUID = 2675899265640874796L;

    private static ThreadLocal server = new InheritableThreadLocal();
    
    /**
     * <p>Sets the MBean server on which the query is to be performed.
     * The setting is valid for the thread performing the set.
     * It is copied to any threads created by that thread at the moment
     * of their creation.</p>
     *
     * <p>For historical reasons, this method is not static, but its
     * behavior does not depend on the instance on which it is
     * called.</p>
     *
     * @param s The MBean server on which the query is to be performed.
     *
     * @see #getMBeanServer
     */
    public void setMBeanServer(MBeanServer s) {
	server.set(s);
    }

    /**
     * <p>Return the MBean server that was most recently given to the
     * {@link #setMBeanServer setMBeanServer} method by this thread.
     * If this thread never called that method, the result is the
     * value its parent thread would have obtained from
     * <code>getMBeanServer</code> at the moment of the creation of
     * this thread, or null if there is no parent thread.</p>
     *
     * @return the MBean server.
     *
     * @see #setMBeanServer
     *
     * @since.unbundled JMX 1.2
     */
    public static MBeanServer getMBeanServer() {
	return (MBeanServer) server.get();
    }
}
