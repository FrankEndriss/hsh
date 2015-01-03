package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.PipedReader;
import java.io.Reader;

/** Reader with a callback.
 * Whenever delegate has no more chars to offer the callback is called.
 * It is named ParserCallbackReader because its primary use is that the HshParser
 * requests more input, the callback reads input from the command line.
 */
public class ParserCallbackReader extends Reader {
	private final PipedReader delegate;
	private final Callback callback;

	public ParserCallbackReader(final PipedReader delegate, final Callback callback) {
		this.delegate=delegate;
		this.callback=callback;
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		while(!delegate.ready())
			callback.feedMe();

		return delegate.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	public interface Callback {
		/** Implementation should write some bytes to the PipedWriter connected to the delegate */
		public void feedMe() throws IOException;
	}

}
