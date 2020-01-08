package enigma;

/**
 * Superclass that represents a rotor in the enigma machine.
 *
 * @author IanTien
 */
class Rotor {


    /**
     * My name.
     */
    private final String _name;
    /**
     * The permutation implemented by this rotor in its 0 position.
     */
    private Permutation _perms;
    /**
     * A rotor named NAME whose permutation is given by PERM.
     */
    protected int _setting;
    /**
     * A new ring containing CHARS.
     */
    protected int _rings;

    /**
     * A rotor that takes in NAME and PERM.
     */
    Rotor(String name, Permutation perm) {
        _name = name;
        _perms = perm;
        _setting = 0;
    }

    /**
     * Return my alphabet.
     * @param a a
     */
    void setRing(char a) {
        _rings = alphabet().toInt(a);
    }

    /**
     * Return my name.
     */
    String name() {
        return _name;
    }

    /**
     * Return my alphabet.
     */
    Alphabet alphabet() {
        return _perms.alphabet();
    }

    /**
     * Return my permutation.
     */
    Permutation permutation() {
        return _perms;
    }

    /**
     * Return the size of my alphabet.
     */
    int size() {
        return _perms.size();
    }

    /**
     * Return true iff I have a ratchet and can move.
     */
    boolean rotates() {
        return false;
    }

    /**
     * Return true iff I reflect.
     */
    boolean reflecting() {
        return false;
    }

    /**
     * Return my current setting.
     */
    int setting() {
        return _setting;
    }

    /**
     * Set setting() to POSN.
     */
    void set(int posn) {
        _setting = _perms.wrap(posn);
    }

    /**
     * Set setting() to character CPOSN.
     */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /**
     * Return the value of P modulo the input SIZE.
     */
    int mod(int p, int size) {
        int r = p % size;
        if (r < 0) {
            r += size;
        }
        return r;
    }

    /**
     * Return the conversion of P (an integer in the range 0..size()-1)
     * according to my permutation.
     */
    int convertForward(int p) {
        int result = _perms.permute(_perms.wrap(p + _setting - _rings));
        return mod(result - _setting + _rings, size());
    }

    /**
     * Return the conversion of E (an integer in the range 0..size()-1)
     * according to the inverse of my permutation.
     */
    int convertBackward(int e) {
        int result = _perms.invert(_perms.wrap(e + _setting - _rings));
        return mod(result - _setting + _rings, size());
    }

    /**
     * Returns true iff I am positioned to allow the rotor to my left
     * to advance.
     */
    boolean atNotch() {
        return false;
    }

    /**
     * Advance me one position, if possible. By default, does nothing.
     */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

}
