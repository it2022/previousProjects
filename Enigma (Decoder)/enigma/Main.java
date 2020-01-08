package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.error;

/**
 * Enigma simulator.
 *
 * @author IanTien
 */
public final class Main {

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;
    /**
     * Source of input messages.
     */
    private Scanner _input;
    /**
     * Source of machine configuration.
     */
    private Scanner _config;
    /**
     * An ArrayList containing all rotors that can be used.
     */
    private ArrayList<Rotor> _allTheRotors = new ArrayList<>();
    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;
    /**
     * Temp String Holder.
     */
    private String temp;
    /**
     * String that is fed into scanner.
     */
    private String title;
    /**
     * Individual string notches.
     */
    private String notches;
    /**
     * String Permutations.
     */
    private String perms;
    /**
     * Num of Pawls.
     */
    private int pawls;
    /**
     * Num of Rotors.
     */
    private int numRotors;
    /**
     * Ringstelleung.
     */
    private String rings = "";

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the title of a configuration file.
     * ARGS[1] is optional; when present, it titles an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it titles an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Return a Scanner reading from the file titled title.
     *
     * @param titles makes titles
     */
    private Scanner getInput(String titles) {
        try {
            return new Scanner(new File(titles));
        } catch (IOException excp) {
            throw error("could not open %s", titles);
        }
    }

    /**
     * Return a PrintStream writing to the file titled title.
     *
     * @param title1 makes title1
     */
    private PrintStream getOutput(String title1) {
        try {
            return new PrintStream(new File(title1));
        } catch (IOException excp) {
            throw error("could not open %s", title1);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine newMachine = readConfig();
        String line = "";
        boolean sawSettingLine = false;
        while (_input.hasNextLine()) {
            line = _input.nextLine();
            if (line.contains("*")) {
                setUp(newMachine, line);
                sawSettingLine = true;
                continue;
            } else {
                if (!sawSettingLine) {
                    throw new EnigmaException("No setting line seen");
                }
            }
            line = line.replace(" ", "");
            if (line.isEmpty()) {
                _output.println();
            }
            String newString = newMachine.convert(line);
            printMessageLine(newString);
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String a = _config.next();
            if (a.contains("(") || a.contains(")") || a.contains("*")) {
                throw new EnigmaException("Wrong config format");
            }
            _alphabet = new Alphabet(a);

            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong config format");
            }
            numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong config format");
            }
            pawls = _config.nextInt();
            temp = _config.next();
            while (_config.hasNext()) {
                title = temp;
                notches = _config.next();
                _allTheRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, _allTheRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            perms = "";
            temp = _config.next();
            while (temp.contains("(") && _config.hasNext()) {
                perms = perms.concat(temp + " ");
                temp = _config.next();
            }
            if (!_config.hasNext()) {
                perms = perms.concat(temp + " ");
            }

            if (notches.charAt(0) == 'M') {
                Permutation s = new Permutation(perms, _alphabet);
                return new MovingRotor(title, s, notches.substring(1));
            } else if (notches.charAt(0) == 'N') {
                return new FixedRotor(title, new Permutation(perms, _alphabet));
            } else {
                return new Reflector(title, new Permutation(perms, _alphabet));
            }
        } catch (NoSuchElementException excp) {
            throw error("Bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        String[] setList = settings.split(" ");
        String[] rotorsList = new String[M.numRotors()];
        if (setList.length - 1 < M.numRotors()) {
            throw new EnigmaException("not enough arguments in settings");
        }
        for (int i = 1; i < M.numRotors() + 1; i++) {
            rotorsList[i - 1] = setList[i];
        }
        for (int i = 0; i <= rotorsList.length - 2; i++) {
            for (int j = i + 1; j <= rotorsList.length - 1; j++) {
                if (rotorsList[i].equals(rotorsList[j])
                        || setList.length - 1 < M.numRotors()) {
                    throw error("Error: Incorrect format");
                }
            }
        }
        if (setList.length > M.numRotors() + 2) {
            boolean c = setList[M.numRotors() + 2].contains(")");
            if (!setList[M.numRotors() + 2].contains("(") && !c) {
                rings = setList[M.numRotors() + 2];
            }
        }

        int movRotors = 0;
        M.insertRotors(rotorsList);
        if (!M.rots(0).reflecting()) {
            throw new EnigmaException("First Rotor should be a reflector");
        }
        for (int i = 0; i < M.numRotors(); i++) {
            if (M.rots(i).rotates()) {
                movRotors += 1;
            }
        }
        if (movRotors != pawls) {
            throw new EnigmaException("arguments wrong");
        }
        try {
            M.setRotors(setList[M.numRotors() + 1], rings);
        } catch (IndexOutOfBoundsException excp) {
            throw new EnigmaException("misnamed");
        }

        String delete = "";
        for (int i = rings.equals("") ? rotorsList.length + 2
                : rotorsList.length + 3;
             i < setList.length; i++) {
            delete = delete.concat(setList[i] + " ");
        }

        M.setPlugboard(new Permutation(delete, _alphabet));
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 5) {
            if (msg.length() - i > 5) {
                _output.print(msg.substring(i, i + 5) + " ");
            } else {
                _output.println(msg.substring(i, i + (msg.length() - i)));
            }
        }
    }
}
