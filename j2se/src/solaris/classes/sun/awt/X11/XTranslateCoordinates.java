// This file is an automatically generated file, please do not edit this file, modify the WrapperGenerator.java file instead !

package sun.awt.X11;

import sun.misc.Unsafe;

class XTranslateCoordinates {
	private static Unsafe unsafe = XlibWrapper.unsafe;
	private boolean __executed = false;
	private boolean __disposed = false;
	long _scr_w;
	long _dest_w;
	int _src_x;
	int _src_y;
	long dest_x_ptr = unsafe.allocateMemory(Native.getIntSize());
	long dest_y_ptr = unsafe.allocateMemory(Native.getIntSize());
	long child_ptr = unsafe.allocateMemory(Native.getLongSize());
	public XTranslateCoordinates(
		long scr_w,
		long dest_w,
		int src_x,
		int src_y	)
	{
		set_scr_w(scr_w);
		set_dest_w(dest_w);
		set_src_x(src_x);
		set_src_y(src_y);
	}
	public int execute() {
		return execute(null);
	}
	public int execute(XToolkit.XErrorHandler errorHandler) {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		try {
			XToolkit.awtLock();
			if (__executed) {
			    throw new IllegalStateException("Already executed");
			}
			__executed = true;
			if (errorHandler != null) {
			    XToolkit.WITH_XERROR_HANDLER(errorHandler);
			}
			int status = 
			XlibWrapper.XTranslateCoordinates(XToolkit.getDisplay(), 
				get_scr_w(),
				get_dest_w(),
				get_src_x(),
				get_src_y(),
				dest_x_ptr,
				dest_y_ptr,
				child_ptr			);
			if (errorHandler != null) {
			    XToolkit.RESTORE_XERROR_HANDLER();
			}
			return status;
		} finally {
		    XToolkit.awtUnlock();
		}
	}
	public boolean isExecuted() {
	    return __executed;
	}
	
	public boolean isDisposed() {
	    return __disposed;
	}
	public void finalize() {
            if (XlibWrapper.isBuildInternal && !__disposed) {
                System.err.println("WARNING: sun.awt.X11.XTranslateCoordinates not disposed till finalization!");
            }
	}
	public void dispose() {
            synchronized(XToolkit.getAWTLock()) {
		if (__disposed) {
		    return;
		}
		unsafe.freeMemory(dest_x_ptr);
		unsafe.freeMemory(dest_y_ptr);
		unsafe.freeMemory(child_ptr);
		__disposed = true;
            }
	}
	public long get_scr_w() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return _scr_w;
	}
	public void set_scr_w(long data) {
		_scr_w = data;
	}
	public long get_dest_w() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return _dest_w;
	}
	public void set_dest_w(long data) {
		_dest_w = data;
	}
	public int get_src_x() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return _src_x;
	}
	public void set_src_x(int data) {
		_src_x = data;
	}
	public int get_src_y() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return _src_y;
	}
	public void set_src_y(int data) {
		_src_y = data;
	}
	public int get_dest_x() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return Native.getInt(dest_x_ptr);
	}
	public void set_dest_x(int data) {
		Native.putInt(dest_x_ptr, data);
	}
	public int get_dest_y() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return Native.getInt(dest_y_ptr);
	}
	public void set_dest_y(int data) {
		Native.putInt(dest_y_ptr, data);
	}
	public long get_child() {
		if (__disposed) {
		    throw new IllegalStateException("Disposed");
		}
		if (!__executed) {
		    throw new IllegalStateException("Not executed");
		}
		return Native.getLong(child_ptr);
	}
	public void set_child(long data) {
		Native.putLong(child_ptr, data);
	}
}
