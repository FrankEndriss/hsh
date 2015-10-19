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

import com.happypeople.hsh.HshPipe;

/** A HshPipeImpl has two ends, a reading and a writing one.
 * Bytes written to the writing one can be read by reading the reading one.
 * If there is another end. If not, no bytes are transferred.
 */
public class HshPipeImpl implements HshPipe {
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
		/*
		new Thread() {
			@Override
			public void run() {
				try {
					final byte[] buf=new byte[1024];
					int c;
					while((c=in.read(buf, 0, buf.length))>0)
						out.write(buf, 0, c);
				}catch(final IOException e) {

				}
			}
		}.start();
		*/
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
			return obj.hashCode();
		}

		@Override
		public boolean equals(final Object otherObjRef) {
			if(!(otherObjRef instanceof ObjRef))
				return false;

			if(otherObjRef!=null)
				return obj==((ObjRef)otherObjRef).obj;

			return false;
		}
	}
}
