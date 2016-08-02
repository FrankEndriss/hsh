/**
 */
package com.happypeople.hshutil.util;

import org.junit.Before;
import org.junit.Test;

/**
 * Testclass for class Pair.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public class PairTest {

    /**
     * Magic Integer.
     */
    private static final int NUM = 42;
    /**
     * Magic String.
     */
    private static final String STR = "hello";

    /**
     * Object under test.
     */
    private Pair<String, Integer> pair;

    /**
     * Sets up the out pair.
     * @throws Exception No, it does not
     */
    @Before
    public final void setUp() throws Exception {
        this.pair = new Pair(PairTest.STR, PairTest.NUM);
    }

    /**
     * Tests Pair.getFirst().
     */
    @Test
    public final void testGetFirst() {
        org.junit.Assert.assertEquals(
            "first", PairTest.STR, this.pair.getFirst()
        );
    }

    /**
     * Tests Pair.getSecond().
     */
    @Test
    public final void testGetSecond() {
        org.junit.Assert.assertEquals(
            "second", PairTest.NUM, this.pair.getSecond()
        );
    }

}
