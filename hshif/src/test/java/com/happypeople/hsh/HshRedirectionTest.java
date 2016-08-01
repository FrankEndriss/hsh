/**
 */
package com.happypeople.hsh;

import java.io.File;
import org.junit.Assert;
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
        final HshRedirection hshredir = new HshRedirection(
            HshFDSet.STDIN, HshRedirection.OperationType.READ,
            new File("/tmp/bla.f")
        );
        Assert.assertEquals(
            "redirected stream",
            HshFDSet.STDIN,
            hshredir.getRedirectedFD()
        );
        Assert.assertEquals(
            "targetType FILE",
            HshRedirection.TargetType.FILE,
            hshredir.getTargetType()
        );
    }

    /**
     * Tests HshRedirection constructor.
     */
    @Test
    public void testHshRedirectionIntOperationTypeInteger() {
        new HshRedirection(
            HshFDSet.STDERR,
            HshRedirection.OperationType.WRITE,
            HshFDSet.STDOUT
        );
    }

}
