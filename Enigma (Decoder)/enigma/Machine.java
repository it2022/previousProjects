package enigma;

import java.util.Collection;

/**
 * Class that represents a complete enigma machine.
 *
 * @author IanTien
 */
class Machine {
    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;
    /**
     * Total number of rotors.
     */
    private final int _numRotors;
    /**
     * Total number of pawls.
     */
    private final int _pawls;
    /**
     * The array of rotors that formats the machine.
     */
    private Rotor[] rots;
    /**
     * The initial plugboard which includes steckered pairs.
     */
    private Permutation _plugboard;
    /**
     * An ArrayList containing all possible rotors that can be used.
     */
    private Object[] _allRots;

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRots = allRotors.toArray();
        rots = new Rotor[numRotors];
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            boolean found = false;
            for (int j = 0; j < _allRots.length; j++) {
                String name = (((Rotor) _allRots[j]).name());
                if ((rotors[i].toUpperCase()).equals(name.toUpperCase())) {
                    rots[i] = (Rotor) _allRots[j];
                    found = true;
                }
            }
            if (!found) {
                throw new EnigmaException("Misnamed rotors");
            }
        }

    }

    /**
     * Set my rotors according to SETTING, which must be a string of four
     * upper-case letters. The first letter refers to the leftmost
     * rotor setting (not counting the reflector).
     *
     */
    /**
     * @param rings ringstelleung
     * @param setting settings
     */
    void setRotors(String setting, String rings) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Initial positions string wrong length");
        }
        for (int i = 1; i < _numRotors; i++) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw new EnigmaException("Init positions not in alphabet");
            }
            rots[i].set(setting.charAt(i - 1));
            if (rings.length() != 0) {
                rots[i].set(rots[i].setting());
                rots[i].setRing(rings.charAt(i - 1));
            }
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * the machine.
     */
    int convert(int c) {
        boolean[] checks = new boolean[_numRotors];
        checks[_numRotors - 1] = true;
        if (rots[_numRotors - 1].atNotch()
                && _numRotors - 2 >= 0
                && rots[_numRotors - 2].rotates()) {
            checks[_numRotors - 2] = true;
        }
        for (int i = _numRotors - 2; i > 1; i--) {

            if (rots[i].atNotch() && rots[i - 1].rotates()) {
                checks[i] = true;
            }
            if (rots[i + 1].atNotch()) {
                checks[i] = true;
            }

        }

        for (int i = 1; i <= _numRotors - 1; i++) {
            if (checks[i]) {
                rots[i].advance();
            }
        }

        int result = _plugboard.permute(c);
        for (int i = _numRotors - 1; i >= 0; i--) {
            result = rots[i].convertForward(result);
        }
        for (int i = 1; i < _numRotors; i++) {
            result = rots[i].convertBackward(result);
        }
        result = _plugboard.invert(result);
        return result;

    }
    /**
     * returns rotor integer contained in settings, used in Tests.
     * @param i index
     */
    Rotor rots(int i) {
        return this.rots[i];
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int temp = convert(_alphabet.toInt(msg.charAt(i)));
            char converted = _alphabet.toChar(temp);
            result += converted;
        }
        return result;
    }
}
