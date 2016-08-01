/**
 */
package com.happypeople.hsh;

import java.util.List;

/**
 * Interface to execute commands in a given HshContext.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshExecutor {
    /**
     * Execute command using context.
     * @param command And args executed
     * @param parentcontext Given to execution
     * @param redirections Activ for this execution
     * @return Exit status of execution
     * @throws Exception if one happens while execution
     */
    int execute(String[] command, HshContext parentcontext,
        List<HshRedirection> redirections)
        throws Exception;

    /**
     * Checks if this executor can execute a specific command within a given
     * parentContext.
     * @param command Contains the command to execute
     * @param parentcontext Contains the context, ie the PATH variable
     * @return True if this executor thinks it is able to execute command
     */
    boolean canExecute(String[] command, HshContext parentcontext);

    /**
     * Closes this executor and releases all resources held.
     */
    void close();
}
