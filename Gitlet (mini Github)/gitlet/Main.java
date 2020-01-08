package gitlet;

import static gitlet.Utils.join;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author ian t
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        } else if (!join(System.getProperty("user.dir"),
                ".gitlet").exists() && !args[0].equals("init")) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        TextInterpreter interpreter =
                new TextInterpreter(args, System.out);
        interpreter.process();
    }

}
