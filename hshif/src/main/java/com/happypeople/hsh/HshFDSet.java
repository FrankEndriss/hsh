package com.happypeople.hsh;

import java.io.IOException;

/**
 * A Set of Input and Output streams usable by a client of a context. iE
 * getIntput(STDIN) returns a HshPipe which refers to the standard input stream.
 * getOutput(4) returns a HshPipe to fd 4 if such one was opened.
 */
public interface HshFDSet extends AutoCloseable {
    public final static int STDIN = 0;
    public final static int STDOUT = 1;
    public final static int STDERR = 2;

    /**
     * @param fd
     * @return the HshPipe associated with fd, if there is one.
     */
    public HshPipe getPipe(final int fd);

    /**
     * Associates pipe with fd, closes any previously associated pipe.
     *
     * @param fd
     * @param pipe
     * @throws IOException
     */
    public void setPipe(int fd, HshPipe pipe) throws IOException;

    /**
     * Removes and closes the pipe associated with fd
     *
     * @param fd
     * @throws IOException
     */
    public void closePipe(int fd) throws IOException;

    /**
     * Creates a copy of this set. By creating copies of all HshPipes in this
     * set. This is usually called when a new Context is created, because the
     * new context contains copies of all streams of its parent context.
     *
     * @throws IOException
     **/
    public HshFDSet createCopy();

    /**
     * Adds a redirection to this FDSet. this can be that a FD is created, or
     * one is substituted
     *
     * @param redir
     *            the redirection
     * @throws IOException
     * @throws IllegalArgumentException
     *             if a non existing FD should be copied.
     */
    public void addRedirection(HshRedirection redir) throws IOException;
}
