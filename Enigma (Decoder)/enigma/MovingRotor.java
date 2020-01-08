package enigma;

/**
 * Class that represents a rotating rotor in the enigma machine.
 *
 * @author IanTien
 */
class MovingRotor extends Rotor {

    /**
     * addtional.
     */
    private String _notches;
    /**
     * addtional.
     */
    private Permutation _permutation;
    /**
     * addtional.
     */
    private Alphabet _alphabet;


    /**
     * A rotor named NAME whose permutation in its default setting is
     * PERM, and whose notches are at the positions indicated in NOTCHES.
     * The Rotor is initally in its 0 setting (first character of its
     * alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;

    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (alphabet().toInt(_notches.charAt(i)) == setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(setting() + 1 % size());
    }


}
