/**
 */
package com.happypeople.hsh;

import java.io.File;
import org.junit.Test;

/**
 * Tests class HshRedirection.
 * @author Frank Endriss (frank.endriss@fumgroup.com)
 * @version $Id$
 * @since 0.1
 */
public final class HshRedirectionTest {

    /**
     * Test redirection to read from /tmp/bla.f.
     */
    @Test
    public void testHshRedirectionIntOperationTypeFile() {
        new HshRedirection(
            HshFdSet.STDIN, HshRedirection.OperationType.READ,
            new File("/tmp/bla.f")
        );
    }

    /**
     * Tests HshRedirection constructor.
     */
    @Test
    public void testHshRedirectionIntOperationTypeInteger() {
        new HshRedirection(
            HshFdSet.STDERR,
            HshRedirection.OperationType.WRITE,
            HshFdSet.STDOUT
        );
    }

}
