package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.happypeople.hsh.HshInput;
import com.happypeople.hsh.HshOutput;

/** HshIOConnector copies bytes from InputStreams to OutputStreams. One adds such a pair by calling add(in, out).
 * The copying process is stopped when in is closed (in.read(buf) returns <=0).
 */
public class HshIOConnector {

	private static boolean running=false;
	private static Object runningLock=new Object();
	private final static BlockingQueue<IOTriple> newIOTriples=new LinkedBlockingQueue<IOTriple>();

	private static class IOTriple {
		SelectableChannel in;
		SelectableChannel out;
		ByteBuffer buf=ByteBuffer.allocate(1024);

		IOTriple(final SelectableChannel in, final SelectableChannel out) {
			this.in=in;
			this.out=out;
		}
	}

	private static void selectorLoop(final Selector selector) throws IOException {
		while(!selector.keys().isEmpty() || !newIOTriples.isEmpty()) {
			while(!newIOTriples.isEmpty()) {
				final IOTriple triple=newIOTriples.poll();
				triple.in.register(selector, SelectionKey.OP_READ, triple);
			}

			selector.select();

			final Iterator<SelectionKey> iter=selector.selectedKeys().iterator();

			while(iter.hasNext()) {
				final SelectionKey selKey=iter.next();

				final IOTriple triple=(IOTriple)selKey.attachment();

				if(selKey.isWritable()) { // write from buffer to HshOutput
					((WritableByteChannel)selKey.channel()).write(triple.buf);

					if(triple.buf.remaining()==0) {	// write is done
						triple.buf.clear();

						// add HshInput to selector
						iter.remove();
						triple.in.register(selector, SelectionKey.OP_READ, triple);
					}
				} // else

				if(selKey.isReadable()) { // read from HshInput to buffer
					final int c=((ReadableByteChannel)selKey.channel()).read(triple.buf);
					if(c<0) { // in was closed, do cleanup
						((WritableByteChannel)selKey.channel()).close();
						iter.remove();
					} else if(c>0){
						triple.buf.flip();

						// TODO: try a write once

						// add HshOutput to selector
						iter.remove();
						triple.out.register(selector, SelectionKey.OP_WRITE, triple);

					} else if(c==0) { // should not happen in select loop
						throw new RuntimeException("something went wrong :/");
					}
				}
			}
		}
		synchronized(runningLock) {
			running=false;
		}
	}

	private static void ensureThreadRunning() {
		synchronized(runningLock) {
			if(!running) {
				running=true;
				new Thread() {
					@Override
					public void run() {
						try(Selector lSelector=Selector.open()) {
							selectorLoop(lSelector);
						} catch (final IOException e) {
							// TODO the Channel causing this Exception should be removed from
							// the selector, and the Thread should be restarted.
							e.printStackTrace();
							System.exit(1);
						} finally {
							synchronized(runningLock) {
								running=false;
							}
						}
					}
				}.start();
			}
		}
	}

	/** Connects a pair of streams.
	 * Implementation: creates a Thread which runs until in is closed, or an Exception occurs.
	 * If out is closed the bytes are sent to /dev/null.
	 * @param in
	 * @param out
	 */
	public static void add(final HshInput in, final HshOutput out) {
		if(in.getChannel()!=null && out.getChannel()!=null) { // do single thread io
			newIOTriples.offer(new IOTriple(in.getChannel(), out.getChannel()));
			ensureThreadRunning();
			return;
		} // else

		new Thread() {
			@Override
			public void run() {
				try {
					final byte[] buf=new byte[1024];
					int cIn;
					while((cIn=read(in, buf))>0)
						try {
							write(out, buf, cIn);
						}catch(final Exception e) {
							// ignore
						}
				} catch(final Exception e) {
					// ignore, just stop copy bytes
				}finally {

				}
			}
		}.start();
	}

	private static int read(final HshInput in, final byte[] buf) throws IOException {
		if(in.getStream()!=null)
			return in.getStream().read(buf);
		else {
			// TODO in.getChannel does not block
			final ByteBuffer bb=ByteBuffer.allocate(buf.length);
			final int c=((ReadableByteChannel)in.getChannel()).read(bb);
			bb.get(buf, 0, c);
			return c;
		}
	}

	private static void write(final HshOutput out, final byte[] buf, final int len) throws IOException {
		if(out.getStream()!=null)
			out.getStream().write(buf, 0, len);
		else {
			final ByteBuffer bb=ByteBuffer.allocate(len);
			bb.put(buf, 0, len);
			bb.flip();
			int c=0;
			// TODO out.getChannel does not block, need to selectNow()
			while((c+=((WritableByteChannel)out.getChannel()).write(bb))<len);
		}
	}

}
