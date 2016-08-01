/**
 */
package com.happypeople.hsh;

/**
 * A HshRedirections object is used to setup the ProcessBuilder while executing
 * a command.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshRedirections {
    /**
     * X.
     * @return The redirection for stderr.
     */
    HshRedirection getStderrRedirection();

    /**
     * X.
     * @return The redirection for stdout.
     */
    HshRedirection getStdoutRedirection();

    /**
     * X.
     * @return The redirection for stdin.
     */
    HshRedirection getStdinRedirection();

    /**
     * Creates a copy of this HshRedirections, but all not null parameters
     *  overwritten.
     * @param stdin The redirection used for stdin
     * @param stdout The redirection used for stdout
     * @param stderr The redirection used for stderr
     * @return A child HshRedirections.
     */
    HshRedirections createChild(
        HshRedirection stdin,
        HshRedirection stdout,
        HshRedirection stderr
    );
}
