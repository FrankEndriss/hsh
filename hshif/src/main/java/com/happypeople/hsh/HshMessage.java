/**
 */
package com.happypeople.hsh;

/**
 * Simple generic messages.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshMessage {
    /**
     * Query the type tag of the message.
     * @return The type of the message (metadata)
     */
    Type getType();

    /**
     * Query the payload of the message.
     * @return The payload of the message
     */
    Object getPayload();

    /**
     * Type of a message.
     */
    enum Type {
        /**
         * Used by exit-command.
         */
        Finish,
        /**
         * Used in ${param:?word} -operator.
         */
        FinishIfNotInteractive
    };

}
