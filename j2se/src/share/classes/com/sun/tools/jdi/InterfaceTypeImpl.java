/*
 * @(#)InterfaceTypeImpl.java	1.27 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.tools.jdi;

import com.sun.jdi.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.lang.ref.SoftReference;

public class InterfaceTypeImpl extends ReferenceTypeImpl
                               implements InterfaceType {

    private SoftReference superinterfacesRef = null;

    protected InterfaceTypeImpl(VirtualMachine aVm,long aRef) {
        super(aVm, aRef);
    }

    public List superinterfaces() {
        List superinterfaces = (superinterfacesRef == null) ? null :
                                     (List)superinterfacesRef.get();
        if (superinterfaces == null) {
            superinterfaces = getInterfaces();
            superinterfaces = Collections.unmodifiableList(superinterfaces);
            superinterfacesRef = new SoftReference(superinterfaces);
        }
        return superinterfaces;
    }

    public List subinterfaces() {
        List all = vm.allClasses();
        List subs = new ArrayList();
        Iterator iter = all.iterator();
        while (iter.hasNext()) {
            ReferenceType refType = (ReferenceType)iter.next();
            if (refType instanceof InterfaceType) {
                InterfaceType interfaze = (InterfaceType)refType;
                if (interfaze.isPrepared() && interfaze.superinterfaces().contains(this)) {
                    subs.add(interfaze);
                }
            }
        }

        return subs;
    }

    public List implementors() {
        List all = vm.allClasses();
        List implementors = new ArrayList();
        Iterator iter = all.iterator();
        while (iter.hasNext()) {
            ReferenceType refType = (ReferenceType)iter.next();
            if (refType instanceof ClassType) {
                ClassType clazz = (ClassType)refType;
                if (clazz.isPrepared() && clazz.interfaces().contains(this)) {
                    implementors.add(clazz);
                }
            }
        }

        return implementors;
    }

    void addVisibleMethods(Map methodMap) {
        /*
         * Add methods from 
         * parent types first, so that the methods in this class will
         * overwrite them in the hash table
         */

        Iterator iter = superinterfaces().iterator();
        while (iter.hasNext()) {
            InterfaceTypeImpl interfaze = (InterfaceTypeImpl)iter.next();
            interfaze.addVisibleMethods(methodMap);
        }

        addToMethodMap(methodMap, methods());
    }

    public List allMethods() {
        ArrayList list = new ArrayList(methods());
        
        /*
         * It's more efficient if don't do this 
         * recursively.
         */
        List interfaces = allSuperinterfaces();
        Iterator iter = interfaces.iterator();
        while (iter.hasNext()) {
            InterfaceType interfaze = (InterfaceType)iter.next();
            list.addAll(interfaze.methods());
        }

        return list;
    }

    List allSuperinterfaces() {
        ArrayList list = new ArrayList();
        addSuperinterfaces(list);
        return list;
    }

    void addSuperinterfaces(List list) {
        /*
         * This code is a little strange because it 
         * builds the list with a more suitable order than the
         * depth-first approach a normal recursive solution would
         * take. Instead, all direct superinterfaces precede all
         * indirect ones.
         */

        /*
         * Get a list of direct superinterfaces that's not already in the 
         * list being built.
         */
        List immediate = new ArrayList(superinterfaces());
        Iterator iter = immediate.iterator();
        while (iter.hasNext()) {
            InterfaceType interfaze = (InterfaceType)iter.next();
            if (list.contains(interfaze)) {
                iter.remove();
            }
        }

        /*
         * Add all new direct superinterfaces
         */
        list.addAll(immediate);

        /*
         * Recurse for all new direct superinterfaces.
         */
        iter = immediate.iterator();
        while (iter.hasNext()) {
            InterfaceTypeImpl interfaze = (InterfaceTypeImpl)iter.next();
            interfaze.addSuperinterfaces(list);
        }
    }

    boolean isAssignableTo(ReferenceType type) {

        // Exact match?
        if (this.equals(type)) {
            return true;
        } else {

            // Try superinterfaces.
            List supers = superinterfaces();
            Iterator iter = supers.iterator();
            while (iter.hasNext()) {
                InterfaceTypeImpl interfaze = (InterfaceTypeImpl)iter.next();
                if (interfaze.isAssignableTo(type)) {
                    return true;
                }
            }

            return false;
        }
    }

    List inheritedTypes() {
        return superinterfaces();
    }

    public boolean isInitialized() { 
        return isPrepared(); 
    }

    public String toString() {
       return "interface " + name() + " (" + loaderString() + ")";
    }
}
