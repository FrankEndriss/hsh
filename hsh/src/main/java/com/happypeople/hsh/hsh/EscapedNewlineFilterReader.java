package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.Reader;

/** Filters all not quoted backslashes followed by a newline.
 * A backslash is quoted by a backslash.
 * Implementation reads always one char ahead.
 */
public class EscapedNewlineFilterReader extends Reader {
	private final Reader in;

	public EscapedNewlineFilterReader(final Reader in) {
		this.in=in;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public int read() throws IOException {
		char[] buf=new char[1];
		if(read(buf, 0, 1)>0)
			return buf[0];
		return -1;
	}

	public int read(char[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		// TODO optimize by buffering
		int ret=_read();
		if(ret<0)	 // EOF
			return 0;
		cbuf[off]=(char)ret;
		return 1;
	}

	
	enum FilterState {
		// lastReadChar is empty
		INIT,
		// lastReadChar was not an escaped backslash, but may be a backslash
		NORMAL,
		// lastReadChar was an escaped backslash
		ESCAPED;
	}

	private int lastReadChar;
	private FilterState filterState=FilterState.INIT;

	/** Filters all not quoted backslashes followed by a newline.
	 * A backslash is quoted by a backslash.
	 * Implementation reads allways one char ahead.
	 * @return the next char in the stream after filtering
	 * @throws IOException 
	 */
	private int _read() throws IOException {
		final int nextChar=in.read();

		switch(filterState) {
		case INIT:
			lastReadChar=nextChar;
			filterState=FilterState.NORMAL;
			return _read();
		case NORMAL:
			if(lastReadChar=='\\') {	 // unescaped backslash
				if(nextChar=='\\') {
					filterState=FilterState.ESCAPED;
					return lastReadChar;
				} else if(nextChar=='\n') {
					// throw away both
					filterState=FilterState.INIT;
					return _read();
				} else {
					int ret=lastReadChar;
					lastReadChar=nextChar;
					// FilterState stays NORMAL
					return ret;
				}
			} else {
				int ret=lastReadChar;
				lastReadChar=nextChar;
				return ret;
			}
			// break; unreachable
		case ESCAPED:
			int ret=lastReadChar;
			lastReadChar=nextChar;
			return ret;
		}
		// unreachable
		throw new RuntimeException("broken :/");
	}

}
