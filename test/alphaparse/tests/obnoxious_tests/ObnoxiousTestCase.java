package alphaparse.tests.obnoxious_tests;

import alphaparse.Alpha;
import alphaparse.result.ParseTree;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.rules.Timeout;

/**
 * Test cases for especially obnoxious test cases.
 */
public class ObnoxiousTestCase {
    /**
     * Do I really need to document default constructors for test cases?
     */
    public ObnoxiousTestCase() {
    }

    /**
     * Timeout for all test cases in this class.
     */
    @Rule
    public Timeout timeout = Timeout.millis(200);

    /**
     * <pre>
     *     Mode: Single parse.
     *     Grammar: {@code S = A\nA = S}
     *     Text: {@code ""}
     *     Expect: Failure
     * </pre>
     */
    @Test
    public void SA() {
        {
            var p = Alpha.parser("S = A\nA = S");
            Assertions.assertTrue(p.parse("").isFailure());
        }
    }

    /**
     * <pre>
     *     Mode: Single parse.
     *     Grammar: {@code S = S}
     *     Text: {@code ""}
     *     Expect: Failure
     * </pre>
     */
    @Test
    public void SS1() {
        {
            var p = Alpha.parser("S = S");
            Assertions.assertTrue(p.parse("").isFailure());
        }
    }

    /**
     * <pre>
     *     Mode: Single parse.
     *     Grammar: {@code S = S}
     *     Text: {@code "a"}
     *     Expect: Failure
     * </pre>
     */
    @Test
    public void SS2() {
        {
            var p = Alpha.parser("S = S");
            Assertions.assertTrue(p.parse("a").isFailure());
        }
    }

    /**
     * <pre>
     *     Mode: Single parse.
     *     Grammar: {@code S = S | 'a'}
     *     Text: {@code "a"}
     *     Expect: Parse tree {@code [:S, "a"]}
     * </pre>
     */
    @Test
    public void SS3() {
        {
            var p = Alpha.parser("S = S | 'a'");
            Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
        }
    }

    /**
     * <pre>
     *     Mode: All parses.
     *     Grammar: {@code S = S}
     *     Text: {@code ""}
     *     Expect: Empty
     * </pre>
     */
    @Test
    public void SSParses() {
        var p = Alpha.parser("S = S");
        Assertions.assertTrue(p.parses("").isEmpty());
    }

    /**
     * <pre>
     *     Mode: All parses.
     *     Grammar: {@code S = S | epsilon}
     *     Text: {@code ""}
     *     Expect: Does something other than timing out.
     * </pre>
     */
    @Test
    public void infiniteEpsilon() {
        var p = Alpha.parser("S = S | epsilon");
        Assertions.assertDoesNotThrow(() -> p.parses("").size());
    }
}
