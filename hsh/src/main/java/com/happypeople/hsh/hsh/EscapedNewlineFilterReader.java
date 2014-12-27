package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.Reader;

/** Filters all not quoted backslashes followed by a "\n".
 * A backslash is quoted by a backslash.
 * Implementation reads on backslash one char ahead.
 * TODO: extend to make this work for "\r\n" also.
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

	@Override
	public int read() throws IOException {
		final char[] buf=new char[1];
		if(read(buf, 0, 1)>0)
			return buf[0];
		return -1;
	}

	@Override
	public int read(final char[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		// TODO optimize by buffering
		final int ret=_read2();
		if(ret<0)	 // EOF
			return 0;
		cbuf[off]=(char)ret;
		return 1;
	}


	enum FilterState2 {
		/** Initial state */
		NOTHING_CACHED,
		/** If an ordinary char was chached */
		CHAR_CACHED,
		/** If a not escaped backslash was chached */
		BACKSLASH_CACHED
	}
	private int lastReadChar2;
	private FilterState2 filterState2=FilterState2.NOTHING_CACHED;

	private int _read2() throws IOException {

		while(true) {
		switch(filterState2) {
		case NOTHING_CACHED:
			if((lastReadChar2=in.read())=='\\') {
				filterState2=FilterState2.BACKSLASH_CACHED;
				//return _read2(); // better just fall throu
			} else
				return lastReadChar2;
		case BACKSLASH_CACHED:
			lastReadChar2=in.read();
			if(lastReadChar2=='\n') {
				filterState2=FilterState2.NOTHING_CACHED;
				continue;
				//return _read2();	// TODO: substitute this recursion by a loop
			} else {	// return the cached backslash and move the just read char into the buffer
				filterState2=FilterState2.CHAR_CACHED;
				return '\\';
			}
		case CHAR_CACHED:
			filterState2=FilterState2.NOTHING_CACHED;
			return lastReadChar2;
		}
		}
		// unreachable
		//throw new RuntimeException("broken :/");
	}

	/****************** old implementation
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
	*/

	/** Filters all not quoted backslashes followed by a newline.
	 * A backslash is quoted by a backslash.
	 * Implementation reads allways one char ahead.
	 * @return the next char in the stream after filtering
	 * @throws IOException
	 */
	/*
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
					final int ret=lastReadChar;
					lastReadChar=nextChar;
					// FilterState stays NORMAL
					return ret;
				}
			} else {
				final int ret=lastReadChar;
				lastReadChar=nextChar;
				return ret;
			}
			// break; unreachable
		case ESCAPED:
			final int ret=lastReadChar;
			lastReadChar=nextChar;
			return ret;
		}
		// unreachable
		throw new RuntimeException("broken :/");
	}
	*/

}
