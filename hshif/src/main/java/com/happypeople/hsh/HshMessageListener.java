/**
 */
package com.happypeople.hsh;

/**
 * Receiver of messages.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshMessageListener {
    /**
     * Called after posting of a HshMessage.
     * @param msg The sent message
     */
    void msg(HshMessage msg);
}
