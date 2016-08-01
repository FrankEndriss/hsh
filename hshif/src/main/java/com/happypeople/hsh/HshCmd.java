package com.happypeople.hsh;

import java.util.ArrayList;

/**
 * Interface for Hsh buildin commands. Default is, on any invocation an object
 * of the command is created using the non arg constructor and then execute() is
 * called. If a builtin command does not implement this interface its static
 * main(String[] args) method is called instead. However, it is allways executed
 * in the same process as the parent Hsh, thats the nature of a builtin.
 */
public interface HshCmd {
    /**
     * Executes this command in the given context using the given args.
     *
     * @param hsh
     *            the hsh context
     * @param args
     *            the args including the unix args[0], modifiable List
     * @return the exit-status of the command
     */
    public int execute(HshContext hsh, ArrayList<String> args) throws Exception;
}
