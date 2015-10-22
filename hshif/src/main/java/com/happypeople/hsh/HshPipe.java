package com.happypeople.hsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/** An HshPipe is a construct to encapsulate targets and sources of IO.
 * At least one of input or output is != null.
 * If both are != null there are three cases:
 * 1.) they are to each other, what is written to getOutputStream() is readable throu * getInputStream().
 * 2.) they are two ends of a sub-process, what is written to getOutputStream is read by the process, and what is
 * written by the process can be read throu getInputStream()
 * 3.) its a stream/socket which can be read and write independently
 */
public interface HshPipe {
	public InputStream getInputStream();
	public PrintStream getOutputStream();

	/** Closes this HshPipe, unlinks the underlying streams from being used.
	 *  Usually the underlying streams are closed if unlinked from all HshPipes, ie unused.
	 */
	public void close() throws IOException;

	/**
	 * @return a copy of this HshPipe, ie a HshPipe which references the same underlying streams
	 * as this HshPipe. The following should be true:
	 * hshPipe.getInputStream()==hshPipe.createCopy().getInputStream();
	 * hshPipe.getOutputStream()==hshPipe.createCopy().getOutputStream();
	 */
	public HshPipe createCopy();
}
