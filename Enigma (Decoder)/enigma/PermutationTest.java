package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static enigma.TestUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * The suite of all JUnit tests for the Permutation class.
 *
 * @author
 */
public class PermutationTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * Check that perm has an alphabet whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void permuteTest() {
        perm =  new Permutation("(ABC)", UPPER);
        assertEquals(perm.permute('A'), 'B');

        perm = new Permutation("(DEBI)", UPPER);
        assertEquals(perm.permute('E'), 'B');

        perm = new Permutation("(DEBORAH)", UPPER);
        assertEquals(perm.permute(1),  14);

        perm = new Permutation("(ABCD) (ZYX) (S)", UPPER);
        assertEquals(perm.permute('Z'), 'Y');

        perm = new Permutation("(HEY) (U)"
                + " (IAM) (BUSNOW)", UPPER);
        assertEquals(perm.permute('H'), 'E');
        assertEquals(perm.permute('U'), 'U');
        assertEquals(perm.permute('M'), 'I');
        assertEquals(perm.permute('S'), 'N');

    }

    @Test
    public void invertTest() {
        perm =  new Permutation("(ABC)", UPPER);
        assertEquals(perm.invert('B'), 'A');

        perm = new Permutation("(DEBI)", UPPER);
        assertEquals(perm.invert('E'), 'D');

        perm = new Permutation("(DEBORAH)", UPPER);
        assertEquals(perm.invert(1), 4);

        perm = new Permutation("(ABCD) (ZYX) (S)", UPPER);
        assertEquals(perm.invert('Y'), 'Z');

        perm = new Permutation("(ABC) (DEFGHI) (JK)",
                new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals(perm.invert('B'), 'A');
        assertEquals(perm.invert('L'), 'L');

        perm = new Permutation("(ZHI) (DEOPMH) (EQL) (JK)", UPPER);
        assertEquals(perm.invert('D'), 'H');
        assertEquals(perm.invert('K'), 'J');
    }

    @Test
    public void derangementTest() {
        perm =  new Permutation("(ABC)", UPPER);
        assertEquals(perm.derangement(), true);

        perm = new Permutation("(DEBI)", UPPER);
        assertEquals(perm.derangement(), true);

        perm = new Permutation("(deborah)", UPPER);
        assertEquals(perm.derangement(), true);

        perm = new Permutation("(A)", UPPER);
        assertEquals(perm.derangement(), false);

        perm = new Permutation("(ABC) (DEFGHI) (JK)",
                new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals(perm.derangement(), true);

        perm = new Permutation("(HEY) (U) (IAM) (BUSNOW)", UPPER);
        assertEquals(perm.derangement(), false);

    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)",
                new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('G'), 'G');
    }
}
