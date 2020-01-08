package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;

/**
 * Booby stores the files content.
 *
 * @author ian t
 */
class Booby implements Serializable {
    /**
     * SUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * file content.
     */
    private byte[] _boob;
    /**
     * file name.
     */
    private String _name;

    /**
     * @param file is input file.
     *             Blob constructor.
     */
    Booby(File file) {
        _boob = readContents(file);
        _name = "b" + sha1(_boob);
    }

    /**
     * @return file name
     * get file name.
     */
    String getname() {
        return _name;
    }

    /**
     * @return file content in bytes
     * get file content./
     */
    byte[] content() {
        return _boob;
    }
}
