package gitlet;

/**
 * A Class stores all paths.
 *
 * @author ian t
 */
public class Paths {

    /**
     * @return remote path.
     */
    static String remote() {
        return ".gitlet/refs/remotes";
    }

    /**
     * @return local path.
     */
    static String local() {
        return ".gitlet/refs/heads";
    }

    /**
     * @return heads path.
     */
    static String heads() {
        return "refs/heads";
    }

    /**
     * @return objects path.
     */
    static String objects() {
        return "objects";
    }

    /**
     * @return local objects path.
     */
    static String localobjects() {
        return ".gitlet/objects";
    }

    /**
     * @return local head path.
     */
    static String localhead() {
        return Utils.readContentsAsString(Utils.join(".gitlet/HEAD"));
    }
}
