package com.happypeople.hsh.hsh;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshPipe;

/** A HshPipeImpl is kind of a unix file descriptor.
 * It can be open to read, open to write or open to read and write.
 * As file descriptors, HshPipeImpl can be copied (see "man dup" and "man dup2")
 * Reading and writing is done by delegates, getInputStream() and getOutputStream()
 * So, n HshPipeImpl objects can share one and the same underlying stream(s).
 * Closing a HshPipe does not directly close the underlying stream(s).
 * The underlying stream(s) are closed if no HshPipeImpl-Object references
 * a single stream object. Frankly speaking, the streams are closed when the
 * last HshPipeImpl referencing that stream is closed.
 */
public class HshPipeImpl implements HshPipe {
	private final static Logger log = Logger.getLogger(HshPipeImpl.class);
	private final InputStream inputStream;
	private final PrintStream outputStream;
	private boolean closed=false;

	private static ConcurrentMap<ObjRef, AtomicInteger> refCounterMap=new ConcurrentHashMap<ObjRef, AtomicInteger>();

	/** Constructs a HshPipeImpl based on PipedInputStream/PipedOutputStream
	 * @throws IOException
	 */
	public HshPipeImpl() throws IOException {
		inputStream=new PipedInputStream();
		final PipedOutputStream poutputStream=new PipedOutputStream();
		outputStream=new PrintStream(poutputStream);
		((PipedInputStream)inputStream).connect(poutputStream);
	}

	/** Encapsulates an InputStream.
	 * @param in
	 */
	public HshPipeImpl(final InputStream in) {
		if(in==null)
			throw new IllegalArgumentException("in must not be null");
		this.inputStream=in;
		this.outputStream=null;

		put2Map(in);
	}

	private void put2Map(final Object o) {
		final ObjRef ref=new ObjRef(o);
		refCounterMap.putIfAbsent(ref, new AtomicInteger(0));
		refCounterMap.get(ref).incrementAndGet();
	}

	private void removeFromMap(final Closeable o) throws IOException {
		final ObjRef ref=new ObjRef(o);
		final AtomicInteger refCounter=refCounterMap.get(ref);
		if(refCounter!=null) {
			final int c=refCounter.decrementAndGet();
			if(c==0) {
				log.info("closing stream: "+o);
				o.close();
				refCounterMap.remove(ref, new AtomicInteger(0));
			}
		}
	}

	/** Encapsulates an OutputStream
	 * @param out
	 */
	public HshPipeImpl(final PrintStream out) {
		if(out==null)
			throw new IllegalArgumentException("out must not be null");
		this.outputStream=out;
		this.inputStream=null;

		put2Map(out);
	}

	/** Encapsulates a pair of streams. These might be connected to each other or not.
	 * @param in
	 * @param out
	 */
	public HshPipeImpl(final InputStream in, final PrintStream out) {
		if(in==null)
			throw new IllegalArgumentException("in must not be null");
		if(out==null)
			throw new IllegalArgumentException("out must not be null");
		this.inputStream=in;
		this.outputStream=out;

		put2Map(in);
		put2Map(out);
	}

	public Thread startConnectThread() {
		if(getInputStream()==null || getOutputStream()==null)
			throw new IllegalStateException("cannot connect null stream");
		return startConnectThread(getInputStream(), getOutputStream());
	}

	/** Starts and returns a Thread which connects the two streams.
	 * @param in input to read from
	 * @param out output to write all what was read from input
	 * @return the started thread
	 */
	public static Thread startConnectThread(final InputStream in, final PrintStream out) {
		final Thread t=new Thread() {
			@Override
			public void run() {
				try {
					final byte[] buf=new byte[1024];
					int c;
					while(!Thread.currentThread().isInterrupted() && !out.checkError() && (c=in.read(buf, 0, buf.length))>0)
						out.write(buf, 0, c);
				}catch(final IOException e) {
					// ignore Exception, just finish this Thread by returning
				}
			}
		};
		t.start();
		return t;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public PrintStream getOutputStream() {
		return outputStream;
	}

	@Override
	protected void finalize() {
		try {
			if(!closed)
				close();
		} catch (final IOException e) {
			// ignore
		}
	}

	@Override
	public synchronized void close() throws IOException {
		synchronized(this) {
			if(closed)
				return;
			else
				closed=true;
		}

		if(inputStream!=null)
			removeFromMap(inputStream);
		if(outputStream!=null)
			removeFromMap(outputStream);
	}

	@Override
	public HshPipe createCopy() {
		if(inputStream==null)
			return new HshPipeImpl(outputStream);
		else if(outputStream==null)
			return new HshPipeImpl(inputStream);
		else
			return new HshPipeImpl(inputStream, outputStream);
	}

	private class ObjRef {
		private final Object obj;
		ObjRef(final Object obj) {
			if(obj==null)
				throw new IllegalArgumentException("obj must not be null");
			this.obj=obj;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(obj);
		}

		@Override
		public boolean equals(final Object otherObjRef) {
			return	(otherObjRef instanceof ObjRef) &&
					(obj==((ObjRef)otherObjRef).obj);
		}
	}
}
