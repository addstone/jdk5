/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#if !defined(__CFDMonitor__)
#define __CFDMonitor__

#include "IFDMonitor.h"

class CFDMonitor : public IFDMonitor {
public:

    DECL_IEGO

    virtual void connectFD(int fd, void (*)(void *), void *);
    virtual void disconnectFD(void * data);

    CFDMonitor::CFDMonitor();

private:

};

#endif
