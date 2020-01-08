package enigma;

import ucb.junit.textui;

/**
 * The suite of all JUnit tests for the enigma package.
 *
 * @author
 */
public class UnitTest {

    private static Class<PermutationTest> _perm;

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        _perm = PermutationTest.class;
        textui.runClasses(_perm, MovingRotorTest.class, MachineTest.class);
    }

}


