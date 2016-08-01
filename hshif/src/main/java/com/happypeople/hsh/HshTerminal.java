/**
 */
package com.happypeople.hsh;

/**
 * Abstraction of the Terminal within a HshContext.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshTerminal {
    /**
     * X.
     * @return Number of columns of the terminal window.
     */
    int getCols();

    /**
     * X.
     * @return Number of rows of the terminal window.
     */
    int getRows();
}
