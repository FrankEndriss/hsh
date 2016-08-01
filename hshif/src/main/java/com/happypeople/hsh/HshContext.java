/**
 */
package com.happypeople.hsh;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Context information of a calling instance.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshContext extends AutoCloseable, HshMessageListener {
    /**
     * Query STDIN of this context.
     * @return StdIn to use.
     */
    InputStream getStdIn();

    /**
     * Query STDOUT of this context.
     * @return The StdOut to use
     */
    PrintStream getStdOut();

    /**
     * Query STDERR of this context.
     * @return StdErr to use
     */
    PrintStream getStdErr();

    // HshMessageListener implementation Used for FINISHED-Messages
    @Override
    void msg(HshMessage msg);

    /**
     * Register an observer.
     * @param listener Observer to register
     */
    void addMsgListener(HshMessageListener listener);

    /**
     * Query the environment.
     * @return The environment of this context
     */
    HshEnvironment getEnv();

    /**
     * Query the executor.
     * @return The executor of this context, usefull to execute commands. And
     *  has nothing to do with javas ExecutorService.
     */
    HshExecutor getExecutor();

    /**
     * Query the fd set.
     * @return The open FDs of this context
     */
    HshFdSet getFdSet();

    /**
     * Query the terminal, might be null.
     * @return The terminal of this context, or null if there is none
     */
    HshTerminal getTerminal();
}
