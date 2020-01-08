package enigma;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author IanTien
 */
class Permutation {

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;
    /**
     * Private String of cycles.
     */
    private String _coicles;

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        this._coicles = cycles;
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        if (cycle.startsWith("(") && cycle.endsWith(")")) {
            this._coicles += _coicles;
        } else {
            throw new EnigmaException("Wrong number of arguments");
        }
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        char result = permute(_alphabet.toChar(wrap(p)));
        return wrap(_alphabet.toInt(result));
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        char position = _alphabet.toChar(wrap(c));
        return wrap(_alphabet.toInt(invert(position)));
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        if (this._coicles.indexOf(p) == -1) {
            return p;
        }
        int index = this._coicles.indexOf(p) + 1;
        char nextChar = this._coicles.charAt(index);
        if (nextChar == ')') {
            while (this._coicles.charAt(index) != '(') {
                index -= 1;
            }
            return this._coicles.charAt(index + 1);
        } else {
            return nextChar;
        }
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        if (this._coicles.indexOf(c) == -1) {
            return c;
        }
        int index = this._coicles.indexOf(c) - 1;
        char nextChar = this._coicles.charAt(index);
        if (nextChar == '(') {
            while (this._coicles.charAt(index) != ')') {
                index += 1;
            }
            char nextInt = this._coicles.charAt(index - 1);
            return nextInt;
        } else {
            return nextChar;
        }
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        for (int i = 0; i < _coicles.length(); i++) {
            if ((_alphabet.contains(_coicles.charAt(i)))
                    && (_alphabet.toInt(_coicles.charAt(i))
                    == permute(_alphabet.toInt(_coicles.charAt(i))))) {
                return false;
            }
        }
        return true;
    }
}
