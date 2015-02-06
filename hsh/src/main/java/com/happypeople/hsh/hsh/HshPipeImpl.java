package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import com.happypeople.hsh.HshPipe;

/** A HshPipeImpl has two ends, a reading and a writing one.
 * Bytes written to the writing one can be read by reading the reading one.
 * If there is another end. If not, no bytes are transferred.
 */
public class HshPipeImpl implements HshPipe {
	private final InputStream inputStream;
	private final PrintStream outputStream;

	/** Constructs a HshPipeImpl based on PipedInputStream/PipedOutputStream
	 * @throws IOException
	 */
	public HshPipeImpl() throws IOException {
		inputStream=new PipedInputStream();
		final PipedOutputStream poutputStream=new PipedOutputStream();
		outputStream=new PrintStream(poutputStream);
		((PipedInputStream)inputStream).connect(poutputStream);
	}

	public HshPipeImpl(final InputStream in) {
		this.inputStream=in;
		this.outputStream=null;
	}

	public HshPipeImpl(final PrintStream out) {
		this.outputStream=out;
		this.inputStream=null;
	}

	/** Connects two stream by reading in and writing to out until in or out is closed. Asynchronously.
	 * @param in
	 * @param out
	 */
	public HshPipeImpl(final InputStream in, final PrintStream out) {
		this.inputStream=in;
		this.outputStream=out;
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
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public PrintStream getOutputStream() {
		return outputStream;
	}

}
