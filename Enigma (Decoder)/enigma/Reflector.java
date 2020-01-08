package enigma;

import static enigma.EnigmaException.error;

/**
 * Class that represents a reflector in the enigma.
 *
 * @author IanTien
 */
class Reflector extends FixedRotor {
    /**
     * String title given to perms.
     */
    private String _name;

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM.
     */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    int convertBackward(int e) {
        throw error("Reflector do not convert backward");
    }


    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

}
