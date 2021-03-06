/*
 * @(#)ThreadGroupReferenceImpl.java	1.25 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.tools.jdi;

import com.sun.jdi.*;
import java.util.*;

public class ThreadGroupReferenceImpl extends ObjectReferenceImpl
    implements ThreadGroupReference, VMListener
{
    // Cached components that cannot change
    String name;
    ThreadGroupReference parent;
    boolean triedParent;

    // This is cached only while the VM is suspended
    private static class Cache extends ObjectReferenceImpl.Cache {
        JDWP.ThreadGroupReference.Children kids = null;
    }

    protected ObjectReferenceImpl.Cache newCache() {
        return new Cache();
    }

    ThreadGroupReferenceImpl(VirtualMachine aVm,long aRef) {
        super(aVm,aRef);
        vm.state().addListener(this);
    }

    protected String description() {
        return "ThreadGroupReference " + uniqueID();
    }

    public String name() {
        if (name == null) {
            // Does not need synchronization, since worst-case
            // static info is fetched twice (Thread group name 
            // cannot change)
            try {
                name = JDWP.ThreadGroupReference.Name.
                                     process(vm, this).groupName;
            } catch (JDWPException exc) {
                throw exc.toJDIException();
            }
        }
        return name;
    }

    public ThreadGroupReference parent() {
        if (!triedParent) {
            // Does not need synchronization, since worst-case
            // static info is fetched twice (Thread group parent cannot 
            // change)
            try {
                parent = JDWP.ThreadGroupReference.Parent.
                                 process(vm, this).parentGroup;
                triedParent = true;
            } catch (JDWPException exc) {
                throw exc.toJDIException();
            }
        }
       return parent;
    }

    public void suspend() {
        List threads = threads();
        Iterator iter = threads.iterator();
        while (iter.hasNext()) {
                ((ThreadReference)iter.next()).suspend();
        }

        List groups = threadGroups();
        iter = groups.iterator();
        while (iter.hasNext()) {
                ((ThreadGroupReference)iter.next()).suspend();
        }
    }

    public void resume() {
        List threads = threads();
        Iterator iter = threads.iterator();
        while (iter.hasNext()) {
                ((ThreadReference)iter.next()).resume();
        }

        List groups = threadGroups();
        iter = groups.iterator();
        while (iter.hasNext()) {
                ((ThreadGroupReference)iter.next()).resume();
        }
    }

    private JDWP.ThreadGroupReference.Children kids() {
        JDWP.ThreadGroupReference.Children kids = null;
        try {
            Cache local = (Cache)getCache();

            if (local != null) {
                kids = local.kids;
            }
            if (kids == null) {
                kids = JDWP.ThreadGroupReference.Children
                                                  .process(vm, this);
                if (local != null) {
                    local.kids = kids;
                    if ((vm.traceFlags & vm.TRACE_OBJREFS) != 0) {
                        vm.printTrace(description() + 
                                      " temporarily caching children ");
                    }
                }
            }
        } catch (JDWPException exc) {
            throw exc.toJDIException();
        }
        return kids;
    }

    public List threads() {
        return Arrays.asList(kids().childThreads);
    }

    public List threadGroups() {
        return Arrays.asList(kids().childGroups);
    }

    public String toString() {
        return "instance of " + referenceType().name() + 
               "(name='" + name() + "', " + "id=" + uniqueID() + ")";
    }

    byte typeValueKey() {
        return JDWP.Tag.THREAD_GROUP;
    }
}
