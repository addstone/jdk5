/*
 * @(#)ShortcutDesc.java	1.2 03/12/19* 
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.javaws.jnl;

import com.sun.deploy.xml.*;

/**
 *   Tag for a extension library descriptor
 */
public class ShortcutDesc implements XMLable {

    private boolean _online;
    private boolean _desktop;
    private boolean _menu;
    private String  _submenu;
        
    public ShortcutDesc(boolean online, boolean desktop, 
			boolean menu, String submenu) { 

	_online = online;
	_desktop = desktop;
	_menu = menu;
	_submenu = submenu;
    }

    public boolean getOnline()  { return _online; }
    public boolean getDesktop() { return _desktop; }
    public boolean getMenu()    { return _menu; }
    public String getSubmenu()  { return _submenu; }
    
         
    /** Outputs as XML */
    public XMLNode asXML() {
        XMLAttributeBuilder ab = new XMLAttributeBuilder();
	ab.add("online", _online);
	XMLNodeBuilder nb = new XMLNodeBuilder("shortcut", 
				ab.getAttributeList());

	if (_desktop) {
            nb.add("desktop", null);
	}
	if (_menu) {
	    if (_submenu == null) {
		nb.add("menu", null);
	    } else {
		nb.add(new XMLNode("menu", new XMLAttribute("submenu", _submenu)));
	    }
	}
	return nb.getNode();
    }
}




