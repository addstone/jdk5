/*
 * @(#)ServerSocketChannelImpl.java	1.37 04/04/20
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;


/**
 * An implementation of ServerSocketChannels
 */

class ServerSocketChannelImpl
    extends ServerSocketChannel
    implements SelChImpl
{

    // Used to make native close and configure calls
    private static NativeDispatcher nd;

    // Our file descriptor
    private final FileDescriptor fd;

    // fd value needed for dev/poll. This value will remain valid
    // even after the value in the file descriptor object has been set to -1
    private int fdVal;

    // ID of native thread currently blocked in this channel, for signalling
    private volatile long thread = 0;

    // Lock held by thread currently blocked in this channel
    private final Object lock = new Object();

    // Lock held by any thread that modifies the state fields declared below
    // DO NOT invoke a blocking I/O operation while holding this lock!
    private final Object stateLock = new Object();

    // -- The following fields are protected by stateLock

    // Channel state, increases monotonically
    private static final int ST_UNINITIALIZED = -1;
    private static final int ST_INUSE = 0;
    private static final int ST_KILLED = 1;
    private int state = ST_UNINITIALIZED;
    
    // Binding
    private SocketAddress localAddress = null; // null => unbound

    // Options, created on demand
    private SocketOpts.IP.TCP options = null;

    // Our socket adaptor, if any
    ServerSocket socket;

    // -- End of fields protected by stateLock


    public ServerSocketChannelImpl(SelectorProvider sp) throws IOException {
	super(sp);
	this.fd =  Net.serverSocket(true);
        this.fdVal = IOUtil.fdVal(fd);
	this.state = ST_INUSE;
    }

    public ServerSocketChannelImpl(SelectorProvider sp, FileDescriptor fd) 
	throws IOException 
    {
        super(sp);
        this.fd =  fd;
        this.fdVal = IOUtil.fdVal(fd);
        this.state = ST_INUSE;
	localAddress = Net.localAddress(fd);
    }


    public ServerSocket socket() {
	synchronized (stateLock) {
	    if (socket == null)
		socket = ServerSocketAdaptor.create(this);
	    return socket;
	}
    }

    public boolean isBound() {
	synchronized (stateLock) {
	    return localAddress != null;
	}
    }

    public SocketAddress localAddress() {
	synchronized (stateLock) {
	    return localAddress;
	}
    }

    public void bind(SocketAddress local, int backlog) throws IOException {
	synchronized (lock) {
	    if (!isOpen())
		throw new ClosedChannelException();
	    if (isBound())
		throw new AlreadyBoundException();
	    InetSocketAddress isa = Net.checkAddress(local);
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null)
		sm.checkListen(isa.getPort());
	    Net.bind(fd, isa.getAddress(), isa.getPort());
	    listen(fd, backlog < 1 ? 50 : backlog);
	    synchronized (stateLock) {
		localAddress = Net.localAddress(fd);
	    }
	}
    }

    public SocketChannel accept() throws IOException {
	synchronized (lock) {
	    if (!isOpen())
		throw new ClosedChannelException();
	    if (!isBound())
		throw new NotYetBoundException();
	    SocketChannel sc = null;

	    int n = 0;
	    FileDescriptor newfd = new FileDescriptor();
	    InetSocketAddress[] isaa = new InetSocketAddress[1];

	    try {
		begin();
		if (!isOpen())
		    return null;
		thread = NativeThread.current();
		for (;;) {
		    n = accept0(this.fd, newfd, isaa);
		    if ((n == IOStatus.INTERRUPTED) && isOpen())
			continue;
		    break;
		}
	    } finally {
		thread = 0;
		end(n > 0);
		assert IOStatus.check(n);
	    }

	    if (n < 1)
		return null;

	    IOUtil.configureBlocking(newfd, true);
	    InetSocketAddress isa = isaa[0];
	    sc = new SocketChannelImpl(provider(), newfd, isa);
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null) {
		try {
		    sm.checkAccept(isa.getAddress().getHostAddress(),
				   isa.getPort());
		} catch (SecurityException x) {
		    sc.close();
		    throw x;
		}
	    }
	    return sc;

	}
    }

    protected void implConfigureBlocking(boolean block) throws IOException {
	IOUtil.configureBlocking(fd, block);
    }

    public SocketOpts options() {
	synchronized (stateLock) {
	    if (options == null) {
		SocketOptsImpl.Dispatcher d
		    = new SocketOptsImpl.Dispatcher() {
			    int getInt(int opt) throws IOException {
				return Net.getIntOption(fd, opt);
			    }
			    void setInt(int opt, int arg) throws IOException {
				Net.setIntOption(fd, opt, arg);
			    }
			};
		options = new SocketOptsImpl.IP.TCP(d);
	    }
	    return options;
	}
    }

    protected void implCloseSelectableChannel() throws IOException {
	synchronized (stateLock) {
	    nd.preClose(fd);
	    long th = thread;
	    if (th != 0)
		NativeThread.signal(th);
	    if (!isRegistered())
		kill();
	}
    }

    public void kill() throws IOException {
	synchronized (stateLock) {
	    if (state == ST_KILLED)
		return;
	    if (state == ST_UNINITIALIZED) {
                state = ST_KILLED;
		return;
            }
	    assert !isOpen() && !isRegistered();
	    nd.close(fd);
	    state = ST_KILLED;
	}
    }

    /**
     * Translates native poll revent set into a ready operation set
     */
    public boolean translateReadyOps(int ops, int initialOps,
                                     SelectionKeyImpl sk) {
        int intOps = sk.nioInterestOps(); // Do this just once, it synchronizes
        int oldOps = sk.nioReadyOps();
        int newOps = initialOps;

        if ((ops & PollArrayWrapper.POLLNVAL) != 0) {
	    // This should only happen if this channel is pre-closed while a
	    // selection operation is in progress
	    // ## Throw an error if this channel has not been pre-closed
	    return false;
	}

        if ((ops & (PollArrayWrapper.POLLERR
                    | PollArrayWrapper.POLLHUP)) != 0) {
            newOps = intOps;
            sk.nioReadyOps(newOps);
            return (newOps & ~oldOps) != 0;
        }

        if (((ops & PollArrayWrapper.POLLIN) != 0) &&
            ((intOps & SelectionKey.OP_ACCEPT) != 0))
                newOps |= SelectionKey.OP_ACCEPT;

        sk.nioReadyOps(newOps);
        return (newOps & ~oldOps) != 0;
    }

    public boolean translateAndUpdateReadyOps(int ops, SelectionKeyImpl sk) {
        return translateReadyOps(ops, sk.readyOps(), sk);
    }

    public boolean translateAndSetReadyOps(int ops, SelectionKeyImpl sk) {
        return translateReadyOps(ops, 0, sk);
    }

    /**
     * Translates an interest operation set into a native poll event set
     */
    public void translateAndSetInterestOps(int ops, SelectionKeyImpl sk) {
        int newOps = 0;

        // Translate ops
        if ((ops & SelectionKey.OP_ACCEPT) != 0)
            newOps |= PollArrayWrapper.POLLIN;
        // Place ops into pollfd array
        sk.selector.putEventOps(sk, newOps);
    }

    public FileDescriptor getFD() {
        return fd;
    }

    public int getFDVal() {
        return fdVal;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.getClass().getName());
	sb.append('[');
	if (!isOpen())
	    sb.append("closed");
	else {
	    synchronized (stateLock) {
		if (localAddress() == null) {
		    sb.append("unbound");
		} else {
		    sb.append(localAddress().toString());
		}
	    }
	}
	sb.append(']');
	return sb.toString();
    }

    // -- Native methods --

    private static native void listen(FileDescriptor fd, int backlog)
	throws IOException;

    // Accepts a new connection, setting the given file descriptor to refer to
    // the new socket and setting isaa[0] to the socket's remote address.
    // Returns 1 on success, or IOStatus.UNAVAILABLE (if non-blocking and no
    // connections are pending) or IOStatus.INTERRUPTED.
    //
    private native int accept0(FileDescriptor ssfd, FileDescriptor newfd,
			       InetSocketAddress[] isaa)
	throws IOException;

    private static native void initIDs();

    static {
	Util.load();
        initIDs();
        nd = new SocketDispatcher();
    }

}
