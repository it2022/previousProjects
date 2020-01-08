package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

/**
 * Commit stores the commit.
 *
 * @author IT
 */
class Commit implements Serializable {
    /**
     * Stores UID commit.
     */
    private static final long serialVersionUID = -887631419061221439L;
    /**
     * time stamp.
     */
    private Date _timeStamp;
    /**
     * Commit log.
     */
    private String _log;
    /**
     * Current Branch of Commit.
     */
    private String _twigs;
    /**
     * Stores parent commit.
     */
    private String _adult;
    /**
     * Stores parent2 commit.
     */
    private String _adult2;
    /**
     * Stores sha1.
     */
    private String _sha1;
    /**
     * Hashmap stores pointers to all blobs.
     */
    private HashMap<String, String> _reference = new HashMap<>();

    /**
     * @param log    is the log.
     * @param parent is the parent sha1.
     * @param time   is the time.
     *               A new Commit, using Current Blobs.
     */
    Commit(String log, String parent, Date time) {
        _timeStamp = time;
        _log = log;
        _twigs = null;
        _adult = parent;
    }

    /**
     * @param log    is the log.
     * @param parent is the parent sha1.
     *               A new Commit, using Current Blobs.
     */
    Commit(String log, String parent) {
        _timeStamp = new Date(Instant.now().getEpochSecond());
        _log = log;
        _twigs = null;
        _adult = parent;
        _adult2 = null;
    }

    /**
     * @param log     is the log.
     * @param parent1 is the parent sha1.
     * @param parent2 is the second parent.
     *                A new Commit, using Current Blobs.
     */
    Commit(String log, String parent1, String parent2) {
        _timeStamp = new Date(Instant.now().getEpochSecond());
        _log = log;
        _twigs = null;
        _adult = parent1;
        _adult2 = parent2;

    }

    /**
     * @param name is the file name.
     * @param sha1 is the file sha1.
     *             A new Commit, using Current Blobs.
     */
    void put(String name, String sha1) {
        _reference.put(name, sha1);
    }

    /**
     * @param name is the file name.
     *             remove the hashmap entry.
     */
    void remove(String name) {
        _reference.remove(name);
    }

    /**
     * @return Date.
     */
    Date time() {
        return _timeStamp;
    }

    /**
     * @return Log.
     */
    String log() {
        return _log;
    }

    /**
     * return parent sha1.
     */
    String adult() {
        return _adult;
    }

    /**
     * @return the hashmap.
     */
    HashMap getfile() {
        return _reference;
    }

    /**
     * @return the sha1 .
     */
    String sha1() {
        _sha1 = "c" + Utils.sha1
                (Utils.serialize(_timeStamp.toString()), _log, _adult,
                _reference.toString());
        return _sha1;
    }

    /**
     * @return the sha1.
     */
    String showcha2() {
        return _sha1;
    }

    /**
     * Print the commit.
     */
    public void print() {
        System.out.println("===");
        System.out.format("commit %s\n", sha1());
        if (_adult2 != null) {
            System.out.format("Merge: %s %s\n", adult().substring(0, 7),
                    _adult2.substring(0, 7));
        }
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String date = sdf.format(time());
        System.out.format("Date: %s\n", date);
        System.out.println(log());
        System.out.println();
    }
}
