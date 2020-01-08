package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author ian t
 */
public class Remove implements Serializable {

    /**
     * UID.
     */
    private static final long serialVersionUID = 4L;
    /**
     * stores a list of blobs.
     */
    private ArrayList<String> _current;

    /**
     * A new Delete, using Current Blobs.
     */
    Remove() {
        _current = new ArrayList<String>();
    }

    /**
     * @param name is the file name.
     */
    void add(String name) {
        _current.add(name);
    }

    /**
     * @param name is the file name.
     */
    void remove(String name) {
        _current.remove(name);
    }

    /**
     * @return the current files to be deleted.
     */
    ArrayList<String> getfile() {
        return _current;
    }
}
