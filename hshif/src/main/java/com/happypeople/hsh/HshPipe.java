/**
 */
package com.happypeople.hsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * An HshPipe is a construct to encapsulate targets and sources of IO. At least
 * one of input or output is != null. If both are != null there are three cases:
 * 1.) they are to each other, what is written to getOutputStream() is readable
 * throu * getInputStream(). 2.) they are two ends of a sub-process, what is
 * written to getOutputStream is read by the process, and what is written by the
 * process can be read throu getInputStream() 3.) its a stream/socket which can
 * be read and write independently.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshPipe {
    /**
     * Query the readable part of the pipe.
     * @return The InputStream of the pipe
     */
    InputStream getInputStream();

    /**
     * Query the writable part of the pipe.
     * @return The OutputStream of the pipe
     */
    PrintStream getOutputStream();

    /**
     * Closes this HshPipe, unlinks the underlying streams from being used.
     * Usually the underlying streams are closed if unlinked from all HshPipes,
     * ie unused.
     * @throws IOException if such one is thrown by closing the underlying
     *  mechanisms.
     */
    void close() throws IOException;

    /**
     * Copy a pipe.
     * @return A copy of this HshPipe, ie a HshPipe which references the same
     *  underlying streams as this HshPipe. The following should be true:
     *  hshPipe.getInputStream()==hshPipe.createCopy().getInputStream();
     *  hshPipe.getOutputStream()==hshPipe.createCopy().getOutputStream();
     */
    HshPipe createCopy();
}
