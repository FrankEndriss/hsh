/**
 */
package com.happypeople.hsh;

import java.io.IOException;

/**
 * A Set of Input and Output streams usable by a client of a context. iE
 * getIntput(STDIN) returns a HshPipe which refers to the standard input stream.
 * getOutput(4) returns a HshPipe to fd 4 if such one was opened.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshFdSet extends AutoCloseable {
    /**
     * Field descriptor of STDIN=0.
     */
    int STDIN = 0;
    /**
     * Field descriptor of STDOUT=1.
     */
    int STDOUT = 1;
    /**
     * Field descriptor of STDERR=2.
     */
    int STDERR = 2;

    /**
     * Query a pipe by field descriptor.
     * @param fdes The field descriptor
     * @return The HshPipe associated with fdes, if there is one.
     */
    HshPipe getPipe(final int fdes);

    /**
     * Associates pipe with fd, closes any previously associated pipe.
     * @param fdes The field descriptor
     * @param pipe The associated pipe
     * @throws IOException If an IOException is thrown by closing the
     *  previously associated pipe
     */
    void setPipe(int fdes, HshPipe pipe) throws IOException;

    /**
     * Removes and closes the pipe associated with fdes.
     * @param fdes The field descriptor
     * @throws IOException If an IOException is thrown by closing the pipe
     */
    void closePipe(int fdes) throws IOException;

    /**
     * Creates a copy of this set. By creating copies of all HshPipes in this
     * set. This is usually called when a new Context is created, because the
     * new context contains copies of all streams of its parent context.
     * @return A deep copy of this HshFdSet.
     * @throws IOException If an IOException is thrown by the io operations
     */
    HshFdSet createCopy();

    /**
     * Adds a redirection to this FDSet. this can be that a FD is created, or
     * one is substituted
     * @param redir The redirection
     * @throws IOException If some io error happens
     * @throws IllegalArgumentException If a non existing FD should be copied.
     */
    void addRedirection(HshRedirection redir) throws IOException;
}
