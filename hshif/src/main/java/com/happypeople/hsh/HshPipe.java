package com.happypeople.hsh;

import java.io.InputStream;
import java.io.PrintStream;

/** An HshPipe is a construct to encapsulate targets and sources of IO.
 * At least one of input or output is != null.
 * If both are != null there are thow cases:
 * *they are to each other, what is written to getOutputStream() is readable throu * getInputStream().
 * *they are two ends of a sub-process, what is written to getOutputStream is read by the process, and what is
 * written by the process can be read throu getInputStream()
 */
public interface HshPipe {
	public InputStream getInputStream();

	public PrintStream getOutputStream();
}
