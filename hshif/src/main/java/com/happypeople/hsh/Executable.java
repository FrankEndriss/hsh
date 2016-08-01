/**
 */
package com.happypeople.hsh;

/**
 * Interface for executable Nodes (i.e. SimpleCommand) in the parse tree.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface Executable {
    /**
     * Execute a Node. Execution includes substitution.
     * @param context Execution environment
     * @return Exit status of execution
     * @throws Exception If something goes wrong
     */
    int doExecution(HshContext context) throws Exception;
}
