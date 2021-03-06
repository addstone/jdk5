/*
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.plugin.dom.html;

import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

/**
 *  Organizes form controls into logical groups. See the   FIELDSET  element 
 * definition in HTML 4.0.
 * <p>See also the <a href='http://www.w3.org/TR/2000/CR-DOM-Level-2-20000510'>Document Object Model (DOM) Level 2 Specification</a>.
 */
public final class HTMLFieldSetElement extends HTMLElement
				       implements org.w3c.dom.html.HTMLFieldSetElement {
    public HTMLFieldSetElement(DOMObject obj, 
			       org.w3c.dom.html.HTMLDocument doc) {
	super(obj, doc);
    }

    /**
     *  Returns the <code>FORM</code> element containing this control. Returns 
     * <code>null</code> if this control is not within the context of a form. 
     */
    public org.w3c.dom.html.HTMLFormElement getForm() {
	Object result = obj.getMember(HTMLConstants.ATTR_FORM);
	if(result != null && result instanceof DOMObject) {	    
	    Object ret = DOMObjectFactory.createHTMLObject((DOMObject)result, 
			 (org.w3c.dom.html.HTMLDocument)getOwnerDocument());
	    if(ret != null && ret instanceof org.w3c.dom.html.HTMLFormElement) {
		return (org.w3c.dom.html.HTMLFormElement)ret;
	    }
	}

	return null;
    }
}