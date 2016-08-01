/**
 */
package com.happypeople.hsh;

import java.util.List;

/**
 * Interface for Hsh buildin commands. Default is, on any invocation an object
 * of the command is created using the non arg constructor and then execute() is
 * called. If a builtin command does not implement this interface its static
 * main(String[] args) method is called instead. However, it is allways executed
 * in the same process as the parent Hsh, thats the nature of a builtin.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshCmd {
    /**
     * Executes this command in the given context using the given args.
     * @param hsh The hsh context
     * @param args The args including the unix args[0], modifiable List
     * @return The exit-status of the command
     * @throws Exception Of any kind if one happens while execution
     */
    int execute(HshContext hsh, List<String> args) throws Exception;
}
