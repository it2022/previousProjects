package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Date;
import java.util.function.Consumer;

import static gitlet.Paths.*;
import static gitlet.TextCommands.Type.*;
import static gitlet.Utils.*;

/**
 * An object that reads and interprets a sequence of commands from an
 * input source.
 *
 * @author ian t
 */
class TextInterpreter {
    /**
     * The hashmap maps command type to method.
     */
    private final HashMap<TextCommands.Type, Consumer<String[]>> _commands =
            new HashMap<>();
    /**
     * The command input source.
     */
    private String[] _inputs;

    {
        _commands.put(INIT, this::lineInit);
        _commands.put(ADD, this::lineAdd);
        _commands.put(COMMIT, this::lineCommit);
        _commands.put(REMOVE, this::lineRemove);
        _commands.put(LOG, this::lineLog);
        _commands.put(FIND, this::lineFind);
        _commands.put(CHECKOUT, this::lineCheckout);
        _commands.put(BRANCH, this::lineBranch);
        _commands.put(RMBRANCH, this::lineRMbranch);
        _commands.put(GLOBALLOG, this::lineGloballog);
        _commands.put(STATUS, this::lineStatus);
        _commands.put(RESET, this::lineReset);
        _commands.put(MERGE, this::lineMerge);
        _commands.put(ERROR, this::lineError);
        _commands.put(ADDREMOTE, this::lineAddremote);
        _commands.put(PUSH, this::linePush);
        _commands.put(PULL, this::linePull);
        _commands.put(RMREMOTE, this::lineRMremote);
        _commands.put(FETCH, this::lineFetch);
    }

    /**
     * A new CommandInterpreter executing commands read from INP, writing
     * prompts on PROMPTER, if it is non-null.
     */
    TextInterpreter(String[] inp, PrintStream prompter) {
        _inputs = inp;
    }

    /**
     * process() method.
     */
    void process() {
        lineCommand();
    }

    /**
     * Perform the next command from our input source.
     */
    void lineCommand() {
        try {
            TextCommands cmnd =
                    TextCommands.parseCommand(_inputs);
            _commands.get(cmnd.commandType()).accept(cmnd.operands());
        } catch (GitletException e) {
            System.out.printf("Error: %s%n", e.getMessage());
        }
    }

    /**
     * Perform the command 'lineInit OPERANDS[0]'.
     */
    void lineInit(String[] unused) {
        File theDir = new File(".gitlet");
        if (!theDir.exists()) {
            theDir.mkdir();
            new File(".gitlet/stage").mkdir();
            new File(".gitlet/objects").mkdir();
            new File(".gitlet/refs").mkdir();
            new File(".gitlet/refs/heads").mkdir();

            Commit commit = new Commit("initial commit", "null",
                    new Date(Instant.EPOCH.getEpochSecond()));

            String branch = "master";
            String path = ".gitlet/objects/";
            writeObject(join(path, commit.sha1()), commit);
            writeContents(join(".gitlet/HEAD"), branch.getBytes());
            writeObject(join(".gitlet/refs/heads/master"), commit);
            writeObject(join(".gitlet/delete"), new Remove());

            Platform s = new Platform();
            writeObject(join(".gitlet/stage/index"), s);
        } else {
            System.out.println("A gitlet version-control system"
                    + " already exists in the current directory");
        }
    }

    /**
     * Perform the command 'Add OPERANDS[0]'.
     */
    void lineAdd(String... operands) {
        Platform s = readObject(join(".gitlet/stage/index"), Platform.class);
        File file = new File(operands[0]);
        Booby b;
        Remove d = readObject(join(".gitlet/delete"), Remove.class);
        if (file.exists()) {
            if (changenotstaged(operands[0])
                    | trackchange(operands[0])
                    | !tracked(operands[0])) {
                String path = ".gitlet/stage/";
                b = new Booby(file);
                writeObject(join(path, b.getname()), b);
                s.put(operands[0], b.getname());
            }
        } else {
            System.out.println("File does not exist.");
        }

        if (tracked(operands[0]) && d.getfile().contains(operands[0])) {
            d.remove(operands[0]);
            s.remove(operands[0]);
        }


        writeObject(join(".gitlet/stage/index"), s);
        writeObject(join(".gitlet/delete"), d);
    }


    /**
     * Perform the command 'Add OPERANDS[0]'.
     */
    @SuppressWarnings("unchecked")
    void lineCommit(String... operands) {
        if (operands == null || operands[0].equals("")) {
            System.out.println("Please enter a commit message.");
        }

        boolean changed = false;
        String text = operands[0];
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit father = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        Commit big = null;
        Commit commit = new Commit(text, father.showcha2());
        if (text.split(" ")[0].equals("Merged")) {
            big = readObject(join(".gitlet/refs/heads",
                    text.split(" ")[1]), Commit.class);
            commit = new Commit(text, father.showcha2(), big.showcha2());
        }
        for (String name : (Set<String>) father.getfile().keySet()) {
            commit.put(name, (String) father.getfile().get(name));
        }

        Platform s = readObject(join(".gitlet/stage/index"), Platform.class);
        Set<String> keys = s.getfile().keySet();
        for (String name : keys) {
            changed = true;
            String sha1 = (String) s.getfile().get(name);
            Booby b = readObject(join(".gitlet/stage", sha1), Booby.class);
            commit.put(name, sha1);
            writeObject(join(".gitlet/objects", sha1), b);
        }

        Remove e = readObject(join(".gitlet/delete"), Remove.class);
        for (String todelete : e.getfile()) {
            changed = true;
            if (commit.getfile().get(todelete) != null) {
                commit.remove(todelete);
            }
        }
        e = new Remove();

        if (!changed) {
            System.out.println("No changes added to the commit.");
            return;
        }

        writeObject(join(".gitlet/objects", commit.sha1()), commit);
        writeObject(join(".gitlet/refs/heads", head), commit);
        writeObject(join(".gitlet/delete"), e);
        for (String f : plainFilenamesIn(".gitlet/stage")) {
            join(".gitlet/stage", f).delete();
        }
        writeObject(join(".gitlet/stage/index"), new Platform());
    }

    /**
     * Perform the command 'Remove OPERANDS[0]'.
     */
    void lineRemove(String... operands) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        File record = new File(".gitlet/delete");
        Remove d = readObject(record, Remove.class);
        boolean find = false;
        for (String f : operands) {
            if (current.getfile().get(f) != null) {
                d.add(f);
                if (join(f).exists()) {
                    join(f).delete();
                }
                find = true;
            }

            if (join(".gitlet/stage/index").exists()) {
                Platform s = readObject(join(".gitlet/stage/index"),
                        Platform.class);
                if (s.getfile().get(f) != null) {
                    s.remove(f);
                    writeObject(join(".gitlet/stage/index"), s);
                    find = true;
                    break;
                }
            }
        }

        writeObject(join(".gitlet/delete"), d);

        if (!find) {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * Perform the command 'log'.
     */
    void lineLog(String[] unused) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        String pathway = ".gitlet/objects";
        String cha2 = current.showcha2();
        while (join(pathway, cha2).exists()) {
            Commit commit = readObject(join(pathway, cha2), Commit.class);
            commit.print();
            cha2 = commit.adult();
        }
    }

    /**
     * Perform the command 'global-log'.
     */
    void lineGloballog(String[] unused) {
        String path = ".gitlet/objects";
        List<String> rest = plainFilenamesIn(path);
        for (String c : rest) {
            if (c.charAt(0) == 'c') {
                Commit commit = readObject(join(path, c), Commit.class);
                commit.print();
            }
        }
    }

    /**
     * Perform the command 'Find OPERANDS[0]'.
     */
    void lineFind(String[] operands) {
        String pathway = ".gitlet/objects";
        boolean find = false;
        for (String cha2 : plainFilenamesIn(pathway)) {
            if (join(pathway, cha2).exists() && cha2.charAt(0) == 'c') {
                Commit commit = readObject(join(pathway, cha2), Commit.class);
                if (commit.log().equals(operands[0])) {
                    find = true;
                    System.out.println(cha2);
                }
            }
        }
        if (!find) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Perform.
     * @param unuse t
     */
    @SuppressWarnings("unchecked")
    void lineStatus(String[] unuse) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        System.out.println("=== Branches ===");
        List<String> twigs = plainFilenamesIn(".gitlet/refs/heads");
        Collections.sort(twigs);
        for (String branch : twigs) {
            if (head.equals(branch)) {
                System.out.format("*%s\n", branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        Platform e = readObject(join(".gitlet/stage/index"), Platform.class);
        List<String> keys = new ArrayList<String>(e.getfile().keySet());
        Collections.sort(keys);
        System.out.println("=== Staged Files ===");
        for (int i = 0; i < keys.size(); i++) {
            System.out.println(keys.get(i));
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Remove d = readObject(join(".gitlet/delete"), Remove.class);
        for (String todelete : d.getfile()) {
            System.out.println(todelete);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String f : plainFilenamesIn(System.getProperty("user.dir"))) {
            if (trackchange(f) && e.getfile().get(f) == null) {
                System.out.format("%s (modified)\n", f);
            } else if (changenotstaged(f)) {
                System.out.format("%s (modified)\n", f);
            }
        }

        Commit now = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        for (String f : (Set<String>) now.getfile().keySet()) {
            if (e.getfile().get(f) != null && !join(f).exists()) {
                System.out.format("%s (deleted)\n", f);
            }
            if (!join(f).exists() && trackdelete(f)
                    && !d.getfile().contains(f)) {
                System.out.format("%s (deleted)\n", f);
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String f : plainFilenamesIn(System.getProperty("user.dir"))) {
            if (!tracked(f) && !staged(f)) {
                System.out.format("%s\n", f);
            }
        }
        System.out.println();
    }

    /**
     * @param name is filename
     * @return true if the file is tracked
     */
    boolean tracked(String name) {
        String top = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", top),
                Commit.class);
        if (current.getfile().get(name) != null) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is staged
     */
    boolean staged(String name) {
        Platform s = readObject(join(".gitlet/stage/index"), Platform.class);
        if (s.getfile().get(name) != null) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is tracked but changed
     */
    boolean trackchange(String name) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit now = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        String newcha2 = "b" + sha1(readContentsAsString(join(name)));
        if (now.getfile().get(name) != null
                && !now.getfile().get(name).equals(newcha2)) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is tracked but changed
     */
    boolean trackdelete(String name) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        if (current.getfile().get(name) != null
                && !plainFilenamesIn(System.getProperty("user.dir"))
                .contains(name)) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is staged but changed
     */
    boolean changenotstaged(String name) {
        Platform s = readObject(join(".gitlet/stage/index"), Platform.class);
        String newsha1 = "b" + sha1(readContentsAsString(join(name)));
        if (s.getfile().get(name) != null
                && !s.getfile().get(name).equals(newsha1)) {
            return true;
        }
        return false;

    }

    /**
     * @param operands is input args.
     *                 Performs the commands 'checkout -- file'.
     */
    void lineCheckout(String[] operands) {
        if (operands[0].equals("--")) {
            String name = operands[1];
            String head = readContentsAsString(join(".gitlet/HEAD"));
            Commit current = readObject(join(".gitlet/refs/heads", head),
                    Commit.class);
            String boobysha1 = (String) current.getfile().get(name);
            if (boobysha1 != null) {
                Booby b = readObject(join(".gitlet/objects", boobysha1),
                        Booby.class);
                writeContents(join(name), b.content());
                return;
            } else {
                System.out.println("File does not exist in that commit.");
            }

        } else if (operands.length == 3) {
            if (operands[1].equals("--")) {
                String commitid = operands[0];
                String name = operands[2];
                String path = ".gitlet/objects";
                Boolean exsitid = false;
                for (String sha1 : plainFilenamesIn(path)) {
                    if (sha1.charAt(0) == 'c') {
                        Commit commit = readObject(join(path, sha1),
                                Commit.class);
                        if (commitid.regionMatches(true, 0,
                                sha1, 0, commitid.length())) {
                            exsitid = true;
                            if (commit.getfile().get(name) != null) {
                                String boobysha1 = (String)
                                        commit.getfile().get(name);
                                Booby b = readObject(join(".gitlet/objects",
                                        boobysha1),
                                        Booby.class);
                                writeContents(join(name), b.content());
                                return;
                            }
                        }
                    }
                }

                if (exsitid) {
                    System.out.println("File does not exist in that commit.");
                } else {
                    System.out.println("No commit with that id exists.");
                }
            } else {
                System.out.println("Incorrect operands.");
            }
        } else {
            lineCheckout(operands[0]);
        }
    }

    /**
     * @param operands is input args.
     *                 performs the commands 'checkout branch'
     */
    @SuppressWarnings("unchecked")
    void lineCheckout(String operands) {
        String currentbranch = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads",
                currentbranch),
                Commit.class);
        String givenbranch = operands;
        if (givenbranch.equals(currentbranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }


        String path = ".gitlet/";
        if (join(".gitlet/refs/heads", givenbranch).exists()) {
            Commit gs = readObject(join(".gitlet/refs/heads",
                    givenbranch),
                    Commit.class);
            Set<String> library = gs.getfile().keySet();
            Set<String> libcurrent = current.getfile().keySet();

            for (String f : plainFilenamesIn(System.getProperty
                    ("user.dir"))) {
                if (!libcurrent.contains(f) && library.contains(f)
                        && !gs.getfile().get(f).equals("b"
                        + sha1(readContentsAsString(join(f))))) {
                    System.out.println("There is an untracked file in "
                            + "the way; delete it or add it first.");
                    return;
                }
            }

            for (String title : library) {
                String cha2 = (String) gs.getfile().get(title);
                Booby b = readObject(join(".gitlet/objects", cha2),
                        Booby.class);
                writeContents(join(System.getProperty("user.dir"), title),
                        b.content());
            }

            for (String name : libcurrent) {
                if (!library.contains(name)) {
                    join(System.getProperty("user.dir"), name).delete();
                }
            }

            writeContents(join(".gitlet/HEAD"), givenbranch.getBytes());
            for (String f : plainFilenamesIn(".gitlet/stage")) {
                join(".gitlet/stage", f).delete();
            }
            writeObject(join(".gitlet/stage/index"), new Platform());
        } else {
            System.out.println("No such branch exist.");
        }
    }

    /**
     * @param operands is input args.
     *                 Perform the command 'branch operands[0].
     */
    void lineBranch(String... operands) {
        File branch = join(".gitlet/refs/heads", operands[0]);
        if (operands[0].split("/").length == 2) {
            join(".gitlet/refs/heads", operands[0]
                    .split("/")[0]).mkdir();
        }
        if (!branch.exists()) {
            String head = readContentsAsString(join(".gitlet/HEAD"));
            Commit current = readObject(join(".gitlet/refs/heads", head),
                    Commit.class);
            writeObject(branch, current);
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    /**
     * @param operands is input args
     *                 Perform the command 'rm operands[0]'.
     */
    void lineRMbranch(String[] operands) {
        String branch = operands[0];
        String head = readContentsAsString(join(".gitlet/HEAD"));
        String path = ".gitlet/refs/heads";
        if (join(path, branch).exists()) {
            if (!branch.equals(head)) {
                (join(path, branch)).delete();
            } else {
                System.out.println("Cannot remove the current branch.");
            }
        } else {
            System.out.println("A branch with that name does not exist.");
        }
    }

    /**
     * @param operands is input args.
     *                 Perform the command 'reset operands[0]'.
     */
    @SuppressWarnings("unchecked")
    void lineReset(String[] operands) {
        String commitid = operands[0];
        String top = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", top),
                Commit.class);
        String path = ".gitlet/objects";
        boolean find = false;
        for (String cha2 : plainFilenamesIn(path)) {
            if (cha2.charAt(0) == 'c') {
                if (commitid.regionMatches(true, 0,
                        cha2, 0, commitid.length())) {
                    find = true;
                    Commit commit = readObject(join(path, cha2), Commit.class);
                    Set<String> givenkey = commit.getfile().keySet();
                    Set<String> currkey = current.getfile().keySet();
                    for (String f : plainFilenamesIn(System.getProperty
                            ("user.dir"))) {
                        if (!currkey.contains(f) && givenkey.contains(f)
                                && !commit.getfile().get(f).equals("b"
                                + sha1(readContentsAsString(join(f))))) {
                            System.out.println("There is an untracked file "
                                    + "in the way; delete it or add it first.");
                            return;
                        }
                    }

                    for (String title: givenkey) {
                        String[] file = new String[3];
                        file[0] = commit.showcha2();
                        file[1] = "--";
                        file[2] = title;
                        lineCheckout(file);
                    }

                    List<String> k = plainFilenamesIn(System.getProperty
                            ("user.dir"));
                    for (String f : k) {
                        if (!givenkey.contains(f)) {
                            join(f).delete();
                        }
                    }

                    writeObject(join(".gitlet/refs/heads", top),
                            commit);
                    writeObject(join(".gitlet/stage/index"), new Platform());
                }
            }
        }

        if (!find) {
            System.out.println("No commit with that id exists.");
        }
    }

    /**
     * @param operands is input args.
     *                 Perform the command 'merge operands[0]'.
     */
    @SuppressWarnings("unchecked")
    void lineMerge(String... operands) {
        String giventwig = operands[0];
        String currtwig = readContentsAsString(join(".gitlet/HEAD"));
        String path = ".gitlet/refs/heads";

        if (!join(path, giventwig).exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (currtwig.equals(giventwig)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Platform s = readObject(join(".gitlet/stage/index"), Platform.class);
        Remove d = readObject(join(".gitlet/delete"), Remove.class);
        if (!s.getfile().isEmpty() | !d.getfile().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        Commit given = readObject(join(path, giventwig), Commit.class);
        Commit current = readObject(join(path, currtwig), Commit.class);
        Commit split = null;

        Set<String> keygive = given.getfile().keySet();
        Set<String> currkey = current.getfile().keySet();
        for (String f : plainFilenamesIn(System.getProperty
                ("user.dir"))) {
            if (!currkey.contains(f) && keygive.contains(f)
                    && !given.getfile().get(f).equals("b"
                    + sha1(readContentsAsString(join(f))))) {
                System.out.println("There is an untracked file in the "
                        + "way; delete it or add it first.");
                return;
            }
        }
        lineMerge(current, given, split,
                currtwig, giventwig, currkey, keygive);

    }

    /**
     * @param nowbranch is current branch.
     * @param twiggiventous   is given branch.
     * @param split         is the commit where two branches splits.
     * @param keycurr   is the list of file names in current commit
     * @param keygiv     is the list of file names in given commit
     * @param current       is current commit
     * @param given         is given commit
     */
    void lineMerge(Commit current, Commit given, Commit split,
                   String nowbranch, String twiggiventous,
                   Set<String> keycurr, Set<String> keygiv) {
        String pathcommit = ".gitlet/objects";
        ArrayList<String> oldcom1 = new ArrayList<String>();
        ArrayList<String> oldcom2 = new ArrayList<String>();
        String currcha2 = current.showcha2();

        while (join(pathcommit, currcha2).exists()) {
            oldcom1.add(currcha2);
            current = readObject(join(pathcommit, currcha2), Commit.class);
            currcha2 = current.adult();
        }

        if (oldcom1.contains(given.showcha2())) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            return;
        }
        String givensha1 = given.showcha2();
        while (join(pathcommit, givensha1).exists()) {
            oldcom2.add(givensha1);
            if (givensha1.equals(oldcom1.get(0))) {
                System.out.println("Current branch fast-forwarded.");
                writeObject(join(".gitlet/refs/heads", nowbranch),
                        oldcom2.get(0));
                return;
            } else if (oldcom1.contains(givensha1)) {
                split = readObject(join(pathcommit, givensha1),
                        Commit.class);
                break;
            }
            given = readObject(join(pathcommit, givensha1), Commit.class);
            givensha1 = given.adult();
        }

        lineMerge(nowbranch, twiggiventous, split, keycurr, keygiv);
    }


    /**
     * @param nowtwig is current branch.
     * @param twiggiventous   is given branch.
     * @param split         is the commit where two branches splits.
     * @param keycurr   is the list of file names in current commit
     * @param keygiv     is the list of file names in given commit
     */
    @SuppressWarnings("unchecked")
    void lineMerge(String nowtwig, String twiggiventous,
                   Commit split, Set keycurr, Set keygiv) {
        Set<String> keys = split.getfile().keySet();
        boolean conflict = false;
        String path = ".gitlet/refs/heads";
        String pathtocommit = ".gitlet/objects";
        Commit given = readObject(join(path, twiggiventous), Commit.class);
        Commit current = readObject(join(path, nowtwig), Commit.class);
        for (String name : keys) {
            if (!split.getfile().get(name).equals(given.getfile().get(name))
                    && split.getfile().get(name).equals
                    (current.getfile().get(name))) {
                if (given.getfile().get(name) == null) {
                    String[] filetodelete = new String[1];
                    filetodelete[0] = (String) split.getfile().get(name);
                    lineRemove(name);
                } else {
                    Booby b = readObject(join(".gitlet/objects",
                            (String) given.getfile().get(name)), Booby.class);
                    writeContents(join(name), b.content());
                    lineAdd(name);
                }
            } else if (!split.getfile().get(name)
                    .equals(given.getfile().get(name))
                    && !split.getfile().get(name)
                    .equals(current.getfile().get(name))) {
                conflict = printconflict(given, current, name,
                        pathtocommit, conflict);
            }
        }

        for (String title : (Set<String>) keygiv) {
            if (!keys.contains(title) && !keycurr.contains(title)) {
                String[] file = new String[3];
                file[0] = given.showcha2();
                file[1] = "--";
                file[2] = title;
                lineCheckout(file);
                lineAdd(title);
            }
        }

        if (conflict) {
            System.out.println("Encountered a merge conflict. ");
        }

        lineCommit(String.format("Merged %s into %s.", twiggiventous,
                nowtwig), current.showcha2(), given.showcha2());
    }


    /**
     * @param current    is current branch.
     * @param given      is given branch.
     * @param name       is the commit where two branches splits.
     * @param pathcommit is the list of file names in current commit
     * @param conflict   is the list of file names in given commit
     * @return true if has confilict.
     */
    boolean printconflict(Commit given, Commit current, String name,
                          String pathcommit, boolean conflict) {
        if (given.getfile().get(name) != null
                && current.getfile().get(name) != null
                && !given.getfile().get(name)
                .equals(current.getfile().get(name))) {
            Booby givenb = readObject(join(pathcommit,
                    (String) given.getfile().get(name)), Booby.class);
            Booby currentb = readObject(join(pathcommit,
                    (String) current.getfile().get(name)), Booby.class);
            writeContents(join(System.getProperty("user.dir"), name),
                    "<<<<<<< HEAD\n",
                    currentb.content(), "=======\n", givenb.content(),
                    ">>>>>>>\n");
            lineAdd(name);
            conflict = true;
        } else if (given.getfile().get(name) == null
                && current.getfile().get(name) != null) {
            Booby currentb = readObject(join(pathcommit,
                    (String) current.getfile().get(name)), Booby.class);
            writeContents(join(System.getProperty("user.dir"), name),
                    "<<<<<<< HEAD\n",
                    currentb.content(), "=======\n", ">>>>>>>\n");
            lineAdd(name);
            conflict = true;
        } else if (given.getfile().get(name) != null
                && current.getfile().get(name) == null) {
            Booby givenb = readObject(join(pathcommit,
                    (String) given.getfile().get(name)), Booby.class);
            writeContents(join(System.getProperty("user.dir"), name),
                    "<<<<<<< HEAD\n", "=======\n", givenb.content(),
                    ">>>>>>>\n");
            lineAdd(name);
            conflict = true;
        }
        return conflict;
    }


    /**
     * @param args is input args.
     *             do add remote.
     */
    void lineAddremote(String... args) {
        String remote1 = args[0];
        String pathname = args[1];
        if (!join(remote(), remote1).exists()) {
            new File(remote()).mkdir();
            join(remote(), remote1).mkdir();
            writeContents(join(remote(), remote1, "path"), pathname.getBytes());
        } else {
            System.out.println("A remote with that name already exists.");
            return;
        }
    }

    /**
     * @param args is input args.
     *             do Fetch from remote.
     */
    void lineFetch(String... args) {
        String arg1 = args[0];
        String arg2 = args[1];
        String path = readContentsAsString(join(remote(), arg1, "path"));
        if (!join(path).exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        if (!join(path, heads(), arg2).exists()) {
            System.out.println("That remote does not have that branch.");
            return;
        }
        join(remote(), arg1, arg2).mkdir();
        String localtwig = arg1 + "/" + arg2;
        if (!join(local(), localtwig).exists()) {
            lineBranch(localtwig);
        }
        try {
            List<String> k = plainFilenamesIn(join(".gitlet", objects()));
            for (String f : plainFilenamesIn(join(path, objects()))) {
                if (!k.contains(f)) {

                    Path f1 = join(path, objects(), f).toPath();
                    Path f2 = join(".gitlet", objects(), f).toPath();
                    Files.copy(f1, f2);
                }
            }
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
        writeObject(join(local(), localtwig),
                readObject(join(path, heads(), arg2), Commit.class));
    }

    /**
     * @param args is input args.
     */
    void linePull(String... args) {
        String a1 = args[0];
        String a2 = args[1];
        String concat = a1 + "/" + a2;
        lineFetch(args);
        lineMerge(concat);
    }

    /**
     * @param operands is input args.
     *                 do Push to remote.
     */
    void linePush(String... operands) {
        String remotename = operands[0];
        String mytwig = operands[1];

        String dirname = readContentsAsString(join(remote(),
                remotename, "path"));
        if (!join(dirname).exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        Commit nowcom = readObject(join(local(), localhead()), Commit.class);
        ArrayList<String> oldiecom = new ArrayList<String>();
        String nowhas = nowcom.showcha2();
        Commit intcom = null;
        if (join(dirname, heads(), mytwig).exists()) {
            intcom = readObject(join(dirname, heads(),
                    mytwig), Commit.class);
        }

        while (join(localobjects(), nowhas).exists()) {
            oldiecom.add(nowhas);
            nowcom = readObject(join(localobjects(), nowhas),
                    Commit.class);
            nowhas = nowcom.adult();
        }

        nowcom = readObject(join(local(), localhead()), Commit.class);
        if (intcom == null | oldiecom.contains(intcom.showcha2())) {
            writeObject(join(local(), localhead()),
                    nowcom);
            List<String> k = plainFilenamesIn(join(dirname, objects()));
            for (String f : plainFilenamesIn(join(".gitlet", objects()))) {
                if (!k.contains(f)) {
                    try {
                        Files.copy(join(".gitlet", objects(), f).toPath(),
                                join(dirname, objects(), f).toPath());
                    } catch (IOException excp) {
                        throw new IllegalArgumentException(excp.getMessage());
                    }
                }
            }
            writeObject(join(dirname, heads(), mytwig), nowcom);
        } else {
            System.out.println("Please pull down "
                    + "remote changes before pushing.");
        }
    }

    /**
     * @param args is input args.
     *             remove the local branch that stores remote ones.
     */
    void lineRMremote(String... args) {
        String remotename = args[0];
        if (join(remote(), remotename).exists()) {
            deleteDirectives(join(remote(), remotename));
            return;
        }
        System.out.println("A remote with that name does not exist.");
    }

    /**
     * Perform the command that has error.
     */
    void lineError(String[] unused) {
        System.out.println("No command with that name exists.");
    }
}
