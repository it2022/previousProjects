package gitlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All things to do with parsing commands.
 *
 * @author ian t
 */
class TextCommands {

    /**
     * The command name.
     */
    private final Type _type;
    /**
     * Command arguments.
     */
    private final String[] _operands;

    /**
     * A new Command of type TYPE with OPERANDS as its operands.
     */
    TextCommands(Type type, String... operands) {
        _type = type;
        _operands = operands;
    }

    /**
     * Parse COMMAND, returning the command and its operands.
     */
    static TextCommands parseCommand(String[] command) {
        if (command == null) {
            return new TextCommands(Type.EOF);
        }
        for (Type types : Type.values()) {
            Matcher mat = types._pat.matcher(command[0]);
            if (mat.matches()) {
                String[] operators = new String[command.length - 1];
                for (int i = 1; i < command.length; i += 1) {
                    operators[i - 1] = command[i];
                }
                return new TextCommands(types, operators);
            }
        }
        throw new Error("Internal failure: error command did not match.");
    }

    /**
     * Return the type of this Command.
     */
    Type commandType() {
        return _type;
    }

    /**
     * Returns this Command's operands.
     */
    String[] operands() {
        return _operands;
    }
    /**
     * Command types.  PIECEMOVE indicates a move of the form
     * c0r0-c1r1.  ERROR indicates a parse error in the command.
     * All other commands are upper-case versions of what the
     * programmer writes.
     */
    enum Type {
        ADD("add"),
        COMMIT("commit"),
        REMOVE("rm"),
        INIT("init"),
        LOG("log"),
        CHECKOUT("checkout"),
        FIND("find"),
        BRANCH("branch"),
        RMBRANCH("rm-branch"),
        RESET("reset"),
        MERGE("merge"),
        GLOBALLOG("global-log"),
        STATUS("status"),
        ADDREMOTE("add-remote"),
        FETCH("fetch"),
        PUSH("push"),
        PULL("pull"),
        RMREMOTE("rm-remote"),
        ERROR(".*"),
        EOF;

        /**
         * The Pattern descrbing syntactically correct versions of this
         * type of command.
         */
        private final Pattern _pat;

        /**
         * PATTERN is a regular expression string giving the syntax of
         * a command of the given type.  It matches the entire command,
         * assuming no leading or trailing whitespace.  The groups in
         * the pattern capture the operands (if any).
         */
        Type(String pattern) {
            _pat = Pattern.compile(pattern + "$");
        }

        /**
         * A Type whose pattern is the lower-case version of its name.
         */
        Type() {
            _pat = Pattern.compile(this.toString().toLowerCase() + "$");
        }

    }
}
