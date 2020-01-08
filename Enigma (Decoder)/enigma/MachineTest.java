package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static enigma.TestUtils.UPPER;
import static enigma.TestUtils.UPPER_STRING;
import static org.junit.Assert.assertEquals;


/**
 * @author Ian Tien
 */
public class MachineTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */
    Permutation pm = new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER);
    FixedRotor fixed = new FixedRotor("B", pm);
    String alpha = UPPER_STRING;
    String t1 = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
    Permutation p1 = new Permutation(t1, UPPER);
    private MovingRotor moving1 = new MovingRotor("BETA", p1, "Q");
    String t2 = "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)";
    Permutation p2 = new Permutation(t2, UPPER);
    private MovingRotor moving2 = new MovingRotor("I", p2, "E");
    String t3 = "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)";
    Permutation p3 = new Permutation(t3, UPPER);
    private MovingRotor moving3 = new MovingRotor("III", p3, "V");
    String t4 = "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)";
    Permutation p4 = new Permutation(t4, UPPER);
    private MovingRotor moving4 = new MovingRotor("IV", p4, "J");
    private ArrayList<Rotor> rotors;
    private Machine machine;
    private String[] insert = {"B", "BETA", "III", "IV", "I"};

    private void setUpRotors() {
        rotors = new ArrayList<Rotor>();
        rotors.add(fixed);
        rotors.add(moving1);
        rotors.add(moving2);
        rotors.add(moving3);
        rotors.add(moving4);
    }

    /**
     * Set the rotor to the one with given NAME and permutation as
     * specified by the NAME entry in ROTORS, with given NOTCHES.
     */
    private void setMachine(Alphabet a, int num, int p, Collection<Rotor> all) {
        machine = new Machine(a, num, p, all);
    }

    @Test
    public void testSetRotors() {
        setUpRotors();
        setMachine(UPPER, 5, 3, rotors);
        machine.insertRotors(insert);
        machine.setRotors("AXLE", "");
        assertEquals("Wrong setting at 1", 0, machine.rots(1).setting());
        assertEquals("Wrong setting at 2", 23, machine.rots(2).setting());
        assertEquals("Wrong setting at 3", 11, machine.rots(3).setting());
        assertEquals("Wrong setting at 4", 4, machine.rots(4).setting());
    }

    @Test
    public void testConvert() {
        setUpRotors();
        setMachine(UPPER, 5, 3, rotors);
        machine.insertRotors(insert);
        machine.setRotors("AXLE", "");
        String plugs = "(HQ) (EX) (IP) (TR) (BY)";
        machine.setPlugboard(new Permutation(plugs, UPPER));
        assertEquals("Wrong convert", "KPFQ", machine.convert("FROM"));
        setMachine(UPPER, 5, 3, rotors);
        machine.insertRotors(insert);
        machine.setRotors("AXLE", "");
        machine.setPlugboard(new Permutation(plugs, UPPER));
        assertEquals("Wrong convert", "CZIN", machine.convert("QVPQ"));
    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new Alphabet("ABCD");
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotorString = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotorString);
        mach.setRotors(setting, "");
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));

    }

    /**
     * Helper method to get the String
     * representation of the current Rotor settings
     */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }
}
