/**
 */
package com.happypeople.hsh.exit;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshMessage;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Implements the exit command, which causes a sh instance to finish
 * immediately.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public class Exit implements HshCmd {
    /**
     * A standard Logger.
     */
    private static final Logger LOG = Logger.getLogger(Exit.class);

    @Override
    public final int execute(final HshContext context, final List<String> args)
        throws Exception {
        final int exitcode = Integer.parseInt(args.get(1));
        context.msg(new FinishMessage());
        LOG.info(
            new StringBuilder("exit, exitCode=").append(exitcode).toString()
        );
        return exitcode;
    }

    /**
     * Message to denote finishing the current sh instance.
     */
    private static class FinishMessage implements HshMessage {
        @Override
        public Type getType() {
            return HshMessage.Type.Finish;
        }

        @Override
        public Object getPayload() {
            return null;
        }
    }
}
